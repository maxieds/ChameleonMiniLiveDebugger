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
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

public class MainActivityNavActions {

    private static final String TAG = MainActivityNavActions.class.getSimpleName();

    public static AlertDialog getAboutTheAppDialog() {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(LiveLoggerActivity.getLiveLoggerInstance(), R.style.SpinnerTheme);
        String rawAboutStr = LiveLoggerActivity.getLiveLoggerInstance().getString(R.string.apphtmlheader) +
                LiveLoggerActivity.getLiveLoggerInstance().getString(R.string.aboutapp) +
                LiveLoggerActivity.getLiveLoggerInstance().getString(R.string.privacyhtml) +
                LiveLoggerActivity.getLiveLoggerInstance().getString(R.string.apphtmlfooter);
        rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_CODE%%", String.valueOf(BuildConfig.VERSION_CODE));
        rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_NAME%%", String.valueOf(BuildConfig.VERSION_NAME));
        rawAboutStr = rawAboutStr.replace("%%ANDROID_FLAVOR_NAME%%", String.valueOf(BuildConfig.FLAVOR) + ", " + BuildConfig.BUILD_TIMESTAMP);
        rawAboutStr = rawAboutStr.replace("%%GIT_COMMIT_HASH%%", String.valueOf(BuildConfig.GIT_COMMIT_HASH));
        rawAboutStr = rawAboutStr.replace("%%GIT_COMMIT_DATE%%", String.valueOf(BuildConfig.GIT_COMMIT_DATE));
        int aboutLinkColor = LiveLoggerActivity.getLiveLoggerInstance().getTheme().obtainStyledAttributes(new int[] {R.attr.colorAboutLinkColor}).getColor(0,
                             LiveLoggerActivity.getLiveLoggerInstance().getResources().getColor(R.color.colorBigVeryBadError));
        rawAboutStr = rawAboutStr.replace("%%ABOUTLINKCOLOR%%", String.format(BuildConfig.DEFAULT_LOCALE, "#%06X", 0xFFFFFF & aboutLinkColor));

        WebView wv = new WebView(LiveLoggerActivity.getLiveLoggerInstance());
        wv.getSettings().setJavaScriptEnabled(false);
        wv.loadDataWithBaseURL(null, rawAboutStr, "text/html", "UTF-8", "");
        wv.setBackgroundColor(ThemesConfiguration.getThemeColorVariant(R.attr.colorAccentHighlight));
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        wv.setInitialScale(10);

        adBuilder.setCancelable(true);
        adBuilder.setTitle("");
        //adBuilder.setIcon(R.drawable.chameleonicon_about64_roundicon);
        adBuilder.setPositiveButton(
                "Back to Previous",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        adBuilder.setView(wv);
        adBuilder.setInverseBackgroundForced(true);
        AlertDialog alertDialog = adBuilder.create();
        return alertDialog;
    }

    public static void setSignalStrengthIndicator(int threshold) {
        double signalStrength = threshold / 4500.0;
        if (signalStrength >= 0.80)
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.signalStrength, R.drawable.signalbars5);
        else if (signalStrength >= 0.60)
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.signalStrength, R.drawable.signalbars4);
        else if (signalStrength >= 0.40)
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.signalStrength, R.drawable.signalbars3);
        else if (signalStrength >= 0.20)
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.signalStrength, R.drawable.signalbars2);
        else
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.signalStrength, R.drawable.signalbars1);
    }

}
