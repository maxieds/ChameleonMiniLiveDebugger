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
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class BluetoothBLEInterface extends SerialIOReceiver {

    /* TODO: Check XModem functionality with the BT devices */

    private static final String TAG = BluetoothBLEInterface.class.getSimpleName();

    public static final int ACTVITY_REQUEST_BLUETOOTH_ENABLED_CODE = 0x00B1;
    public static final int ACTVITY_REQUEST_BLUETOOTH_DISCOVERABLE_CODE = 0x00B1;

    public String getInterfaceLoggingTag() {
        return TAG;
    }

    private BluetoothDevice activeDevice;
    private BluetoothGattConnector btGattConnectorBLEDevice;
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private boolean scanning;
    private Semaphore btDevLock = new Semaphore(1, true);

    @SuppressLint("MissingPermission")
    public boolean isBluetoothEnabled(boolean startActivityIfNot) {
        LiveLoggerActivity mainActivityCtx = LiveLoggerActivity.getLiveLoggerInstance();
        BluetoothAdapter btAdapter = null;
        BluetoothAdapter btAdapterDefault = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager btManager = (BluetoothManager) mainActivityCtx.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapterFromService = btManager != null ? btManager.getAdapter() : null;
        if (btAdapterFromService != null) {
            btAdapter = btAdapterFromService;
        } else if (btAdapterDefault != null) {
            btAdapter = btAdapterDefault;
        }
        boolean status = true;
        if (btAdapter == null) {
            return false;
        }
        ChameleonSettings.disableBTAdapter = !btAdapter.isEnabled();
        if (!btAdapter.isEnabled() && !btAdapter.enable()) {
            if (startActivityIfNot) {
                try {
                    Intent turnBTOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    mainActivityCtx.startActivityForResult(turnBTOn, ACTVITY_REQUEST_BLUETOOTH_ENABLED_CODE);
                } catch (Exception excpt) {
                    AndroidLog.printStackTrace(excpt);
                }
            }
            status = false;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(LiveLoggerActivity.getLiveLoggerInstance(), "android.permission.BLUETOOTH_ADVERTISE") != PackageManager.PERMISSION_GRANTED) {
                if (startActivityIfNot) {
                    Intent btMakeDiscIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    btMakeDiscIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    mainActivityCtx.startActivityForResult(btMakeDiscIntent, ACTVITY_REQUEST_BLUETOOTH_DISCOVERABLE_CODE);
                }
                return false;
            }
        }
        return status;
    }

    /**
     * The developer does not like leaving Bluetooth enabled on the Droid device when it is
     * unnecessary. These methods are called to disable the Bluetooth adapter after CMLD closes
     * if BT was enabled on the system by CMLD to facilitate handling Chameleon BLE connections.
     */
    public static void resetBluetoothAdapterAtClose(ChameleonMiniLiveDebuggerActivity mainActivityCtx) {
        resetBluetoothAdapter(mainActivityCtx, false);
    }

    public static void resetBluetoothAdapterAtStart(ChameleonMiniLiveDebuggerActivity mainActivityCtx) {
        resetBluetoothAdapter(mainActivityCtx, true);
    }

    private static void resetBluetoothAdapter(ChameleonMiniLiveDebuggerActivity mainActivityCtx, boolean action) {
        if (!ChameleonSettings.disableBTAdapter) {
            return;
        }
        BluetoothAdapter btAdapter = null;
        BluetoothAdapter btAdapterDefault = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager btManager = (BluetoothManager) mainActivityCtx.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapterFromService = btManager != null ? btManager.getAdapter() : null;
        if (btAdapterFromService != null) {
            btAdapter = btAdapterFromService;
        } else if (btAdapterDefault != null) {
            btAdapter = btAdapterDefault;
        }
        if (btAdapter != null) {
            try {
                if (action && !btAdapter.isEnabled()) {
                    btAdapter.enable();
                } else if (!action && btAdapter.isEnabled()) {
                    btAdapter.disable();
                }
            } catch (SecurityException se) {
                AndroidLog.printStackTrace(se);
            }
        }
    }

    public boolean isBluetoothEnabled() {
        return isBluetoothEnabled(false);
    }

    public static void displayAndroidBluetoothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        LiveLoggerActivity.getLiveLoggerInstance().startActivity(intentOpenBluetoothSettings);
    }

    public static void displayAndroidBluetoothTroubleshooting() {

        LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(llActivity, R.style.SpinnerTheme);
        WebView wv = new WebView(llActivity);

        String dialogMainPointsHTML = llActivity.getString(R.string.bluetoothTroubleshootingInstructionsHTML);

        wv.loadDataWithBaseURL(null, dialogMainPointsHTML, "text/html", "UTF-8", "");
        wv.getSettings().setJavaScriptEnabled(false);
        wv.setBackgroundColor(ThemesConfiguration.getThemeColorVariant(R.attr.colorAccentHighlight));
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        wv.setInitialScale(10);

        adBuilder.setCancelable(true);
        adBuilder.setTitle("Bluetooth Troubleshooting / Tips:");
        adBuilder.setPositiveButton(
                "Back to Previous",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        adBuilder.setView(wv);
        adBuilder.setInverseBackgroundForced(true);

        AlertDialog alertDialog = adBuilder.create();
        alertDialog.show();

    }

    public boolean isWiredUSB() { return false; }

    public boolean isBluetooth() { return true; }

    public int setSerialBaudRate(int bdRate) {
        AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "Attempt to set serial baud rate to %d on a BT connection"));
        return STATUS_NOT_SUPPORTED;
    }

    public boolean isDeviceConnected() {
        return btGattConnectorBLEDevice != null && btGattConnectorBLEDevice.isDeviceConnected();
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

    public BluetoothGattConnector getBluetoothGattConnector() {
        return btGattConnectorBLEDevice;
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
        activeDevice  = btDev;
        ChameleonIO.REVE_BOARD = false;
        ChameleonIO.PAUSED = false;
        ChameleonSettings.chameleonDeviceSerialNumber = ChameleonSettings.CMINI_DEVICE_FIELD_NONE;
        ChameleonSettings.chameleonDeviceAddress = btDev.getAddress();
        ChameleonIO.CHAMELEON_MINI_BOARD_TYPE = btGattConnectorBLEDevice.getChameleonDeviceType();

        Handler configDeviceHandler = new Handler();
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
            return false;
        }
    }

    public boolean tryAcquireSerialPort(int timeout) {
        try {
            return btDevLock.tryAcquire(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch(Exception ie) {
            AndroidLog.printStackTrace(ie);
            btDevLock.release();
            return false;
        }
    }

    public boolean releaseSerialPortLock() {
        btDevLock.release();
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
