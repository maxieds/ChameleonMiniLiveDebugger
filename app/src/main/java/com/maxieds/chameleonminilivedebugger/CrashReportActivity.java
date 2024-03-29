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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class CrashReportActivity extends ChameleonMiniLiveDebuggerActivity {

    private static final String TAG = CrashReportActivity.class.getSimpleName();

    public static final String INTENT_ACTION_START_ACTIVITY = "Intent.CrashReport.Action.StartRecoveryActivity";
    public static final String INTENT_ACTION_RESTART_CMLD_ACTIVITY = "Intent.CrashReport.Action.RestartCMLDMainActivity";
    public static final String INTENT_CMLD_RECOVERED_FROM_CRASH = "Intent.CrashReport.CMLDRestartRecoveredFromCrash";
    public static final String INTENT_STACK_TRACE = "Intent.CrashReport.StackTrace";
    public static final String INTENT_INVOKING_EXCPTMSG = "Intent.CrashReport.InvokingExceptionMessage";
    public static final String INTENT_TIMESTAMP = "Intent.CrashReport.Timestamp";
    public static final String INTENT_CHAMELEON_DEVICE_TYPE = "Intent.CrashReport.ChameleonDeviceType";
    public static final String INTENT_SERIAL_CONNECTION_TYPE = "Intent.CrashReport.";
    public static final String INTENT_CHAMELEON_CONFIG = "Intent.CrashReport.ChameleonConfigType";
    public static final String INTENT_CHAMELEON_LOGMODE = "Intent.CrashReport.ChameleonLogMode";
    public static final String INTENT_CHAMELEON_TIMEOUT = "Intent.CrashReport.ChameleonCmdTimeout";
    public static final String INTENT_LOG_FILE_DOWNLOAD_PATH = "Intent.CrashReport.LogFileDownloadPath";

    public static final String CRASH_REPORT_THEME_NAME = "Standard Green";

    private String stackTrace;
    private String invokingExcptMsg;
    private String timeStamp;
    private String chameleonDeviceType;
    private String serialConnType;
    private String chameleonConfig;
    private String chameleonLogMode;
    private String chameleonTimeout;
    private boolean haveLogFileDownload;
    private String logFileDownloadPath;

    private Handler notifyCrashReportLaunchHandler;
    private Runnable notifyCrashReportLaunchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AndroidSettingsStorage.loadPreviousSettings();

        ThemesConfiguration.setLocalTheme(CRASH_REPORT_THEME_NAME, true, this);
        setContentView(R.layout.activity_crash_report);
        configureLayoutToolbar();
        if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(INTENT_ACTION_START_ACTIVITY)) {
            stackTrace = getIntent().getStringExtra(INTENT_STACK_TRACE);
            invokingExcptMsg = getIntent().getStringExtra(INTENT_INVOKING_EXCPTMSG);
            timeStamp = getIntent().getStringExtra(INTENT_TIMESTAMP);
            chameleonDeviceType = getIntent().getStringExtra(INTENT_CHAMELEON_DEVICE_TYPE);
            serialConnType = getIntent().getStringExtra(INTENT_SERIAL_CONNECTION_TYPE);
            chameleonConfig = getIntent().getStringExtra(INTENT_CHAMELEON_CONFIG);
            chameleonLogMode = getIntent().getStringExtra(INTENT_CHAMELEON_LOGMODE);
            chameleonTimeout = getIntent().getStringExtra(INTENT_CHAMELEON_TIMEOUT);
            logFileDownloadPath = getIntent().getStringExtra(INTENT_LOG_FILE_DOWNLOAD_PATH);
            haveLogFileDownload = logFileDownloadPath != null && logFileDownloadPath.length() > 0;
            configureLayoutDisplay();
        }
        else {
            finish();
        }
        final ChameleonMiniLiveDebuggerActivity mainActivityCtxFinal = this;
        notifyCrashReportLaunchHandler = new Handler();
        notifyCrashReportLaunchRunnable = new Runnable() {
            final ChameleonMiniLiveDebuggerActivity mainActivityCtx = mainActivityCtxFinal;
            @Override
            public void run() {
                BluetoothUtils.resetBluetoothAdapterAtClose(mainActivityCtx);
                signalCrashByVibration();
            }
        };
        notifyCrashReportLaunchHandler.postDelayed(notifyCrashReportLaunchRunnable, 500);

    }

    protected void configureLayoutToolbar() {
        Toolbar crashRptToolbar = (Toolbar) findViewById(R.id.crashReportActivityToolbar);
        crashRptToolbar.setTitle("Chameleon Mini Live Debugger");
        crashRptToolbar.setSubtitle("Crash report | v" + BuildConfig.VERSION_NAME);
        getWindow().setTitleColor(ThemesConfiguration.getThemeColorVariant(this, R.attr.actionBarBackgroundColor));
        getWindow().setStatusBarColor(ThemesConfiguration.getThemeColorVariant(this, R.attr.colorPrimaryDark));
        getWindow().setNavigationBarColor(ThemesConfiguration.getThemeColorVariant(this, R.attr.colorPrimaryDark));
    }

    protected void configureLayoutDisplay() {
        TextView tvStackTraceHeader = (TextView) findViewById(R.id.stackTraceHeaderTextBar);
        GradientDrawable hdrGradientBg = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[] {
                        Utils.getColorFromTheme(R.attr.colorAccent, this),
                        Utils.getColorFromTheme(R.attr.colorAccentHighlight, this)
                });
        hdrGradientBg.setCornerRadius(27f);
        tvStackTraceHeader.setBackground(hdrGradientBg);
        TextView tvStackTraceData = (TextView) findViewById(R.id.crashReportActivityStackTraceText);
        tvStackTraceData.setSingleLine(false);
        tvStackTraceData.setText(highlightStackTrace(stackTrace), TextView.BufferType.SPANNABLE);
    }

    public static class StringHighlightTextSpec {

        public String matchRegexPattern;
        public int matchHighlightColor;
        public int matchStyleType;

        StringHighlightTextSpec(String regexPattern, int highlightColor, int styleType) {
            matchRegexPattern = regexPattern;
            matchHighlightColor = highlightColor;
            matchStyleType = styleType;
        }

    }

    public static final int STACK_TRACE_LINE_MAX_CHARS = 20;

    protected SpannableStringBuilder highlightStackTrace(String inputStackTrace) {
        /* Do preprocessing to get the line wrapping to work: */
        inputStackTrace = inputStackTrace.replaceAll("\\(", "\\( ");
        inputStackTrace = inputStackTrace.replaceAll("\\)", " \\)");
        inputStackTrace = inputStackTrace.replaceAll("\\.(\\w+\\( .*:.* \\))", "\\.\\-\\-\n     $1");
        String[] subLinesToTrim = inputStackTrace.split("\\-\\-");
        for(int sidx = 0; sidx < subLinesToTrim.length; sidx++) {
            String[] splitLine = subLinesToTrim[sidx].split("\n");
            if(splitLine.length > 2) {
                continue;
            }
            else if(splitLine.length == 0) {
                subLinesToTrim[sidx] = "\n";
            }
            boolean needEllipses = splitLine[0].length() >= STACK_TRACE_LINE_MAX_CHARS;
            subLinesToTrim[sidx] = splitLine[0].substring(0, Math.min(splitLine[0].length(), STACK_TRACE_LINE_MAX_CHARS - 1)) +
                    (needEllipses ? "<...>" : "") + "\n" + (splitLine.length == 2 ? splitLine[1] + "\n" : "");
        }
        inputStackTrace = String.join("", subLinesToTrim);
        stackTrace = inputStackTrace;
        return new SpannableStringBuilder(inputStackTrace);
    }

    protected void signalCrashByVibration() {
        Vibrator deviceVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibratePattern = new long[] { 0, 350, 500, 350, 500, 1250 };
        if(!deviceVibrator.hasVibrator()) {
            return;
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceVibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, -1));
        }
        else {
            deviceVibrator.vibrate(vibratePattern, -1);
        }
    }

    private String getEncodedNewGitHubIssueURL() {
        String newIssueURL = "https://github.com/maxieds/ChameleonMiniLiveDebugger/issues/new?";
        String[][] issueContentsData = new String[][] {
                { "CMLD Version",           String.format(BuildConfig.DEFAULT_LOCALE, "v%s (%d:%d)",
                                                   BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE,
                                                          BuildConfig.VERSION_CODE - 8080) },
                { "CMLD GitHub Commit",     String.format(BuildConfig.DEFAULT_LOCALE, "%s @ %s",
                                                          BuildConfig.GIT_COMMIT_HASH, BuildConfig.GIT_COMMIT_DATE) },
                { "CMLD Build Date",        BuildConfig.BUILD_TIMESTAMP },
                { "Chameleon Type",         chameleonDeviceType },
                { "Serial Connection Type", serialConnType },
                { "Chameleon Config",       chameleonConfig },
                { "Chameleon Logmode",      chameleonLogMode },
                { "Chameleon Timeout",      chameleonTimeout },
                { "Android Device",         Build.DEVICE },
                { "Android Manufacturer",   Build.MANUFACTURER },
                { "Android Brand",          Build.BRAND },
                { "Android Product",        Build.PRODUCT },
                { "Android Model",          Build.MODEL },
                { "Android Hardware",       Build.HARDWARE},
                { "Android SDK",            String.format(BuildConfig.DEFAULT_LOCALE, "%d", Build.VERSION.SDK_INT) },
                { "Android OS Release",     String.format(BuildConfig.DEFAULT_LOCALE, "%s %s (%s / %s)",
                                                          Build.VERSION.BASE_OS, Build.VERSION.RELEASE,
                                                          Build.VERSION.CODENAME, Build.VERSION.INCREMENTAL) },
                { "Android Board",          Build.BOARD },
                { "Android Type",           Build.TYPE },
        };
        StringBuilder assembleIssueContentBldr = new StringBuilder();
        for(int sidx = 0; sidx < issueContentsData.length; sidx++) {
            assembleIssueContentBldr.append("* *" + issueContentsData[sidx][0] + "*: " + issueContentsData[sidx][1] + "\n");
        }
        String issueBodyText = "@maxieds:\n\n" + "**Crash data summary:**\n" + assembleIssueContentBldr.toString();
        if (haveLogFileDownload) {
            issueBodyText += "\n**Log Data Associated With the Crash:**\n";
            issueBodyText += "*Make sure to attach the log file located at ";
            issueBodyText += logFileDownloadPath;
            issueBodyText += " on your local Android phone.*\n\n";
        }
        issueBodyText += "\n**Stack trace:**\n```java" + stackTrace + "\n```\n";
        issueBodyText += "\n**Additional Information:**\n";
        issueBodyText += "*PLEASE fill in more details about how you generated this error... ";
        issueBodyText += "What actions you took before this screen is displayed. Did the error happen on launch of ";
        issueBodyText += "the application? Did you click a button or switch tabs? Any extra details about how the error ";
        issueBodyText += "happened are useful to fixing the issue in future releases.*\n\n";
        int invokingMsgLastItemPos = invokingExcptMsg.lastIndexOf(':');
        String issueTitle = String.format(BuildConfig.DEFAULT_LOCALE, "Crash Report (CMLD %s): %s [Android %s, SDK %d]",
                                          BuildConfig.VERSION_NAME, invokingExcptMsg.substring(invokingMsgLastItemPos + 1),
                                          Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
        newIssueURL += String.format(BuildConfig.DEFAULT_LOCALE, "title=%s&body=%s&assignee=maxieds",
                                     Utils.encodeAsciiToURL(issueTitle), Utils.encodeAsciiToURL(issueBodyText));
        return newIssueURL;
    }

    private static final long ISSUE_INSTRUCTIONS_TOASTMSG_PAUSE_DURATION = 750; // milliseconds

    private boolean sendNewGitHubIssue() {
        String toastInstMsgToUsers = "Fill in the extra information section before posting the new issue!";
        if (haveLogFileDownload) {
            toastInstMsgToUsers += "\nMake sure to attach the local logging file.";
        }
        Utils.displayToastMessage(this, toastInstMsgToUsers, Toast.LENGTH_LONG);
        SystemClock.sleep(ISSUE_INSTRUCTIONS_TOASTMSG_PAUSE_DURATION);
        String newIssueURL = getEncodedNewGitHubIssueURL();
        Intent startIssueBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newIssueURL));
        startActivity(startIssueBrowserIntent);
        return true;
    }

    public void actionButtonCopyStackTrace(View btn) {
        ClipboardManager sysClipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipDataPlain = ClipData.newPlainText("CMLD Crash Stack Trace", stackTrace);
        sysClipBoard.setPrimaryClip(clipDataPlain);
    }

    public void actionButtonSendIssueReport(View btn) {
        sendNewGitHubIssue();
    }

    public void actionButtonRestartCMLDMainActivity(View btn) {
        Intent restartCMLDMainActivityIntent = new Intent(this, LiveLoggerActivity.class);
        restartCMLDMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        restartCMLDMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        restartCMLDMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        restartCMLDMainActivityIntent.setAction(CrashReportActivity.INTENT_ACTION_RESTART_CMLD_ACTIVITY);
        restartCMLDMainActivityIntent.putExtra(INTENT_CMLD_RECOVERED_FROM_CRASH, true);
        Utils.clearToastMessage();
        startActivity(restartCMLDMainActivityIntent);
        finish();
        System.exit(-2);
    }

}
