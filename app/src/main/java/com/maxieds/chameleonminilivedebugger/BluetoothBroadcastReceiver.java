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
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothBroadcastReceiver.class.getSimpleName();

    private static BluetoothBroadcastReceiver activeReceiverInstance = null;
    private BluetoothGattConnector btGattConn;
    private String btDeviceName;

    public static BluetoothBroadcastReceiver getActiveInstance() {
        return activeReceiverInstance;
    }

    public static BluetoothBroadcastReceiver initializeActiveInstance(BluetoothGattConnector bgc) {
        if (BluetoothBroadcastReceiver.getActiveInstance() == null) {
            activeReceiverInstance = new BluetoothBroadcastReceiver();
            activeReceiverInstance.btGattConn = bgc;
            activeReceiverInstance.resetActiveBluetoothDeviceName();
        }
        return BluetoothBroadcastReceiver.getActiveInstance();
    }

    public void resetActiveBluetoothDeviceName() {
        btDeviceName = "";
    }

    public BluetoothBroadcastReceiver() {
        activeReceiverInstance = this;
        btGattConn = null;
        resetActiveBluetoothDeviceName();
    }

    private static final boolean PRINT_SERVICES_LIST_TO_LOG = true;
    private static final boolean PRINT_SERVICES_LIST_FULL = false;

    public static void printServicesSummaryListToLog(BluetoothGatt btGatt) {
        if (PRINT_SERVICES_LIST_TO_LOG && btGatt != null) {
            List<BluetoothGattService> svcList = btGatt.getServices();
            StringBuilder svcUUIDSummary = new StringBuilder(" ==== \n");
            for (BluetoothGattService svc : svcList) {
                /* NOTE: BT service data types described in table here:
                 *       https://btprodspecificationrefs.blob.core.windows.net/assigned-numbers/Assigned%20Number%20Types/Format%20Types.pdf
                 * NOTE: BT GATT characteristic permissions and service type constants are defined here:
                 *       https://developer.android.com/reference/android/bluetooth/BluetoothGattCharacteristic
                 */
                UUID svcUuid = svc.getUuid();
                if (!PRINT_SERVICES_LIST_FULL && svcUuid != null && !svcUuid.toString().equals(BluetoothGattConnector.BleUuidType.getUuidByType(BluetoothGattConnector.BleUuidType.UART_SERVICE_UUID).toString())) {
                    continue;
                }
                if (svcUuid == null) {
                    continue;
                }
                svcUUIDSummary.append(String.format(BuildConfig.DEFAULT_LOCALE, "   > SERVICE %s [type %02x]\n", svcUuid.toString(), svc.getType()));
                List<BluetoothGattCharacteristic> svcCharList = svc.getCharacteristics();
                for (BluetoothGattCharacteristic svcChar : svcCharList) {
                    UUID svcCharUuid = svcChar.getUuid();
                    if (svcCharUuid != null) {
                        svcUUIDSummary.append(String.format(BuildConfig.DEFAULT_LOCALE, "      -- SVC-CHAR %s\n", svcCharUuid.toString()));
                    }
                }
                svcUUIDSummary.append("\n");
            }
            AndroidLogger.d(TAG, svcUUIDSummary.toString());
        }
    }

    public static final int DISCOVER_SVCS_ATTEMPT_COUNT = 16;
    public static final long CHECK_DISCOVER_SVCS_INTERVAL = 60000L;

    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (intent.getExtras() == null) {
            return;
        }
        BluetoothDevice btIntentDevice = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
        if (btIntentDevice == null) {
            return;
        }
        if (btGattConn == null || (btGattConn.btDevice == null && btIntentDevice == null)) {
            return;
        } else if (btGattConn.btDevice != null && btGattConn.btDevice.getName() == btDeviceName) {
            return;
        }
        boolean isChameleonDevice = (btGattConn.btDevice != null && BluetoothUtils.isChameleonDeviceName(btDeviceName)) ||
                (btIntentDevice != null && BluetoothUtils.isChameleonDeviceName(btIntentDevice.getName()));
        if (!isChameleonDevice || btDeviceName.length() == 0 || (btIntentDevice != null && btIntentDevice.getName().length() == 0)) {
            return;
        } else if (btGattConn.btDevice == null) {
            btGattConn.btDevice = btIntentDevice;
            btDeviceName = btIntentDevice.getName();
        } else if (btIntentDevice != null && btIntentDevice.getName().equals(btDeviceName)) {
            return;
        }
        final short DEFAULT_BTDEV_RSSI = Short.MIN_VALUE;
        short btDeviceRSSI = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI, DEFAULT_BTDEV_RSSI);
        String rssiInfoStr = "";
        if (btDeviceRSSI != DEFAULT_BTDEV_RSSI) {
            rssiInfoStr = String.format(BuildConfig.DEFAULT_LOCALE, " at RSSI of %d dBm", btDeviceRSSI);
        }
        AndroidLogger.d(TAG, "NEW INTENT " + action);
        AndroidLogger.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "Device name \"%s\" @ %s%s found.", btDeviceName, btGattConn.btDevice.getAddress(), rssiInfoStr));
        String userConnInstMsg = btGattConn.btSerialContext.getString(R.string.bluetoothExtraConfigInstructions);
        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            btGattConn.btDevice = btIntentDevice;
            btGattConn.stopConnectingDevices();
            btGattConn.btDevice.createBond();
        }
        int intentExtraState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE, -1);
        int intentExtraBondState = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE, -1);
        if ((action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && intentExtraBondState == BluetoothDevice.BOND_NONE) ||
                (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_DISCONNECTED) ||
                (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_DISCONNECTING)) {
            btGattConn.startConnectingDevices();
            return;
        } else if ((action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && intentExtraBondState == BluetoothDevice.BOND_BONDING)) {
            Utils.displayToastMessage(userConnInstMsg, Toast.LENGTH_LONG);
        }
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED) ||
                (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED) ||
                (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTING) ||
                btGattConn.btDevice.getBondState() == BluetoothDevice.BOND_BONDED ||
                (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && intentExtraBondState == BluetoothDevice.BOND_BONDED) ||
                (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED)) {
            try {
                btGattConn.btGatt = btGattConn.btDevice.connectGatt(btGattConn.btSerialContext, true, btGattConn, BluetoothDevice.TRANSPORT_LE);
            } catch (NullPointerException npe) {
                AndroidLogger.printStackTrace(npe);
                btGattConn.startConnectingDevices();
                return;
            }
            if (btGattConn.btGatt == null) {
                btGattConn.startConnectingDevices();
                return;
            }
            AndroidLogger.d(TAG, "BT Device bonded ... Starting service discovery.");
            String userConnNotifyMsg = String.format(BuildConfig.DEFAULT_LOCALE, "%s %s connected.\nStarting BT service discovery. This can take a while ...",
                    btGattConn.btDevice.getName(), btGattConn.btDevice.getAddress());
            Utils.displayToastMessage(userConnNotifyMsg, Toast.LENGTH_LONG);
            btGattConn.btGatt = btGattConn.configureGattDataConnection();
        }
    }
}