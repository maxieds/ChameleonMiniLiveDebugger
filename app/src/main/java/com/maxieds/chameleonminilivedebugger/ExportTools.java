/*
This program (The Chameleon Mini Live Debugger) is free software written by
Maxie Dion Schmidt: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The complete license provided with source distributions of this library is
available at the following link:
https://github.com/maxieds/ChameleonMiniLiveDebugger
*/

package com.maxieds.chameleonminilivedebugger;

import android.app.DownloadManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * <h1>File Export Tools</h1>
 * The ExportTools class provides utilities for storing logs to file, downloading / uploading
 * card data via XModem, and downloading the stored log data from the device.
 * Parts of this code for the XModem connections are based on
 * XModem.c/h in the Chameleon Mini firmware distribution.
 *
 * @author  Maxie D. Schmidt
 * @since   1/11/18
 * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/_x_modem_8h_source.html
 */
public class ExportTools {

    /**
     * State information for the XModem connections/
     */
    public static boolean EOT = false;

    /**
     * Named XModem connection status bytes.
     */
    public static final byte BYTE_NAK = (byte) 0x15;
    public static final byte BYTE_SOH = (byte) 0x01;
    public static final byte BYTE_ACK = (byte) 0x06;
    public static final byte BYTE_CAN = (byte) 0x18;
    public static final byte BYTE_EOF = (byte) 0x1A;
    public static final byte BYTE_EOT = (byte) 0x04;
    public static final byte BYTE_ESC = (byte) 0x1B;

    /**
     * XModem connection configuration.
     */
    public static final short XMODEM_BLOCK_SIZE = 128;
    public static final byte FIRST_FRAME_NUMBER = (byte) 1;
    public static final byte CHECKSUM_INIT_VALUE = 0;
    public static int MAX_NAK_COUNT = 20; // to match the Chameleon device standard

    /**
     * Static variables used internally within the class.
     */
    public static int fileSize = 0;
    public static FileOutputStream streamDest;
    public static InputStream streamSrc;
    public static File outfile;
    private static String currentLogMode = "LIVE";
    private static boolean throwToLive = false;
    public static byte CurrentFrameNumber;
    public static byte Checksum;
    public static int currentNAKCount;
    public static boolean transmissionErrorOccurred;
    public static int uploadState;
    public static byte[] uploadFramebuffer = new byte[XMODEM_BLOCK_SIZE + 4];

    /**
     * Completes the XModem download command. Implemented this way to keep the GUI from
     * freezing waiting for the command to complete by while loop / Thread.sleep.
     * @ref ExportTools.downloadByXModem
     * @ref ExportTools.performXModemSerialDownload
     */
    public static Runnable eotSleepRunnable = new Runnable() {
        public void run() {
            ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
            if(serialIOPort == null || !serialIOPort.serialConfigured()) {
                return;
            }
            if (!ExportTools.EOT) {
                eotSleepHandler.postDelayed(this, 50);
            }
            else if(ChameleonIO.DOWNLOAD){
                try {
                    streamDest.close();
                } catch (Exception ioe) {
                    MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
                    ioe.printStackTrace();
                } finally {
                    ChameleonIO.DOWNLOAD = false;
                    ChameleonIO.executeChameleonMiniCommand("LOGMODE=" + currentLogMode, ChameleonIO.TIMEOUT);
                    serialIOPort.releaseSerialPortLock();
                }
                if(!ExportTools.transmissionErrorOccurred) {
                    DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.defaultContext.getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "application/octet-stream",
                            outfile.getAbsolutePath(), outfile.length(), true);
                    String statusMsg = "Write internal log data to file " + outfile.getName() + "(+" + outfile.length() + " / " + fileSize + " bytes).\n\n";
                    statusMsg += "If you are not seeing the expected output, try running the LOGSTORE command from the tools menu first.";
                    MainActivityLogUtils.appendNewLog(new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, "EXPORT", statusMsg));
                    if (throwToLive) {
                        throwDeviceLogDataToLive(outfile);
                    }
                }
                else {
                    outfile.delete();
                    LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
                    MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Maximum number of NAK errors exceeded. Download of data aborted."));
                }
            }
            else if(ChameleonIO.UPLOAD) {
                Log.i(TAG, "Cleaning up after UPLOAD ...");
                try {
                    streamSrc.close();
                } catch (Exception ioe) {
                    MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
                    ioe.printStackTrace();
                } finally {
                    ChameleonIO.UPLOAD = false;
                    //ChameleonIO.getSettingFromDevice("READONLY=" + (ChameleonIO.deviceStatus.READONLY ? "1" : "0"));
                    serialIOPort.releaseSerialPortLock();
                }
                if(!ExportTools.transmissionErrorOccurred) {
                    ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                }
                else {
                    LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
                    MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "File transmission errors encountered. Maximum number of NAK errors exceeded. Download of data aborted."));
                }
            }
        }
    };
    public static Handler eotSleepHandler = new Handler();

    /**
     * Calculates the checksum of the passed byte buffer.
     * @param buffer
     * @param byteCount
     * @return byte checksum value
     */
    public static byte CalcChecksum(byte[] buffer, short byteCount) {
        byte checksum = CHECKSUM_INIT_VALUE;
        int bufPos = 0;
        while(byteCount-- != 0) {
            checksum += buffer[bufPos++];
        }
        return checksum;
    }

    /**
     * Handles the logic of receiving the data in a XModem download.
     * @param liveLogData
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static void performXModemSerialDownload(byte[] liveLogData) {
        ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
        if(serialIOPort == null || !serialIOPort.serialConfigured()) {
            return;
        }
        if(ExportTools.EOT)
            return; // waiting for conclusion of timer to cleanup the download files
        Log.i(TAG, "Received XModem data (#bytes=" + liveLogData.length + ") ..." + Utils.bytes2Hex(liveLogData));
        Log.i(TAG, "    => " + Utils.bytes2Ascii(liveLogData));
        byte[] frameBuffer = new byte[XMODEM_BLOCK_SIZE];
        if (liveLogData != null && liveLogData.length > 0 && liveLogData[0] != ExportTools.BYTE_EOT) {
            if (liveLogData[0] == ExportTools.BYTE_SOH && liveLogData[1] == ExportTools.CurrentFrameNumber && liveLogData[2] == (byte) (255 - ExportTools.CurrentFrameNumber)) {
                Log.i(TAG, "Writing XModem data ...");
                int dataBufferSize = liveLogData.length - 4;
                System.arraycopy(liveLogData, 3, frameBuffer, 0, ExportTools.XMODEM_BLOCK_SIZE);
                byte checksumByte = liveLogData[dataBufferSize + 3];
                ExportTools.Checksum = ExportTools.CalcChecksum(frameBuffer, ExportTools.XMODEM_BLOCK_SIZE);
                if (ExportTools.Checksum != checksumByte && currentNAKCount < MAX_NAK_COUNT) {
                    Log.w(TAG, "Sent another NAK (invalid checksum) : # = " + currentNAKCount);
                    serialIOPort.sendDataBuffer(new byte[]{ExportTools.BYTE_NAK});
                    currentNAKCount++;
                    return;
                }
                else if(ExportTools.Checksum != checksumByte) {
                    ExportTools.EOT = true;
                    ExportTools.transmissionErrorOccurred = true;
                    serialIOPort.sendDataBuffer(new byte[] {ExportTools.BYTE_CAN});
                    return;
                }
                try {
                    ExportTools.fileSize += liveLogData.length;
                    ExportTools.streamDest.write(frameBuffer);
                    ExportTools.streamDest.flush();
                    ExportTools.CurrentFrameNumber++;
                    serialIOPort.sendDataBuffer(new byte[]{BYTE_ACK});
                } catch (Exception e) {
                    ExportTools.EOT = true;
                    e.printStackTrace();
                }
            }
            else {
                if(currentNAKCount >= MAX_NAK_COUNT) {
                    ExportTools.EOT = true;
                    ExportTools.transmissionErrorOccurred = true;
                    serialIOPort.sendDataBuffer(new byte[] {ExportTools.BYTE_CAN});
                    return;
                }
                Log.w(TAG, "Sent another NAK (header bytes) : # = " + currentNAKCount);
                serialIOPort.sendDataBuffer(new byte[]{ExportTools.BYTE_NAK});
                currentNAKCount++;
            }
        }
        else {
            try {
                serialIOPort.sendDataBuffer(new byte[]{ExportTools.BYTE_ACK});
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            ExportTools.EOT = true;
        }
    }

    /**
     * Initiates the file download by XModem.
     * @param issueCmd
     * @param outfilePrefix
     * @param throwToLiveParam
     * @return boolean success of the operation
     * @ref LiveLoggerActivity.actionButtonExportLogDownload
     */
    public static boolean downloadByXModem(String issueCmd, String outfilePrefix, boolean throwToLiveParam) {
        ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
        if(serialIOPort == null || !serialIOPort.serialConfigured()) {
            return false;
        }
        LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
        String outfilePath = outfilePrefix + "-" + Utils.getTimestamp().replace(":", "") + ".dump";
        File downloadsFolder = new File("//sdcard//Download//");
        boolean docsFolderExists = true;
        if (!downloadsFolder.exists()) {
            docsFolderExists = downloadsFolder.mkdir();
        }
        if (docsFolderExists) {
            outfile = new File(downloadsFolder.getAbsolutePath(), outfilePath);
        }
        else {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to save output in Downloads folder."));
            LiveLoggerActivity.getInstance().clearStatusIcon(R.id.statusIconUlDl);
            return false;
        }

        try {
            outfile.createNewFile();
            streamDest = new FileOutputStream(outfile);
        } catch(Exception ioe) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
            LiveLoggerActivity.getInstance().clearStatusIcon(R.id.statusIconUlDl);
            return false;
        }

        throwToLive = throwToLiveParam;
        // turn of logging so the transfer doesn't get accidentally logged:
        currentLogMode = ChameleonIO.getSettingFromDevice("LOGMODE?");
        ChameleonIO.executeChameleonMiniCommand("LOGMODE=OFF", ChameleonIO.TIMEOUT);
        ChameleonIO.WAITING_FOR_XMODEM = true;
        ChameleonIO.getSettingFromDevice(issueCmd);
        serialIOPort.acquireSerialPortNoInterrupt();
        fileSize = 0;
        CurrentFrameNumber = FIRST_FRAME_NUMBER;
        currentNAKCount = 0;
        transmissionErrorOccurred = false;
        EOT = false;
        while(ChameleonIO.WAITING_FOR_XMODEM) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
            }
        }
        ChameleonIO.DOWNLOAD = true;
        serialIOPort.sendDataBuffer(new byte[]{BYTE_NAK});
        eotSleepHandler.postDelayed(eotSleepRunnable, 50);
        return true;
    }

    /**
     * Writes the downloaded log data to the logger interface tab after the download completes.
     * @param logDataFile
     */
    public static void throwDeviceLogDataToLive(File logDataFile) {
        try {
            FileInputStream fin = new FileInputStream(logDataFile);
            byte[] headerBytes = new byte[4];
            while(fin.read(headerBytes, 0, 4) == 0) {
                int dlen = (int) headerBytes[1];
                byte[] payloadBytes = new byte[dlen + 4];
                System.arraycopy(headerBytes, 0, payloadBytes, 0, 4);
                fin.read(payloadBytes, 4, dlen);
                LogEntryUI nextLogEntry = LogEntryUI.newInstance(payloadBytes, "");
                // highlight the entries so it's clear they're from the device's logs:
                nextLogEntry.getMainEntryContainer().setBackgroundColor(ThemesConfiguration.getThemeColorVariant(R.attr.deviceMemoryLogHighlight));
                MainActivityLogUtils.appendNewLog(nextLogEntry);
            }
            fin.close();
        } catch(Exception ioe) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
        }
    }

    /**
     * Implements the actual data exchange with the card in the upload process.
     * Currently freezes the UI after the BYTE_EOF frame is sent.
     * @param liveLogData
     */
    public static void performXModemSerialUpload(byte[] liveLogData) {
        ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
        if(serialIOPort == null || !serialIOPort.serialConfigured()) {
            return;
        }
        Log.i(TAG, "Received Upload Data (#=" + liveLogData.length + ") ... " + Utils.bytes2Hex(liveLogData));
        Log.i(TAG, "    => " + Utils.bytes2Ascii(liveLogData));
        if(ExportTools.EOT || liveLogData == null || liveLogData.length == 0)
            return;
        byte statusByte = liveLogData[0];
        if(uploadState == 0 || uploadState == 1 && statusByte == BYTE_ACK) {
            if(uploadState == 1)
                CurrentFrameNumber++;
            else
                uploadState = 1;
            uploadFramebuffer[0] = BYTE_SOH;
            uploadFramebuffer[1] = CurrentFrameNumber;
            uploadFramebuffer[2] = (byte) (255 - CurrentFrameNumber);
            byte[] payloadBytes = new byte[XMODEM_BLOCK_SIZE];
            try {
                if(streamSrc.available() == 0) {
                    Log.i(TAG, "Upload / Sending EOT to device.");
                    EOT = true;
                    serialIOPort.sendDataBuffer(new byte[]{BYTE_EOT});
                    return;
                }
                streamSrc.read(payloadBytes, 0, XMODEM_BLOCK_SIZE);
                System.arraycopy(payloadBytes, 0, uploadFramebuffer, 3, XMODEM_BLOCK_SIZE);
            } catch(IOException ioe) {
                EOT = true;
                transmissionErrorOccurred = true;
                serialIOPort.sendDataBuffer(new byte[]{BYTE_CAN});
                return;
            }
            uploadFramebuffer[XMODEM_BLOCK_SIZE + 3] = CalcChecksum(payloadBytes, XMODEM_BLOCK_SIZE);
            Log.i(TAG, "Upload Writing Data: frame=" + CurrentFrameNumber + ": " + Utils.bytes2Hex(uploadFramebuffer));
            serialIOPort.sendDataBuffer(uploadFramebuffer);
        }
        else if(statusByte == BYTE_NAK && currentNAKCount <= MAX_NAK_COUNT) {
            Log.i(TAG, "Upload / Sending Another NAK response (#=" + currentNAKCount + ")");
            currentNAKCount++;
            serialIOPort.sendDataBuffer(uploadFramebuffer);
        }
        else {
            EOT = true;
            transmissionErrorOccurred = true;
            serialIOPort.sendDataBuffer(new byte[]{BYTE_CAN});
            return;
        }
    }

    /**
     * Called to initiate the card data upload process.
     * @param rawResID
     * @ref LiveLoggerActivity.actionButtonUploadCard
     */
    public static void uploadCardFromRawByXModem(int rawResID) {
        try {
            InputStream istream = LiveLoggerActivity.defaultContext.getResources().openRawResource(rawResID);
            uploadCardFileByXModem(istream);
        } catch(Exception ioe) {
            String cardFilePath = LiveLoggerActivity.defaultContext.getResources().getResourceName(rawResID);
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to open chosen resource \"" + cardFilePath + "\": " + ioe.getMessage()));
            LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            return;
        }
    }

    /**
     * Called to initiate the card data upload process.
     * @param cardFilePath
     * @ref LiveLoggerActivity.actionButtonUploadCard
     */
    public static void uploadCardFileByXModem(String cardFilePath) {
        if(new File(cardFilePath).length() % XMODEM_BLOCK_SIZE != 0) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Invalid file size for the selected card file \"" + cardFilePath + "\". Aborting."));
            LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            return;
        }
        try {
            InputStream istream = new FileInputStream(cardFilePath);
            uploadCardFileByXModem(istream);
        } catch(IOException ioe) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to open chosen file \"" + cardFilePath + "\": " + ioe.getMessage()));
            LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            return;
        }
    }

    /**
     * Called to initiate the card data upload process.
     * @param cardInputStream
     * @ref LiveLoggerActivity.actionButtonUploadCard
     */
    public static void uploadCardFileByXModem(InputStream cardInputStream) {
        ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
        if(serialIOPort == null || !serialIOPort.serialConfigured() || cardInputStream == null)
            return;
        LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusupload16);
        streamSrc = cardInputStream;
        serialIOPort.acquireSerialPortNoInterrupt();
        ChameleonIO.WAITING_FOR_XMODEM = true;
        ChameleonIO.getSettingFromDevice("READONLY=0");
        ChameleonIO.executeChameleonMiniCommand("UPLOAD", ChameleonIO.TIMEOUT);
        fileSize = 0;
        CurrentFrameNumber = FIRST_FRAME_NUMBER;
        currentNAKCount = -1;
        transmissionErrorOccurred = false;
        uploadState = 0;
        EOT = false;
        while(ChameleonIO.WAITING_FOR_XMODEM) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {}
        }
        ChameleonIO.UPLOAD = true;
        serialIOPort.sendDataBuffer(new byte[]{BYTE_NAK});
        eotSleepHandler.postDelayed(eotSleepRunnable, 50);
    }

    /**
     * Writes the logged data to plaintext roughly in the format of the Python script on the
     * Chameleon Mini scripts page https://github.com/emsec/ChameleonMini/tree/master/Software.
     * @param fd
     * @return boolean success of the operation
     * @throws Exception (IOException)
     * @ref LiveLoggerActivity.actionButtonWriteFile
     */
    public static boolean writeFormattedLogFile(File fd) throws Exception {
        Log.i(TAG, String.valueOf("00".getBytes(StandardCharsets.US_ASCII)));

        FileOutputStream fout = new FileOutputStream(fd);
        for (int vi = 0; vi < MainActivityLogUtils.logDataFeed.getChildCount(); vi++) {
            View logEntryView = MainActivityLogUtils.logDataFeed.getChildAt(vi);
            if (MainActivityLogUtils.logDataEntries.get(vi) instanceof LogEntryUI) {
                String dataLine = ((LogEntryUI) MainActivityLogUtils.logDataEntries.get(vi)).toString() + "\n";
                fout.write(dataLine.getBytes(StandardCharsets.US_ASCII));
            }
            else {
                String lineStr = "\n## " + ((LogEntryMetadataRecord) MainActivityLogUtils.logDataEntries.get(vi)).toString() + "\n";
                fout.write(lineStr.getBytes(StandardCharsets.US_ASCII));
            }
        }
        fout.close();
        return true;
    }

    /**
     * Writes the logged data to color-coded HTML roughly in the format of the Python script on the
     * Chameleon Mini scripts page https://github.com/emsec/ChameleonMini/tree/master/Software.
     * @param fd
     * @return boolean success of the operation
     * @throws Exception (IOException)
     * @ref LiveLoggerActivity.actionButtonWriteFile
     */
    public static boolean writeHTMLLogFile(File fd) throws Exception {
        FileOutputStream fout = new FileOutputStream(fd);
        String htmlHeader = "<html><head><title>Chameleon Mini Live Debugger -- Logging Output</title></head><body>\n\n";
        fout.write(htmlHeader.getBytes(StandardCharsets.US_ASCII));
        String defaultBgColor = String.format(Locale.ENGLISH, "#%06X", (0xFFFFFF & ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDarkLog)));
        for (int vi = 0; vi < MainActivityLogUtils.logDataFeed.getChildCount(); vi++) {
            View logEntryView = MainActivityLogUtils.logDataFeed.getChildAt(vi);
            if (MainActivityLogUtils.logDataEntries.get(vi) instanceof LogEntryUI) {
                String bgColor = String.format(Locale.ENGLISH, "#%06X", (0xFFFFFF & logEntryView.getDrawingCacheBackgroundColor()));
                if(bgColor.equals(defaultBgColor))
                    bgColor = "#ffffff";
                String lineData = "<code bgcolor='" + bgColor + "'>" + ((LogEntryUI) MainActivityLogUtils.logDataEntries.get(vi)).toString() + "</code><br/>\n";
                fout.write(lineData.getBytes(StandardCharsets.US_ASCII));
            }
            else {
                String lineData = "<b><code>" + ((LogEntryMetadataRecord) MainActivityLogUtils.logDataEntries.get(vi)).toString() + "</code></b><br/>\n";
                fout.write(lineData.getBytes(StandardCharsets.US_ASCII));
            }
        }
        String htmlFooter = "</body></html>";
        fout.write(htmlFooter.getBytes(StandardCharsets.US_ASCII));
        fout.close();
        return true;
    }

    /**
     * Writes the logged data to the binary format returned by the LOGDOWNLOAD command.
     * @param fd
     * @return boolean success of the operation
     * @throws Exception (IOException)
     * @ref LiveLoggerActivity.actionButtonWriteFile
     * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/Page_Log.html
     */
    public static boolean writeBinaryLogFile(File fd) throws Exception {
        FileOutputStream fout = new FileOutputStream(fd);
        short localTicks = 0;
        for (int vi = 0; vi < MainActivityLogUtils.logDataFeed.getChildCount(); vi++) {
            View logEntryView = MainActivityLogUtils.logDataFeed.getChildAt(vi);
            if (MainActivityLogUtils.logDataEntries.get(vi) instanceof LogEntryUI) {
                LogEntryUI logEntry = (LogEntryUI) MainActivityLogUtils.logDataEntries.get(vi);
                byte[] entryBytes = logEntry.packageBinaryLogData(localTicks);
                localTicks = logEntry.getNextOffsetTime(localTicks);
                fout.write(entryBytes);
            }
        }
        fout.close();
        return true;
    }

    /**
     * Saves the output of the DUMP_MFU command to binary file.
     * @param filePathPrefix
     * @return boolean success of the operation
     * @ref LiveLoggerActivity.actionButtonDumpMFU
     */
    public static boolean saveBinaryDumpMFU(String filePathPrefix) {
        LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
        String mimeType = "application/octet-stream";
        String outfilePath = filePathPrefix + Utils.getTimestamp().replace(":", "") + ".bin";
        File downloadsFolder = new File("//sdcard//Download//");
        outfile = new File(downloadsFolder, outfilePath);
        boolean docsFolderExists = true;
        if (!downloadsFolder.exists()) {
            docsFolderExists = downloadsFolder.mkdir();
        }
        if (docsFolderExists) {
            outfile = new File(downloadsFolder.getAbsolutePath(),outfilePath);
        }
        else {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to save output in Downloads folder."));
            LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            return false;
        }
        try {
            outfile.createNewFile();
            FileOutputStream fout = new FileOutputStream(outfile);
            ChameleonIO.EXPECTING_BINARY_DATA = true;
            ChameleonIO.getSettingFromDevice("DUMP_MFU");
            fout.write(ChameleonIO.DEVICE_RESPONSE_BINARY);
            fout.flush();
            fout.close();
        } catch(Exception ioe) {
            MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            ioe.printStackTrace();
            return false;
        }
        DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, mimeType,
                outfile.getAbsolutePath(), outfile.length(),true);
        String statusMsg = "Dumped MFU binary data to " + outfilePath + " (" + String.valueOf(outfile.length()) + " bytes).";
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("EXPORT", statusMsg));
        return true;
    }

    /**
     * Clones the output of the DUMP_MFU command to the running DIP slot.
     * @param dataBytes
     * @return boolean success of the operation
     * @ref LiveLoggerActivity.actionButtonCloneMFU
     * @ref http://tech.springcard.com/2014/reading-and-writing-data-in-a-mifare-ultralight-card-with-a-proxnroll/
     * @ref https://stackoverflow.com/questions/30121809/how-do-i-permanently-lock-specific-data-pages-in-a-mifare-ultralight-c-tag
     */
    public static boolean cloneBinaryDumpMFU(byte[] dataBytes) {

        if(dataBytes.length % 4 != 0) { // realign array:
            int dbPadding = (4 - (dataBytes.length % 4)) % 4;
            byte[] fullDataBytes = new byte[dataBytes.length + dbPadding];
            System.arraycopy(dataBytes, 0, fullDataBytes, 0, dataBytes.length);
            dataBytes = fullDataBytes;
        }
        ChameleonIO.executeChameleonMiniCommand("CONFIG=MF_ULTRALIGHT", ChameleonIO.TIMEOUT);
        ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
        for(int page = 0; page < dataBytes.length; page += 4) {
            byte[] apduSendBytesHdr = {
                    (byte) 0xff, // CLA
                    (byte) 0xa2, // INS (write)
                    (byte) 0x00, // P1
                    (byte) (page / 4), // P2
            };
            byte[] apduSendBytesData = new byte[4];
            System.arraycopy(dataBytes, page, apduSendBytesData, 0, 4);
            int sendBytesHdr = Utils.bytes2Integer32(apduSendBytesHdr);
            int sendBytesData = Utils.bytes2Integer32(apduSendBytesData);
            String chameleonCmd = String.format("SEND %08x%08x", sendBytesHdr, sendBytesData);
            ChameleonIO.executeChameleonMiniCommand(chameleonCmd, ChameleonIO.TIMEOUT);
        }
        return true;

    }

    public static void exportLogDownload(String action) {
        if(action.equals("LOGDOWNLOAD"))
            ExportTools.downloadByXModem("LOGDOWNLOAD", "devicelog", false);
        else if(action.equals("LOGDOWNLOAD2LIVE"))
            ExportTools.downloadByXModem("LOGDOWNLOAD", "devicelog", true);
        else if(action.equals("DOWNLOAD")) {
            String dldCmd = ChameleonIO.REVE_BOARD ? "download" : "DOWNLOAD";
            ExportTools.downloadByXModem(dldCmd, "carddata-" + ChameleonIO.deviceStatus.CONFIG, false);
        }
    }

}