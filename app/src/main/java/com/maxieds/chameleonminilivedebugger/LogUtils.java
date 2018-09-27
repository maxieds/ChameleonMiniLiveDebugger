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

    public static final int DATADIR_INCOMING = 0;
    public static final int DATADIR_OUTGOING = 1;
    public static final int DATADIR_BIDIRECTIONAL = 2;

    public enum LogCode {
        /* Generic */
        LOG_INFO_GENERIC(0x10, DATADIR_BIDIRECTIONAL, "Unspecific log entry."),
        LOG_INFO_CONFIG_SET(0x11, DATADIR_BIDIRECTIONAL, "Configuration change."),
        LOG_INFO_SETTING_SET(0x12, DATADIR_BIDIRECTIONAL, "Setting change."),
        LOG_INFO_UID_SET(0x13, DATADIR_BIDIRECTIONAL, "UID change."),
        LOG_INFO_RESET_APP(0x20, DATADIR_BIDIRECTIONAL, "Application reset."),
        /* Codec */
        LOG_INFO_CODEC_RX_DATA(0x40, DATADIR_OUTGOING, "Currently active codec received data."),
        LOG_INFO_CODEC_TX_DATA(0x41, DATADIR_OUTGOING, "Currently active codec sent data."),
        LOG_INFO_CODEC_RX_DATA_W_PARITY(0x42, DATADIR_OUTGOING, "Currently active codec received data."),
        LOG_INFO_CODEC_TX_DATA_W_PARITY(0x43, DATADIR_OUTGOING, "Currently active codec sent data."),
        LOG_INFO_CODEC_SNI_READER_DATA(0x44, DATADIR_INCOMING, "Sniffing codec receive data from reader."),
        LOG_INFO_CODEC_SNI_READER_DATA_W_PARITY(0x44, DATADIR_INCOMING, "Sniffing codec receive data from reader"),
        LOG_INFO_CODEC_SNI_CARD_DATA(0x46, DATADIR_INCOMING, "Sniffing codec receive data from card."),
        LOG_INFO_CODEC_SNI_CARD_DATA_W_PARITY(0x47, DATADIR_INCOMING, "Sniffing codec receive data from card."),
        /* App */
        LOG_INFO_APP_CMD_READ(0x80, DATADIR_BIDIRECTIONAL, "Application processed read command."),
        LOG_INFO_APP_CMD_WRITE(0x81, DATADIR_BIDIRECTIONAL, "Application processed write command."),
        LOG_INFO_APP_CMD_INC(0x84, DATADIR_BIDIRECTIONAL, "Application processed increment command."),
        LOG_INFO_APP_CMD_DEC(0x85, DATADIR_BIDIRECTIONAL, "Application processed decrement command."),
        LOG_INFO_APP_CMD_TRANSFER(0x86, DATADIR_BIDIRECTIONAL, "Application processed transfer command."),
        LOG_INFO_APP_CMD_RESTORE(0x87, DATADIR_BIDIRECTIONAL, "Application processed restore command."),
        LOG_INFO_APP_CMD_AUTH(0x90, DATADIR_BIDIRECTIONAL, "Application processed authentication command."),
        LOG_INFO_APP_CMD_HALT(0x91, DATADIR_BIDIRECTIONAL, "Application processed halt command."),
        LOG_INFO_APP_CMD_UNKNOWN(0x92, DATADIR_BIDIRECTIONAL, "Application processed an unknown command."),
        LOG_INFO_APP_AUTHING(0xA0, DATADIR_BIDIRECTIONAL, "Application is in `authing` state."),
        LOG_INFO_APP_AUTHED(0xA1, DATADIR_BIDIRECTIONAL, "Application is in `auth` state."),
        /* Log errors */
        LOG_ERR_APP_AUTH_FAIL(0xC0, DATADIR_BIDIRECTIONAL, "Application authentication failed."),
        LOG_ERR_APP_CHECKSUM_FAIL(0xC1, DATADIR_BIDIRECTIONAL, "Application had a checksum fail."),
        LOG_ERR_APP_NOT_AUTHED(0xC2, DATADIR_BIDIRECTIONAL, "Application is not authenticated."),
        /* Other Chameleon-specific */
        LOG_INFO_SYSTEM_BOOT(0xFF, DATADIR_BIDIRECTIONAL, "Chameleon boots"),
        LOG_EMPTY(0x00, DATADIR_BIDIRECTIONAL, "Empty Log Entry. This is not followed by a length byte nor the two systick bytes nor any data."),
        LOG_CODE_DNE(0xff, DATADIR_BIDIRECTIONAL, "This is a dummy log code entry for matching where the input code does not exist.");

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
        private int logDataDirection;
        private String logDesc;

        /**
         * Constructor.
         * @param lcode
         * @param ldesc
         */
        private LogCode(int lcode, int ldd, String ldesc) {
            logCode = lcode;
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
        public int getDataDirection() { return logDataDirection; }
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