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

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class ChameleonConfigSlot {

    private static final String TAG = ChameleonConfigSlot.class.getSimpleName();

    public static int CHAMELEON_DEVICE_CONFIG_SLOT_COUNT = 8;
    public static ChameleonConfigSlot[] CHAMELEON_DEVICE_CONFIG_SLOTS = new ChameleonConfigSlot[8];
    public static String[] CHAMELEON_SLOT_NAMES = new String[8];
    static {
        String[] storedSlotNameSettings = AndroidSettingsStorage.getStringArrayValueByKey(ChameleonSettings.chameleonDeviceSerialNumber, AndroidSettingsStorage.CHAMELEON_SLOT_NAMES);
        if(storedSlotNameSettings != null) {
            System.arraycopy(storedSlotNameSettings, 0, CHAMELEON_SLOT_NAMES, 0, storedSlotNameSettings.length);
        }
        else {
            CHAMELEON_SLOT_NAMES = new String[] {
                    "Slot Nickname",
                    "Slot Nickname",
                    "Slot Nickname",
                    "Slot Nickname",
                    "Slot Nickname",
                    "Slot Nickname",
                    "Slot Nickname",
                    "Slot Nickname"
            };
        }
        for(int slotNum = 1; slotNum <= CHAMELEON_DEVICE_CONFIG_SLOT_COUNT; slotNum++) {
            CHAMELEON_DEVICE_CONFIG_SLOTS[slotNum - 1] = new ChameleonConfigSlot(slotNum, false);
            CHAMELEON_DEVICE_CONFIG_SLOTS[slotNum - 1].slotNickname = CHAMELEON_SLOT_NAMES[slotNum - 1];
        }
    }

    public String slotNickname;
    public int slotIndex;
    public String uidHexBytes, uidHexDisplayStr;
    public boolean uidMode;
    public boolean fieldSetting;
    public int uidSize;
    public String tagConfigType;
    public int tagMemorySize;
    public boolean isLocked;
    public boolean sakAtqaMode;
    public boolean isEnabled;
    public String[] knownTagKeys;
    public String[] storedKeys;
    public int tagSectorSize, tagBlockSize;

    private String[] tagConfigModes;
    private View slotConfigLayout;

    public ChameleonConfigSlot(int slotNumber, boolean readDeviceParams) {
        slotConfigLayout = null;
        resetLayoutParameters(slotNumber);
        if(readDeviceParams) {
            readParametersFromChameleonSlot();
        }
    }

    public void resetLayoutParameters(int slotNumber) {
        slotNickname = String.format(Locale.ENGLISH, "Slot %02d", slotNumber);
        slotIndex = slotNumber;
        uidHexBytes = "";
        uidHexDisplayStr = "<uid-unknown>";
        uidMode = false;
        fieldSetting = false;
        uidSize = 0;
        tagConfigType = "NONE";
        tagMemorySize = 0;
        isLocked = false;
        sakAtqaMode = false;
        isEnabled = false;
        knownTagKeys = new String[0];
        storedKeys = new String[0];
        tagSectorSize = tagBlockSize = 0;
        tagConfigModes = LiveLoggerActivity.getInstance().getResources().getStringArray(R.array.FullTagConfigModes);
    }

    public View createSlotConfigUILayout(int slotNumber) {
        LayoutInflater layoutInflater = LiveLoggerActivity.getInstance().getLayoutInflater();
        View tabView = TabFragment.UITAB_DATA[TabFragment.TAB_TOOLS].tabInflatedView;
        if(tabView == null) {
            slotConfigLayout = null;
            return null;
        }
        LinearLayout slotLayoutContainer = tabView.findViewById(R.id.slotConfigLayoutsContainer);
        View layoutView = layoutInflater.inflate(R.layout.slot_sublayout, slotLayoutContainer, false);
        slotConfigLayout = layoutView;
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[] {
                        Utils.getColorFromTheme(R.attr.colorAccent),
                        Utils.getColorFromTheme(R.attr.colorAccentLog)
                });
        gradientBg.setCornerRadius(45f);
        InsetDrawable gradientWithPadding = new InsetDrawable(gradientBg, 5);
        slotConfigLayout.setBackgroundColor(Utils.getColorFromTheme(R.attr.colorPrimaryDark));
        slotConfigLayout.setBackgroundDrawable(gradientWithPadding);
        updateLayoutParameters();
        slotLayoutContainer.addView(slotConfigLayout);
        disableLayout();
        return slotConfigLayout;
    }

    public boolean readParametersFromChameleonSlot() {
        if(ChameleonSettings.getActiveSerialIOPort() == null) {
            return false;
        }
        try {
            tagConfigType = ChameleonIO.getSettingFromDevice("CONFIG?");
            uidHexBytes = ChameleonIO.getSettingFromDevice("UID?");
            uidHexDisplayStr = Utils.formatUIDString(uidHexBytes, " ");
            uidSize = Integer.parseInt(ChameleonIO.getSettingFromDevice("UIDSIZE?"));
            tagMemorySize = Integer.parseInt(ChameleonIO.getSettingFromDevice("MEMSIZE?"));
            isLocked = ChameleonIO.getSettingFromDevice("READONLY?") == "1" ? true : false;
            // this will only work if the UIDMODE, SAKMODE commands are supported in the firmware:
            uidMode = ChameleonIO.getSettingFromDevice("UIDMODE?") == "1" ? true : false;
            fieldSetting = ChameleonIO.getSettingFromDevice("FIELD?") == "1" ? true : false;
            sakAtqaMode = ChameleonIO.getSettingFromDevice("SAKMODE?") == "1" ? true : false;
            //getTagConfigurationsListFromDevice();
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean readParametersFromChameleonSlot(int nextSlot, int activeSlot) {
        if(ChameleonSettings.getActiveSerialIOPort() == null) {
            return false;
        }
        else if(nextSlot < 1 || activeSlot < 1 ||
                nextSlot > CHAMELEON_DEVICE_CONFIG_SLOT_COUNT ||
                activeSlot > CHAMELEON_DEVICE_CONFIG_SLOT_COUNT) {
            return false;
        }
        try {
            ChameleonIO.getSettingFromDevice(String.format(Locale.ENGLISH, "SETTING=%d", nextSlot));
            readParametersFromChameleonSlot();
            //ChameleonIO.getSettingFromDevice(String.format(Locale.ENGLISH, "SETTING=%d", activeSlot));
        } catch(Exception exe) {
            exe.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean getTagConfigurationsListFromDevice() {
         if(ChameleonSettings.getActiveSerialIOPort() == null) {
             return false;
         }
         String configModesList = ChameleonIO.getSettingFromDevice("CONFIG=?");
         String[] configModesArray = configModesList.replace(" ", "").split(",");
         tagConfigModes = configModesArray;
         String activeConfigMode = ChameleonIO.getSettingFromDevice("CONFIG?");
         tagConfigType = activeConfigMode;
         Spinner configModeSpinner = (Spinner) slotConfigLayout.findViewById(R.id.tagConfigModeSpinner);
         configModeSpinner.setAdapter(new ArrayAdapter<String>(slotConfigLayout.getContext(),
                 android.R.layout.simple_list_item_1, tagConfigModes));
         for(int si = 0; si < configModeSpinner.getAdapter().getCount(); si++) {
             if (configModeSpinner.getAdapter().getItem(si).toString().equals(tagConfigType)) {
                 configModeSpinner.setSelection(si, false);
                 break;
             }
         }
         return true;
    }

    public boolean updateLayoutParameters() {
         EditText slotNicknameDisplay = slotConfigLayout.findViewById(R.id.slotNicknameText);
         slotNicknameDisplay.setText(slotNickname);
         TextView slotNumberLabel = slotConfigLayout.findViewById(R.id.slotOnOffNumberText);
         slotNumberLabel.setText(String.format(Locale.ENGLISH, "SLOT #%02d", slotIndex));
         Spinner configModeSpinner = (Spinner) slotConfigLayout.findViewById(R.id.tagConfigModeSpinner);
         //configModeSpinner.setAdapter(new ArrayAdapter<String>(slotConfigLayout.getContext(),
         //        android.R.layout.simple_list_item_1, tagConfigModes));
         //for(int si = 0; si < configModeSpinner.getAdapter().getCount(); si++) {
         //    if (configModeSpinner.getAdapter().getItem(si).toString().equals(tagConfigType)) {
         //        configModeSpinner.setSelection(si, false);
         //        break;
         //    }
         //}
         TextView uidBytes = (TextView) slotConfigLayout.findViewById(R.id.uidBytesText);
         uidHexDisplayStr = Utils.formatUIDString(uidHexBytes, " ");
         uidBytes.setText(uidHexDisplayStr);
         TextView memSizeText = (TextView) slotConfigLayout.findViewById(R.id.memorySizeText);
         memSizeText.setText(String.format(Locale.ENGLISH, "%dB | %dK", tagMemorySize, tagMemorySize / 1024));
         Switch lockSwitch = (Switch) slotConfigLayout.findViewById(R.id.readonlyOnOffSwitch);
         lockSwitch.setChecked(isLocked);
         Switch uidModeSwitch = (Switch) slotConfigLayout.findViewById(R.id.uidModeOnOffSwitch);
         uidModeSwitch.setChecked(uidMode);
         Switch fieldModeSwitch = (Switch) slotConfigLayout.findViewById(R.id.fieldOnOffSwitch);
         fieldModeSwitch.setChecked(fieldSetting);
         Switch sakModeSwitch = (Switch) slotConfigLayout.findViewById(R.id.sakAtqaModifyModeOnOffSwitch);
         sakModeSwitch.setChecked(sakAtqaMode);
         slotConfigLayout.setEnabled(isEnabled);
         return true;
    }

    public boolean enableLayout() {
        slotConfigLayout.setEnabled(true);
        ImageView slotOnOffImageMarker = slotConfigLayout.findViewById(R.id.slotOnOffMarker);
        Drawable slotEnabledMarker = LiveLoggerActivity.getInstance().getResources().getDrawable(R.drawable.slot_on);
        slotOnOffImageMarker.setImageDrawable(slotEnabledMarker);
        // set onClick handlers here:
        EditText slotNicknameEditor = slotConfigLayout.findViewById(R.id.slotNicknameText);
        slotNicknameEditor.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editStr) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "Changing Slot #" + slotIndex + " name to " + s);
                CHAMELEON_SLOT_NAMES[slotIndex - 1] = s.toString();
                slotNickname = s.toString();
                AndroidSettingsStorage.updateValueByKey(ChameleonSettings.chameleonDeviceSerialNumber, AndroidSettingsStorage.CHAMELEON_SLOT_NAMES);
            }
        });
        Spinner configModeSpinner = (Spinner) slotConfigLayout.findViewById(R.id.tagConfigModeSpinner);
        configModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String[] localSpinnerList = tagConfigModes;
            int lastSelectedPosition = 0;
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(ChameleonSettings.getActiveSerialIOPort() == null) {
                    return;
                }
                else if(i == lastSelectedPosition) {
                    return;
                }
                lastSelectedPosition = i;
                String nextConfigMode = localSpinnerList[i];
                String setConfigCmd = String.format(Locale.ENGLISH, "CONFIG=%s", nextConfigMode);
                ChameleonIO.getSettingFromDevice(setConfigCmd);
                ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                readParametersFromChameleonSlot();
                updateLayoutParameters();
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        Switch lockSwitch = (Switch) slotConfigLayout.findViewById(R.id.readonlyOnOffSwitch);
        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ChameleonSettings.getActiveSerialIOPort() == null) {
                    return;
                }
                else if(isChecked && !isLocked || !isChecked && isLocked) {
                    String lockCmd = String.format(Locale.ENGLISH, "READONLY=%s", isChecked ? "1" : "0");
                    ChameleonIO.getSettingFromDevice(lockCmd);
                }
            }
        });
        Switch uidModeSwitch = (Switch) slotConfigLayout.findViewById(R.id.uidModeOnOffSwitch);
        uidModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ChameleonSettings.getActiveSerialIOPort() == null) {
                    return;
                }
                else if(isChecked && !uidMode || !isChecked && uidMode) {
                    String uidModeCmd = String.format(Locale.ENGLISH, "UIDMODE=%s", isChecked ? "1" : "0");
                    ChameleonIO.getSettingFromDevice(uidModeCmd);
                }
            }
        });
        Switch fieldModeSwitch = (Switch) slotConfigLayout.findViewById(R.id.fieldOnOffSwitch);
        uidModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ChameleonSettings.getActiveSerialIOPort() == null) {
                    return;
                }
                else if(isChecked && !fieldSetting || !isChecked && fieldSetting) {
                    String uidModeCmd = String.format(Locale.ENGLISH, "FIELD=%s", isChecked ? "1" : "0");
                    ChameleonIO.getSettingFromDevice(uidModeCmd);
                }
            }
        });
        Switch sakModeSwitch = (Switch) slotConfigLayout.findViewById(R.id.sakAtqaModifyModeOnOffSwitch);
        sakModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ChameleonSettings.getActiveSerialIOPort() == null) {
                    return;
                }
                else if(isChecked && !sakAtqaMode || !isChecked && sakAtqaMode) {
                    String sakModeCmd = String.format(Locale.ENGLISH, "SAKMODE=%s", isChecked ? "1" : "0");
                    ChameleonIO.getSettingFromDevice(sakModeCmd);
                }
            }
        });
        isEnabled = true;
        return true;
    }

    public boolean disableLayout() {
        ImageView slotOnOffImageMarker = slotConfigLayout.findViewById(R.id.slotOnOffMarker);
        Drawable slotEnabledMarker = LiveLoggerActivity.getInstance().getResources().getDrawable(R.drawable.slot_off);
        slotOnOffImageMarker.setImageDrawable(slotEnabledMarker);
        slotConfigLayout.setEnabled(false);
        isEnabled = false;
        return true;
    }

}
