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
import android.os.SystemClock;
import android.widget.SeekBar;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.FALSE;
import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.OK;
import static java.lang.Math.round;

/**
 * <h1>Chameleon IO Wrapper</h1>
 * The ChameleonIO class provides subclasses storing status configurations of the
 * attached device and utilities for sending commands to the Chameleon Mini.
 *
 * @author Maxie D. Schmidt
 * @since 12/31/2017
 */
public class ChameleonIO {

    private static final String TAG = ChameleonIO.class.getSimpleName();

    /**
     * Chameleon Mini USB device ID information (hex codes).
     */
    public static final int CMUSB_VENDORID = 0x16d0;
    public static final int CMUSB_PRODUCTID = 0x04b2;
    public static final int CMUSB_REVE_VENDORID = 0x03eb;
    public static final int CMUSB_REVE_PRODUCTID = 0x2044;
    public static final int CMUSB_DFUMODE_VENDORID = 0x03eb;
    public static final int CMUSB_DFUMODE_PRODUCTID = 0x2fde;

    public static final int CHAMELEON_TYPE_UNKNOWN = -1;
    public static final int CHAMELEON_TYPE_KAOS_REVG = 0;
    public static final int CHAMELEON_TYPE_PROXGRIND_REVG = 1;
    public static final int CHAMELEON_TYPE_PROXGRIND_REVG_TINY = 2;
    public static final int CHAMELEON_TYPE_PROXGRIND_REVG_TINYPRO = 3;
    public static final int CHAMELEON_TYPE_REVE = 4;
    public static final int CHAMELEON_TYPE_DFUMODE = 5;
    public static final int CHAMELEON_TYPE_DESFIRE_FWMOD = 6;

    public static boolean REVE_BOARD = false;
    public static int CHAMELEON_DEVICE_USBVID = 0x00;
    public static int CHAMELEON_DEVICE_USBPID = 0x00;
    public static int CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_UNKNOWN;
    public static String CHAMELEON_MINI_BOARD_TYPE_DESC = ChameleonSettings.CMINI_DEVICE_FIELD_NONE;

    public static String getDeviceDescription(int chameleonBoardType) {
        switch(chameleonBoardType) {
            case CHAMELEON_TYPE_DFUMODE:
                return "DFU Bootloader Programmer Mode";
            case CHAMELEON_TYPE_REVE:
                return "RevE Device";
            case CHAMELEON_TYPE_PROXGRIND_REVG:
                return "Proxgrind RevG Device";
            case CHAMELEON_TYPE_PROXGRIND_REVG_TINY:
                return "Proxgrind Tiny Device";
            case CHAMELEON_TYPE_PROXGRIND_REVG_TINYPRO:
                return "Proxgrind TinyPro Device";
            case CHAMELEON_TYPE_KAOS_REVG:
                return "KAOS RevG Device";
            case CHAMELEON_TYPE_DESFIRE_FWMOD:
                return "DESFire Firmware Mod (~RevG)";
            default:
                return "Unknown Device Type";
        }
    }

    public static int detectChameleonType() {
        ChameleonSerialIOInterface deviceActiveSerialIOPort = ChameleonSettings.getActiveSerialIOPort();
        if(deviceActiveSerialIOPort == null) {
            return CHAMELEON_TYPE_UNKNOWN;
        }
        String deviceConnType = deviceActiveSerialIOPort.isWiredUSB() ? "USB" : "Bluetooth";
        CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_UNKNOWN;
        if(CHAMELEON_DEVICE_USBVID == CMUSB_DFUMODE_VENDORID &&
           CHAMELEON_DEVICE_USBPID == CMUSB_DFUMODE_PRODUCTID) {
            CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_DFUMODE;
        }
        else if(REVE_BOARD) {
            CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_REVE;
        }
        else if(deviceActiveSerialIOPort.isBluetooth()) {
            String deviceName = ((BluetoothBLEInterface) deviceActiveSerialIOPort).getDeviceName();
            if(deviceName.equals(BluetoothGattConnector.CHAMELEON_REVG_NAME)) {
                CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_PROXGRIND_REVG;
            }
            else if(deviceName.equals(BluetoothGattConnector.CHAMELEON_REVG_TINY_NAME)) {
                CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_PROXGRIND_REVG_TINY;
            }
        }
        else {
            String firmwareVersion = getSettingFromDevice("VERSION?");
            String commandsList = getSettingFromDevice("HELP");
            AndroidLogger.i(TAG, "CHAMELEON DEVICE TYPE -- " + firmwareVersion + "------" + commandsList);
            if(firmwareVersion.contains("DESFire") ||
                    (deviceConnType.equals("USB") && deviceActiveSerialIOPort.getActiveDeviceInfo().contains("DESFireMod"))) {
                CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_DESFIRE_FWMOD;
            }
            else if (firmwareVersion.contains("RevG") && firmwareVersion.contains("emsec")) {
                if (commandsList.contains("SAKMODE")) {
                    CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_PROXGRIND_REVG;
                } else if (commandsList.contains("MEMORYINFO")) {
                    CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_PROXGRIND_REVG_TINY;
                } else {
                    CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_KAOS_REVG;
                }
            }
            else if (firmwareVersion.contains("RevG")) {
                if (commandsList.contains("SAKMODE")) {
                    CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_PROXGRIND_REVG;
                } else if (commandsList.contains("MEMORYINFO")) {
                    CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_PROXGRIND_REVG_TINY;
                }
            }
        }
        String chameleonDeviceType = getDeviceDescription(CHAMELEON_MINI_BOARD_TYPE);
        CHAMELEON_MINI_BOARD_TYPE_DESC = chameleonDeviceType;
        String statusMsg = String.format(BuildConfig.DEFAULT_LOCALE, "New Chameleon discovered over %s: %s.", deviceConnType, chameleonDeviceType);
        Utils.displayToastMessageLong(statusMsg);
        return CHAMELEON_MINI_BOARD_TYPE;
    }

    /**
     * Default timeout to use when communicating with the device.
     */
    public static int TIMEOUT = 3000;
    public static final int LOCK_TIMEOUT = 1000;
    public static final int LONG_USER_TIMEOUT = 5000;
    public static final long BLE_GATT_CHAR_WRITE_TIMEOUT = 2000;
    public static final long NOTHREAD_SLEEP_INTERVAL = 50;

    /**
     * Static constants for storing state of the device.
     */
    public static boolean PAUSED = true;
    public static boolean WAITING_FOR_RESPONSE = false;
    public static boolean WAITING_FOR_XMODEM = false;
    public static boolean DOWNLOAD = false;
    public static boolean UPLOAD = false;
    public static boolean EXPECTING_BINARY_DATA = false;
    public static String LASTCMD = "";
    public static byte[] PRIOR_BUFFER_DATA = new byte[0];
    public static boolean APPEND_PRIOR_BUFFER_DATA = false;

    /**
     * Static storage for command return values.
     * Used to avoid overhead of passing messages for the command responses.
     *
     * @ref LiveLoggerActivity.getSettingFromDevice
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static String DEVICE_RESPONSE_CODE = "";
    public static String[] DEVICE_RESPONSE = new String[0];
    public static byte[] DEVICE_RESPONSE_BINARY = new byte[0];

    /**
     * <h1>Serial Response Code</h1>
     * The class SerialRespCode contains extended enum definitions of the possible response
     * codes returned by the device. Also provides helper methods.
     */
    public enum SerialRespCode {

        /**
         * List of the status codes and their corresponding text descriptions
         * (taken almost verbatim from the ChameleonMini source code).
         */
        TIMEOUT(203, "TIMEOUT"),
        OK(100, "OK"),
        OK_WITH_TEXT(101, "OK WITH TEXT"),
        WAITING_FOR_MODEM(110, "WAITING FOR XMODEM"),
        TRUE(121, "TRUE"),
        FALSE(120, "FALSE"),
        UNKNOWN_COMMAND(200, "UNKNOWN COMMAND"),
        INVALID_COMMAND_USAGE(201, "INVALID COMMAND USAGE"),
        INVALID_PARAMETER(202, "INVALID PARAMETER");

        private int responseCode;
        private String responseMsg;

        private SerialRespCode(int rcode, String rmsg) {
            responseCode = rcode;
            responseMsg = rmsg;
        }

        /**
         * Stores a map of integer-valued response codes to their corresponding enum value.
         */
        private static final Map<Integer, SerialRespCode> RESP_CODE_MAP = new HashMap<>();

        static {
            for (SerialRespCode respCode : values()) {
                int rcode = respCode.toInteger();
                Integer aRespCode = Integer.valueOf(rcode);
                RESP_CODE_MAP.put(aRespCode, respCode);
            }
        }

        /**
         * Lookup table of String response codes prefixing command return data sent by the device.
         *
         * @ref ChameleonIO.isCommandResponse
         */
        public static final Map<String, SerialRespCode> RESP_CODE_TEXT_MAP = new HashMap<>();
        public static final Map<String, SerialRespCode> RESP_CODE_TEXT_MAP2 = new HashMap<>();

        static {
            for (SerialRespCode respCode : values()) {
                String rcode = String.valueOf(respCode.toInteger());
                String rcodeText = respCode.name().replace("_", " ");
                RESP_CODE_TEXT_MAP.put(rcode + ":" + rcodeText, respCode);
                RESP_CODE_TEXT_MAP2.put(rcode, respCode);
            }
        }

        /**
         * Retrieve the integer-valued response code associated with the enum value.
         *
         * @return int response code
         */
        public int toInteger() {
            return responseCode;
        }

        /**
         * Lookup the enum value by its associated integer response code value.
         *
         * @param rcode
         * @return SerialRespCode enum value associated with the integer code
         */
        public static SerialRespCode lookupByResponseCode(int rcode) {
            return RESP_CODE_MAP.get(rcode);
        }

        public String getChameleonTerminalResponse() {
            String codeMsgDelim = responseMsg.length() == 0 ? "" : ":";
            return String.format(BuildConfig.DEFAULT_LOCALE, "%d%s%s\n\r", responseCode, codeMsgDelim, responseMsg);
        }

        public static String getChameleonTerminalResponse(int rcode) {
            SerialRespCode srCode = RESP_CODE_MAP.get(rcode);
            if (srCode == null) {
                srCode = RESP_CODE_MAP.get(FALSE.responseCode);
            }
            return srCode.getChameleonTerminalResponse();
        }

        public static String getChameleonTerminalResponse(SerialRespCode srCode) {
            if (srCode == null) {
                srCode = RESP_CODE_MAP.get(FALSE.responseCode);
            }
            return srCode.getChameleonTerminalResponse();
        }

    }

    /**
     * Determines whether the received serial byte data is a command response sent by the device
     * (as opposed to a LIVE log sent by the device).
     *
     * @param liveLogData
     * @return boolean whether the log data is a response to an issued command
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static boolean isCommandResponse(byte[] liveLogData) {
        String[] respTextArray = new String(liveLogData).split("[\n\r][\n\r]+");
        if(respTextArray.length == 0) {
            return false;
        }
        String respText = respTextArray[0];
        respText.replaceAll("(\\d+):.+", "$1").replaceAll(".+(\\d+)", "$1");
        if(SerialRespCode.RESP_CODE_TEXT_MAP.get(respText) != null)
            return true;
        respText = new String(liveLogData).split(":")[0];
        if(respText.length() >= 3 && SerialRespCode.RESP_CODE_TEXT_MAP2.get(respText.substring(respText.length() - 3)) != null)
            return true;
        return false;
    }

    /**
     * <h1>Device Status Settings</h1>
     * The class DeviceStatusSettings stores status information about the live connected device.
     */
    public static class DeviceStatusSettings {

        private static final String UNSET_VALUE_NONE = "NONE";
        private static final String UNSET_VALUE_NA = "N/A";
        private static final String UID_NONE = "NO UID";

        public static final String DEFAULT_CONFIG = "NO-CONFIG";
        public static final String DEFAULT_UID = UID_NONE;
        public static final String DEFAULT_LOGMODE = UNSET_VALUE_NA;
        public static final int DEFAULT_UIDSIZE = 0;
        public static final int DEFAULT_MEMSIZE = 0;
        public static final int DEFAULT_LOGSIZE = 0;
        public static final int DEFAULT_DIP_SETTING = 0;
        public static final boolean DEFAULT_FIELD = false;
        public static final boolean DEFAULT_READONLY = false;
        public static final boolean DEFAULT_CHARGING = false;
        public static final int DEFAULT_THRESHOLD = 0;
        public static final String DEFAULT_TIMEOUT = UNSET_VALUE_NA;

        /**
         * The status settings summarized at the top of the GUI window.
         */
        public static String CONFIG = DEFAULT_CONFIG;
        public static String UID = DEFAULT_UID;
        public static String LASTUID = DEFAULT_UID;
        public static String LOGMODE = DEFAULT_LOGMODE;
        public static int UIDSIZE = DEFAULT_UIDSIZE;
        public static int MEMSIZE = DEFAULT_MEMSIZE;
        public static int LOGSIZE = DEFAULT_LOGSIZE;
        public static int DIP_SETTING = DEFAULT_DIP_SETTING;
        public static boolean FIELD = DEFAULT_FIELD;
        public static boolean READONLY = DEFAULT_READONLY;
        public static boolean CHARGING = DEFAULT_CHARGING;
        public static int THRESHOLD = DEFAULT_THRESHOLD;
        public static String TIMEOUT = DEFAULT_TIMEOUT;

        /**
         * How often do we update / refresh the stats at the top of the window?
         */
        public static final int STATS_UPDATE_INTERVAL = 6500; // ~6.5 seconds
        public static boolean postingStatsInProgress = false;
        public static Handler statsUpdateHandler = new Handler();
        public static Runnable statsUpdateRunnable = new Runnable() {
            public void run() {
                if(ChameleonSettings.getActiveSerialIOPort() == null || !ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES) {
                    statsUpdateHandler.removeCallbacksAndMessages(this);
                    postingStatsInProgress = false;
                }
                else {
                    updateAllStatusAndPost(true);
                }
            }
        };

        public static void stopPostingStats() {
            statsUpdateHandler.removeCallbacksAndMessages(statsUpdateRunnable);
            setToolbarStatsToDefault();
            postingStatsInProgress = false;
        }

        public static void setToolbarStatsToDefault() {
            CONFIG = DEFAULT_CONFIG;
            UID = LASTUID = DEFAULT_UID;
            LOGMODE = DEFAULT_LOGMODE;
            UIDSIZE = DEFAULT_UIDSIZE;
            MEMSIZE = DEFAULT_MEMSIZE;
            LOGSIZE = DEFAULT_LOGSIZE;
            DIP_SETTING = DEFAULT_DIP_SETTING;
            FIELD = DEFAULT_FIELD;
            READONLY = DEFAULT_READONLY;
            CHARGING = DEFAULT_CHARGING;
            THRESHOLD = DEFAULT_THRESHOLD;
            TIMEOUT = DEFAULT_TIMEOUT;
            Thread setToolbarResetDefaultSettingsDataThread = new Thread() {
                @Override
                public void run() {
                    LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceConfigText)).setText(CONFIG);
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceConfigUID)).setText(UID);
                                String subStats1 = String.format(BuildConfig.DEFAULT_LOCALE, "REV%s | MEM-%dK | LOG-%s-%dK", ChameleonIO.REVE_BOARD ? "E" : "G", round(MEMSIZE / 1024), LOGMODE, round(LOGSIZE / 1024));
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceStats1)).setText(subStats1);
                                String subStats2 = String.format(BuildConfig.DEFAULT_LOCALE, "SLOT-%d | %s | FLD-%s | CHRG-%s", DIP_SETTING, READONLY ? "RO" : "RW", FIELD ? "1" : "0", CHARGING ? "1" : "0");
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceStats2)).setText(subStats2);
                                String subStats3 = String.format(BuildConfig.DEFAULT_LOCALE, "THRS-%dmv | TMT-%s", THRESHOLD, TIMEOUT.replace(" ", ""));
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceStats3)).setText(subStats3);
                                LiveLoggerActivity.setSignalStrengthIndicator(THRESHOLD);
                            } catch (Exception ex) {
                                AndroidLogger.printStackTrace(ex);
                            }
                        }
                    });
                }
            };
            setToolbarResetDefaultSettingsDataThread.start();
        }

        public static void startPostingStats(int msDelay) {
            if(postingStatsInProgress || !ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES) {
                statsUpdateHandler.removeCallbacksAndMessages(statsUpdateRunnable);
                return;
            }
            postingStatsInProgress = true;
            statsUpdateHandler.postDelayed(statsUpdateRunnable, msDelay);
        }

        /**
         * Queries the live device for its status settings.
         */
        public static boolean updateAllStatus() {
            try {
                if (!ChameleonIO.REVE_BOARD) {
                    CONFIG = ChameleonIO.getSettingFromDevice("CONFIG?", CONFIG);
                    UID = ChameleonIO.getSettingFromDevice("UID?", UID);
                    if(UID.equals("TIMEOUT")) {
                        UID = UID_NONE;
                    }
                    UIDSIZE = Utils.parseInt(ChameleonIO.getSettingFromDevice("UIDSIZE?", String.format("%d", UIDSIZE)));
                    MEMSIZE = Utils.parseInt(ChameleonIO.getSettingFromDevice("MEMSIZE?", String.format("%d", MEMSIZE)));
                    LOGMODE = ChameleonIO.getSettingFromDevice("LOGMODE?", String.format("%d", LOGSIZE)).replaceAll(" \\(.*\\)", "");
                    LOGSIZE = Utils.parseInt(ChameleonIO.getSettingFromDevice("LOGMEM?", String.format("%d", LOGSIZE)).replaceAll(" \\(.*\\)", ""));
                    DIP_SETTING = Utils.parseInt(ChameleonIO.getSettingFromDevice("SETTING?", String.format("%d", DIP_SETTING)));
                    READONLY = ChameleonIO.getSettingFromDevice("READONLY?", String.format("%d", READONLY ? 1 : 0)).equals("1");
                    FIELD = ChameleonIO.getSettingFromDevice("FIELD?", String.format("%d", FIELD ? 1 : 0)).equals("1");
                    CHARGING = ChameleonIO.getSettingFromDevice("CHARGING?", String.format("%d", CHARGING ? 1 : 0)).equals("TRUE");
                    THRESHOLD = Utils.parseInt(ChameleonIO.getSettingFromDevice("THRESHOLD?", String.format("%d", THRESHOLD)));
                    TIMEOUT = ChameleonIO.getSettingFromDevice("TIMEOUT?", TIMEOUT);
                }
                else {
                    CONFIG = ChameleonIO.getSettingFromDevice("config?", CONFIG);
                    UID = ChameleonIO.getSettingFromDevice("uid?", UID);
                    UIDSIZE = Utils.parseInt(ChameleonIO.getSettingFromDevice("uidsize?", String.format("%d",UIDSIZE)));
                    MEMSIZE = Utils.parseInt(ChameleonIO.getSettingFromDevice("memsize?", String.format("%d",MEMSIZE)));
                    LOGMODE = UNSET_VALUE_NA;
                    LOGSIZE = 0;
                    DIP_SETTING = Utils.parseInt(ChameleonIO.getSettingFromDevice("setting?", String.format("%d", DIP_SETTING)));
                    READONLY = ChameleonIO.getSettingFromDevice("readonly?", String.format("%d", READONLY ? 1 : 0)).equals("1");
                    FIELD = false;
                    CHARGING = false;
                    THRESHOLD = 0;
                    TIMEOUT = UNSET_VALUE_NA;
                }
            } catch (Exception ex) {
                AndroidLogger.printStackTrace(ex);
                return false;
            }
            LiveLoggerActivity.setSignalStrengthIndicator(THRESHOLD);
            return true;
        }

        /**
         * Updates all status settings and posts the results to the live activity window.
         *
         * @param resetTimer whether to have this execute again in STATS_UPDATE_INTERVAL milliseconds
         * @ref DeviceStatusSettings.STATS_UPDATE_INTERVAL
         * @ref DeviceStatusSettings.updateAllStatus
         */
        public static void updateAllStatusAndPost(boolean resetTimer) {
            if(ChameleonSettings.getActiveSerialIOPort() == null) {
                stopPostingStats();
                setToolbarStatsToDefault();
                return;
            }
            Thread setToolbarSettingsDataThread = new Thread() {
                @Override
                public void run() {
                    try {
                        boolean haveUpdates = updateAllStatus();
                    }
                    catch(Exception nfe) {
                        AndroidLogger.printStackTrace(nfe);
                        stopPostingStats();
                        return;
                    }
                    LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                if(!CONFIG.equals("TIMEOUT") && !CONFIG.equals("")) {
                                    ((TextView) LiveLoggerActivity.getContentView(R.id.deviceConfigText)).setText(CONFIG);
                                }
                                if(!UID.equals("TIMEOUT") && !UID.equals("") && Utils.stringIsHexadecimal(UID)) {
                                    String formattedUID = Utils.formatUIDString(UID, ":");
                                    ((TextView) LiveLoggerActivity.getContentView(R.id.deviceConfigUID)).setText(formattedUID);
                                }
                                String subStats1 = String.format(BuildConfig.DEFAULT_LOCALE, "REV%s | MEM-%dK | LOG-%s-%dK", ChameleonIO.REVE_BOARD ? "E" : "G", round(MEMSIZE / 1024), LOGMODE, round(LOGSIZE / 1024));
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceStats1)).setText(subStats1);
                                String subStats2 = String.format(BuildConfig.DEFAULT_LOCALE, "SLOT-%d | %s | FLD-%s | CHRG-%s", DIP_SETTING, READONLY ? "RO" : "RW", FIELD ? "1" : "0", CHARGING ? "1" : "0");
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceStats2)).setText(subStats2);
                                String subStats3 = String.format(BuildConfig.DEFAULT_LOCALE, "THRS-%dmv | TMT-%s", THRESHOLD, TIMEOUT.replace(" ", ""));
                                ((TextView) LiveLoggerActivity.getContentView(R.id.deviceStats3)).setText(subStats3);
                                SeekBar thresholdSeekbar = (SeekBar) LiveLoggerActivity.getContentView(R.id.thresholdSeekbar);
                                if (thresholdSeekbar != null) {
                                    thresholdSeekbar.setProgress(THRESHOLD);
                                    ((TextView) LiveLoggerActivity.getContentView(R.id.thresholdSeekbarValueText)).setText(String.format(BuildConfig.DEFAULT_LOCALE, "% 5d mV", THRESHOLD));
                                }
                                SeekBar timeoutSeekbar = (SeekBar) LiveLoggerActivity.getContentView(R.id.cmdTimeoutSeekbar);
                                if (thresholdSeekbar != null) {
                                    thresholdSeekbar.setProgress(THRESHOLD);
                                    ((TextView) LiveLoggerActivity.getContentView(R.id.cmdTimeoutSeekbarValueText)).setText(String.format(BuildConfig.DEFAULT_LOCALE, "% 4s (x128) ms", TIMEOUT));
                                }
                            } catch (Exception ex) {
                                AndroidLogger.printStackTrace(ex);
                            }
                        }
                    });
                }
            };
            setToolbarSettingsDataThread.start();
            if (resetTimer) {
                statsUpdateHandler.removeCallbacksAndMessages(statsUpdateRunnable);
                statsUpdateHandler.postDelayed(statsUpdateRunnable, STATS_UPDATE_INTERVAL);
            }
        }
    }

    public static DeviceStatusSettings deviceStatus = new ChameleonIO.DeviceStatusSettings();

    /**
     * Executes the passed command by sending the command to the device.
     * The response returned by the device is handled separately elsewhere in the program.
     *
     * @param rawCmd
     * @param timeout
     * @return SerialRespCode status code (OK)
     * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/Page_CommandLine.html
     */
    public static SerialRespCode executeChameleonMiniCommand(String rawCmd, int timeout) {
        if(PAUSED) {
            AndroidLogger.i(TAG, "executeChameleonMiniCommand: PAUSED.");
            return FALSE;
        }
        if (timeout < 0) {
            timeout *= -1;
            SystemClock.sleep(timeout);
        }
        String deviceConfigCmd = rawCmd + (REVE_BOARD ? "\r\n" : "\n\r");
        byte[] sendBuf = deviceConfigCmd.getBytes(StandardCharsets.UTF_8);
        ChameleonSerialIOInterface serialPort = ChameleonSettings.getActiveSerialIOPort();
        if(serialPort == null) {
            AndroidLogger.i(TAG, "Serial port is null while executing command");
            return null;
        }
        if (serialPort.sendDataBuffer(sendBuf) == SerialIOReceiver.STATUS_OK) {
            return OK;
        } else {
            return null;
        }
    }

    /**
     * Queries the Chameleon device with the query command and returns its response
     * (sans the preceeding ascii status code).
     * @param query
     * @return String device response
     * @ref ChameleonIO.DEVICE_RESPONSE
     * @ref ChameleonIO.DEVICE_RESPONSE_CODE
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static String getSettingFromDevice(String query, String hint) {
        ChameleonIO.DEVICE_RESPONSE = new String[1];
        ChameleonIO.DEVICE_RESPONSE[0] = (hint == null) ? "TIMEOUT" : hint;
        ChameleonIO.LASTCMD = query;
        ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
        if(serialIOPort == null) {
            AndroidLogger.i(TAG, "Serial port is null");
            return ChameleonIO.DEVICE_RESPONSE[0];
        }
        else if(!serialIOPort.tryAcquireSerialPort(LOCK_TIMEOUT)) {
            AndroidLogger.i(TAG, "Unable to acquire serial port");
            return ChameleonIO.DEVICE_RESPONSE[0];
        }
        ChameleonIO.WAITING_FOR_RESPONSE = true;
        ChameleonIO.executeChameleonMiniCommand(query, TIMEOUT);
        for(int i = 0; i < ChameleonIO.TIMEOUT / 50; i++) {
            if(!ChameleonIO.WAITING_FOR_RESPONSE) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ChameleonIO.WAITING_FOR_RESPONSE = false;
                break;
            }
        }
        int deviceRespCode = -1;
        try {
            if(ChameleonIO.DEVICE_RESPONSE_CODE == null) {
                ChameleonIO.DEVICE_RESPONSE_CODE = "";
            }
            else if(ChameleonIO.DEVICE_RESPONSE_CODE.length() >= 3) {
                deviceRespCode = Integer.valueOf(ChameleonIO.DEVICE_RESPONSE_CODE.substring(0, 3));
            }
            else {
                deviceRespCode = Integer.valueOf(ChameleonIO.DEVICE_RESPONSE_CODE);
            }
        } catch(NumberFormatException nfe) {
            AndroidLogger.printStackTrace(nfe);
            serialIOPort.releaseSerialPortLock();
            return ChameleonIO.DEVICE_RESPONSE_CODE;
        }
        serialIOPort.releaseSerialPortLock();
        if(deviceRespCode != ChameleonIO.SerialRespCode.OK.toInteger() &&
                deviceRespCode != ChameleonIO.SerialRespCode.OK_WITH_TEXT.toInteger()) {
            return ChameleonIO.DEVICE_RESPONSE_CODE;
        }
        String retValue = ChameleonIO.DEVICE_RESPONSE[0] != null ? ChameleonIO.DEVICE_RESPONSE[0] : "";
        if(retValue.equals("201:INVALID COMMAND USAGE")) {
            retValue += " (Are you in READER mode?)";
        }
        return retValue;
    }

    /**
     * Queries the Chameleon device with the query command and returns its response
     * (sans the preceeding ascii status code).
     * @param query
     * @return String device response
     * @ref ChameleonIO.DEVICE_RESPONSE
     * @ref ChameleonIO.DEVICE_RESPONSE_CODE
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static String getSettingFromDevice(String query) {
        return ChameleonIO.getSettingFromDevice(query, null);
    }

}