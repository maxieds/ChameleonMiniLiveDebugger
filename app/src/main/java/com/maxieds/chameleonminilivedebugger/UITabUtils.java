package com.maxieds.chameleonminilivedebugger;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import androidx.core.widget.CompoundButtonCompat;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Locale;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG_MITEM_CONNECT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG_MITEM_SETTINGS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_LOGS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_LOGTOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_SEARCH;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_APDU;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_CMDS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_PERIPHERALS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_SLOTS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_TAGCONFIG;

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
            case TAB_CONFIG:
                return UITabUtils.initializeConfigTab(menuItemIdx, tabMainLayoutView);
            default:
                return false;
        }
    }

    public static boolean initializeLoggingTab(int menuItemIdx, View tabMainLayoutView) {
        if(menuItemIdx == TAB_LOG_MITEM_LOGS && !MainActivityLogUtils.logDataFeedConfigured) {
            ScrollView logScroller = (ScrollView) tabMainLayoutView.findViewById(R.id.log_scroll_view);
            logScroller.removeAllViews();
            LinearLayout logDataFeed = MainActivityLogUtils.logDataFeed;
            logDataFeed.setOrientation(LinearLayout.VERTICAL);
            logScroller.addView(logDataFeed);
            logScroller.setFillViewport(true);
            MainActivityLogUtils.logScrollView = logScroller;
            MainActivityLogUtils.logDataFeed = logDataFeed;
            MainActivityLogUtils.logDataFeedConfigured = true;
        }
        else if(menuItemIdx == TAB_LOG_MITEM_LOGS) {
            ScrollView logScroller = (ScrollView) tabMainLayoutView.findViewById(R.id.log_scroll_view);
            if(MainActivityLogUtils.logScrollView.getChildCount() > 0) {
                MainActivityLogUtils.logScrollView.removeViewAt(0);
            }
            logScroller.addView(MainActivityLogUtils.logDataFeed);
            MainActivityLogUtils.logScrollView = logScroller;
        }
        else if(menuItemIdx == TAB_LOG_MITEM_LOGTOOLS) {
            UITabUtils.connectPeripheralSpinnerAdapter(tabMainLayoutView, R.id.LogModeSpinner,
                       R.array.LogModeOptions, ChameleonPeripherals.spinnerLogModeAdapter, "LOGMODE?");
        }
        else if(menuItemIdx == TAB_LOG_MITEM_SEARCH) {
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {
                    ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark),
                    ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark)
            };
            CompoundButtonCompat.setButtonTintList((CheckBox) tabMainLayoutView.findViewById(R.id.entrySearchIncludeStatus), new ColorStateList(states, colors));
            CompoundButtonCompat.setButtonTintList((CheckBox) tabMainLayoutView.findViewById(R.id.entrySearchAPDU), new ColorStateList(states, colors));
            CompoundButtonCompat.setButtonTintList((CheckBox) tabMainLayoutView.findViewById(R.id.entrySearchRawLogData), new ColorStateList(states, colors));
            CompoundButtonCompat.setButtonTintList((CheckBox) tabMainLayoutView.findViewById(R.id.entrySearchLogHeaders), new ColorStateList(states, colors));
        }
        return true;
    }

    @SuppressLint("WrongConstant")
    public static boolean initializeToolsTab(int menuItemIdx, View tabMainLayoutView) {
        if(menuItemIdx == TAB_TOOLS_MITEM_SLOTS) {
            NumberPicker settingsNumberPicker = (NumberPicker) tabMainLayoutView.findViewById(R.id.settingsNumberPicker);
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
                        ChameleonIO.getSettingFromDevice(settingCmd + numberPicker.getValue());
                        ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                        int activeSlotNumber = numberPicker.getValue();
                        ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].readParametersFromChameleonSlot();
                        ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].updateLayoutParameters();
                        ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].enableLayout();
                        ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[previousSlotNumber].disableLayout();
                    }
                }
            });
            int activeSlotNumber = settingsNumberPicker.getValue();
            LinearLayout slotConfigContainer = tabMainLayoutView.findViewById(R.id.slotConfigLayoutsContainer);
            if(slotConfigContainer.getChildCount() == 0) {
                for(int si = 0; si < ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOT_COUNT; si++) {
                    ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[si].createSlotConfigUILayout(si);
                    ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[si].updateLayoutParameters();
                    if(si + 1 != activeSlotNumber) {
                        ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[si].disableLayout();
                    }
                    else {
                        ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[si].enableLayout();
                    }
                }
            }
            else {
                ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].getTagConfigurationsListFromDevice();
                ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].readParametersFromChameleonSlot();
                ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].updateLayoutParameters();
                ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[activeSlotNumber - 1].enableLayout();
            }
        }
        else if(menuItemIdx == TAB_TOOLS_MITEM_TAGCONFIG) {
            Spinner tagConfigModeSpinner = tabMainLayoutView.findViewById(R.id.tagConfigModeSpinner);
            if(tagConfigModeSpinner == null) {
                return false;
            }
            ChameleonSerialIOInterface serialPort = Settings.getActiveSerialIOPort();
            if(serialPort == null) {
                String[] tagConfigModesArray = LiveLoggerActivity.getInstance().getResources().getStringArray(R.array.FullTagConfigModes);
                tagConfigModeSpinner.setAdapter(new ArrayAdapter<String>(tabMainLayoutView.getContext(),
                        android.R.layout.simple_list_item_1, tagConfigModesArray));
            }
            else {
                String configModesList = ChameleonIO.getSettingFromDevice("CONFIG=?");
                String[] tagConfigModesArray = configModesList.replace(" ", "").split(",");
                String activeConfigMode = ChameleonIO.getSettingFromDevice("CONFIG?");
                int selectionIndex = 0;
                for (int si = 0; si < tagConfigModeSpinner.getAdapter().getCount(); si++) {
                    if (tagConfigModeSpinner.getAdapter().getItem(si).toString().equals(activeConfigMode)) {
                        tagConfigModeSpinner.setSelection(si, false);
                        break;
                    }
                }
                tagConfigModeSpinner.setAdapter(new ArrayAdapter<String>(tabMainLayoutView.getContext(),
                        android.R.layout.simple_list_item_1, tagConfigModesArray));

                //String[] tagConfigModesArray = LiveLoggerActivity.getInstance().getResources().getStringArray(R.array.FullTagConfigModes);
                //tagConfigModeSpinner.setAdapter(new ArrayAdapter<String>(tabMainLayoutView.getContext(),
                //        android.R.layout.simple_list_item_1, tagConfigModesArray));
            }
        }
        else if(menuItemIdx == TAB_TOOLS_MITEM_CMDS) {
            Switch fieldSwitch = (Switch) tabMainLayoutView.findViewById(R.id.fieldOnOffSwitch);
            fieldSwitch.setChecked(ChameleonIO.deviceStatus.FIELD);
            fieldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ChameleonIO.executeChameleonMiniCommand("FIELD=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                }
            });
            Switch roSwitch = (Switch) tabMainLayoutView.findViewById(R.id.readonlyOnOffSwitch);
            roSwitch.setChecked(ChameleonIO.deviceStatus.READONLY);
            roSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ChameleonIO.executeChameleonMiniCommand("READONLY=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                }
            });
            SeekBar thresholdSeekbar = (SeekBar) tabMainLayoutView.findViewById(R.id.thresholdSeekbar);
            int threshold = 400;
            if(Settings.getActiveSerialIOPort() != null) {
                try {
                    threshold = Integer.parseInt(ChameleonIO.getSettingFromDevice("THRESHOLD?"));
                }
                catch(NumberFormatException nfe) {}
                thresholdSeekbar.setProgress(threshold);
            }
            thresholdSeekbar.incrementProgressBy(25);
            ((TextView) tabMainLayoutView.findViewById(R.id.thresholdSeekbarValueText)).setText(String.format(Locale.ENGLISH, "% 5d mV", threshold));
            final View seekbarView = tabMainLayoutView;
            thresholdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                TextView labelText = (TextView) seekbarView.findViewById(R.id.thresholdSeekbarValueText);
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    labelText.setText(String.format(Locale.ENGLISH, "% 5d mV", progress));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int nextThreshold = seekBar.getProgress();
                    LiveLoggerActivity.setSignalStrengthIndicator(nextThreshold);
                    ChameleonIO.executeChameleonMiniCommand("THRESHOLD=" + String.valueOf(nextThreshold), ChameleonIO.TIMEOUT);
                    ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                }
            });
            LiveLoggerActivity.setSignalStrengthIndicator(thresholdSeekbar.getProgress());
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
            LinearLayout searchResultsContainer = new LinearLayout(LiveLoggerActivity.getInstance());
            searchResultsContainer.setOrientation(LinearLayout.VERTICAL);
            sv.addView(searchResultsContainer);
        }
        return true;
    }

    public static boolean initializeExportTab(int menuItemIdx, View tabMainLayoutView) {
        return true;
    }

    public static boolean initializeConfigTab(int menuItemIdx, View tabMainLayoutView) {
        if(menuItemIdx == TAB_CONFIG_MITEM_SETTINGS) {
             // allow USB checkbox setup:
             CheckBox cbAllowUSB = tabMainLayoutView.findViewById(R.id.settingsAllowWiredUSB);
             cbAllowUSB.setChecked(Settings.allowWiredUSB);
             cbAllowUSB.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton cb, boolean checked) {
                     Settings.allowWiredUSB = checked;
                     if(Settings.getActiveSerialIOPort() == null) {
                         Settings.stopSerialIOConnectionDiscovery();
                         Settings.initializeSerialIOConnections();
                     }
                 }
             });
             // allow bluetooth checkbox setup:
             CheckBox cbAllowBT = tabMainLayoutView.findViewById(R.id.settingsAllowBluetooth);
             cbAllowBT.setChecked(Settings.allowBluetooth);
             cbAllowBT.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton cb, boolean checked) {
                     Settings.allowBluetooth = checked;
                     if(Settings.getActiveSerialIOPort() == null) {
                         Settings.stopSerialIOConnectionDiscovery();
                         Settings.initializeSerialIOConnections();
                     }
                 }
             });
             // allow NFC checkbox setup:
             CheckBox cbAllowNFC = tabMainLayoutView.findViewById(R.id.settingsAllowAndroidNFC);
             cbAllowNFC.setChecked(Settings.allowAndroidNFC);
             cbAllowNFC.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton cb, boolean checked) {
                     Settings.allowAndroidNFC = checked;
                     // TODO: init NFC in library for MFC cloning ...
                 }
             });
             // bi/unidirectional sniffing checkbox setup:
             CheckBox cbUseBidirSniff = tabMainLayoutView.findViewById(R.id.settingsUseBidirectionalSniffing);
             cbUseBidirSniff.setChecked(Settings.sniffingMode == Settings.SNIFFING_MODE_BIDIRECTIONAL);
             cbUseBidirSniff.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton cb, boolean checked) {
                     Settings.sniffingMode = checked ? Settings.SNIFFING_MODE_BIDIRECTIONAL : Settings.SNIFFING_MODE_UNIDIRECTIONAL;
                     // TODO: configure unidirectional versus bidirectional sniffing mode ...
                 }
             });
             // serial baud rate spinner setup:
             Spinner serialBaudRateSpinner = tabMainLayoutView.findViewById(R.id.serialBaudRateSpinner);
             serialBaudRateSpinner.setAdapter(new ArrayAdapter<Integer>(tabMainLayoutView.getContext(),
                     android.R.layout.simple_list_item_1, ChameleonSerialIOInterface.UART_BAUD_RATES));
             for (int si = 0; si <  serialBaudRateSpinner.getAdapter().getCount(); si++) {
                 if (serialBaudRateSpinner.getAdapter().getItem(si).equals(Integer.valueOf(Settings.serialBaudRate))) {
                     serialBaudRateSpinner.setSelection(si, false);
                     break;
                 }
             }
             serialBaudRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 Integer[] localSpinnerList = ChameleonSerialIOInterface.UART_BAUD_RATES;
                 int lastSelectedPosition = 0;
                 public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                     if(Settings.getActiveSerialIOPort() == null) {
                         return;
                     }
                     else if(i == lastSelectedPosition) {
                         return;
                     }
                     lastSelectedPosition = i;
                     Settings.serialBaudRate = localSpinnerList[i].intValue();
                     Settings.getActiveSerialIOPort().setSerialBaudRate(Settings.serialBaudRate);
                 }
                 public void onNothingSelected(AdapterView<?> adapterView) {
                     return;
                 }
             });
        }
        else if(menuItemIdx == TAB_CONFIG_MITEM_CONNECT) {
            // Android bluetooth settings config:
            TextView btStatusText = tabMainLayoutView.findViewById(R.id.androidBluetoothStatusText);
            String btStatus = ((BluetoothSerialInterface) Settings.serialIOPorts[Settings.BTIO_IFACE_INDEX]).isBluetoothEnabled() ? "Enabled" : "Disabled";
            btStatusText.setText(btStatus);
            Button btSettingsBtn = tabMainLayoutView.findViewById(R.id.androidBTSettingsButton);
            btSettingsBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    BluetoothSerialInterface.displayAndroidBluetoothSettings();
                }
            });
            // Android NFC settings config:
            TextView nfcStatusText = tabMainLayoutView.findViewById(R.id.androidNFCStatusText);
            String nfcStatus = AndroidNFCExchange.isNFCEnabled() ? "Enabled" : "Disabled";
            nfcStatusText.setText(nfcStatus);
            Button nfcSettingsBtn = tabMainLayoutView.findViewById(R.id.androidNFCSettingsButton);
            nfcSettingsBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    AndroidNFCExchange.displayAndroidNFCSettings();
                }
            });
            // Chameleon device connection information:
            boolean isChameleonDevConn = Settings.getActiveSerialIOPort() != null;
            EditText deviceNameText = tabMainLayoutView.findViewById(R.id.slotNicknameText);
            deviceNameText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editStr) {}
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Settings.chameleonDeviceNickname = s.toString();
                    AndroidSettingsStorage.updateValueByKey(Settings.chameleonDeviceSerialNumber, AndroidSettingsStorage.PROFILE_NAME_PREFERENCE);
                }
            });
            TextView chamTypeText = tabMainLayoutView.findViewById(R.id.chameleonTypeText);
            TextView hardwareIDText = tabMainLayoutView.findViewById(R.id.hardwareSerialIDText);
            TextView connStatusText = tabMainLayoutView.findViewById(R.id.connectionStatusText);
            if(isChameleonDevConn) {
                deviceNameText.setText(AndroidSettingsStorage.getStringValueByKey(Settings.chameleonDeviceSerialNumber, AndroidSettingsStorage.PROFILE_NAME_PREFERENCE));
                chamTypeText.setText(ChameleonIO.getDeviceDescription(ChameleonIO.CHAMELEON_MINI_BOARD_TYPE));
                hardwareIDText.setText(Settings.chameleonDeviceSerialNumber);
                connStatusText.setText(Settings.getActiveSerialIOPort().isWiredUSB() ? "USB connection" : "BT connection");
            }
            else {
                deviceNameText.setText(Settings.chameleonDeviceNickname);
                chamTypeText.setText("None");
                hardwareIDText.setText("None");
                connStatusText.setText("Not connected");
            }
            // Chameleon device connect / disconnect buttons:
            Button chamConnectBtn = tabMainLayoutView.findViewById(R.id.connectToDeviceButton);
            chamConnectBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    if(Settings.getActiveSerialIOPort() != null) {
                        Utils.displayToastMessageShort("Chameleon device already connected.");
                    }
                    else {
                        Settings.stopSerialIOConnectionDiscovery();
                        Settings.initializeSerialIOConnections();
                        Utils.displayToastMessageShort("Attempting to connect to chameleon device.");
                    }
                }
            });
            Button chamDisconnectBtn = tabMainLayoutView.findViewById(R.id.disconnectFromDeviceButton);
            chamDisconnectBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View btn) {
                    if(Settings.getActiveSerialIOPort() == null) {
                        Utils.displayToastMessageShort("Chameleon device not yet connected.");
                    }
                    else {
                        Settings.stopSerialIOConnectionDiscovery();
                        ChameleonSerialIOInterface serialPort = Settings.getActiveSerialIOPort();
                        serialPort.shutdownSerial();
                        Utils.displayToastMessageShort("Shutdown connection to active chameleon device.");
                    }
                }
            });
        }
        return true;
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
        spinner.setAdapter(spinnerAdapter);
        if(queryCmd != null && Settings.getActiveSerialIOPort() != null) {
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

}