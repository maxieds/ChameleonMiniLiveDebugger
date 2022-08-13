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
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
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

    public enum BleUuidType {
        UART_SERVICE_UUID,
        CTRL_CHAR_UUID,
        SEND_CHAR_UUID,
        RECV_CHAR_UUID;

        public static ParcelUuid getParcelUuidByType(BleUuidType uuidType) {
            if (uuidType == null) {
                return null;
            }
            switch (uuidType) {
                case UART_SERVICE_UUID:
                    return CHAMELEON_REVG_UART_SERVICE_UUID;
                case CTRL_CHAR_UUID:
                    return CHAMELEON_REVG_CTRL_CHAR_UUID;
                case SEND_CHAR_UUID:
                    return CHAMELEON_REVG_SEND_CHAR_UUID;
                case RECV_CHAR_UUID:
                    return CHAMELEON_REVG_RECV_CHAR_UUID;
                default:
                    break;
            }
            return null;
        }

        public static UUID getUuidByType(BleUuidType uuidType) {
            ParcelUuid pUuid = getParcelUuidByType(uuidType);
            if (pUuid != null) {
                return pUuid.getUuid();
            }
            return null;
        }

    }

    private static final String BLUETOOTH_SYSTEM_SERVICE = Context.BLUETOOTH_SERVICE;
    private static final byte[] BLUETOOTH_GATT_ENABLE_NOTIFY_PROP = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    private static final int BLUETOOTH_LOCAL_MTU_THRESHOLD = 56; // 244;
    private static final int BLUETOOTH_GATT_RSP_WRITE = 0x13;
    private static final int BLUETOOTH_GATT_RSP_EXEC_WRITE = 0x19;
    private static final int BLUETOOTH_GATT_ERROR = 0x85;

    private boolean btPermsObtained;
    private boolean isConnected;
    public final Context btSerialContext;
    public BluetoothDevice btDevice;
    public BluetoothAdapter btAdapter;
    private Handler pollBTDevicesFromAdapterHandler;
    private Runnable pollBTDevicesFromAdapterRunner;
    public BluetoothGatt btGatt;
    private final BroadcastReceiver btConnReceiver;
    private boolean btConnRecvRegistered;
    private BluetoothBLEInterface btSerialIface;
    public static byte[] btDevicePinDataBytes = new byte[0];

    private Handler discoverServicesHandler;
    private Runnable discoverServicesRunner;

    private BluetoothGattService rxtxDataService;
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

    private final Semaphore bleRawLock = new Semaphore(1, true);
    private boolean READ_DATA_RAW;
    private byte[] READ_DATA_RAW_RESULT;

    private static BluetoothGattConnector activeInstance = null;

    public static BluetoothGattConnector getActiveInstance() {
        return activeInstance;
    }

    public BluetoothGattConnector(Context appContext) {
        btSerialContext = appContext;
        btDevice = null;
        btConnReceiver = BluetoothBroadcastReceiver.initializeActiveInstance(this);
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
        discoverServicesHandler = null;
        discoverServicesRunner = null;
        rxtxDataService = null;
        ctrlGattChar = null;
        sendGattChar = null;
        recvGattChar = null;
        ctrlGattCharDesc = null;
        sendGattCharDesc = null;
        recvGattCharDesc = null;
        READ_DATA_RAW = false;
        READ_DATA_RAW_RESULT = null;
        activeInstance = this;
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
        //btConnectIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
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
                AndroidLogger.printStackTrace(se);
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
                if (discoverServicesHandler != null && discoverServicesRunner != null) {
                    discoverServicesHandler.removeCallbacks(discoverServicesRunner);
                }
                //discoverServicesHandler = null;
                //discoverServicesRunner = null;
                stopRestartCancelledBTDiscRuntime();
                stopBTDevicesFromAdapterPolling();
            } catch(SecurityException se) {
                AndroidLogger.printStackTrace(se);
                status = false;
            }
        }
        return status;
    }

    /**
     *  NOTE: The next procedure is lifted from the Nordic BLE library
     *        (see URL references to the original source below).
     *
     *        It should remove the pairing information of the selected Chameleon BT device
     *        from the Android system. In recent releases of Android OS, the call to the
     *        BluetoothDevice class 'removeBond' method is blocked by SDK version annotations that
     *        effectively "hide" this functionality making the operation impossible outside of the
     *        system Settings GUI :(
     *
     *        If we do not remove the pairing data for the
     *        device each time we connect a new Chameleon over BLE/BT, the service discovery process
     *        fails the second time after the initial run where the device is paired.
     *
     * @url https://github.com/NordicSemiconductor/Android-BLE-Library/blob/e3eeeb82a742b45707a58ec72eb384ccf6db9620/ble/src/main/java/no/nordicsemi/android/ble/BleManagerHandler.java#L786
     * @url https://github.com/innoveit/react-native-ble-manager/blob/e2e5ca00019eb567b59b16e16e597f488d8001fe/android/src/main/java/it/innove/BleManager.java#L253
     */
    @SuppressLint("MissingPermission")
    private static boolean removeBluetoothDeviceBond(@NonNull BluetoothDevice btDev) {
        try {
            final Method removeBond = btDev.getClass().getMethod("removeBond");
            return removeBond.invoke(btDev) == Boolean.TRUE;
        } catch (final Exception excpt) {
            AndroidLogger.w(TAG, "An exception occurred while removing bond");
            AndroidLogger.printStackTrace(excpt);
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private boolean unpairConnectedBluetoothDeviceCompletely() {
        boolean status = true;
        if (btGatt != null) {
            try {
                btGatt.disconnect();
                //btGatt.close();
            } catch (SecurityException se){
                AndroidLogger.printStackTrace(se);
                status = false;
            }
        } else {
            status = false;
        }
        if (btDevice != null) {
            removeBluetoothDeviceBond(btDevice);
        } else {
            status = false;
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
                    unpairConnectedBluetoothDeviceCompletely();
                } catch (SecurityException se) {
                    AndroidLogger.printStackTrace(se);
                }
                btGatt = null;
            }
            if (btAdapter != null) {
                try {
                    if (btAdapter.isDiscovering()) {
                        btAdapter.cancelDiscovery();
                    }
                    if (discoverServicesHandler != null && discoverServicesRunner != null) {
                        discoverServicesHandler.removeCallbacks(discoverServicesRunner);
                    }
                    //discoverServicesHandler = null;
                    //discoverServicesRunner = null;
                } catch (SecurityException se) {
                    AndroidLogger.printStackTrace(se);
                }
            }
            try {
                if (btConnRecvRegistered) {
                    btSerialContext.unregisterReceiver(btConnReceiver);
                }
            } catch (Exception excpt) {
                AndroidLogger.printStackTrace(excpt);
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

    public void releaseAllLocks() {
        bleReadLock.release();
        bleWriteLock.release();
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
                    AndroidLogger.printStackTrace(se);
                    return;
                } catch (NullPointerException npe) {
                    AndroidLogger.printStackTrace(npe);
                }
                btGattConnRef.checkRestartCancelledBTDiscHandler.postDelayed(this, CHECK_RESTART_BTDISC_INTERVAL);
            }
        };
        checkRestartCancelledBTDiscHandler = new Handler(Looper.getMainLooper());
    }

    public void startRestartCancelledBTDiscRuntime() {
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
                            String devName = "<UNKNOWN>";
                            try {
                                if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
                                    removeBluetoothDeviceBond(btDev);
                                }
                                devName = btDev.getName();
                            } catch (Exception btEx) {
                                AndroidLogger.printStackTrace(btEx);
                                continue;
                            }
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
                                postNotifyPairedHandler.postDelayed(postNotifyPairedRunner, 500L);
                                break;
                            }
                        }
                    }
                } catch (Exception seNPE) {
                    AndroidLogger.printStackTrace(seNPE);
                }
                if (foundChamBTDevice) {
                    AndroidLogger.d(TAG, "BT device already paired with the adapter found ==>\n ... Forwarding the device with an ACTION_FOUND intent to the broadcast receiver");
                    pollBTDevicesFromAdapterHandler.removeCallbacks(pollBTDevicesFromAdapterRunner);
                } else {
                    pollBTDevicesFromAdapterHandler.postDelayed(pollBTDevicesFromAdapterRunner, pollBTDevicesFromAdapterInterval);
                }
            }
        };
    }

    public void startBTDevicesFromAdapterPolling() {
        pollBTDevicesFromAdapterHandler.post(pollBTDevicesFromAdapterRunner);
    }

    public void stopBTDevicesFromAdapterPolling() {
        pollBTDevicesFromAdapterHandler.removeCallbacks(pollBTDevicesFromAdapterRunner);
    }

    @SuppressLint("MissingPermission")
    private void notifyBluetoothBLEDeviceConnected(BluetoothDevice btLocalDevice) {
        if (btLocalDevice == null) {
            return;
        }
        isConnected = true;
        stopConnectingDevices();
        if (btGatt != null) {
            int btConnPriority = Integer.valueOf(AndroidSettingsStorage.getStringValueByKey(AndroidSettingsStorage.BLUETOOTH_CONNECTION_PRIORITY));
            requestConnectionPriority(btConnPriority);
        }
        Intent notifyMainActivityIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(notifyMainActivityIntent);
        btSerialIface.configureSerialConnection(btLocalDevice);
    }

    public void notifyBluetoothBLEDeviceConnected() {
        notifyBluetoothBLEDeviceConnected(btDevice);
    }

    private void notifyBluetoothSerialInterfaceDataRead(byte[] serialDataRead) {
        if (btSerialIface != null && serialDataRead != null) {
            btSerialIface.onReceivedData(serialDataRead);
        }
    }

    private void insertBluetoothSerialInterfaceTerminalResponse(@NonNull String respText) {
        if (btSerialIface != null) {
            byte[] insRespBytes = respText.getBytes(StandardCharsets.US_ASCII);
            btSerialIface.onReceivedData(insRespBytes);
        }
    }

    private void insertBluetoothSerialInterfaceTerminalResponse(@NonNull ChameleonIO.SerialRespCode srCode) {
        insertBluetoothSerialInterfaceTerminalResponse(srCode.getChameleonTerminalResponse());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            AndroidLogger.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onConnectionStateChange: error/status code %d = %04x", status, status));
            disconnectDevice();
        } else if (newState == BluetoothGatt.STATE_CONNECTED) {
            if (btGatt == null) {
                btGatt = gatt;
            }
            configureGattConnector();
        }
    }

    @SuppressLint("MissingPermission")
    public void onSearchComplete(String address, int status) {
        AndroidLogger.d(TAG, "onSearchComplete() = Device=" + address + " Status=" + status);
        if (btDevice != null && !address.equals(btDevice.getAddress())) {
            return;
        }
        onServicesDiscovered(btGatt, status);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (gatt != null) {
            btGatt = gatt;
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            AndroidLogger.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onServicesDiscovered: error/status code %d = %04x", status, status));
            return;
        } else if (configureGattConnector()) {
            if (discoverServicesHandler != null && discoverServicesRunner != null) {
                discoverServicesHandler.removeCallbacks(discoverServicesRunner);
            }
            BluetoothBroadcastReceiver.printServicesSummaryListToLog(btGatt);
            notifyBluetoothBLEDeviceConnected();
        } else {
            disconnectDevice();
            stopConnectingDevices();
            startConnectingDevices();
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        try {
            if (status == BLUETOOTH_GATT_ERROR) {
                AndroidLogger.d(TAG, "onDescriptorWrite: status BLUETOOTH_GATT_ERROR");
                insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            }
            AndroidLogger.d(TAG, "onDescriptorWrite: [UUID] " + descriptor.getCharacteristic().getUuid());
        } catch (NullPointerException npe) {
            AndroidLogger.printStackTrace(npe);
        }
        bleWriteLock.release();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] charData = null;
        try {
            gatt.readCharacteristic(characteristic);
            charData = characteristic.getValue();
            AndroidLogger.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE,
                    "onCharacteristicChanged: readCharacteristic: %s WITH DATA %s",
                    characteristic.getUuid().toString(), new String(charData)));
        } catch (Exception seNPE) {
            AndroidLogger.printStackTrace(seNPE);
        }
        if (charData != null && bleReadLock.availablePermits() == 0) {
            try {
                if (bleRawLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS) && READ_DATA_RAW) {
                    READ_DATA_RAW_RESULT = charData;
                    READ_DATA_RAW = false;
                    bleRawLock.release();
                } else {
                    notifyBluetoothSerialInterfaceDataRead(BluetoothUtils.BLEPacket.unpackageData(charData));
                }
            } catch(InterruptedException ie) {
                AndroidLogger.printStackTrace(ie);
                notifyBluetoothSerialInterfaceDataRead(BluetoothUtils.BLEPacket.unpackageData(charData));
            }
        }
        bleRawLock.release();
        bleReadLock.release();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BLUETOOTH_GATT_ERROR) {
            AndroidLogger.i(TAG, "onCharacteristicRead: status BLUETOOTH_GATT_ERROR");
            bleReadLock.release();
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            return;
        }
        byte[] charData = null;
        try {
            gatt.readCharacteristic(characteristic);
            charData = characteristic.getValue();
            AndroidLogger.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE,
                    "onCharacteristicChanged: readCharacteristic: %s WITH DATA %s",
                    characteristic.getUuid().toString(), new String(charData)));
        } catch (Exception seNPE) {
            AndroidLogger.printStackTrace(seNPE);
        }
        if (charData != null && bleReadLock.availablePermits() == 0) {
            try {
                if (bleRawLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS) && READ_DATA_RAW) {
                    READ_DATA_RAW_RESULT = charData;
                    READ_DATA_RAW = false;
                    bleRawLock.release();
                } else {
                    notifyBluetoothSerialInterfaceDataRead(BluetoothUtils.BLEPacket.unpackageData(charData));
                }
            } catch(InterruptedException ie) {
                AndroidLogger.printStackTrace(ie);
                notifyBluetoothSerialInterfaceDataRead(BluetoothUtils.BLEPacket.unpackageData(charData));
            }
        }
        bleRawLock.release();
        bleReadLock.release();
    }

    public boolean configureGattConnector() {
        return enumerateBLEGattComms(btGatt) && enableNotifyOnBLEGattService(btGatt);
    }

    @SuppressLint("MissingPermission")
    public BluetoothGatt configureGattDataConnection() {
        if (btGatt == null) {
            return null;
        }
        if (btDevice == null) {
            btDevice = btGatt.getDevice();
        }
        //if (btDevice != null) {
        //    btDevice.fetchUuidsWithSdp();
        //}
        if (btGatt != null) {
            btGatt.requestMtu(BLUETOOTH_LOCAL_MTU_THRESHOLD);
            requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
            //btGatt.readRemoteRssi();
            if (discoverServicesHandler == null) {
                discoverServicesHandler = new Handler(Looper.getMainLooper());
            }
            if (discoverServicesRunner == null) {
                discoverServicesRunner = new Runnable() {
                    int retryAttempts = 0;
                    final BluetoothGatt btGattRef = btGatt;
                    @Override
                    public void run() {
                        AndroidLogger.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "Initializing BLE device service connections ... ATTEMPT #%d", retryAttempts + 1));
                        if (++retryAttempts >= BluetoothBroadcastReceiver.DISCOVER_SVCS_ATTEMPT_COUNT) {
                            disconnectDevice();
                            stopConnectingDevices();
                            startConnectingDevices();
                        } else if (configureGattConnector()) {
                            BluetoothBroadcastReceiver.printServicesSummaryListToLog(btGattRef);
                            notifyBluetoothBLEDeviceConnected();
                        } else if (!btGattRef.discoverServices()) {
                            Utils.displayToastMessage("Discovering bluetooth services.\nPrepare to wait ...", Toast.LENGTH_LONG);
                            discoverServicesHandler.postDelayed(this, BluetoothBroadcastReceiver.CHECK_DISCOVER_SVCS_INTERVAL);
                        } else if (configureGattConnector()) {
                            BluetoothBroadcastReceiver.printServicesSummaryListToLog(btGattRef);
                            notifyBluetoothBLEDeviceConnected();
                        } else {
                            Utils.displayToastMessage("Discovering bluetooth services.\nPrepare to wait ...", Toast.LENGTH_LONG);
                            discoverServicesHandler.postDelayed(this, BluetoothBroadcastReceiver.CHECK_DISCOVER_SVCS_INTERVAL);
                        }
                    }
                };
            } else {
                discoverServicesHandler.removeCallbacks(discoverServicesRunner);
            }
            discoverServicesHandler.postDelayed(discoverServicesRunner, 500L);
        }
        return btGatt;
    }

    @SuppressLint("MissingPermission")
    private boolean enumerateBLEGattComms(BluetoothGatt gatt) {
        if (gatt != null) {
            rxtxDataService = gatt.getService(BleUuidType.getUuidByType(BleUuidType.UART_SERVICE_UUID));
            if (rxtxDataService == null) {
                rxtxDataService = new BluetoothGattService(BleUuidType.getUuidByType(BleUuidType.UART_SERVICE_UUID), BluetoothGattService.SERVICE_TYPE_PRIMARY);
            }
        } else {
            rxtxDataService = new BluetoothGattService(BleUuidType.getUuidByType(BleUuidType.UART_SERVICE_UUID), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        }
        if (rxtxDataService == null) {
            return false;
        }
        try {
            ctrlGattChar = rxtxDataService.getCharacteristic(BleUuidType.getUuidByType(BleUuidType.CTRL_CHAR_UUID));
            ctrlGattCharDesc = ctrlGattChar.getDescriptor(BleUuidType.getUuidByType(BleUuidType.CTRL_CHAR_UUID));
            sendGattChar = rxtxDataService.getCharacteristic(BleUuidType.getUuidByType(BleUuidType.SEND_CHAR_UUID));
            sendGattCharDesc = sendGattChar.getDescriptor(BleUuidType.getUuidByType(BleUuidType.SEND_CHAR_UUID));
            recvGattChar = rxtxDataService.getCharacteristic(BleUuidType.getUuidByType(BleUuidType.RECV_CHAR_UUID));
            recvGattCharDesc = recvGattChar.getDescriptor(BleUuidType.getUuidByType(BleUuidType.RECV_CHAR_UUID));
        } catch (NullPointerException npe) {}
        return ctrlGattChar != null && ctrlGattCharDesc != null && sendGattChar != null &&
                sendGattCharDesc != null && recvGattChar != null && recvGattCharDesc != null;
    }

    @SuppressLint("MissingPermission")
    private boolean enableNotifyOnBLEGattService(BluetoothGatt gatt) {
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
            AndroidLogger.printStackTrace(se);
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
    public static boolean requestConnectionPriority(int connectPrioritySetting) {
        BluetoothGattConnector btGattConn = BluetoothGattConnector.getActiveInstance();
        if (btGattConn != null) {
            if(btGattConn.btGatt == null) {
                return false;
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return btGattConn.btGatt.requestConnectionPriority(connectPrioritySetting);
                }
            } catch(SecurityException se) {
                AndroidLogger.printStackTrace(se);
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public int write(byte[] dataBuf) throws IOException {
        AndroidLogger.d(TAG, "write: " + Utils.bytes2Hex(dataBuf));
        if (dataBuf.length > BLUETOOTH_LOCAL_MTU_THRESHOLD) {
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        if ((btGatt == null || sendGattChar == null) && !configureGattConnector()) {
            return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
        } else if (!sendGattChar.setValue(BluetoothUtils.BLEPacket.packageData(dataBuf))) {
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        try {
            if (bleWriteLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                btGatt.writeCharacteristic(sendGattChar);
            } else {
                AndroidLogger.w(TAG, "Cannot acquire BT BLE read lock for operation");
                bleWriteLock.release();
                insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
                return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
            }
        } catch(SecurityException se) {
            AndroidLogger.printStackTrace(se);
            disconnectDevice();
            bleWriteLock.release();
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            return ChameleonSerialIOInterface.STATUS_RESOURCE_PERMISSIONS_ERROR;
        } catch(InterruptedException ie) {
            AndroidLogger.printStackTrace(ie);
            bleWriteLock.release();
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
        }
        return ChameleonSerialIOInterface.STATUS_OK;
    }

    @SuppressLint("MissingPermission")
    public byte[] rawWrite(byte[] dataBuf) throws IOException {
        AndroidLogger.d(TAG, "write: " + Utils.bytes2Hex(dataBuf));
        if (dataBuf.length > BLUETOOTH_LOCAL_MTU_THRESHOLD) {
            return null;
        }
        if ((btGatt == null || sendGattChar == null) && !configureGattConnector()) {
            return null;
        } else if (!ctrlGattChar.setValue(dataBuf)) {
            return null;
        }
        try {
            if (bleRawLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                READ_DATA_RAW = true;
                READ_DATA_RAW_RESULT = null;
                bleRawLock.release();
            } else {
                return null;
            }
            if (bleWriteLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS) &&
                    bleReadLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                btGatt.writeCharacteristic(ctrlGattChar);
            } else {
                AndroidLogger.w(TAG, "Cannot acquire BT BLE read lock for operation");
                bleReadLock.release();
                bleWriteLock.release();
                insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
                return null;
            }
        } catch(SecurityException se) {
            AndroidLogger.printStackTrace(se);
            disconnectDevice();
            bleReadLock.release();
            bleRawLock.release();
            bleWriteLock.release();
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            return null;
        } catch(InterruptedException ie) {
            AndroidLogger.printStackTrace(ie);
            bleReadLock.release();
            bleRawLock.release();
            bleWriteLock.release();
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            return null;
        }
        for (long tmtSoFar = 0L; tmtSoFar < ChameleonIO.TIMEOUT; tmtSoFar += 50L) {
            try {
                if (bleRawLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    if (READ_DATA_RAW_RESULT != null) {
                        READ_DATA_RAW = false;
                        byte[] dataRespBytes = READ_DATA_RAW_RESULT.clone();
                        bleRawLock.release();
                        return dataRespBytes;
                    }
                    bleRawLock.release();
                }
                Thread.sleep(50L);
            } catch (InterruptedException ie) {
                AndroidLogger.printStackTrace(ie);
                break;
            }
        }
        READ_DATA_RAW = false;
        bleReadLock.release();
        bleWriteLock.release();
        return null;
    }

    @SuppressLint("MissingPermission")
    public int read() throws IOException {
        if ((btGatt == null || recvGattChar == null) && !configureGattConnector()) {
            AndroidLogger.w(TAG, "read: Unable to obtain recv char charactristic.");
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
        }
        try {
            if (bleReadLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                btGatt.readCharacteristic(recvGattChar);
            } else {
                AndroidLogger.w(TAG, "Cannot acquire BT BLE read lock for operation");
                bleReadLock.release();
                insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
                return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
            }
        } catch(SecurityException se) {
            AndroidLogger.printStackTrace(se);
            disconnectDevice();
            bleReadLock.release();
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
            return ChameleonSerialIOInterface.STATUS_RESOURCE_PERMISSIONS_ERROR;
        } catch(InterruptedException ie) {
            AndroidLogger.printStackTrace(ie);
            bleReadLock.release();
            insertBluetoothSerialInterfaceTerminalResponse(ChameleonIO.SerialRespCode.TIMEOUT);
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
