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

import com.maxieds.chameleonminilivedebugger.BuildConfig;

public class ScriptingEnvironment {

    private static final String TAG = ScriptingEnvironment.class.getSimpleName();

    public static String getEnvironmentVariableByName(String envVarName) {
        switch(envVarName) {
            case "Android.userHomePath":
                break;
            case "Android.externalStoragePath":
                break;
            case "Android.downloadsPath":
            case "Android.documentsPath":
            case "Android.picturesPath":
            case "Android.dataPath":
                break;
            case "Chameleon.deviceType":
                break;
            case "Chameleon.deviceRevision":
                break;
            case "Chameleon.connectionType":
                break;
            case "Chameleon.serialNumber":
                break;
            case "Chameleon.deviceName":
                break;
            case "CMLD.versionName":
                return BuildConfig.VERSION_NAME;
            case "CMLD.versionCode":
                return String.format(BuildConfig.DEFAULT_LOCALE, "%d", BuildConfig.VERSION_CODE);
            case "CMLD.versionCodeNormalized":
                return String.format(BuildConfig.DEFAULT_LOCALE, "%d", BuildConfig.VERSION_CODE - 8080);
            case "$env0":
                break;
            case "$env1":
                break;
            case "$envKey0":
                break;
            case "$envKey1":
                break;
            default:
                break;
        }
        return ScriptingTypes.NULL;
    }

}
