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

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_MITEM_LOGS;

public class LogUtils {

    private static final String TAG = LogUtils.class.getSimpleName();

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
                    ScrollView logScroller = (ScrollView) LiveLoggerActivity.getLiveLoggerInstance().findViewById(R.id.log_scroll_view);
                    if(logScroller == null) {
                        return;
                    }
                    LinearLayout lastLogElt = (LinearLayout) logDataFeed.getChildAt(logDataFeed.getChildCount() - 1);
                    if(lastLogElt == null) {
                        return;
                    }
                    lastLogElt.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int bottomEltHeight = lastLogElt.getMeasuredHeight();
                    if(logScroller.getBottom() > lastLogElt.getBottom()) {
                        logScroller.scrollTo(0, logScroller.getBottom());
                    }
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
        LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
        if(llActivity == null) {
            return;
        } else if(llActivity.getSelectedTab() != TAB_LOG) {
            if(logEntry instanceof LogEntryUI) {
                llActivity.setStatusIcon(R.id.statusIconNewXFer, R.drawable.statusxfer16);
            } else {
                llActivity.setStatusIcon(R.id.statusIconNewMsg, R.drawable.statusnewmsg16);
            }
        }
        if(logDataFeed != null && logDataEntries != null) {
            logDataFeed.addView(logEntry.getLayoutContainer());
            logDataEntries.add(logEntry);
        }
        if(logEntry instanceof LogEntryMetadataRecord) { // switch to the log tab to display the results:
            TabLayout tabLayout = (TabLayout) llActivity.findViewById(R.id.tab_layout);
            if(tabLayout != null) {
                tabLayout.getTabAt(TAB_LOG).select();
                if(TabFragment.UITAB_DATA[TAB_LOG] != null && TabFragment.UITAB_DATA[TAB_LOG].lastMenuIndex != TAB_LOG_MITEM_LOGS) {
                    TabFragment.UITAB_DATA[TAB_LOG].selectMenuItem(TAB_LOG_MITEM_LOGS);
                }
            }
        }
        moveLiveLogTabScrollerToBottom();
    }

    public static void clearAllLogs() {
        if(logDataEntries == null || logDataFeed == null) {
            return;
        } else if (RECORDID > 0) {
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
        if(logDataEntries == null || logDataFeed == null) {
            return;
        } else if(RECORDID == 0) {
            return;
        }
        byte[] curBits = null;
        boolean newBits = true;
        for(int v = 0; v < logDataEntries.size(); v++) {
            LogEntryBase lde = logDataEntries.get(v);
            if(lde == null) {
                continue;
            }
            if(lde instanceof LogEntryMetadataRecord) {
                newBits = true;
                continue;
            } else if(lde instanceof LogEntryUI && newBits) {
                byte[] nextDataPattern = ((LogEntryUI) lde).getEntryData();
                curBits = new byte[nextDataPattern.length];
                System.arraycopy(nextDataPattern, 0, curBits, 0, nextDataPattern.length);
                newBits = false;
            } else if(Arrays.equals(curBits, ((LogEntryUI) lde).getEntryData())) {
                View vthLogDataFeedView = logDataFeed.getChildAt(v);
                if(vthLogDataFeedView != null) {
                    vthLogDataFeedView.setVisibility(LinearLayout.GONE);
                }
            } else {
                newBits = true;
            }
        }
    }

    /**
     * Highlights the selected logs (by checkmark in the Log tab) in the color of the passed button.
     * @param highlightColor
     */
    public static void selectedHighlightedLogs(int highlightColor) {
        if(logDataEntries == null || logDataFeed == null) {
            return;
        }
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logEntryView != null && logDataEntries.get(vi) instanceof LogEntryUI) {
                CheckBox cb = (CheckBox) logEntryView.findViewById(R.id.entrySelect);
                if(cb == null) {
                    continue;
                }
                boolean isChecked = cb.isChecked();
                if (isChecked) {
                    logEntryView.setBackgroundColor(highlightColor);
                }
            }
        }
    }

    /**
     * Unchecks all of the selected logs in the Log tab.
     */
    public static void uncheckAllLogs() {
        if(logDataEntries == null || logDataFeed == null) {
            return;
        }
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logEntryView != null && logDataEntries.get(vi) instanceof LogEntryUI) {
                CheckBox cb = (CheckBox) logEntryView.findViewById(R.id.entrySelect);
                if(cb != null) {
                    cb.setChecked(false);
                }
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
        LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
        if(llActivity == null || llActivity.getResources() == null) {
            return;
        } else if(logDataEntries == null || logDataFeed == null) {
            return;
        }
        Drawable dirArrowIcon = llActivity.getResources().getDrawable(R.drawable.xfer16);
        if(directionFlag == 1) {
            dirArrowIcon = llActivity.getResources().getDrawable(R.drawable.incoming16v2);
        } else if(directionFlag == 2) {
            dirArrowIcon = llActivity.getResources().getDrawable(R.drawable.outgoing16v2);
        }
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logEntryView != null && logDataEntries.get(vi) instanceof LogEntryUI) {
                CheckBox cb = (CheckBox) logEntryView.findViewById(R.id.entrySelect);
                if(cb == null) {
                    continue;
                }
                boolean isChecked = cb.isChecked();
                if (isChecked) {
                    ImageView xferMarker = (ImageView) logEntryView.findViewById(R.id.inputDirIndicatorImg);
                    if(xferMarker != null) {
                        xferMarker.setImageDrawable(dirArrowIcon);
                    }
                }
            }
        }
    }

    /**
     * Handles parsing of the buttons in the Logging Tools menu to be applied to all selected logs.
     * @param actionFlag
     */
    public static void processBatchOfSelectedLogs(String actionFlag) {
        if(logDataEntries == null || logDataFeed == null) {
            return;
        }
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logEntryView != null && logDataEntries.get(vi) instanceof LogEntryUI) {
                CheckBox cb = (CheckBox) logEntryView.findViewById(R.id.entrySelect);
                if(cb == null) {
                    continue;
                }
                boolean isChecked = cb.isChecked();
                LogEntryUI logEntryUIInst = (LogEntryUI) logDataEntries.get(vi);
                if(logEntryUIInst == null) {
                    continue;
                }
                int recordIdx = logEntryUIInst.getRecordIndex();
                String payloadDataDesc = logEntryUIInst.getPayloadData();
                if (isChecked && actionFlag.equals("SEND")) {
                    String byteString = payloadDataDesc;
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                    ChameleonIO.executeChameleonMiniCommand("SEND " + byteString, ChameleonIO.TIMEOUT);
                }
                else if(isChecked && actionFlag.equals("SEND_RAW")) {
                    String byteString = payloadDataDesc;
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                    ChameleonIO.executeChameleonMiniCommand("SEND_RAW " + byteString, ChameleonIO.TIMEOUT);
                }
                else if(isChecked && actionFlag.equals("CLONE_UID")) {
                    String uid = payloadDataDesc;
                    if(uid.length() != 2 * ChameleonIO.deviceStatus.UIDSIZE) {
                        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", String.format("Number of bytes for record #%d != the required %d bytes!", recordIdx, ChameleonIO.deviceStatus.UIDSIZE)));
                    }
                    else {
                        ChameleonIO.executeChameleonMiniCommand("UID=" + uid, ChameleonIO.TIMEOUT);
                    }
                }
                else if(isChecked && actionFlag.equals("PRINT")) {
                    byte[] rawBytes = logEntryUIInst.getEntryData();
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
        final EditText userInput = new EditText(LiveLoggerActivity.getLiveLoggerInstance());
        userInput.setHint("What is the event description?");
        new AlertDialog.Builder(LiveLoggerActivity.getLiveLoggerInstance())
                .setTitle(promptMsg)
                .setMessage("Enter annotation for the current log.")
                .setView(userInput)
                .setPositiveButton("Submit Message", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(userInput == null || userInput.getText() == null || userInputStack == null) {
                            return;
                        }
                        userInputStack = userInput.getText().toString();
                        if(!userInputStack.equals("")) {
                            userInputStack += "\n";
                        }
                        userInputStack += Utils.getGPSLocationString();
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
        LiveLoggerActivity llActivity = LiveLoggerActivity.getLiveLoggerInstance();
        if(llActivity == null) {
            return;
        } else if(logDataEntries == null) {
            String toastMsg = "Unable to perform search -- The main activity is not initialized -- Aborting search operation";
            Utils.displayToastMessage(llActivity, toastMsg, Toast.LENGTH_SHORT);
        }
        /* Clear out the existing search data first: */
        ScrollView searchResultsScroller = (ScrollView) llActivity.findViewById(R.id.searchResultsScrollView);
        if(searchResultsScroller == null) {
            String toastMsg = "Unable to perform search -- The main activity is not initialized -- Aborting search operation";
            Utils.displayToastMessage(llActivity, toastMsg, Toast.LENGTH_SHORT);
        }
        if(searchResultsScroller.getChildCount() != 0) {
            searchResultsScroller.removeViewAt(0);
        }
        LinearLayout searchResultsContainer = new LinearLayout(llActivity.getApplicationContext());
        searchResultsContainer.setOrientation(LinearLayout.VERTICAL);
        searchResultsScroller.addView(searchResultsContainer);
        /* Perform the search: */
        boolean selectedBytes = false;
        String searchString = "";
        boolean searchStatus, searchAPDU, searchLogPayload, searchLogHeaders;
        try {
            selectedBytes = ((RadioButton) llActivity.findViewById(R.id.radio_search_bytes)).isChecked();
            searchString = ((TextView) llActivity.findViewById(R.id.userInputSearchData)).getText().toString();
            if (searchString.equals(""))
                return;
            else if (selectedBytes && !Utils.stringIsHexadecimal(searchString)) {
                searchResultsContainer.addView(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Not a hexadecimal string.").getLayoutContainer());
                return;
            } else if (selectedBytes) {
                searchString = searchString.replace("[\n\t\r]+", "").replaceAll("..(?!$)", "$0 ");
            }
            searchString = searchString.toLowerCase(BuildConfig.DEFAULT_LOCALE);
            searchStatus = ((CheckBox) llActivity.findViewById(R.id.entrySearchIncludeStatus)).isChecked();
            searchAPDU = ((CheckBox) llActivity.findViewById(R.id.entrySearchAPDU)).isChecked();
            searchLogPayload = ((CheckBox) llActivity.findViewById(R.id.entrySearchRawLogData)).isChecked();
            searchLogHeaders = ((CheckBox) llActivity.findViewById(R.id.entrySearchLogHeaders)).isChecked();
        } catch(NullPointerException npe) {
            AndroidLogger.printStackTrace(npe);
            return;
        }
        AndroidLogger.i(TAG, "Searching for: " + searchString);
        int matchCount = 0;
        for(int vi = 0; vi < logDataEntries.size(); vi++) {
            LogEntryBase nextLogEntry = logDataEntries.get(vi);
            if(nextLogEntry == null) {
                continue;
            }
            if (nextLogEntry instanceof LogEntryMetadataRecord) {
                if (searchStatus && nextLogEntry.toString().toLowerCase(BuildConfig.DEFAULT_LOCALE).contains(searchString)) {
                    searchResultsContainer.addView(nextLogEntry.cloneLayoutContainer());
                    matchCount++;
                }
                continue;
            }
            LogEntryUI nextLogEntryUI = (LogEntryUI) nextLogEntry;
            AndroidLogger.i(TAG, nextLogEntryUI.getPayloadDataString(selectedBytes));
            if (searchAPDU && nextLogEntryUI.getAPDUString().toLowerCase(BuildConfig.DEFAULT_LOCALE).contains(searchString) ||
                    searchLogHeaders && nextLogEntryUI.getLogCodeName().toLowerCase(BuildConfig.DEFAULT_LOCALE).contains(searchString) ||
                    searchLogPayload && nextLogEntryUI.getPayloadDataString(selectedBytes).toLowerCase(BuildConfig.DEFAULT_LOCALE).contains(searchString)) {
                LinearLayout searchResult = (LinearLayout) nextLogEntryUI.cloneLayoutContainer();
                if(searchResult != null) {
                    searchResult.setVisibility(LinearLayout.VISIBLE);
                    searchResult.setEnabled(true);
                    searchResult.setMinimumWidth(350);
                    searchResult.setMinimumHeight(150);
                    LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    searchResultsContainer.addView(searchResult, lllp);
                    AndroidLogger.i(TAG, "Case II: Record " + vi + " matches");
                    matchCount++;
                }
            }
        }
        /* Report stats on the search time and display the findings: */
        double diffSeconds = (double) (System.currentTimeMillis() - startTime) / 1000.0;
        String resultStr = String.format(BuildConfig.DEFAULT_LOCALE, "Explored #%d logs in %4g seconds for a total of #%d matching records.",
                logDataEntries.size(), diffSeconds, matchCount);
        searchResultsContainer.addView(LogEntryMetadataRecord.createDefaultEventRecord("SEARCH", resultStr).getLayoutContainer());
    }
}
