package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AndroidSettingsStorage {

    private static final String TAG = AndroidSettingsStorage.class.getSimpleName();

    private static final String CHAMELEON_PROFILES_TAG = "ChameleonProfiles";
    private static final String CHAMELEON_PROFILE_NAMES_TAG = "ChameleonProfileNames";
    private static final String CHAMELEON_PROFILE_SERIALS_TAG = "ChameleonProfileSerials";
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
        return true;
    }

    public static boolean loadPreviousSettings(String profileID) {
        if(!LiveLoggerActivity.getInstance().getSharedPreferences(profileID, Context.MODE_PRIVATE).contains(PROFILE_SERIALID_PREFERENCE)) {
            return loadDefaultSettings(profileID);
        }
        try {
            ThemesConfiguration.storedAppTheme = getStringValueByKey(profileID, THEMEID_PREFERENCE);
            Settings.chameleonDeviceSerialNumber = getStringValueByKey(profileID, PROFILE_SERIALID_PREFERENCE);
            Settings.chameleonDeviceNickname = getStringValueByKey(profileID, PROFILE_NAME_PREFERENCE);
            Settings.serialBaudRate = Integer.parseInt(getStringValueByKey(profileID, SERIAL_BAUDRATE_PREFERENCE));
            Settings.allowWiredUSB = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_USB_PREFERENCE));
            Settings.allowBluetooth = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_BLUETOOTH_PREFERENCE));
            Settings.allowAndroidNFC = Boolean.valueOf(getStringValueByKey(profileID, ALLOW_ANDROID_NFC_PREFERENCE));
            Settings.sniffingMode = Integer.parseInt(getStringValueByKey(profileID, SNIFFING_MODE_PREFERENCE));
            ExternalFileIO.CURRENT_WORKING_DIRECTORY = getStringValueByKey(profileID, CWD_PREFERENCE);
            LiveLoggerActivity.setSelectedTab(Integer.parseInt(getStringValueByKey(profileID, LAST_TAB_INDEX_PREFERENCE)));
            TabFragment.UITAB_DATA[LiveLoggerActivity.getSelectedTab()].lastMenuIndex = Integer.parseInt(getStringValueByKey(profileID, LAST_TAB_SUBMENU_INDEX_PREFERENCE));
            ChameleonLogUtils.LOGGING_MIN_DATA_BYTES = Integer.parseInt(getStringValueByKey(profileID, LOGGING_MIN_DATA_BYTES));
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
            spEditor.putString(prefsKey, Settings.chameleonDeviceNickname);
        }
        else if(prefsKey.equals(PROFILE_SERIALID_PREFERENCE)) {
            spEditor.putString(prefsKey, Settings.chameleonDeviceSerialNumber);
        }
        else if(prefsKey.equals(CHAMELEON_SLOT_NAMES)) {
            spEditor.putStringSet(CHAMELEON_SLOT_NAMES, new HashSet<String>(Arrays.asList(ChameleonConfigSlot.CHAMELEON_SLOT_NAMES)));
        }
        else if(prefsKey.equals(SERIAL_BAUDRATE_PREFERENCE)) {
            spEditor.putInt(prefsKey, Settings.serialBaudRate);
        }
        else if(prefsKey.equals(ALLOW_USB_PREFERENCE)) {
            spEditor.putBoolean(prefsKey, Settings.allowWiredUSB);
        }
        else if(prefsKey.equals(ALLOW_BLUETOOTH_PREFERENCE)) {
            spEditor.putBoolean(prefsKey, Settings.allowBluetooth);
        }
        else if(prefsKey.equals(ALLOW_ANDROID_NFC_PREFERENCE)) {
            spEditor.putBoolean(prefsKey, Settings.allowAndroidNFC);
        }
        else if(prefsKey.equals(SNIFFING_MODE_PREFERENCE)) {
            spEditor.putInt(prefsKey, Settings.sniffingMode);
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
        else {
            return false;
        }
        spEditor.commit();
        return true;
    }

    public static boolean updateValueByKey(String prefsKey) {
        return updateValueByKey(Settings.chameleonDeviceSerialNumber, prefsKey);
    }

    public static String getStringValueByKey(String profileID, String prefsKey) {
        SharedPreferences sharedPrefs = LiveLoggerActivity.getInstance().getSharedPreferences(profileID, Context.MODE_PRIVATE);
        if(prefsKey.equals(THEMEID_PREFERENCE)) {
            return sharedPrefs.getString(prefsKey, ThemesConfiguration.storedAppTheme);
        }
        else if(prefsKey.equals(PROFILE_NAME_PREFERENCE)) {
            return sharedPrefs.getString(prefsKey, Settings.chameleonDeviceNickname);
        }
        else if(prefsKey.equals(PROFILE_SERIALID_PREFERENCE)) {
            return sharedPrefs.getString(prefsKey, Settings.chameleonDeviceSerialNumber);
        }
        else if(prefsKey.equals(SERIAL_BAUDRATE_PREFERENCE)) {
            return String.format(Locale.ENGLISH, "%d", sharedPrefs.getInt(prefsKey, Settings.serialBaudRate));
        }
        else if(prefsKey.equals(ALLOW_USB_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, Settings.allowWiredUSB) ? "true" : "false";
        }
        else if(prefsKey.equals(ALLOW_BLUETOOTH_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, Settings.allowBluetooth) ? "true" : "false";
        }
        else if(prefsKey.equals(ALLOW_ANDROID_NFC_PREFERENCE)) {
            return sharedPrefs.getBoolean(prefsKey, Settings.allowAndroidNFC) ? "true" : "false";
        }
        else if(prefsKey.equals(SNIFFING_MODE_PREFERENCE)) {
            return String.format(Locale.ENGLISH, "%d", sharedPrefs.getInt(prefsKey, Settings.sniffingMode));
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
