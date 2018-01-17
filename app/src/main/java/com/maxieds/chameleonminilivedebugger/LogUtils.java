package com.maxieds.chameleonminilivedebugger;

import java.util.HashMap;
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
public class LogUtils {

    public enum LogCode {
        /* Generic */
        LOG_INFO_GENERIC(0x10, "Unspecific log entry."),
        LOG_INFO_CONFIG_SET(0x11, "Configuration change."),
        LOG_INFO_SETTING_SET(0x12, "Setting change."),
        LOG_INFO_UID_SET(0x13, "UID change."),
        LOG_INFO_RESET_APP(0x20, "Application reset."),
        /* Codec */
        LOG_INFO_CODEC_RX_DATA(0x40, "Currently active codec received data."),
        LOG_INFO_CODEC_TX_DATA(0x41, "Currently active codec sent data."),
        LOG_INFO_CODEC_RX_DATA_W_PARITY(0x42, "Currently active codec received data."),
        LOG_INFO_CODEC_TX_DATA_W_PARITY(0x43, "Currently active codec sent data."),
        /* App */
        LOG_INFO_APP_CMD_READ(0x80, "Application processed read command."),
        LOG_INFO_APP_CMD_WRITE(0x81, "Application processed write command."),
        LOG_INFO_APP_CMD_INC(0x84, "Application processed increment command."),
        LOG_INFO_APP_CMD_DEC(0x85, "Application processed decrement command."),
        LOG_INFO_APP_CMD_TRANSFER(0x86, "Application processed transfer command."),
        LOG_INFO_APP_CMD_RESTORE(0x87, "Application processed restore command."),
        LOG_INFO_APP_CMD_AUTH(0x90, "Application processed authentication command."),
        LOG_INFO_APP_CMD_HALT(0x91, "Application processed halt command."),
        LOG_INFO_APP_CMD_UNKNOWN(0x92, "Application processed an unknown command."),
        LOG_INFO_APP_AUTHING(0xA0, "Application is in `authing` state."),
        LOG_INFO_APP_AUTHED(0xA1, "Application is in `auth` state."),
        /* Log errors */
        LOG_ERR_APP_AUTH_FAIL(0xC0, "Application authentication failed."),
        LOG_ERR_APP_CHECKSUM_FAIL(0xC1, "Application had a checksum fail."),
        LOG_ERR_APP_NOT_AUTHED(0xC2, "Application is not authenticated."),
        /* Other Chameleon-specific */
        LOG_INFO_SYSTEM_BOOT(0xFF, "Chameleon boots"),
        LOG_EMPTY(0x00, "Empty Log Entry. This is not followed by a length byte nor the two systick bytes nor any data."),
        LOG_CODE_DNE(0xff, "This is a dummy log code entry for matching where the input code does not exist.");

        /**
         * Stores a mapping of the log codes to their enum values.
         */
        private static final Map<Integer, LogCode> LOG_CODE_MAP = new HashMap<>();
        static {
            for (LogCode logCode : values()) {
                int lcode = logCode.toInteger();
                Integer aLogCode = Integer.valueOf(lcode);
                LOG_CODE_MAP.put(aLogCode, logCode);
            }
        }

        /**
         * Local data stored by the class.
         */
        private int logCode;
        private String logDesc;

        /**
         * Constructor.
         * @param lcode
         * @param ldesc
         */
        private LogCode(int lcode, String ldesc) {
            logCode = lcode;
            logDesc = ldesc;
        }

        /**
         * Get methods for the private variables.
         * @return
         */
        public int toInteger() {
            return logCode;
        }
        public String getDesc() { return logDesc; }

        /**
         * Finds the enum value associated with the integer-valued log code.
         * @param lcode
         * @return LogCode enum value
         */
        public static LogCode lookupByLogCode(int lcode) {
            LogCode lc = LOG_CODE_MAP.get(lcode);
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
            String longName = lc.name();
            longName = longName.replace("LOG_INFO_", "");
            longName = longName.replace("LOG_INFO_CODEC_", "");
            longName = longName.replace("LOG_INFO_APP_", "");
            longName = longName.replace("LOG_ERR_APP_", "");
            return longName;
        }

    }

}