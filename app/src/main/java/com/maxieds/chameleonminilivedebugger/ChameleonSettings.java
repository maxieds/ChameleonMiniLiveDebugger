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

import android.util.Log;

public class ChameleonSettings {

    private static final String TAG = ChameleonSettings.class.getSimpleName();

    public static boolean allowWiredUSB = true;
    public static boolean allowBluetooth = false;
    public static int serialBaudRate = ChameleonSerialIOInterface.LIMITED_SPEED_BAUD_RATE;

    public static final String CMINI_DEVICE_FIELD_UNKNOWN = "<UNKNOWN>";
    public static String chameleonDeviceSerialNumber = CMINI_DEVICE_FIELD_UNKNOWN;
    public static String chameleonDeviceMAC = CMINI_DEVICE_FIELD_UNKNOWN;
    public static String chameleonDeviceNickname = "Chameleon Mini (Default)";

    public static final int SNIFFING_MODE_UNIDIRECTIONAL = 1;
    public static final int SNIFFING_MODE_BIDIRECTIONAL = 2;
    public static int sniffingMode = SNIFFING_MODE_BIDIRECTIONAL;

    public static final int USBIO_IFACE_INDEX = 0;
    public static final int BTIO_IFACE_INDEX = 1;
    public static SerialIOReceiver[] serialIOPorts = null;
    public static int SERIALIO_IFACE_ACTIVE_INDEX = -1;

    public static synchronized void initSerialIOPortObjects() {
        serialIOPorts = new SerialIOReceiver[2];
        serialIOPorts[USBIO_IFACE_INDEX] = new SerialUSBInterface(LiveLoggerActivity.getLiveLoggerInstance());
        serialIOPorts[BTIO_IFACE_INDEX] = new BluetoothSerialInterface(LiveLoggerActivity.getLiveLoggerInstance());
        SERIALIO_IFACE_ACTIVE_INDEX = -1;
    }

    public static ChameleonSerialIOInterface getActiveSerialIOPort() {
        if(SERIALIO_IFACE_ACTIVE_INDEX < 0) {
            return null;
        }
        else if(!serialIOPorts[SERIALIO_IFACE_ACTIVE_INDEX].serialConfigured()) {
            return null;
        }
        return serialIOPorts[SERIALIO_IFACE_ACTIVE_INDEX];
    }

    public static void initializeSerialIOConnections() {
        if(serialIOPorts == null) {
            initSerialIOPortObjects();
        }
        for(int si = 0; si < serialIOPorts.length; si++) {
            if(si == USBIO_IFACE_INDEX && allowWiredUSB) {
                Log.i(TAG, "Started scanning for SerialUSB devices ... ");
                serialIOPorts[si].startScanningDevices();
            }
            else if(si == BTIO_IFACE_INDEX && allowBluetooth && ((BluetoothSerialInterface) serialIOPorts[si]).isBluetoothEnabled()) {
                Log.i(TAG, "Started scanning for SerialBT devices ... ");
                serialIOPorts[si].startScanningDevices();
            }
        }
    }

    public static void stopSerialIOConnectionDiscovery() {
        for(int si = 0; si < serialIOPorts.length; si++) {
            serialIOPorts[si].stopScanningDevices();
        }
    }


}
