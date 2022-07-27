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
import android.content.Intent;
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

    private Context notifyContext;

    public void setListenerContext(Context context) { notifyContext = context; }

    public String getInterfaceLoggingTag() {
        return TAG;
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
        return setSerialBaudRate(ChameleonSerialIOInterface.HIGH_SPEED_BAUD_RATE);
    }

    public int setSerialBaudRateLimited() {
        return setSerialBaudRate(ChameleonSerialIOInterface.LIMITED_SPEED_BAUD_RATE);
    }

    public boolean startScanningDevices() {
        return false;
    }

    public boolean stopScanningDevices() {
        return false;
    }

    public String getDeviceName() { return AndroidSettingsStorage.getStringValueByKey(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE, AndroidSettingsStorage.PROFILE_NAME_PREFERENCE); }

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

    public boolean notifySerialDataReceived(byte[] serialData) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED);
        notifyIntent.putExtra(ChameleonSerialIOInterface.SERIALIO_BYTE_DATA, serialData);
        notifyContext.sendBroadcast(notifyIntent);
        AndroidLog.i(TAG, "SERIALIO_DATA_RECEIVED: (HEX) " + Utils.bytes2Hex(serialData));
        AndroidLog.i(TAG, "SERIALIO_DATA_RECEIVED: (TXT) " + Utils.bytes2Ascii(serialData));
        return true;
    }

    public boolean notifyLogDataReceived(byte[] serialData) {
        if(serialData.length < ChameleonLogUtils.LOGGING_MIN_DATA_BYTES + 4) {
            return false;
        }
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED);
        notifyIntent.putExtra(ChameleonSerialIOInterface.SERIALIO_BYTE_DATA, serialData);
        notifyContext.sendBroadcast(notifyIntent);
        AndroidLog.i(TAG, "SERIALIO_LOGDATA_RECEIVED: (HEX) " + Utils.bytes2Hex(serialData));
        AndroidLog.i(TAG, "SERIALIO_LOGDATA_RECEIVED: (TXT) " + Utils.bytes2Ascii(serialData));
        return true;
    }

    public boolean notifyDeviceConnectionTerminated() {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyStatus(String msgType, String statusMsg) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS);
        notifyIntent.putExtra(ChameleonSerialIOInterface.SERIALIO_STATUS_TYPE, msgType);
        notifyIntent.putExtra(ChameleonSerialIOInterface.SERIALIO_STATUS_MSG, statusMsg);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public void onReceivedData(byte[] liveLogData) {

        if(redirectSerialDataInterface != null) {
            redirectSerialDataInterface.onReceivedData(liveLogData);
            return;
        } else if(liveLogData == null || liveLogData.length == 0) {
            return;
        } else if (liveLogData.length == 0) {
            return;
        }
        AndroidLog.d(getInterfaceLoggingTag(), "SerialReaderCallback Received Data: (HEX) " + Utils.bytes2Hex(liveLogData));
        AndroidLog.d(getInterfaceLoggingTag(), "SerialReaderCallback Received Data: (TXT) " + Utils.bytes2Ascii(liveLogData));

        int loggingRespSize = ChameleonLogUtils.ResponseIsLiveLoggingBytes(liveLogData);
        if (loggingRespSize > 0) {
            AndroidLog.i(TAG, "Received new LogEntry @ " + String.format(BuildConfig.DEFAULT_LOCALE, "0x%02x", liveLogData[0]));
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
