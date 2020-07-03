package com.maxieds.chameleonminilivedebugger;

import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mschmidt34 on 12/26/2017.
 */

/**
 * <h1>Log Utils</h1>
 * Stores classifications of LIVE log types returned by the device.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 * @see LogEntryUI
 * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/_log_8h.html#a34112fbd78128ae58dc7801690dfa6e0
 */
public class ChameleonLogUtils {

    private static final String TAG = ChameleonLogUtils.class.getSimpleName();

    public static int LOGGING_MIN_DATA_BYTES = 0;
    public static boolean CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT = false;
    public static boolean CONFIG_COLLAPSE_COMMON_LOG_ENTRIES = false;

    public static final int DATADIR_INCOMING = 0;
    public static final int DATADIR_OUTGOING = 1;
    public static final int DATADIR_BIDIRECTIONAL = 2;

    public enum LogCode {
        /* Generic */
        LOG_INFO_GENERIC((byte) 0x10, DATADIR_BIDIRECTIONAL, "Unspecific log entry."),
        LOG_INFO_CONFIG_SET((byte) 0x11, DATADIR_BIDIRECTIONAL, "Configuration change."),
        LOG_INFO_SETTING_SET((byte) 0x12, DATADIR_BIDIRECTIONAL, "Setting change."),
        LOG_INFO_UID_SET((byte) 0x13, DATADIR_BIDIRECTIONAL, "UID change."),
        LOG_INFO_RESET_APP((byte) 0x20, DATADIR_BIDIRECTIONAL, "Application reset."),
        /* Codec */
        LOG_INFO_CODEC_RX_DATA((byte) 0x40, DATADIR_OUTGOING, "Currently active codec received data."),
        LOG_INFO_CODEC_TX_DATA((byte) 0x41, DATADIR_OUTGOING, "Currently active codec sent data."),
        LOG_INFO_CODEC_RX_DATA_W_PARITY((byte) 0x42, DATADIR_OUTGOING, "Currently active codec received data."),
        LOG_INFO_CODEC_TX_DATA_W_PARITY((byte) 0x43, DATADIR_OUTGOING, "Currently active codec sent data."),
        LOG_INFO_CODEC_SNI_READER_DATA((byte) 0x44, DATADIR_INCOMING, "Sniffing codec receive data from reader."),
        LOG_INFO_CODEC_SNI_READER_DATA_W_PARITY((byte) 0x45, DATADIR_INCOMING, "Sniffing codec receive data from reader"),
        LOG_INFO_CODEC_SNI_CARD_DATA((byte) 0x46, DATADIR_INCOMING, "Sniffing codec receive data from card."),
        LOG_INFO_CODEC_SNI_CARD_DATA_W_PARITY((byte) 0x47, DATADIR_INCOMING, "Sniffing codec receive data from card."),
        /* App */
        LOG_INFO_APP_CMD_READ((byte) 0x80, DATADIR_BIDIRECTIONAL, "Application processed read command."),
        LOG_INFO_APP_CMD_WRITE((byte) 0x81, DATADIR_BIDIRECTIONAL, "Application processed write command."),
        LOG_INFO_APP_CMD_INC((byte) 0x84, DATADIR_BIDIRECTIONAL, "Application processed increment command."),
        LOG_INFO_APP_CMD_DEC((byte) 0x85, DATADIR_BIDIRECTIONAL, "Application processed decrement command."),
        LOG_INFO_APP_CMD_TRANSFER((byte) 0x86, DATADIR_BIDIRECTIONAL, "Application processed transfer command."),
        LOG_INFO_APP_CMD_RESTORE((byte) 0x87, DATADIR_BIDIRECTIONAL, "Application processed restore command."),
        LOG_INFO_APP_CMD_AUTH((byte) 0x90, DATADIR_BIDIRECTIONAL, "Application processed authentication command."),
        LOG_INFO_APP_CMD_HALT((byte) 0x91, DATADIR_BIDIRECTIONAL, "Application processed halt command."),
        LOG_INFO_APP_CMD_UNKNOWN((byte) 0x92, DATADIR_BIDIRECTIONAL, "Application processed an unknown command."),
        LOG_INFO_APP_AUTHING((byte) 0xA0, DATADIR_BIDIRECTIONAL, "Application is in `authing` state."),
        LOG_INFO_APP_AUTHED((byte) 0xA1, DATADIR_BIDIRECTIONAL, "Application is in `auth` state."),
        /* Log errors */
        LOG_ERR_APP_AUTH_FAIL((byte) 0xC0, DATADIR_BIDIRECTIONAL, "Application authentication failed."),
        LOG_ERR_APP_CHECKSUM_FAIL((byte) 0xC1, DATADIR_BIDIRECTIONAL, "Application had a checksum fail."),
        LOG_ERR_APP_NOT_AUTHED((byte) 0xC2, DATADIR_BIDIRECTIONAL, "Application is not authenticated."),
        /* DESFire firmware stack specific */
        LOG_ERR_DESFIRE_GENERIC_ERROR((byte) 0xE0, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_STATUS_INFO((byte) 0xE1, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_DEBUGGING_OUTPUT((byte) 0xE2, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_INCOMING_DATA((byte) 0xE3, DATADIR_INCOMING, ""),
        LOG_INFO_DESFIRE_INCOMING_DATA_ENC((byte) 0xE4, DATADIR_INCOMING, ""),
        LOG_INFO_DESFIRE_OUTGOING_DATA((byte) 0xE5, DATADIR_OUTGOING, ""),
        LOG_INFO_DESFIRE_OUTGOING_DATA_ENC((byte) 0xE6, DATADIR_OUTGOING, ""),
        LOG_INFO_DESFIRE_NATIVE_COMMAND((byte) 0xE7, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_ISO1443_COMMAND((byte) 0xE8, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_ISO7816_COMMAND((byte) 0xE9, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_PICC_RESET((byte) 0xEA, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_PICC_RESET_FROM_MEMORY((byte) 0xEB, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_PROTECTED_DATA_SET((byte) 0xEC, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_DESFIRE_PROTECTED_DATA_SET_VERBOSE((byte) 0xED, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_APP_AUTH_KEY((byte) 0xD0, DATADIR_BIDIRECTIONAL, "The key used for authentication"),
        LOG_INFO_APP_NONCE_B((byte) 0xD1, DATADIR_BIDIRECTIONAL, "Nonce B's value (generated)"),
        LOG_INFO_APP_NONCE_AB((byte) 0xD2, DATADIR_BIDIRECTIONAL, "Nonces A and B values (received)"),
        /* ISO14443-3A,4 related logging */
        LOG_INFO_ISO14443_3A_STATE((byte) 0x53, DATADIR_BIDIRECTIONAL, ""),
        LOG_INFO_ISO14443_4_STATE((byte) 0x54, DATADIR_BIDIRECTIONAL, ""),
        /* Other Chameleon-specific */
        LOG_INFO_SYSTEM_BOOT((byte) 0xFF, DATADIR_BIDIRECTIONAL, "Chameleon boots"),
        LOG_EMPTY((byte) 0x00, DATADIR_BIDIRECTIONAL, "Empty Log Entry. This is not followed by a length byte nor the two systick bytes nor any data."),
        LOG_CODE_DNE((byte) 0xff, DATADIR_BIDIRECTIONAL, "This is a dummy log code entry for matching where the input code does not exist.");

        /**
         * Stores a mapping of the log codes to their enum values.
         */
        private static final Map<Byte, LogCode> LOG_CODE_MAP = new HashMap<>();
        static {
            for (LogCode logCode : values()) {
                byte lcode = logCode.toByte();
                Byte aLogCode = Byte.valueOf(lcode);
                LOG_CODE_MAP.put(aLogCode, logCode);
            }
        }

        /**
         * Local data stored by the class.
         */
        private int logCode;
        private byte logByteCode;
        private int logDataDirection;
        private String logDesc;

        /**
         * Constructor.
         * @param lcode
         * @param ldesc
         */
        private LogCode(byte lcode, int ldd, String ldesc) {
            logCode = Byte.toUnsignedInt(lcode);
            logByteCode = lcode;
            logDataDirection = ldd;
            logDesc = ldesc;
        }

        /**
         * Get methods for the private variables.
         * @return
         */
        public int toInteger() {
            return logCode;
        }
        public byte toByte() { return logByteCode; }
        public int getDataDirection() { return logDataDirection; }
        public String getDesc() { return logDesc; }

        /**
         * Finds the enum value associated with the integer-valued log code.
         * @param lcode
         * @return LogCode enum value
         */
        public static LogCode lookupByLogCode(int lcode) {
            LogCode lc = LOG_CODE_MAP.get((byte) lcode);
            if(lc == null)
                return LOG_CODE_DNE;
            else
                return lc;
        }

        /**
         * Gets the (shortened) technical description of the integer-valued log code.
         * @param lcode
         * @return
         */
        public static String getShortCodeName(int lcode) {
            LogCode lc = lookupByLogCode(lcode);
            if(lc == null) {
                return "UNKNOWN_LOG_CODE";
            }
            String longName = lc.name();
            longName = longName.replace("LOG_INFO_", "");
            longName = longName.replace("LOG_INFO_CODEC_", "");
            longName = longName.replace("LOG_INFO_APP_", "");
            longName = longName.replace("LOG_ERR_APP_", "");
            return longName;
        }

    }

    public static int ResponseIsLiveLoggingBytes(byte[] loggingBytes) {
         if(loggingBytes.length < 4) {
              return 0;
         }
         byte logCodeByte = loggingBytes[0];
         int logDataLength = Byte.toUnsignedInt(loggingBytes[1]);
         //if(loggingBytes.length < 4 + logDataLength) {
         //     return 0;
         //}
         if(LogCode.LOG_CODE_MAP.get(logCodeByte) != null) {
             return 4 + logDataLength;
         }
         return 0;
    }

    /**
     * Returns the data transfer direction based on the logging code.
     * Note that this bidirectional sniffing output from the Chameleon Rev. G
     * boards is fairly recent as of (9-10/2018).
     */
    public static int getDataDirection(int lcode) {
        LogCode lc = LogCode.lookupByLogCode(lcode);
        if(lc == null) {
            return DATADIR_BIDIRECTIONAL;
        }
        return lc.getDataDirection();
    }

}