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
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;
import static com.maxieds.chameleonminilivedebugger.ExportTools.throwDeviceLogDataToLive;
import static com.maxieds.chameleonminilivedebugger.LiveLoggerActivity.defaultContext;
import static com.maxieds.chameleonminilivedebugger.LiveLoggerActivity.logDataFeed;

/**
 * Created by maxie on 1/11/18.
 */

public class ExportTools {

    public static final byte BYTE_NAK = (byte) 0x15;
    public static final byte BYTE_SOH = (byte) 0x01;
    public static final byte BYTE_ACK = (byte) 0x06;
    public static final byte BYTE_CAN = (byte) 0x18;
    public static final byte BYTE_EOF = (byte) 0x1A;
    public static final byte BYTE_EOT = (byte) 0x04;
    public static final byte BYTE_ESC = (byte) 0x1B;
    public static final short XMODEM_BLOCK_SIZE = 128;

    public static boolean EOT = false;
    public static UsbSerialDevice xferSerialPort;
    public static int fileSize = 0;

    public static boolean downloadByZModem(String issueCmd, String outfilePrefix, boolean throwToLive) {

        LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, issueCmd);
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
            return false;
        }

        final DataOutputStream fout;
        try {
            outfile.createNewFile();
            fout = new DataOutputStream(new FileOutputStream(outfile));
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
            return false;
        }

        UsbSerialInterface.UsbReadCallback usbReaderCallback = new UsbSerialInterface.UsbReadCallback() {
            DataOutputStream streamDest = fout;
            @Override
            public void onReceivedData(byte[] liveLogData) {
                fileSize += liveLogData.length;
                Log.i(TAG, "Received XModem data ...");
                if(liveLogData.length == XMODEM_BLOCK_SIZE) {
                    try {
                        streamDest.write(liveLogData);
                        xferSerialPort.write(new byte[] {BYTE_ACK});
                    } catch(Exception e) {
                        ExportTools.EOT = true;
                    }
                }
                else {
                    ExportTools.EOT = true;
                    xferSerialPort.write(new byte[]{BYTE_CAN});
                }
            }
        };

        try {
            fileSize = 0;
            ChameleonIO.PAUSED = true;
            xferSerialPort = LiveLoggerActivity.runningActivity.configureSerialPort(null, usbReaderCallback);
            xferSerialPort.write(new byte[]{BYTE_NAK});
            while (!EOT) {
                Thread.sleep(50);
            }
            xferSerialPort.close();
            ChameleonIO.PAUSED = false;
            fout.close();
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
            return false;
        }
        DownloadManager downloadManager = (DownloadManager) defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "application/octet-stream",
                outfile.getAbsolutePath(), outfile.length(),true);
        LiveLoggerActivity.appendNewLog(new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, "NEW EVENT", "Write internal log data to file " + outfilePath + "(+" + fileSize + " bytes)."));
        if(throwToLive) {
            throwDeviceLogDataToLive(outfile);
        }
        return true;
    }

    public static void throwDeviceLogDataToLive(File logDataFile) {
        try {
            DataInputStream fin = new DataInputStream(new FileInputStream(logDataFile));
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
