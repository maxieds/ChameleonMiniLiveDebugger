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

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.maxieds.chameleonminilivedebugger.ChameleonLogUtils.LogCode.LOG_INFO_CODEC_READER_FIELD_DETECTED;
import static com.maxieds.chameleonminilivedebugger.ChameleonLogUtils.LogCode.LOG_INFO_CODEC_RX_DATA;
import static com.maxieds.chameleonminilivedebugger.ChameleonLogUtils.LogCode.LOG_INFO_CODEC_RX_DATA_W_PARITY;

public class SerialIOReceiver implements ChameleonSerialIOInterface, ChameleonSerialIOInterface.SerialDataReceiverInterface {

    private static final String TAG = SerialIOReceiver.class.getSimpleName();

    public void setListenerContext(Context context) {}

    public String getInterfaceLoggingTag() {
        return "AbstractSerialReceiver";
    }

    public boolean notifySerialDataReceived(byte[] serialData) {
        return false;
    }

    public boolean notifyLogDataReceived(byte[] serialData) {
        return false;
    }

    public boolean notifyDeviceFound() {
        return false;
    }

    public boolean notifyDeviceConnectionTerminated() {
        return false;
    }

    public boolean notifyStatus(String msgType, String statusMsg) {
        return false;
    }

    public boolean isWiredUSB() {
        return false;
    }

    public boolean isBluetooth() {
        return false;
    }

    public int setSerialBaudRate(int baudRate) {
        return STATUS_ERROR;
    }

    public int setSerialBaudRateHigh() {
        return STATUS_ERROR;
    }

    public int setSerialBaudRateLimited() {
        return STATUS_ERROR;
    }

    public boolean startScanningDevices() {
        return false;
    }

    public boolean stopScanningDevices() {
        return false;
    }

    public String getActiveDeviceInfo() {
        return null;
    }

    public int configureSerial() {
        return STATUS_ERROR;
    }

    public int shutdownSerial() {
        return STATUS_ERROR;
    }

    public boolean serialConfigured() {
        return false;
    }

    public boolean serialReceiversRegistered() {
        return false;
    }

    public boolean acquireSerialPort() {
        return false;
    }

    public boolean acquireSerialPortNoInterrupt() {
        return false;
    }

    public boolean tryAcquireSerialPort(int timeout) {
        return false;
    }

    public boolean releaseSerialPortLock() {
        return false;
    }

    private static ChameleonSerialIOInterface.SerialDataReceiverInterface redirectSerialDataInterface = null;

    public static void setRedirectInterface(ChameleonSerialIOInterface.SerialDataReceiverInterface iface) {
        redirectSerialDataInterface = iface;
    }

    public static void resetRedirectInterface() {
        redirectSerialDataInterface = null;
    }

    public void onReceivedData(byte[] liveLogData) {

        if(redirectSerialDataInterface != null) {
            redirectSerialDataInterface.onReceivedData(liveLogData);
            return;
        }
        if(liveLogData == null || liveLogData.length == 0) {
            return;
        }
        Log.d(getInterfaceLoggingTag(), "SerialReaderCallback Received Data: (HEX) " + Utils.bytes2Hex(liveLogData));
        Log.d(getInterfaceLoggingTag(), "SerialReaderCallback Received Data: (TXT) " + Utils.bytes2Ascii(liveLogData));

        if (liveLogData.length == 0) {
            return;
        }
        int loggingRespSize = ChameleonLogUtils.ResponseIsLiveLoggingBytes(liveLogData);
        if (loggingRespSize > 0) {
            Log.i(TAG, "Received new LogEntry @ " + String.format(BuildConfig.DEFAULT_LOCALE, "0x%02x", liveLogData[0]));
            if(ChameleonLogUtils.LOGMODE_ENABLE_PRINTING_LIVE_LOGS) {
                notifyLogDataReceived(liveLogData);
            }
            byte logCode = liveLogData[0];
            if(ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_CODECRX_STATUS_INDICATOR &&
                    (ChameleonLogUtils.LogCode.LOG_CODE_MAP.get(logCode) == LOG_INFO_CODEC_RX_DATA ||
                            ChameleonLogUtils.LogCode.LOG_CODE_MAP.get(logCode) == LOG_INFO_CODEC_RX_DATA_W_PARITY)) {
                LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusCodecRXDataEvent, R.drawable.toolbar_icon16_codec_rx);
            }
            return;
        }
        if (ChameleonIO.PAUSED) {
            return;
        } else if (ChameleonIO.DOWNLOAD) {
            ExportTools.performXModemSerialDownload(liveLogData);
            return;
        } else if (ChameleonIO.UPLOAD) {
            ExportTools.performXModemSerialUpload(liveLogData);
            return;
        } else if (ChameleonIO.WAITING_FOR_XMODEM) {
            String strLogData = new String(liveLogData);
            if (strLogData.length() >= 11 && strLogData.substring(0, 11).equals("110:WAITING")) {
                ChameleonIO.WAITING_FOR_XMODEM = false;
                return;
            }
        } else if (ChameleonIO.isCommandResponse(liveLogData)) {
            String[] strLogData = (new String(liveLogData)).split("[\n\r\t][\n\r\t]+");
            ChameleonIO.DEVICE_RESPONSE_CODE = strLogData[0];
            int respCodeStartIndex = Utils.getFirstResponseCodeIndex(ChameleonIO.DEVICE_RESPONSE_CODE);
            ChameleonIO.DEVICE_RESPONSE_CODE = ChameleonIO.DEVICE_RESPONSE_CODE.substring(respCodeStartIndex);
            boolean respCodeHasPreText = respCodeStartIndex > 0;
            if (respCodeHasPreText) {
                strLogData[0] = strLogData[0].substring(0, respCodeStartIndex - 1);
            }
            if (strLogData.length >= 2) {
                if (respCodeHasPreText) {
                    ChameleonIO.DEVICE_RESPONSE = Arrays.copyOfRange(strLogData, 0, strLogData.length);
                } else {
                    ChameleonIO.DEVICE_RESPONSE = Arrays.copyOfRange(strLogData, 1, strLogData.length);
                }
            } else
                ChameleonIO.DEVICE_RESPONSE[0] = strLogData[0];
            if (ChameleonIO.EXPECTING_BINARY_DATA) {
                int binaryBufSize = liveLogData.length - ChameleonIO.DEVICE_RESPONSE_CODE.length() - 2;
                ChameleonIO.DEVICE_RESPONSE_BINARY = new byte[binaryBufSize];
                System.arraycopy(liveLogData, liveLogData.length - binaryBufSize, ChameleonIO.DEVICE_RESPONSE_BINARY, 0, binaryBufSize);
                ChameleonIO.EXPECTING_BINARY_DATA = false;
            }
            ChameleonIO.WAITING_FOR_RESPONSE = false;
            return;
        }
        notifySerialDataReceived(liveLogData);
    }

    public int sendDataBuffer(byte[] dataWriteBuffer) {
        return STATUS_OK;
    }

}
