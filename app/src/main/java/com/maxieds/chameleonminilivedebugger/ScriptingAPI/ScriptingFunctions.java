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

import android.util.Log;

import com.maxieds.chameleonminilivedebugger.BuildConfig;
import com.maxieds.chameleonminilivedebugger.ChameleonIO;
import com.maxieds.chameleonminilivedebugger.ChameleonSettings;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ChameleonScriptingException;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ExceptionType;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptingFunctions {

    private static final String TAG = ScriptingFunctions.class.getSimpleName();

    public static ScriptVariable callFunction(String funcName, List<ScriptVariable> funcArgs) throws ChameleonScriptingException {
        // The argument list is passed in reverse default from the parser:
        Collections.reverse(funcArgs);
        switch(funcName) {
            case "Exit":
                return ScriptingFunctions.ScriptingAPIFunctions.Exit(funcArgs);
            case "Assert":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "Print":
                return ScriptingFunctions.ScriptingAPIFunctions.Print(funcArgs);
            case "Printf":
                return ScriptingFunctions.ScriptingAPIFunctions.Printf(funcArgs);
            case "Sprintf":
                return ScriptingFunctions.ScriptingAPIFunctions.Sprintf(funcArgs);
            case "AsHexString":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "AsBinaryString":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "AsByteArray":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetLength":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetEnv":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "IsChameleonConnected":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "IsChameleonRevG":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "IsChameleonRevE":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetChameleonDesc":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "DownloadTagDump":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "UploadTagDump":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "DownloadLogs":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "AsWrappedAPDU":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "ExtractDataFromWrappedAPDU":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "ExtractDataFromNativeAPDU":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "SplitAPDUResponse":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "SearchAPDUStatusCodes":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "SearchAPDUInsCodes":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "SearchAPDUClaCodes":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "RandomBytes":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "RandomInt32":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetCRC16":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "AppendCRC16":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "CheckCRC16":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetCommonKeys":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetUserKeys":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetTimestamp":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "MemoryXOR":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "Max":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "Min":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "Reverse":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "PadLeft":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "PadRight":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetSubarray":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "GetConstantString":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "IntegerRange":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "Find":
                return ScriptingFunctions.ScriptingAPIFunctions.Find(funcArgs);
            case "Contains":
                return ScriptingFunctions.ScriptingAPIFunctions.Contains(funcArgs);
            case "Replace":
                return ScriptingFunctions.ScriptingAPIFunctions.Replace(funcArgs);
            case "Split":
                return ScriptingFunctions.ScriptingAPIFunctions.Split(funcArgs);
            case "Strip":
                return ScriptingFunctions.ScriptingAPIFunctions.Strip(funcArgs);
            case "Substring":
                return ScriptingFunctions.ScriptingAPIFunctions.Substring(funcArgs);
            default:
                break;
        }
        Log.w(TAG, "Script: Calling function '" + funcName + "'");
        throw new ChameleonScriptingException(ExceptionType.OperationNotSupportedException);
    }

    public static class ScriptingAPIFunctions {

        public static ScriptVariable Exit(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException, "Invalid number of parameters.");
            }
            String scriptExitMsg = String.format(Locale.getDefault(), "Script exited with CODE = %d.", argList.get(0).getValueAsInt());
            ChameleonScripting.getRunningInstance().killRunningScript(scriptExitMsg);
            return ScriptingTypes.ScriptVariable.newInstance();
        }

        public static ScriptVariable Print(List<ScriptVariable> argList) throws ChameleonScriptingException {
            printFunctionArgumentList("Print", argList);
            StringBuilder consoleOutput = new StringBuilder();
            for(int argIdx = 0; argIdx < argList.size(); argIdx++) {
                ScriptVariable svar = argList.get(argIdx);
                consoleOutput.append(svar.getValueAsString());
            }
            ChameleonScripting.getRunningInstance().writeConsoleOutput(ScriptingUtils.rawStringToSpecialCharEncoding(consoleOutput.toString()));
            return ScriptVariable.newInstance().set(consoleOutput.toString());
        }

        public static ScriptVariable Printf(List<ScriptVariable> argList) throws ChameleonScriptingException {
            printFunctionArgumentList("Printf", argList);
            ScriptVariable sprintfText = Sprintf(argList);
            Log.i(TAG, "Printf [sprintf var str value] -> \"" + sprintfText.getValueAsString() + "\"");
            String returnText = ScriptingUtils.rawStringToSpecialCharEncoding(sprintfText.getValueAsString());
            ChameleonScripting.getRunningInstance().writeConsoleOutput(returnText);
            return ScriptVariable.newInstance().set(returnText.length());
        }

        public static ScriptVariable Sprintf(List<ScriptVariable> argList) throws ChameleonScriptingException {
            printFunctionArgumentList("Sprintf", argList);
            if(argList.size() == 0) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException, "Requires a format string parameter");
            }
            String fmtMsg = argList.get(0).getValueAsString();
            int varIndex = -1;
            StringBuilder consoleOutput = new StringBuilder("");
            String[] fmtFlagMatches = fmtMsg.split("%");
            if(fmtFlagMatches.length > 0 && fmtFlagMatches.length != argList.size()) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException, "Not enough variables supplied");
            }
            for(String rawStringPart : fmtFlagMatches) {
                Log.d(TAG, "MATCHING RAW FMT PART: '" + rawStringPart + "'");
                ++varIndex;
                if(varIndex == 0 && fmtMsg.charAt(0) != '%') {
                    consoleOutput.append(rawStringPart);
                }
                else {
                    rawStringPart = "%" + rawStringPart;
                    int fmtSpecPos = 0, fmtSearchPos = 1;
                    while(fmtSearchPos < rawStringPart.length()) {
                        if(Character.isLetter(rawStringPart.charAt(fmtSearchPos))) {
                            break;
                        }
                        fmtSearchPos++;
                    }
                    if(fmtSearchPos == rawStringPart.length()) {
                        ScriptingGUIConsole.appendConsoleOutputRecordErrorWarning(
                                String.format(Locale.getDefault(), "String format error '%s' is invalid!", rawStringPart),
                                null,
                                ChameleonScripting.getRunningInstance().getExecutingLineOfCode()
                        );
                        consoleOutput.append(rawStringPart);
                        continue;
                    }
                    char fmtSpec = rawStringPart.charAt(fmtSearchPos);
                    try {
                        if(fmtSpec == 's' || fmtSpec == 'S' || fmtSpec == 'c' || !argList.get(varIndex).isIntegerType()) {
                            consoleOutput.append(String.format(Locale.getDefault(), rawStringPart, argList.get(varIndex).getValueAsString()));
                            Log.d(TAG, "MATCHING RAW FMT PART II: " + fmtSpec + " -- '" + rawStringPart + "'" + "    " + String.format(Locale.getDefault(), rawStringPart, argList.get(varIndex).getValueAsString()));
                        }
                        else {
                            consoleOutput.append(String.format(Locale.getDefault(), rawStringPart, argList.get(varIndex).getValueAsInt()));
                            Log.d(TAG, "MATCHING RAW FMT PART II: " + fmtSpec + " -- '" + rawStringPart + "'" + "    " + String.format(Locale.getDefault(), rawStringPart, argList.get(varIndex).getValueAsString()));
                        }
                    } catch(Exception strFmtEx) {
                        strFmtEx.printStackTrace();
                        ScriptingGUIConsole.appendConsoleOutputRecordErrorWarning(
                                String.format(Locale.getDefault(), "String format error '%s' is invalid!", rawStringPart),
                                null,
                                ChameleonScripting.getRunningInstance().getExecutingLineOfCode()
                        );
                        consoleOutput.append(rawStringPart);
                    }
                }
            }
            Log.i(TAG, "Sprintf -> \"" + consoleOutput.toString() + "\"");
            return ScriptVariable.newInstance().set(ScriptingUtils.rawStringToSpecialCharEncoding(consoleOutput.toString()));
        }

        public static ScriptVariable Find(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 2) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptingTypes.ScriptVariable.VariableType[][] matchingArgTypes =
                    new ScriptingTypes.ScriptVariable.VariableType[][] {
                            new ScriptingTypes.ScriptVariable.VariableType[] {
                                    ScriptVariable.VariableType.VariableTypeHexString,
                                    ScriptVariable.VariableType.VariableTypeHexString
                            }
                    };
            if(!ScriptingTypes.verifyArgumentListHasPattern(argList, matchingArgTypes)) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            String strBaseVar = argList.get(0).getValueAsString(), strNeedleVar = argList.get(1).getValueAsString();
            return new ScriptVariable(strBaseVar.indexOf(strNeedleVar));
        }

        public static ScriptVariable Contains(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 2) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptingTypes.ScriptVariable.VariableType[][] matchingArgTypes =
                    new ScriptingTypes.ScriptVariable.VariableType[][] {
                            new ScriptingTypes.ScriptVariable.VariableType[] {
                                    ScriptVariable.VariableType.VariableTypeHexString,
                                    ScriptVariable.VariableType.VariableTypeHexString
                            }
                    };
            if(!ScriptingTypes.verifyArgumentListHasPattern(argList, matchingArgTypes)) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            String strBaseVar = argList.get(0).getValueAsString(), strNeedleVar = argList.get(1).getValueAsString();
            return new ScriptVariable(strBaseVar.contains(strNeedleVar));
        }

        public static ScriptVariable Replace(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 3) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptingTypes.ScriptVariable.VariableType[][] matchingArgTypes =
                    new ScriptingTypes.ScriptVariable.VariableType[][] {
                            new ScriptingTypes.ScriptVariable.VariableType[] {
                                    ScriptVariable.VariableType.VariableTypeHexString,
                                    ScriptVariable.VariableType.VariableTypeHexString,
                                    ScriptVariable.VariableType.VariableTypeHexString
                            }
                    };
            if(!ScriptingTypes.verifyArgumentListHasPattern(argList, matchingArgTypes)) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            String strBaseVar = argList.get(0).getValueAsString();
            String strSearchVar = argList.get(1).getValueAsString();
            String strReplVar = argList.get(2).getValueAsString();
            return new ScriptVariable(strBaseVar.replaceAll(strSearchVar, strReplVar));

        }

        public static ScriptVariable Split(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 2) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptingTypes.ScriptVariable.VariableType[][] matchingArgTypes =
                    new ScriptingTypes.ScriptVariable.VariableType[][] {
                            new ScriptingTypes.ScriptVariable.VariableType[] {
                                    ScriptVariable.VariableType.VariableTypeHexString,
                                    ScriptVariable.VariableType.VariableTypeHexString
                            }
                    };
            if(!ScriptingTypes.verifyArgumentListHasPattern(argList, matchingArgTypes)) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            String strBaseVar = argList.get(0).getValueAsString(), strDelimVar = argList.get(1).getValueAsString();
            String[] splitItemsStrArr = strBaseVar.split(strBaseVar);
            ScriptVariable[] splitItemsScriptVarArr = new ScriptVariable[splitItemsStrArr.length];
            for(int sidx = 0; sidx < splitItemsStrArr.length; sidx++) {
                splitItemsScriptVarArr[sidx] = new ScriptVariable(splitItemsStrArr[sidx]);
            }
            ScriptVariable splitItemsArrVar = new ScriptingTypes.ScriptVariable();
            splitItemsArrVar.setArrayListItems(splitItemsScriptVarArr);
            return splitItemsArrVar;
        }

        public static ScriptVariable Strip(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptVariable strVar = argList.get(0);
            if(!strVar.isStringType()) {
                throw new ChameleonScriptingException(ExceptionType.IllegalArgumentException);
            }
            return new ScriptVariable(strVar.getValueAsString().replaceAll("\\s+", ""));
        }

        public static ScriptVariable Substring(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 2 && argList.size() != 3) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            else if(argList.size() == 2) {
                ScriptVariable strVar = argList.get(0), intVar = argList.get(1);
                if(!strVar.isStringType() || !intVar.isIntegerType()) {
                    throw new ChameleonScriptingException(ExceptionType.IllegalArgumentException);
                }
                String strValue = strVar.getValueAsString();
                int intIndexValue = intVar.getValueAsInt();
                if(intIndexValue < 0 || intIndexValue >= strValue.length()) {
                    throw new ChameleonScriptingException(ExceptionType.IndexOutOfBoundsException);
                }
                return new ScriptVariable(strValue.substring(intIndexValue));
            }
            else {
                ScriptVariable strVar = argList.get(0), intVar1 = argList.get(1), intVar2 = argList.get(2);
                if(!strVar.isStringType() || !intVar1.isIntegerType() || !intVar2.isIntegerType()) {
                    throw new ChameleonScriptingException(ExceptionType.IllegalArgumentException);
                }
                String strValue = strVar.getValueAsString();
                int intIndexStartValue = intVar1.getValueAsInt(), intIndexLengthValue = intVar2.getValueAsInt();
                if(intIndexStartValue < 0 || intIndexStartValue >= strValue.length() ||
                        intIndexLengthValue <= 0 || intIndexLengthValue > strValue.length()) {
                    throw new ChameleonScriptingException(ExceptionType.IndexOutOfBoundsException);
                }
                return new ScriptVariable(strValue.substring(intIndexStartValue, intIndexLengthValue));
            }
        }

    };

    public static String getEnvironmentVariableByName(String envVarName) {
        switch(envVarName) {
            case "Chameleon.deviceType":
                return ChameleonIO.CHAMELEON_MINI_BOARD_TYPE_DESC;
            case "Chameleon.deviceRevision":
                return ChameleonIO.CHAMELEON_MINI_BOARD_TYPE == ChameleonIO.CHAMELEON_TYPE_REVE ? "E" : "G";
            case "Chameleon.connectionType":
                if(ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX == ChameleonSettings.USBIO_IFACE_INDEX) {
                    return "USB";
                }
                else if(ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX == ChameleonSettings.BTIO_IFACE_INDEX) {
                    return "BT";
                }
                else {
                    return "NONE";
                }
            case "Chameleon.serialNumber":
                return ChameleonSettings.chameleonDeviceSerialNumber;
            case "Chameleon.deviceName":
                return ChameleonSettings.chameleonDeviceNickname;
            case "CMLD.versionName":
                return BuildConfig.VERSION_NAME;
            case "CMLD.versionCode":
                return String.format(BuildConfig.DEFAULT_LOCALE, "%d", BuildConfig.VERSION_CODE);
            case "CMLD.versionCodeNormalized":
                return String.format(BuildConfig.DEFAULT_LOCALE, "%d", BuildConfig.VERSION_CODE - 8080);
            case "$env0":
                return ScriptingConfig.ENV0_VALUE;
            case "$env1":
                return ScriptingConfig.ENV1_VALUE;
            case "$envKey0":
                return ScriptingConfig.ENVKEY0_VALUE;
            case "$envKey1":
                return ScriptingConfig.ENVKEY1_VALUE;
            default:
                break;
        }
        throw new ChameleonScriptingException(ExceptionType.KeyNotFoundException);
    }

    private static void printFunctionArgumentList(String funcName, List<ScriptVariable> svList) {
        Log.i(TAG, String.format(Locale.getDefault(), "FUNCTION %s(...) called with ##% 2d ARGS", funcName, svList.size()));
        int varIndex = 0;
        for(ScriptVariable svar : svList) {
            Log.i(TAG, String.format(Locale.getDefault(), "    &&&& [VARIDX=% 2d] '%s' (quoted)", varIndex, svList.get(varIndex++).getValueAsString()));
        }
    }

}
