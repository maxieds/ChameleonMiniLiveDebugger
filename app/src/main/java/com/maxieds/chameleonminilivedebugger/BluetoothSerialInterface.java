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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class BluetoothSerialInterface extends SerialIOReceiver {

    /**
     * TODO: More documentation at:
     *       https://punchthrough.com/android-ble-guide/
     * TODO: Check XModem functionality with the BT devices ...
     * TODO: On connect, update the device serial HW/MAC ...
     */

    private static final String TAG = BluetoothSerialInterface.class.getSimpleName();

    public enum ChameleonBluetoothDeviceState {

        BTDEV_STATE_ERROR(0),
        BTDEV_STATE_DISABLED_OFF(1),
        BTDEV_STATE_DISCONNECTED(2),
        BTDEV_STATE_IDLE(3),
        BTDEV_STATE_AVAILABLE_INIT(4),
        BTDEV_STATE_AVAILABLE_CONNECTING_ABTN(5),
        BTDEV_STATE_AVAILABLE_CONNECTING_BLE(6),
        BTDEV_STATE_ACTIVE(7),
        BTDEV_STATE_HALTING(8);

        public static final int BTDEV_STATE_COUNT = ChameleonBluetoothDeviceState.values().length;

        public static final Map<Integer, ChameleonBluetoothDeviceState> BTDEV_STATE_MAP = new HashMap<>();
        static {
            for (BluetoothSerialInterface.ChameleonBluetoothDeviceState btDevState : values()) {
                Integer levelOrdering = Integer.valueOf(btDevState.getStateLevelOrdering());
                BTDEV_STATE_MAP.put(levelOrdering, btDevState);
            }
        }

        private int stateLevel;
        ChameleonBluetoothDeviceState(int level) {
            stateLevel = level;
        }

        public int getStateLevelOrdering() {
            return this.stateLevel;
        }

        public static ChameleonBluetoothDeviceState getStateFromLevelOrdering(int inputLevel) {
            inputLevel = inputLevel % ChameleonBluetoothDeviceState.BTDEV_STATE_COUNT;
            if(inputLevel < 0 || inputLevel >= ChameleonBluetoothDeviceState.BTDEV_STATE_COUNT) {
                return null;
            }
            int inputLevelKey = Integer.valueOf(inputLevel);
            return BTDEV_STATE_MAP.get(inputLevelKey);
        }

        public boolean isBeforeNextStateInSequence(ChameleonBluetoothDeviceState nextState) {
            int readyStateLevel = 0;
            int haltStateLevel = Math.max(0, ChameleonBluetoothDeviceState.BTDEV_STATE_COUNT - 1);
            int curStateLevel = getStateLevelOrdering();
            int nextStateLevel = nextState.getStateLevelOrdering();
            if(nextStateLevel == haltStateLevel && curStateLevel == readyStateLevel) {
                return true;
            } else if(nextStateLevel >= haltStateLevel) {
                return false;
            } else if(curStateLevel >= nextStateLevel) {
                return false;
            } else if(nextStateLevel > curStateLevel) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isAfterNextStateInSequence(ChameleonBluetoothDeviceState nextState) {
            return getStateLevelOrdering() != nextState.getStateLevelOrdering() && !isBeforeNextStateInSequence(nextState);
        }

        public boolean isEqualInSequence(ChameleonBluetoothDeviceState nextState) {
            return getStateLevelOrdering() != nextState.getStateLevelOrdering();
        }

    }

    public static final int ACTVITY_REQUEST_BLUETOOTH_ENABLED_CODE = 0x00B1;
    public static final int ACTVITY_REQUEST_BLUETOOTH_DISCOVERABLE_CODE = 0x00B1;

    public String getInterfaceLoggingTag() {
        return "SerialBTReaderInterface";
    }

    private Context notifyContext;
    private BluetoothDevice activeDevice;
    private BluetoothSerialInterface.ChameleonBluetoothDeviceState btDeviceState;
    private BluetoothGattConnector btGattConnectorBLEDevice;
    private int baudRate;
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private final Semaphore serialPortLock = new Semaphore(1, true);

    public boolean isBluetoothEnabled(boolean startActivityIfNot) {
        LiveLoggerActivity mainActivityCtx = LiveLoggerActivity.getLiveLoggerInstance();
        if(!mainActivityCtx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            return false;
        } else if(!mainActivityCtx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        BluetoothAdapter btAdapter = null;
        BluetoothAdapter btAdapterDefault = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager btManager = (BluetoothManager) mainActivityCtx.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapterFromService = btManager != null ? btManager.getAdapter() : null;
        if(btAdapterFromService != null) {
            btAdapter = btAdapterFromService;
        } else if(btAdapterDefault != null) {
            btAdapter = btAdapterDefault;
        }
        if(btAdapter == null) {
            return false;
        } else if(!btAdapter.isEnabled()) {
            if(startActivityIfNot) {
                Intent turnBTOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mainActivityCtx.startActivityForResult(turnBTOn, ACTVITY_REQUEST_BLUETOOTH_ENABLED_CODE);
            }
        }
        if(startActivityIfNot) {
            Intent btMakeDiscIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            btMakeDiscIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            mainActivityCtx.startActivityForResult(btMakeDiscIntent, ACTVITY_REQUEST_BLUETOOTH_DISCOVERABLE_CODE);
        }
        return false;
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
        adBuilder.setTitle("Bluetooth Troubleshooting/Tips:");
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
        ChameleonSettings.chameleonDeviceMAC = btDev.getAddress();
        ChameleonSettings.chameleonDeviceSerialNumber = ChameleonSettings.chameleonDeviceMAC;
        /* TODO: Update the settings tab with connected device information at this point ?!? */

        Handler configDeviceHandler = new Handler();
        Runnable configDeviceRunnable = new Runnable() {
            public void run() {
                Log.i(TAG, ChameleonSettings.getActiveSerialIOPort().toString());
                if(ChameleonSettings.getActiveSerialIOPort() != null && btGattConnectorBLEDevice.isDeviceConnected()) {
                    configDeviceHandler.removeCallbacks(this);
                    ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                    ChameleonIO.deviceStatus.updateAllStatusAndPost(false); /* Make sure the device returned the correct data to display */
                    ChameleonIO.DeviceStatusSettings.startPostingStats(0);
                    LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusIconBT, R.drawable.bluetooth16);
                }
                else {
                    Log.i(TAG, "BLE device __NOT__ connected! ... Looping");
                    configDeviceHandler.postDelayed(this, 1000);
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

    public String getDeviceName() {
        return activeDevice != null ? activeDevice.getName() : "<UNKNOWN-BTDEV-NAME>";
    }

    public BluetoothSerialInterface(Context appContext) {
        notifyContext = appContext;
        activeDevice = null;
        btGattConnectorBLEDevice = new BluetoothGattConnector(notifyContext);
        btGattConnectorBLEDevice.setBluetoothSerialInterface(this);
        btDeviceState = ChameleonBluetoothDeviceState.BTDEV_STATE_DISCONNECTED;
        baudRate = ChameleonSettings.serialBaudRate;
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
        ChameleonSettings.serialBaudRate = baudRate;
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
        String devInfo = String.format(Locale.getDefault(), "BT Class: %s\nBond State: %s\nProduct Name: %s\nType: %s\nDevice Address: %s",
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
        if(ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX == ChameleonSettings.BTIO_IFACE_INDEX) {
            ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
        }
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
        } catch(Exception ie) {
            ie.printStackTrace();
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

    public BluetoothSerialInterface.ChameleonBluetoothDeviceState getState() {
        return btDeviceState;
    }

    public BluetoothSerialInterface.ChameleonBluetoothDeviceState getBLEGattState() {
        return btGattConnectorBLEDevice.getState();
    }

}
