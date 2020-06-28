package com.maxieds.chameleonminilivedebugger;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.felhr.usbserial.UsbSerialInterface;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class BluetoothSerialInterface implements ChameleonSerialIOInterface {

    private static final String TAG = BluetoothSerialInterface.class.getSimpleName();

    public final String CHAMELEON_REVG_NAME = "BLE-Chameleon";

    private Context notifyContext;
    private BluetoothSPP serialPort;
    private BluetoothDevice activeDevice;
    private int baudRate; // irrelevant?
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private final Semaphore serialPortLock = new Semaphore(1, true);

    public boolean isBluetoothAvailable() {
        if(serialPort == null) {
            return false;
        }
        return serialPort.isBluetoothAvailable();
    }

    public boolean isBluetoothEnabled() {
        if(serialPort == null) {
            return false;
        }
        return serialPort.isBluetoothEnabled();
    }

    public static void displayAndroidBluetoothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        LiveLoggerActivity.getInstance().startActivity(intentOpenBluetoothSettings);
    }

    public boolean configureSerialConnection(BluetoothDevice btDev) {
        if(!btDev.getName().equals(CHAMELEON_REVG_NAME)) {
            return false;
        }
        if(!receiversRegistered) {
            configureSerial();
        }
        activeDevice = btDev;
        serialPort.connect(btDev.getAddress());
        return true;
    }

    public BluetoothSerialInterface(Context appContext) {
        notifyContext = appContext;
        serialPort = new BluetoothSPP(notifyContext);
        serialPort.setDeviceTarget(false);
        activeDevice = null;
        baudRate = Settings.serialBaudRate;
        serialConfigured = false;
        receiversRegistered = false;
    }

    public void setListenerContext(Context context) {
        notifyContext = context;
    }

    public boolean notifySerialDataReceived(byte[] serialData) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED);
        notifyIntent.putExtra("DATA", serialData);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyLogDataReceived(byte[] serialData) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED);
        notifyIntent.putExtra("DATA", serialData);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyDeviceFound() {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DEVICE_FOUND);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyDeviceConnectionTerminated() {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyStatus(String msgType, String statusMsg) {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS);
        notifyIntent.putExtra("STATUS-TYPE", msgType);
        notifyIntent.putExtra("STATUS-MSG", statusMsg);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyBluetoothChameleonDeviceConnected() {
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean isWiredUSB() { return false; }

    public boolean isBluetooth() { return true; }

    public int setSerialBaudRate(int brate) {
        baudRate = brate;
        Settings.serialBaudRate = baudRate;
        if(serialPort != null) {
            return baudRate;
        }
        return 0;
    }
    public int setSerialBaudRateHigh() {
        return setSerialBaudRate(ChameleonSerialIOInterface.HIGH_SPEED_BAUD_RATE);
    }
    public int setSerialBuadRateLimited() {
        return setSerialBaudRate(ChameleonSerialIOInterface.LIMITED_SPEED_BAUD_RATE);
    }

    public boolean startScanningDevices() {
        configureSerial();
        serialPort.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                if(name.equals(CHAMELEON_REVG_NAME)) {
                    serialConfigured = true;
                    stopScanningDevices();
                    Settings.chameleonDeviceSerialNumber = address;
                    Settings.SERIALIO_IFACE_ACTIVE_INDEX = Settings.BTIO_IFACE_INDEX;
                    LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconBT, R.drawable.bluetooth16);
                    ChameleonIO.PAUSED = false;
                    notifyBluetoothChameleonDeviceConnected();
                    notifyStatus("USB STATUS: ", "Successfully configured the device in passive logging mode...\n" + getActiveDeviceInfo());
                }
            }
            public void onAutoConnectionStarted() {}
        });
        serialPort.autoConnect(CHAMELEON_REVG_NAME);
        return true;
    }

    public boolean stopScanningDevices() {
        serialPort.stopAutoConnect();
        serialPort.cancelDiscovery();
        return true;
    }

    public String getActiveDeviceInfo() {
        if(activeDevice == null) {
            return "";
        }
        String devInfo = String.format(Locale.ENGLISH, "BT Class: %s\nBond State: %s\nProduct Name: %s\nType: %s\nDevice Address: %s",
                activeDevice.getBluetoothClass(), activeDevice.getBondState(),
                activeDevice.getName(), activeDevice.getType(),
                activeDevice.getAddress());;
        return devInfo;
    }

    public int configureSerial() {
        if(serialConfigured() || serialPort.isAutoConnecting()) {
            return 1;
        }
        serialPort.startService(BluetoothState.DEVICE_OTHER);
        serialPort.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                if(name.equals(CHAMELEON_REVG_NAME)) {
                    serialConfigured = true;
                    stopScanningDevices();
                    Settings.chameleonDeviceSerialNumber = address;
                    Settings.SERIALIO_IFACE_ACTIVE_INDEX = Settings.BTIO_IFACE_INDEX;
                    LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconBT, R.drawable.bluetooth16);
                    ChameleonIO.PAUSED = false;
                    notifyBluetoothChameleonDeviceConnected();
                    notifyStatus("USB STATUS: ", "Successfully configured the device in passive logging mode...\n" + getActiveDeviceInfo());
                }
            }
            public void onDeviceDisconnected() {
                shutdownSerial();
                notifyDeviceConnectionTerminated();
            }
            public void onDeviceConnectionFailed() {
                shutdownSerial();
                notifyDeviceConnectionTerminated();
            }
        });
        receiversRegistered = true;
        return 1;
    }

    public int shutdownSerial() {
        if(serialPort != null) {
            serialPort.stopAutoConnect();
            serialPort.cancelDiscovery();
            serialPort.disconnect();
            serialPort.stopService();
        }
        ChameleonIO.PAUSED = true;
        ExportTools.EOT = true;
        ExportTools.transmissionErrorOccurred = true;
        ChameleonIO.DOWNLOAD = false;
        ChameleonIO.UPLOAD = false;
        ChameleonIO.WAITING_FOR_XMODEM = false;
        ChameleonIO.WAITING_FOR_RESPONSE = false;
        ChameleonIO.EXPECTING_BINARY_DATA = false;
        ChameleonIO.LASTCMD = "";
        activeDevice = null;
        serialConfigured = false;
        receiversRegistered = false;
        notifyDeviceConnectionTerminated();
        return 1;
    }

    private BluetoothSPP.OnDataReceivedListener createSerialReaderCallback() {
        return new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] liveLogData, String message) {
                Log.d(TAG, "BTReaderCallback Received Data: (HEX) " + Utils.bytes2Hex(liveLogData));
                Log.d(TAG, "BTReaderCallback Received Data: (TXT) " + Utils.bytes2Ascii(liveLogData));
                Log.d(TAG, "BTReaderCallback Received Data: (MSG) " + message);
                int loggingRespSize = ChameleonLogUtils.ResponseIsLiveLoggingBytes(liveLogData);
                if(loggingRespSize > 0) {
                    if(loggingRespSize == liveLogData.length) {
                        notifyLogDataReceived(liveLogData);
                        return;
                    }
                    else {
                        notifyLogDataReceived(Arrays.copyOfRange(liveLogData, 0, loggingRespSize));
                        liveLogData = Arrays.copyOfRange(liveLogData, loggingRespSize, liveLogData.length);
                    }
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
                } else if (ChameleonIO.WAITING_FOR_RESPONSE && ChameleonIO.isCommandResponse(liveLogData)) {
                    String[] strLogData = (new String(liveLogData)).split("[\n\r\t]+");
                    ChameleonIO.DEVICE_RESPONSE_CODE = strLogData[0];
                    if (strLogData.length >= 2)
                        ChameleonIO.DEVICE_RESPONSE = Arrays.copyOfRange(strLogData, 1, strLogData.length);
                    else
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
        };
    }

    public boolean serialConfigured() { return serialConfigured; }

    public boolean serialReceiversRegistered() { return receiversRegistered; }

    public boolean acquireSerialPort() {
        try {
            serialPortLock.acquire();
            return true;
        } catch(Exception inte) {
            inte.printStackTrace();
            serialPortLock.release();
            return false;
        }
    }

    public boolean acquireSerialPortNoInterrupt() {
        try {
            serialPortLock.acquireUninterruptibly();
            return true;
        } catch(Exception inte) {
            inte.printStackTrace();
            serialPortLock.release();
            return false;
        }
    }

    public boolean tryAcquireSerialPort(int timeout) {
        boolean status = false;
        try {
            status = serialPortLock.tryAcquire(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
            return status;
        } catch(Exception inte) {
            inte.printStackTrace();
            serialPortLock.release();
            return false;
        }
    }

    public boolean releaseSerialPortLock() {
        serialPortLock.release();
        return true;
    }

    public int sendDataBuffer(byte[] dataWriteBuffer) {
        if(dataWriteBuffer == null || dataWriteBuffer.length == 0) {
            return 0;
        }
        else if(!serialConfigured()) {
            return 0;
        }
        serialPort.send(dataWriteBuffer, false);
        return 1;
    }

}
