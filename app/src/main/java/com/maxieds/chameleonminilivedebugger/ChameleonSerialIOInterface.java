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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Arrays;

public interface ChameleonSerialIOInterface {

    void setListenerContext(Context context);

    String SERIALIO_DEVICE_FOUND = "ChameleonSerialIOInterface.SERIALIO_DEVICE_FOUND";
    String SERIALIO_DEVICE_CONNECTION_LOST = "ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST";
    String SERIALIO_DATA_RECEIVED = "ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED";
    String SERIALIO_LOGDATA_RECEIVED = "ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED";
    String SERIALIO_NOTIFY_STATUS = "ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS";
    String SERIALIO_NOTIFY_BTDEV_CONNECTED = "ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED";

    int STATUS_ERROR = -1;
    int STATUS_OK = 0;
    int STATUS_TRUE = 1;
    int STATUS_FALSE = 0;

    String getInterfaceLoggingTag();

    boolean notifySerialDataReceived(byte[] serialData);
    boolean notifyLogDataReceived(byte[] serialData);
    boolean notifyDeviceFound();
    boolean notifyDeviceConnectionTerminated();
    boolean notifyStatus(String msgType, String statusMsg);

    boolean isWiredUSB();
    boolean isBluetooth();

    int HIGH_SPEED_BAUD_RATE = 256000;
    int LIMITED_SPEED_BAUD_RATE = 115200;
    Integer[] UART_BAUD_RATES = {
            50,
            75,
            110,
            134,
            150,
            200,
            300,
            600,
            1200,
            1800,
            2400,
            4800,
            9600,
            19200,
            38400,
            57600,
            115200,
            230400,
            460800,
            921600
    };

    int setSerialBaudRate(int baudRate);
    int setSerialBaudRateHigh();
    int setSerialBaudRateLimited();

    boolean startScanningDevices();
    boolean stopScanningDevices();
    String getActiveDeviceInfo();

    int configureSerial();
    int shutdownSerial();
    boolean serialConfigured();
    boolean serialReceiversRegistered();

    boolean acquireSerialPort();
    boolean acquireSerialPortNoInterrupt();
    boolean tryAcquireSerialPort(int timeout);
    boolean releaseSerialPortLock();

    interface SerialDataReceiverInterface {
        void onReceivedData(byte[] liveLogData);
    }

    int sendDataBuffer(byte[] dataWriteBuffer);

}