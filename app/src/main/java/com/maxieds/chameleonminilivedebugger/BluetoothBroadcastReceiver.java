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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = BluetoothBroadcastReceiver.class.getSimpleName();

    private BluetoothGattConnector btGattConn;

    public BluetoothBroadcastReceiver() {
        btGattConn = null;
    }

    public BluetoothBroadcastReceiver(BluetoothGattConnector bgc) {
        btGattConn = bgc;
    }

    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        AndroidLog.i(TAG, "btConnReceiver: intent action: " + action);
        if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            btGattConn.stopConnectingDevices();
            if (!btGattConn.isDeviceConnected()) {
                btGattConn.startConnectingDevices();
            }
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
        } else if (action.equals(BluetoothDevice.ACTION_FOUND) || action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            /** NOTE: See https://developer.android.com/reference/android/bluetooth/BluetoothDevice#EXTRA_RSSI */
            btGattConn.btDevice = btIntentDevice;
            /**
             * NOTE: Given the long pairing times with the Chameleon BLE interface while holding down button 'A',
             *       it is a good idea to remind the user to keep the button pressed until the connection is
             *       finally established. This means we want the next Toast message to persist for a while.
             *       There are a few solid options:
             *       (1) Something like: https://stackoverflow.com/a/45922317/10661959
             *       (2) Use the Toast.setCallback(Callback) method to communicate with the Toast object created by the
             *           Utils functions.
             */
            String userConnInstMsg = String.format(BuildConfig.DEFAULT_LOCALE, "New %s %s found.\n%s.",
                    btGattConn.btDevice.getName(), btGattConn.btDevice.getAddress(),
                    btGattConn.btSerialContext.getApplicationContext().getResources().getString(R.string.bluetoothExtraConfigInstructions));
            Utils.displayToastMessageLong(userConnInstMsg);
            final long shortPauseDuration = 1500L, threadSleepInterval = 50L;
            btGattConn.btDevice.createBond();
            btGattConn.requestConnectionPriority(BluetoothGattConnector.BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
            btGattConn.btGatt = btGattConn.btDevice.connectGatt(btGattConn.btSerialContext, true, btGattConn);
            if (btGattConn.btGatt != null) {
                btGattConn.btGatt.discoverServices();
            }
            return;
        }
        int intentExtraState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE, -1);
        int intentExtraBondState = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE, -1);
        if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST) ||
                (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED) ||
                (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && intentExtraBondState == BluetoothDevice.BOND_BONDED) ||
                (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED)) {
            btGattConn.btDevice = btIntentDevice;
            if (btGattConn.btDevice == null) {
                return;
            }
            if (btGattConn.btGatt == null) {
                btGattConn.requestConnectionPriority(BluetoothGattConnector.BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
                btGattConn.btGatt = btGattConn.btDevice.connectGatt(btGattConn.btSerialContext, true, btGattConn);
            }
            if (btGattConn.btGatt != null) {
                btGattConn.btGatt.discoverServices();
            }
        }
    }

}
