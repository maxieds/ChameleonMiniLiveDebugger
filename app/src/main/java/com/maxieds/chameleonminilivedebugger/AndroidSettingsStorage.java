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

import android.content.Context;
import android.content.SharedPreferences;

import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingConfig;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AndroidSettingsStorage {

    private static final String TAG = AndroidSettingsStorage.class.getSimpleName();

    public static final String DEFAULT_CMLDAPP_PROFILE = "CMLDAppProfile";

    public static final String THEMEID_PREFERENCE = "themeID";
    public static final String PROFILE_NAME_PREFERENCE = "profileName";
    public static final String PROFILE_SERIALID_PREFERENCE = "profileSerialID";
    public static final String CHAMELEON_SLOT_NAMES = "chameleonDeviceSlotNames";
    public static final String SERIAL_BAUDRATE_PREFERENCE = "serialBaudRate";
    public static final String ALLOW_USB_PREFERENCE = "allowWiredUSB";
    public static final String ALLOW_BLUETOOTH_PREFERENCE = "allowBluetooth";
    public static final String BLUETOOTH_DEVICE_PIN_DATA = "bluetoothDevicePinData";
    public static final String SNIFFING_MODE_PREFERENCE = "sniffingModeDirection";
    public static final String KEY_CONFIG_PREFERENCE = "keyConfigurations";
    public static final String CWD_PREFERENCE = "currentWorkingDirectory";
    public static final String LAST_TAB_INDEX_PREFERENCE = "lastTabIndex";
    public static final String LAST_TAB_SUBMENU_INDEX_PREFERENCE = "lastTabSubmenuIndex";
    public static final String LOGGING_MIN_DATA_BYTES = "loggingMinDataBytes";
    public static final String LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE = "loggingConfigClearLogsOnNewDevice";
    public static final String LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES = "loggingConfigCollapseCommonEntries";
    public static final String LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES = "loggingConfigEnableLiveStatusUpdates";
    public static final String LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS = "loggingConfigLogModeNotifyCodecRXEvents";
    public static final String LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS = "loggingConfigLogModeNotifyRdrFldDetectEvents";
    public static final String LOGGING_CONFIG_WRITE_LOGDATA_TO_FILE = "loggingConfigWriteLogDataToFile";
    public static final String LOGGING_CONFIG_LOGDATA_LEVEL_THRESHOLD = "loggingConfigLogDataLevelThreshold";
    public static final String LOGGING_CONFIG_LOGMODE_NOTIFY_STATE = "loggingConfigLogModeNotifyState";
    public static final String SCRIPTING_CONFIG_SAVE_CONSOLE_OUTPUT_FILE = "scriptingConfigSaveConsoleOutputFile";
    public static final String SCRIPTING_CONFIG_APPEND_CONSOLE_OUTPUT_FILE = "scriptingConfigAppendConsoleOutputFile";
    public static final String SCRIPTING_CONFIG_DATESTAMP_OUTPUT_FILES = "scriptingConfigDatestampOutputFiles";
    public static final String SCRIPTING_CONFIG_VERBOSE_ERROR_LOGGING = "scriptingConfigVerboseErrorLogging";
    public static final String SCRIPTING_CONFIG_VIBRATE_PHONE_ON_EXIT = "scriptingConfigVibrateOnExit";
    public static final String SCRIPTING_CONFIG_SAVE_RESTORE_CHAMELEON_STATE = "scriptingConfigRestoreChameleonState";
    public static final String SCRIPTING_CONFIG_TERMINATE_ON_EXCEPTION = "scriptingConfigTerminateOnException";
    public static final String SCRIPTING_CONFIG_IGNORE_LIVE_LOGGING = "scriptingConfigIgnoreLiveLogging";
    public static final String SCRIPTING_CONFIG_ENV0_VALUE = "scriptingConfigEnv0";
    public static final String SCRIPTING_CONFIG_ENV1_VALUE = "scriptingConfigEnv1";
    public static final String SCRIPTING_CONFIG_ENVKEY0_VALUE = "scriptingConfigEnvKey0";
    public static final String SCRIPTING_CONFIG_ENVKEY1_VALUE = "scriptingConfigEnvKey1";
    public static final String SCRIPTING_CONFIG_OUTPUT_FILE_BASENAME = "scriptingConfigOutputFileBaseName";
    public static final String SCRIPTING_CONFIG_OUTPUT_LOGFILE_BASENAME = "scriptingConfigLoggingFileBaseName";
    public static final String SCRIPTING_CONFIG_DATESTAMP_FORMAT = "scriptingConfigDatestampFormat";
    public static final String SCRIPTING_CONFIG_DEFAULT_SCRIPT_LOAD_FOLDER = "scriptingConfigDefaultScriptLoadFolder";
    public static final String SCRIPTING_CONFIG_DEFAULT_FILE_OUTPUT_FOLDER = "scriptingConfigDefaultFileOutputFolder";
    public static final String SCRIPTING_CONFIG_DEFAULT_LOGGING_OUTPUT_FOLDER = "scriptingConfigDefaultLoggingOutputFolder";
    public static final String SCRIPTING_CONFIG_DEFAULT_SCRIPT_CWD = "scriptingConfigDefaultScriptCWD";
    public static final String SCRIPTING_CONFIG_EXTRA_KEYS_FILE = "scriptingConfigExtraKeysFile";
    public static final String SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME = "scriptingConfigLimitExecTime";
    public static final String SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME_SECONDS = "scriptingConfigLimitExecTimeValue";
    public static final String SCRIPTING_CONFIG_LAST_SCRIPT_LOADED_PATH = "scriptingConfigLastLoadedScriptPath";

    public enum AndroidSettingsType {
        ALL,
        APP_STATE,
        GENERAL_SETTINGS_CONFIG,
        LOGGING_CONFIG,
        SCRIPTING_CONFIG,
    }

    public static boolean saveSettings(String profileID, AndroidSettingsType settingsMask) {
        if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.APP_STATE) {
            updateValueByKey(profileID, THEMEID_PREFERENCE);
            updateValueByKey(profileID, CWD_PREFERENCE);
            updateValueByKey(profileID, LAST_TAB_INDEX_PREFERENCE);
            updateValueByKey(profileID, LAST_TAB_SUBMENU_INDEX_PREFERENCE);
            return true;
        }
        else if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.GENERAL_SETTINGS_CONFIG) {
            updateValueByKey(profileID, PROFILE_SERIALID_PREFERENCE);
            updateValueByKey(profileID, PROFILE_NAME_PREFERENCE);
            updateValueByKey(profileID, SERIAL_BAUDRATE_PREFERENCE);
            updateValueByKey(profileID, ALLOW_USB_PREFERENCE);
            updateValueByKey(profileID, ALLOW_BLUETOOTH_PREFERENCE);
            updateValueByKey(profileID, BLUETOOTH_DEVICE_PIN_DATA);
            updateValueByKey(profileID, SNIFFING_MODE_PREFERENCE);
            return true;
        }
        else if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.LOGGING_CONFIG) {
            updateValueByKey(profileID, LOGGING_MIN_DATA_BYTES);
            updateValueByKey(profileID, LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE);
            updateValueByKey(profileID, LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES);
            updateValueByKey(profileID, LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES);
            updateValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS);
            updateValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS);
            updateValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_STATE);
            updateValueByKey(profileID, LOGGING_CONFIG_WRITE_LOGDATA_TO_FILE);
            updateValueByKey(profileID, LOGGING_CONFIG_LOGDATA_LEVEL_THRESHOLD);
            return true;
        }
        else if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.SCRIPTING_CONFIG) {
            updateValueByKey(profileID, SCRIPTING_CONFIG_SAVE_CONSOLE_OUTPUT_FILE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_APPEND_CONSOLE_OUTPUT_FILE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_DATESTAMP_OUTPUT_FILES);
            updateValueByKey(profileID, SCRIPTING_CONFIG_VERBOSE_ERROR_LOGGING);
            updateValueByKey(profileID, SCRIPTING_CONFIG_VIBRATE_PHONE_ON_EXIT);
            updateValueByKey(profileID, SCRIPTING_CONFIG_SAVE_RESTORE_CHAMELEON_STATE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_TERMINATE_ON_EXCEPTION);
            updateValueByKey(profileID, SCRIPTING_CONFIG_IGNORE_LIVE_LOGGING);
            updateValueByKey(profileID, SCRIPTING_CONFIG_ENV0_VALUE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_ENV1_VALUE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_ENVKEY0_VALUE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_ENVKEY1_VALUE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_OUTPUT_FILE_BASENAME);
            updateValueByKey(profileID, SCRIPTING_CONFIG_OUTPUT_LOGFILE_BASENAME);
            updateValueByKey(profileID, SCRIPTING_CONFIG_DATESTAMP_FORMAT);
            updateValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_SCRIPT_LOAD_FOLDER);
            updateValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_FILE_OUTPUT_FOLDER);
            updateValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_LOGGING_OUTPUT_FOLDER);
            updateValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_SCRIPT_CWD);
            updateValueByKey(profileID, SCRIPTING_CONFIG_EXTRA_KEYS_FILE);
            updateValueByKey(profileID, SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME);
            updateValueByKey(profileID, SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME_SECONDS);
            updateValueByKey(profileID, SCRIPTING_CONFIG_LAST_SCRIPT_LOADED_PATH);
            return true;
        }
        return false;
    }

    public static boolean restorePreviousSettings(String profileID, AndroidSettingsType settingsMask) {
        try {
            if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.APP_STATE) {
                ThemesConfiguration.storedAppTheme = getStringValueByKey(profileID, THEMEID_PREFERENCE);
                ExternalFileIO.CURRENT_WORKING_DIRECTORY = getStringValueByKey(profileID, CWD_PREFERENCE);
                LiveLoggerActivity.setSelectedTab(Integer.parseInt(getStringValueByKey(profileID, LAST_TAB_INDEX_PREFERENCE)));
                TabFragment.UITAB_DATA[LiveLoggerActivity.getSelectedTab()].lastMenuIndex = Integer.parseInt(getStringValueByKey(profileID, LAST_TAB_SUBMENU_INDEX_PREFERENCE));
                return true;
            }
            else if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.GENERAL_SETTINGS_CONFIG) {
                ChameleonSettings.chameleonDeviceSerialNumber = getStringValueByKey(profileID, PROFILE_SERIALID_PREFERENCE);
                ChameleonSettings.chameleonDeviceNickname = getStringValueByKey(profileID, PROFILE_NAME_PREFERENCE);
                ChameleonSettings.serialBaudRate = Integer.parseInt(getStringValueByKey(profileID, SERIAL_BAUDRATE_PREFERENCE));
                ChameleonSettings.allowWiredUSB = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_USB_PREFERENCE));
                ChameleonSettings.allowBluetooth = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_BLUETOOTH_PREFERENCE));
                ChameleonSettings.sniffingMode = Integer.parseInt(getStringValueByKey(profileID, SNIFFING_MODE_PREFERENCE));
                BluetoothGattConnector.btDevicePinDataBytes = getStringValueByKey(profileID, BLUETOOTH_DEVICE_PIN_DATA).getBytes(StandardCharsets.UTF_8);
                return true;
            }
            else if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.LOGGING_CONFIG) {
                ChameleonLogUtils.LOGGING_MIN_DATA_BYTES = Integer.parseInt(getStringValueByKey(profileID, LOGGING_MIN_DATA_BYTES));
                ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE));
                ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES));
                ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES));
                ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_CODECRX_STATUS_INDICATOR = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS));
                ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_RDRFLDDETECT_STATUS_INDICATOR = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS));
                ChameleonLogUtils.LOGMODE_NOTIFY_STATE = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_STATE));
                AndroidLog.WRITE_LOGDATA_TO_FILE = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_WRITE_LOGDATA_TO_FILE));
                AndroidLog.LOGDATA_LEVEL_THRESHOLD = AndroidLog.LogLevel.getLogLevelFromOrdinal(Integer.parseInt(getStringValueByKey(profileID, LOGGING_CONFIG_LOGDATA_LEVEL_THRESHOLD)));
                return true;
            }
            else if(settingsMask == AndroidSettingsType.ALL || settingsMask == AndroidSettingsType.SCRIPTING_CONFIG) {
                ScriptingConfig.SAVE_CONSOLE_OUTPUT_FILE = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_SAVE_CONSOLE_OUTPUT_FILE));
                ScriptingConfig.APPEND_CONSOLE_OUTPUT_FILE = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_APPEND_CONSOLE_OUTPUT_FILE));
                ScriptingConfig.DATESTAMP_OUTPUT_FILES = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_DATESTAMP_OUTPUT_FILES));
                ScriptingConfig.VERBOSE_ERROR_LOGGING = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_VERBOSE_ERROR_LOGGING));
                ScriptingConfig.VIBRATE_PHONE_ON_EXIT = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_VIBRATE_PHONE_ON_EXIT));
                ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_SAVE_RESTORE_CHAMELEON_STATE));
                ScriptingConfig.TERMINATE_ON_EXCEPTION = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_TERMINATE_ON_EXCEPTION));
                ScriptingConfig.IGNORE_LIVE_LOGGING = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_IGNORE_LIVE_LOGGING));
                ScriptingConfig.ENV0_VALUE = getStringValueByKey(profileID, SCRIPTING_CONFIG_ENV0_VALUE);
                ScriptingConfig.ENV1_VALUE = getStringValueByKey(profileID, SCRIPTING_CONFIG_ENV1_VALUE);
                ScriptingConfig.ENVKEY0_VALUE = getStringValueByKey(profileID, SCRIPTING_CONFIG_ENVKEY0_VALUE);
                ScriptingConfig.ENVKEY1_VALUE = getStringValueByKey(profileID, SCRIPTING_CONFIG_ENVKEY1_VALUE);
                ScriptingConfig.OUTPUT_FILE_BASENAME = getStringValueByKey(profileID, SCRIPTING_CONFIG_OUTPUT_FILE_BASENAME);
                ScriptingConfig.OUTPUT_LOGFILE_BASENAME = getStringValueByKey(profileID, SCRIPTING_CONFIG_OUTPUT_LOGFILE_BASENAME);
                ScriptingConfig.DATESTAMP_FORMAT = getStringValueByKey(profileID, SCRIPTING_CONFIG_DATESTAMP_FORMAT);
                ScriptingConfig.DEFAULT_SCRIPT_LOAD_FOLDER = getStringValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_SCRIPT_LOAD_FOLDER);
                ScriptingConfig.DEFAULT_FILE_OUTPUT_FOLDER = getStringValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_FILE_OUTPUT_FOLDER);
                ScriptingConfig.DEFAULT_LOGGING_OUTPUT_FOLDER = getStringValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_LOGGING_OUTPUT_FOLDER);
                ScriptingConfig.DEFAULT_SCRIPT_CWD = getStringValueByKey(profileID, SCRIPTING_CONFIG_DEFAULT_SCRIPT_CWD);
                ScriptingConfig.EXTRA_KEYS_FILE = getStringValueByKey(profileID, SCRIPTING_CONFIG_EXTRA_KEYS_FILE);
                ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME = Boolean.valueOf(getStringValueByKey(profileID, SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME));
                ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME_SECONDS = Integer.parseInt(getStringValueByKey(profileID, SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME_SECONDS));
                ScriptingConfig.LAST_SCRIPT_LOADED_PATH = getStringValueByKey(profileID, SCRIPTING_CONFIG_LAST_SCRIPT_LOADED_PATH);
                return true;
            }
        } catch(Exception ex) {
            AndroidLog.printStackTrace(ex);
            return false;
        }
        return false;
    }

    public static boolean saveAllSettings() {
        return saveSettings(DEFAULT_CMLDAPP_PROFILE, AndroidSettingsType.ALL);
    }

    public static boolean loadPreviousSettings() {
        return restorePreviousSettings(DEFAULT_CMLDAPP_PROFILE, AndroidSettingsType.ALL);
    }

    public static boolean updateValueByKey(String profileTag, String prefsKey) {
        SharedPreferences sharedPrefs = LiveLoggerActivity.getInstance().getSharedPreferences(profileTag, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPrefs.edit();
        if(prefsKey.equals(THEMEID_PREFERENCE)) {
            spEditor.putString(prefsKey, ThemesConfiguration.storedAppTheme);
        }
        else if(prefsKey.equals(PROFILE_NAME_PREFERENCE)) {
            spEditor.putString(prefsKey, ChameleonSettings.chameleonDeviceNickname);
        }
        else if(prefsKey.equals(PROFILE_SERIALID_PREFERENCE)) {
            spEditor.putString(prefsKey, ChameleonSettings.chameleonDeviceSerialNumber);
        }
        else if(prefsKey.equals(CHAMELEON_SLOT_NAMES)) {
            spEditor.putStringSet(CHAMELEON_SLOT_NAMES, new HashSet<String>(Arrays.asList(ChameleonConfigSlot.CHAMELEON_SLOT_NAMES)));
        }
        else if(prefsKey.equals(SERIAL_BAUDRATE_PREFERENCE)) {
            spEditor.putInt(prefsKey, ChameleonSettings.serialBaudRate);
        }
        else if(prefsKey.equals(ALLOW_USB_PREFERENCE)) {
            spEditor.putBoolean(prefsKey, ChameleonSettings.allowWiredUSB);
        }
        else if(prefsKey.equals(ALLOW_BLUETOOTH_PREFERENCE)) {
            spEditor.putBoolean(prefsKey, ChameleonSettings.allowBluetooth);
        }
        else if(prefsKey.equals(BLUETOOTH_DEVICE_PIN_DATA)) {
            String btDevicePinData = new String(BluetoothGattConnector.btDevicePinDataBytes, StandardCharsets.UTF_8);
            spEditor.putString(prefsKey, btDevicePinData);
        }
        else if(prefsKey.equals(SNIFFING_MODE_PREFERENCE)) {
            spEditor.putInt(prefsKey, ChameleonSettings.sniffingMode);
        }
        else if(prefsKey.equals(KEY_CONFIG_PREFERENCE)) {
            spEditor.putStringSet(prefsKey, null);
        }
        else if(prefsKey.equals(CWD_PREFERENCE)) {
            spEditor.putString(prefsKey, ExternalFileIO.CURRENT_WORKING_DIRECTORY);
        }
        else if(prefsKey.equals(LAST_TAB_INDEX_PREFERENCE)) {
            spEditor.putInt(prefsKey, LiveLoggerActivity.getSelectedTab());
        }
        else if(prefsKey.equals(LAST_TAB_SUBMENU_INDEX_PREFERENCE)) {
            int submenuIndex = TabFragment.UITAB_DATA[LiveLoggerActivity.getSelectedTab()].lastMenuIndex;
            spEditor.putInt(prefsKey, submenuIndex);
        }
        else if(prefsKey.equals(LAST_TAB_SUBMENU_INDEX_PREFERENCE)) {
            spEditor.putInt(prefsKey, ChameleonLogUtils.LOGGING_MIN_DATA_BYTES);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE)) {
            spEditor.putBoolean(prefsKey, ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES)) {
            spEditor.putBoolean(prefsKey, ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES)) {
            spEditor.putBoolean(prefsKey, ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS)) {
            spEditor.putBoolean(prefsKey, ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_CODECRX_STATUS_INDICATOR);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS)) {
            spEditor.putBoolean(prefsKey, ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_RDRFLDDETECT_STATUS_INDICATOR);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_NOTIFY_STATE)) {
            spEditor.putBoolean(prefsKey, ChameleonLogUtils.LOGMODE_NOTIFY_STATE);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_WRITE_LOGDATA_TO_FILE)) {
            spEditor.putBoolean(prefsKey, AndroidLog.WRITE_LOGDATA_TO_FILE);
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGDATA_LEVEL_THRESHOLD)) {
            spEditor.putInt(prefsKey, AndroidLog.LOGDATA_LEVEL_THRESHOLD.ordinal());
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_SAVE_CONSOLE_OUTPUT_FILE)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.SAVE_CONSOLE_OUTPUT_FILE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_APPEND_CONSOLE_OUTPUT_FILE)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.APPEND_CONSOLE_OUTPUT_FILE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DATESTAMP_OUTPUT_FILES)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.DATESTAMP_OUTPUT_FILES);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_VERBOSE_ERROR_LOGGING)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.VERBOSE_ERROR_LOGGING);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_VIBRATE_PHONE_ON_EXIT)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.VIBRATE_PHONE_ON_EXIT);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_SAVE_RESTORE_CHAMELEON_STATE)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_TERMINATE_ON_EXCEPTION)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.TERMINATE_ON_EXCEPTION);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_IGNORE_LIVE_LOGGING)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.IGNORE_LIVE_LOGGING);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENV0_VALUE)) {
            spEditor.putString(prefsKey, ScriptingConfig.ENV0_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENV1_VALUE)) {
            spEditor.putString(prefsKey, ScriptingConfig.ENV1_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENVKEY0_VALUE)) {
            spEditor.putString(prefsKey, ScriptingConfig.ENVKEY0_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENVKEY1_VALUE)) {
            spEditor.putString(prefsKey, ScriptingConfig.ENVKEY1_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_OUTPUT_FILE_BASENAME)) {
            spEditor.putString(prefsKey, ScriptingConfig.OUTPUT_FILE_BASENAME);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_OUTPUT_LOGFILE_BASENAME)) {
            spEditor.putString(prefsKey, ScriptingConfig.OUTPUT_LOGFILE_BASENAME);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DATESTAMP_FORMAT)) {
            spEditor.putString(prefsKey, ScriptingConfig.DATESTAMP_FORMAT);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_SCRIPT_LOAD_FOLDER)) {
            spEditor.putString(prefsKey, ScriptingConfig.DEFAULT_SCRIPT_LOAD_FOLDER);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_FILE_OUTPUT_FOLDER)) {
            spEditor.putString(prefsKey, ScriptingConfig.DEFAULT_FILE_OUTPUT_FOLDER);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_LOGGING_OUTPUT_FOLDER)) {
            spEditor.putString(prefsKey, ScriptingConfig.DEFAULT_LOGGING_OUTPUT_FOLDER);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_SCRIPT_CWD)) {
            spEditor.putString(prefsKey, ScriptingConfig.DEFAULT_SCRIPT_CWD);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_EXTRA_KEYS_FILE)) {
            spEditor.putString(prefsKey, ScriptingConfig.EXTRA_KEYS_FILE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME)) {
            spEditor.putBoolean(prefsKey, ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME_SECONDS)) {
            spEditor.putInt(prefsKey, ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME_SECONDS);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_LAST_SCRIPT_LOADED_PATH)) {
            spEditor.putString(prefsKey, ScriptingConfig.LAST_SCRIPT_LOADED_PATH);
        }
        else {
            return false;
        }
        spEditor.apply();
        spEditor.commit();
        return true;
    }

    public static boolean updateValueByKey(String prefsKey) {
        return updateValueByKey(DEFAULT_CMLDAPP_PROFILE, prefsKey);
    }

    public static String getStringValueByKey(String profileID, String prefsKey) {
        SharedPreferences sharedPrefs = LiveLoggerActivity.getInstance().getSharedPreferences(profileID, Context.MODE_PRIVATE);
        if(prefsKey.equals(THEMEID_PREFERENCE)) {
            return sharedPrefs.getString(prefsKey, ThemesConfiguration.storedAppTheme);
        }
        else if(prefsKey.equals(PROFILE_NAME_PREFERENCE)) {
            return sharedPrefs.getString(prefsKey, ChameleonSettings.chameleonDeviceNickname);
        }
        else if(prefsKey.equals(PROFILE_SERIALID_PREFERENCE)) {
            return sharedPrefs.getString(prefsKey, ChameleonSettings.chameleonDeviceSerialNumber);
        }
        else if(prefsKey.equals(SERIAL_BAUDRATE_PREFERENCE)) {
            return String.format(Locale.getDefault(), "%d", sharedPrefs.getInt(prefsKey, ChameleonSettings.serialBaudRate));
        }
        else if(prefsKey.equals(ALLOW_USB_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonSettings.allowWiredUSB) ? "true" : "false";
        }
        else if(prefsKey.equals(ALLOW_BLUETOOTH_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonSettings.allowBluetooth) ? "true" : "false";
        }
        else if(prefsKey.equals(BLUETOOTH_DEVICE_PIN_DATA)) {
            return sharedPrefs.getString(prefsKey, new String(BluetoothGattConnector.btDevicePinDataBytes, StandardCharsets.UTF_8));
        }
        else if(prefsKey.equals(SNIFFING_MODE_PREFERENCE)) {
            return String.format(Locale.getDefault(), "%d", sharedPrefs.getInt(prefsKey, ChameleonSettings.sniffingMode));
        }
        else if(prefsKey.equals(CWD_PREFERENCE)) {
            sharedPrefs.getString(prefsKey, ExternalFileIO.CURRENT_WORKING_DIRECTORY);
        }
        else if(prefsKey.equals(LAST_TAB_INDEX_PREFERENCE)) {
            return String.format(Locale.getDefault(), "%d", sharedPrefs.getInt(prefsKey, LiveLoggerActivity.getSelectedTab()));
        }
        else if(prefsKey.equals(LAST_TAB_SUBMENU_INDEX_PREFERENCE)) {
            int submenuIndex = TabFragment.UITAB_DATA[LiveLoggerActivity.getSelectedTab()].lastMenuIndex;
            return String.format(Locale.getDefault(), "%d", sharedPrefs.getInt(prefsKey, submenuIndex));
        }
        else if(prefsKey.equals(LOGGING_MIN_DATA_BYTES)) {
            return String.format(Locale.getDefault(), "%d", sharedPrefs.getInt(prefsKey, 0));
        }
        else if(prefsKey.equals(LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT) ? "true" : "false";
        }
        else if(prefsKey.equals(LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES) ? "true" : "false";
        }
        else if(prefsKey.equals(LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES) ? "true" : "false";
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_CODECRX_STATUS_INDICATOR) ? "true" : "false";
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_RDRFLDDETECT_STATUS_INDICATOR) ? "true" : "false";
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_NOTIFY_STATE)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonLogUtils.LOGMODE_NOTIFY_STATE) ? "true" : "false";
        }
        else if(prefsKey.equals(LOGGING_CONFIG_WRITE_LOGDATA_TO_FILE)) {
            return sharedPrefs.getBoolean(prefsKey, AndroidLog.WRITE_LOGDATA_TO_FILE) ? "true" : "false";
        }
        else if(prefsKey.equals(LOGGING_CONFIG_LOGDATA_LEVEL_THRESHOLD)) {
            return String.format(Locale.getDefault(), "%d", sharedPrefs.getInt(prefsKey, AndroidLog.LOGDATA_LEVEL_THRESHOLD.ordinal()));
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_SAVE_CONSOLE_OUTPUT_FILE)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.SAVE_CONSOLE_OUTPUT_FILE) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_APPEND_CONSOLE_OUTPUT_FILE)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.APPEND_CONSOLE_OUTPUT_FILE) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DATESTAMP_OUTPUT_FILES)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.DATESTAMP_OUTPUT_FILES) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_VERBOSE_ERROR_LOGGING)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.VERBOSE_ERROR_LOGGING) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_VIBRATE_PHONE_ON_EXIT)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.VIBRATE_PHONE_ON_EXIT) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_SAVE_RESTORE_CHAMELEON_STATE)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_TERMINATE_ON_EXCEPTION)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.TERMINATE_ON_EXCEPTION) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_IGNORE_LIVE_LOGGING)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.IGNORE_LIVE_LOGGING) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENV0_VALUE)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.ENV0_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENV1_VALUE)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.ENV1_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENVKEY0_VALUE)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.ENVKEY0_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_ENVKEY1_VALUE)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.ENVKEY1_VALUE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_OUTPUT_FILE_BASENAME)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.OUTPUT_FILE_BASENAME);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_OUTPUT_LOGFILE_BASENAME)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.OUTPUT_LOGFILE_BASENAME);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DATESTAMP_FORMAT)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.DATESTAMP_FORMAT);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_SCRIPT_LOAD_FOLDER)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.DEFAULT_SCRIPT_LOAD_FOLDER);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_FILE_OUTPUT_FOLDER)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.DEFAULT_FILE_OUTPUT_FOLDER);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_LOGGING_OUTPUT_FOLDER)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.DEFAULT_LOGGING_OUTPUT_FOLDER);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_DEFAULT_SCRIPT_CWD)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.DEFAULT_SCRIPT_CWD);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_EXTRA_KEYS_FILE)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.EXTRA_KEYS_FILE);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_LAST_SCRIPT_LOADED_PATH)) {
            return sharedPrefs.getString(prefsKey, ScriptingConfig.LAST_SCRIPT_LOADED_PATH);
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME)) {
            return sharedPrefs.getBoolean(prefsKey, ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME) ? "true" : "false";
        }
        else if(prefsKey.equals(SCRIPTING_CONFIG_LIMIT_SCRIPT_EXEC_TIME_SECONDS)) {
            return String.format(Locale.getDefault(), "%d", sharedPrefs.getInt(prefsKey, ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME_SECONDS));
        }
        return null;
    }

    public static String[] getStringArrayValueByKey(String profileID, String prefsKey) {
        SharedPreferences sharedPrefs = LiveLoggerActivity.getInstance().getSharedPreferences(profileID, Context.MODE_PRIVATE);
        if (prefsKey.equals(KEY_CONFIG_PREFERENCE)) {
            return null;
        }
        else if (prefsKey.equals(CHAMELEON_SLOT_NAMES)) {
            Set<String> chSlotNames = sharedPrefs.getStringSet(CHAMELEON_SLOT_NAMES, null);
            if(chSlotNames != null) {
                String[] slotNames = new String[ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOT_COUNT];
                chSlotNames.toArray(slotNames);
                return slotNames;
            }
        }
        return null;
    }

}
