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

package com.maxieds.chameleonminilivedebugger.ScriptingAPI;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

import com.maxieds.chameleonminilivedebugger.AndroidLogger;
import com.maxieds.chameleonminilivedebugger.LiveLoggerActivity;
import com.maxieds.chameleonminilivedebugger.R;
import com.maxieds.chameleonminilivedebugger.TabFragment;
import com.maxieds.chameleonminilivedebugger.Utils;
import com.maxieds.chameleonminilivedebugger.BuildConfig;

public class ScriptingGUIConsole {

    public enum ScriptingConsoleRecordType {
        SCRECORD_INFOMSG,
        SCRECORD_ERROR_WARNING,
        SCRECORD_CHAMCMDRESP,
        SCRECORD_BREAKPOINT,
        SCRECORD_SCRIPT_SUMMARY;
    }

    public static class ConsoleOutputRecord {

        private View mainLayoutView;

        public ConsoleOutputRecord(String recTitleMsg) {
            LayoutInflater inflater = LiveLoggerActivity.getInstance().getLayoutInflater();
            mainLayoutView = inflater.inflate(R.layout.scripting_console_record_entry_base, null);
            if(mainLayoutView == null) {
                return;
            }
            GradientDrawable gradientBg = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {
                            Utils.getColorFromTheme(R.attr.colorAccent),
                            Utils.getColorFromTheme(R.attr.colorAccentHighlight)
                    });
            LinearLayout mainHdrLayout = mainLayoutView.findViewById(R.id.consoleOutputRecordHeaderLayout);
            if(mainHdrLayout != null) {
                mainHdrLayout.setBackground(gradientBg);
            }
            setRecordTitle(recTitleMsg);
        }

        public ConsoleOutputRecord setRecordTitle(String nextRecTitleMsg) {
            TextView tvRecTitle = (TextView) mainLayoutView.findViewById(R.id.consoleOutputTitleMsgText);
            if(tvRecTitle != null) {
                tvRecTitle.setText(nextRecTitleMsg);
            }
            return this;
        }

        public ConsoleOutputRecord setRecordIcon(@DrawableRes int recIconResID) {
            ImageView ivRecIcon = (ImageView) mainLayoutView.findViewById(R.id.consoleOutputRecordIcon);
            if(ivRecIcon != null) {
                Drawable recIconImg = LiveLoggerActivity.getInstance().getResources().getDrawable(recIconResID);
                ivRecIcon.setImageDrawable(recIconImg);
            }
            return this;
        }

        public ConsoleOutputRecord setRecordLineOfCode(int nextLoc) {
            TextView tvRecLoc = (TextView) mainLayoutView.findViewById(R.id.consoleOutputRecordLOCText);
            if(tvRecLoc != null) {
                if (nextLoc <= 0) {
                    tvRecLoc.setText("Line ----  ");
                } else {
                    tvRecLoc.setText(String.format(BuildConfig.DEFAULT_LOCALE, "Line % 3d  ", nextLoc));
                }
            }
            return this;
        }

        public ConsoleOutputRecord setRecordTimestamp(String timeStampData) {
            TextView tvRecTimestamp = (TextView) mainLayoutView.findViewById(R.id.consoleOutputRecordTimestampText);
            if(tvRecTimestamp != null) {
                tvRecTimestamp.setText(timeStampData);
            }
            return this;
        }

        public ConsoleOutputRecord setRecordTimestamp() {
            return setRecordTimestamp(Utils.getTimestamp());
        }

        private static final int RECORD_TYPE_MARKER_CHAR_WIDTH = 16;

        public ConsoleOutputRecord setRecordTypeMarker(String typeMarkerText) {
            TextView tvRecTypeMarker = (TextView) mainLayoutView.findViewById(R.id.consoleOutputRecordTypeText);
            if(tvRecTypeMarker != null) {
                if (typeMarkerText.length() > RECORD_TYPE_MARKER_CHAR_WIDTH) {
                    typeMarkerText = typeMarkerText.substring(0, RECORD_TYPE_MARKER_CHAR_WIDTH - 1);
                } else {
                    String fmtString = String.format(BuildConfig.DEFAULT_LOCALE, "%%%ds", RECORD_TYPE_MARKER_CHAR_WIDTH);
                    typeMarkerText = String.format(BuildConfig.DEFAULT_LOCALE, fmtString, typeMarkerText);
                }
                tvRecTypeMarker.setText(typeMarkerText);
            }
            return this;
        }

        public ConsoleOutputRecord setRecordTypeMarker(ScriptingConsoleRecordType scrType) {
            switch(scrType) {
                case SCRECORD_INFOMSG:
                    return setRecordTypeMarker("INFO");
                case SCRECORD_ERROR_WARNING:
                    return setRecordTypeMarker("EWARN");
                case SCRECORD_CHAMCMDRESP:
                    return setRecordTypeMarker("CMDRESP");
                case SCRECORD_BREAKPOINT:
                    return setRecordTypeMarker("BKPT");
                case SCRECORD_SCRIPT_SUMMARY:
                    return setRecordTypeMarker("RSUMM");
                default:
                    return setRecordTypeMarker("NONE");
            }
        }

        public ConsoleOutputRecord setMainContentLayout(@LayoutRes int layoutResID) {
            LinearLayout mainLayoutContainer = (LinearLayout) mainLayoutView.findViewById(R.id.consoleOutputRecordMainLayoutContainer);
            if(mainLayoutContainer != null) {
                mainLayoutContainer.removeAllViews();
                LayoutInflater inflater = LiveLoggerActivity.getInstance().getLayoutInflater();
                View mainLayoutInflatedView = inflater.inflate(layoutResID, mainLayoutContainer);
                if (mainLayoutInflatedView != null) {
                    mainLayoutView = mainLayoutInflatedView;
                    mainLayoutContainer.addView(mainLayoutView);
                }
            }
            return this;
        }

        public ConsoleOutputRecord setRecordMessageText(String msgPrefixData, String[] msgData) {
            TextView tvRecMainMsg = (TextView) mainLayoutView.findViewById(R.id.consoleOutputRecordMainTextBoxData);
            if(tvRecMainMsg != null) {
                if (msgData != null && msgData.length > 0) {
                    String nextMsgText = msgPrefixData;
                    for (int mdidx = 0; mdidx < msgData.length; mdidx++) {
                        nextMsgText += "\n" + msgData[mdidx];
                    }
                    SpannableString spanBulletListText = new SpannableString(nextMsgText);
                    int curBulletPos = msgPrefixData.length();
                    for (int mdidx = 0; mdidx < msgData.length; mdidx++) {
                        spanBulletListText.setSpan(
                                new BulletSpan(15, Utils.getColorFromTheme(R.attr.colorPrimaryDark)),
                                curBulletPos, curBulletPos + msgData[mdidx].length() + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        curBulletPos += msgData[mdidx].length() + 1;
                    }
                    tvRecMainMsg.setText(spanBulletListText);
                } else {
                    tvRecMainMsg.setText(msgPrefixData);
                }
            }
            return this;
        }

        public ConsoleOutputRecord setRecordMessageText(String msgPrefixData) {
            return setRecordMessageText(msgPrefixData, null);
        }

        public ConsoleOutputRecord hideRecordMessageText() {
            LinearLayout llMainMsgContainer = (LinearLayout) mainLayoutView.findViewById(R.id.consoleOutputRecordMainLayoutContainer);
            if(llMainMsgContainer != null) {
                llMainMsgContainer.setVisibility(LinearLayout.GONE);
            }
            return this;
        }

        public View getMainLayoutView() {
            return mainLayoutView;
        }

        public static ConsoleOutputRecord newRecordInstance(ScriptingConsoleRecordType scrType, String msgData) {
            return newRecordInstance(scrType, msgData, null, -1);
        }

        public static ConsoleOutputRecord newRecordInstance(ScriptingConsoleRecordType scrType, String msgDataPrefix, String msgData[], int lineOfCode) {
            switch(scrType) {
                case SCRECORD_INFOMSG:
                    return newInfoMessageInstance(msgDataPrefix, msgData, lineOfCode);
                case SCRECORD_ERROR_WARNING:
                    return newErrorWarningMessageInstance(msgDataPrefix, msgData, lineOfCode);
                case SCRECORD_BREAKPOINT:
                    return newBreakpointRecordInstance("--" + msgDataPrefix + "--", lineOfCode);
                case SCRECORD_CHAMCMDRESP:
                    return null;
                case SCRECORD_SCRIPT_SUMMARY:
                    return newScriptRuntimeSummaryRecordInstance(msgDataPrefix, msgData);
                default:
                    return null;
            }
        }

        public static ConsoleOutputRecord newRecordInstance(ScriptingConsoleRecordType scrType, ScriptingTypes.ScriptVariable scrVarData, int lineOfCode) {
            switch(scrType) {
                case SCRECORD_CHAMCMDRESP:
                    return newChameleonCommandResponseRecordInstance(scrVarData, lineOfCode);
                default:
                    String msgPrefixData = "Script Variable Data:";
                    String[] msgDataComponents = new String[] {
                            String.format(BuildConfig.DEFAULT_LOCALE, "AS BOOL:   %s", scrVarData.getValueAsBoolean() ? "TRUE" : "FALSE"),
                            String.format(BuildConfig.DEFAULT_LOCALE, "AS INT32:  %04x (% 6d)", scrVarData.getValueAsInt(), scrVarData.getValueAsInt()),
                            String.format(BuildConfig.DEFAULT_LOCALE, "AS ASCII:  %s", scrVarData.getValueAsString()),
                            String.format(BuildConfig.DEFAULT_LOCALE, "AS HEXSTR: %s", Utils.bytes2Ascii(scrVarData.getValueAsString().getBytes()))
                    };
                    return newInfoMessageInstance(msgPrefixData, msgDataComponents, lineOfCode);
            }
        }

        public static ConsoleOutputRecord newInfoMessageInstance(String msgDataPrefix, String msgData[], int lineOfCode) {
            ConsoleOutputRecord newInfoMsgRecord = new ConsoleOutputRecord(String.format(BuildConfig.DEFAULT_LOCALE, "Information"));
            newInfoMsgRecord.setMainContentLayout(R.layout.scripting_console_record_textinfomsg);
            newInfoMsgRecord.setRecordIcon(R.drawable.scripting_output_icon_info16);
            newInfoMsgRecord.setRecordLineOfCode(lineOfCode);
            newInfoMsgRecord.setRecordTimestamp();
            newInfoMsgRecord.setRecordTypeMarker(ScriptingConsoleRecordType.SCRECORD_INFOMSG);
            newInfoMsgRecord.setRecordMessageText(msgDataPrefix, msgData);
            return newInfoMsgRecord;
        }

        public static ConsoleOutputRecord newErrorWarningMessageInstance(String msgDataPrefix, String msgData[], int lineOfCode) {
            ConsoleOutputRecord newEWarnMsgRecord = new ConsoleOutputRecord(String.format(BuildConfig.DEFAULT_LOCALE, "Error -- Warning"));
            newEWarnMsgRecord.setMainContentLayout(R.layout.scripting_console_record_textinfomsg);
            newEWarnMsgRecord.setRecordIcon(R.drawable.scripting_output_icon_error16);
            newEWarnMsgRecord.setRecordLineOfCode(lineOfCode);
            newEWarnMsgRecord.setRecordTimestamp();
            newEWarnMsgRecord.setRecordTypeMarker(ScriptingConsoleRecordType.SCRECORD_ERROR_WARNING);
            newEWarnMsgRecord.setRecordMessageText(msgDataPrefix, msgData);
            return newEWarnMsgRecord;
        }

        public static ConsoleOutputRecord newBreakpointRecordInstance(String bpLabel, int lineOfCode) {
            ConsoleOutputRecord newBkptMsgRecord = new ConsoleOutputRecord(String.format(BuildConfig.DEFAULT_LOCALE, "Breakpoint '%s'", bpLabel));
            newBkptMsgRecord.setMainContentLayout(R.layout.scripting_console_record_textinfomsg);
            newBkptMsgRecord.setRecordIcon(R.drawable.scripting_output_icon_bkpt16);
            newBkptMsgRecord.setRecordLineOfCode(lineOfCode);
            newBkptMsgRecord.setRecordTimestamp();
            newBkptMsgRecord.setRecordTypeMarker(ScriptingConsoleRecordType.SCRECORD_BREAKPOINT);
            newBkptMsgRecord.hideRecordMessageText();
            return newBkptMsgRecord;
        }

        public static ConsoleOutputRecord newChameleonCommandResponseRecordInstance(ScriptingTypes.ScriptVariable scHashedArrayVar, int lineOfCode) {
            try {
                ConsoleOutputRecord newCmdRespMsgRecord = new ConsoleOutputRecord(String.format(BuildConfig.DEFAULT_LOCALE, "Command Response"));
                newCmdRespMsgRecord.setMainContentLayout(R.layout.scripting_console_record_cmdresp);
                newCmdRespMsgRecord.setRecordIcon(R.drawable.scripting_output_icon_cmdresp16_v1);
                newCmdRespMsgRecord.setRecordLineOfCode(lineOfCode);
                newCmdRespMsgRecord.setRecordTimestamp();
                newCmdRespMsgRecord.setRecordTypeMarker(ScriptingConsoleRecordType.SCRECORD_CHAMCMDRESP);
                TextView tvCmdName = (TextView) newCmdRespMsgRecord.getMainLayoutView().findViewById(R.id.localCmdResponseRecordChamCmdNameText);
                tvCmdName.setText(String.format(BuildConfig.DEFAULT_LOCALE, "%s", scHashedArrayVar.getValueAt("cmdName").getValueAsString()).toUpperCase(BuildConfig.DEFAULT_LOCALE));
                TextView tvCmdResp = (TextView) newCmdRespMsgRecord.getMainLayoutView().findViewById(R.id.localCmdResponseRecordChamCmdRespAndCodeText);
                tvCmdResp.setText(String.format(BuildConfig.DEFAULT_LOCALE, "%s (%s)",
                        scHashedArrayVar.getValueAt("respText").getValueAsString(), scHashedArrayVar.getValueAt("respCode").getValueAsString()));
                TextView tvCmdDataAscii = (TextView) newCmdRespMsgRecord.getMainLayoutView().findViewById(R.id.localCmdResponseRecordChamCmdReturnDataAsciiText);
                tvCmdDataAscii.setText(String.format(BuildConfig.DEFAULT_LOCALE, "%s", scHashedArrayVar.getValueAt("data").getValueAsString()));
                TextView tvCmdDataHex = (TextView) newCmdRespMsgRecord.getMainLayoutView().findViewById(R.id.localCmdResponseRecordChamCmdReturnDataHexText);
                tvCmdDataHex.setText(String.format(BuildConfig.DEFAULT_LOCALE, "%s", Utils.bytes2Ascii(scHashedArrayVar.getValueAt("data").getValueAsString().getBytes())));
                TextView tvCmdDataIsError = (TextView) newCmdRespMsgRecord.getMainLayoutView().findViewById(R.id.localCmdResponseRecordChamCmdIsErrorText);
                tvCmdDataIsError.setText(String.format(BuildConfig.DEFAULT_LOCALE, "%s", scHashedArrayVar.getValueAt("isError").getValueAsBoolean() ? "True" : "False"));
                TextView tvCmdDataIsTmt = (TextView) newCmdRespMsgRecord.getMainLayoutView().findViewById(R.id.localCmdResponseRecordChamCmdIsTimeoutText);
                tvCmdDataIsTmt.setText(String.format(BuildConfig.DEFAULT_LOCALE, "%s", scHashedArrayVar.getValueAt("isTimeout").getValueAsBoolean() ? "True" : "False"));
                return newCmdRespMsgRecord;
            } catch(NullPointerException npe) {
                AndroidLogger.printStackTrace(npe);
                return null;
            }

        }

        public static ConsoleOutputRecord newScriptRuntimeSummaryRecordInstance(String msgDataPrefix, String msgData[]) {
            ConsoleOutputRecord newRuntimeSummaryMsgRecord = new ConsoleOutputRecord("Script Runtime Summary");
            newRuntimeSummaryMsgRecord.setMainContentLayout(R.layout.scripting_console_record_textinfomsg);
            newRuntimeSummaryMsgRecord.setRecordIcon(R.drawable.scripting_output_icon_summary16_v1);
            newRuntimeSummaryMsgRecord.setRecordLineOfCode(-1);
            newRuntimeSummaryMsgRecord.setRecordTimestamp();
            newRuntimeSummaryMsgRecord.setRecordTypeMarker(ScriptingConsoleRecordType.SCRECORD_SCRIPT_SUMMARY);
            newRuntimeSummaryMsgRecord.setRecordMessageText(msgDataPrefix, msgData);
            return newRuntimeSummaryMsgRecord;
        }

    }

    public static void appendConsoleOutputRecordGenericMessage(String msgText) {
        appendConsoleOutputRecordInfoMessage(msgText, null, -1);
    }

    public static void appendConsoleOutputRecordInfoMessage(String msgText, String[] bulletedListText, int lineOfCode) {
        ConsoleOutputRecord infoMsgRecord = ConsoleOutputRecord.newInfoMessageInstance(msgText, bulletedListText, lineOfCode);
        appendConsoleOutputRecord(infoMsgRecord);
    }

    public static void appendConsoleOutputRecordErrorWarning(String msgText, String[] bulletedListText, int lineOfCode) {
        ConsoleOutputRecord ewarnMsgRecord = ConsoleOutputRecord.newErrorWarningMessageInstance(msgText, bulletedListText, lineOfCode);
        appendConsoleOutputRecord(ewarnMsgRecord);
    }

    public static void appendConsoleOutputRecordBreakpoint(String bpLabel, int lineOfCode) {
        ConsoleOutputRecord bkptMsgRecord = ConsoleOutputRecord.newBreakpointRecordInstance(bpLabel, lineOfCode);
        appendConsoleOutputRecord(bkptMsgRecord);
    }

    public static void appendConsoleOutputRecordChameleonCommandResponse(ScriptingTypes.ScriptVariable scrRespVar, int lineOfCode) {
        ConsoleOutputRecord cmdRespMsgRecord = ConsoleOutputRecord.newChameleonCommandResponseRecordInstance(scrRespVar, lineOfCode);
        appendConsoleOutputRecord(cmdRespMsgRecord);
    }

    public static void appendConsoleOutputRecordScriptRuntimeSummary(String summaryMsg, String[] bulletedListText) {
        ConsoleOutputRecord rtSummaryMsgRecord = ConsoleOutputRecord.newScriptRuntimeSummaryRecordInstance(summaryMsg, bulletedListText);
        appendConsoleOutputRecord(rtSummaryMsgRecord);
    }

    private static void appendConsoleOutputRecord(ConsoleOutputRecord consoleOutputRecord) {
        if(consoleOutputRecord == null || TabFragment.UITAB_DATA == null || TabFragment.UITAB_DATA[TabFragment.TAB_SCRIPTING] == null) {
            return;
        }
        LiveLoggerActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout consoleViewMainLayout = ChameleonScripting.getRunningInstance().getConsoleViewMainLayout();
                if (consoleViewMainLayout != null) {
                    consoleViewMainLayout.addView(consoleOutputRecord.getMainLayoutView());
                    consoleViewMainLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ScrollView mainLayoutScroller = (ScrollView) TabFragment.UITAB_DATA[TabFragment.TAB_SCRIPTING].tabMenuItemLayouts[TabFragment.TAB_SCRIPTING_MITEM_CONSOLE_VIEW].findViewById(R.id.scriptingTabConsoleViewMainLayoutScroller);
                            if (mainLayoutScroller == null || consoleViewMainLayout.getChildCount() == 0) {
                                return;
                            }
                            LinearLayout lastRecordElt = (LinearLayout) consoleViewMainLayout.getChildAt(consoleViewMainLayout.getChildCount() - 1);
                            if(lastRecordElt != null) {
                                lastRecordElt.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                                int bottomEltHeight = lastRecordElt.getMeasuredHeight();
                                if (mainLayoutScroller.getBottom() > lastRecordElt.getBottom()) {
                                    mainLayoutScroller.scrollTo(0, mainLayoutScroller.getBottom() + bottomEltHeight);
                                }
                            }
                        }
                    }, 75);
                }
            }
        });
    }

}
