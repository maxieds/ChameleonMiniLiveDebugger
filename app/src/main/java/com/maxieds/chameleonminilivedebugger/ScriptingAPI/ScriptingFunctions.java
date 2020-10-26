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
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExecptions.ChameleonScriptingException;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExecptions.ExceptionType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            case "Sprintf":
                return ScriptingAPIFunctions.Sprintf(funcArgs);
                /* ... */
            case "Find":
                return ScriptingAPIFunctions.Find(funcArgs);
            case "Contains":
                return ScriptingAPIFunctions.Contains(funcArgs);
            case "Replace":
                return ScriptingAPIFunctions.Replace(funcArgs);
            case "Split":
                return ScriptingAPIFunctions.Split(funcArgs);
            case "Strip":
                return ScriptingAPIFunctions.Strip(funcArgs);
            case "Substring":
                return ScriptingAPIFunctions.Substring(funcArgs);
            /* ... */
            default:
                break;
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
            return ScriptVariable.newInstance().set(consoleOutput.toString());
        }

        public static ScriptVariable Printf(List<ScriptVariable> argList) throws ChameleonScriptingException {
            ScriptVariable sprintfText = Sprintf(argList);
            ChameleonScripting.getRunningInstance().writeConsoleOutput(sprintfText.getValueAsString());
            return ScriptVariable.newInstance().set(sprintfText.getValueAsString().length());
        }

        public static ScriptVariable Sprintf(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() == 0) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException, "Requires a format string parameter");
            }
            String fmtMsg = argList.get(0).getValueAsString();
            int varIndex = 1, strBaseIdx = 0;
            StringBuilder consoleOutput = new StringBuilder();
            Pattern fmtFlagPattern = Pattern.compile("%[^diuoxXcs]*[diuoxXcs]");
            Matcher fmtMatcher = fmtFlagPattern.matcher(fmtMsg);
            while(fmtMatcher.find()) {
                String fmtFlag = fmtMatcher.group();
                String fmtSpec = new String(new char[] { fmtFlag.charAt(fmtFlag.length() - 1) });
                String fmtFragment = fmtMsg.substring(strBaseIdx, fmtMatcher.end() + 1);
                if(fmtSpec.equalsIgnoreCase("s") || fmtSpec.equalsIgnoreCase("c")) {
                    consoleOutput.append(String.format(BuildConfig.DEFAULT_LOCALE, fmtFragment, argList.get(varIndex).getValueAsString()));
                }
                else { // integer types:
                    consoleOutput.append(String.format(BuildConfig.DEFAULT_LOCALE, fmtFragment, argList.get(varIndex).getValueAsInt()));
                }
                strBaseIdx += fmtMatcher.end() + 1;
                if(++varIndex >= argList.size()) {
                    break;
                }
            }
            if(fmtMatcher.find()) {
                throw new ChameleonScriptingException(ExceptionType.FormatErrorException);
            }
            return ScriptVariable.newInstance().set(consoleOutput.toString());
        }

        public static ScriptVariable Find(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

        public static ScriptVariable Contains(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

        public static ScriptVariable Replace(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

        public static ScriptVariable Split(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

        public static ScriptVariable Strip(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

        public static ScriptVariable Substring(List<ScriptVariable> argList) throws ChameleonScriptingException {
            throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
        }

    };

    public static String getEnvironmentVariableByName(String envVarName) {
        switch(envVarName) {
            case "Android.externalStoragePath":
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
