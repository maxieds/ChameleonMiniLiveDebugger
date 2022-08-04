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
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.List;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothBroadcastReceiver.class.getSimpleName();

    private static BluetoothBroadcastReceiver activeReceiverInstance = null;
    private BluetoothGattConnector btGattConn;

    public static BluetoothBroadcastReceiver getActiveInstance() {
        return activeReceiverInstance;
    }

    public static BluetoothBroadcastReceiver initializeActiveInstance(BluetoothGattConnector bgc) {
        if (BluetoothBroadcastReceiver.getActiveInstance() == null) {
            activeReceiverInstance = new BluetoothBroadcastReceiver();
            activeReceiverInstance.btGattConn = bgc;
        }
        return BluetoothBroadcastReceiver.getActiveInstance();
    }

    public BluetoothBroadcastReceiver() {
        activeReceiverInstance = this;
        btGattConn = null;
    }

    private static final boolean PRINT_SERVICES_LIST_TO_LOG = false;
    private static final boolean PRINT_SERVICES_LIST_FULL = false;

    private void printServicesSummaryListToLog() {
        if (PRINT_SERVICES_LIST_TO_LOG) {
            List<BluetoothGattService> svcList = btGattConn.btGatt.getServices();
            StringBuilder svcUUIDSummary = new StringBuilder(" ==== \n");
            for (BluetoothGattService svc : svcList) {
                /**
                 * NOTE: BT service data types described in table here:
                 *       https://btprodspecificationrefs.blob.core.windows.net/assigned-numbers/Assigned%20Number%20Types/Format%20Types.pdf
                 * NOTE: BT GATT characteristic permissions and service type constants are defined here:
                 *       https://developer.android.com/reference/android/bluetooth/BluetoothGattCharacteristic
                 */
                if (!PRINT_SERVICES_LIST_FULL && !svc.getUuid().toString().equals(BluetoothGattConnector.BleUuidType.getUuidByType(BluetoothGattConnector.BleUuidType.UART_SERVICE_UUID).toString())) {
                    continue;
                }
                svcUUIDSummary.append(String.format(BuildConfig.DEFAULT_LOCALE, "   > SERVICE %s [type %02x]\n", svc.getUuid().toString(), svc.getType()));
                List<BluetoothGattCharacteristic> svcCharList = svc.getCharacteristics();
                for (BluetoothGattCharacteristic svcChar : svcCharList) {
                    svcUUIDSummary.append(String.format(BuildConfig.DEFAULT_LOCALE, "      -- SVC-CHAR %s\n", svcChar.getUuid().toString()));
                }
                svcUUIDSummary.append("\n");
            }
            AndroidLog.d(TAG, svcUUIDSummary.toString());
        }
    }

    private static final int DISCOVER_SVCS_ATTEMPT_COUNT = 5;
    private static final long CHECK_DISCOVER_SVCS_INTERVAL = 1000L;

    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        AndroidLog.d(TAG, "btConnReceiver: intent action: " + action);
        if (intent.getExtras() == null) {
            return;
        }
        BluetoothDevice btIntentDevice = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
        if (btIntentDevice == null) {
            return;
        }
        String btDeviceName = btIntentDevice.getName();
        AndroidLog.i(TAG, "btConnReceiver: intent device name: " + btDeviceName);
        if (btGattConn == null || !BluetoothUtils.isChameleonDeviceName(btDeviceName)) {
            return;
        } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            final short DEFAULT_BTDEV_RSSI = Short.MIN_VALUE;
            short btDeviceRSSI = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI, DEFAULT_BTDEV_RSSI);
            String rssiInfoStr = "";
            if (btDeviceRSSI != DEFAULT_BTDEV_RSSI) {
                rssiInfoStr = String.format(BuildConfig.DEFAULT_LOCALE, " at RSSI of %d dBm", btDeviceRSSI);
            }
            btGattConn.btDevice = btIntentDevice;
            String userConnInstMsg = String.format(BuildConfig.DEFAULT_LOCALE, "New %s %s found%s.",
                    btGattConn.btDevice.getName(), btGattConn.btDevice.getAddress(), rssiInfoStr);
            Utils.displayToastMessage(userConnInstMsg, Toast.LENGTH_SHORT);
            btGattConn.btDevice.createBond();
        }
        int intentExtraState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE, -1);
        int intentExtraBondState = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE, -1);
        AndroidLog.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "EXTRA BOND STATE: %d", intentExtraBondState));
        if ((action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED) ||
                (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && intentExtraBondState == BluetoothDevice.BOND_BONDED) ||
                (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED)) {
            btGattConn.btGatt = btGattConn.btDevice.connectGatt(btGattConn.btSerialContext, true, btGattConn);
            btGattConn.btGatt = btGattConn.configureGattDataConnection();
            btGattConn.stopBTDevicesFromAdapterPolling();
            btGattConn.stopRestartCancelledBTDiscRuntime();
            final Handler discoverSvcsHandler = new Handler(Looper.getMainLooper());
            Runnable discoverSvcsRunner = new Runnable() {
                int retryAttempts = 0;
                final BluetoothGattConnector btGattConnRef = btGattConn;
                @Override
                public void run() {
                    AndroidLog.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "Initializing BLE device service connections ... ATTEMPT #%d", retryAttempts + 1));
                    if (++retryAttempts >= DISCOVER_SVCS_ATTEMPT_COUNT) {
                        btGattConnRef.disconnectDevice();
                        btGattConnRef.stopConnectingDevices();
                        btGattConnRef.startConnectingDevices();
                    } else if (btGattConnRef.configureGattConnector()) {
                        printServicesSummaryListToLog();
                        btGattConn.notifyBluetoothBLEDeviceConnected();
                    } else {
                        discoverSvcsHandler.postDelayed(this, CHECK_DISCOVER_SVCS_INTERVAL);
                    }
                }
            };
            discoverSvcsHandler.post(discoverSvcsRunner);
        }
    }
}