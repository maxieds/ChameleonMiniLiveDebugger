package com.maxieds.chameleonminilivedebugger;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public class ChameleonIO {

    private static final String TAG = ChameleonIO.class.getSimpleName();
    public static final int RESP_BUFFER_SIZE = 256;

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

    public static SerialRespCode setLoggerConfigMode(UsbSerialPort cmPort, int timeout) {
        String deviceConfigCmd = "CONFIG=ISO14443A_SNIFF";
        byte[] sendBuf = deviceConfigCmd.getBytes(StandardCharsets.UTF_8);
        cmPort.write(sendBuf, timeout);
        byte[] respBuf = new byte[RESP_BUFFER_SIZE];
        int numBytesRead = cmPort.read(respBuf, timeout);
        int respCode = Integer.parseInt(String.valueOf(respBuf));
        SerialRespCode rc = SerialRespCode.lookupByResponseCode(respCode);
        Log.w(TAG, "Read " + numBytesRead + "bytes from device ... " + rc.name());
        return rc;
    }

    public static SerialRespCode setReaderConfigMode(UsbSerialPort cmPort) {
        String deviceConfigCmd = "CONFIG=ISO14443A_READER";

    }

    public static SerialRespCode enableLiveDebugging(UsbSerialPort cmPort) {


    }





}
