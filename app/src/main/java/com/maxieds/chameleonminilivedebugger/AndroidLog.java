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

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * @see https://github.com/maxieds/ChameleonMiniLiveDebugger/projects/11
 */
public class AndroidLog {

    private static final String TAG = AndroidLog.class.getSimpleName();

    /*
     * TODO: Add a settings pref and a checkbox in the settings tab to enable/disable logging to file
     *       (and same for the level threshold)
     * TODO: Move all instances of excpt.printStackTrace() to use this new format
     * TODO: Ditto for Log(TAG, msg) calls in the CMLD source
     */

    /*
     * TODO: Update crash handler activity to optionally email the most recent logs
     * TODO: Config tab option to download these logs to the phone
     * TODO: Method to email the local log file so can post it with the crash handler issues on GitHub
     *       (or download to phone -- if the file based logging is enabled, point user to the file name
     *        with a toast message, and in the GH issue submit innstructions)
     */

    /* TODO: Make sure to open and close these files on crash handler, app exit / restart, and app start / onCreate() */

    public enum LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR;

        public static final Map<Integer, LogLevel> ENUM_INTEGER_VALUE_TO_LOGLEVEL_MAP = new HashMap<>();
        static {
            for (LogLevel logLevelSpec : values()) {
                Integer levelOrdering = Integer.valueOf(logLevelSpec.ordinal());
                ENUM_INTEGER_VALUE_TO_LOGLEVEL_MAP.put(levelOrdering, logLevelSpec);
            }
        }

        private static final int LOGLEVEL_MIN_ORDINAL = 0;
        private static final int LOGLEVEL_MAX_ORDINAL = 4;

        public LogLevel getLogLevelFromOrdinal(int ordValue) {
            if(ordValue < LOGLEVEL_MIN_ORDINAL || ordValue > LOGLEVEL_MAX_ORDINAL) {
                return  LogLevel.VERBOSE;
            }
            return ENUM_INTEGER_VALUE_TO_LOGLEVEL_MAP.get(Integer.valueOf(ordValue));
        }

    }

    public static boolean WRITE_LOGDATA_TO_FILE = true;
    public static LogLevel LOGDATA_FILE_LEVEL_THRESHOLD = LogLevel.WARNING;

    public static final String LOGDATA_FILE_LOCAL_DIRPATH = "logs";
    public static final String LOGDATA_FILE_FORMAT = "logdata-%Y-%m-%d";

    private static File logDataOutputFileHandle = null;
    private static PrintStream logDataOutputStreamHandle = null;

    private static PrintStream openLogDataOutputFile() {
        String logDataOutFilePath = Utils.getTimestamp(LOGDATA_FILE_FORMAT);
        boolean logOutputFolderExists = true;
        String localAppStoragePath = LiveLoggerActivity.getLiveLoggerInstance().getFilesDir().getAbsolutePath();
        String logDataOutputFilePath = localAppStoragePath + "/" + LOGDATA_FILE_LOCAL_DIRPATH;
        File logDataOutputFolder = new File(logDataOutputFilePath);
        if (!logDataOutputFolder.exists()) {
            logOutputFolderExists = logDataOutputFolder.mkdir();
        }
        if(logDataOutputFileHandle == null || logDataOutputStreamHandle == null) {
            if (logOutputFolderExists) {
                logDataOutputFileHandle = new File(logDataOutputFolder.getAbsolutePath(), logDataOutFilePath);
            } else {
                logDataOutputFileHandle = null;
                logDataOutputStreamHandle = null;
                return null;
            }
            try {
                logDataOutputStreamHandle = new PrintStream(logDataOutputFileHandle);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                logDataOutputFileHandle = null;
                logDataOutputStreamHandle = null;
                return null;
            }
            return logDataOutputStreamHandle;
        }
        File nextLogDataOutputFile = new File(logDataOutputFolder.getAbsolutePath(), logDataOutFilePath);
        boolean newLogFileCreated = nextLogDataOutputFile.exists();
        if(!newLogFileCreated && logDataOutputStreamHandle != null) {
            logDataOutputStreamHandle.close();
        }
        logDataOutputFileHandle = nextLogDataOutputFile;
        try {
            logDataOutputStreamHandle = new PrintStream(logDataOutputFileHandle);
            if(!newLogFileCreated) {
                logDataOutputStreamHandle.print(LOGDATA_START_FILE_DELIMITER);
                logDataOutputStreamHandle.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            logDataOutputFileHandle = null;
            logDataOutputStreamHandle = null;
            return null;
        }
        return logDataOutputStreamHandle;
    }

    private static void closeLogDataOutputFile() {
        if(!WRITE_LOGDATA_TO_FILE) {
            return;
        } else if(logDataOutputStreamHandle != null) {
            logDataOutputStreamHandle.close();
        }
        logDataOutputFileHandle = null;
        logDataOutputStreamHandle = null;
    }

    private static final int LINE_WRAP_CHARACTERS = 80;

    private static String wrapDataAt(String linePrefix, String data) {
        Locale defaultLocale = Locale.getDefault();
        int lineWrapNumChars = (int) (LINE_WRAP_CHARACTERS - linePrefix.length());
        String wrapRegexFmt = String.format(defaultLocale, ".{%d}(?=.)", lineWrapNumChars);
        String replaceLineFmt = String.format(defaultLocale, "%s$0\n", linePrefix);
        return data.replaceAll(wrapRegexFmt, replaceLineFmt);
    }

    private static String wrapDataAt(String data) {
        return wrapDataAt(LOGDATA_ITEM_DELIMITER, data);
    }

    private static final String LOGDATA_START_FILE_DELIMITER = "/******** : START OF LOGGING OUTPUT: ********/\n\n";
    private static final String LOGDATA_START_ENTRY_DELIMITER = "++++++++++";
    private static final String LOGDATA_END_ENTRY_DELIMITER = "\n\n";
    private static final String LOGDATA_ITEM_DELIMITER = "   ---- ";

    private static void logAtLevel(LogLevel level, String tag, String msg) {
        if(!WRITE_LOGDATA_TO_FILE || LOGDATA_FILE_LEVEL_THRESHOLD.ordinal() < level.ordinal()) {
            return;
        }
        PrintStream logDataOutStream = openLogDataOutputFile();
        if(logDataOutStream == null) {
            return;
        }
        String logTimeStamp = Utils.getTimestamp();
        String logLevelDesc = level.name();
        StringBuilder logDataBuilder = new StringBuilder();
        Locale defaultLocale = Locale.getDefault();
        logDataBuilder.append(String.format(defaultLocale, "%s LOG ENTRY @ %s\n", LOGDATA_START_ENTRY_DELIMITER, logTimeStamp));
        logDataBuilder.append(String.format(defaultLocale, "%s LEVEL %s / %s\n", LOGDATA_ITEM_DELIMITER, logLevelDesc, tag));
        logDataBuilder.append(String.format(defaultLocale, wrapDataAt(msg)));
        logDataBuilder.append(LOGDATA_END_ENTRY_DELIMITER);
        logDataOutputStreamHandle.print(logDataBuilder.toString().getBytes(StandardCharsets.UTF_8));
        logDataOutputStreamHandle.flush();
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        logAtLevel(LogLevel.DEBUG, tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        logAtLevel(LogLevel.ERROR, tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        logAtLevel(LogLevel.INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
        logAtLevel(LogLevel.WARNING, tag, msg);
    }

    public static void v(String tag, String msg) {
        Log.e(tag, msg);
        logAtLevel(LogLevel.VERBOSE, tag, msg);
    }

    public static void printStackTrace(@NonNull Exception e) {
        e.printStackTrace();
        if(!WRITE_LOGDATA_TO_FILE) {
            return;
        }
        PrintStream outStream = openLogDataOutputFile();
        if(outStream != null) {
            String excptTimeStamp = Utils.getTimestamp();
            StringBuilder logDataBuilder = new StringBuilder();
            String excptMsgPrefix = String.format(Locale.getDefault(), "%s EXCEPTION STACK TRACE @ %s\n\n", LOGDATA_START_ENTRY_DELIMITER, excptTimeStamp);
            outStream.print(excptMsgPrefix);
            outStream.flush();
            e.printStackTrace(outStream);
            outStream.flush();
            outStream.print(LOGDATA_START_ENTRY_DELIMITER);
            outStream.flush();
        }
    }

}