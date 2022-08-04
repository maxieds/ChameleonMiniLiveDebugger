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
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothStatusCodes;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Locale;

public class BluetoothUtils {

    public static final int ACTVITY_REQUEST_BLUETOOTH_ENABLED_CODE = 0x00B1;
    public static final int ACTVITY_REQUEST_BLUETOOTH_DISCOVERABLE_CODE = 0x00B1;

    @SuppressLint("MissingPermission")
    public static boolean isBluetoothEnabled(boolean startActivityIfNot) {
        LiveLoggerActivity mainActivityCtx = LiveLoggerActivity.getLiveLoggerInstance();
        BluetoothAdapter btAdapter = null;
        BluetoothAdapter btAdapterDefault = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager btManager = (BluetoothManager) mainActivityCtx.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapterFromService = btManager != null ? btManager.getAdapter() : null;
        if (btAdapterFromService != null) {
            btAdapter = btAdapterFromService;
        } else if (btAdapterDefault != null) {
            btAdapter = btAdapterDefault;
        }
        boolean status = true;
        if (btAdapter == null) {
            return false;
        }
        ChameleonSettings.disableBTAdapter = !btAdapter.isEnabled();
        if (!btAdapter.isEnabled() && !btAdapter.enable()) {
            if (startActivityIfNot) {
                try {
                    Intent turnBTOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    mainActivityCtx.startActivityForResult(turnBTOn, ACTVITY_REQUEST_BLUETOOTH_ENABLED_CODE);
                } catch (Exception excpt) {
                    AndroidLog.printStackTrace(excpt);
                }
            }
            status = false;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(LiveLoggerActivity.getLiveLoggerInstance(), "android.permission.BLUETOOTH_ADVERTISE") != PackageManager.PERMISSION_GRANTED) {
                if (startActivityIfNot) {
                    Intent btMakeDiscIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    btMakeDiscIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    mainActivityCtx.startActivityForResult(btMakeDiscIntent, ACTVITY_REQUEST_BLUETOOTH_DISCOVERABLE_CODE);
                }
                return false;
            }
        }
        return status;
    }

    public static boolean isBluetoothEnabled() {
        return isBluetoothEnabled(false);
    }

    /**
     * The developer does not like leaving Bluetooth enabled on the Droid device when it is
     * unnecessary. These methods are called to disable the Bluetooth adapter after CMLD closes
     * if BT was enabled on the system by CMLD to facilitate handling Chameleon BLE connections.
     */
    public static void resetBluetoothAdapterAtClose(ChameleonMiniLiveDebuggerActivity mainActivityCtx) {
        resetBluetoothAdapter(mainActivityCtx, false);
    }

    public static void resetBluetoothAdapterAtStart(ChameleonMiniLiveDebuggerActivity mainActivityCtx) {
        resetBluetoothAdapter(mainActivityCtx, true);
    }

    private static void resetBluetoothAdapter(ChameleonMiniLiveDebuggerActivity mainActivityCtx, boolean action) {
        if (!ChameleonSettings.disableBTAdapter) {
            return;
        }
        BluetoothAdapter btAdapter = null;
        BluetoothAdapter btAdapterDefault = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager btManager = (BluetoothManager) mainActivityCtx.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapterFromService = btManager != null ? btManager.getAdapter() : null;
        if (btAdapterFromService != null) {
            btAdapter = btAdapterFromService;
        } else if (btAdapterDefault != null) {
            btAdapter = btAdapterDefault;
        }
        if (btAdapter != null) {
            try {
                if (action && !btAdapter.isEnabled()) {
                    btAdapter.enable();
                } else if (!action && btAdapter.isEnabled()) {
                    btAdapter.disable();
                }
            } catch (SecurityException se) {
                AndroidLog.printStackTrace(se);
            }
        }
    }

    public static boolean isStatusResultCodeError(int resultCode) {
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
            case BluetoothStatusCodes.ERROR_BLUETOOTH_NOT_ALLOWED:
            case BluetoothStatusCodes.ERROR_BLUETOOTH_NOT_ENABLED:
            case BluetoothStatusCodes.ERROR_DEVICE_NOT_BONDED:
            case BluetoothStatusCodes.ERROR_MISSING_BLUETOOTH_CONNECT_PERMISSION:
            case BluetoothStatusCodes.ERROR_UNKNOWN:
                return true;
            default:
                return false;
        }
    }

    public static String getStatusResultCodeErrorString(int resultCode) {
        String errorDesc = "NO-ERROR";
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                errorDesc = "OPEERATION-CANCELED";
                break;
            case BluetoothStatusCodes.ERROR_BLUETOOTH_NOT_ALLOWED:
                errorDesc = "BLUETOOTH-NOT-ALLOWED";
                break;
            case BluetoothStatusCodes.ERROR_BLUETOOTH_NOT_ENABLED:
                errorDesc = "BLUETOOTH-NOT-ENABLED";
                break;
            case BluetoothStatusCodes.ERROR_DEVICE_NOT_BONDED:
                errorDesc = "DEVICE-NOT-BONDED";
                break;
            case BluetoothStatusCodes.ERROR_MISSING_BLUETOOTH_CONNECT_PERMISSION:
                errorDesc = "MISSING-CONNECT-PERMISSION";
                break;
            case BluetoothStatusCodes.ERROR_UNKNOWN:
                errorDesc = "ERROR-UNKNOWN";
                break;
            default:
                break;
        }
        return String.format(Locale.getDefault(), "Bluetooth reequest error: %s.", errorDesc);
    }

    public static void displayAndroidBluetoothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        LiveLoggerActivity.getLiveLoggerInstance().startActivity(intentOpenBluetoothSettings);
    }

    public static void displayAndroidBluetoothTroubleshooting() {

        LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(llActivity, R.style.SpinnerTheme);
        WebView wv = new WebView(llActivity);

        String dialogMainPointsHTML = llActivity.getString(R.string.apphtmlheader) +
                llActivity.getString(R.string.bluetoothTroubleshootingInstructionsHTML) +
                llActivity.getString(R.string.apphtmlfooter);

        wv.loadDataWithBaseURL(null, dialogMainPointsHTML, "text/html", "UTF-8", "");
        wv.getSettings().setJavaScriptEnabled(false);
        wv.setBackgroundColor(ThemesConfiguration.getThemeColorVariant(R.attr.colorAccentHighlight));
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        wv.setInitialScale(10);

        adBuilder.setCancelable(true);
        adBuilder.setTitle("");
        adBuilder.setPositiveButton(
                "Back to Previous",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        adBuilder.setView(wv);
        adBuilder.setInverseBackgroundForced(true);

        AlertDialog alertDialog = adBuilder.create();
        alertDialog.show();

    }

    public static boolean isChameleonDeviceName(String devName) {
        if (devName == null) {
            return false;
        }
        return devName.equals(BluetoothGattConnector.CHAMELEON_REVG_NAME) ||
                devName.equals(BluetoothGattConnector.CHAMELEON_REVG_NAME_ALTERNATE) ||
                devName.equals(BluetoothGattConnector.CHAMELEON_REVG_TINY_NAME);
    }

    @SuppressLint("MissingPermission")
    public static int getChameleonDeviceType(String btDeviceName) {
        try {
            if (btDeviceName.equals(BluetoothGattConnector.CHAMELEON_REVG_TINY_NAME)) {
                return ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG_TINY;
            } else if (btDeviceName.equals(BluetoothGattConnector.CHAMELEON_REVG_NAME) ||
                    btDeviceName.equals(BluetoothGattConnector.CHAMELEON_REVG_NAME_ALTERNATE)) {
                return ChameleonIO.CHAMELEON_TYPE_PROXGRIND_REVG;
            }
        } catch (Exception excpt) {
            AndroidLog.printStackTrace(excpt);
        }
        return ChameleonIO.CHAMELEON_TYPE_UNKNOWN;
    }

    /* NOTE: The UUID strings for the GATT characteristics associated with each service can be
     *       parsed by observing that the first part of the UUID is of the form '0000XXXX-' where
     *       the 16-bit 'XXXX' hexadecimal codes are standardized here:
     *       https://btprodspecificationrefs.blob.core.windows.net/assigned-values/16-bit%20UUID%20Numbers%20Document.pdf
     *       For example, the UUID '00002a29-0000-1000-8000-00805f9b34fb' is an identifier for the characteristic
     *       that provides the Manufacturer Name String.
     *
     * EXAMPLE (CHAMELEON TINY PRO):
     *       > SERVICE 00001800-0000-1000-8000-00805f9b34fb [type 00]
     *          -- SVC-CHAR 00002a00-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a01-0000-1000-8000-00805f9b34fb
     *       > SERVICE 00001801-0000-1000-8000-00805f9b34fb [type 00]
     *       > SERVICE 0000180a-0000-1000-8000-00805f9b34fb [type 00]
     *          -- SVC-CHAR 00002a29-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a24-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a25-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a27-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a26-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a28-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a23-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a2a-0000-1000-8000-00805f9b34fb
     *          -- SVC-CHAR 00002a50-0000-1000-8000-00805f9b34fb
     *       > SERVICE 51510001-7969-6473-6f40-6b6f6c6c6957 [type 00]
     *          -- SVC-CHAR 51510002-7969-6473-6f40-6b6f6c6c6957
     *          -- SVC-CHAR 51510003-7969-6473-6f40-6b6f6c6c6957
     *          -- SVC-CHAR 51510004-7969-6473-6f40-6b6f6c6c6957
     *
     * See: https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library/blob/master/scanner/src/main/java/no/nordicsemi/android/support/v18/scanner/ScanRecord.java
     * public static final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
     * public static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
     * public static final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
     * public static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;
     */

    public static class BLEPacket {

        private static String TAG = BLEPacket.class.getSimpleName();

        /**
         * NOTE: BLE device command and control codes taken from:
         *       https://github.com/RfidResearchGroup/ChameleonMini/blob/proxgrind/Firmware/Chameleon-Mini/uartcmd.h
         *       https://github.com/RfidResearchGroup/ChameleonBLEAPI/blob/master/appmain/devices/BleCMDControl.java
         *       https://github.com/RfidResearchGroup/ChameleonBLEAPI/blob/master/packets/src/main/java/com/proxgrind/chameleon/packets/DataPackets.java
         */
        public static final byte CMD_HEAD_SIGN = (byte) 0xA5; /* Command header ID */
        public static final byte CMD_UART_RXTX = 0x75;        /* UART data command 'U' */
        public static final byte CMD_BLE_INFO = 0x69;         /* Extra info about the Mini devices */
        public static final byte CMD_SEND_ACK = (byte) 0x80;  /* Command reply ID */
        public static final byte CMD_BLE_TEST = 0x74;         /* TEST data command 't' */
        public static final byte CMD_TYPE_ALIVE = 0x61;       /* Heartbeat command 'a' */
        public static final byte CMD_TYPE_POWERDOWN = 0x70;   /* Power down command 'p' */

        public enum DataEncodeMode {
            AUTO,
            DOPACK,
            UNPACK
        }

        public static class BlePacketHeader {

            private byte sign;
            private byte command;
            private byte length;
            private byte status;

            public BlePacketHeader() {
                sign = CMD_HEAD_SIGN;
                command = CMD_UART_RXTX;
                length = 0x00;
                status = 0x00;
            }

            BlePacketHeader setCommand(byte cmd) {
                command = cmd;
                return this;
            }

            BlePacketHeader setLength(byte len) {
                length = len;
                return this;
            }

            BlePacketHeader setStatus(byte st) {
                status = st;
                return this;
            }

            byte[] getHeaderData() {
                return new byte[] { sign, command, length, status };
            }

        }

        public static byte[] packageData(byte[] dataBytes) {
            if (dataBytes == null) {
                dataBytes = new byte[0];
            }
            return new Builder(dataBytes)
                    .setEncodeMode(DataEncodeMode.DOPACK)
                    .build();
        }

        public static byte[] unpackageData(byte[] dataBytes) {
            if (dataBytes == null) {
                return null;
            }
            return new Builder(dataBytes)
                    .setEncodeMode(DataEncodeMode.UNPACK)
                    .build();
        }

        private static byte calculateChecksum(byte initCheckSum, byte[] bytesArr, boolean subOp) {
            if (bytesArr == null) {
                return (byte) 0x00;
            }
            byte checksum = initCheckSum;
            int bufPos = 0;
            int byteCount = bytesArr.length;
            while (byteCount-- > 0) {
                byte b = bytesArr[bufPos++];
                if (!subOp) {
                    checksum += b;
                } else {
                    checksum -= b;
                }
            }
            return checksum;
        }

        private static byte calculateChecksum(byte[] bytesArr) {
            return calculateChecksum((byte) 0x00, bytesArr, false);
        }

        public static class Builder {

            private static String TAG = BLEPacket.class.getSimpleName() + "." + Builder.class.getSimpleName();

            private boolean autoCRLF;
            private DataEncodeMode dataEncodeMode;
            private byte cmdCode;
            private byte[] rawData;

            public Builder(byte[] dataBytes) {
                autoCRLF = true;
                dataEncodeMode = DataEncodeMode.AUTO;
                cmdCode = CMD_UART_RXTX;
                rawData = dataBytes;
            }

            public Builder setAutoCRLF(boolean auto) {
                autoCRLF = auto;
                return this;
            }

            public Builder setEncodeMode(DataEncodeMode mode) {
                dataEncodeMode = mode;
                return this;
            }

            public Builder setCommandCode(byte code) {
                cmdCode = code;
                return this;
            }

            public byte[] build() {
                if (dataEncodeMode == DataEncodeMode.AUTO) {
                    dataEncodeMode = DataEncodeMode.UNPACK;
                }
                if (rawData == null) {
                    rawData = new byte[0];
                }
                if (dataEncodeMode == DataEncodeMode.DOPACK) {
                    if (cmdCode == CMD_UART_RXTX && autoCRLF) {
                        final byte[] crlfBytes = new byte[] { 0x0d, 0x0a };
                        rawData = Utils.mergeBytes(rawData, crlfBytes);
                    }
                    BlePacketHeader pktHeaderData = new BlePacketHeader()
                            .setCommand(cmdCode)
                            .setLength((byte) rawData.length);
                    byte checksum = calculateChecksum(Utils.mergeBytes(pktHeaderData.getHeaderData(), rawData));
                    pktHeaderData.setStatus((byte) (0x00 - checksum));
                    return Utils.mergeBytes(pktHeaderData.getHeaderData(), rawData);
                } else if (dataEncodeMode == DataEncodeMode.UNPACK) {
                    if (rawData.length < 4) {
                        return null;
                    }
                    byte pktLength = rawData[2];
                    if (pktLength + 4 != rawData.length) {
                        return null;
                    }
                    byte[] pktPayloadData = new byte[pktLength];
                    System.arraycopy(pktPayloadData, 0, rawData, 4, pktLength);
                    byte pktHeadStatus = rawData[3];
                    byte[] pktPackagedBytes = rawData.clone();
                    pktPackagedBytes[3] = (byte) 0x00; /* Reset the status byte so it does not contribute to the checksum */
                    byte pktChecksum = calculateChecksum(pktHeadStatus, pktPackagedBytes, false);
                    if (pktChecksum != (byte) 0x00) {
                        AndroidLog.d(TAG, "Incoming BT bytes to unpack: " + Utils.bytes2Hex(rawData));
                        AndroidLog.w(TAG, "Packet checksum does not match for raw byte data " + Utils.bytes2Ascii(pktPayloadData));
                        return null;
                    }
                    /** ??? TODO: Big or little endian byte order of the results returned (Chameleon Mini AVR is LE) ??? */
                    //return ArrayUtils.reverse(pktPayloadData);
                    return pktPayloadData;
                }
                return null;
            }

        }

    }

}
