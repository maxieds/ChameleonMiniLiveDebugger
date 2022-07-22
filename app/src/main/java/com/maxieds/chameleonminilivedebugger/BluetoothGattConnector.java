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

    /**
     * OBSERVATIONS / TROUBLESHOOTING NOTES:
     *     The proprietary Proxgrind / RRG application does something unusual the
     *     first time it tries to connect to the Chameleon over BT when the
     *     Chameleon device is reconnected to power via USB after the battery has
     *     completely lost charge:
     *          > The user is instructed to press and hold button 'A' for at least
     *            15 seconds while this initial connection is made.
     *          > The step is skipped upon attempts at BT reconnection so long as the
     *            device has not lost power (disconnected from wired USB, or a
     *            dead integrated rechargeable battery inside the Tiny series devices)
     *          > Not easy to find out whether a secret PIN is exchanged during the initial
     *            button press period because the BT connection is relinquished by the
     *            RRG brand application every time the app is minimized.
     *            [The only way to see an active PIN string for a connected BT device on
     *             Android OS is to open the system Settings app and navigate to
     *            'Connected devices -> ChameleonDeviceName -> Settings (icon)'
     *             and then inspect the live settings that are active for the device.]
     */

    /**
     * NOTE: More BLE documentation at https://punchthrough.com/android-ble-guide/
     */

    /* TODO: Make sure that the BLE UUIDs are the same for both of the
     *       Proxgrind RevG Tiny and TinyPro devices
     */

    public static final String CHAMELEON_REVG_NAME = "BLE-Chameleon";
    public static final String CHAMELEON_REVG_NAME_ALTERNATE = "Chameleon";
    public static final String CHAMELEON_REVG_SERVICE_UUID_STRING = "51510001-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_SEND_CHAR_UUID_STRING = "51510002-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_CHAR_UUID_STRING = "51510003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_NAME = "ChameleonTiny";
    public static final String CHAMELEON_REVG_TINY_SERVICE_UUID_STRING = "51510001-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_SEND_CHAR_UUID_STRING = "51510002-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_RECV_CHAR_UUID_STRING = "51510003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_CTRL_CHAR_UUID_STRING = "52520003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_DESC_UUID_STRING = "00002902-0000-1000-8000-00805f9b34fb";

    private static final ParcelUuid CHAMELEON_REVG_SERVICE_UUID = ParcelUuid.fromString(CHAMELEON_REVG_SERVICE_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_SEND_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_SEND_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_RECV_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_RECV_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_TINY_SERVICE_UUID = ParcelUuid.fromString(CHAMELEON_REVG_TINY_SERVICE_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_TINY_SEND_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_TINY_SEND_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_TINY_RECV_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_TINY_RECV_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_CTRL_CHAR_UUID = ParcelUuid.fromString(CHAMELEON_REVG_CTRL_CHAR_UUID_STRING);
    private static final ParcelUuid CHAMELEON_REVG_RECV_DESC_UUID = ParcelUuid.fromString(CHAMELEON_REVG_RECV_DESC_UUID_STRING);

    private static final String BLUETOOTH_SYSTEM_SERVICE = Context.BLUETOOTH_SERVICE;
    private static final String BLUETOOTH_BOND_RECEIVER_ACTION = BluetoothDevice.ACTION_BOND_STATE_CHANGED;

    public static final byte[] BLUETOOTH_GATT_ENABLE_NOTIFY_PROP = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    public static final int BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
    public static final int BLUETOOTH_LOCAL_MTU_THRESHOLD = 244;

    public static final int BLUETOOTH_BLE_GATT_RSP_WRITE = 0x13;
    public static final int BLUETOOTH_BLE_GATT_RSP_EXEC_WRITE = 0x19;
    public static final int BLUETOOTH_BLE_GATT_ERROR = 0x85;

    private Context btSerialContext;
    private ParcelUuid chameleonDeviceBLEService;
    private ParcelUuid chameleonDeviceBLECtrlChar;
    private ParcelUuid chameleonDeviceBLESendChar;
    private ParcelUuid chameleonDeviceBLERecvChar;
    private BluetoothDevice btDevice;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt btGatt;
    private BluetoothGattCallback btGattCallback;
    private BroadcastReceiver btBondReceiver;
    private boolean btPermsObtained;
    private boolean btNotifyUARTService;
    private boolean btBondRecvRegistered;
    private BluetoothBLEInterface btSerialIface;
    private boolean isConnected;
    public static byte[] btDevicePinDataBytes = new byte[0];

    private static final long BLE_READ_WRITE_OPERATION_TRYLOCK_TIMEOUT = ChameleonIO.LOCK_TIMEOUT;

    private Semaphore bleReadLock = new Semaphore(1, true);
    private Semaphore bleWriteLock = new Semaphore(1, true);

    public BluetoothGattConnector(@NonNull Context localContext) {
        btSerialContext = localContext;
        chameleonDeviceBLEService = CHAMELEON_REVG_SERVICE_UUID;
        chameleonDeviceBLESendChar = CHAMELEON_REVG_SEND_CHAR_UUID;
        chameleonDeviceBLERecvChar = CHAMELEON_REVG_RECV_CHAR_UUID;
        chameleonDeviceBLECtrlChar = CHAMELEON_REVG_CTRL_CHAR_UUID;
        btDevice = null;
        btBondReceiver = null;
        btPermsObtained = false;
        btBondRecvRegistered = false;
        btNotifyUARTService = false;
        bleScanner = null;
        btAdapter = configureBluetoothAdapter();
        btGatt = null;
        btGattCallback = configureBluetoothGattCallback();
        btSerialIface = (BluetoothBLEInterface) ChameleonSettings.serialIOPorts[ChameleonSettings.BTIO_IFACE_INDEX];
        isConnected = false;
        BluetoothGattConnector.btDevicePinDataBytes = getStoredBluetoothDevicePinData();
    }

    public void setBluetoothSerialInterface(BluetoothBLEInterface btLocalSerialIface) {
        btSerialIface = btLocalSerialIface;
    }

    private BroadcastReceiver configureBluetoothBondReceiver() {
        BroadcastReceiver btLocalBondReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                AndroidLog.i(TAG, "btBondReceiver: intent action: " + action);
                if (action == null || !action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    return;
                }
                BluetoothDevice btIntentDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btIntentDevice != null) {
                    AndroidLog.i(TAG, "btBondReceiver: calling notifyBluetoothSerialInterfaceDeviceConnected");
                    notifyBluetoothBLEDeviceConnected(btIntentDevice);
                }
            }
        };
        return btLocalBondReceiver;
    }

    private BluetoothAdapter configureBluetoothAdapter() {
        BluetoothManager btManager = (BluetoothManager) btSerialContext.getSystemService(BLUETOOTH_SYSTEM_SERVICE);
        if (btManager == null) {
            return null;
        }
        BluetoothAdapter btLocalAdapter = btManager.getAdapter();
        if(btLocalAdapter != null) {
            bleScanner = btLocalAdapter.getBluetoothLeScanner();
        }
        return btLocalAdapter;
    }

    private BluetoothGattCallback configureBluetoothGattCallback() {
        BluetoothGattCallback btLocalGattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onConnectionStateChange: error/status code %d = %04x", status, status));
                }
                if(status == BLUETOOTH_BLE_GATT_ERROR) {
                    return;
                } else if (status == BLUETOOTH_BLE_GATT_RSP_WRITE) {
                    return;
                } else if (status == BLUETOOTH_BLE_GATT_RSP_EXEC_WRITE) {
                    return;
                }
                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    requestConnectionPriority(BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
                    btGatt = gatt;
                    try {
                        btGatt.discoverServices();
                    } catch(SecurityException se) {
                        AndroidLog.printStackTrace(se);
                    }
                }
                if(newState == BluetoothGatt.STATE_DISCONNECTED) {
                    disconnectDevice();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onServicesDiscovered: error/status code %d = %04x", status, status));
                }
                if (status == BLUETOOTH_BLE_GATT_ERROR) {
                    return;
                }
                List<BluetoothGattService> services = gatt.getServices();
                AndroidLog.i(TAG,"onServicesDiscovered" + services.toString());
                /*try {
                    gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
                } catch(SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }*/
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    configureNotifyOnSerialBluetoothService(gatt, chameleonDeviceBLERecvChar.toString());
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                bleWriteLock.release();
                if (status == BLUETOOTH_BLE_GATT_ERROR) {
                    return;
                }
                UUID activeLocalCharUUID = descriptor.getCharacteristic().getUuid();
                if(!activeLocalCharUUID.equals(CHAMELEON_REVG_CTRL_CHAR_UUID)) {
                    configureNotifyOnSerialBluetoothService(gatt, chameleonDeviceBLERecvChar.toString());
                }
                AndroidLog.i(TAG, "onDescriptorWrite: [UUID] " + activeLocalCharUUID);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
                try{
                    gatt.readCharacteristic(characteristic);
                } catch(SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }
                byte[] charData = characteristic.getValue();
                if (charData == null) {
                    AndroidLog.d(TAG, "read characteristic: <null>");
                    return;
                } else {
                    AndroidLog.d(TAG, "read characteristic: " + String.valueOf(charData));
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
                bleReadLock.release();
                if (status == BLUETOOTH_BLE_GATT_ERROR) {
                    return;
                } else if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte[] charData = characteristic.getValue();
                    notifyBluetoothSerialInterfaceDataRead(charData);
                }
                AndroidLog.i(TAG,"onCharacteristicRead" + characteristic.toString());
                byte[] charData = characteristic.getValue();
                if (charData == null) {
                    AndroidLog.d(TAG, "read characteristic: <null>");
                    return;
                } else {
                    AndroidLog.d(TAG, "read characteristic: " +String.valueOf(charData));
                }
                try {
                    notifyBluetoothSerialInterfaceDataRead(charData);
                } catch (Exception dinvEx) {
                    AndroidLog.printStackTrace(dinvEx);
                }
                /*try{
                    gatt.disconnect();
                } catch(SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }*/
            }

        };
        return btLocalGattCallback;
    }

    private void registerBluetoothBondReceiver() {
        btBondReceiver = configureBluetoothBondReceiver();
        btSerialContext.registerReceiver(btBondReceiver, new IntentFilter(BLUETOOTH_BOND_RECEIVER_ACTION));
        btBondRecvRegistered = true;
    }

    public boolean isDeviceConnected() {
        return isConnected;
    }

    public boolean disconnectDevice() {
        if(isDeviceConnected()) {
            btDevice = null;
            if(btGatt != null) {
                try {
                    btGatt.abortReliableWrite();
                    btGatt.disconnect();
                    btGatt.close();
                } catch(SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }
                btGatt = null;
            }
            btBondRecvRegistered = false;
            isConnected = false;
            bleReadLock.release();
            bleWriteLock.release();
            return true;
        }
        return false;
    }

    /* TODO: This is where we need to connect the device / gatt and stop the ongoing scan */
    private ScanCallback bleScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult scanResultData) {
                    LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String bleDeviceName = "<No-Device-Name>";
                            try {
                                bleDeviceName = scanResultData.getScanRecord().getDeviceName();
                                //bleDeviceName = scanResultData.getDevice().getAddress();
                                //scanResultData.isConnectable()
                                //scanResultData.getDevice().connectGatt()
                                //scanResultData.getRssi() // in dBm
                                //scanResultData.getTxPower() // in dBm
                                /*scanResultData.getScanRecord().toString();
                                scanResultData.getScanRecord().getDeviceName();
                                List<ParcelUuid> svcUUIDList = scanResultData.getScanRecord().getServiceUuids();
                                SparseArray<byte[]> saDeviceManuData = scanResultData.getScanRecord().getManufacturerSpecificData();
                                 */
                            } catch(SecurityException se) {
                                bleDeviceName = "<No-Device-Name>";
                                AndroidLog.printStackTrace(se);
                            } catch(NullPointerException npe) {
                                AndroidLog.printStackTrace(npe);
                            }
                            AndroidLog.i(TAG, "BLE device with name " + bleDeviceName + "scanned");
                        }
                    });
                }
                @Override
                public void onBatchScanResults(List<ScanResult> scanResultsLst) {
                    for(ScanResult scanRes : scanResultsLst) {
                        this.onScanResult(0, scanRes);
                    }
                }
            };

    public boolean startConnectingDevices() {
        if(!btPermsObtained) {
            btPermsObtained = btSerialIface.isBluetoothEnabled(false);
        }
        if(btPermsObtained) {
            registerBluetoothBondReceiver();
            try {
                btAdapter.startDiscovery();
            } catch(SecurityException se) {
                AndroidLog.printStackTrace(se);
            }
            if (bleScanner == null) {
                bleScanner = btAdapter.getBluetoothLeScanner();
            }
            if (bleScanner != null) {
                ScanSettings bleScanSettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                        .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                        .build();
                ScanFilter chameleonRevGDeviceFilter = new ScanFilter.Builder()
                        .setDeviceName(CHAMELEON_REVG_NAME)
                        .build();
                        //.setServiceUuid()
                ScanFilter chameleonRevGTinyProDeviceFilter = new ScanFilter.Builder()
                        .setDeviceName(CHAMELEON_REVG_NAME_ALTERNATE)
                        .build();
                ScanFilter chameleonRevGTinyDeviceFilter = new ScanFilter.Builder()
                        .setDeviceName(CHAMELEON_REVG_TINY_NAME)
                        .build();
                List<ScanFilter> bleScanFilters = new ArrayList<ScanFilter>();
                bleScanFilters.add(chameleonRevGDeviceFilter);
                bleScanFilters.add(chameleonRevGTinyProDeviceFilter);
                bleScanFilters.add(chameleonRevGTinyDeviceFilter);
                try {
                    bleScanner.startScan(bleScanFilters, bleScanSettings, bleScanCallback);
                } catch(SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }
            }
            return true;
        }
        return false;
    }

    public boolean stopConnectingDevices() {
        if(!btPermsObtained) {
            btPermsObtained = btSerialIface.isBluetoothEnabled(false);
        }
        if(btPermsObtained && btAdapter != null) {
            try {
                btAdapter.cancelDiscovery();
                if(bleScanner != null) {
                    bleScanner.stopScan(bleScanCallback);
                    bleScanner.flushPendingScanResults(bleScanCallback);
                }
            } catch(SecurityException se) {
                AndroidLog.printStackTrace(se);
                return false;
            }
            return true;
        } else {
            return false;
        }
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
        if (status != BluetoothGatt.GATT_SUCCESS) {
            AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onCharacteristicRead (%s): error/status code %d = %04x", characteristic.getUuid().toString(), status, status));
            return;
        }
        btGattCallback.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        btGattCallback.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onCharacteristicRead (%s): error/status code %d = %04x", characteristic.getUuid().toString(), status, status));
            return;
        }
        btGattCallback.onCharacteristicRead(gatt, characteristic, status);
    }

    public void notifyBluetoothBLEDeviceConnected(@NonNull BluetoothDevice btLocalDevice) {
        if(isDeviceConnected() || btNotifyUARTService || btAdapter == null) {
            return;
        }
        btDevice = btLocalDevice;
        stopConnectingDevices();
        String btDeviceName = "<NONE>";
        try {
            btDevice.connectGatt(btSerialContext, false, this);
            btDevice.createBond();
            btDeviceName = btDevice.getName();
        } catch(SecurityException se) {
            btDeviceName = "<NONE>";
            AndroidLog.printStackTrace(se);
        }
        AndroidLog.i(TAG, "BT Device Name: " + btDeviceName);
        if(btDeviceName != null && (btDeviceName.equals(CHAMELEON_REVG_NAME) || btDeviceName.equals(CHAMELEON_REVG_NAME_ALTERNATE))) {
            chameleonDeviceBLEService = CHAMELEON_REVG_SERVICE_UUID;
            chameleonDeviceBLESendChar = CHAMELEON_REVG_SEND_CHAR_UUID;
            chameleonDeviceBLERecvChar = CHAMELEON_REVG_RECV_CHAR_UUID;
        }
        else {
            chameleonDeviceBLEService = CHAMELEON_REVG_TINY_SERVICE_UUID;
            chameleonDeviceBLESendChar = CHAMELEON_REVG_TINY_SEND_CHAR_UUID;
            chameleonDeviceBLERecvChar = CHAMELEON_REVG_TINY_RECV_CHAR_UUID;
        }
        isConnected = true;
        Intent notifyMainActivityIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(notifyMainActivityIntent);
        btNotifyUARTService = true;
        btSerialIface.configureSerialConnection(btDevice);
    }

    public void notifyBluetoothSerialInterfaceDataRead(byte[] serialDataRead) {
        if(btSerialIface != null) {
            btSerialIface.onReceivedData(serialDataRead);
        }
    }

    private void configureNotifyOnSerialBluetoothService(BluetoothGatt btLocalGatt, String gattUUID) {
        AndroidLog.i(TAG, "configureNotifyOnSerialBluetoothService");
        BluetoothGattService btgService = btLocalGatt.getService(chameleonDeviceBLEService.getUuid());
        if (btgService == null) {
            return;
        }
        BluetoothGattCharacteristic btgChar = btgService.getCharacteristic(UUID.fromString(gattUUID));
        if (btgChar == null) {
            return;
        }
        try {
            btLocalGatt.setCharacteristicNotification(btgChar, true);
            BluetoothGattDescriptor descriptor = btgChar.getDescriptor(CHAMELEON_REVG_RECV_DESC_UUID.getUuid());
            descriptor.setValue(BLUETOOTH_GATT_ENABLE_NOTIFY_PROP);
            btLocalGatt.writeDescriptor(descriptor);
        } catch(SecurityException se) {
            AndroidLog.printStackTrace(se);
        }

    }

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

    public int write(byte[] dataBuf) throws IOException {
        AndroidLog.i(TAG, "write: " + Utils.bytes2Hex(dataBuf));
        if (dataBuf.length > BLUETOOTH_LOCAL_MTU_THRESHOLD) {
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        if(btGatt == null) {
            disconnectDevice();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattService txDataService = btGatt.getService(chameleonDeviceBLEService.getUuid());
        if(txDataService == null) {
            disconnectDevice();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattCharacteristic btGattChar = txDataService.getCharacteristic(chameleonDeviceBLESendChar.getUuid());
        if(btGattChar == null) {
            disconnectDevice();
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

    public int read() throws IOException {
        if(btGatt == null) {
            disconnectDevice();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattService rxDataService = btGatt.getService(chameleonDeviceBLEService.getUuid());
        if(rxDataService == null) {
            disconnectDevice();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattCharacteristic btGattChar = rxDataService.getCharacteristic(chameleonDeviceBLERecvChar.getUuid());
        if(btGattChar == null) {
            disconnectDevice();
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
