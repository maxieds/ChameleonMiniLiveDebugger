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

public class BluetoothSerialInterface extends SerialIOReceiver {

    private static final String TAG = BluetoothSerialInterface.class.getSimpleName();

    public final String CHAMELEON_REVG_NAME = "BLE-Chameleon";

    public String getInterfaceLoggingTag() {
        return "SerialBTReader";
    }

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
        if(serialData.length < ChameleonLogUtils.LOGGING_MIN_DATA_BYTES + 4) {
            return false;
        }
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
        return STATUS_OK;
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
            return STATUS_TRUE;
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
        return STATUS_TRUE;
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
        ChameleonIO.APPEND_PRIOR_BUFFER_DATA = false;
        ChameleonIO.PRIOR_BUFFER_DATA = new byte[0];
        activeDevice = null;
        serialConfigured = false;
        receiversRegistered = false;
        notifyDeviceConnectionTerminated();
        return STATUS_TRUE;
    }

    private BluetoothSPP.OnDataReceivedListener createSerialReaderCallback() {
        return new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] liveLogData, String message) {
                Settings.serialIOPorts[Settings.BTIO_IFACE_INDEX].onReceivedData(liveLogData);
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
            return STATUS_FALSE;
        }
        else if(!serialConfigured()) {
            return STATUS_FALSE;
        }
        serialPort.send(dataWriteBuffer, false);
        return STATUS_TRUE;
    }

}
