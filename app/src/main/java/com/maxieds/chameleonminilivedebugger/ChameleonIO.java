package com.maxieds.chameleonminilivedebugger;

import com.felhr.usbserial.UsbSerialDevice;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.FALSE;
import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.OK;
import static java.lang.Math.round;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public class ChameleonIO {

    private static final String TAG = ChameleonIO.class.getSimpleName();

    public static final int RESP_BUFFER_SIZE = 1024;
    public static final int TIMEOUT = 1000;
    public static boolean PAUSED = true;
    public static boolean WAITING_FOR_RESPONSE = false;
    public static final int CMUSB_VENDORID = 0x16d0;
    public static final int CMUSB_PRODUCTID = 0x04b2;
    public static final String DEVICE_RESPONSE_INTENT = "ChameleonIO.device.CMD_QUERY_RESPONSE";
    public static String DEVICE_RESPONSE;
    public static String DEVICE_RESPONSE_CODE;

    public enum SerialRespCode {

        OK(100),
        OK_WITH_TEXT(101),
        WAITING_FOR_MODEM(110),
        TRUE(121),
        FALSE(120),
        UNKNOWN_COMMAND(200),
        INVALID_COMMAND_USAGE(201),
        INVALID_PARAMETER(202),
        TIMEOUT(203);

        private int responseCode;
        private SerialRespCode(int rcode) { responseCode = rcode; }

        private static final Map<Integer, SerialRespCode> RESP_CODE_MAP = new HashMap<>();
        static {
            for (SerialRespCode respCode : values()) {
                int rcode = respCode.toInteger();
                Integer aRespCode = Integer.valueOf(rcode);
                RESP_CODE_MAP.put(aRespCode, respCode);
            }
        }

        public int toInteger() { return responseCode; }

        public static SerialRespCode lookupByResponseCode(int rcode) {
            return RESP_CODE_MAP.get(rcode);
        }

    }

    public static class DeviceStatusSettings {

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

        public final int STATS_UPDATE_INTERVAL = 10000;
        public Handler statsUpdateHandler = new Handler();
        public Runnable statsUpdateRunnable = new Runnable(){
            public void run() {
                updateAllStatusAndPost(true);
            }
        };

        private void updateAllStatus() {
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
        }

        public void updateAllStatusAndPost(boolean resetTimer) {
            if(LiveLoggerActivity.serialPort == null)
                return;
            updateAllStatus();
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceConfigText)).setText(CONFIG);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceConfigUID)).setText(UID);
            String subStats1 = String.format("MEM-%dK/LOG-%dK/DIP#%d", round(MEMSIZE / 1024), round(LOGSIZE / 1024), DIP_SETTING);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats1)).setText(subStats1);
            String subStats2 = String.format("%s/FLD-%d/%sCHRG", READONLY ? "RO" : "RW", FIELD ? 1 : 0, CHARGING ? "" : "NO-");
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats2)).setText(subStats2);
            String subStats3 = String.format("THRS-%d mv/TMT-%s", THRESHOLD, TIMEOUT);
            ((TextView) LiveLoggerActivity.runningActivity.findViewById(R.id.deviceStats3)).setText(subStats3);
            if(resetTimer)
                statsUpdateHandler.postDelayed(statsUpdateRunnable, STATS_UPDATE_INTERVAL);
        }
    }
    public static DeviceStatusSettings deviceStatus = new ChameleonIO.DeviceStatusSettings();

    public static SerialRespCode setLoggerConfigMode(UsbSerialDevice cmPort, int timeout) {
        return executeChameleonMiniCommand(cmPort, "CONFIG=ISO14443A_SNIFF", timeout);
    }

    public static SerialRespCode setReaderConfigMode(UsbSerialDevice cmPort, int timeout) {
        return executeChameleonMiniCommand(cmPort, "CONFIG=ISO14443A_READER", timeout);
    }

    public static SerialRespCode enableLiveDebugging(UsbSerialDevice cmPort, int timeout) {
        return executeChameleonMiniCommand(cmPort, "LOGMODE=LIVE", timeout);
    }

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
