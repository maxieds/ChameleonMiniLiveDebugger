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
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.FALSE;
import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.OK;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_SLOTS;
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
    public static final int CHAMELEON_TYPE_REVE = 3;
    public static final int CHAMELEON_TYPE_DFUMODE = 4;
    public static final int CHAMELEON_TYPE_DESFIRE_FWMOD = 5;

    public static boolean REVE_BOARD = false;
    public static int CHAMELEON_DEVICE_USBVID = 0x00;
    public static int CHAMELEON_DEVICE_USBPID = 0x00;
    public static int CHAMELEON_MINI_BOARD_TYPE = CHAMELEON_TYPE_UNKNOWN;
    public static String CHAMELEON_MINI_BOARD_TYPE_DESC = "<UNKNOWN>";

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
            case CHAMELEON_TYPE_KAOS_REVG:
                return "KAOS RevG Device";
            case CHAMELEON_TYPE_DESFIRE_FWMOD:
                return "DESFire Firmware Mod (Device ~ RevG)";
            default:
                return "Unknown";
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
            String deviceName = ((BluetoothSerialInterface) deviceActiveSerialIOPort).getDeviceName();
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
            Log.i(TAG, "CHAMELEON DEVICE TYPE -- " + firmwareVersion + "------" + commandsList);
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
        String statusMsg = String.format(Locale.ENGLISH, "New Chameleon discovered over %s: %s.", deviceConnType, chameleonDeviceType);
        Utils.displayToastMessageLong(statusMsg);
        return CHAMELEON_MINI_BOARD_TYPE;
    }

    public static boolean initializeDevice() {
        AndroidSettingsStorage.loadPreviousSettings(ChameleonSettings.chameleonDeviceSerialNumber);
        if(LiveLoggerActivity.getSelectedTab() == TAB_TOOLS &&
           TabFragment.UITAB_DATA[LiveLoggerActivity.getSelectedTab()].lastMenuIndex == TAB_TOOLS_MITEM_SLOTS) {
            try {
                int activeSlot = ChameleonIO.DeviceStatusSettings.DIP_SETTING;
                ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlot - 1].readParametersFromChameleonSlot(activeSlot, activeSlot);
                for (int si = 0; si < ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOT_COUNT; si++) {
                    ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[si].resetLayoutParameters(si + 1);
                    ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[si].updateLayoutParameters();
                }
            } catch(NumberFormatException nfe) {
                nfe.printStackTrace();
                return false;
            }
        }
        else {
            int selectedTab = LiveLoggerActivity.getSelectedTab();
            int selectedMenuIdx = TabFragment.UITAB_DATA[selectedTab].lastMenuIndex;
            View tabView = TabFragment.UITAB_DATA[selectedTab].tabInflatedView;
            UITabUtils.initializeTabMainContent(selectedTab, selectedMenuIdx, tabView);
            int activeSlot = Math.max(0, ChameleonIO.DeviceStatusSettings.DIP_SETTING - 1);
            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlot].readParametersFromChameleonSlot();
        }
        Log.i(TAG, "TODO: setup bi-directional sniffing if necessary ...");
        return true;
    }

    /**
     * Default timeout to use when communicating with the device.
     */
    public static int TIMEOUT = 3000;
    public static final int LOCK_TIMEOUT = 1000;

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
        TIMEOUT(203),
        OK(100),
        OK_WITH_TEXT(101),
        WAITING_FOR_MODEM(110),
        TRUE(121),
        FALSE(120),
        UNKNOWN_COMMAND(200),
        INVALID_COMMAND_USAGE(201),
        INVALID_PARAMETER(202);

        /**
         * Integer value associated with each enum value.
         */
        private int responseCode;

        /**
         * Constructor
         *
         * @param rcode
         */
        private SerialRespCode(int rcode) {
            responseCode = rcode;
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

        /**
         * The status settings summarized at the top of the GUI window.
         */
        public static String CONFIG = "NOT SET";
        public static String UID = "NOT SET";
        public static String LASTUID = "NOT SET";
        public static String LOGMODE = "NOT SET";
        public static int UIDSIZE = 0;
        public static int MEMSIZE = 0;
        public static int LOGSIZE = 0;
        public static int DIP_SETTING = 0;
        public static boolean FIELD = false;
        public static boolean READONLY = false;
        public static boolean CHARGING = false;
        public static int THRESHOLD = 0;
        public static String TIMEOUT = "NOT SET";

        /**
         * How often do we update / refresh the stats at the top of the window?
         */
        public static final int STATS_UPDATE_INTERVAL = 4500; // 4.5 seconds
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
            postingStatsInProgress = false;
        }

        public static void startPostingStats(int msDelay) {
            if(postingStatsInProgress || !ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES) {
                return;
            }
            postingStatsInProgress = true;
            statsUpdateHandler.postDelayed(statsUpdateRunnable, msDelay);
        }

        /**
         * Queries the live device for its status settings.
         */
        public static boolean updateAllStatus(boolean resetTimer) {
            try {
                if (!ChameleonIO.REVE_BOARD) {
                    CONFIG = ChameleonIO.getSettingFromDevice("CONFIG?", CONFIG);
                    UID = ChameleonIO.getSettingFromDevice("UID?", UID);
                    if(UID.equals("TIMEOUT")) {
                        UID = "NO UID.";
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
                    LOGMODE = "NONE";
                    LOGSIZE = 0;
                    DIP_SETTING = Utils.parseInt(ChameleonIO.getSettingFromDevice("setting?", String.format("%d", DIP_SETTING)));
                    READONLY = ChameleonIO.getSettingFromDevice("readonly?", String.format("%d", READONLY ? 1 : 0)).equals("1");
                    FIELD = false;
                    CHARGING = false;
                    THRESHOLD = 0;
                    TIMEOUT = "NA";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // setup threshold signal bars:
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
                return;
            }
            Thread setToolbarSettingsDataThread = new Thread() {
                @Override
                public void run() {
                    try {
                        boolean haveUpdates = updateAllStatus(resetTimer);
                    }
                    catch(Exception nfe) {
                        nfe.printStackTrace();
                        stopPostingStats();
                        return;
                    }
                    LiveLoggerActivity.getInstance().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.deviceConfigText)).setText(CONFIG);
                                String formattedUID = Utils.formatUIDString(UID, ":");
                                ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.deviceConfigUID)).setText(formattedUID);
                                String subStats1 = String.format(Locale.ENGLISH, "REV%s|MEM-%dK|LOG-%s-%dK", ChameleonIO.REVE_BOARD ? "E" : "G", round(MEMSIZE / 1024), LOGMODE, round(LOGSIZE / 1024));
                                ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.deviceStats1)).setText(subStats1);
                                String subStats2 = String.format(Locale.ENGLISH, "SLOT-%d|%s|FLD-%s|CHRG-%s", DIP_SETTING, READONLY ? "RO" : "RW", FIELD ? "1" : "0", CHARGING ? "1" : "0");
                                ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.deviceStats2)).setText(subStats2);
                                String subStats3 = String.format(Locale.ENGLISH, "THRS-%dmv|TMT-%s", THRESHOLD, TIMEOUT.replace(" ", ""));
                                ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.deviceStats3)).setText(subStats3);
                                SeekBar thresholdSeekbar = (SeekBar) LiveLoggerActivity.getInstance().findViewById(R.id.thresholdSeekbar);
                                if (thresholdSeekbar != null) {
                                    thresholdSeekbar.setProgress(THRESHOLD);
                                    ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.thresholdSeekbarValueText)).setText(String.format(Locale.ENGLISH, "% 5d mV", THRESHOLD));
                                }
                                SeekBar timeoutSeekbar = (SeekBar) LiveLoggerActivity.getInstance().findViewById(R.id.cmdTimeoutSeekbar);
                                if (thresholdSeekbar != null) {
                                    thresholdSeekbar.setProgress(THRESHOLD);
                                    ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.cmdTimeoutSeekbarValueText)).setText(String.format(Locale.ENGLISH, "% 4d (x128) ms", TIMEOUT));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
            };
            setToolbarSettingsDataThread.start();
            if (resetTimer && postingStatsInProgress) {
                statsUpdateHandler.removeCallbacksAndMessages(statsUpdateRunnable);
                statsUpdateHandler.postDelayed(statsUpdateRunnable, STATS_UPDATE_INTERVAL);
            }
        }
    }

    public static DeviceStatusSettings deviceStatus = new ChameleonIO.DeviceStatusSettings();

    /**
     * Put the device into sniffer / logger mode.
     *
     * @param timeout
     * @return SerialRespCode status code (OK)
     */
    public static SerialRespCode setLoggerConfigMode(int timeout) {
        // TODO: Modern firmware may have multiple sniffer modes ...
        if(!REVE_BOARD) {
            SerialRespCode cfgResp = executeChameleonMiniCommand("CONFIG=ISO14443A_SNIFF", timeout);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return cfgResp;
        }
        else {
            SerialRespCode cfgResp = executeChameleonMiniCommand("config=ISO14443A_SNIFF", timeout);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return cfgResp;
        }
    }

    /**
     * Put the device into reader mode.
     *
     * @param timeout
     * @return SerialRespCode status code (OK)
     */
    public static SerialRespCode setReaderConfigMode(int timeout) {
        // TODO: Modern firmware may have multiple reader modes ...
        if(!REVE_BOARD) {
            SerialRespCode cfgResp = executeChameleonMiniCommand("CONFIG=ISO14443A_READER", timeout);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return cfgResp;
        }
        else {
            SerialRespCode cfgResp = executeChameleonMiniCommand("config=ISO14443A_READER", timeout);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return cfgResp;
        }
    }

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
            Log.i(TAG, "executeChameleonMiniCommand: PAUSED.");
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
            Log.i(TAG, "serial port is null executing command");
            return null;
        }
        Log.i(TAG, "sending data buffer");
        serialPort.sendDataBuffer(sendBuf);
        return OK;
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
            Log.i(TAG, "Serial port is null");
            return ChameleonIO.DEVICE_RESPONSE[0];
        }
        else if(!serialIOPort.tryAcquireSerialPort(LOCK_TIMEOUT)) {
            Log.i(TAG, "Unable to acquire serial port");
            return ChameleonIO.DEVICE_RESPONSE[0];
        }
        ChameleonIO.WAITING_FOR_RESPONSE = true;
        ChameleonIO.executeChameleonMiniCommand(query, TIMEOUT);
        for(int i = 0; i < ChameleonIO.TIMEOUT / 50; i++) {
            if(!ChameleonIO.WAITING_FOR_RESPONSE)
                break;
            try {
                Thread.sleep(50);
            } catch(InterruptedException ie) {
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
            nfe.printStackTrace();
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