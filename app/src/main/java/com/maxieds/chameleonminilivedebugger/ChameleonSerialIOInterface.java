package com.maxieds.chameleonminilivedebugger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public interface ChameleonSerialIOInterface {

    void setListenerContext(Context context);

    public final String SERIALIO_DEVICE_FOUND = "ChameleonSerialIOInterface.SERIALIO_DEVICE_FOUND";
    public final String SERIALIO_DEVICE_CONNECTION_LOST = "ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST";
    public final String SERIALIO_DATA_RECEIVED = "ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED";
    public final String SERIALIO_LOGDATA_RECEIVED = "ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED";
    public final String SERIALIO_NOTIFY_STATUS = "ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS";
    public final String SERIALIO_NOTIFY_BTDEV_CONNECTED = "ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED";

    boolean notifySerialDataReceived(byte[] serialData);
    boolean notifyLogDataReceived(byte[] serialData);
    boolean notifyDeviceFound();
    boolean notifyDeviceConnectionTerminated();
    boolean notifyStatus(String msgType, String statusMsg);

    boolean isWiredUSB();
    boolean isBluetooth();

    public final int HIGH_SPEED_BAUD_RATE = 256000;
    public final int LIMITED_SPEED_BAUD_RATE = 115200;
    public final int[] UART_BAUD_RATES = {
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
    int setSerialBuadRateLimited();

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

    int sendDataBuffer(byte[] dataWriteBuffer);

}