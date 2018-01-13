package com.maxieds.chameleonminilivedebugger;

import android.app.DownloadManager;
import android.util.Log;
import android.widget.Button;

import com.felhr.usbserial.SerialInputStream;
import com.felhr.usbserial.SerialOutputStream;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Files;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;
import static com.maxieds.chameleonminilivedebugger.ExportTools.State_t.STATE_OFF;
import static com.maxieds.chameleonminilivedebugger.ExportTools.throwDeviceLogDataToLive;
import static com.maxieds.chameleonminilivedebugger.LiveLoggerActivity.defaultContext;
import static com.maxieds.chameleonminilivedebugger.LiveLoggerActivity.logDataFeed;

/**
 * Created by maxie on 1/11/18.
 * Based on XModem.c/h in the Chameleon Mini firmware distribution.
 */

public class ExportTools {

    public static final long LOCK_TIMEOUT = 2000;
    public static boolean EOT = false;

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
    public static byte[] frameBuffer = new byte[XMODEM_BLOCK_SIZE];
    public static byte CurrentFrameNumber;
    public static byte Checksum;

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
                if (ExportTools.Checksum != checksumByte) {
                    Log.w(TAG, "Sent another NAK (invalid checksum)");
                    try {
                        LiveLoggerActivity.serialPort.write(new byte[]{ExportTools.BYTE_NAK});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            } else {
                Log.w(TAG, "Sent another NAK");
                try {
                    LiveLoggerActivity.serialPort.write(new byte[]{ExportTools.BYTE_NAK});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                LiveLoggerActivity.serialPort.write(new byte[]{ExportTools.BYTE_ACK});
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            ExportTools.EOT = true;
        }
    }

    public static boolean downloadByXModem(String issueCmd, String outfilePrefix, boolean throwToLive) {
        LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
        String outfilePath = outfilePrefix + "-" + Utils.getTimestamp().replace(":", "") + ".bin";
        File downloadsFolder = new File("//sdcard//Download//");
        File outfile;
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

        String currentLogMode = "LIVE";
        try {
            LiveLoggerActivity.serialPortLock.acquireUninterruptibly();
            ChameleonIO.DOWNLOAD = true;
            // turn of logging so the transfer doesn't get accidentally logged:
            currentLogMode = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "LOGMODE?");
            ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "LOGMODE=OFF", ChameleonIO.TIMEOUT);
            //ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "LOGSTORE", ChameleonIO.TIMEOUT);
            LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, issueCmd);
            fileSize = 0;
            CurrentFrameNumber = FIRST_FRAME_NUMBER;
            LiveLoggerActivity.serialPort.write(new byte[]{BYTE_NAK});
            while (!EOT) {
                Thread.sleep(50);
            }
            streamDest.close();
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
        } finally {
            ChameleonIO.DOWNLOAD = false;
            ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "LOGMODE=" + currentLogMode, ChameleonIO.TIMEOUT);
            LiveLoggerActivity.serialPortLock.release();
        }
        DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "application/octet-stream",
                outfile.getAbsolutePath(), outfile.length(),true);
        String statusMsg = "Write internal log data to file " + outfilePath + "(+" + fileSize + " / " + outfile.length() + " bytes).\n";
        statusMsg += "If you are not seeing the expected output, try running the LOGSTORE command from the tools menu first.";
        LiveLoggerActivity.appendNewLog(new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, "NEW EVENT", statusMsg));
        if(throwToLive) {
            throwDeviceLogDataToLive(outfile);
        }
        LiveLoggerActivity.runningActivity.clearStatusIcon(R.id.statusIconUlDl);
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






}
