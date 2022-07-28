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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BluetoothGattConnector extends BluetoothGattCallback {

    private static final String TAG = BluetoothGattConnector.class.getSimpleName();

    /* NOTE: More Android Bluetooth BLE documentation available here:
     *       https://punchthrough.com/android-ble-guide/
     */

    /* NOTE: See also these new options for a regex pattern for the companion device name:
     *       https://developer.android.com/guide/topics/connectivity/companion-device-pairing
     */

    public static final String CHAMELEON_REVG_NAME = "BLE-Chameleon";
    public static final String CHAMELEON_REVG_NAME_ALTERNATE = "Chameleon";
    public static final String CHAMELEON_REVG_TINY_NAME = "ChameleonTiny";

    public static final String CHAMELEON_REVG_UART_SERVICE_UUID_STRING = "51510001-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_SEND_CHAR_UUID_STRING = "51510002-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_CHAR_UUID_STRING = "51510003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_CTRL_CHAR_UUID_STRING = "51510004-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_DESC_UUID_STRING = "00002902-0000-1000-8000-00805f9b34fb";

    public static final ParcelUuid CHAMELEON_REVG_UART_SERVICE_UUID = ParcelUuid.fromString(CHAMELEON_REVG_UART_SERVICE_UUID_STRING);
    public static final ParcelUuid CHAMELEON_REVG_SEND_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_SEND_CHAR_UUID_STRING);
    public static final ParcelUuid CHAMELEON_REVG_RECV_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_RECV_CHAR_UUID_STRING);
    public static final ParcelUuid CHAMELEON_REVG_CTRL_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_CTRL_CHAR_UUID_STRING);
    public static final ParcelUuid CHAMELEON_REVG_RECV_DESC_UUID = ParcelUuid.fromString(CHAMELEON_REVG_RECV_DESC_UUID_STRING);

    private static final String BLUETOOTH_SYSTEM_SERVICE = Context.BLUETOOTH_SERVICE;
    public static final byte[] BLUETOOTH_GATT_ENABLE_NOTIFY_PROP = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    public static final int BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
    public static final int BLUETOOTH_LOCAL_MTU_THRESHOLD = 244;
    public static final int BLUETOOTH_GATT_RSP_WRITE = 0x13;
    public static final int BLUETOOTH_GATT_RSP_EXEC_WRITE = 0x19;
    public static final int BLUETOOTH_GATT_ERROR = 0x85;

    private boolean btPermsObtained;
    private boolean isConnected;
    public final Context btSerialContext;
    public BluetoothDevice btDevice;
    public BluetoothAdapter btAdapter;
    private Handler pollBTDevicesFromAdapterHandler;
    private Runnable pollBTDevicesFromAdapterRunner;
    public BluetoothGatt btGatt;
    private BroadcastReceiver btConnReceiver;
    private boolean btConnRecvRegistered;
    private BluetoothBLEInterface btSerialIface;
    public static byte[] btDevicePinDataBytes = new byte[0];

    private BluetoothGattService txDataService;
    private BluetoothGattCharacteristic ctrlGattChar;
    private BluetoothGattCharacteristic sendGattChar;
    private BluetoothGattCharacteristic recvGattChar;
    private BluetoothGattDescriptor ctrlGattCharDesc;
    private BluetoothGattDescriptor sendGattCharDesc;
    private BluetoothGattDescriptor recvGattCharDesc;

    public Handler checkRestartCancelledBTDiscHandler;
    private Runnable checkRestartCancelledBTDiscRunner;
    private static final long CHECK_RESTART_BTDISC_INTERVAL = 10000L;

    private static final long BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT = ChameleonIO.LOCK_TIMEOUT;
    private final Semaphore bleReadLock = new Semaphore(1, true);
    private final Semaphore bleWriteLock = new Semaphore(1, true);

    public BluetoothGattConnector(Context appContext) {
        btSerialContext = appContext;
        btDevice = null;
        btConnReceiver = new BluetoothBroadcastReceiver(this);
        btPermsObtained = false;
        btConnRecvRegistered = false;
        pollBTDevicesFromAdapterHandler = null;
        pollBTDevicesFromAdapterRunner = null;
        initializeBTDevicesFromAdapterPolling();
        btAdapter = configureBluetoothAdapter();
        btGatt = null;
        btSerialIface = ChameleonSettings.getBluetoothIOInterface();
        checkRestartCancelledBTDiscHandler = null;
        checkRestartCancelledBTDiscRunner = null;
        initializeRestartCancelledBTDiscRuntime();
        isConnected = false;
        BluetoothGattConnector.btDevicePinDataBytes = getStoredBluetoothDevicePinData();
        txDataService = null;
        ctrlGattChar = null;
        sendGattChar = null;
        recvGattChar = null;
        ctrlGattCharDesc = null;
        sendGattCharDesc = null;
        recvGattCharDesc = null;
    }

    public boolean isDeviceConnected() {
        return isConnected;
    }

    public void setBluetoothSerialInterface(BluetoothBLEInterface btLocalSerialIface) {
        btSerialIface = btLocalSerialIface;
    }

    public void receiveBroadcastIntent(@NonNull Intent bcIntent) {
        btConnReceiver.onReceive(btSerialContext, bcIntent);
    }

    private BluetoothAdapter configureBluetoothAdapter() {
        BluetoothManager btManager = (BluetoothManager) btSerialContext.getSystemService(BLUETOOTH_SYSTEM_SERVICE);
        if (btManager == null) {
            return null;
        }
        BluetoothAdapter btLocalAdapter = btManager.getAdapter();
        if (btLocalAdapter == null) {
            btLocalAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btLocalAdapter == null) {
                return null;
            }
        }
        return btLocalAdapter;
    }

    private void registerBluetoothConnectionReceiver() {
        if (btConnRecvRegistered) {
            return;
        }
        IntentFilter btConnectIntentFilter = new IntentFilter();
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        btConnectIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        btConnectIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        btConnectIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btConnectIntentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        btSerialContext.registerReceiver(btConnReceiver, btConnectIntentFilter);
        btConnRecvRegistered = true;
    }

    @SuppressLint("MissingPermission")
    public boolean startConnectingDevices() {
        if(!btPermsObtained) {
            btPermsObtained = BluetoothUtils.isBluetoothEnabled(true);
        }
        if(btPermsObtained) {
            registerBluetoothConnectionReceiver();
            try {
                if (btAdapter == null) {
                    btAdapter = configureBluetoothAdapter();
                }
                if (btAdapter != null && !btAdapter.isDiscovering()) {
                    btAdapter.startDiscovery();
                }
            } catch (SecurityException se) {
                AndroidLog.printStackTrace(se);
                return false;
            }
            startBTDevicesFromAdapterPolling();
            startRestartCancelledBTDiscRuntime();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public boolean stopConnectingDevices() {
        Utils.clearToastMessage();
        if(!btPermsObtained) {
            btPermsObtained = BluetoothUtils.isBluetoothEnabled(false);
        }
        boolean status = true;
        if(btPermsObtained && btAdapter != null) {
            try {
                if (btAdapter.isDiscovering()) {
                    btAdapter.cancelDiscovery();
                }
                stopRestartCancelledBTDiscRuntime();
                stopBTDevicesFromAdapterPolling();
            } catch(SecurityException se) {
                AndroidLog.printStackTrace(se);
                status = false;
            }
        }
        return status;
    }

    @SuppressLint("MissingPermission")
    public boolean disconnectDevice() {
        if (isDeviceConnected()) {
            btDevice = null;
            if (btGatt != null) {
                try {
                    btGatt.abortReliableWrite();
                    btGatt.disconnect();
                    btGatt.close();
                } catch (SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }
                btGatt = null;
            }
            if (btAdapter != null) {
                try {
                    if (btAdapter.isDiscovering()) {
                        btAdapter.cancelDiscovery();
                    }
                } catch (SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }
            }
            try {
                if (btConnRecvRegistered) {
                    btSerialContext.unregisterReceiver(btConnReceiver);
                }
            } catch (Exception excpt) {
                AndroidLog.printStackTrace(excpt);
            }
            stopRestartCancelledBTDiscRuntime();
            stopBTDevicesFromAdapterPolling();
            btConnRecvRegistered = false;
            isConnected = false;
            bleReadLock.release();
            bleWriteLock.release();
            btSerialIface.shutdownSerial();
            return true;
        }
        return false;
    }

    private void initializeRestartCancelledBTDiscRuntime() {
        BluetoothGattConnector btGattConnMainRef = this;
        checkRestartCancelledBTDiscRunner = new Runnable() {
            BluetoothGattConnector btGattConnRef = btGattConnMainRef;
            BluetoothAdapter btLocalAdapter = btAdapter;
            @Override
            public void run() {
                if (btLocalAdapter == null) {
                    btLocalAdapter = configureBluetoothAdapter();
                }
                try {
                    if (!btGattConnRef.isDeviceConnected() && !btLocalAdapter.isDiscovering()) {
                        btGattConnRef.stopConnectingDevices();
                        btGattConnRef.startConnectingDevices();
                    }
                } catch (SecurityException se) {
                    AndroidLog.printStackTrace(se);
                    return;
                } catch (NullPointerException npe) {
                    AndroidLog.printStackTrace(npe);
                }
                btGattConnRef.checkRestartCancelledBTDiscHandler.postDelayed(this, CHECK_RESTART_BTDISC_INTERVAL);
            }
        };
        checkRestartCancelledBTDiscHandler = new Handler(Looper.getMainLooper());
    }

    private void startRestartCancelledBTDiscRuntime() {
        checkRestartCancelledBTDiscHandler.post(checkRestartCancelledBTDiscRunner);
    }

    public void stopRestartCancelledBTDiscRuntime() {
        checkRestartCancelledBTDiscHandler.removeCallbacks(checkRestartCancelledBTDiscRunner);
    }

    @SuppressLint("MissingPermission")
    private void initializeBTDevicesFromAdapterPolling() {
        BluetoothGattConnector btGattConnPollingRef = this;
        pollBTDevicesFromAdapterHandler = new Handler(Looper.getMainLooper());
        pollBTDevicesFromAdapterRunner = new Runnable() {
            BluetoothGattConnector btGattConnRef = btGattConnPollingRef;
            BluetoothAdapter btPollAdapter = btAdapter;
            final long pollBTDevicesFromAdapterInterval = CHECK_RESTART_BTDISC_INTERVAL;
            @Override
            public void run() {
                if (btPollAdapter == null) {
                    btPollAdapter = configureBluetoothAdapter();
                }
                boolean foundChamBTDevice = false;
                try {
                    Set<BluetoothDevice> pairedDevices = btPollAdapter.getBondedDevices();
                    if (pairedDevices != null && pairedDevices.size() > 0) {
                        ArrayList<BluetoothDevice> devList = new ArrayList<>(pairedDevices);
                        for (BluetoothDevice btDev : devList) {
                            String devName = btDev.getName();
                            if (BluetoothUtils.isChameleonDeviceName(devName)) {
                                Intent bcDevIntent = new Intent(BluetoothDevice.ACTION_FOUND);
                                bcDevIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, btDev);
                                foundChamBTDevice = true;
                                final Handler postNotifyPairedHandler = new Handler(Looper.getMainLooper());
                                final Runnable postNotifyPairedRunner = new Runnable() {
                                    final BluetoothGattConnector btGattConnLocalRef = btGattConnRef;
                                    @Override
                                    public void run() {
                                        btGattConnLocalRef.receiveBroadcastIntent(bcDevIntent);
                                    }
                                };
                                break;
                            }
                        }
                    }
                } catch (Exception seNPE) {
                    AndroidLog.printStackTrace(seNPE);
                }
                if (foundChamBTDevice) {
                    AndroidLog.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "BT device already paired with the adapter found ==>\nforwarding the device with an ACTION_FOUND intent to the broadcast receiver"));
                    pollBTDevicesFromAdapterHandler.removeCallbacks(pollBTDevicesFromAdapterRunner);
                } else {
                    pollBTDevicesFromAdapterHandler.postDelayed(pollBTDevicesFromAdapterRunner, pollBTDevicesFromAdapterInterval);
                }
            }
        };
    }

    private void startBTDevicesFromAdapterPolling() {
        pollBTDevicesFromAdapterHandler.post(pollBTDevicesFromAdapterRunner);
    }

    private void stopBTDevicesFromAdapterPolling() {
        pollBTDevicesFromAdapterHandler.removeCallbacks(pollBTDevicesFromAdapterRunner);
    }

    @SuppressLint("MissingPermission")
    public void notifyBluetoothBLEDeviceConnected(@NonNull BluetoothDevice btLocalDevice) {
        isConnected = true;
        stopConnectingDevices();
        Intent notifyMainActivityIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(notifyMainActivityIntent);
        btSerialIface.configureSerialConnection(btLocalDevice);
    }

    public void notifyBluetoothSerialInterfaceDataRead(byte[] serialDataRead) {
        if (btSerialIface != null && serialDataRead != null) {
            btSerialIface.onReceivedData(serialDataRead);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onConnectionStateChange: error/status code %d = %04x", status, status));
            return;
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        try {
            AndroidLog.d(TAG, "onDescriptorWrite: [UUID] " + descriptor.getCharacteristic().getUuid());
        } catch (NullPointerException npe) {
            AndroidLog.printStackTrace(npe);
        }
        bleWriteLock.release();
        if (status == BLUETOOTH_GATT_ERROR) {
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            AndroidLog.i(TAG, "onDescriptorWrite: status BLUETOOTH_GATT_ERROR");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] charData = null;
        try {
            gatt.readCharacteristic(characteristic);
            charData = characteristic.getValue();
            AndroidLog.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE,
                    "onCharacteristicChanged: readCharacteristic: %s WITH DATA %s",
                    characteristic.getUuid().toString(), new String(charData)));
        } catch (Exception seNPE) {
            AndroidLog.printStackTrace(seNPE);
        }
        bleReadLock.release();
        if (charData != null) {
            notifyBluetoothSerialInterfaceDataRead(charData);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BLUETOOTH_GATT_ERROR) {
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            AndroidLog.i(TAG, "onCharacteristicRead: status BLUETOOTH_GATT_ERROR");
            bleReadLock.release();
            return;
        }
        byte[] charData = null;
        try {
            gatt.readCharacteristic(characteristic);
            charData = characteristic.getValue();
            AndroidLog.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE,
                    "onCharacteristicChanged: readCharacteristic: %s WITH DATA %s",
                    characteristic.getUuid().toString(), new String(charData)));
        } catch (Exception seNPE) {
            AndroidLog.printStackTrace(seNPE);
        }
        bleReadLock.release();
        if (charData != null) {
            notifyBluetoothSerialInterfaceDataRead(charData);
        }
    }

    @SuppressLint("MissingPermission")
    public boolean enumerateBLEGattComms(BluetoothGatt gatt) {
        txDataService = new BluetoothGattService(CHAMELEON_REVG_UART_SERVICE_UUID.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        if (txDataService == null && gatt != null) {
            txDataService = gatt.getService(CHAMELEON_REVG_UART_SERVICE_UUID.getUuid());
        }
        if (gatt != null && txDataService == null) {
            return false;
        }
        ctrlGattChar = txDataService.getCharacteristic(CHAMELEON_REVG_CTRL_CHAR_UUID.getUuid());
        ctrlGattCharDesc = ctrlGattChar.getDescriptor(CHAMELEON_REVG_CTRL_CHAR_UUID.getUuid());
        sendGattChar = txDataService.getCharacteristic(CHAMELEON_REVG_SEND_CHAR_UUID.getUuid());
        sendGattCharDesc = sendGattChar.getDescriptor(CHAMELEON_REVG_SEND_CHAR_UUID.getUuid());
        recvGattChar = txDataService.getCharacteristic(CHAMELEON_REVG_RECV_CHAR_UUID.getUuid());
        recvGattCharDesc = recvGattChar.getDescriptor(CHAMELEON_REVG_RECV_CHAR_UUID.getUuid());
        return ctrlGattChar != null && ctrlGattCharDesc != null && sendGattChar != null &&
                sendGattCharDesc != null && recvGattChar != null && recvGattCharDesc != null;
    }

    @SuppressLint("MissingPermission")
    public boolean enableNotifyOnBLEGattService(BluetoothGatt gatt) {
        try {
            gatt.setCharacteristicNotification(ctrlGattChar, true);
            ctrlGattCharDesc.setValue(BLUETOOTH_GATT_ENABLE_NOTIFY_PROP);
            gatt.writeDescriptor(ctrlGattCharDesc);
            gatt.setCharacteristicNotification(sendGattChar, true);
            sendGattCharDesc.setValue(BLUETOOTH_GATT_ENABLE_NOTIFY_PROP);
            gatt.writeDescriptor(sendGattCharDesc);
            gatt.setCharacteristicNotification(recvGattChar, true);
            recvGattCharDesc.setValue(BLUETOOTH_GATT_ENABLE_NOTIFY_PROP);
            gatt.writeDescriptor(recvGattCharDesc);
        } catch (SecurityException se) {
            AndroidLog.printStackTrace(se);
            return false;
        }
        isConnected = true;
        stopConnectingDevices();
        Intent notifyMainActivityIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(notifyMainActivityIntent);
        btSerialIface.configureSerialConnection(btDevice);
        return true;
    }

    @SuppressLint("MissingPermission")
    public boolean requestConnectionPriority(int connectPrioritySetting) {
        if(btGatt == null) {
            return false;
        }
        try {
            return btGatt.requestConnectionPriority(connectPrioritySetting);
        } catch(SecurityException se) {
            AndroidLog.printStackTrace(se);
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    public int write(byte[] dataBuf) throws IOException {
        /* TODO: Need to wrap the byte buffer with checksums and/or control characters. */
        AndroidLog.i(TAG, "write: " + Utils.bytes2Hex(dataBuf));
        if (dataBuf.length > BLUETOOTH_LOCAL_MTU_THRESHOLD) {
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        if(txDataService == null) {
            AndroidLog.i(TAG, "write: Unable to obtain BLE service");
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        if(sendGattChar == null) {
            AndroidLog.i(TAG, "write: Unable to obtain charactristic " + CHAMELEON_REVG_SEND_CHAR_UUID.getUuid().toString());
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        sendGattChar.setValue(dataBuf);
        try {
            if (bleWriteLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                btGatt.writeCharacteristic(sendGattChar);
            } else {
                AndroidLog.w(TAG, "Cannot acquire BT BLE read lock for operation");
                /* TODO: Return a Chameleon terminal TIMEOUT response here ??? */
                return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
            }
        } catch(SecurityException se) {
            AndroidLog.printStackTrace(se);
            disconnectDevice();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        } catch(InterruptedException ie) {
            AndroidLog.printStackTrace(ie);
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
        }
        return ChameleonSerialIOInterface.STATUS_OK;
    }

    @SuppressLint("MissingPermission")
    public int read() throws IOException {
        /* TODO: Big or little endian byte order of the results returned (Chameleon Mini AVR is LE) -- Handle in the BTUtils BLEPacket formatter routines. */
        BluetoothGattService rxDataService = null;
        if (btGatt == null) {
            rxDataService = new BluetoothGattService(CHAMELEON_REVG_UART_SERVICE_UUID.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        } else {
            rxDataService = btGatt.getService(CHAMELEON_REVG_UART_SERVICE_UUID.getUuid());
        }
        if(rxDataService == null) {
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattCharacteristic btGattChar = rxDataService.getCharacteristic(CHAMELEON_REVG_UART_SERVICE_UUID.getUuid());
        if(recvGattChar == null) {
            AndroidLog.i(TAG, "read: Unable to obtain charactristic " + CHAMELEON_REVG_UART_SERVICE_UUID.getUuid().toString());
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        try {
            if (bleReadLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                btGatt.readCharacteristic(recvGattChar);
            } else {
                AndroidLog.w(TAG, "Cannot acquire BT BLE read lock for operation");
                /* ??? TODO: Return a Chameleon terminal TIMEOUT response here ??? */
                return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
            }
        } catch(SecurityException se) {
            AndroidLog.printStackTrace(se);
            disconnectDevice();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        } catch(InterruptedException ie) {
            AndroidLog.printStackTrace(ie);
            /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
            return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
        }
        return ChameleonSerialIOInterface.STATUS_OK;
    }

    public byte[] getStoredBluetoothDevicePinData() {
        String pinData = AndroidSettingsStorage.getStringValueByKey(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE, AndroidSettingsStorage.BLUETOOTH_DEVICE_PIN_DATA);
        if(pinData == null || pinData.length() == 0) {
            return new byte[0];
        }
        else {
            return pinData.getBytes(StandardCharsets.UTF_8);
        }
    }

    public void setStoredBluetoothDevicePinData(@NonNull String btPinData) {
        AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE, AndroidSettingsStorage.BLUETOOTH_DEVICE_PIN_DATA);
        BluetoothGattConnector.btDevicePinDataBytes = btPinData.getBytes(StandardCharsets.UTF_8);
    }

}
