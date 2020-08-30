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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_SLOTS;

public class BluetoothSerialInterface extends SerialIOReceiver {

    /**
     * TODO: Check XModem functionality with the BT devices ...
     */

    private static final String TAG = BluetoothSerialInterface.class.getSimpleName();

    public String getInterfaceLoggingTag() {
        return "SerialBTReader";
    }

    private Context notifyContext;
    private BluetoothDevice activeDevice;
    private BluetoothGattConnector btGattConnectorBLEDevice;
    private int baudRate; // irrelevant?
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private final Semaphore serialPortLock = new Semaphore(1, true);

    public boolean isBluetoothEnabled() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            return false;
        }
        return btAdapter.isEnabled();
    }

    public static void displayAndroidBluetoothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        LiveLoggerActivity.getInstance().startActivity(intentOpenBluetoothSettings);
    }

    public boolean configureSerialConnection(BluetoothDevice btDev) {
        if(btDev == null) {
            return false;
        }
        if(!receiversRegistered) {
            configureSerial();
        }
        activeDevice = btDev;
        Log.i(TAG, "BTDEV: " + activeDevice.toString());
        ChameleonIO.REVE_BOARD = false;
        ChameleonIO.PAUSED = false;
        Settings.chameleonDeviceSerialNumber = btDev.getAddress();

        Handler configDeviceHandler = new Handler();
        Runnable configDeviceRunnable = new Runnable() {
            public void run() {
                Log.i(TAG, Settings.getActiveSerialIOPort().toString());
                if(Settings.getActiveSerialIOPort() != null && btGattConnectorBLEDevice.isDeviceConnected()) {
                    configDeviceHandler.removeCallbacks(this);
                    //ChameleonIO.detectChameleonType();
                    //ChameleonIO.initializeDevice();
                    //UITabUtils.initializeToolsTab(TAB_TOOLS_MITEM_SLOTS, TabFragment.UITAB_DATA[TAB_TOOLS].tabInflatedView);
                    //ChameleonPeripherals.actionButtonRestorePeripheralDefaults(null);
                    ChameleonIO.DeviceStatusSettings.startPostingStats(0);
                    LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconBT, R.drawable.bluetooth16);
                }
                else {
                    Log.i(TAG, "BLE device __NOT__ connected! ... Looping");
                    configDeviceHandler.postDelayed(this, 1000);
                }
            }
        };
        Settings.SERIALIO_IFACE_ACTIVE_INDEX = Settings.BTIO_IFACE_INDEX;
        Settings.stopSerialIOConnectionDiscovery();
        ChameleonIO.DeviceStatusSettings.stopPostingStats();
        serialConfigured = true;
        configDeviceHandler.postDelayed(configDeviceRunnable, 500);

        return true;
    }

    public String getDeviceName() {
        return activeDevice != null ? activeDevice.getName() : "<UNKNOWN-BTDEV-NAME>";
    }

    public BluetoothSerialInterface(Context appContext) {
        notifyContext = appContext;
        activeDevice = null;
        btGattConnectorBLEDevice = new BluetoothGattConnector(notifyContext);
        btGattConnectorBLEDevice.setBluetoothSerialInterface(this);
        baudRate = Settings.serialBaudRate;
        serialConfigured = false;
        receiversRegistered = false;
    }

    public void setListenerContext(Context context) {
        notifyContext = context;
    }

    public boolean notifySerialDataReceived(byte[] serialData) {
        Log.d(TAG, "BTReaderCallback Serial Data: (HEX) " + Utils.bytes2Hex(serialData));
        Log.d(TAG, "BTReaderCallback Serial Data: (TXT) " + Utils.bytes2Ascii(serialData));
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED);
        notifyIntent.putExtra("DATA", serialData);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyLogDataReceived(byte[] serialData) {
        Log.d(TAG, "BTReaderCallback Log Data: (HEX) " + Utils.bytes2Hex(serialData));
        Log.d(TAG, "BTReaderCallback Log Data: (TXT) " + Utils.bytes2Ascii(serialData));
        if(serialData.length < ChameleonLogUtils.LOGGING_MIN_DATA_BYTES + 4) {
            return false;
        }
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED);
        notifyIntent.putExtra("DATA", serialData);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyDeviceFound() {
        Log.i(TAG, "notifyDeviceFound");
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DEVICE_FOUND);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyDeviceConnectionTerminated() {
        Log.i(TAG, "notifyDeviceConnectionTerminated");
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyStatus(String msgType, String statusMsg) {
        Log.i(TAG, "notifyStatus: " + msgType + ": " + statusMsg);
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS);
        notifyIntent.putExtra("STATUS-TYPE", msgType);
        notifyIntent.putExtra("STATUS-MSG", statusMsg);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean notifyBluetoothChameleonDeviceConnected() {
        Log.i(TAG, "notifyBluetoothChameleonDeviceConnected");
        Intent notifyIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        notifyContext.sendBroadcast(notifyIntent);
        return true;
    }

    public boolean isWiredUSB() { return false; }

    public boolean isBluetooth() { return true; }

    public int setSerialBaudRate(int brate) {
        baudRate = brate;
        Settings.serialBaudRate = baudRate;
        return baudRate;
    }
    public int setSerialBaudRateHigh() {
        return setSerialBaudRate(ChameleonSerialIOInterface.HIGH_SPEED_BAUD_RATE);
    }
    public int setSerialBaudRateLimited() {
        return setSerialBaudRate(ChameleonSerialIOInterface.LIMITED_SPEED_BAUD_RATE);
    }

    public boolean isDeviceConnected() {
        return btGattConnectorBLEDevice != null && btGattConnectorBLEDevice.isDeviceConnected();
    }

    public BluetoothGattConnector getBluetoothGattConnector() {
        return btGattConnectorBLEDevice;
    }

    public boolean startScanningDevices() {
        configureSerial();
        btGattConnectorBLEDevice.startConnectingDevices();
        return true;
    }

    public boolean stopScanningDevices() {
        btGattConnectorBLEDevice.stopConnectingDevices();
        return true;
    }

    public String getActiveDeviceInfo() {
        if(activeDevice == null) {
            return "<null-device>: No information available.";
        }
        String devInfo = String.format(Locale.ENGLISH, "BT Class: %s\nBond State: %s\nProduct Name: %s\nType: %s\nDevice Address: %s",
                activeDevice.getBluetoothClass(), activeDevice.getBondState(),
                activeDevice.getName(), activeDevice.getType(),
                activeDevice.getAddress());;
        return devInfo;
    }

    public int configureSerial() {
        if(serialConfigured()) {
            return STATUS_TRUE;
        }
        receiversRegistered = true;
        return STATUS_TRUE;
    }

    public int shutdownSerial() {
        if(btGattConnectorBLEDevice != null) {
            btGattConnectorBLEDevice.disconnectDevice();
        }
        stopScanningDevices();
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
        Log.i(TAG, "write: " + Utils.bytes2Hex(dataWriteBuffer));
        if(dataWriteBuffer == null || dataWriteBuffer.length == 0) {
            return STATUS_FALSE;
        }
        else if(!serialConfigured()) {
            return STATUS_FALSE;
        }
        try {
            btGattConnectorBLEDevice.write(dataWriteBuffer);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return STATUS_TRUE;
    }

}
