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
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BluetoothGattConnector extends BluetoothGattCallback {

    private static final String TAG = BluetoothGattConnector.class.getSimpleName();

    /*
     * NOTE: More Android Bluetooth BLE documentation available here:
     *       https://punchthrough.com/android-ble-guide/
     */

    /*
     * NOTE: See also these new options for a regex pattern for the companion device name:
     *       https://developer.android.com/guide/topics/connectivity/companion-device-pairing
     */

    public static final String CHAMELEON_REVG_NAME = "BLE-Chameleon";
    public static final String CHAMELEON_REVG_NAME_ALTERNATE = "Chameleon";
    public static final String CHAMELEON_REVG_UART_SERVICE_UUID_STRING = "51510001-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_SEND_CHAR_UUID_STRING = "51510002-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_CHAR_UUID_STRING = "51510003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_NAME = "ChameleonTiny";
    public static final String CHAMELEON_REVG_TINY_UART_SERVICE_UUID_STRING = "51510001-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_SEND_CHAR_UUID_STRING = "51510002-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_RECV_CHAR_UUID_STRING = "51510003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_CTRL_CHAR_UUID_STRING = "52520003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_DESC_UUID_STRING = "00002902-0000-1000-8000-00805f9b34fb";

    private static final ParcelUuid CHAMELEON_REVG_UART_SERVICE_UUID = ParcelUuid.fromString(CHAMELEON_REVG_UART_SERVICE_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_SEND_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_SEND_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_RECV_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_RECV_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_TINY_UART_SERVICE_UUID = ParcelUuid.fromString(CHAMELEON_REVG_TINY_UART_SERVICE_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_TINY_SEND_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_TINY_SEND_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_TINY_RECV_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_TINY_RECV_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_CTRL_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_CTRL_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_RECV_DESC_UUID = ParcelUuid.fromString(CHAMELEON_REVG_RECV_DESC_UUID_STRING);

    private static final String BLUETOOTH_SYSTEM_SERVICE = Context.BLUETOOTH_SERVICE;
    public static final byte[] BLUETOOTH_GATT_ENABLE_NOTIFY_PROP = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    public static final int BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
    public static final int BLUETOOTH_LOCAL_MTU_THRESHOLD = 244;
    public static final int BLUETOOTH_GATT_RSP_WRITE = 0x13;
    public static final int BLUETOOTH_GATT_RSP_EXEC_WRITE = 0x19;
    public static final int BLUETOOTH_GATT_ERROR = 0x85;

    private boolean btPermsObtained;
    private boolean isConnected;
    private final Context btSerialContext;
    private ParcelUuid chameleonDeviceBLEService;
    private final ParcelUuid chameleonDeviceBLECtrlChar;
    private ParcelUuid chameleonDeviceBLESendChar;
    private ParcelUuid chameleonDeviceBLERecvChar;
    private BluetoothDevice btDevice;
    private final BluetoothAdapter btAdapter;
    private BluetoothLeScanner bleScanner;
    private final ScanCallback bleScanCallback;
    private boolean bleScanInProgress;
    private BluetoothGatt btGatt;
    private final BluetoothGattCallback btGattCallback;
    private BroadcastReceiver btConnReceiver;
    private boolean btConnRecvRegistered;
    private BluetoothBLEInterface btSerialIface;
    public static byte[] btDevicePinDataBytes = new byte[0];

    private static final long BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT = ChameleonIO.LOCK_TIMEOUT;
    private final Semaphore bleReadLock = new Semaphore(1, true);
    private final Semaphore bleWriteLock = new Semaphore(1, true);

    public BluetoothGattConnector(@NonNull Context localContext) {
        btSerialContext = localContext;
        chameleonDeviceBLEService = CHAMELEON_REVG_UART_SERVICE_UUID;
        chameleonDeviceBLESendChar = CHAMELEON_REVG_SEND_CHAR_UUID;
        chameleonDeviceBLERecvChar = CHAMELEON_REVG_RECV_CHAR_UUID;
        chameleonDeviceBLECtrlChar = CHAMELEON_REVG_CTRL_CHAR_UUID;
        btDevice = null;
        btConnReceiver = null;
        btPermsObtained = false;
        btConnRecvRegistered = false;
        bleScanner =  null;
        bleScanCallback = configureBLEScanCallback();
        bleScanInProgress = false;
        btAdapter = configureBluetoothAdapter();
        btGatt = null;
        btGattCallback = configureBluetoothGattCallback();
        btSerialIface = ChameleonSettings.getBluetoothIOInterface();
        isConnected = false;
        BluetoothGattConnector.btDevicePinDataBytes = getStoredBluetoothDevicePinData();
    }

    public void setBluetoothSerialInterface(BluetoothBLEInterface btLocalSerialIface) {
        btSerialIface = btLocalSerialIface;
    }

    public void receiveBroadcastIntent(@NonNull Intent bcIntent) {
        if (btConnReceiver != null) {
            btConnReceiver.onReceive(btSerialContext, bcIntent);
        }
    }

    @SuppressLint("MissingPermission")
    private BroadcastReceiver configureBluetoothConnectionReceiver() {
        final BluetoothGattConnector btGattConnFinal = this;
        btConnReceiver = new BroadcastReceiver() {
            final BluetoothGattConnector btGattConn = btGattConnFinal;
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                AndroidLog.i(TAG, "btConnReceiver: intent action: " + action);
                BluetoothDevice btIntentDevice = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
                if (btIntentDevice == null) {
                    return;
                }
                String btDeviceName = btIntentDevice.getName();
                AndroidLog.i(TAG, "btConnReceiver: intent device name: " + btDeviceName);
                if (!isChameleonDeviceName(btDeviceName)) {
                    return;
                } else if (action.equals(BluetoothDevice.ACTION_FOUND) || action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                    /* NOTE: See https://developer.android.com/reference/android/bluetooth/BluetoothDevice#EXTRA_RSSI */
                    btDevice = btIntentDevice;
                    String userConnInstMsg = String.format(BuildConfig.DEFAULT_LOCALE, "New %s %s found.\n%s.",
                            btDevice.getName(), btDevice.getAddress(),
                            btSerialContext.getApplicationContext().getResources().getString(R.string.bluetoothExtraConfigInstructions));
                    Utils.displayToastMessageLong(userConnInstMsg);
                    final long shortPauseDuration = 1500L, threadSleepInterval = 50L;
                    for (long threadSleepTime = 0L; threadSleepTime <= shortPauseDuration; threadSleepTime += threadSleepInterval) {
                        try {
                            Thread.sleep(threadSleepInterval);
                        } catch (InterruptedException ie) {
                            AndroidLog.printStackTrace(ie);
                            break;
                        }
                    }
                    if (btDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        btDevice.createBond();
                    } else {
                        btGattConn.requestConnectionPriority(BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
                        btDevice.connectGatt(btGattConn.btSerialContext, true, btGattConn);
                    }
                    return;
                }
                int intentExtraState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE, -1);
                int intentExtraBondState = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE, -1);
                if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST) ||
                           (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED) ||
                           (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && intentExtraBondState == BluetoothDevice.BOND_BONDED) ||
                           (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED)) {
                    btDevice = btIntentDevice;
                    btGattConn.requestConnectionPriority(BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
                    btDevice.connectGatt(btGattConn.btSerialContext, true, btGattConn);
                }
            }
        };
        return btConnReceiver;
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
        bleScanner = btLocalAdapter.getBluetoothLeScanner();
        return btLocalAdapter;
    }

    private BluetoothGattCallback configureBluetoothGattCallback() {
        final BluetoothGattConnector btGattConnectorRefFinal = this;
        BluetoothGattCallback btLocalGattCallback = new BluetoothGattCallback() {
            final BluetoothGattConnector btGattConnectorRef = btGattConnectorRefFinal;

            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onConnectionStateChange: error/status code %d = %04x", status, status));
                }
                if (status == BLUETOOTH_GATT_ERROR || status == BLUETOOTH_GATT_RSP_WRITE || status == BLUETOOTH_GATT_RSP_EXEC_WRITE) {
                    return;
                }
                if (newState == BluetoothGatt.STATE_DISCONNECTED || newState == BluetoothGatt.STATE_DISCONNECTING) {
                    disconnectDevice();
                    startConnectingDevices();
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                    btGatt = gatt;
                    try {
                        btGatt.discoverServices();
                    } catch (SecurityException se) {
                        AndroidLog.printStackTrace(se);
                    }
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onServicesDiscovered: error/status code %d = %04x", status, status));
                    return;
                }
                stopConnectingDevices();
                try {
                    BluetoothDevice btDev = gatt.getDevice();
                    btGattConnectorRef.setBluetoothDevice(btDev);
                    String bleDeviceName = btDev.getName();
                    AndroidLog.i(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "BLE Device: %s @ %s", bleDeviceName, btDev.getAddress()));
                    List<BluetoothGattService> svcUUIDList = gatt.getServices();
                    StringBuilder svcUUIDSummary = new StringBuilder("SERVICE UUID LIST:\n");
                    for (BluetoothGattService svcUuid : svcUUIDList) {
                        svcUUIDSummary.append(String.format(BuildConfig.DEFAULT_LOCALE, "   > %s\n", svcUuid.getUuid().toString()));
                    }
                    AndroidLog.i(TAG, svcUUIDSummary.toString());
                    btGattConnectorRef.stopConnectingDevices();
                    int chameleonConnDeviceType = btGattConnectorRef.getChameleonDeviceType();
                    if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINY) {
                        btGattConnectorRef.chameleonDeviceBLEService = CHAMELEON_REVG_TINY_UART_SERVICE_UUID;
                        btGattConnectorRef.chameleonDeviceBLESendChar = CHAMELEON_REVG_TINY_SEND_CHAR_UUID;
                        btGattConnectorRef.chameleonDeviceBLERecvChar = CHAMELEON_REVG_TINY_RECV_CHAR_UUID;
                    } else if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINYPRO) {
                        btGattConnectorRef.chameleonDeviceBLEService = CHAMELEON_REVG_TINY_UART_SERVICE_UUID;
                        btGattConnectorRef.chameleonDeviceBLESendChar = CHAMELEON_REVG_TINY_SEND_CHAR_UUID;
                        btGattConnectorRef.chameleonDeviceBLERecvChar = CHAMELEON_REVG_TINY_RECV_CHAR_UUID;
                    } else if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG) {
                        btGattConnectorRef.chameleonDeviceBLEService = CHAMELEON_REVG_UART_SERVICE_UUID;
                        btGattConnectorRef.chameleonDeviceBLESendChar = CHAMELEON_REVG_SEND_CHAR_UUID;
                        btGattConnectorRef.chameleonDeviceBLERecvChar = CHAMELEON_REVG_RECV_CHAR_UUID;
                    } else {
                        return;
                    }
                    btGattConnectorRef.btGatt = gatt;
                    BluetoothGattService txDataService = btGattConnectorRef.btGatt.getService(btGattConnectorRef.chameleonDeviceBLECtrlChar.getUuid());
                    BluetoothGattCharacteristic sendChar = txDataService.getCharacteristic(btGattConnectorRef.chameleonDeviceBLESendChar.getUuid());
                    btGattConnectorRef.btGatt.setCharacteristicNotification(sendChar, true);
                    BluetoothGattDescriptor sendCharDesc = sendChar.getDescriptor(CHAMELEON_REVG_RECV_DESC_UUID.getUuid());
                    sendCharDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(sendCharDesc);
                    gatt.executeReliableWrite();
                } catch (SecurityException seNPE) {
                    AndroidLog.printStackTrace(seNPE);
                    startConnectingDevices();
                }
                if (configureNotifyOnSerialBluetoothService(btGattConnectorRef.btGatt, btGattConnectorRef.chameleonDeviceBLERecvChar.getUuid().toString())) {
                    btGattConnectorRef.btSerialIface.configureSerialConnection(btGattConnectorRef.btDevice);
                    notifyBluetoothBLEDeviceConnected(btDevice);
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
                    AndroidLog.i(TAG, "onDescriptorWrite: status BLUETOOTH_GATT_ERROR -- TODO");
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
                try {
                    gatt.readCharacteristic(characteristic);
                } catch (SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }
                byte[] charData = characteristic.getValue();
                if (charData == null) {
                    AndroidLog.d(TAG, "read characteristic: <null>");
                    return;
                } else {
                    AndroidLog.d(TAG, "read characteristic: " + new String(charData));
                }
                bleReadLock.release();
                try {
                    notifyBluetoothSerialInterfaceDataRead(charData);
                } catch (Exception dinvEx) {
                    AndroidLog.printStackTrace(dinvEx);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BLUETOOTH_GATT_ERROR) {
                    /* ??? TODO: Should we return bytes that correspond to a Chameleon terminal error status ??? */
                    AndroidLog.i(TAG, "onCharacteristicRead: status BLUETOOTH_GATT_ERROR -- TODO");
                    bleReadLock.release();
                    return;
                }
                AndroidLog.d(TAG, "onCharacteristicRead" + characteristic.toString());
                byte[] charData = characteristic.getValue();
                if (charData == null) {
                    AndroidLog.d(TAG, "read characteristic: <null>");
                    return;
                } else {
                    AndroidLog.d(TAG, "read characteristic: " + new String(charData));
                }
                bleReadLock.release();
                try {
                    notifyBluetoothSerialInterfaceDataRead(charData);
                } catch (Exception dinvEx) {
                    AndroidLog.printStackTrace(dinvEx);
                }
            }

        };
        return btLocalGattCallback;
    }

    @SuppressLint("MissingPermission")
    private ScanCallback configureBLEScanCallback() {
        final BluetoothGattConnector btGattConnectorRefFinal = this;
        ScanCallback bleLocalScanCallback = new ScanCallback() {
            final BluetoothGattConnector btGattConnectorRef = btGattConnectorRefFinal;
            @Override
            public void onScanResult(int callbackType, ScanResult scanResultData) {
                if (callbackType == ScanSettings.CALLBACK_TYPE_MATCH_LOST) {
                    return;
                }
                LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String bleDeviceNameNone = "<No-Device-Name>";
                        String bleDeviceName;
                        try {
                            BluetoothDevice btDev = scanResultData.getDevice();
                            bleDeviceName = scanResultData.getScanRecord().getDeviceName();
                            AndroidLog.i(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "BLE Device: %s @ %s WITH TxPower %d and RSSI %d", bleDeviceName, btDev.getAddress(), scanResultData.getTxPower(), scanResultData.getRssi()));
                            AndroidLog.d(TAG, "ASSOCIATED FULL SCAN RECORD DATA:\n" + scanResultData.getScanRecord().toString());
                            List<ParcelUuid> svcUUIDList = scanResultData.getScanRecord().getServiceUuids();
                            StringBuilder svcUUIDSummary = new StringBuilder("SERVICE UUID LIST:\n");
                            for (ParcelUuid svcUuid : svcUUIDList) {
                                svcUUIDSummary.append(String.format(BuildConfig.DEFAULT_LOCALE, "   > %s\n", svcUuid.getUuid().toString()));
                            }
                            AndroidLog.i(TAG, svcUUIDSummary.toString());
                            SparseArray<byte[]> bleDeviceManuDataRaw = scanResultData.getScanRecord().getManufacturerSpecificData();
                            StringBuilder bleDeviceManuDataSummary = new StringBuilder("BLE DEVICE MANUFACTURER DATA:\n");
                            for (int saIndex = 0; saIndex < bleDeviceManuDataRaw.size(); saIndex++) {
                                byte[] mdataBytesAtIndex = bleDeviceManuDataRaw.valueAt(saIndex);
                                bleDeviceManuDataSummary.append(String.format(BuildConfig.DEFAULT_LOCALE, "   > %s [%s]\n", Utils.bytes2Hex(mdataBytesAtIndex), Utils.bytes2Ascii(mdataBytesAtIndex)));
                            }
                            AndroidLog.d(TAG, bleDeviceManuDataSummary.toString());
                            if (scanResultData.isConnectable()) {
                                btGattConnectorRef.stopConnectingDevices();
                                if (!btConnRecvRegistered) {
                                    btGattConnectorRef.registerBluetoothConnectionReceiver();
                                }
                                int chameleonConnDeviceType = btGattConnectorRef.getChameleonDeviceType();
                                if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINY) {
                                    btGattConnectorRef.chameleonDeviceBLEService = CHAMELEON_REVG_TINY_UART_SERVICE_UUID;
                                    btGattConnectorRef.chameleonDeviceBLESendChar = CHAMELEON_REVG_TINY_SEND_CHAR_UUID;
                                    btGattConnectorRef.chameleonDeviceBLERecvChar = CHAMELEON_REVG_TINY_RECV_CHAR_UUID;
                                } else if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINYPRO) {
                                    btGattConnectorRef.chameleonDeviceBLEService = CHAMELEON_REVG_TINY_UART_SERVICE_UUID;
                                    btGattConnectorRef.chameleonDeviceBLESendChar = CHAMELEON_REVG_TINY_SEND_CHAR_UUID;
                                    btGattConnectorRef.chameleonDeviceBLERecvChar = CHAMELEON_REVG_TINY_RECV_CHAR_UUID;
                                } else if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG) {
                                    btGattConnectorRef.chameleonDeviceBLEService = CHAMELEON_REVG_UART_SERVICE_UUID;
                                    btGattConnectorRef.chameleonDeviceBLESendChar = CHAMELEON_REVG_SEND_CHAR_UUID;
                                    btGattConnectorRef.chameleonDeviceBLERecvChar = CHAMELEON_REVG_RECV_CHAR_UUID;
                                } else {
                                    return;
                                }
                                if (configureNotifyOnSerialBluetoothService(null, chameleonDeviceBLERecvChar.getUuid().toString())) {
                                    btSerialIface.configureSerialConnection(btDevice);
                                    notifyBluetoothBLEDeviceConnected(btDevice);
                                }
                            }
                        } catch (Exception seNPE) {
                            bleDeviceName = bleDeviceNameNone;
                            AndroidLog.printStackTrace(seNPE);
                        }
                        AndroidLog.i(TAG, "BLE device with name " + bleDeviceName + "scanned");
                    }
                });
            }
        };
        return bleLocalScanCallback;
    }

    private void registerBluetoothConnectionReceiver() {
        if (btConnRecvRegistered) {
            return;
        }
        btConnReceiver = configureBluetoothConnectionReceiver();
        IntentFilter btConnectIntentFilter = new IntentFilter();
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        btConnectIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        btConnectIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        btConnectIntentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        btConnectIntentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        btSerialContext.registerReceiver(btConnReceiver, btConnectIntentFilter);
        btConnRecvRegistered = true;
    }

    public boolean isDeviceConnected() {
        return isConnected;
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
                if (bleScanner != null && bleScanInProgress) {
                    bleScanner.stopScan(bleScanCallback);
                    bleScanner.flushPendingScanResults(bleScanCallback);
                }
            } catch(Exception excpt) {
                AndroidLog.printStackTrace(excpt);
            }
            bleScanInProgress = false;
            try {
                if (btConnRecvRegistered) {
                    btSerialContext.unregisterReceiver(btConnReceiver);
                }
            } catch(Exception excpt) {
                AndroidLog.printStackTrace(excpt);
            }
            btConnRecvRegistered = false;
            isConnected = false;
            bleReadLock.release();
            bleWriteLock.release();
            return true;
        }
        return false;
    }

    private void setBluetoothDevice(BluetoothDevice btDevLocal) {
        btDevice = btDevLocal;
    }

    public boolean isChameleonDeviceName(String devName) {
        if (devName == null) {
            return false;
        }
        return devName.equals(CHAMELEON_REVG_NAME) || devName.equals(CHAMELEON_REVG_NAME_ALTERNATE) || devName.equals(CHAMELEON_REVG_TINY_NAME);
    }

    @SuppressLint("MissingPermission")
    public int getChameleonDeviceType() {
        try {
            String btDeviceName = btDevice.getName();
            if (btDeviceName.equals(CHAMELEON_REVG_TINY_NAME)) {
                return ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINY;
            } else if (btDeviceName.equals(CHAMELEON_REVG_NAME) || btDeviceName.equals(CHAMELEON_REVG_NAME_ALTERNATE)) {
                return ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG;
            }
        } catch (Exception excpt) {
            AndroidLog.printStackTrace(excpt);
        }
        return ChameleonIO.CHAMELEON_TYPE_UNKNOWN;
    }

    @SuppressLint("MissingPermission")
    public boolean startConnectingDevices() {
        if(!btPermsObtained) {
            btPermsObtained = btSerialIface.isBluetoothEnabled(true);
        }
        if(btPermsObtained) {
            registerBluetoothConnectionReceiver();
            try {
                if (btAdapter != null && !btAdapter.isDiscovering()) {
                    btAdapter.startDiscovery();
                }
            } catch(SecurityException se) {
                AndroidLog.printStackTrace(se);
                return false;
            }
            return startBLEDeviceScan();
        }
        return false;
    }

    private static int bleScanMode = ScanSettings.SCAN_MODE_LOW_LATENCY;
    private static int bleMatchMode = ScanSettings.MATCH_MODE_AGGRESSIVE;
    private static final boolean bleUseLegacyAdvertisements = false;
    private static final long bleScanReportDelay = 5000;

    /*
     * TODO: Add a drop down to the BT configuration layout to let the user choose how much power
     *       to drain from the Android device to enumerate BT.
     */

    public static void setLowPowerBLEScanSettings() {
        bleScanMode = ScanSettings.SCAN_MODE_LOW_POWER;
        bleMatchMode = ScanSettings.MATCH_MODE_STICKY;
    }

    public static void setBalancedPowerBLEScanSettings() {
        bleScanMode = ScanSettings.SCAN_MODE_BALANCED;
        bleMatchMode = ScanSettings.MATCH_MODE_AGGRESSIVE;
    }

    public static void setHighPowerBLEScanSettings() {
        bleScanMode = ScanSettings.SCAN_MODE_LOW_LATENCY;
        bleMatchMode = ScanSettings.MATCH_MODE_AGGRESSIVE;
    }

    public boolean startBLEDeviceScan() {
        btPermsObtained = btPermsObtained || btSerialIface.isBluetoothEnabled(true);
        if  (btPermsObtained && bleScanner != null && !bleScanInProgress) {
            ScanSettings bleScanSettings = new ScanSettings.Builder()
                    .setScanMode(bleScanMode)
                    .setLegacy(bleUseLegacyAdvertisements)
                    .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                    .setReportDelay(bleScanReportDelay)
                    .setMatchMode(bleMatchMode)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .build();
            ScanFilter chameleonRevGDeviceFilter = new ScanFilter.Builder()
                    .setDeviceName(CHAMELEON_REVG_NAME)
                    .build();
            ScanFilter chameleonRevGTinyProDeviceFilter = new ScanFilter.Builder()
                    .setDeviceName(CHAMELEON_REVG_NAME_ALTERNATE)
                    .build();
            ScanFilter chameleonRevGTinyDeviceFilter = new ScanFilter.Builder()
                    .setDeviceName(CHAMELEON_REVG_TINY_NAME)
                    .build();
            ScanFilter chameleonCtrlServiceFilter = new ScanFilter.Builder()
                    .setServiceUuid(CHAMELEON_REVG_TINY_UART_SERVICE_UUID)
                    .build();
            List<ScanFilter> bleScanFilters = new ArrayList<ScanFilter>();
            bleScanFilters.add(chameleonRevGDeviceFilter);
            bleScanFilters.add(chameleonRevGTinyProDeviceFilter);
            bleScanFilters.add(chameleonRevGTinyDeviceFilter);
            bleScanFilters.add(chameleonCtrlServiceFilter);
            try {
                bleScanner.startScan(bleScanFilters, bleScanSettings, bleScanCallback);
                bleScanInProgress = true;
            } catch(SecurityException se) {
                AndroidLog.printStackTrace(se);
                return false;
            }
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public boolean stopConnectingDevices() {
        if(!btPermsObtained) {
            btPermsObtained = btSerialIface.isBluetoothEnabled(false);
        }
        boolean status = true;
        if(btPermsObtained && btAdapter != null) {
            try {
                if (btAdapter.isDiscovering()) {
                    btAdapter.cancelDiscovery();
                }
                if (bleScanner != null && bleScanInProgress) {
                    bleScanner.stopScan(bleScanCallback);
                    bleScanner.flushPendingScanResults(bleScanCallback);
                }
            } catch(SecurityException se) {
                AndroidLog.printStackTrace(se);
                status = false;
            }
            bleScanInProgress = false;
            if (btConnRecvRegistered) {
                try {
                    btSerialContext.unregisterReceiver(btConnReceiver);
                } catch (Exception excpt) {
                    AndroidLog.printStackTrace(excpt);
                }
            }
            btConnRecvRegistered = false;
            btConnReceiver = null;
        }
        return status;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        btGattCallback.onConnectionStateChange(gatt, status, newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        btGattCallback.onServicesDiscovered(gatt, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        btGattCallback.onDescriptorWrite(gatt, descriptor, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        btGattCallback.onMtuChanged(gatt, mtu, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        btGattCallback.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        btGattCallback.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        btGattCallback.onCharacteristicRead(gatt, characteristic, status);
    }

    @SuppressLint("MissingPermission")
    public void notifyBluetoothBLEDeviceConnected(@NonNull BluetoothDevice btLocalDevice) {
        isConnected = true;
        Intent notifyMainActivityIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(notifyMainActivityIntent);
        btSerialIface.configureSerialConnection(btLocalDevice);
    }

    public void notifyBluetoothSerialInterfaceDataRead(byte[] serialDataRead) {
        if(btSerialIface != null && serialDataRead != null) {
            btSerialIface.onReceivedData(serialDataRead);
        }
    }

    @SuppressLint("MissingPermission")
    private boolean configureNotifyOnSerialBluetoothService(BluetoothGatt btLocalGatt, String gattUUID) {
        BluetoothGattService btgService = null;
        if (btLocalGatt == null) {
            btgService = new BluetoothGattService(chameleonDeviceBLEService.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        } else {
            btgService = btLocalGatt.getService(chameleonDeviceBLEService.getUuid());
        }
        if (btgService == null) {
            AndroidLog.i(TAG, "configureNotifyOnSerialBluetoothService: Unable to obtain BLE service");
            return false;
        }
        BluetoothGattCharacteristic btgChar = btgService.getCharacteristic(UUID.fromString(gattUUID));
        if (btgChar == null) {
            AndroidLog.i(TAG, "configureNotifyOnSerialBluetoothService: Unable to obtain charactristic " + gattUUID);
            return false;
        }
        try {
            btLocalGatt.setCharacteristicNotification(btgChar, true);
            BluetoothGattDescriptor recvDesc = btgChar.getDescriptor(CHAMELEON_REVG_RECV_DESC_UUID.getUuid());
            recvDesc.setValue(BLUETOOTH_GATT_ENABLE_NOTIFY_PROP);
            btLocalGatt.writeDescriptor(recvDesc);
            btLocalGatt.executeReliableWrite();
        } catch(SecurityException se) {
            AndroidLog.printStackTrace(se);
            return false;
        }
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
        /* ??? TODO: Need to wrap the byte buffer with checksums and/or control characters ??? */
        AndroidLog.i(TAG, "write: " + Utils.bytes2Hex(dataBuf));
        if (dataBuf.length > BLUETOOTH_LOCAL_MTU_THRESHOLD) {
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattService txDataService = null;
        if (btGatt == null) {
            txDataService = new BluetoothGattService(chameleonDeviceBLEService.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        } else {
            txDataService = btGatt.getService(chameleonDeviceBLECtrlChar.getUuid());
        }
        if(txDataService == null) {
            AndroidLog.i(TAG, "write: Unable to obtain BLE service");
            disconnectDevice();
            startConnectingDevices();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattCharacteristic btGattChar = txDataService.getCharacteristic(chameleonDeviceBLESendChar.getUuid());
        if(btGattChar == null) {
            AndroidLog.i(TAG, "write: Unable to obtain charactristic " + chameleonDeviceBLESendChar.getUuid().toString());
            disconnectDevice();
            startConnectingDevices();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        btGattChar.setValue(dataBuf);
        try {
            if (bleWriteLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                btGatt.writeCharacteristic(btGattChar);
                btGatt.executeReliableWrite();
            } else {
                AndroidLog.w(TAG, "Cannot acquire BT BLE read lock for operation");
                return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
            }
        } catch(SecurityException se) {
            AndroidLog.printStackTrace(se);
            return ChameleonSerialIOInterface.STATUS_ERROR;
        } catch(InterruptedException ie) {
            AndroidLog.printStackTrace(ie);
            return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
        }
        return ChameleonSerialIOInterface.STATUS_OK;
    }

    @SuppressLint("MissingPermission")
    public int read() throws IOException {
        /* ??? TODO: Big or little endian byte order of the results returned (Chameleon Mini AVR is LE) ??? */
        BluetoothGattService rxDataService = null;
        if (btGatt == null) {
            rxDataService = new BluetoothGattService(chameleonDeviceBLEService.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        } else {
            rxDataService = btGatt.getService(chameleonDeviceBLEService.getUuid());
        }
        if(rxDataService == null) {
            disconnectDevice();
            startConnectingDevices();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattCharacteristic btGattChar = rxDataService.getCharacteristic(chameleonDeviceBLERecvChar.getUuid());
        if(btGattChar == null) {
            AndroidLog.i(TAG, "read: Unable to obtain charactristic " + chameleonDeviceBLERecvChar.getUuid().toString());
            disconnectDevice();
            startConnectingDevices();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        try {
            if (bleReadLock.tryAcquire(BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                btGatt.readCharacteristic(btGattChar);
            } else {
                AndroidLog.w(TAG, "Cannot acquire BT BLE read lock for operation");
                return ChameleonSerialIOInterface.STATUS_RESOURCE_UNAVAILABLE;
            }
        } catch(SecurityException se) {
            AndroidLog.printStackTrace(se);
            return ChameleonSerialIOInterface.STATUS_ERROR;
        } catch(InterruptedException ie) {
            AndroidLog.printStackTrace(ie);
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
