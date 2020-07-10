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

public class SerialIOReceiver implements ChameleonSerialIOInterface {

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

    public int setSerialBuadRateLimited() {
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

    private ArrayList<Byte[]> splitReceivedDataIntoLogs(byte[] liveLogData) {
        ArrayList<Byte[]> splitLogData = new ArrayList<Byte[]>();
        int logLength = liveLogData.length, indexPos = 0;
        while(indexPos < liveLogData.length) {
            int nextLogLength = ChameleonLogUtils.ResponseIsLiveLoggingBytes(liveLogData, indexPos, logLength);
            if(nextLogLength == 0) {
                continue;
            }
            if(nextLogLength + indexPos >= liveLogData.length) {
                nextLogLength = liveLogData.length - indexPos;
                if(nextLogLength == 0) {
                    break;
                }
            }
            byte[] nextLogBytes = new byte[nextLogLength];
            System.arraycopy(liveLogData, indexPos, nextLogBytes, 0, nextLogLength);
            splitLogData.add(ArrayUtils.toObject(nextLogBytes));
            logLength -= nextLogLength;
            indexPos += Math.max(1, nextLogLength);
        }
        if(logLength > 0) {
            byte[] nextLogBytes = new byte[logLength];
            System.arraycopy(liveLogData, liveLogData.length - logLength, nextLogBytes, 0, logLength);
            splitLogData.add(ArrayUtils.toObject(nextLogBytes));
        }
        return splitLogData;
    }

    public void onReceivedData(byte[] liveLogData) {

        Log.d(getInterfaceLoggingTag(), "SerialReaderCallback Received Data: (HEX) " + Utils.bytes2Hex(liveLogData));
        Log.d(getInterfaceLoggingTag(), "SerialReaderCallback Received Data: (TXT) " + Utils.bytes2Ascii(liveLogData));

        ArrayList<Byte[]> splitLogData = splitReceivedDataIntoLogs(liveLogData);
        for(int sidx = 0; sidx < splitLogData.size(); sidx++) {
            liveLogData = new byte[splitLogData.get(sidx).length];
            System.arraycopy(ArrayUtils.toPrimitive(splitLogData.get(sidx)), 0, liveLogData, 0, ArrayUtils.toPrimitive(splitLogData.get(sidx)).length);
            if (liveLogData.length == 0) {
                return;
            }
            int loggingRespSize = ChameleonLogUtils.ResponseIsLiveLoggingBytes(liveLogData);
            if (loggingRespSize > 0) {
                notifyLogDataReceived(liveLogData);
                continue;
            }
            if (ChameleonIO.PAUSED) {
                continue;
            } else if (ChameleonIO.DOWNLOAD) {
                ExportTools.performXModemSerialDownload(liveLogData);
                continue;
            } else if (ChameleonIO.UPLOAD) {
                ExportTools.performXModemSerialUpload(liveLogData);
                continue;
            } else if (ChameleonIO.WAITING_FOR_XMODEM) {
                String strLogData = new String(liveLogData);
                if (strLogData.length() >= 11 && strLogData.substring(0, 11).equals("110:WAITING")) {
                    ChameleonIO.WAITING_FOR_XMODEM = false;
                    continue;
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
                continue;
            }
            notifySerialDataReceived(liveLogData);
        }
    }

    public int sendDataBuffer(byte[] dataWriteBuffer) {
        return STATUS_OK;
    }

}
