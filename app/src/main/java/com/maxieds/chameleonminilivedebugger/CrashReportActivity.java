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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrashReportActivity extends ChameleonMiniLiveDebuggerActivity {

    private static final String TAG = CrashReportActivity.class.getSimpleName();

    public static final String INTENT_ACTION_START_ACTIVITY = "Intent.CrashReport.Action.StartRecoveryActivity";
    public static final String INTENT_ACTION_RESTART_CMLD_ACTIVITY = "Intent.CrashReport.Action.RestartCMLDMainActivity";
    public static final String INTENT_CMLD_RECOVERED_FROM_CRASH = "Intent.CrashReport.CMLDRestartRecoveredFromCrash";
    public static final String INTENT_STACK_TRACE = "Intent.CrashReport.StackTrace";
    public static final String INTENT_TIMESTAMP = "Intent.CrashReport.Timestamp";
    public static final String INTENT_CHAMELEON_DEVICE_TYPE = "Intent.CrashReport.ChameleonDeviceType";
    public static final String INTENT_SERIAL_CONNECTION_TYPE = "Intent.CrashReport.";
    public static final String INTENT_CHAMELEON_CONFIG = "Intent.CrashReport.ChameleonConfigType";
    public static final String INTENT_CHAMELEON_LOGMODE = "Intent.CrashReport.ChameleonLogMode";
    public static final String INTENT_CHAMELEON_TIMEOUT = "Intent.CrashReport.ChameleonCmdTimeout";

    public static final String CRASH_REPORT_THEME_NAME = "Standard Green (Default)";

    private String stackTrace;
    private String timeStamp;
    private String chameleonDeviceType;
    private String serialConnType;
    private String chameleonConfig;
    private String chameleonLogMode;
    private String chameleonTimeout;

    private Handler vibrateNotifyHandler;
    private Runnable vibrateNotifyRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AndroidSettingsStorage.loadPreviousSettings(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE);

        ThemesConfiguration.setLocalTheme(CRASH_REPORT_THEME_NAME, true);
        setContentView(R.layout.activity_crash_report);
        configureLayoutToolbar();
        if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(INTENT_ACTION_START_ACTIVITY)) {
            stackTrace = getIntent().getStringExtra(INTENT_STACK_TRACE);
            timeStamp = getIntent().getStringExtra(INTENT_TIMESTAMP);
            chameleonDeviceType = getIntent().getStringExtra(INTENT_CHAMELEON_DEVICE_TYPE);
            serialConnType = getIntent().getStringExtra(INTENT_SERIAL_CONNECTION_TYPE);
            chameleonConfig = getIntent().getStringExtra(INTENT_CHAMELEON_CONFIG);
            chameleonLogMode = getIntent().getStringExtra(INTENT_CHAMELEON_LOGMODE);
            chameleonTimeout = getIntent().getStringExtra(INTENT_CHAMELEON_TIMEOUT);
            configureLayoutDisplay(getIntent());
        }
        else {
            finish();
        }
        vibrateNotifyHandler = new Handler();
        vibrateNotifyRunnable = new Runnable() {
            @Override
            public void run() {
                signalCrashByVibration();
            }
        };
        vibrateNotifyHandler.postDelayed(vibrateNotifyRunnable, 500);

    }

    protected void configureLayoutToolbar() {
        Toolbar crashRptToolbar = (Toolbar) findViewById(R.id.crashReportActivityToolbar);
        crashRptToolbar.setTitle("Chameleon Mini Live Debugger");
        crashRptToolbar.setSubtitle("Crash report summary | v" + BuildConfig.VERSION_NAME);
        getWindow().setTitleColor(ThemesConfiguration.getThemeColorVariant(this, R.attr.actionBarBackgroundColor));
        getWindow().setStatusBarColor(ThemesConfiguration.getThemeColorVariant(this, R.attr.colorPrimaryDark));
        getWindow().setNavigationBarColor(ThemesConfiguration.getThemeColorVariant(this, R.attr.colorPrimaryDark));
    }

    protected void configureLayoutDisplay(Intent launchIntent) {
        TextView tvStackTraceHeader = (TextView) findViewById(R.id.stackTraceHeaderTextBar);
        GradientDrawable hdrGradientBg = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[] {
                        Utils.getColorFromTheme(R.attr.colorAccent),
                        Utils.getColorFromTheme(R.attr.colorAccentHighlight)
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
        long[] vibratePattern = { 0, 350, 500, 350, 500, 1250 };
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
                { "CMLD Version",           String.format(Locale.ENGLISH, "v%s (%d:%d)",
                                                          BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE,
                                                          BuildConfig.VERSION_CODE - 8080) },
                { "CMLD GitHub Commit",     String.format(Locale.ENGLISH, "%s @ %s",
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
                { "Android SDK",            String.format(Locale.ENGLISH, "%d", Build.VERSION.SDK_INT) },
                { "Android OS Release",     String.format(Locale.ENGLISH, "%s %s (%s / %s)",
                                                          Build.VERSION.BASE_OS, Build.VERSION.RELEASE,
                                                          Build.VERSION.CODENAME, Build.VERSION.INCREMENTAL) },
                { "Android Board",          Build.BOARD },
                { "Android Type",           Build.TYPE },
        };
        StringBuilder assembleIssueContentBldr = new StringBuilder();
        for(int sidx = 0; sidx < issueContentsData.length; sidx++) {
            assembleIssueContentBldr.append("* *" + issueContentsData[0] + "*: " + issueContentsData[1] + "\n");
        }
        String issueBodyText = "@maxieds:\n\n" + "**Crash data summary:**\n" + assembleIssueContentBldr.toString();
        issueBodyText += "**Stack trace:**\n\" + \"```java\n" + stackTrace + "\n```\n";
        issueBodyText += "**Additional comments:**\n\n*NONE*\n";
        String issueTitle = String.format(Locale.ENGLISH, "Crash Report: CMLD %s running %s %s [SDK %d] on device %s %s %s",
                                          BuildConfig.VERSION_NAME, Build.VERSION.BASE_OS, Build.VERSION.INCREMENTAL,
                                          Build.VERSION.SDK_INT, Build.MANUFACTURER, Build.PRODUCT, Build.MODEL);
        newIssueURL += String.format(Locale.ENGLISH, "title=%s&body=%s&assignee=maxieds",
                                     Utils.encodeAsciiToURL(issueTitle), Utils.encodeAsciiToURL(issueBodyText));
        return newIssueURL;
    }

    private boolean sendNewGitHubIssue() {
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
        startActivity(restartCMLDMainActivityIntent);
        finish();
        System.exit(-2);
    }

}
