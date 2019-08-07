package com.maxieds.chameleonminilivedebugger;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.shawnlin.numberpicker.NumberPicker;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    public static boolean REVE_BOARD = false;

    /**
     * Default timeout to use when communicating with the device.
     */
    public static int TIMEOUT = 3000;
    public static final int LOCK_TIMEOUT = 350;

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
    public static boolean USB_CONFIGURED = false;

    /**
     * Static storage for command return values.
     * Used to avoid overhead of passing messages for the command responses.
     *
     * @ref LiveLoggerActivity.getSettingFromDevice
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static String DEVICE_RESPONSE_CODE;
    public static String[] DEVICE_RESPONSE;
    public static byte[] DEVICE_RESPONSE_BINARY;

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
        Log.i(TAG, "liveLogData: " + new String(liveLogData));
        String respText = new String(liveLogData).split("[\n\r]+")[0];
        String[] respText2 = new String(liveLogData).split("=");
        if(SerialRespCode.RESP_CODE_TEXT_MAP.get(respText) != null)
            return true;
        respText = new String(liveLogData).split(":")[0];
        if(respText.length() >= 3 && SerialRespCode.RESP_CODE_TEXT_MAP2.get(respText.substring(respText.length() - 3)) != null)
            return true;
        //else if (respText2.length >= 2 && SerialRespCode.RESP_CODE_TEXT_MAP.get(LASTCMD.replace(respText2[1].substring(0, min(respText2[1].length(), LiveLoggerActivity.USB_DATA_BITS)), "")) != null)
        //    return false;
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
        public String CONFIG;
        public String UID;
        public String LASTUID = "00000000000000";
        public String LOGMODE = "NONE";
        public int UIDSIZE;
        public int MEMSIZE;
        public int LOGSIZE;
        public int DIP_SETTING;
        public boolean FIELD;
        public boolean READONLY;
        public boolean CHARGING;
        public int THRESHOLD;
        public String TIMEOUT;

        /**
         * How often do we update / refresh the stats at the top of the window?
         */
        public final int STATS_UPDATE_INTERVAL = 8000; // 8 seconds
        public Handler statsUpdateHandler = new Handler();
        public Runnable statsUpdateRunnable = new Runnable() {
            public void run() {
                updateAllStatusAndPost(true);
            }
        };

        /**
         * Queries the live device for its status settings.
         */
        private boolean updateAllStatus(boolean resetTimer) {
            if (!ChameleonIO.REVE_BOARD) {
                CONFIG = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "CONFIG?", CONFIG);
                UID = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "UID?", UID);
                UIDSIZE = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "UIDSIZE?", String.format("%d", UIDSIZE)));
                MEMSIZE = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "MEMSIZE?", String.format("%d", MEMSIZE)));
                LOGMODE = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "LOGMODE?", String.format("%d", LOGSIZE)).replaceAll(" \\(.*\\)", "");
                LOGSIZE = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "LOGMEM?", String.format("%d", LOGSIZE)).replaceAll(" \\(.*\\)", ""));
                DIP_SETTING = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "SETTING?", String.format("%d", DIP_SETTING)));
                READONLY = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "READONLY?", String.format("%d", READONLY ? 1 : 0)).equals("1");
                FIELD = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "FIELD?", String.format("%d", FIELD ? 1 : 0)).equals("1");
                CHARGING = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "CHARGING?", String.format("%d", CHARGING ? 1 : 0)).equals("TRUE");
                THRESHOLD = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "THRESHOLD?", String.format("%d", THRESHOLD)));
                TIMEOUT = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "TIMEOUT?", TIMEOUT);
            }
            else {
                CONFIG = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "config?", CONFIG);
                UID = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "uid?", UID);
                UIDSIZE = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "uidsize?", String.format("%d",UIDSIZE)));
                MEMSIZE = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "memsize?", String.format("%d",MEMSIZE)));
                LOGMODE = "NONE";
                LOGSIZE = 0;
                DIP_SETTING = Utils.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "setting?", String.format("%d", DIP_SETTING)));
                READONLY = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "readonly?", String.format("%d", READONLY ? 1 : 0)).equals("1");
                FIELD = false;
                CHARGING = false;
                THRESHOLD = 0;
                TIMEOUT = "NA";
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
        public void updateAllStatusAndPost(boolean resetTimer) {
            if (LiveLoggerActivity.serialPort == null)
                return;
            boolean haveUpdates = updateAllStatus(resetTimer);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceConfigText)).setText(CONFIG);
            String formattedUID = UID;
            if (!UID.equals("NO UID."))
                formattedUID = UID.replaceAll("..(?!$)", "$0:");
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceConfigUID)).setText(Utils.trimString(formattedUID, "DEVICE CONFIGURATION".length()));
            String subStats1 = String.format(Locale.ENGLISH, "MEM-%dK/LMEM-%dK/LMD-%s/REV%s", round(MEMSIZE / 1024), round(LOGSIZE / 1024), LOGMODE, ChameleonIO.REVE_BOARD ? "E" : "G");
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats1)).setText(subStats1);
            String subStats2 = String.format(Locale.ENGLISH, "DIP#%d/%s/FLD-%d/%sCHRG", DIP_SETTING, READONLY ? "RO" : "RW", FIELD ? 1 : 0, CHARGING ? "+" : "NO-");
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats2)).setText(subStats2);
            String subStats3 = String.format(Locale.ENGLISH, "THRS-%d mv/TMT-%s", THRESHOLD, TIMEOUT);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats3)).setText(subStats3);
            SeekBar thresholdSeekbar = (SeekBar) LiveLoggerActivity.runningActivity.findViewById(R.id.thresholdSeekbar);
            if (thresholdSeekbar != null) {
                thresholdSeekbar.setProgress(THRESHOLD);
                ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.thresholdSeekbarValueText)).setText(String.format(Locale.ENGLISH, "% 5d mV", THRESHOLD));
            }
            NumberPicker settingsNumberPicker = (NumberPicker) LiveLoggerActivity.runningActivity.findViewById(R.id.settingsNumberPicker);
            if (settingsNumberPicker != null) {
                settingsNumberPicker.setValue(DIP_SETTING);
            }
            if (resetTimer) {
                statsUpdateHandler.removeCallbacks(statsUpdateRunnable);
                statsUpdateHandler.postDelayed(statsUpdateRunnable, STATS_UPDATE_INTERVAL);
            }
        }
    }

    public static DeviceStatusSettings deviceStatus = new ChameleonIO.DeviceStatusSettings();

    /**
     * Put the device into sniffer / logger mode.
     *
     * @param cmPort
     * @param timeout
     * @return SerialRespCode status code (OK)
     */
    public static SerialRespCode setLoggerConfigMode(UsbSerialDevice cmPort, int timeout) {
        if(!REVE_BOARD)
             return executeChameleonMiniCommand(cmPort, "CONFIG=ISO14443A_SNIFF", timeout);
        else
             return executeChameleonMiniCommand(cmPort, "config=ISO14443A_SNIFF", timeout);
    }

    /**
     * Put the device into reader mode.
     *
     * @param cmPort
     * @param timeout
     * @return SerialRespCode status code (OK)
     */
    public static SerialRespCode setReaderConfigMode(UsbSerialDevice cmPort, int timeout) {
        if(!REVE_BOARD)
             return executeChameleonMiniCommand(cmPort, "CONFIG=ISO14443A_READER", timeout);
        else
             return executeChameleonMiniCommand(cmPort, "config=ISO14443A_READER", timeout);
    }

    /**
     * Enables LIVE logging on the device.
     *
     * @param cmPort
     * @param timeout
     * @return SerialRespCode status code (OK)
     */
    public static SerialRespCode enableLiveDebugging(UsbSerialDevice cmPort, int timeout) {
        return executeChameleonMiniCommand(cmPort, "LOGMODE=LIVE", timeout);
    }

    /**
     * Executes the passed command by sending the command to the device.
     * The response returned by the device is handled separately elsewhere in the program.
     *
     * @param cmPort
     * @param rawCmd
     * @param timeout
     * @return SerialRespCode status code (OK)
     * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/Page_CommandLine.html
     */
    public static SerialRespCode executeChameleonMiniCommand(UsbSerialDevice cmPort, String rawCmd, int timeout) {
        if (cmPort == null || PAUSED)
            return FALSE;
        if (timeout < 0) {
            timeout *= -1;
            SystemClock.sleep(timeout);
        }
        //if (timeout != Utils.parseInt(deviceStatus.TIMEOUT))
        //    setTimeout(cmPort, timeout);
        String deviceConfigCmd = rawCmd + (REVE_BOARD ? "\r\n" : "\n\r");
        byte[] sendBuf = deviceConfigCmd.getBytes(StandardCharsets.UTF_8);
        cmPort.write(sendBuf);
        return OK;
    }

}