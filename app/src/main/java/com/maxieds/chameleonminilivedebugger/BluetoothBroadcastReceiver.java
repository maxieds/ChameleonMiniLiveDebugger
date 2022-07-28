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
import android.widget.Toast;

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
            /** NOTE: See https://developer.android.com/reference/android/bluetooth/BluetoothDevice#EXTRA_RSSI */
            btGattConn.btDevice = btIntentDevice;
            String userConnInstMsg = String.format(BuildConfig.DEFAULT_LOCALE, "New %s %s found.",
                    btGattConn.btDevice.getName(), btGattConn.btDevice.getAddress());
            Utils.displayToastMessage(userConnInstMsg, Toast.LENGTH_SHORT);
            btGattConn.btDevice.createBond();
            btGattConn.requestConnectionPriority(BluetoothGattConnector.BLUETOOTH_GATT_CONNECT_PRIORITY_HIGH);
            btGattConn.btGatt = btGattConn.btDevice.connectGatt(btGattConn.btSerialContext, true, btGattConn);
        }
        int intentExtraState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE, -1);
        int intentExtraBondState = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE, -1);
        AndroidLog.d(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "EXTRA BOND STATE: %d", intentExtraBondState));
        if ((action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED) ||
                (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && intentExtraBondState == BluetoothDevice.BOND_BONDED) ||
                (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && intentExtraState == BluetoothAdapter.STATE_CONNECTED)) {
            //btGattConn.btGatt.discoverServices();
            btGattConn.btGatt.
        }
    }
}