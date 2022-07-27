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

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;

import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingGUIMain;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.Locale;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG_MITEM_CONNECT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG_MITEM_LOGGING;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG_MITEM_SCRIPTING;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG_MITEM_SETTINGS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_LOGS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_LOGTOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_SEARCH;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SCRIPTING;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SCRIPTING_MITEM_CONSOLE_VIEW;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SCRIPTING_MITEM_LOAD_IMPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SCRIPTING_MITEM_REGISTER_VIEW;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_APDU;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_CMDS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_PERIPHERALS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_SLOTS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_TAGCONFIG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.UITAB_DATA;

public class UITabUtils {

    private static final String TAG = UITabUtils.class.getSimpleName();

    public static boolean initializeTabMainContent(int tabIndex, int menuItemIdx, View tabMainLayoutView) {
        if(tabMainLayoutView == null) {
            return false;
        }
        switch(tabIndex) {
            case TAB_LOG:
                return UITabUtils.initializeLoggingTab(menuItemIdx, tabMainLayoutView);
            case TAB_TOOLS:
                return UITabUtils.initializeToolsTab(menuItemIdx, tabMainLayoutView);
            case TAB_EXPORT:
                return UITabUtils.initializeExportTab(menuItemIdx, tabMainLayoutView);
            case TAB_SCRIPTING:
                return UITabUtils.initializeScriptingTab(menuItemIdx, tabMainLayoutView);
            case TAB_CONFIG:
                return UITabUtils.initializeConfigTab(menuItemIdx, tabMainLayoutView);
            default:
                return false;
        }
    }

    public static boolean initializeLoggingTab(int menuItemIdx, View tabMainLayoutView) {
        if(tabMainLayoutView == null) {
            return false;
        }
        boolean errorOnInit = false;
        if(menuItemIdx == TAB_LOG_MITEM_LOGS && !MainActivityLogUtils.logDataFeedConfigured) {
            if(MainActivityLogUtils.logDataFeed == null) {
                MainActivityLogUtils.logDataFeed = new LinearLayout(LiveLoggerActivity.getLiveLoggerInstance());
            }
            ScrollView logScroller = (ScrollView) tabMainLayoutView.findViewById(R.id.log_scroll_view);
            if(logScroller != null) {
                logScroller.removeAllViews();
            } else {
                errorOnInit = true;
            }
            LinearLayout logDataFeed = MainActivityLogUtils.logDataFeed;
            logDataFeed.setOrientation(LinearLayout.VERTICAL);
            if(logScroller != null) {
                logScroller.addView(logDataFeed);
                logScroller.setFillViewport(true);
            }
            MainActivityLogUtils.logScrollView = logScroller;
            MainActivityLogUtils.logDataFeed = logDataFeed;
            MainActivityLogUtils.logDataFeedConfigured = true;
        }
        else if(menuItemIdx == TAB_LOG_MITEM_LOGTOOLS) {}
        else if(menuItemIdx == TAB_LOG_MITEM_SEARCH) {
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {
                    ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark),
                    ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark)
            };
            int resCheckBoxIDs[] = new int[] {
                    R.id.entrySearchIncludeStatus,
                    R.id.entrySearchAPDU,
                    R.id.entrySearchRawLogData,
                    R.id.entrySearchLogHeaders
            };
            for(int rcbIdx = 0; rcbIdx < resCheckBoxIDs.length; rcbIdx++) {
                CheckBox cb = (CheckBox) tabMainLayoutView.findViewById(resCheckBoxIDs[rcbIdx]);
                if(cb != null) {
                    CompoundButtonCompat.setButtonTintList(cb, new ColorStateList(states, colors));
                } else {
                    errorOnInit = true;
                }
            }
        }
        return !errorOnInit;
    }

    @SuppressLint("WrongConstant")
    public static boolean initializeToolsTab(int menuItemIdx, @NonNull View tabMainLayoutView) {
        boolean errorOnInit = false;
        if(menuItemIdx == TAB_TOOLS_MITEM_SLOTS) {
            NumberPicker settingsNumberPicker = (NumberPicker) tabMainLayoutView.findViewById(R.id.settingsNumberPicker);
            if(settingsNumberPicker == null) {
                return false;
            }
            settingsNumberPicker.setDividerThickness(1);
            settingsNumberPicker.setOrientation(LinearLayout.HORIZONTAL);
            settingsNumberPicker.setValue(ChameleonIO.deviceStatus.DIP_SETTING);
            settingsNumberPicker.setFormatter("%02d");
            settingsNumberPicker.setTypeface("sans-serif", Typeface.BOLD_ITALIC);
            settingsNumberPicker.setOnLongPressUpdateInterval(25);
            settingsNumberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker numberPicker, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE) {
                        int previousSlotNumber = ChameleonIO.DeviceStatusSettings.DIP_SETTING;
                        String settingCmd = ChameleonIO.REVE_BOARD ? "setting=" : "SETTING=";
                        ChameleonIO.getSettingFromDevice(String.format(BuildConfig.DEFAULT_LOCALE, "%s%d", settingCmd, numberPicker.getValue()));
                        int activeSlotNumber = numberPicker.getValue();
                        int numSlotsUpperBound = ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOT_COUNT;
                        if(previousSlotNumber != activeSlotNumber &&
                                previousSlotNumber  >= 1 && previousSlotNumber <= numSlotsUpperBound &&
                                activeSlotNumber >= 1 && activeSlotNumber <= numSlotsUpperBound) {
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[previousSlotNumber - 1].disableLayout();
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].readParametersFromChameleonSlot();
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].updateLayoutParameters();
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].enableLayout();
                        }
                    }
                }
            });
            int activeSlotNumber = settingsNumberPicker.getValue();
            if(ChameleonSettings.getActiveSerialIOPort() != null) {
                try {
                    activeSlotNumber = Integer.parseInt(ChameleonIO.getSettingFromDevice("SETTING?"), 10);
                } catch(NumberFormatException nfe) {
                    AndroidLog.printStackTrace(nfe);
                    errorOnInit = true;
                }
            }
            LinearLayout slotConfigContainer = tabMainLayoutView.findViewById(R.id.slotConfigLayoutsContainer);
            if(slotConfigContainer != null && slotConfigContainer.getChildCount() == 0) {
                for(int si = 0; si < ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOT_COUNT; si++) {
                    ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[si].createSlotConfigUILayout(si);
                }
            }
            if (ChameleonSettings.getActiveSerialIOPort() != null) {
                final int activeSlotNumberConst = activeSlotNumber;
                Thread configureSlotGetDataThread = new Thread() {
                    @Override
                    public void run() {
                        if(activeSlotNumberConst >=1 && activeSlotNumberConst <= ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOT_COUNT) {
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumberConst - 1].getTagConfigurationsListFromDevice();
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumberConst - 1].readParametersFromChameleonSlot();
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumberConst - 1].updateLayoutParameters();
                            ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumberConst - 1].enableLayout();
                            Switch swLockTag = (Switch) slotConfigContainer.findViewById(R.id.fieldOnOffSwitch);
                            swLockTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    ChameleonIO.executeChameleonMiniCommand("READONLY=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                                }
                            });
                            Switch swField = (Switch) slotConfigContainer.findViewById(R.id.fieldOnOffSwitch);
                            swField.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    ChameleonIO.executeChameleonMiniCommand("FIELD=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                                }
                            });
                        }
                    }
                };
                LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(configureSlotGetDataThread);
            } else {
                errorOnInit = true;
            }
        }
        else if(menuItemIdx == TAB_TOOLS_MITEM_TAGCONFIG) {}
        else if(menuItemIdx == TAB_TOOLS_MITEM_CMDS) {
            Thread cfgToolsCmdsDataThread = new Thread() {
                @Override
                public void run() {
                    Switch fieldSwitch = (Switch) tabMainLayoutView.findViewById(R.id.fieldOnOffSwitch);
                    if(fieldSwitch != null) {
                        fieldSwitch.setChecked(ChameleonIO.deviceStatus.FIELD);
                        fieldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ChameleonIO.executeChameleonMiniCommand("FIELD=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                            }
                        });
                    }
                    Switch roSwitch = (Switch) tabMainLayoutView.findViewById(R.id.readonlyOnOffSwitch);
                    if(roSwitch != null) {
                        roSwitch.setChecked(ChameleonIO.deviceStatus.READONLY);
                        roSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ChameleonIO.executeChameleonMiniCommand("READONLY=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                            }
                        });
                    }
                    int threshold = 400;
                    SeekBar thresholdSeekbar = (SeekBar) tabMainLayoutView.findViewById(R.id.thresholdSeekbar);
                    if(thresholdSeekbar != null) {
                        if (ChameleonSettings.getActiveSerialIOPort() != null) {
                            try {
                                threshold = Integer.parseInt(ChameleonIO.getSettingFromDevice("THRESHOLD?"));
                            } catch (NumberFormatException nfe) {
                            }
                            thresholdSeekbar.setProgress(threshold);
                        }
                        thresholdSeekbar.incrementProgressBy(25);
                        TextView thresholdSeekbarValueText = (TextView) tabMainLayoutView.findViewById(R.id.thresholdSeekbarValueText);
                        if (thresholdSeekbarValueText != null) {
                            thresholdSeekbarValueText.setText(String.format(BuildConfig.DEFAULT_LOCALE, "% 5d mV", threshold));
                        }
                        final View seekbarView = tabMainLayoutView;
                        thresholdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            TextView labelText = (TextView) seekbarView.findViewById(R.id.thresholdSeekbarValueText);

                            @Override
                            public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
                                if (labelText != null) {
                                    labelText.setText(String.format(BuildConfig.DEFAULT_LOCALE, "% 5d mV", progress));
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(@NonNull SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
                                int nextThreshold = seekBar.getProgress();
                                LiveLoggerActivity.setSignalStrengthIndicator(nextThreshold);
                                ChameleonIO.executeChameleonMiniCommand("THRESHOLD=" + String.valueOf(nextThreshold), ChameleonIO.TIMEOUT);
                                ChameleonIO.DeviceStatusSettings.updateAllStatusAndPost(false);
                                ChameleonIO.DeviceStatusSettings.updateAllStatusAndPost(false); /* Make sure the device returned the correct data to display */
                            }
                        });
                        LiveLoggerActivity.setSignalStrengthIndicator(thresholdSeekbar.getProgress());
                    }
                    SeekBar timeoutSeekbar = (SeekBar) tabMainLayoutView.findViewById(R.id.cmdTimeoutSeekbar);
                    int timeout = 1;
                    if(timeoutSeekbar != null) {
                        if (ChameleonSettings.getActiveSerialIOPort() != null) {
                            try {
                                timeout = Integer.parseInt(ChameleonIO.getSettingFromDevice("TIMEOUT?"));
                            } catch (NumberFormatException nfe) {
                            }
                            timeoutSeekbar.setProgress(timeout);
                        }
                        timeoutSeekbar.incrementProgressBy(2);
                        TextView cmdTimeoutSeekbarValueText = (TextView) tabMainLayoutView.findViewById(R.id.cmdTimeoutSeekbarValueText);
                        if(cmdTimeoutSeekbarValueText != null) {
                            cmdTimeoutSeekbarValueText.setText(String.format(BuildConfig.DEFAULT_LOCALE, "% 4d (x128) ms", timeout));
                        }
                        final View tmtSeekbarView = tabMainLayoutView;
                        timeoutSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            TextView labelText = (TextView) tmtSeekbarView.findViewById(R.id.thresholdSeekbarValueText);

                            @Override
                            public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
                                if(labelText != null) {
                                    labelText.setText(String.format(BuildConfig.DEFAULT_LOCALE, "% 4d (x128) ms", progress));
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(@NonNull SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
                                int nextTimeout = seekBar.getProgress();
                                ChameleonIO.executeChameleonMiniCommand("TIMEOUT=" + String.valueOf(nextTimeout), ChameleonIO.TIMEOUT);
                                ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                                /* Make sure the device returned the correct data to display: */
                                ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                                ChameleonIO.TIMEOUT = nextTimeout;
                            }
                        });
                    }
                }
            };
            LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(cfgToolsCmdsDataThread);
        }
        else if(menuItemIdx == TAB_TOOLS_MITEM_PERIPHERALS) {
            connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.RButtonSpinner, R.array.RButtonOptions, ChameleonPeripherals.spinnerRButtonAdapter, "RBUTTON?");
            connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.RButtonLongSpinner, R.array.RButtonLongOptions, ChameleonPeripherals.spinnerRButtonLongAdapter, "RBUTTON_LONG?");
            connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.LButtonSpinner, R.array.LButtonOptions, ChameleonPeripherals.spinnerLButtonAdapter, "LBUTTON?");
            connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.LButtonLongSpinner, R.array.LButtonLongOptions, ChameleonPeripherals.spinnerLButtonLongAdapter, "LBUTTON_LONG?");
            connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.LEDRedSpinner, R.array.LEDRedOptions, ChameleonPeripherals.spinnerLEDRedAdapter, "LEDRED?");
            connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.LEDGreenSpinner, R.array.LEDGreenOptions, ChameleonPeripherals.spinnerLEDGreenAdapter, "LEDGREEN?");
            connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.ButtonMyRevEBoardSpinner, R.array.ButtonMyRevEBoards, ChameleonPeripherals.spinnerButtonMyAdapter, "button?");
        }
        else if(menuItemIdx == TAB_TOOLS_MITEM_APDU) {
            ApduUtils.buildFullInstructionsList();
            ApduUtils.tabView = tabMainLayoutView;
            ScrollView sv = (ScrollView) tabMainLayoutView.findViewById(R.id.apduSearchResultsScrollView);
            LinearLayout searchResultsContainer = new LinearLayout(LiveLoggerActivity.getLiveLoggerInstance());
            searchResultsContainer.setOrientation(LinearLayout.VERTICAL);
            if(sv != null) {
                sv.addView(searchResultsContainer);
            } else {
                errorOnInit = true;
            }
        }
        return !errorOnInit;
    }

    public static boolean initializeExportTab(int menuItemIdx, View tabMainLayoutView) {
        if(tabMainLayoutView == null) {
            return false;
        }
        return true;
    }

    public static boolean initializeScriptingTab(int menuItemIdx, View tabMainLayoutView) {
        if(tabMainLayoutView == null) {
            return false;
        }
        if(menuItemIdx == TAB_SCRIPTING_MITEM_LOAD_IMPORT) {
            ScriptingGUIMain.initializeScriptingTabGUI(tabMainLayoutView);
        }
        else if(menuItemIdx == TAB_SCRIPTING_MITEM_CONSOLE_VIEW) {}
        else if(menuItemIdx == TAB_SCRIPTING_MITEM_REGISTER_VIEW) {}
        else {
            return false;
        }
        return true;
    }

    public static boolean updateConfigTabConnDeviceInfo(View parentLayoutView, boolean resetConnection) {
        if (parentLayoutView == null) {
            return false;
        }
        EditText deviceNameText = parentLayoutView.findViewById(R.id.slotNicknameText);
        TextView chamTypeText = parentLayoutView.findViewById(R.id.chameleonTypeText);
        TextView hardwareIDText = parentLayoutView.findViewById(R.id.hardwareSerialIDText);
        TextView connStatusText = parentLayoutView.findViewById(R.id.connectionStatusText);
        if(chamTypeText != null && hardwareIDText != null && connStatusText != null) {
            boolean isChameleonDevConn = ChameleonSettings.getActiveSerialIOPort() != null && !resetConnection;
            if (isChameleonDevConn) {
                deviceNameText.setText(ChameleonSettings.getActiveSerialIOPort().getDeviceName());
                chamTypeText.setText(ChameleonIO.getDeviceDescription(ChameleonIO.CHAMELEON_MINI_BOARD_TYPE));
                hardwareIDText.setText(ChameleonSettings.chameleonDeviceSerialNumber);
                connStatusText.setText(ChameleonSettings.getActiveSerialIOPort().isWiredUSB() ? "USB connection" : "BT connection");
            } else {
                deviceNameText.setText(ChameleonSettings.chameleonDeviceNickname);
                chamTypeText.setText("None");
                hardwareIDText.setText("None");
                connStatusText.setText("Not connected");
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean updateConfigTabConnDeviceInfo(boolean resetConnection) {
        if (UITAB_DATA == null || UITAB_DATA.length >= TAB_CONFIG || UITAB_DATA[TAB_CONFIG] == null) {
            return false;
        } else {
            return updateConfigTabConnDeviceInfo(UITAB_DATA[TAB_CONFIG].tabInflatedView, resetConnection);
        }
    }

    public static boolean initializeConfigTab(int menuItemIdx, View tabMainLayoutView) {
        if(tabMainLayoutView == null) {
            return false;
        }
        boolean errorOnInit = false;
        if(menuItemIdx == TAB_CONFIG_MITEM_SETTINGS) {
            CheckBox cbEnableLoggingToFile = (CheckBox) tabMainLayoutView.findViewById(R.id.settingsEnableLoggingToFile);
            if(cbEnableLoggingToFile != null) {
                cbEnableLoggingToFile.setChecked(AndroidLog.WRITE_LOGDATA_TO_FILE);
                cbEnableLoggingToFile.setOnClickListener(new CheckBox.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        AndroidLog.WRITE_LOGDATA_TO_FILE = cb.isChecked();
                        AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_WRITE_LOGDATA_TO_FILE);
                    }
                });
            } else {
                errorOnInit = true;
            }
            if(!UITabUtils.connectPeripheralSpinnerAdapterLogToFileLevel(tabMainLayoutView, R.id.LogToFileLevelThresholdsSpinner,
                                                                         R.array.LoggingLevelThresholdSettings)) {
                errorOnInit = true;
            }
            CheckBox cbAllowUSB = tabMainLayoutView.findViewById(R.id.settingsAllowWiredUSB);
            if(cbAllowUSB == null) {
                return false;
            }
            cbAllowUSB.setChecked(ChameleonSettings.allowWiredUSB);
            cbAllowUSB.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean checked) {
                    try {
                        ChameleonSettings.allowWiredUSB = checked;
                        if (ChameleonSettings.getActiveSerialIOPort() == null) {
                            ChameleonSettings.stopSerialIOConnectionDiscovery();
                            ChameleonSettings.initializeSerialIOConnections();
                        }
                    } catch(Exception ex) {
                        AndroidLog.printStackTrace(ex);
                    }
                }
            });
            CheckBox cbAllowBT = tabMainLayoutView.findViewById(R.id.settingsAllowBluetooth);
            if(cbAllowBT == null) {
                return false;
            }
            cbAllowBT.setChecked(ChameleonSettings.allowBluetooth);
            cbAllowBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View cbView) {
                    CheckBox cb = (CheckBox) cbView;
                    try {
                        LiveLoggerActivity llInst = LiveLoggerActivity.getLiveLoggerInstance();
                        if(llInst == null) {
                            return;
                        }
                        boolean haveSufficientBTPerms = true;
                        if(!llInst.checkPermissionsAcquired(ActivityPermissions.CMLD_PERMISSIONS_GROUP_BLUETOOTH, true, cbView)) {
                            cbView.wait(ActivityPermissions.REQUEST_RESULT_MAX_VIEWOBJ_WAIT_TIMEOUT);
                            haveSufficientBTPerms = llInst.checkPermissionsAcquired(ActivityPermissions.CMLD_PERMISSIONS_GROUP_BLUETOOTH, false);
                        }
                        BluetoothUtils.isBluetoothEnabled(true);
                        if(cb.isChecked() && !haveSufficientBTPerms) {
                            cb.setChecked(false);
                            String btPermsRequiredResStr = llInst.getResources().getString(R.string.btPermsRequiredMsg);
                            Utils.displayToastMessageShort(btPermsRequiredResStr);
                            return;
                        }
                        boolean btScanPrevEnabled = ChameleonSettings.allowBluetooth;
                        ChameleonSettings.allowBluetooth = cb.isChecked();
                        AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.ALLOW_BLUETOOTH_PREFERENCE);
                        if (ChameleonSettings.allowBluetooth != btScanPrevEnabled && ChameleonSettings.allowBluetooth) {
                            ChameleonSettings.stopSerialIOConnectionDiscovery();
                            ChameleonSettings.initializeSerialIOConnections();
                        }
                    } catch(Exception ex) {
                        AndroidLog.printStackTrace(ex);
                        cb.setChecked(false);
                        LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String btPermsRequiredResStr = LiveLoggerActivity.getLiveLoggerInstance().getResources().getString(R.string.btPermsRequiredMsg);
                                Utils.displayToastMessageShort(btPermsRequiredResStr);
                            }
                        });
                    }
                }
            });
            CheckBox cbUseBidirSniff = tabMainLayoutView.findViewById(R.id.settingsUseBidirectionalSniffing);
            if(cbUseBidirSniff != null) {
                cbUseBidirSniff.setChecked(ChameleonSettings.sniffingMode == ChameleonSettings.SNIFFING_MODE_BIDIRECTIONAL);
                cbUseBidirSniff.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton cb, boolean checked) {
                        ChameleonSettings.sniffingMode = checked ? ChameleonSettings.SNIFFING_MODE_BIDIRECTIONAL : ChameleonSettings.SNIFFING_MODE_UNIDIRECTIONAL;
                        // TODO: configure unidirectional versus bidirectional sniffing mode ...
                    }
                });
            } else {
                errorOnInit = true;
            }
            Spinner serialBaudRateSpinner = tabMainLayoutView.findViewById(R.id.serialBaudRateSpinner);
            if(serialBaudRateSpinner != null) {
                serialBaudRateSpinner.setAdapter(new ArrayAdapter<Integer>(tabMainLayoutView.getContext(),
                        android.R.layout.simple_list_item_1, ChameleonSerialIOInterface.UART_BAUD_RATES));
                for (int si = 0; si < serialBaudRateSpinner.getAdapter().getCount(); si++) {
                    if (serialBaudRateSpinner.getAdapter().getItem(si).equals(Integer.valueOf(ChameleonSettings.serialBaudRate))) {
                        serialBaudRateSpinner.setSelection(si, false);
                        break;
                    }
                }
                serialBaudRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    Integer[] localSpinnerList = ChameleonSerialIOInterface.UART_BAUD_RATES;
                    int lastSelectedPosition = 0;

                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (ChameleonSettings.getActiveSerialIOPort() == null) {
                            return;
                        } else if (i == lastSelectedPosition) {
                            return;
                        }
                        lastSelectedPosition = i;
                        ChameleonSettings.serialBaudRate = localSpinnerList[i].intValue();
                        ChameleonSettings.getActiveSerialIOPort().setSerialBaudRate(ChameleonSettings.serialBaudRate);
                    }

                    public void onNothingSelected(AdapterView<?> adapterView) {
                        return;
                    }
                });
            } else {
                errorOnInit = true;
            }
        }
        else if(menuItemIdx == TAB_CONFIG_MITEM_CONNECT) {
            TextView btStatusText = tabMainLayoutView.findViewById(R.id.androidBluetoothStatusText);
            String btStatus = BluetoothUtils.isBluetoothEnabled() ? "Enabled" : "Disabled";
            btStatusText.setText(btStatus);
            Button btSettingsBtn = tabMainLayoutView.findViewById(R.id.androidBTSettingsButton);
            if(btSettingsBtn == null) {
                return false;
            }
            btSettingsBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    BluetoothUtils.displayAndroidBluetoothSettings();
                }
            });
            Button btTroubleshootingBtn = tabMainLayoutView.findViewById(R.id.androidBTTroubleshootingButton);
            if(btTroubleshootingBtn == null) {
                return false;
            }
            btTroubleshootingBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    BluetoothUtils.displayAndroidBluetoothTroubleshooting();
                }
            });
            boolean isChameleonDevConn = ChameleonSettings.getActiveSerialIOPort() != null;
            EditText deviceNameText = tabMainLayoutView.findViewById(R.id.slotNicknameText);
            if(deviceNameText != null) {
                deviceNameText.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable editStr) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ChameleonSettings.chameleonDeviceNickname = s.toString();
                        AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE, AndroidSettingsStorage.PROFILE_NAME_PREFERENCE);
                    }
                });
            } else {
                errorOnInit = true;
            }
            if (!updateConfigTabConnDeviceInfo(tabMainLayoutView, false)) {
                errorOnInit = true;
            }
            Button chamConnectBtn = tabMainLayoutView.findViewById(R.id.connectToDeviceButton);
            if(chamConnectBtn == null) {
                return false;
            }
            chamConnectBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    if(ChameleonSettings.getActiveSerialIOPort() != null) {
                        Utils.displayToastMessageShort("Chameleon device already connected.");
                    }
                    else {
                        ChameleonSettings.stopSerialIOConnectionDiscovery();
                        ChameleonSettings.initializeSerialIOConnections();
                        Utils.displayToastMessageShort("Attempting to connect to chameleon device.");
                    }
                }
            });
            Button chamDisconnectBtn = tabMainLayoutView.findViewById(R.id.disconnectFromDeviceButton);
            if(chamDisconnectBtn == null) {
                return false;
            }
            chamDisconnectBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    if(ChameleonSettings.getActiveSerialIOPort() == null) {
                        Utils.displayToastMessageShort("Chameleon device not yet connected.");
                    }
                    else {
                        ChameleonSettings.stopSerialIOConnectionDiscovery();
                        ChameleonSerialIOInterface serialPort = ChameleonSettings.getActiveSerialIOPort();
                        if(serialPort != null) {
                            serialPort.shutdownSerial();
                        }
                        Utils.displayToastMessageShort("Shutdown connection to active chameleon device.");
                    }
                }
            });
        }
        else if(menuItemIdx == TAB_CONFIG_MITEM_LOGGING) {
            try {
                UITabUtils.connectPeripheralSpinnerAdapterLogMode(tabMainLayoutView, R.id.LogModeSpinner,
                        R.array.LogModeOptions, ChameleonPeripherals.spinnerLogModeAdapter);
                String loggingMinDataFieldValue = String.format(BuildConfig.DEFAULT_LOCALE, "%d", ChameleonLogUtils.LOGGING_MIN_DATA_BYTES);
                EditText loggingLogDataMinBytesField = (EditText) tabMainLayoutView.findViewById(R.id.loggingLogDataMinBytesField);
                if(loggingLogDataMinBytesField != null) {
                    loggingLogDataMinBytesField.setText(loggingMinDataFieldValue);
                    loggingLogDataMinBytesField.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                            LiveLoggerActivity.getLiveLoggerInstance().actionButtonSetMinimumLogDataLength(null);
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            LiveLoggerActivity.getLiveLoggerInstance().actionButtonSetMinimumLogDataLength(null);
                        }
                    });
                } else {
                    errorOnInit = true;
                }
                CheckBox cbLoggingConfigClearOnNewConnect = (CheckBox) tabMainLayoutView.findViewById(R.id.cbLoggingConfigClearOnNewConnect);
                if(cbLoggingConfigClearOnNewConnect != null) {
                    cbLoggingConfigClearOnNewConnect.setChecked(ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT);
                    cbLoggingConfigClearOnNewConnect.setOnClickListener(new CheckBox.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox cb = (CheckBox) view;
                            ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT = cb.isChecked();
                            AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_CLEAR_LOGS_ON_NEW_DEVICE);
                        }
                    });
                } else {
                    errorOnInit = true;
                }
                CheckBox cbLoggingConfigCollapseCommonEntries = (CheckBox) tabMainLayoutView.findViewById(R.id.cbLoggingConfigCollapseCommonEntries);
                if(cbLoggingConfigCollapseCommonEntries != null) {
                    cbLoggingConfigCollapseCommonEntries.setChecked(ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES);
                    cbLoggingConfigCollapseCommonEntries.setOnClickListener(new CheckBox.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox cb = (CheckBox) view;
                            ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES = cb.isChecked();
                            AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_COLLAPSE_COMMON_ENTRIES);
                        }
                    });
                } else {
                    errorOnInit = true;
                }
                CheckBox cbLoggingEnableToolbarStatusUpdates = (CheckBox) tabMainLayoutView.findViewById(R.id.cbLoggingEnableToolbarStatusUpdates);
                if(cbLoggingEnableToolbarStatusUpdates != null) {
                    cbLoggingEnableToolbarStatusUpdates.setChecked(ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES);
                    cbLoggingEnableToolbarStatusUpdates.setOnClickListener(new CheckBox.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox cb = (CheckBox) view;
                            ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES = cb.isChecked();
                            AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_ENABLE_LIVE_STATUS_UPDATES);
                            if (ChameleonLogUtils.CONFIG_ENABLE_LIVE_TOOLBAR_STATUS_UPDATES) {
                                if (ChameleonSettings.getActiveSerialIOPort() != null) {
                                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                                    ChameleonIO.DeviceStatusSettings.startPostingStats(0);
                                }
                            } else {
                                if (ChameleonSettings.getActiveSerialIOPort() != null) {
                                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                                }
                            }
                        }
                    });
                } else {
                    errorOnInit = true;
                }
                CheckBox cbLoggingNotifyModeEnableCodecRXEvent = (CheckBox) tabMainLayoutView.findViewById(R.id.cbLoggingNotifyModeEnableCodecRXEvent);
                if(cbLoggingNotifyModeEnableCodecRXEvent != null) {
                    cbLoggingNotifyModeEnableCodecRXEvent.setChecked(ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT);
                    cbLoggingNotifyModeEnableCodecRXEvent.setOnClickListener(new CheckBox.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox cb = (CheckBox) view;
                            ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_CODECRX_STATUS_INDICATOR = cb.isChecked();
                            AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_LOGMODE_NOTIFY_CODECRX_EVENTS);
                        }
                    });
                }
                CheckBox cbLoggingNotifyModeEnableCodecReaderFieldDetected = (CheckBox) tabMainLayoutView.findViewById(R.id.cbLoggingNotifyModeEnableCodecReaderFieldDetected);
                if(cbLoggingNotifyModeEnableCodecReaderFieldDetected != null) {
                    cbLoggingNotifyModeEnableCodecReaderFieldDetected.setChecked(ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT);
                    cbLoggingNotifyModeEnableCodecReaderFieldDetected.setOnClickListener(new CheckBox.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox cb = (CheckBox) view;
                            ChameleonLogUtils.LOGMODE_NOTIFY_ENABLE_RDRFLDDETECT_STATUS_INDICATOR = cb.isChecked();
                            AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_LOGMODE_NOTIFY_RDRFLDDETECT_EVENTS);
                        }
                    });
                } else {
                    errorOnInit = true;
                }
            } catch(Exception ex) {
                AndroidLog.printStackTrace(ex);
                return false;
            }
        }
        else if(menuItemIdx == TAB_CONFIG_MITEM_SCRIPTING) {
            ScriptingGUIMain.initializeScriptingConfigGUI(tabMainLayoutView);
        }
        return !errorOnInit;
    }

    /**
     * Helper method to setup a peripheral spinner.
     * @param view
     * @param spinnerID
     * @param spinnerStringList
     * @param spinnerAdapter
     * @param queryCmd
     * @ref TabFragment.onCreateView
     * @see res/layout/tools_tab_commandsands.xml
     */
    private static void connectPeripheralSpinnerAdapter(View view, int spinnerID, int spinnerStringList,
                                                        SpinnerAdapter spinnerAdapter, String queryCmd) {
        final String[] spinnerList = view.getContext().getResources().getStringArray(spinnerStringList);
        spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spinnerList);
        Spinner spinner = (Spinner) view.findViewById(spinnerID);
        if(spinner == null) {
            return;
        }
        spinner.setAdapter(spinnerAdapter);
        if(queryCmd != null && ChameleonSettings.getActiveSerialIOPort() != null) {
            String deviceSetting = ChameleonIO.getSettingFromDevice(queryCmd);
            spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(deviceSetting));
        }
        final Spinner localSpinnerRef = spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Spinner localSpinner = localSpinnerRef;
            String[] localSpinnerList = spinnerList;
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String setCmd = localSpinner.getTag().toString() + localSpinnerList[i];
                ChameleonIO.executeChameleonMiniCommand(setCmd, ChameleonIO.TIMEOUT);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private static void connectPeripheralSpinnerAdapterLogMode(View view, int spinnerID, int spinnerStringList,
                                                               SpinnerAdapter spinnerAdapter) {
        final String[] spinnerList = view.getContext().getResources().getStringArray(spinnerStringList);
        spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spinnerList);
        Spinner spinner = (Spinner) view.findViewById(spinnerID);
        if(spinner == null) {
            return;
        }
        spinner.setAdapter(spinnerAdapter);
        if(ChameleonSettings.getActiveSerialIOPort() != null) {
            Thread cfgLogSettingsDataThread = new Thread() {
                @Override
                public void run() {
                    String deviceSetting = ChameleonIO.getSettingFromDevice("LOGMODE?");
                    if (ChameleonLogUtils.LOGMODE_NOTIFY_STATE && deviceSetting.equals(ChameleonLogUtils.LOGMODE_LIVE)) {
                        deviceSetting = ChameleonLogUtils.LOGMODE_LIVE_WITH_NOTIFY_SELECT_STATE;
                    } else if (ChameleonLogUtils.LOGMODE_NOTIFY_STATE && deviceSetting.equals(ChameleonLogUtils.LOGMODE_OFF)) {
                        deviceSetting = ChameleonLogUtils.LOGMODE_OFF_WITH_NOTIFY_SELECT_STATE;
                    }
                    spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(deviceSetting));
                }
            };
            LiveLoggerActivity.getLiveLoggerInstance().runOnUiThread(cfgLogSettingsDataThread);
        }
        final Spinner localSpinnerRef = spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Spinner localSpinner = localSpinnerRef;
            String[] localSpinnerList = spinnerList;
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLogMode = localSpinnerList[i];
                String nextLogMode = "";
                if(selectedLogMode.equals(ChameleonLogUtils.LOGMODE_OFF_WITH_NOTIFY_SELECT_STATE)) {
                    nextLogMode = ChameleonLogUtils.LOGMODE_LIVE;
                    ChameleonLogUtils.LOGMODE_NOTIFY_STATE = true;
                    ChameleonLogUtils.LOGMODE_ENABLE_PRINTING_LIVE_LOGS = false;
                }
                else if(selectedLogMode.equals(ChameleonLogUtils.LOGMODE_LIVE_WITH_NOTIFY_SELECT_STATE)) {
                    nextLogMode = ChameleonLogUtils.LOGMODE_LIVE;
                    ChameleonLogUtils.LOGMODE_NOTIFY_STATE = true;
                    ChameleonLogUtils.LOGMODE_ENABLE_PRINTING_LIVE_LOGS = true;
                }
                else {
                    nextLogMode = selectedLogMode;
                    ChameleonLogUtils.LOGMODE_NOTIFY_STATE = false;
                    ChameleonLogUtils.LOGMODE_ENABLE_PRINTING_LIVE_LOGS = true;
                }
                AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_LOGMODE_NOTIFY_STATE);
                String setCmd = "LOGMODE=" + nextLogMode;
                ChameleonIO.executeChameleonMiniCommand(setCmd, ChameleonIO.TIMEOUT);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private static boolean connectPeripheralSpinnerAdapterLogToFileLevel(View view, int spinnerID, int spinnerStringList) {
        final String[] spinnerList = view.getContext().getResources().getStringArray(spinnerStringList);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spinnerList);
        Spinner spinner = (Spinner) view.findViewById(spinnerID);
        if(spinner == null) {
            return false;
        }
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(((ArrayAdapter<String>) spinnerAdapter).getPosition(AndroidLog.LOGDATA_LEVEL_THRESHOLD.name()));
        final Spinner localSpinnerRef = spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Spinner localSpinner = localSpinnerRef;
            String[] localSpinnerList = spinnerList;
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                localSpinner.setSelection(((ArrayAdapter<String>) localSpinner.getAdapter()).getPosition(localSpinnerList[i]));
                AndroidLog.LOGDATA_LEVEL_THRESHOLD = AndroidLog.LogLevel.getLogLevelFromOrdinal(i);
                AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_CONFIG_LOGDATA_LEVEL_THRESHOLD);
                //Utils.displayToastMessageShort("New logging threshold: " + AndroidLog.LOGDATA_LEVEL_THRESHOLD.name());
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        return true;
    }

}