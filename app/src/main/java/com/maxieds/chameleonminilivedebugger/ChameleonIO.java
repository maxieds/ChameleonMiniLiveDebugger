package com.maxieds.chameleonminilivedebugger;

import com.felhr.usbserial.UsbSerialDevice;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.FALSE;
import static com.maxieds.chameleonminilivedebugger.ChameleonIO.SerialRespCode.OK;

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
        if(cmPort == null)
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

    /*public static String getSettingFromDevice(UsbSerialDevice cmPort, String query, int timeout) {
        if(cmPort == null)
            return null;
        String deviceConfigCmd = query + "\n\r";
        byte[] sendBuf = deviceConfigCmd.getBytes(StandardCharsets.UTF_8);
        cmPort.syncWrite(sendBuf, timeout);
        byte[] recvBuf = new byte[RESP_BUFFER_SIZE];
        cmPort.syncRead(recvBuf, timeout);
        return new String(recvBuf);
    }*/

}
