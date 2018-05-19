package com.maxieds.chameleonminilivedebugger;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Locale;

/**
 * <h1>Tab Fragment</h1>
 * Implements a Fragment for individual tab data in the application.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public class TabFragment extends Fragment {

    private static final String TAG = TabFragment.class.getSimpleName();

    /**
     * Definitions of the in-order tab indices.
     */
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int TAB_LOG = 0;
    public static final int TAB_TOOLS = 1;
    public static final int TAB_LOG_TOOLS = 2;
    public static final int TAB_EXPORT = 3;
    public static final int TAB_SEARCH = 4;
    public static final int TAB_APDU = 5;

    /**
     * Local tab-specific data stored by the class.
     */
    private int tabNumber;
    private int layoutResRef;
    private View inflatedView;

    /**
     * Effectively the default constructor used to obtain a new tab of the specified index.
     * @param page
     * @return
     */
    public static TabFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabFragment fragment = new TabFragment();
        fragment.tabNumber = page;
        fragment.setArguments(args);
        switch(page) {
            case TAB_LOG:
                fragment.layoutResRef = R.layout.logging_tab;
                break;
            case TAB_TOOLS:
                fragment.layoutResRef = R.layout.tools_menu_tab;
                break;
            case TAB_EXPORT:
                fragment.layoutResRef = R.layout.export_tab;
                break;
            case TAB_LOG_TOOLS:
                fragment.layoutResRef = R.layout.log_tools_tab;
                break;
            case TAB_SEARCH:
                fragment.layoutResRef = R.layout.search_tab;
                break;
            case TAB_APDU:
                fragment.layoutResRef = R.layout.apdu_tab;
                break;
            default:
                break;
        }
        return fragment;
    }

    /**
     * Called when the tab is created.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabNumber = getArguments().getInt(ARG_PAGE);
    }

    /**
     * Helper method to setup a peripheral spinner.
     * @param view
     * @param spinnerID
     * @param spinnerStringList
     * @param spinnerAdapter
     * @param queryCmd
     * @ref TabFragment.onCreateView
     * @see res/layout/tools_menu_tab.xml
     */
    private void connectPeripheralSpinnerAdapter(View view, int spinnerID, int spinnerStringList, SpinnerAdapter spinnerAdapter, String queryCmd) {
        final String[] spinnerList = getResources().getStringArray(spinnerStringList);
        spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spinnerList);
        Spinner spinner = (Spinner) view.findViewById(spinnerID);
        spinner.setAdapter(spinnerAdapter);
        if(queryCmd != null && LiveLoggerActivity.serialPort != null) {
            String deviceSetting = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, queryCmd);
            Log.i(TAG, "Returned deviceSetting: " + deviceSetting);
            spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(deviceSetting));
        }
        final Spinner localSpinnerRef = spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Spinner localSpinner = localSpinnerRef;
            String[] localSpinnerList = spinnerList;
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                    return;
                String setCmd = localSpinner.getTag().toString() + localSpinnerList[i];
                ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, setCmd, ChameleonIO.TIMEOUT);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    /**
     * Helper method to setup the advanced Chameleon Mini command-line spinner.
     * @param view
     * @param spinnerID
     * @param spinnerStringList
     * @param spinnerAdapter
     * @param queryCmd
     * @ref TabFragment.onCreateView
     * @see res/layout/tools_menu_tab.xml
     */
    private void connectCommandListSpinnerAdapter(View view, final int spinnerID, int spinnerStringList, SpinnerAdapter spinnerAdapter, String queryCmd) {
        final String[] spinnerList = getResources().getStringArray(spinnerStringList);
        spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spinnerList);
        Spinner spinner = (Spinner) view.findViewById(spinnerID);
        spinner.setAdapter(spinnerAdapter);
        final View localFinalView = view;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            View localView = localFinalView;
            String[] localSpinnerList = spinnerList;
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String setCmd = localSpinnerList[i];
                if(setCmd.charAt(0) != '-') {
                    String userInputBytes = ((EditText) LiveLoggerActivity.runningActivity.findViewById(R.id.userInputFormattedBytes)).getText().toString();
                    userInputBytes = userInputBytes.replace(" ", "").replace(":", "").replace("-", ""); // remove pretty printing / spaces formatting
                    boolean errorFlag = false, resetStatus = false;
                    if(setCmd.equals("UID=") && (!Utils.stringIsHexadecimal(userInputBytes) || userInputBytes.length() != 2 * ChameleonIO.deviceStatus.UIDSIZE)) {
                        errorFlag = true;
                    }
                    else if(setCmd.equals("SETTING=") && !userInputBytes.matches("-?[0-9]")) {
                        errorFlag = true;
                    }
                    else if((setCmd.equals("THRESHOLD=") || setCmd.equals("TIMEOUT=")) && !Utils.stringIsDecimal(userInputBytes)) {
                        errorFlag = true;
                    }
                    else if(setCmd.equals("UID=") || setCmd.equals("SETTING=") || setCmd.equals("THRESHOLD=") || setCmd.equals("TIMEOUT=")) {
                        setCmd += userInputBytes;
                        resetStatus = true;
                    }
                    else if(setCmd.equals("RANDOM UID")) {
                        byte[] randomBytes = Utils.getRandomBytes(ChameleonIO.deviceStatus.UIDSIZE);
                        setCmd = "UID=" + Utils.bytes2Hex(randomBytes).replace(" ", "");
                        resetStatus = true;
                    }
                    if(!errorFlag) {
                        String deviceSetting = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, setCmd);
                        LiveLoggerActivity.appendNewLog(new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, "INFO: Shell command of " + setCmd + " returned status " + ChameleonIO.DEVICE_RESPONSE_CODE, ChameleonIO.DEVICE_RESPONSE[0]));
                        if(resetStatus)
                            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                    }
                    else {
                        LiveLoggerActivity.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Command formatting error: the input user bytes are invalid or not of the correct length"));
                    }
                    ((Spinner) localView.findViewById(spinnerID)).setSelection(0);
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    /**
     * Inflates the layout and sets up the configuration of the widgets associated with each tab index.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View inflated tab
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layoutResRef, container, false);
        inflatedView = view;
        LiveLoggerActivity.defaultInflater = inflater;
        if(tabNumber == TAB_LOG && !LiveLoggerActivity.logDataFeedConfigured) {
            ScrollView logScroller = (ScrollView) view.findViewById(R.id.log_scroll_view);
            LinearLayout logDataFeed = LiveLoggerActivity.logDataFeed;
            logDataFeed.setOrientation(LinearLayout.VERTICAL);
            logScroller.addView(logDataFeed);
            logScroller.setFillViewport(true);
            LiveLoggerActivity.logScrollView = logScroller;
            LiveLoggerActivity.logDataFeed = logDataFeed;
            LiveLoggerActivity.logDataFeedConfigured = true;
        }
        else if(tabNumber == TAB_TOOLS && LiveLoggerActivity.spinnerRButtonLongAdapter == null) {
            // first connect the spinners to their resp. adapters so something will happen when a new option is selected:
            connectPeripheralSpinnerAdapter(view, R.id.RButtonSpinner, R.array.RButtonOptions, LiveLoggerActivity.spinnerRButtonAdapter, "RBUTTON?");
            connectPeripheralSpinnerAdapter(view, R.id.RButtonLongSpinner, R.array.RButtonLongOptions, LiveLoggerActivity.spinnerRButtonLongAdapter, "RBUTTON_LONG?");
            connectPeripheralSpinnerAdapter(view, R.id.LButtonSpinner, R.array.LButtonOptions, LiveLoggerActivity.spinnerLButtonAdapter, "LBUTTON?");
            connectPeripheralSpinnerAdapter(view, R.id.LButtonLongSpinner, R.array.LButtonLongOptions, LiveLoggerActivity.spinnerLButtonLongAdapter, "LBUTTON_LONG?");
            connectPeripheralSpinnerAdapter(view, R.id.LEDRedSpinner, R.array.LEDRedOptions, LiveLoggerActivity.spinnerLEDRedAdapter, "LEDRED?");
            connectPeripheralSpinnerAdapter(view, R.id.LEDGreenSpinner, R.array.LEDGreenOptions, LiveLoggerActivity.spinnerLEDGreenAdapter, "LEDGREEN?");
            connectPeripheralSpinnerAdapter(view, R.id.ButtonMyRevEBoardSpinner, R.array.ButtonMyRevEBoards, LiveLoggerActivity.spinnerButtonMyAdapter, "buttonmy?");
            connectPeripheralSpinnerAdapter(view, R.id.LogModeSpinner, R.array.LogModeOptions, LiveLoggerActivity.spinnerLogModeAdapter, "LOGMODE?");
            connectCommandListSpinnerAdapter(view, R.id.FullCmdListSpinner, R.array.FullCommandList, LiveLoggerActivity.spinnerCmdShellAdapter, "");

            Switch fieldSwitch = (Switch) view.findViewById(R.id.fieldOnOffSwitch);
            fieldSwitch.setChecked(ChameleonIO.deviceStatus.FIELD);
            fieldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "FIELD=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                }
            });


            Switch roSwitch = (Switch) view.findViewById(R.id.readonlyOnOffSwitch);
            roSwitch.setChecked(ChameleonIO.deviceStatus.READONLY);
            roSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "READONLY=" + (isChecked ? "1" : "0"), ChameleonIO.TIMEOUT);
                }
            });

            SeekBar thresholdSeekbar = (SeekBar) view.findViewById(R.id.thresholdSeekbar);
            int threshold = 400;
            if(LiveLoggerActivity.serialPort != null) {
                threshold = Integer.parseInt(LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, "THRESHOLD?"));
                thresholdSeekbar.setProgress(threshold);
            }
            thresholdSeekbar.incrementProgressBy(25);
            ((TextView) view.findViewById(R.id.thresholdSeekbarValueText)).setText(String.format(Locale.ENGLISH, "% 5d mV", threshold));
            final View seekbarView = view;
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
                    ChameleonIO.executeChameleonMiniCommand(LiveLoggerActivity.serialPort, "THRESHOLD=" + String.valueOf(nextThreshold), ChameleonIO.TIMEOUT);
                    ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                }
            });
            LiveLoggerActivity.setSignalStrengthIndicator(thresholdSeekbar.getProgress());

            NumberPicker settingsNumberPicker = (NumberPicker) view.findViewById(R.id.settingsNumberPicker);
            settingsNumberPicker.setDividerThickness(1);
            settingsNumberPicker.setOrientation(LinearLayout.HORIZONTAL);
            settingsNumberPicker.setValue(ChameleonIO.deviceStatus.DIP_SETTING);
            settingsNumberPicker.setFormatter("%02d");
            settingsNumberPicker.setTypeface("sans-serif", Typeface.BOLD_ITALIC);
            settingsNumberPicker.setOnLongPressUpdateInterval(25);
            settingsNumberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker numberPicker, int scrollState) {
                    if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                        String settingCmd = ChameleonIO.REVE_BOARD ? "settingmy=" : "SETTING=";
                        LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, settingCmd + numberPicker.getValue());
                        ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                    }
                }
            });
        }
        else if(tabNumber == TAB_SEARCH) {
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {LiveLoggerActivity.runningActivity.getThemeColorVariant(R.attr.colorPrimaryDark), LiveLoggerActivity.runningActivity.getThemeColorVariant(R.attr.colorPrimaryDark)};
            CompoundButtonCompat.setButtonTintList((CheckBox) view.findViewById(R.id.entrySearchIncludeStatus), new ColorStateList(states, colors));
            CompoundButtonCompat.setButtonTintList((CheckBox) view.findViewById(R.id.entrySearchAPDU), new ColorStateList(states, colors));
            CompoundButtonCompat.setButtonTintList((CheckBox) view.findViewById(R.id.entrySearchRawLogData), new ColorStateList(states, colors));
            CompoundButtonCompat.setButtonTintList((CheckBox) view.findViewById(R.id.entrySearchLogHeaders), new ColorStateList(states, colors));
        }
        else if(tabNumber == TAB_APDU) {
            ApduUtils.buildFullInstructionsList();
            ApduUtils.tabView = inflatedView;
            ScrollView sv = (ScrollView) inflatedView.findViewById(R.id.apduSearchResultsScrollView);
            //TextView tvSearchResults = new TextView(inflatedView.getContext());
            //tvSearchResults.setTextSize((float) 10.0);
            //tvSearchResults.setTypeface(Typeface.MONOSPACE);
            //sv.addView(tvSearchResults);
            LinearLayout searchResultsContainer = new LinearLayout(LiveLoggerActivity.runningActivity);
            searchResultsContainer.setOrientation(LinearLayout.VERTICAL);
            sv.addView(searchResultsContainer);
        }
        return inflatedView;
    }

    /**
     * Called when the tab view is destroyed.
     * (Nothing but the default behavior implemented here.)
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}