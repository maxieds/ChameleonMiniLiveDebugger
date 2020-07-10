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
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;

public class ThemesConfiguration {

    private static final String TAG = ThemesConfiguration.class.getSimpleName();

    /**
     * Obtains the color associated with the theme.
     * @param attrID
     * @return
     */
    @ColorInt
    public static int getThemeColorVariant(int attrID) {
        return LiveLoggerActivity.getInstance().getTheme().obtainStyledAttributes(new int[] {attrID}).getColor(0, attrID);
    }

    /**
     * Attempts to set themes will a background before the current tab has been loaded will
     * result in a NullPointerException getting issued by the system. We fix this by setting the
     * theme about 1 second after the application's onCreate(...) method is invoked.
     */
    public static String storedAppTheme = "Standard Green";
    public static int appThemeResID = R.style.AppTheme;
    public static Handler setThemeHandler = new Handler();
    public static Runnable setThemeRunner = new Runnable() {
        @Override
        public void run() {
            setLocalTheme(storedAppTheme, true);
        }
    };

    /**
     * Sets the local theme (before the ful UI updating to implement the theme change) based on
     * the passed theme text description.
     * @param themeDesc
     * @ref res/values/style.xml
     */
    public static void setLocalTheme(String themeDesc, boolean canResetBackgroundData) {
        int themeID;
        boolean resetBackground = false;
        int bgResID = 0;
        switch(themeDesc) {
            case "Amber":
                themeID = R.style.AppThemeAmber;
                break;
            case "Atlanta":
                themeID = R.style.AppThemeAtlanta;
                break;
            case "Black":
                themeID = R.style.AppThemeBlack;
                break;
            case "Blue":
                themeID = R.style.AppThemeBlue;
                break;
            case "Chocolate":
                themeID = R.style.AppThemeChocolate;
                break;
            case "Chicky":
                themeID = R.style.AppThemeChicky;
                break;
            case "Goldenrod":
                themeID = R.style.AppThemeGoldenrod;
                break;
            case "Standard Green":
                themeID = R.style.AppThemeGreen;
                break;
            case "Lightblue":
                themeID = R.style.AppThemeLightblue;
                break;
            case "Linux Green On Black":
                themeID = R.style.AppThemeLinuxGreenOnBlack;
                break;
            case "Purple":
                themeID = R.style.AppThemePurple;
                break;
            case "Red":
                themeID = R.style.AppThemeRed;
                break;
            case "Redmond":
                themeID = R.style.AppThemeRedmond;
                break;
            case "Teal":
                themeID = R.style.AppThemeTeal;
                break;
            case "Urbana Desfire":
                themeID = R.style.AppThemeUrbanaDesfire;
                break;
            case "White":
                themeID = R.style.AppThemeWhite;
                break;
            case "Winter":
                themeID = R.style.AppThemeWinter;
                break;
            default:
                themeID = R.style.AppThemeGreen;
        }
        Log.w(TAG, themeDesc);
        Log.w(TAG, String.valueOf(themeID));
        LiveLoggerActivity.getInstance().setTheme(themeID);
        appThemeResID = themeID;

    }

    public static void actionButtonAppSettings(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(LiveLoggerActivity.getInstance());
        final View dialogView = LiveLoggerActivity.getInstance().getLayoutInflater().inflate(R.layout.theme_config, null);
        if(!BuildConfig.FLAVOR.equals("paid")) { // restore the "bonus" for upgrading to the paid flavor:
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonAtlanta)).setEnabled(false);
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonBlack)).setEnabled(false);
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonChocolate)).setEnabled(false);
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonGoldenrod)).setEnabled(false);
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonLightblue)).setEnabled(false);
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonPurple)).setEnabled(false);
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonUrbanaDesfire)).setEnabled(false);
            ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonWinter)).setEnabled(false);
        }
        // set the correct current theme as the selected radio button:
        RadioGroup themeRadioGroup = (RadioGroup) dialogView.findViewById(R.id.themeRadioGroup);
        for(int rb = 0; rb < themeRadioGroup.getChildCount(); rb++) {
            RadioButton curThemeBtn = (RadioButton) themeRadioGroup.getChildAt(rb);
            if(curThemeBtn.isEnabled() && curThemeBtn.getText().toString().equals("Theme: " + storedAppTheme)) {
                curThemeBtn.setChecked(true);
                break;
            }
        }
        // finish constructing the theme selection dialog:
        ScrollView themesScroller = new ScrollView(LiveLoggerActivity.getInstance());
        themesScroller.addView(dialogView);
        dialog.setView(themesScroller);
        dialog.setIcon(R.drawable.settingsgears24);
        dialog.setTitle( "Application Theme Configuration: \n(Clears all logs.)");
        dialog.setPositiveButton( "Set Theme", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int whichBtn) {

                int getSelectedOption = ((RadioGroup) dialogView.findViewById(R.id.themeRadioGroup)).getCheckedRadioButtonId();
                String themeID = ((RadioButton) dialogView.findViewById(getSelectedOption)).getText().toString();
                String themeDesc = themeID.substring("Theme: ".length());
                setLocalTheme(themeDesc, true);
                storedAppTheme = themeDesc;

                // store the theme setting for when the app reopens:
                ThemesConfiguration.storedAppTheme = themeDesc;
                AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.THEMEID_PREFERENCE);

                // finally, apply the theme settings by (essentially) restarting the activity UI:
                MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("THEME", "New theme installed: " + themeDesc));

                LiveLoggerActivity.getInstance().recreate();

            }

        });
        dialog.setNegativeButton( "Cancel", null);
        dialog.setInverseBackgroundForced(true);
        dialog.show();
    }
}
