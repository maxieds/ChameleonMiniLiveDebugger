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
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ChameleonScriptingException;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ExceptionType;

import java.util.List;

public class ScriptingFunctions {

    private static final String TAG = ScriptingFunctions.class.getSimpleName();

    public static ScriptVariable callFunction(String funcName, List<ScriptVariable> funcArgs) throws ChameleonScriptingException {
        switch(funcName) {
            case "Exit":
                return ScriptingAPIFunctions.Exit(funcArgs);
            case "Print":
                return ScriptingAPIFunctions.Print(funcArgs);
            case "Printf":
                return ScriptingAPIFunctions.Printf(funcArgs);
        }
        throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
    }

    public static class ScriptingAPIFunctions {

        public static ScriptVariable Exit(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

        public static ScriptVariable Print(List<ScriptVariable> argList) throws ChameleonScriptingException {
            StringBuilder consoleOutput = new StringBuilder();
            for(int argIdx = 0; argIdx < argList.size(); argIdx++) {
                ScriptVariable svar = argList.get(argIdx);
                consoleOutput.append(svar.getValueAsString());
            }
            ChameleonScripting.getRunningInstance().writeConsoleOutput(consoleOutput.toString());
            return ScriptingTypes.newInstance().set(consoleOutput.toString());
        }

        public static ScriptVariable Printf(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

    };

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
