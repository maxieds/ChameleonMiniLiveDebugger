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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class BluetoothBLEInterface extends SerialIOReceiver {

    /** TODO: Check XModem functionality with the BT devices ??? */

    private static final String TAG = BluetoothBLEInterface.class.getSimpleName();

    public String getInterfaceLoggingTag() {
        return TAG;
    }

    private BluetoothDevice activeDevice;
    private BluetoothGattConnector btGattConnectorBLEDevice;
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private boolean scanning;
    private Semaphore btDevLock = new Semaphore(1, true);

    public boolean isWiredUSB() { return false; }

    public boolean isBluetooth() { return true; }

    public int setSerialBaudRate(int bdRate) {
        AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "Attempt to set serial baud rate to %d on a BT connection"));
        return STATUS_NOT_SUPPORTED;
    }

    @SuppressLint("MissingPermission")
    public String getDeviceName() {
        final String unknownBTDevName = "<UNKNOWN-BTDEV-NAME>";
        try {
            return activeDevice != null ? activeDevice.getName() : unknownBTDevName;
        } catch (SecurityException se) {
            AndroidLog.printStackTrace(se);
            return unknownBTDevName;
        }
    }

    @SuppressLint("MissingPermission")
    public String getActiveDeviceInfo() {
        if(activeDevice == null) {
            return "<null-device>: No information available.";
        }
        String devInfo = "<Device-Info-Unavailable>";
        try {
            devInfo = String.format(BuildConfig.DEFAULT_LOCALE, "BT Class: %s\nBond State: %s\nProduct Name: %s\nType: %s\nDevice Address: %s",
                    activeDevice.getBluetoothClass(), activeDevice.getBondState(),
                    activeDevice.getName(), activeDevice.getType(),
                    activeDevice.getAddress());
        } catch (SecurityException se) {
            AndroidLog.printStackTrace(se);
            devInfo = "<Device-Info-Unavailable>";
        }
        return devInfo;
    }

    public BluetoothBLEInterface(Context appContext) {
        setListenerContext(appContext);
        activeDevice = null;
        btGattConnectorBLEDevice = new BluetoothGattConnector(appContext);
        btGattConnectorBLEDevice.setBluetoothSerialInterface(this);
        serialConfigured = false;
        receiversRegistered = false;
        scanning = false;
    }

    public void broadcastIntent(@NonNull Intent bcIntent) {
        if (btGattConnectorBLEDevice != null) {
            btGattConnectorBLEDevice.receiveBroadcastIntent(bcIntent);
        }
    }

    @SuppressLint("MissingPermission")
    public boolean configureSerialConnection(BluetoothDevice btDev) {
        if (btDev == null) {
            return false;
        } else if (!receiversRegistered) {
            configureSerial();
        }
        Utils.clearToastMessage();
        activeDevice  = btDev;
        ChameleonIO.REVE_BOARD = false;
        ChameleonIO.PAUSED = false;
        ChameleonSettings.chameleonDeviceSerialNumber = ChameleonSettings.CMINI_DEVICE_FIELD_NONE;
        ChameleonSettings.chameleonDeviceAddress = btDev.getAddress();
        ChameleonIO.CHAMELEON_MINI_BOARD_TYPE = BluetoothUtils.getChameleonDeviceType(btDev.getName());
        Handler configDeviceHandler = new Handler(Looper.getMainLooper());
        Runnable configDeviceRunnable = new Runnable() {
            public void run() {
                AndroidLog.i(TAG, ChameleonSettings.getActiveSerialIOPort().toString());
                if(ChameleonSettings.getActiveSerialIOPort() != null && btGattConnectorBLEDevice.isDeviceConnected()) {
                    configDeviceHandler.removeCallbacks(this);
                    LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
                    llActivity.reconfigureSerialIODevices();
                    llActivity.setStatusIcon(R.id.statusIconBT, R.drawable.bluetooth16);
                    Utils.displayToastMessageShort(String.format(BuildConfig.DEFAULT_LOCALE, "New Bluetooth BLE device connection:\n%s @ %s\n%s", btDev.getName(), ChameleonSettings.chameleonDeviceAddress, ChameleonIO.getDeviceDescription(ChameleonIO.CHAMELEON_MINI_BOARD_TYPE)));
                    UITabUtils.updateConfigTabConnDeviceInfo(false);
                    notifyStatus("BLUETOOTH STATUS: ", "Chameleon:     " + getActiveDeviceInfo());
                    /**
                     * TODO: Extract more manufacturer information about the BT device using these commands and
                     *       the byte order for which device properties are mapped onto which bytes:
                     *       https://github.com/RfidResearchGroup/ChameleonBLEAPI/blob/master/appmain/devices/BleCMDControl.java#L52
                     */
                }
                else {
                    AndroidLog.i(TAG, "BLE device __NOT__ connected! ... Looping");
                    configDeviceHandler.postDelayed(this, ChameleonIO.TIMEOUT);
                }
            }
        };
        ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = ChameleonSettings.BTIO_IFACE_INDEX;
        ChameleonSettings.stopSerialIOConnectionDiscovery();
        ChameleonIO.DeviceStatusSettings.stopPostingStats();
        serialConfigured = true;
        configDeviceHandler.postDelayed(configDeviceRunnable, 500);
        return true;
    }

    public boolean startScanningDevices() {
        if (scanning) {
            return false;
        }
        scanning = true;
        configureSerial();
        btGattConnectorBLEDevice.startConnectingDevices();
        return true;
    }

    public boolean stopScanningDevices() {
        scanning = false;
        btGattConnectorBLEDevice.stopConnectingDevices();
        return true;
    }

    public int configureSerial() {
        if(serialConfigured()) {
            return STATUS_TRUE;
        }
        receiversRegistered = true;
        return STATUS_TRUE;
    }

    public int shutdownSerial() {
        ChameleonIO.DeviceStatusSettings.stopPostingStats();
        if(btGattConnectorBLEDevice != null && btGattConnectorBLEDevice.isDeviceConnected()) {
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
        if(ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX == ChameleonSettings.BTIO_IFACE_INDEX) {
            ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
        }
        btDevLock.release();
        LiveLoggerActivity.getLiveLoggerInstance().clearStatusIcon(R.id.statusIconBT);
        UITabUtils.updateConfigTabConnDeviceInfo(true);
        notifyDeviceConnectionTerminated();
        return STATUS_TRUE;
    }

    public boolean serialConfigured() { return serialConfigured; }

    public boolean serialReceiversRegistered() { return receiversRegistered; }

    public boolean acquireSerialPort() {
        try {
            btDevLock.acquire();
            return true;
        } catch(Exception inte) {
            AndroidLog.printStackTrace(inte);
            btGattConnectorBLEDevice.releaseAllLocks();
            btDevLock.release();
            return false;
        }
    }

    public boolean acquireSerialPortNoInterrupt() {
        try {
            btDevLock.acquireUninterruptibly();
            return true;
        } catch(Exception inte) {
            AndroidLog.printStackTrace(inte);
            btDevLock.release();
            btGattConnectorBLEDevice.releaseAllLocks();
            return false;
        }
    }

    public boolean tryAcquireSerialPort(int timeout) {
        try {
            return btDevLock.tryAcquire(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch(Exception ie) {
            AndroidLog.printStackTrace(ie);
            btDevLock.release();
            btGattConnectorBLEDevice.releaseAllLocks();
            return false;
        }
    }

    public boolean releaseSerialPortLock() {
        btDevLock.release();
        btGattConnectorBLEDevice.releaseAllLocks();
        return true;
    }

    public int sendDataBuffer(byte[] dataWriteBuffer) {
        AndroidLog.i(TAG, "write: " + Utils.bytes2Hex(dataWriteBuffer));
        if(dataWriteBuffer == null || dataWriteBuffer.length == 0) {
            return STATUS_FALSE;
        } else if(!serialConfigured()) {
            return STATUS_FALSE;
        }
        try {
            if (btGattConnectorBLEDevice.write(dataWriteBuffer) != STATUS_OK) {
                return STATUS_FALSE;
            } else if (btGattConnectorBLEDevice.read() != STATUS_OK) {
                return STATUS_FALSE;
            }
        } catch(IOException ioe) {
            AndroidLog.printStackTrace(ioe);
        }
        return STATUS_TRUE;
    }

}
