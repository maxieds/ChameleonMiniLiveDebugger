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

    public void setListenerContext(Context context);

    public final String SERIALIO_DEVICE_FOUND = "ChameleonSerialIOInterface.SERIALIO_DEVICE_FOUND";
    public final String SERIALIO_DEVICE_CONNECTION_LOST = "ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST";
    public final String SERIALIO_DATA_RECEIVED = "ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED";
    public final String SERIALIO_LOGDATA_RECEIVED = "ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED";
    public final String SERIALIO_NOTIFY_STATUS = "ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS";
    public final String SERIALIO_NOTIFY_BTDEV_CONNECTED = "ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED";

    public static final int STATUS_ERROR = -1;
    public static final int STATUS_OK = 0;
    public static final int STATUS_TRUE = 1;
    public static final int STATUS_FALSE = 0;

    public String getInterfaceLoggingTag();

    public boolean notifySerialDataReceived(byte[] serialData);
    public boolean notifyLogDataReceived(byte[] serialData);
    public boolean notifyDeviceFound();
    public boolean notifyDeviceConnectionTerminated();
    public boolean notifyStatus(String msgType, String statusMsg);

    public boolean isWiredUSB();
    public boolean isBluetooth();

    public final int HIGH_SPEED_BAUD_RATE = 256000;
    public final int LIMITED_SPEED_BAUD_RATE = 115200;
    public final Integer[] UART_BAUD_RATES = {
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

    public int setSerialBaudRate(int baudRate);
    public int setSerialBaudRateHigh();
    public int setSerialBaudRateLimited();

    public boolean startScanningDevices();
    public boolean stopScanningDevices();
    public String getActiveDeviceInfo();

    public int configureSerial();
    public int shutdownSerial();
    public boolean serialConfigured();
    public boolean serialReceiversRegistered();

    public boolean acquireSerialPort();
    public boolean acquireSerialPortNoInterrupt();
    public boolean tryAcquireSerialPort(int timeout);
    public boolean releaseSerialPortLock();

    public void onReceivedData(byte[] liveLogData);

    public int sendDataBuffer(byte[] dataWriteBuffer);

}