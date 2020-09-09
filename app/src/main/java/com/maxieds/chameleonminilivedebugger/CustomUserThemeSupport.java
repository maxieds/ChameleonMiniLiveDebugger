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
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.AttrRes;

import java.io.Serializable;
import java.util.Locale;

public class CustomUserThemeSupport implements Serializable {

    private static final String TAG = CustomUserThemeSupport.class.getSimpleName();
    private static Context APPLICATION_CONTEXT = null;
    public static CustomUserThemeSupport CUSTOM_USER_THEME_DATA = null;
    public static boolean USE_CUSTOM_USER_THEME_DATA = false;

    public static int USER_CUSTOM_THEME_COLOR_PRIMARY;
    public static int USER_CUSTOM_THEME_COLOR_PRIMARY_DARK;
    public static int USER_CUSTOM_THEME_COLOR_ACCENT;
    public static int USER_CUSTOM_THEME_COLOR_PRIMARY_DARK_LOG;
    public static int USER_CUSTOM_THEME_COLOR_ACCENT_LOG;
    public static int USER_CUSTOM_THEME_COLOR_ACCENT_HIGHLIGHT;
    public static int USER_CUSTOM_THEME_COLOR_ABOUT_LINK_COLOR;

    static {
        APPLICATION_CONTEXT = LiveLoggerActivity.getInstance();
        if(APPLICATION_CONTEXT != null) {
            USER_CUSTOM_THEME_COLOR_PRIMARY = APPLICATION_CONTEXT.getResources().getColor(R.color.transparent);
            USER_CUSTOM_THEME_COLOR_PRIMARY_DARK = APPLICATION_CONTEXT.getResources().getColor(R.color.transparent);
            USER_CUSTOM_THEME_COLOR_ACCENT = APPLICATION_CONTEXT.getResources().getColor(R.color.transparent);
            USER_CUSTOM_THEME_COLOR_PRIMARY_DARK_LOG = APPLICATION_CONTEXT.getResources().getColor(R.color.transparent);
            USER_CUSTOM_THEME_COLOR_ACCENT_LOG = APPLICATION_CONTEXT.getResources().getColor(R.color.transparent);
            USER_CUSTOM_THEME_COLOR_ACCENT_HIGHLIGHT = APPLICATION_CONTEXT.getResources().getColor(R.color.transparent);
            USER_CUSTOM_THEME_COLOR_ABOUT_LINK_COLOR = APPLICATION_CONTEXT.getResources().getColor(R.color.transparent);
        }
    }

    public static int getLocalCustomUserColorFromAttr(@AttrRes int attrID) {
        switch(attrID) {
            case R.attr.colorPrimary:
                return USER_CUSTOM_THEME_COLOR_PRIMARY;
            case R.attr.colorPrimaryDark:
                return USER_CUSTOM_THEME_COLOR_PRIMARY_DARK;
            case R.attr.colorAccent:
                return USER_CUSTOM_THEME_COLOR_ACCENT;
            case R.attr.colorPrimaryDarkLog:
                return USER_CUSTOM_THEME_COLOR_PRIMARY_DARK_LOG;
            case R.attr.colorAccentLog:
                return USER_CUSTOM_THEME_COLOR_ACCENT_LOG;
            case R.attr.colorAccentHighlight:
                return USER_CUSTOM_THEME_COLOR_ACCENT_HIGHLIGHT;
            case R.attr.colorAboutLinkColor:
                return USER_CUSTOM_THEME_COLOR_ABOUT_LINK_COLOR;
            default:
                Log.w(TAG, "Unable to find R.attr color specified ...");
                return USER_CUSTOM_THEME_COLOR_PRIMARY;
        }
    }

    private String themeName;
    private String themeDesc;
    private boolean saveThemeToAndroidPrefs;

    public CustomUserThemeSupport() {
        themeName = "<NONE>";
        themeDesc = "<NONE>";
        saveThemeToAndroidPrefs = true;
    }

    public String getAndroidSettingsStorageDefaultThemeName() {
        return AndroidSettingsStorage.CUSTOM_THEME_DATA_PREFERENCE;
    }

    /* TODO: Need a color picker dialog for each color attr ... */
    /* TODO: Can suggest shades of a given initial color for visual niceness? ... */

}
