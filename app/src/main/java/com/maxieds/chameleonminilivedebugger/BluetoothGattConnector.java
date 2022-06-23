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
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BluetoothGattConnector extends BluetoothGattCallback {

    private static final String TAG = BluetoothGattConnector.class.getSimpleName();

    /**
     * OBSERVATIONS/TROUBLESHOOTING NOTES:
     *               The proprietary Proxgrind/RRG application does something unusual the
     *               first time it tries to connect to the Chameleon over BT when the
     *               Chameleon device is reconnected to power via USB after the battery has
     *               completely lost charge:
     *               > The user is instructed to press and hold button 'A' for at least
     *                 15 seconds while this initial connection is made.
     *               > The step is skipped upon attempts at BT reconnection so long as the
     *                 device has not lost power (disconnected from wired USB, or a
     *                 dead integrated rechargable battery inside the Tiny series devices)
     *               > Not easy to find out whether a secret PIN is exchanged during the initial
     *                 button press period because the BT connection is relinquished by the
     *                 RRG brand application every time the app is minimized.
     *                 [The only way to see an active PIN string for a connected BT device on
     *                 Android OS is to open the system Settings app and navigate to
     *                 'Connected devices -> ChameleonDeviceName -> Settings (icon)'
     *                 and then inspect the live settings that are active for the device.]
     */

    public static final String CHAMELEON_REVG_NAME = "BLE-Chameleon";
    public static final String CHAMELEON_REVG_NAME_ALTERNATE = "Chameleon";
    public static final String CHAMELEON_REVG_SERVICE_UUID = "51510001-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_SEND_CHAR_UUID = "51510002-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_CHAR_UUID = "51510003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_NAME = "ChameleonTiny";
    public static final String CHAMELEON_REVG_TINY_SERVICE_UUID = "51510001-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_SEND_CHAR_UUID = "51510002-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_TINY_RECV_CHAR_UUID = "51510003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_CTRL_CHAR_UUID = "52520003-7969-6473-6f40-6b6f6c6c6957";
    public static final String CHAMELEON_REVG_RECV_DESC_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    private static final String BLUETOOTH_SYSTEM_SERVICE = Context.BLUETOOTH_SERVICE;
    private static final String BLUETOOTH_BOND_RECEIVER_ACTION = BluetoothDevice.ACTION_BOND_STATE_CHANGED;

    public static final byte[] BLUETOOTH_GATT_ENABLE_NOTIFY_PROP = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    public static final int BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
    public static final int BLUETOOTH_GATT_CONNECT_PRIORITY_BALANCED = BluetoothGatt.CONNECTION_PRIORITY_BALANCED;
    public static final int BLUETOOTH_GATT_CONNECT_PRIORITY_LOW_POWER = BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER;
    public static final int BLUETOOTH_GATT_WRITE_DESC_TIMEOUT = 2250;
    public static final int BLUETOOTH_LOCAL_MTU_THRESHOLD = 244;

    private Context btSerialContext;
    private String chameleonDeviceBLEService;
    private String chameleonDeviceBLESendChar;
    private String chameleonDeviceBLERecvChar;
    private BluetoothDevice btDevice;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt btGatt;
    private BluetoothGattCallback btGattCallback;
    private BroadcastReceiver btBondReceiver;
    private boolean btPermsObtained;
    private boolean btBondRecvRegistered;
    private boolean btNotifyUARTService;
    private BluetoothSerialInterface btSerialIface;
    private boolean isConnected;
    private BluetoothSerialInterface.ChameleonBluetoothDeviceState btDeviceState;
    public static byte[] btDevicePinDataBytes = new byte[0];

    public BluetoothGattConnector(@NonNull Context localContext) {
        btSerialContext = localContext;
        chameleonDeviceBLEService = CHAMELEON_REVG_SERVICE_UUID;
        chameleonDeviceBLESendChar = CHAMELEON_REVG_SEND_CHAR_UUID;
        chameleonDeviceBLESendChar = CHAMELEON_REVG_RECV_CHAR_UUID;
        btDevice = null;
        btBondReceiver = null;
        btPermsObtained = false;
        btBondRecvRegistered = false;
        btNotifyUARTService = false;
        bleScanner = null;
        btAdapter = configureBluetoothAdapter();
        btGatt = null;
        btGattCallback = configureBluetoothGattCallback();
        btSerialIface = (BluetoothSerialInterface) ChameleonSettings.serialIOPorts[ChameleonSettings.BTIO_IFACE_INDEX];
        isConnected = false;
        btDeviceState = BluetoothSerialInterface.ChameleonBluetoothDeviceState.BTDEV_STATE_DISCONNECTED;
        BluetoothGattConnector.btDevicePinDataBytes = getStoredBluetoothDevicePinData();
    }

    public void setBluetoothSerialInterface(BluetoothSerialInterface btLocalSerialIface) {
        btSerialIface = btLocalSerialIface;
    }

    private BroadcastReceiver configureBluetoothBondReceiver() {
        BroadcastReceiver btLocalBondReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i(TAG, "btBondReceiver: intent action: " + action);
                if (action == null || !action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    return;
                }
                BluetoothDevice btIntentDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btIntentDevice == null) {
                    return;
                }
                else {
                    Log.i(TAG, "btBondReceiver: calling notifyBluetoothSerialInterfaceDeviceConnected");
                    notifyBluetoothSerialInterfaceDeviceConnected(btIntentDevice);
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
                if(status == 19) {
                    /* Status code 19 is caused by loss of binding information ??? */
                    return;
                }
                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    requestConnectionPriority(BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
                    btGatt = gatt;
                    btGatt.discoverServices();
                }
                if(newState == BluetoothGatt.STATE_DISCONNECTED) {
                    disconnectDevice();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                List<BluetoothGattService> services = gatt.getServices();
                Log.i(TAG,"onServicesDiscovered" + services.toString());
                gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    configureNotifyOnSerialBluetoothService(gatt, chameleonDeviceBLERecvChar);
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                UUID activeLocalCharUUID = descriptor.getCharacteristic().getUuid();
                if(!activeLocalCharUUID.equals(UUID.fromString(CHAMELEON_REVG_CTRL_CHAR_UUID))) {
                    configureNotifyOnSerialBluetoothService(gatt, chameleonDeviceBLERecvChar);
                }
                Log.i(TAG, "onDescriptorWrite: [UUID] " + activeLocalCharUUID.toString());
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
                gatt.readCharacteristic(characteristic);
                Log.d(TAG, "read characteristic: " + characteristic.getValue().toString());
                byte[] charData = characteristic.getValue();
                if (charData == null) {
                    return;
                }
                try {
                    /* Unpack bytes ?!? */
                    notifyBluetoothSerialInterfaceDataRead(charData);
                } catch (Exception dinvEx) {
                    dinvEx.printStackTrace();
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte[] charData = characteristic.getValue();
                    notifyBluetoothSerialInterfaceDataRead(charData);
                }
                Log.i(TAG,"onCharacteristicRead" + characteristic.toString());
                Log.d(TAG, "onCharRead status: " + status);
                gatt.disconnect();
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
                btGatt.disconnect();
                btGatt.close();
                btGatt = null;
            }
            btBondRecvRegistered = false;
            isConnected = false;
            return true;
        }
        return false;
    }

    private ScanCallback bleScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult scanResultData) {
                    LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "BLE device with name " + scanResultData.getDevice().getName() + "scanned");
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
            btAdapter.startDiscovery();
            if (bleScanner == null) {
                bleScanner = btAdapter.getBluetoothLeScanner();
            }
            if (bleScanner != null) {
                ScanSettings bleScanSettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                List<ScanFilter> bleScanFilters = new ArrayList<ScanFilter>();
                bleScanner.startScan(bleScanFilters, bleScanSettings, bleScanCallback);
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
            btAdapter.cancelDiscovery();
            if(bleScanner != null) {
                bleScanner.stopScan(bleScanCallback);
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

    public void notifyBluetoothSerialInterfaceDeviceConnected(@NonNull BluetoothDevice btLocalDevice) {
        if(isDeviceConnected() || btNotifyUARTService || btAdapter == null) {
            return;
        }
        btDevice = btLocalDevice;
        stopConnectingDevices();
        btDevice.connectGatt(btSerialContext, false, this);
        btDevice.createBond();
        String btDeviceName = btDevice.getName();
        Log.i(TAG, "BT Device Name: " + btDeviceName);
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
        if(serialDataRead == null) {
            return;
        }
        if(btSerialIface != null) {
            btSerialIface.onReceivedData(serialDataRead);
        }
    }

    private void configureNotifyOnSerialBluetoothService(BluetoothGatt btLocalGatt, String gattUUID) {
        Log.i(TAG, "configureNotifyOnSerialBluetoothService");
        BluetoothGattService btgService = btLocalGatt.getService(UUID.fromString(chameleonDeviceBLEService));
        if (btgService == null) {
            return;
        }
        BluetoothGattCharacteristic btgChar = btgService.getCharacteristic(UUID.fromString(gattUUID));
        if (btgChar == null) {
            return;
        }
        btLocalGatt.setCharacteristicNotification(btgChar, true);
        BluetoothGattDescriptor descriptor = btgChar.getDescriptor(UUID.fromString(CHAMELEON_REVG_RECV_DESC_UUID));
        descriptor.setValue(BLUETOOTH_GATT_ENABLE_NOTIFY_PROP);
        btLocalGatt.writeDescriptor(descriptor);

    }

    public boolean requestConnectionPriority(int connectPrioritySetting) {
        if(btGatt == null) {
            return false;
        }
        return btGatt.requestConnectionPriority(connectPrioritySetting);
    }

    public int write(byte[] dataBuf) throws IOException {
        Log.i(TAG, "write: " + Utils.bytes2Hex(dataBuf));
        if (dataBuf.length > BLUETOOTH_LOCAL_MTU_THRESHOLD) {
            return -1;
        }
        if(btGatt == null) {
            Log.i(TAG, "write: btGatt == null!");
            disconnectDevice();
            return -1;
        }
        BluetoothGattService txDataService = btGatt.getService(UUID.fromString(chameleonDeviceBLEService));
        if(txDataService == null) {
            return -1;
        }
        BluetoothGattCharacteristic btGattChar = txDataService.getCharacteristic(UUID.fromString(chameleonDeviceBLESendChar));
        if(btGattChar == null) {
            return -1;
        }
        btGattChar.setValue(dataBuf);
        btGatt.writeCharacteristic(btGattChar);
        return 0;
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

    public BluetoothSerialInterface.ChameleonBluetoothDeviceState getState() {
        return btDeviceState;
    }

}
