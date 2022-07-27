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
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

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

    /* NOTE: The UUID strings for the GATT characteristics associated with each service can be
     *       parsed by observing that the first part of the UUID is of the form '0000XXXX-' where
     *       the 16-bit 'XXXX' hexadecimal codes are standardized here:
     *       https://btprodspecificationrefs.blob.core.windows.net/assigned-values/16-bit%20UUID%20Numbers%20Document.pdf
     *       For example, the UUID '00002a29-0000-1000-8000-00805f9b34fb' is an identifier for the characteristic
     *       that provides the Manufacturer Name String.
     */

    /* TODO: Extract the data from all of these characteristics given an input list of BluetoothGattService objects */
    /* TODO: Extract more manufacturer information about the BT device using these commands and
     *       the byte order for which device properties are mapped onto which bytes:
     *       https://github.com/RfidResearchGroup/ChameleonBLEAPI/blob/master/appmain/devices/BleCMDControl.java#L52
     */

    public static class BLEPacket {

        public static final int PACKET_TYPE_NO_FORMAT = -1;
        public static final int PACKET_TYPE_XFER_OUTGOING_RAW = 0;
        public static final int PACKET_TYPE_XFER_INCOMING_RAW = 1;
        public static final int PACKET_TYPE_WRAPPED_SEND = 2;
        public static final int PACKET_TYPE_WRAPPED_RECV = 3;

        /**
         * NOTE: BLE device command and control codes taken from:
         *       https://github.com/RfidResearchGroup/ChameleonMini/blob/proxgrind/Firmware/Chameleon-Mini/uartcmd.h
         *       https://github.com/RfidResearchGroup/ChameleonBLEAPI/blob/master/appmain/devices/BleCMDControl.java
         *       https://github.com/RfidResearchGroup/ChameleonBLEAPI/blob/master/packets/src/main/java/com/proxgrind/chameleon/packets/DataPackets.java
         *       https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library/blob/master/scanner/src/main/java/no/nordicsemi/android/support/v18/scanner/ScanRecord.java
         */
        public static final int CMD_HEAD_SIGN = 0xA5;      /* Command header ID */
        public static final int CMD_UART_RXTX = 0x75;      /* UART data command 'U' */
        public static final int CMD_BLE_INFO = 0x69;       /* Extra info about the Mini devices */
        public static final int CMD_SEND_ACK = 0x80;       /* Command reply ID */
        public static final int CMD_BLE_TEST = 0x74;       /* TEST data command 't' */
        public static final int CMD_TYPE_ALIVE = 0x61;     /* Heartbeat command 'a' */
        public static final int CMD_TYPE_POWERDOWN = 0x70; /* Power down command 'p' */
        public static final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
        public static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
        public static final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
        public static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;

        public enum DataEncodeMode {
            AUTO,
            DOPACK,
            UNPACK
        }

        public static class Builder {

            private int packetFormatType;
            private boolean autoCRLF;
            private DataEncodeMode dataEncodeMode;
            private int cmdCode;
            private byte[] rawData;
            private byte[] checksum;

            public Builder() {
                packetFormatType = PACKET_TYPE_NO_FORMAT;
                autoCRLF = true;
                dataEncodeMode = DataEncodeMode.AUTO;
                cmdCode = 0x00;
                rawData = null;
            }

            public void setPacketFormatType(int fmtType) {
                packetFormatType = fmtType;
            }

            public void setAutoCRLF(boolean auto) {
                autoCRLF = auto;
            }

            public void setEncodeMode(DataEncodeMode mode) {
                dataEncodeMode = mode;
            }

            public void setCommandCode(int code) {
                cmdCode = code;
            }

            public byte[] getData() {
                /* TODO:
                 * //#pragma pack(push,1)
                 * //    GCC Use another alignment command  __attribute__ ((aligned (1)))
                 * //    Command structure
                 * typedef struct {
                 *     uint8_t bSign;          //    Head sign
                 *     uint8_t bCmd;           //    Command type
                 *     uint8_t bCmdLen;        //    Command length (without header)
                 *     uint8_t bChkSum;        //    Command checksum
                 * } CMD_HEAD, *PCMD_HEAD __attribute__((aligned(1)));
                 *
                 * //    Serial data transmission
                 * typedef struct {
                 *     uint16_t wBitCount;     //    Length of data sent, unit: bit
                 *     uint8_t bFlag;          //    Generate check bit, CRC and other flags
                 * } CMD_SEND_DATA, *PCMD_SEND_DATA __attribute__((aligned(1)));
                 *
                 * //    Check Summing
                 * uint8_t GetChkSum(PCMD_HEAD pCmdHead, uint8_t *DataPtr) {
                 *     uint8_t bRet = 0;
                 *
                 *     if (!DataPtr)
                 *         DataPtr = (uint8_t *)(pCmdHead + 1);
                 *
                 *     bRet -= pCmdHead->bSign;
                 *     bRet -= pCmdHead->bCmd;
                 *     bRet -= pCmdHead->bCmdLen;
                 *
                 *     for (uint8_t i = 0; i < pCmdHead->bCmdLen; i++) {
                 *         bRet -= DataPtr[i];
                 *     }
                 *
                 *     return bRet;
                 * }
                 */
                return null;
            }

        }

    }

}
