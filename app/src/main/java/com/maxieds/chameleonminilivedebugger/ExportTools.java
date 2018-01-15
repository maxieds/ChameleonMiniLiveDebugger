package com.maxieds.chameleonminilivedebugger;

import android.app.DownloadManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;
import static com.maxieds.chameleonminilivedebugger.ExportTools.State_t.STATE_OFF;
import static com.maxieds.chameleonminilivedebugger.LiveLoggerActivity.logDataFeed;

//import java.nio.file.Path;
//import java.nio.file.Files;

/**
 * Created by maxie on 1/11/18.
 * Based on XModem.c/h in the Chameleon Mini firmware distribution.
 */

public class ExportTools {

    public static final long LOCK_TIMEOUT = 2000;
    public static boolean EOT = false;
    public static int MAX_NAK_COUNT = 6;

    public static final byte BYTE_NAK = (byte) 0x15;
    public static final byte BYTE_SOH = (byte) 0x01;
    public static final byte BYTE_ACK = (byte) 0x06;
    public static final byte BYTE_CAN = (byte) 0x18;
    public static final byte BYTE_EOF = (byte) 0x1A;
    public static final byte BYTE_EOT = (byte) 0x04;
    public static final byte BYTE_ESC = (byte) 0x1B;

    public static final short XMODEM_BLOCK_SIZE = 128;
    public static final byte FIRST_FRAME_NUMBER = (byte) 1;
    public static final byte CHECKSUM_INIT_VALUE = 0;

    public static enum State_t {
        STATE_OFF,
        STATE_RECEIVE_INIT,
        STATE_RECEIVE_WAIT,
        STATE_RECEIVE_FRAMENUM1,
        STATE_RECEIVE_FRAMENUM2,
        STATE_RECEIVE_DATA,
        STATE_RECEIVE_PROCESS,
        STATE_SEND_INIT,
        STATE_SEND_WAIT,
        STATE_SEND_EOT
    };
    public static State_t State = STATE_OFF;

    public static int fileSize = 0;
    public static FileOutputStream streamDest;
    public static File outfile;
    private static String currentLogMode = "LIVE";
    private static boolean throwToLive = false;
    public static byte[] frameBuffer = new byte[XMODEM_BLOCK_SIZE];
    public static byte CurrentFrameNumber;
    public static byte Checksum;
    public static int currentNAKCount;
    public static boolean transmissionErrorOccurred;

    public static Runnable eotSleepRunnable = new Runnable() {
        public void run() {
            if (!ExportTools.EOT) {
                eotSleepHandler.postDelayed(this, 50);
            }
            else {
                try {
                    streamDest.close();
                } catch (Exception ioe) {
                    LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
                    ioe.printStackTrace();
                } finally {
                    ChameleonIO.DOWNLOAD = false;
                    ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "LOGMODE=" + currentLogMode, ChameleonIO.TIMEOUT);
                    LiveLoggerActivity.serialPortLock.release();
                }
                if(!ExportTools.transmissionErrorOccurred) {
                    DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.defaultContext.getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "application/octet-stream",
                            outfile.getAbsolutePath(), outfile.length(), true);
                    String statusMsg = "Write internal log data to file " + outfile.getName() + "(+" + fileSize + " / " + outfile.length() + " bytes).\n";
                    statusMsg += "If you are not seeing the expected output, try running the LOGSTORE command from the tools menu first.";
                    LiveLoggerActivity.appendNewLog(new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, "EXPORT", statusMsg));
                    if (throwToLive) {
                        throwDeviceLogDataToLive(outfile);
                    }
                }
                else {
                    outfile.delete();
                    LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
                    LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Maximum number of NAK errors exceeded. Download of data aborted."));
                }
            }
        }
    };
    public static Handler eotSleepHandler = new Handler();

    public static byte CalcChecksum(byte[] buffer, short byteCount) {
        byte checksum = CHECKSUM_INIT_VALUE;
        int bufPos = 0;
        while(byteCount-- != 0) {
            checksum += buffer[bufPos++];
        }
        return checksum;
    }

    public static void performXModemSerialDownload(byte[] liveLogData) {
        Log.i(TAG, "Received XModem data ..." + Utils.bytes2Hex(liveLogData));
        if (liveLogData != null && liveLogData.length > 0 && liveLogData[0] != ExportTools.BYTE_EOT) {
            if (liveLogData[0] == ExportTools.BYTE_SOH && liveLogData[1] == ExportTools.CurrentFrameNumber && liveLogData[2] == (byte) (255 - ExportTools.CurrentFrameNumber)) {
                Log.i(TAG, "Writing XModem data ...");
                System.arraycopy(liveLogData, 3, ExportTools.frameBuffer, 0, ExportTools.XMODEM_BLOCK_SIZE);
                byte checksumByte = liveLogData[liveLogData.length - 1];
                ExportTools.Checksum = ExportTools.CalcChecksum(ExportTools.frameBuffer, ExportTools.XMODEM_BLOCK_SIZE);
                if (ExportTools.Checksum != checksumByte && currentNAKCount < MAX_NAK_COUNT) {
                    Log.w(TAG, "Sent another NAK (invalid checksum) : # = " + currentNAKCount);
                    LiveLoggerActivity.serialPort.write(new byte[]{ExportTools.BYTE_NAK});
                    currentNAKCount++;
                    return;
                }
                else if(ExportTools.Checksum != checksumByte) {
                    ExportTools.EOT = true;
                    ExportTools.transmissionErrorOccurred = true;
                    LiveLoggerActivity.serialPort.write(new byte[] {ExportTools.BYTE_CAN});
                    return;
                }
                try {
                    ExportTools.fileSize += liveLogData.length;
                    ExportTools.streamDest.write(ExportTools.frameBuffer);
                    ExportTools.streamDest.flush();
                    ExportTools.CurrentFrameNumber++;
                    LiveLoggerActivity.serialPort.write(new byte[]{BYTE_ACK});
                } catch (Exception e) {
                    ExportTools.EOT = true;
                    e.printStackTrace();
                }
            }
            else {
                if(currentNAKCount >= MAX_NAK_COUNT) {
                    ExportTools.EOT = true;
                    ExportTools.transmissionErrorOccurred = true;
                    LiveLoggerActivity.serialPort.write(new byte[] {ExportTools.BYTE_CAN});
                    return;
                }
                Log.w(TAG, "Sent another NAK (invalid checksum) : # = " + currentNAKCount);
                LiveLoggerActivity.serialPort.write(new byte[]{ExportTools.BYTE_NAK});
                currentNAKCount++;
            }
        }
        else {
            try {
                LiveLoggerActivity.serialPort.write(new byte[]{ExportTools.BYTE_ACK});
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            ExportTools.EOT = true;
        }
    }

    public static boolean downloadByXModem(String issueCmd, String outfilePrefix, boolean throwToLiveParam) {
        LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
        String outfilePath = outfilePrefix + "-" + Utils.getTimestamp().replace(":", "") + ".bin";
        File downloadsFolder = new File("//sdcard//Download//");
        boolean docsFolderExists = true;
        if (!downloadsFolder.exists()) {
            docsFolderExists = downloadsFolder.mkdir();
        }
        if (docsFolderExists) {
            outfile = new File(downloadsFolder.getAbsolutePath(), outfilePath);
        }
        else {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to save output in Downloads folder."));
            LiveLoggerActivity.runningActivity.clearStatusIcon(R.id.statusIconUlDl);
            return false;
        }

        try {
            outfile.createNewFile();
            streamDest = new FileOutputStream(outfile);
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
            LiveLoggerActivity.runningActivity.clearStatusIcon(R.id.statusIconUlDl);
            return false;
        }

        LiveLoggerActivity.serialPortLock.acquireUninterruptibly();
        throwToLive = throwToLiveParam;
        ChameleonIO.DOWNLOAD = true;
        EOT = false;
        // turn of logging so the transfer doesn't get accidentally logged:
        currentLogMode = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "LOGMODE?");
        ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "LOGMODE=OFF", ChameleonIO.TIMEOUT);
        LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, issueCmd);
        fileSize = 0;
        CurrentFrameNumber = FIRST_FRAME_NUMBER;
        currentNAKCount = 0;
        transmissionErrorOccurred = false;
        LiveLoggerActivity.serialPort.write(new byte[]{BYTE_NAK});
        //while (!EOT) {
        //    Thread.sleep(500);
        //}
        eotSleepHandler.postDelayed(eotSleepRunnable, 50);
        return true;
    }

    public static void throwDeviceLogDataToLive(File logDataFile) {
        try {
            FileInputStream fin = new FileInputStream(logDataFile);
            byte[] headerBytes = new byte[4];
            while(fin.read(headerBytes, 0, 4) == 0) {
                int dlen = (int) headerBytes[1];
                byte[] payloadBytes = new byte[dlen + 4];
                System.arraycopy(headerBytes, 0, payloadBytes, 0, 4);
                fin.read(payloadBytes, 4, dlen);
                LiveLoggerActivity.appendNewLog(LogEntryUI.newInstance(payloadBytes, ""));
                // highlight the entries so it's clear they're from the device's logs:
                LiveLoggerActivity.logDataFeed.getChildAt(logDataFeed.getChildCount() - 1).setBackgroundColor(LiveLoggerActivity.runningActivity.getResources().getColor(R.color.deviceMemoryLogHighlight));
            }
            fin.close();
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
        }
    }

    public static boolean writeFormattedLogFile(File fd) throws Exception {
        Log.i(TAG, String.valueOf("00".getBytes(StandardCharsets.US_ASCII)));

        FileOutputStream fout = new FileOutputStream(fd);
        for (int vi = 0; vi < LiveLoggerActivity.logDataFeed.getChildCount(); vi++) {
            View logEntryView = LiveLoggerActivity.logDataFeed.getChildAt(vi);
            if (LiveLoggerActivity.logDataEntries.get(vi) instanceof LogEntryUI) {
                String dataLine = ((LogEntryUI) LiveLoggerActivity.logDataEntries.get(vi)).toString() + "\n";
                fout.write(dataLine.getBytes(StandardCharsets.US_ASCII));
            }
            else {
                String lineStr = "\n## " + ((LogEntryMetadataRecord) LiveLoggerActivity.logDataEntries.get(vi)).toString() + "\n";
                fout.write(lineStr.getBytes(StandardCharsets.US_ASCII));
            }
        }
        fout.close();
        return true;
    }

    public static boolean writeHTMLLogFile(File fd) throws Exception {
        FileOutputStream fout = new FileOutputStream(fd);
        String htmlHeader = "<html><head><title>Chameleon Mini Live Debugger -- Logging Output</title></head><body>\n\n";
        fout.write(htmlHeader.getBytes(StandardCharsets.US_ASCII));
        String defaultBgColor = String.format("#%06X", (0xFFFFFF & R.color.colorPrimaryDarkLog));
        for (int vi = 0; vi < LiveLoggerActivity.logDataFeed.getChildCount(); vi++) {
            View logEntryView = LiveLoggerActivity.logDataFeed.getChildAt(vi);
            if (LiveLoggerActivity.logDataEntries.get(vi) instanceof LogEntryUI) {
                String bgColor = String.format("#%06X", (0xFFFFFF & logEntryView.getDrawingCacheBackgroundColor()));
                if(bgColor.equals(defaultBgColor))
                    bgColor = "#ffffff";
                String lineData = "<code bgcolor='" + bgColor + "'>" + ((LogEntryUI) LiveLoggerActivity.logDataEntries.get(vi)).toString() + "</code><br/>\n";
                fout.write(lineData.getBytes(StandardCharsets.US_ASCII));
            }
            else {
                String lineData = "<b><code>" + ((LogEntryMetadataRecord) LiveLoggerActivity.logDataEntries.get(vi)).toString() + "</code></b><br/>\n";
                fout.write(lineData.getBytes(StandardCharsets.US_ASCII));
            }
        }
        String htmlFooter = "</body></html>";
        fout.write(htmlFooter.getBytes(StandardCharsets.US_ASCII));
        fout.close();
        return true;
    }

    public static boolean writeBinaryLogFile(File fd) throws Exception {
        FileOutputStream fout = new FileOutputStream(fd);
        short localTicks = 0;
        for (int vi = 0; vi < LiveLoggerActivity.logDataFeed.getChildCount(); vi++) {
            View logEntryView = LiveLoggerActivity.logDataFeed.getChildAt(vi);
            if (LiveLoggerActivity.logDataEntries.get(vi) instanceof LogEntryUI) {
                LogEntryUI logEntry = (LogEntryUI) LiveLoggerActivity.logDataEntries.get(vi);
                byte[] entryBytes = logEntry.packageBinaryLogData(localTicks);
                localTicks = logEntry.getNextOffsetTime(localTicks);
                fout.write(entryBytes);
            }
        }
        fout.close();
        return true;
    }

    public static boolean saveBinaryDumpMFU(String filePathPrefix) {
        LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
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
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to save output in Downloads folder."));
            LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            return false;
        }
        try {
            outfile.createNewFile();
            FileOutputStream fout = new FileOutputStream(outfile);
            ChameleonIO.EXPECTING_BINARY_DATA = true;
            LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "DUMP_MFU");
            fout.write(ChameleonIO.DEVICE_RESPONSE_BINARY);
            fout.flush();
            fout.close();
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            ioe.printStackTrace();
            return false;
        }
        DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, mimeType,
                outfile.getAbsolutePath(), outfile.length(),true);
        String statusMsg = "Dumped MFU binary data to " + outfilePath + " (" + String.valueOf(outfile.length()) + " bytes).";
        LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("EXPORT", statusMsg));
        return true;
    }

}
