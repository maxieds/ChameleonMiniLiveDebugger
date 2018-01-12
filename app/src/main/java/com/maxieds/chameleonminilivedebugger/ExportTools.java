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


    public static boolean EOT = false;
    public static UsbSerialDevice xferSerialPort;
    public static int fileSize = 0;

    public static void pauseMainThreadSerial() {
        ChameleonIO.PAUSED = true;
        ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
        ChameleonIO.deviceStatus = null;
        LiveLoggerActivity.runningActivity.closeSerialPort(LiveLoggerActivity.serialPort);
        ChameleonIO.WAITING_FOR_RESPONSE = false;
    }

    public static void resumeMainThreadSerial() {
        ChameleonIO.PAUSED = false;
        LiveLoggerActivity.runningActivity.configureSerialPort(LiveLoggerActivity.serialPort, LiveLoggerActivity.runningActivity.usbReaderCallback);
        ChameleonIO.deviceStatus = new ChameleonIO.DeviceStatusSettings();
        //ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
        ChameleonIO.deviceStatus.statsUpdateHandler.postDelayed(ChameleonIO.deviceStatus.statsUpdateRunnable, ChameleonIO.deviceStatus.STATS_UPDATE_INTERVAL);
    }

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

    public static boolean downloadByXModem(String issueCmd, String outfilePrefix, boolean throwToLive) {

        ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "LOGSTORE", ChameleonIO.TIMEOUT);
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

        final FileOutputStream fout;
        try {
            outfile.createNewFile();
            fout = new FileOutputStream(outfile);
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
            return false;
        }

        UsbSerialInterface.UsbReadCallback usbReaderCallback = new UsbSerialInterface.UsbReadCallback() {
            FileOutputStream streamDest = fout;
            byte[] frameBuffer = new byte[XMODEM_BLOCK_SIZE];
            @Override
            public void onReceivedData(byte[] liveLogData) {
                Log.i(TAG, "Received XModem data ..." + Utils.bytes2Hex(liveLogData));
                if(liveLogData != null && liveLogData.length > 0 && liveLogData[0] != BYTE_EOT) {
                    if(liveLogData[0] == BYTE_SOH && liveLogData[1] == CurrentFrameNumber && liveLogData[2] == (byte) (255 - CurrentFrameNumber)) {
                        Log.i(TAG, "Writing XModem data ...");
                        System.arraycopy(liveLogData, 3, frameBuffer, 0, XMODEM_BLOCK_SIZE);
                        try {
                            fileSize += liveLogData.length;
                            streamDest.write(frameBuffer);
                            streamDest.flush();
                            CurrentFrameNumber++;
                            xferSerialPort.write(new byte[]{BYTE_ACK});
                        } catch (Exception e) {
                            ExportTools.EOT = true;
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.w(TAG, "Sent another NAK");
                        try {
                            xferSerialPort.write(new byte[]{BYTE_NAK});
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    try {
                        streamDest.close();
                    } catch(Exception ioe) {
                        ioe.printStackTrace();
                    }
                    ExportTools.EOT = true;
                    xferSerialPort.write(new byte[]{BYTE_ACK});
                }
            }
        };

        try {
            fileSize = 0;
            pauseMainThreadSerial();
            xferSerialPort = LiveLoggerActivity.runningActivity.configureSerialPort(null, usbReaderCallback);
            CurrentFrameNumber = FIRST_FRAME_NUMBER;
            xferSerialPort.write(new byte[]{BYTE_NAK});
            while (!EOT) {
                Thread.sleep(50);
            }
            fout.close();
            xferSerialPort.close();
            resumeMainThreadSerial();
        } catch(Exception ioe) {
            LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            ioe.printStackTrace();
            return false;
        }
        DownloadManager downloadManager = (DownloadManager) LiveLoggerActivity.defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "application/octet-stream",
                outfile.getAbsolutePath(), outfile.length(),true);
        LiveLoggerActivity.appendNewLog(new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, "NEW EVENT", "Write internal log data to file " + outfilePath + "(+" + fileSize + " / " + outfile.length() + " bytes)."));
        if(throwToLive) {
            throwDeviceLogDataToLive(outfile);
        }
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
