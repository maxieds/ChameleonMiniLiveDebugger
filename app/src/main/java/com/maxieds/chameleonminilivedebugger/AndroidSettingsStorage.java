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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AndroidSettingsStorage {

    private static final String TAG = AndroidSettingsStorage.class.getSimpleName();

    //private static final String CHAMELEON_PROFILES_TAG = "ChameleonProfiles";
    //private static final String CHAMELEON_PROFILE_NAMES_TAG = "ChameleonProfileNames";
    //private static final String CHAMELEON_PROFILE_SERIALS_TAG = "ChameleonProfileSerials";
    public static final String DEFAULT_CMLDAPP_PROFILE = "CMLDAppProfile";

    public static final String THEMEID_PREFERENCE = "themeID";
    public static final String PROFILE_NAME_PREFERENCE = "profileName";
    public static final String PROFILE_SERIALID_PREFERENCE = "profileSerialID";
    public static final String CHAMELEON_SLOT_NAMES = "chameleonDeviceSlotNames";
    public static final String SERIAL_BAUDRATE_PREFERENCE = "serialBaudRate";
    public static final String ALLOW_USB_PREFERENCE = "allowWiredUSB";
    public static final String ALLOW_BLUETOOTH_PREFERENCE = "allowBluetooth";
    public static final String ALLOW_ANDROID_NFC_PREFERENCE = "allowAndroidNFC";
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
    public static final String LOGGING_CONFIG_LOGMODE_NOTIFY_STATE = "loggingConfigLogModeNotifyState";
    public static final String LOGGING_CONFIG_LOGMODE_ENABLE_PRINTING_LIVE_LOGS = "loggingConfigLogModeEnablePrintingLiveLogs";

    public static boolean loadDefaultSettings(String profileID) {
        updateValueByKey(profileID, THEMEID_PREFERENCE);
        updateValueByKey(profileID, PROFILE_SERIALID_PREFERENCE);
        updateValueByKey(profileID, PROFILE_NAME_PREFERENCE);
        updateValueByKey(profileID, SERIAL_BAUDRATE_PREFERENCE);
        updateValueByKey(profileID, ALLOW_USB_PREFERENCE);
        updateValueByKey(profileID, ALLOW_BLUETOOTH_PREFERENCE);
        updateValueByKey(profileID, ALLOW_ANDROID_NFC_PREFERENCE);
        updateValueByKey(profileID, SNIFFING_MODE_PREFERENCE);
        updateValueByKey(profileID, CWD_PREFERENCE);
        updateValueByKey(profileID, LAST_TAB_INDEX_PREFERENCE);
        updateValueByKey(profileID, LAST_TAB_SUBMENU_INDEX_PREFERENCE);
        updateValueByKey(profileID, LOGGING_MIN_DATA_BYTES);
        updateValueByKey(profileID, LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE);
        updateValueByKey(profileID, LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES);
        updateValueByKey(profileID, LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES);
        updateValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS);
        updateValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS);
        updateValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_STATE);
        updateValueByKey(profileID, LOGGING_CONFIG_LOGMODE_ENABLE_PRINTING_LIVE_LOGS);
        return true;
    }

    public static boolean loadPreviousSettings(String profileID) {
        try {
            ThemesConfiguration.storedAppTheme = getStringValueByKey(profileID, THEMEID_PREFERENCE);
            ChameleonSettings.chameleonDeviceSerialNumber = getStringValueByKey(profileID, PROFILE_SERIALID_PREFERENCE);
            ChameleonSettings.chameleonDeviceNickname = getStringValueByKey(profileID, PROFILE_NAME_PREFERENCE);
            ChameleonSettings.serialBaudRate = Integer.parseInt(getStringValueByKey(profileID, SERIAL_BAUDRATE_PREFERENCE));
            ChameleonSettings.allowWiredUSB = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_USB_PREFERENCE));
            ChameleonSettings.allowBluetooth = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_BLUETOOTH_PREFERENCE));
            ChameleonSettings.allowAndroidNFC = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_ANDROID_NFC_PREFERENCE));
            ChameleonSettings.sniffingMode = Integer.parseInt(getStringValueByKey(profileID, SNIFFING_MODE_PREFERENCE));
            ExternalFileIO.CURRENT_WORKING_DIRECTORY = getStringValueByKey(profileID, CWD_PREFERENCE);
            LiveLoggerActivity.setSelectedTab(Integer.parseInt(getStringValueByKey(profileID, LAST_TAB_INDEX_PREFERENCE)));
            TabFragment.UITAB_DATA[LiveLoggerActivity.getSelectedTab()].lastMenuIndex = Integer.parseInt(getStringValueByKey(profileID, LAST_TAB_SUBMENU_INDEX_PREFERENCE));
            ChameleonLogUtils.LOGGING_MIN_DATA_BYTES = Integer.parseInt(getStringValueByKey(profileID, LOGGING_MIN_DATA_BYTES));
            ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE));
            ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES));
            ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES));
            ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_CODECRX_STATUS_INDICATOR = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS));
            ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_RDRFLDDETECT_STATUS_INDICATOR = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS));
            ChameleonLogUtils.LOGMODE_NOTIFY_STATE = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_LOGMODE_NOTIFY_STATE));
            ChameleonLogUtils.LOGMODE_ENABLE_PRINTING_LIVE_LOGS = Boolean.valueOf(getStringValueByKey(profileID, LOGGING_CONFIG_LOGMODE_ENABLE_PRINTING_LIVE_LOGS));
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
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
        else if(prefsKey.equals(ALLOW_ANDROID_NFC_PREFERENCE)) {
            spEditor.putBoolean(prefsKey, ChameleonSettings.allowAndroidNFC);
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
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_ENABLE_PRINTING_LIVE_LOGS)) {
            spEditor.putBoolean(prefsKey, ChameleonLogUtils.LOGMODE_ENABLE_PRINTING_LIVE_LOGS);
        }
        else {
            return false;
        }
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
            return String.format(Locale.ENGLISH, "%d", sharedPrefs.getInt(prefsKey, ChameleonSettings.serialBaudRate));
        }
        else if(prefsKey.equals(ALLOW_USB_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonSettings.allowWiredUSB) ? "true" : "false";
        }
        else if(prefsKey.equals(ALLOW_BLUETOOTH_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonSettings.allowBluetooth) ? "true" : "false";
        }
        else if(prefsKey.equals(ALLOW_ANDROID_NFC_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonSettings.allowAndroidNFC) ? "true" : "false";
        }
        else if(prefsKey.equals(SNIFFING_MODE_PREFERENCE)) {
            return String.format(Locale.ENGLISH, "%d", sharedPrefs.getInt(prefsKey, ChameleonSettings.sniffingMode));
        }
        else if(prefsKey.equals(CWD_PREFERENCE)) {
            sharedPrefs.getString(prefsKey, ExternalFileIO.CURRENT_WORKING_DIRECTORY);
        }
        else if(prefsKey.equals(LAST_TAB_INDEX_PREFERENCE)) {
            return String.format(Locale.ENGLISH, "%d", sharedPrefs.getInt(prefsKey, LiveLoggerActivity.getSelectedTab()));
        }
        else if(prefsKey.equals(LAST_TAB_SUBMENU_INDEX_PREFERENCE)) {
            int submenuIndex = TabFragment.UITAB_DATA[LiveLoggerActivity.getSelectedTab()].lastMenuIndex;
            return String.format(Locale.ENGLISH, "%d", sharedPrefs.getInt(prefsKey, submenuIndex));
        }
        else if(prefsKey.equals(LOGGING_MIN_DATA_BYTES)) {
            return String.format(Locale.ENGLISH, "%d", sharedPrefs.getInt(prefsKey, 0));
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
        else if(prefsKey.equals(LOGGING_CONFIG_LOGMODE_ENABLE_PRINTING_LIVE_LOGS)) {
            return sharedPrefs.getBoolean(prefsKey, ChameleonLogUtils.LOGMODE_ENABLE_PRINTING_LIVE_LOGS) ? "true" : "false";
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
                String[] slotNames = new String[8];
                chSlotNames.toArray(slotNames);
                return slotNames;
            }
        }
        return null;
    }

}
