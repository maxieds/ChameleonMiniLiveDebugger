package com.maxieds.chameleonminilivedebugger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public class TabFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int TAB_LOG = 0;
    public static final int TAB_TOOLS = 1;
    public static final int TAB_EXPORT = 2;
    public static final int TAB_SEARCH = 3;
    public static boolean CFG_SPINNERS = false;

    private int tabNumber;
    private int layoutResRef;
    private View inflatedView;

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
            case TAB_SEARCH:
                fragment.layoutResRef = R.layout.search_tab;
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabNumber = getArguments().getInt(ARG_PAGE);
    }

    private void connectPeripheralSpinnerAdapter(View view, int spinnerID, int spinnerStringList, SpinnerAdapter spinnerAdapter, String queryCmd) {
        final String[] spinnerList = getResources().getStringArray(spinnerStringList);
        spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spinnerList);
        Spinner spinner = (Spinner) view.findViewById(spinnerID);
        spinner.setAdapter(spinnerAdapter);
        if(queryCmd != null) {
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

    private void connectCommandListSpinnerAdapter(View view, int spinnerID, int spinnerStringList, SpinnerAdapter spinnerAdapter, String queryCmd) {
        final String[] spinnerList = getResources().getStringArray(spinnerStringList);
        spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spinnerList);
        Spinner spinner = (Spinner) view.findViewById(spinnerID);
        spinner.setAdapter(spinnerAdapter);
        final Spinner localSpinnerRef = spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Spinner localSpinner = localSpinnerRef;
            String[] localSpinnerList = spinnerList;
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String setCmd = localSpinnerList[i];
                if(setCmd.charAt(0) != '-') {
                    if(setCmd.equals("SEND_RAW") || setCmd.equals("SEND") || setCmd.equals("UID=")) {
                        String userInputBytes = ((EditText) LiveLoggerActivity.runningActivity.findViewById(R.id.userInputFormattedBytes)).getText().toString();
                        userInputBytes = userInputBytes.replace(" ", ""); // remove pretty printing / spaces formatting
                        setCmd += userInputBytes;
                    }
                    String deviceSetting = LiveLoggerActivity.getSettingFromDevice(LiveLoggerActivity.serialPort, setCmd);
                    LiveLoggerActivity.appendNewLog(new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, "INFO: Shell command of " + setCmd + " returned status " + ChameleonIO.DEVICE_RESPONSE_CODE, ChameleonIO.DEVICE_RESPONSE));
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

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
            connectPeripheralSpinnerAdapter(view, R.id.LogModeSpinner, R.array.LogModeOptions, LiveLoggerActivity.spinnerLogModeAdapter, "LOGMODE?");
            connectCommandListSpinnerAdapter(view, R.id.FullCmdListSpinner, R.array.FullCommandList, LiveLoggerActivity.spinnerCmdShellAdapter, "");
        }
        return inflatedView;
    }


}
