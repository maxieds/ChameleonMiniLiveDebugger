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

public class Settings {

    private static final String TAG = Settings.class.getSimpleName();

    public static boolean allowWiredUSB = true;
    public static boolean allowBluetooth = false;
    public static boolean allowAndroidNFC = false;
    public static int serialBaudRate = 115200;

    public static String chameleonDeviceSerialNumber = "<UNKNOWN>";
    public static String chameleonDeviceNickname = "Chameleon Mini (Default)";

    public static final int SNIFFING_MODE_UNIDIRECTIONAL = 1;
    public static final int SNIFFING_MODE_BIDIRECTIONAL = 2;
    public static int sniffingMode = SNIFFING_MODE_BIDIRECTIONAL;

    public static final int USBIO_IFACE_INDEX = 0;
    public static final int BTIO_IFACE_INDEX = 1;
    public static ChameleonSerialIOInterface[] serialIOPorts = null;
    public static int SERIALIO_IFACE_ACTIVE_INDEX = -1;

    public static synchronized void initSerialIOPortObjects() {
        serialIOPorts = new ChameleonSerialIOInterface[2];
        serialIOPorts[USBIO_IFACE_INDEX] = new SerialUSBInterface(LiveLoggerActivity.getInstance());
        serialIOPorts[BTIO_IFACE_INDEX] = new BluetoothSerialInterface(LiveLoggerActivity.getInstance());
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
