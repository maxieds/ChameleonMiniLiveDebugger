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

import android.os.Handler;

public class ChameleonSettings {

    private static final String TAG = ChameleonSettings.class.getSimpleName();

    public static boolean allowWiredUSB = true;
    public static boolean allowBluetooth = false;
    public static int serialBaudRate = ChameleonSerialIOInterface.HIGH_SPEED_BAUD_RATE;

    public static final String CMINI_DEVICE_FIELD_NONE = "N/A";
    public static String chameleonDeviceSerialNumber = CMINI_DEVICE_FIELD_NONE;
    public static String chameleonDeviceAddress = CMINI_DEVICE_FIELD_NONE;
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
        serialIOPorts[BTIO_IFACE_INDEX] = new BluetoothBLEInterface(LiveLoggerActivity.getLiveLoggerInstance());
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

    private static final long REINIT_SCAN_INTERVAL = 6500;
    private static final Handler initUpdateHandler = new Handler();
    private static final Runnable initUpdateRunnable = new Runnable() {
        public void run() {
            ChameleonSettings.initializeSerialIOConnections();
        }
    };

    public static void initializeSerialIOConnections() {
        if(serialIOPorts == null) {
            initSerialIOPortObjects();
        }
        if (getActiveSerialIOPort() != null) {
            return;
        }
        for(int si = 0; si < serialIOPorts.length; si++) {
            if(si == USBIO_IFACE_INDEX && allowWiredUSB) {
                AndroidLog.i(TAG, "Started scanning for SerialUSB devices ... ");
                serialIOPorts[si].startScanningDevices();
            } else if(si == BTIO_IFACE_INDEX && allowBluetooth && ((BluetoothBLEInterface) serialIOPorts[si]).isBluetoothEnabled(true)) {
                AndroidLog.i(TAG, "Started scanning for BT/BLE devices ... ");
                serialIOPorts[si].startScanningDevices();
                initUpdateHandler.removeCallbacksAndMessages(initUpdateRunnable);
            } else if (si == BTIO_IFACE_INDEX && allowBluetooth) {
                AndroidLog.i(TAG, "Repeating initialization of scanning of BT/BLE devices in a few seconds ... ");
                initUpdateHandler.postDelayed(initUpdateRunnable, ChameleonSettings.REINIT_SCAN_INTERVAL);
            }
        }
    }

    public static void stopSerialIOConnectionDiscovery() {
        initUpdateHandler.removeCallbacksAndMessages(initUpdateRunnable);
        for(int si = 0; si < serialIOPorts.length; si++) {
            serialIOPorts[si].stopScanningDevices();
        }
    }


}
