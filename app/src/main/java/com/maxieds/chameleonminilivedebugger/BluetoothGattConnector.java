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
     * OBSERVATIONS / TROUBLESHOOTING NOTES:
     *     The proprietary Proxgrind / RRG application does something unusual the
     *     first time it tries to connect to the Chameleon over BT when the
     *     Chameleon device is reconnected to power via USB after the battery has
     *     completely lost charge:
     *          > The user is instructed to press and hold button 'A' for at least
     *            15 seconds while this initial connection is made.
     *          > The step is skipped upon attempts at BT reconnection so long as the
     *            device has not lost power (disconnected from wired USB, or a
     *            dead integrated rechargeable battery inside the Tiny series device)
     *          > Not easy to find out whether a secret PIN is exchanged during the initial
     *            button press period because the BT connection is relinquished by the
     *            RRG brand application every time the app is minimized.
     *            [The only way to see an active PIN string for a connected BT device on
     *             Android OS is to open the system Settings app and navigate to
     *            'Connected devices -> ChameleonDeviceName -> Settings (icon)'
     *             and then inspect the live settings that are active for the device.]
     */

    /*
     * NOTE: More Android Bluetooth BLE documentation available here:
     *       https://punchthrough.com/android-ble-guide/
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
    private static final String BLUETOOTH_BOND_RECEIVER_ACTION = BluetoothDevice.ACTION_BOND_STATE_CHANGED;

    public static final byte[] BLUETOOTH_GATT_ENABLE_NOTIFY_PROP = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    public static final int BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
    public static final int BLUETOOTH_LOCAL_MTU_THRESHOLD = 244;
    public static final int BLUETOOTH_GATT_RSP_WRITE = 0x13;
    public static final int BLUETOOTH_GATT_RSP_EXEC_WRITE = 0x19;
    public static final int BLUETOOTH_GATT_ERROR = 0x85;

    private boolean btPermsObtained;
    private boolean btBondRecvRegistered;
    private boolean isConnected;
    private Context btSerialContext;
    private ParcelUuid chameleonDeviceBLEService;
    private ParcelUuid chameleonDeviceBLECtrlChar;
    private ParcelUuid chameleonDeviceBLESendChar;
    private ParcelUuid chameleonDeviceBLERecvChar;
    private BluetoothDevice btDevice;
    private int bleDeviceTxPower;
    private int bleDeviceRSSI;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt btGatt;
    private BluetoothGattCallback btGattCallback;
    private ScanCallback bleScanCallback;
    private BroadcastReceiver btBondReceiver;
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
        bleDeviceTxPower = 0;
        bleDeviceRSSI = 0;
        btBondReceiver = null;
        btPermsObtained = false;
        btBondRecvRegistered = false;
        bleScanner = null;
        btAdapter = configureBluetoothAdapter();
        btGatt = null;
        btGattCallback = configureBluetoothGattCallback();
        configureBLEScanCallback();
        btSerialIface = (BluetoothBLEInterface) ChameleonSettings.serialIOPorts[ChameleonSettings.BTIO_IFACE_INDEX];
        isConnected = false;
        BluetoothGattConnector.btDevicePinDataBytes = getStoredBluetoothDevicePinData();
    }

    public void setBluetoothSerialInterface(BluetoothBLEInterface btLocalSerialIface) {
        btSerialIface = btLocalSerialIface;
    }

    @SuppressLint("MissingPermission")
    private BroadcastReceiver configureBluetoothBondReceiver() {
        final BluetoothGattConnector btGattConnFinal = this;
        BroadcastReceiver btLocalBondReceiver = new BroadcastReceiver() {
            final BluetoothGattConnector btGattConn = btGattConnFinal;
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                AndroidLog.i(TAG, "btBondReceiver: intent action: " + action);
                if (action == null || !action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    return;
                }
                BluetoothDevice btIntentDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btIntentDevice != null) {
                    btDevice = btIntentDevice;
                }
                AndroidLog.i(TAG, "btBondReceiver: calling notifyBluetoothSerialInterfaceDeviceConnected");
                btIntentDevice.connectGatt(LiveLoggerActivity.getLiveLoggerInstance().getApplicationContext(), true, btGattConn);
                btGattConn.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                notifyBluetoothBLEDeviceConnected(btDevice);
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

            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    AndroidLog.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "onConnectionStateChange: error/status code %d = %04x", status, status));
                }
                if(status == BLUETOOTH_GATT_ERROR) {
                    return;
                } else if (status == BLUETOOTH_GATT_RSP_WRITE) {
                    return;
                } else if (status == BLUETOOTH_GATT_RSP_EXEC_WRITE) {
                    return;
                }
                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    requestConnectionPriority(BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
                    btGatt = gatt;
                    try {
                        btGatt.discoverServices();
                        BluetoothGattService txDataService = btGatt.getService(chameleonDeviceBLECtrlChar.getUuid());
                        BluetoothGattCharacteristic sendChar = txDataService.getCharacteristic(chameleonDeviceBLESendChar.getUuid());
                        btGatt.setCharacteristicNotification(sendChar, true);
                        BluetoothGattDescriptor sendCharDesc = sendChar.getDescriptor(CHAMELEON_REVG_RECV_DESC_UUID.getUuid());
                        sendCharDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        long startWriteTime = System.currentTimeMillis();
                        while (!gatt.writeDescriptor(sendCharDesc)) {
                            if (System.currentTimeMillis() >= startWriteTime + ChameleonIO.TIMEOUT) {
                                break;
                            }
                        }
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
                if (status == BLUETOOTH_GATT_ERROR) {
                    return;
                }
                List<BluetoothGattService> services = gatt.getServices();
                AndroidLog.i(TAG,"onServicesDiscovered" + services.toString());
                /* ??? TODO: Need to call gatt.readCharacteristic(services.get(1).getCharacteristics().get(0)); ??? */
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    configureNotifyOnSerialBluetoothService(gatt, chameleonDeviceBLERecvChar.toString());
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                bleWriteLock.release();
                if (status == BLUETOOTH_GATT_ERROR) {
                    return;
                }
                UUID activeLocalCharUUID = descriptor.getCharacteristic().getUuid();
                if(!activeLocalCharUUID.equals(CHAMELEON_REVG_CTRL_CHAR_UUID.getUuid())) {
                    configureNotifyOnSerialBluetoothService(gatt, chameleonDeviceBLERecvChar.toString());
                }
                AndroidLog.i(TAG, "onDescriptorWrite: [UUID] " + activeLocalCharUUID);
            }

            @SuppressLint("MissingPermission")
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
                bleReadLock.release();
                if (status == BLUETOOTH_GATT_ERROR) {
                    return;
                } else if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte[] charData = characteristic.getValue();
                    notifyBluetoothSerialInterfaceDataRead(charData);
                }
                AndroidLog.i(TAG,"onCharacteristicRead" + characteristic.toString());
                /*byte[] charData = characteristic.getValue();
                if (charData == null) {
                    AndroidLog.d(TAG, "read characteristic: <null>");
                    return;
                } else {
                    AndroidLog.d(TAG, "read characteristic: " + new String(charData));
                }
                try {
                    notifyBluetoothSerialInterfaceDataRead(charData);
                    // ??? TODO: Need to call gatt.disconnect() here ???
                } catch (Exception dinvEx) {
                    AndroidLog.printStackTrace(dinvEx);
                }*/
            }

        };
        return btLocalGattCallback;
    }

    private void registerBluetoothBondReceiver() {
        btBondReceiver = configureBluetoothBondReceiver();
        IntentFilter btBondSuccessIntentFilter = new IntentFilter(BLUETOOTH_BOND_RECEIVER_ACTION);
        btSerialContext.registerReceiver(btBondReceiver, btBondSuccessIntentFilter);
        btBondRecvRegistered = true;
    }

    public boolean isDeviceConnected() {
        return isConnected;
    }

    @SuppressLint("MissingPermission")
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
            bleDeviceRSSI = 0;
            bleDeviceTxPower = 0;
            btBondRecvRegistered = false;
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

    public int getTxPower() {
        return bleDeviceTxPower;
    }

    private void setTxPower(int txPower) {
        bleDeviceTxPower = txPower;
    }

    private void setRSSI(int rssi) {
        bleDeviceRSSI = rssi;
    }

    public int getRSSI() {
        return bleDeviceRSSI;
    }

    @SuppressLint("MissingPermission")
    public int getChameleonDeviceType() {
        try {
            String btDeviceName = btDevice.getName();
            if (btDeviceName.equals(CHAMELEON_REVG_TINY_NAME)) {
                /* ??? TODO: Can we distinguish between the Chameleon Tiny and TinyPro devices ??? */
                return ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINY;
            } else if (btDeviceName.equals(CHAMELEON_REVG_NAME) || btDeviceName.equals(CHAMELEON_REVG_NAME_ALTERNATE)) {
                return ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG;
            }
        } catch(Exception excpt) {
            AndroidLog.printStackTrace(excpt);
        }
        return ChameleonIO.CHAMELEON_TYPE_UNKNOWN;
    }

    @SuppressLint("MissingPermission")
    private void configureBLEScanCallback() {
        final BluetoothGattConnector btGattConnectorRefFinal = this;
        bleScanCallback = new ScanCallback() {
            final BluetoothGattConnector btGattConnectorRef = btGattConnectorRefFinal;
            @Override
            public void onScanResult(int callbackType, ScanResult scanResultData) {
                LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String bleDeviceNameNone = "<No-Device-Name>";
                        String bleDeviceName;
                        try {
                            BluetoothDevice btDev = scanResultData.getDevice();
                            btGattConnectorRef.setBluetoothDevice(btDev);
                            bleDeviceName = scanResultData.getScanRecord().getDeviceName();
                            btGattConnectorRef.setTxPower(scanResultData.getTxPower());
                            btGattConnectorRef.setRSSI(scanResultData.getRssi());
                            AndroidLog.i(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "BLE Device: %s @ %s WITH TxPower %d and RSSI %d", bleDeviceName, btDev.getAddress(), scanResultData.getTxPower(), scanResultData.getRssi()));
                            AndroidLog.d(TAG, "ASSOCIATED FULL SCAN RECORD DATA:\n" + scanResultData.getScanRecord().toString());
                            List<ParcelUuid> svcUUIDList = scanResultData.getScanRecord().getServiceUuids();
                            String svcUUIDSummary = "SERVICE UUID LIST:\n";
                            for (ParcelUuid svcUuid : svcUUIDList) {
                                svcUUIDSummary += String.format(BuildConfig.DEFAULT_LOCALE, "   > %s\n", svcUuid.getUuid().toString());
                            }
                            AndroidLog.i(TAG, svcUUIDSummary);
                            SparseArray<byte[]> bleDeviceManuDataRaw = scanResultData.getScanRecord().getManufacturerSpecificData();
                            String bleDeviceManuDataSummary = "BLE DEVICE MANUFACTURER DATA:\n";
                            for (int saIndex = 0; saIndex < bleDeviceManuDataRaw.size(); saIndex++) {
                                byte[] mdataBytesAtIndex = bleDeviceManuDataRaw.valueAt(saIndex);
                                bleDeviceManuDataSummary += String.format(BuildConfig.DEFAULT_LOCALE, "   > %s [%s]\n", Utils.bytes2Hex(mdataBytesAtIndex), Utils.bytes2Ascii(mdataBytesAtIndex));
                            }
                            AndroidLog.d(TAG, bleDeviceManuDataSummary);
                            if (scanResultData.isConnectable()) {
                                btGattConnectorRef.stopConnectingDevices();
                                if (!btBondRecvRegistered) {
                                    btGattConnectorRef.registerBluetoothBondReceiver();
                                }
                                int chameleonConnDeviceType = btGattConnectorRef.getChameleonDeviceType();
                                if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINY) {
                                    chameleonDeviceBLEService = CHAMELEON_REVG_TINY_UART_SERVICE_UUID;
                                    chameleonDeviceBLESendChar = CHAMELEON_REVG_TINY_SEND_CHAR_UUID;
                                    chameleonDeviceBLERecvChar = CHAMELEON_REVG_TINY_RECV_CHAR_UUID;
                                } else if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINYPRO) {
                                    /* ??? TODO: Do these change from the Tiny to the TinyPro ??? */
                                    chameleonDeviceBLEService = CHAMELEON_REVG_TINY_UART_SERVICE_UUID;
                                    chameleonDeviceBLESendChar = CHAMELEON_REVG_TINY_SEND_CHAR_UUID;
                                    chameleonDeviceBLERecvChar = CHAMELEON_REVG_TINY_RECV_CHAR_UUID;
                                } else if (chameleonConnDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG) {
                                    chameleonDeviceBLEService = CHAMELEON_REVG_UART_SERVICE_UUID;
                                    chameleonDeviceBLESendChar = CHAMELEON_REVG_SEND_CHAR_UUID;
                                    chameleonDeviceBLERecvChar = CHAMELEON_REVG_RECV_CHAR_UUID;
                                } else {
                                    /* ??? Have we caught all possible cases above ??? */
                                    return;
                                }
                                btDev.createBond();
                                btGattConnectorRef.btSerialIface.configureSerialConnection(btDev);
                            }
                        } catch(SecurityException se) {
                            bleDeviceName = bleDeviceNameNone;
                            AndroidLog.printStackTrace(se);
                        } catch(NullPointerException npe) {
                            bleDeviceName = bleDeviceNameNone;
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
    }

    @SuppressLint("MissingPermission")
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
                    configureBLEScanCallback();
                    bleScanner.startScan(bleScanFilters, bleScanSettings, bleScanCallback);
                } catch(SecurityException se) {
                    AndroidLog.printStackTrace(se);
                }
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

    @SuppressLint("MissingPermission")
    public void notifyBluetoothBLEDeviceConnected(@NonNull BluetoothDevice btLocalDevice) {
        isConnected = true;
        Intent notifyMainActivityIntent = new Intent(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
        LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(notifyMainActivityIntent);
        btSerialIface.configureSerialConnection(btDevice);
    }

    public void notifyBluetoothSerialInterfaceDataRead(byte[] serialDataRead) {
        if(btSerialIface != null) {
            btSerialIface.onReceivedData(serialDataRead);
        }
    }

    @SuppressLint("MissingPermission")
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
        AndroidLog.i(TAG, "write: " + Utils.bytes2Hex(dataBuf));
        if (dataBuf.length > BLUETOOTH_LOCAL_MTU_THRESHOLD) {
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        if(btGatt == null) {
            disconnectDevice();
            return ChameleonSerialIOInterface.STATUS_ERROR;
        }
        BluetoothGattService txDataService = btGatt.getService(chameleonDeviceBLECtrlChar.getUuid());
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

    @SuppressLint("MissingPermission")
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

    /* ??? TODO: Remove this code ??? */
    public void setStoredBluetoothDevicePinData(@NonNull String btPinData) {
        AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE, AndroidSettingsStorage.BLUETOOTH_DEVICE_PIN_DATA);
        BluetoothGattConnector.btDevicePinDataBytes = btPinData.getBytes(StandardCharsets.UTF_8);
    }

}
