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
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class BluetoothBLEInterface extends SerialIOReceiver {

    /** TODO: Check XModem functionality with the BT devices ??? */

    private static final String TAG = BluetoothBLEInterface.class.getSimpleName();

    public String getInterfaceLoggingTag() {
        return TAG;
    }

    private BluetoothDevice activeDevice;
    private BluetoothGattConnector btGattConnectorBLEDevice;
    private boolean serialConfigured;
    private boolean receiversRegistered;
    private boolean scanning;
    private Semaphore btDevLock = new Semaphore(1, true);

    public boolean isWiredUSB() { return false; }

    public boolean isBluetooth() { return true; }

    public int setSerialBaudRate(int bdRate) {
        AndroidLogger.w(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "Attempt to set serial baud rate to %d on a BT connection"));
        return STATUS_NOT_SUPPORTED;
    }

    @SuppressLint("MissingPermission")
    public String getDeviceName() {
        final String unknownBTDevName = "<UNKNOWN-BTDEV-NAME>";
        try {
            return activeDevice != null ? activeDevice.getName() : unknownBTDevName;
        } catch (SecurityException se) {
            AndroidLogger.printStackTrace(se);
            return unknownBTDevName;
        }
    }

    @SuppressLint("MissingPermission")
    public String getActiveDeviceInfo() {
        if(activeDevice == null) {
            return "<null-device>: No information available.";
        }
        String devInfo = "<Device-Info-Unavailable>";
        try {
            devInfo = String.format(BuildConfig.DEFAULT_LOCALE, "BT Class: %s\nBond State: %s\nProduct Name: %s\nType: %s\nDevice Address: %s",
                    activeDevice.getBluetoothClass(), activeDevice.getBondState(),
                    activeDevice.getName(), activeDevice.getType(),
                    activeDevice.getAddress());
        } catch (SecurityException se) {
            AndroidLogger.printStackTrace(se);
            devInfo = "<Device-Info-Unavailable>";
        }
        return devInfo;
    }

    @SuppressLint("MissingPermission")
    private String getActiveDeviceInfoExtraTiny() {
        try {
            byte[] deviceInfoBytes = btGattConnectorBLEDevice.rawWrite(BluetoothUtils.CMD_GET_INFO_TINY);
            if (deviceInfoBytes == null || deviceInfoBytes.length < BluetoothUtils.BLE_INFO_PACKET_SIZE_MAX_TINY) {
                return null;
            }
            String[][] extraInfoDataList = new String[][] {
                    { "Battery Voltage", String.valueOf(Utils.bytes2Integer32(Utils.reverseByteArray(Utils.getByteSubarray(deviceInfoBytes, 0, 1)))) },
                    { "Battery Percent", String.valueOf(deviceInfoBytes[2]) },
                    { "BLE Firmware Version", Utils.byteSubarrayToString(deviceInfoBytes, 3, 18) },
            };
            final int maxHeaderLength = 20;
            final String paddingBase = "                                        ";
            StringBuilder extraInfoBuilder = new StringBuilder("");
            for (int lstIdx = 0; lstIdx < extraInfoDataList.length; lstIdx++) {
                String einfoHdr = extraInfoDataList[lstIdx][0];
                String einfoHdrPadding = paddingBase.substring(0, maxHeaderLength - einfoHdr.length() - 1);
                String einfoData = extraInfoDataList[lstIdx][1];
                extraInfoBuilder.append(String.format(BuildConfig.DEFAULT_LOCALE, "%s:%s%s\n", einfoHdr, einfoHdrPadding, einfoData));
            }
            return extraInfoBuilder.toString();
        } catch (IOException ioe) {
            AndroidLogger.printStackTrace(ioe);

        }
        return null;
    }

    @SuppressLint("MissingPermission")
    private String getActiveDeviceInfoExtraMini() {
        try {
            byte[] deviceInfoBytes = btGattConnectorBLEDevice.rawWrite(BluetoothUtils.CMD_GET_INFO_MINI);
            if (deviceInfoBytes == null || deviceInfoBytes.length < BluetoothUtils.BLE_INFO_PACKET_SIZE_MAX_MINI) {
                return null;
            }
            String[][] extraInfoDataList = new String[][] {
                    { "Major (Main) Version", String.valueOf(deviceInfoBytes[0]) },
                    { "Minor Version", String.valueOf(deviceInfoBytes[1]) },
                    { "Version Flag", String.valueOf(Utils.bytes2Integer32(Utils.reverseByteArray(Utils.getByteSubarray(deviceInfoBytes, 2, 5)))) },
                    { "BLE Version", Utils.byteSubarrayToString(deviceInfoBytes, 6, 37) },
                    { "Battery Voltage", String.valueOf(Utils.bytes2Integer32(Utils.reverseByteArray(Utils.getByteSubarray(deviceInfoBytes, 38, 41)))) },
                    { "Battery Percent", String.valueOf(deviceInfoBytes[42]) },
                    { "AVR Major (Main) Version", String.valueOf(deviceInfoBytes[43]) },
                    { "AVR Minor Version", String.valueOf(deviceInfoBytes[44]) },
                    { "AVR Version", Utils.byteSubarrayToString(deviceInfoBytes, 45, 74) },
            };
            final int maxHeaderLength = 24;
            final String paddingBase = "                                        ";
            StringBuilder extraInfoBuilder = new StringBuilder("");
            for (int lstIdx = 0; lstIdx < extraInfoDataList.length; lstIdx++) {
                String einfoHdr = extraInfoDataList[lstIdx][0];
                String einfoHdrPadding = paddingBase.substring(0, maxHeaderLength - einfoHdr.length() - 1);
                String einfoData = extraInfoDataList[lstIdx][1];
                extraInfoBuilder.append(String.format(BuildConfig.DEFAULT_LOCALE, "%s:%s%s\n", einfoHdr, einfoHdrPadding, einfoData));
            }
            return extraInfoBuilder.toString();
        } catch (IOException ioe) {
            AndroidLogger.printStackTrace(ioe);
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public String getActiveDeviceInfoExtra() {
        int chamDeviceType = BluetoothUtils.getChameleonDeviceType(getDeviceName());
        if (chamDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINY || chamDeviceType == ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINYPRO) {
            return getActiveDeviceInfoExtraTiny();
        } else {
            return getActiveDeviceInfoExtraMini();
        }
    }

    public BluetoothBLEInterface(Context appContext) {
        setListenerContext(appContext);
        activeDevice = null;
        btGattConnectorBLEDevice = new BluetoothGattConnector(appContext);
        btGattConnectorBLEDevice.setBluetoothSerialInterface(this);
        serialConfigured = false;
        receiversRegistered = false;
        scanning = false;
    }

    public void broadcastIntent(@NonNull Intent bcIntent) {
        if (btGattConnectorBLEDevice != null) {
            btGattConnectorBLEDevice.receiveBroadcastIntent(bcIntent);
        }
    }

    @SuppressLint("MissingPermission")
    public boolean configureSerialConnection(BluetoothDevice btDev) {
        if (btDev == null) {
            return false;
        } else if (!receiversRegistered) {
            configureSerial();
        }
        Utils.clearToastMessage();
        activeDevice  = btDev;
        ChameleonIO.REVE_BOARD = false;
        ChameleonIO.PAUSED = false;
        ChameleonSettings.chameleonDeviceSerialNumber = ChameleonSettings.CMINI_DEVICE_FIELD_NONE;
        ChameleonSettings.chameleonDeviceAddress = btDev.getAddress();
        ChameleonIO.CHAMELEON_MINI_BOARD_TYPE = BluetoothUtils.getChameleonDeviceType(btDev.getName());
        Handler configDeviceHandler = new Handler(Looper.getMainLooper());
        Runnable configDeviceRunnable = new Runnable() {
            public void run() {
                AndroidLogger.i(TAG, ChameleonSettings.getActiveSerialIOPort().toString());
                if(ChameleonSettings.getActiveSerialIOPort() != null && btGattConnectorBLEDevice.isDeviceConnected()) {
                    configDeviceHandler.removeCallbacks(this);
                    LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
                    llActivity.reconfigureSerialIODevices();
                    llActivity.setStatusIcon(R.id.statusIconBT, R.drawable.bluetooth16);
                    Utils.displayToastMessageShort(String.format(BuildConfig.DEFAULT_LOCALE, "New Bluetooth connection:\n%s\n%s @ %s", ChameleonIO.getDeviceDescription(ChameleonIO.CHAMELEON_MINI_BOARD_TYPE), btDev.getName(), ChameleonSettings.chameleonDeviceAddress));
                    UITabUtils.updateConfigTabConnDeviceInfo(false);
                    notifyStatus("BLUETOOTH STATUS: ", "Chameleon:     " + getActiveDeviceInfo());
                    String extraBTInfo = getActiveDeviceInfoExtra();
                    if (extraBTInfo != null) {
                        notifyStatus("BLUETOOTH EXTENDED DEVICE INFO: ", extraBTInfo);
                    }
                }
                else {
                    AndroidLogger.i(TAG, "BLE device __NOT__ connected! ... Looping");
                    configDeviceHandler.postDelayed(this, ChameleonIO.TIMEOUT);
                }
            }
        };
        ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = ChameleonSettings.BTIO_IFACE_INDEX;
        ChameleonSettings.stopSerialIOConnectionDiscovery();
        ChameleonIO.DeviceStatusSettings.stopPostingStats();
        serialConfigured = true;
        configDeviceHandler.postDelayed(configDeviceRunnable, 500);
        return true;
    }

    public boolean startScanningDevices() {
        if (scanning) {
            return false;
        }
        scanning = true;
        configureSerial();
        btGattConnectorBLEDevice.startConnectingDevices();
        return true;
    }

    public boolean stopScanningDevices() {
        scanning = false;
        btGattConnectorBLEDevice.stopConnectingDevices();
        return true;
    }

    public int configureSerial() {
        if(serialConfigured()) {
            return STATUS_TRUE;
        }
        receiversRegistered = true;
        return STATUS_TRUE;
    }

    public int shutdownSerial() {
        ChameleonIO.DeviceStatusSettings.stopPostingStats();
        if(btGattConnectorBLEDevice != null && btGattConnectorBLEDevice.isDeviceConnected()) {
            btGattConnectorBLEDevice.disconnectDevice();
        }
        stopScanningDevices();
        ChameleonIO.PAUSED = true;
        ExportTools.EOT = true;
        ExportTools.transmissionErrorOccurred = true;
        ChameleonIO.DOWNLOAD = false;
        ChameleonIO.UPLOAD = false;
        ChameleonIO.WAITING_FOR_XMODEM = false;
        ChameleonIO.WAITING_FOR_RESPONSE = false;
        ChameleonIO.EXPECTING_BINARY_DATA = false;
        ChameleonIO.LASTCMD = "";
        ChameleonIO.APPEND_PRIOR_BUFFER_DATA = false;
        ChameleonIO.PRIOR_BUFFER_DATA = new byte[0];
        activeDevice = null;
        serialConfigured = false;
        receiversRegistered = false;
        if(ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX == ChameleonSettings.BTIO_IFACE_INDEX) {
            ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
        }
        btDevLock.release();
        LiveLoggerActivity.getLiveLoggerInstance().clearStatusIcon(R.id.statusIconBT);
        UITabUtils.updateConfigTabConnDeviceInfo(true);
        notifyDeviceConnectionTerminated();
        return STATUS_TRUE;
    }

    public boolean serialConfigured() { return serialConfigured; }

    public boolean serialReceiversRegistered() { return receiversRegistered; }

    public boolean acquireSerialPort() {
        try {
            btDevLock.acquire();
            return true;
        } catch(Exception inte) {
            AndroidLogger.printStackTrace(inte);
            btGattConnectorBLEDevice.releaseAllLocks();
            btDevLock.release();
            return false;
        }
    }

    public boolean acquireSerialPortNoInterrupt() {
        try {
            btDevLock.acquireUninterruptibly();
            return true;
        } catch(Exception inte) {
            AndroidLogger.printStackTrace(inte);
            btDevLock.release();
            btGattConnectorBLEDevice.releaseAllLocks();
            return false;
        }
    }

    public boolean tryAcquireSerialPort(int timeout) {
        try {
            return btDevLock.tryAcquire(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch(Exception ie) {
            AndroidLogger.printStackTrace(ie);
            btDevLock.release();
            btGattConnectorBLEDevice.releaseAllLocks();
            return false;
        }
    }

    public boolean releaseSerialPortLock() {
        btDevLock.release();
        btGattConnectorBLEDevice.releaseAllLocks();
        return true;
    }

    public int sendDataBuffer(byte[] dataWriteBuffer) {
        AndroidLogger.i(TAG, "write: " + Utils.bytes2Hex(dataWriteBuffer));
        if(dataWriteBuffer == null || dataWriteBuffer.length == 0) {
            return STATUS_FALSE;
        } else if(!serialConfigured()) {
            return STATUS_FALSE;
        }
        try {
            if (btGattConnectorBLEDevice.write(dataWriteBuffer) != STATUS_OK) {
                return STATUS_FALSE;
            } else if (btGattConnectorBLEDevice.read() != STATUS_OK) {
                return STATUS_FALSE;
            }
        } catch(IOException ioe) {
            AndroidLogger.printStackTrace(ioe);
        }
        return STATUS_TRUE;
    }

}
