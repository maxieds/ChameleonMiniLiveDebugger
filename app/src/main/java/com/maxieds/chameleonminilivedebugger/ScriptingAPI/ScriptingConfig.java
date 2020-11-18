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

package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import com.maxieds.chameleonminilivedebugger.AndroidSettingsStorage;
import com.maxieds.chameleonminilivedebugger.ChameleonMiniLiveDebuggerActivity;
import com.maxieds.chameleonminilivedebugger.LiveLoggerActivity;

public class ScriptingConfig {

    private static final String TAG = ScriptingConfig.class.getSimpleName();

    public static ChameleonMiniLiveDebuggerActivity SCRIPTING_CONFIG_ACTIVITY_CONTEXT = null;

    public static boolean SAVE_CONSOLE_OUTPUT_FILE = true;
    public static boolean APPEND_CONSOLE_OUTPUT_FILE = false;
    public static boolean DATESTAMP_OUTPUT_FILES = true;
    public static boolean VERBOSE_ERROR_LOGGING = true;
    public static boolean VIBRATE_PHONE_ON_EXIT = false;
    public static boolean SAVE_RESTORE_CHAMELEON_STATE = true;
    public static boolean TERMINATE_ON_EXCEPTION = true;
    public static boolean IGNORE_LIVE_LOGGING = false;

    public static String LAST_SCRIPT_LOADED_PATH = "";
    public static boolean DEFAULT_LIMIT_SCRIPT_EXEC_TIME = false;
    public static int DEFAULT_LIMIT_SCRIPT_EXEC_TIME_SECONDS = 90;

    public static String ENV0_VALUE = "";
    public static String ENV1_VALUE = "";
    public static String ENVKEY0_VALUE = "";
    public static String ENVKEY1_VALUE = "";

    public static String OUTPUT_FILE_BASENAME = "";
    public static String OUTPUT_LOGFILE_BASENAME = "";
    public static String DATESTAMP_FORMAT = "%Y-%m-%d-%H%M%S";
    public static String DEFAULT_SCRIPT_LOAD_FOLDER = ScriptingFileIO.DEFAULT_CMLD_SCRIPTS_FOLDER;
    public static String DEFAULT_FILE_OUTPUT_FOLDER = ScriptingFileIO.DEFAULT_CMLD_SCRIPT_OUTPUT_FOLDER;
    public static String DEFAULT_LOGGING_OUTPUT_FOLDER = ScriptingFileIO.DEFAULT_CMLD_SCRIPT_LOGGING_FOLDER;
    public static String DEFAULT_SCRIPT_CWD = ScriptingFileIO.DEFAULT_CMLD_SCRIPTS_FOLDER;
    public static String EXTRA_KEYS_FILE = "";

    public static void initializeScriptingConfig() {
        SCRIPTING_CONFIG_ACTIVITY_CONTEXT = LiveLoggerActivity.getInstance();
        AndroidSettingsStorage.restorePreviousSettings(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE, AndroidSettingsStorage.AndroidSettingsType.SCRIPTING_CONFIG);
        ScriptingFileIO.createDefaultFilePaths();
    }

}
