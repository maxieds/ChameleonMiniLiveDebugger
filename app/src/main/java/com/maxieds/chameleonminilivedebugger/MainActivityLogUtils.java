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
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_LOGS;

public class MainActivityLogUtils {

    private static final String TAG = MainActivityLogUtils.class.getSimpleName();

    public static LinearLayout logDataFeed;
    public static List<LogEntryBase> logDataEntries = new ArrayList<LogEntryBase>();
    public static boolean logDataFeedConfigured = false;
    public static ScrollView logScrollView;
    public static int RECORDID = 0;

    public static void moveLiveLogTabScrollerToBottom() {
        if(logScrollView != null) {
            logScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ScrollView logScroller = (ScrollView) LiveLoggerActivity.getInstance().findViewById(R.id.log_scroll_view);
                    if(logScroller == null) {
                        return;
                    }
                    LinearLayout lastLogElt = (LinearLayout) logDataFeed.getChildAt(logDataFeed.getChildCount() - 1);
                    lastLogElt.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int bottomEltHeight = lastLogElt.getMeasuredHeight();
                    logScroller.scrollTo(0, logScroller.getBottom() + bottomEltHeight);
                }
            }, 75);
        }
    }

    /**
     * Appends a new log to the logging interface tab.
     * @param logEntry
     * @see LogEntryUI
     * @see LogEntryMetadataRecord
     */
    public static void appendNewLog(LogEntryBase logEntry) {
        if(LiveLoggerActivity.getInstance() == null) {
            return;
        }
        else if(LiveLoggerActivity.getInstance().getSelectedTab() != TAB_LOG && LiveLoggerActivity.getInstance() != null) {
            if(logEntry instanceof LogEntryUI) {
                LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconNewXFer, R.drawable.statusxfer16);
            }
            else {
                LiveLoggerActivity.getInstance().setStatusIcon(R.id.statusIconNewMsg, R.drawable.statusnewmsg16);
            }
        }
        if(logDataFeed != null && logDataEntries != null) {
            logDataFeed.addView(logEntry.getLayoutContainer());
            logDataEntries.add(logEntry);
        }
        if(logEntry instanceof LogEntryMetadataRecord) { // switch to the log tab to display the results:
            TabLayout tabLayout = (TabLayout) LiveLoggerActivity.getInstance().findViewById(R.id.tab_layout);
            if(tabLayout != null) {
                tabLayout.getTabAt(TAB_LOG).select();
                TabFragment.UITAB_DATA[TAB_LOG].selectMenuItem(TAB_LOG_MITEM_LOGS);
                moveLiveLogTabScrollerToBottom();
            }
        }
        moveLiveLogTabScrollerToBottom();
    }

    public static void clearAllLogs() {
        if (RECORDID > 0) {
            logDataEntries.clear();
            RECORDID = 0;
            logDataFeed.removeAllViewsInLayout();
        }
    }

    /**
     * Removes repeated log entries in sequential order in the logging tab.
     * Useful for pretty-fying / cleaning up the log entries when a device posts repeated
     * APDU command requests, or zero bits.
     */
    public static void collapseSimilarLogs() {
        if(RECORDID == 0)
            return;
        byte[] curBits = null;
        boolean newBits = true;
        for(int v = 0; v < logDataEntries.size(); v++) {
            LogEntryBase lde = logDataEntries.get(v);
            if(lde instanceof LogEntryMetadataRecord) {
                newBits = true;
                continue;
            }
            else if(lde instanceof LogEntryUI && newBits) {
                byte[] nextDataPattern = ((LogEntryUI) lde).getEntryData();
                curBits = new byte[nextDataPattern.length];
                System.arraycopy(nextDataPattern, 0, curBits, 0, nextDataPattern.length);
                newBits = false;
            }
            else if(Arrays.equals(curBits, ((LogEntryUI) lde).getEntryData())) {
                logDataFeed.getChildAt(v).setVisibility(LinearLayout.GONE);
            }
            else {
                newBits = true;
            }
        }
    }

    /**
     * Highlights the selected logs (by checkmark in the Log tab) in the color of the passed button.
     * @param highlightColor
     */
    public static void selectedHighlightedLogs(int highlightColor) {
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                boolean isChecked = ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).isChecked();
                if (isChecked)
                    logEntryView.setBackgroundColor(highlightColor);
            }
        }
    }

    /**
     * Unchecks all of the selected logs in the Log tab.
     */
    public static void uncheckAllLogs() {
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).setChecked(false);
            }
        }
    }

    /**
     * Used to mark whether the APDU response in the log is incoming / outgoing from
     * card <--> reader. Mostly reserved for future use as the Chameleon currently only logs responses
     * in one direction anyhow.
     * @param directionFlag
     */
    public static void setSelectedXFerOnLogs(int directionFlag) {
        Drawable dirArrowIcon = LiveLoggerActivity.getInstance().getResources().getDrawable(R.drawable.xfer16);
        if(directionFlag == 1)
            dirArrowIcon = LiveLoggerActivity.getInstance().getResources().getDrawable(R.drawable.incoming16v2);
        else if(directionFlag == 2)
            dirArrowIcon = LiveLoggerActivity.getInstance().getResources().getDrawable(R.drawable.outgoing16v2);
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                boolean isChecked = ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).isChecked();
                if (isChecked) {
                    ImageView xferMarker = (ImageView) logEntryView.findViewById(R.id.inputDirIndicatorImg);
                    xferMarker.setImageDrawable(dirArrowIcon);
                }
            }
        }
    }

    /**
     * Handles parsing of the buttons in the Logging Tools menu to be applied to all selected logs.
     * @param actionFlag
     */
    public static void processBatchOfSelectedLogs(String actionFlag) {
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                boolean isChecked = ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).isChecked();
                int recordIdx = ((LogEntryUI) logDataEntries.get(vi)).getRecordIndex();
                if (isChecked && actionFlag.equals("SEND")) {
                    String byteString = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                    ChameleonIO.executeChameleonMiniCommand("SEND " + byteString, ChameleonIO.TIMEOUT);
                }
                else if(isChecked && actionFlag.equals("SEND_RAW")) {
                    String byteString = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                    ChameleonIO.executeChameleonMiniCommand("SEND_RAW " + byteString, ChameleonIO.TIMEOUT);
                }
                else if(isChecked && actionFlag.equals("CLONE_UID")) {
                    String uid = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                    if(uid.length() != 2 * ChameleonIO.deviceStatus.UIDSIZE) {
                        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", String.format("Number of bytes for record #%d != the required %d bytes!", recordIdx, ChameleonIO.deviceStatus.UIDSIZE)));
                    }
                    else {
                        ChameleonIO.executeChameleonMiniCommand("UID=" + uid, ChameleonIO.TIMEOUT);
                    }
                }
                else if(isChecked && actionFlag.equals("PRINT")) {
                    byte[] rawBytes = ((LogEntryUI) logDataEntries.get(vi)).getEntryData();
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("PRINT", Utils.bytes2Hex(rawBytes) + "\n------\n" + Utils.bytes2Ascii(rawBytes)));
                }
                else if(isChecked && actionFlag.equals("HIDE")) {
                    logEntryView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Stores the user input for descriptions of the new annotation events available in the
     * Logging Tools tab.
     */
    public static String userInputStack;

    /**
     * Prompts for a user description of the indicated annotation event from the
     * Log Tools tab.
     * @param promptMsg
     * @ref LiveLoggerActivity.userInputStack
     * @see res/layout/log_tab_logtoolsols.xml
     */
    public static void displayUserInputPrompt(String promptMsg) {
        final EditText userInput = new EditText(LiveLoggerActivity.getInstance());
        userInput.setHint("What is the event description?");
        new AlertDialog.Builder(LiveLoggerActivity.getInstance())
                .setTitle(promptMsg)
                .setMessage("Enter annotation for the current log.")
                .setView(userInput)
                .setPositiveButton("Submit Message", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        userInputStack = userInput.getText().toString() + "\n" + Utils.getGPSLocationString();
                        throw new RuntimeException("The user input is ready.");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public static void performLogSearch() {
        long startTime = System.currentTimeMillis();
        // clear out the existing search data first:
        ScrollView searchResultsScroller = (ScrollView) LiveLoggerActivity.getInstance().findViewById(R.id.searchResultsScrollView);
        if(searchResultsScroller.getChildCount() != 0) {
            searchResultsScroller.removeViewAt(0);
        }
        LinearLayout searchResultsContainer = new LinearLayout(LiveLoggerActivity.getInstance().getApplicationContext());
        searchResultsContainer.setOrientation(LinearLayout.VERTICAL);
        searchResultsScroller.addView(searchResultsContainer);

        boolean selectedBytes = ((RadioButton) LiveLoggerActivity.getInstance().findViewById(R.id.radio_search_bytes)).isChecked();
        String searchString = ((TextView) LiveLoggerActivity.getInstance().findViewById(R.id.userInputSearchData)).getText().toString();
        if(searchString.equals(""))
            return;
        else if(selectedBytes && !Utils.stringIsHexadecimal(searchString)) {
            searchResultsContainer.addView(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Not a hexadecimal string.").getLayoutContainer());
            return;
        }
        else if(selectedBytes) {
            searchString = searchString.replace("[\n\t\r]+", "").replaceAll("..(?!$)", "$0 ");
        }
        searchString = searchString.toLowerCase(Locale.ENGLISH);

        boolean searchStatus = ((CheckBox) LiveLoggerActivity.getInstance().findViewById(R.id.entrySearchIncludeStatus)).isChecked();
        boolean searchAPDU = ((CheckBox) LiveLoggerActivity.getInstance().findViewById(R.id.entrySearchAPDU)).isChecked();
        boolean searchLogPayload = ((CheckBox) LiveLoggerActivity.getInstance().findViewById(R.id.entrySearchRawLogData)).isChecked();
        boolean searchLogHeaders = ((CheckBox) LiveLoggerActivity.getInstance().findViewById(R.id.entrySearchLogHeaders)).isChecked();
        int matchCount = 0;
        Log.i(TAG, "Searching for: " + searchString);
        for(int vi = 0; vi < logDataEntries.size(); vi++) {
            if (logDataEntries.get(vi) instanceof LogEntryMetadataRecord) {
                if (searchStatus && logDataEntries.get(vi).toString().toLowerCase(Locale.ENGLISH).contains(searchString)) {
                    searchResultsContainer.addView(logDataEntries.get(vi).cloneLayoutContainer());
                    matchCount++;
                }
                continue;
            }
            Log.i(TAG, ((LogEntryUI) logDataEntries.get(vi)).getPayloadDataString(selectedBytes));
            if (searchAPDU && ((LogEntryUI) logDataEntries.get(vi)).getAPDUString().toLowerCase(Locale.ENGLISH).contains(searchString) ||
                    searchLogHeaders && ((LogEntryUI) logDataEntries.get(vi)).getLogCodeName().toLowerCase(Locale.ENGLISH).contains(searchString) ||
                    searchLogPayload && ((LogEntryUI) logDataEntries.get(vi)).getPayloadDataString(selectedBytes).toLowerCase(Locale.ENGLISH).contains(searchString)) {
                LinearLayout searchResult = (LinearLayout) logDataEntries.get(vi).cloneLayoutContainer();
                searchResult.setVisibility(LinearLayout.VISIBLE);
                searchResult.setEnabled(true);
                searchResult.setMinimumWidth(350);
                searchResult.setMinimumHeight(150);
                LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                searchResultsContainer.addView(searchResult, lllp);
                Log.i(TAG, "Case II: Record " + vi + " matches");
                matchCount++;
            }
        }
        double diffSeconds = (double) (System.currentTimeMillis() - startTime) / 1000.0;
        String resultStr = String.format(Locale.ENGLISH, "Explored #%d logs in %4g seconds for a total of #%d matching records.",
                logDataEntries.size(), diffSeconds, matchCount);
        searchResultsContainer.addView(LogEntryMetadataRecord.createDefaultEventRecord("SEARCH", resultStr).getLayoutContainer());
    }
}
