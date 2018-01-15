package com.maxieds.chameleonminilivedebugger;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.SeekBar;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;

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
 * @author  Maxie D. Schmidt
 * @since   12/31/2017
 */
public class ChameleonIO {

    private static final String TAG = ChameleonIO.class.getSimpleName();


    /**
     * Chameleon Mini USB device ID information (hex codes).
     */
    public static final int CMUSB_VENDORID = 0x16d0;
    public static final int CMUSB_PRODUCTID = 0x04b2;

    /**
     * Default timeout to use when communicating with the device.
     */
    public static final int TIMEOUT = 2000;

    /**
     * Static constants for storing state of the device.
     */
    public static boolean PAUSED = true;
    public static boolean WAITING_FOR_RESPONSE = false;
    public static boolean DOWNLOAD = false;
    public static boolean EXPECTING_BINARY_DATA = false;

    /**
     * Static storage for command return values.
     * Used to avoid overhead of passing messages for the command responses.
     * @see LiveLoggerActivity.getSettingFromDevice
     * @see LiveLoggerActivity.usbReaderCallback
     */
    public static String DEVICE_RESPONSE_CODE;
    public static String DEVICE_RESPONSE;
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
        OK(100),
        OK_WITH_TEXT(101),
        WAITING_FOR_MODEM(110),
        TRUE(121),
        FALSE(120),
        UNKNOWN_COMMAND(200),
        INVALID_COMMAND_USAGE(201),
        INVALID_PARAMETER(202),
        TIMEOUT(203);

        /**
         * Integer value associated with each enum value.
         */
        private int responseCode;

        /**
         * Constructor
         * @param rcode
         */
        private SerialRespCode(int rcode) { responseCode = rcode; }

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
         * @see ChameleonIO.isCommandResponse
         */
        public static final Map<String, SerialRespCode> RESP_CODE_TEXT_MAP = new HashMap<>();
        static {
            for (SerialRespCode respCode : values()) {
                String rcode = String.valueOf(respCode.toInteger());
                String rcodeText = respCode.name().replace("_", " ");
                RESP_CODE_TEXT_MAP.put(rcode + ":" + rcodeText, respCode);
            }
        }

        /**
         * Retrieve the integer-valued response code associated with the enum value.
         * @return int response code
         */
        public int toInteger() { return responseCode; }

        /**
         * Lookup the enum value by its associated integer response code value.
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
     * @param liveLogData
     * @return boolean whether the log data is a response to an issued command
     * @see LiveLoggerActivity.usbReaderCallback
     */
    public static boolean isCommandResponse(byte[] liveLogData) {
        String respText = new String(liveLogData).split("[\n\r]+")[0];
        if(SerialRespCode.RESP_CODE_TEXT_MAP.get(respText) != null)
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
        public String CONFIG;
        public String UID;
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
        public final int STATS_UPDATE_INTERVAL = 10000; // 10 seconds
        public Handler statsUpdateHandler = new Handler();
        public Runnable statsUpdateRunnable = new Runnable(){
            public void run() {
                updateAllStatusAndPost(true);
            }
        };

        /**
         * Queries the live device for its status settings.
         */
        private void updateAllStatus() {
            try {
                LiveLoggerActivity.serialPortLock.tryAcquire(ChameleonIO.TIMEOUT, TimeUnit.MILLISECONDS);
            } catch(InterruptedException ie) {
                return;
            }
            CONFIG = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "CONFIG?");
            UID = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "UID?");
            if(!UID.equals("NO UID."))
                UID = UID.replaceAll("..(?!$)", "$0:");
            UIDSIZE = Integer.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "UIDSIZE?"));
            MEMSIZE = Integer.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "MEMSIZE?"));
            LOGSIZE = Integer.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "LOGMEM?").replaceAll(" \\(.*\\)", ""));
            DIP_SETTING = Integer.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "SETTING?"));
            FIELD = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "FIELD?").equals("1");
            READONLY = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "READONLY?").equals("1");
            FIELD = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "FIELD?").equals("1");
            CHARGING = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "CHARGING?").equals("TRUE");
            THRESHOLD = Integer.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "THRESHOLD?"));
            TIMEOUT = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "TIMEOUT?");
            LiveLoggerActivity.serialPortLock.release();
        }

        /**
         * Updates all status settings and posts the results to the live activity window.
         * @param resetTimer whether to have this execute again in STATS_UPDATE_INTERVAL milliseconds
         * @see DeviceStatusSettings.STATS_UPDATE_INTERVAL
         * @see DeviceStatusSettings.updateAllStatus
         */
        public void updateAllStatusAndPost(boolean resetTimer) {
            if(LiveLoggerActivity.serialPort == null)
                return;
            updateAllStatus();
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceConfigText)).setText(CONFIG);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceConfigUID)).setText(UID);
            String subStats1 = String.format(Locale.ENGLISH,"MEM-%dK/LOG-%dK/DIP#%d", round(MEMSIZE / 1024), round(LOGSIZE / 1024), DIP_SETTING);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats1)).setText(subStats1);
            String subStats2 = String.format(Locale.ENGLISH,"%s/FLD-%d/%sCHRG", READONLY ? "RO" : "RW", FIELD ? 1 : 0, CHARGING ? "" : "NO-");
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats2)).setText(subStats2);
            String subStats3 = String.format(Locale.ENGLISH,"THRS-%d mv/TMT-%s", THRESHOLD, TIMEOUT);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats3)).setText(subStats3);
            SeekBar thresholdSeekbar = (SeekBar) LiveLoggerActivity.runningActivity.findViewById(R.id.thresholdSeekbar);
            if(thresholdSeekbar != null) {
                thresholdSeekbar.setProgress(THRESHOLD);
                ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.thresholdSeekbarValueText)).setText(String.format("% 5d mV", THRESHOLD));
            }
            if(resetTimer)
                statsUpdateHandler.postDelayed(statsUpdateRunnable, STATS_UPDATE_INTERVAL);
        }
    }
    public static DeviceStatusSettings deviceStatus = new ChameleonIO.DeviceStatusSettings();

    /**
     * Put the device into sniffer / logger mode.
     * @param cmPort
     * @param timeout
     * @return SerialRespCode status code (OK)
     */
    public static SerialRespCode setLoggerConfigMode(UsbSerialDevice cmPort, int timeout) {
        return executeChameleonMiniCommand(cmPort, "CONFIG=ISO14443A_SNIFF", timeout);
    }

    /**
     * Put the device into reader mode.
     * @param cmPort
     * @param timeout
     * @return SerialRespCode status code (OK)
     */
    public static SerialRespCode setReaderConfigMode(UsbSerialDevice cmPort, int timeout) {
        return executeChameleonMiniCommand(cmPort, "CONFIG=ISO14443A_READER", timeout);
    }

    /**
     * Enables LIVE logging on the device.
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
     * @param cmPort
     * @param rawCmd
     * @param timeout
     * @return SerialRespCode status code (OK)
     * @see http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/Page_CommandLine.html
     */
    public static SerialRespCode executeChameleonMiniCommand(UsbSerialDevice cmPort, String rawCmd, int timeout) {
        if(cmPort == null || PAUSED)
            return FALSE;
        if(timeout < 0) {
            timeout *= -1;
            SystemClock.sleep(timeout);
        }
        String deviceConfigCmd = rawCmd + "\n\r";
        byte[] sendBuf = deviceConfigCmd.getBytes(StandardCharsets.UTF_8);
        cmPort.write(sendBuf);
        return OK;
    }

}