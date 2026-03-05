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

import com.maxieds.chameleonminilivedebugger.AndroidLogger;
import com.maxieds.chameleonminilivedebugger.BuildConfig;
import com.maxieds.chameleonminilivedebugger.ChameleonIO;
import com.maxieds.chameleonminilivedebugger.ChameleonSettings;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ChameleonScriptingException;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingExceptions.ExceptionType;
import com.maxieds.chameleonminilivedebugger.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScriptingFunctions {

    private static final String TAG = ScriptingFunctions.class.getSimpleName();

    public static ScriptVariable callFunction(String funcName, List<ScriptVariable> funcArgs) throws ChameleonScriptingException {
        // The argument list is passed in reverse default from the parser:
        Collections.reverse(funcArgs);
        AndroidLogger.w(TAG, "Script: Calling function '" + funcName + "'");
        printFunctionArgumentList(funcName, funcArgs);
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
                return ScriptingFunctions.ScriptingAPIFunctions.AsHexString(funcArgs);
            case "AsBinaryString":
                return ScriptingFunctions.ScriptingAPIFunctions.AsBinaryString(funcArgs);
            case "AsByteArray":
                return ScriptingFunctions.ScriptingAPIFunctions.AsByteArray(funcArgs);
            case "GetLength":
                return ScriptingFunctions.ScriptingAPIFunctions.GetLength(funcArgs);
            case "GetEnv":
                return ScriptingFunctions.ScriptingAPIFunctions.GetEnv(funcArgs);
            case "IsChameleonConnected":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "IsChameleonRevG":
                return ScriptingFunctions.ScriptingAPIFunctions.IsChameleonRevG(funcArgs);
            case "IsChameleonRevE":
                return ScriptingFunctions.ScriptingAPIFunctions.IsChameleonRevE(funcArgs);
            case "GetChameleonDesc":
                return ScriptingFunctions.ScriptingAPIFunctions.GetChameleonDesc(funcArgs);
            case "DownloadTagDump":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "UploadTagDump":
                throw new ChameleonScriptingException(ExceptionType.NotImplementedException);
            case "DownloadLogs":
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
                return ScriptingFunctions.ScriptingAPIFunctions.RandomBytes(funcArgs);
            case "RandomInt32":
                return ScriptingFunctions.ScriptingAPIFunctions.RandomInt32(funcArgs);
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
                return ScriptingFunctions.ScriptingAPIFunctions.GetTimestamp(funcArgs);
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
                return ScriptingFunctions.ScriptingAPIFunctions.GetSubarray(funcArgs);
            case "ArrayToString":
                return ScriptingFunctions.ScriptingAPIFunctions.ArrayToString(funcArgs);
            case "GetConstantString":
                return ScriptingFunctions.ScriptingAPIFunctions.GetConstantString(funcArgs);
            case "IntegerRange":
                return ScriptingFunctions.ScriptingAPIFunctions.IntegerRange(funcArgs);
            case "StringFind":
                return ScriptingFunctions.ScriptingAPIFunctions.Find(funcArgs);
            case "StringContains":
                return ScriptingFunctions.ScriptingAPIFunctions.Contains(funcArgs);
            case "StringReplace":
                return ScriptingFunctions.ScriptingAPIFunctions.Replace(funcArgs);
            case "Strcat":
                return ScriptingFunctions.ScriptingAPIFunctions.Strcat(funcArgs);
            case "StringSplit":
                return ScriptingFunctions.ScriptingAPIFunctions.Split(funcArgs);
            case "StringStrip":
                return ScriptingFunctions.ScriptingAPIFunctions.Strip(funcArgs);
            case "StringSubstring":
                return ScriptingFunctions.ScriptingAPIFunctions.Substring(funcArgs);
            default:
                break;
        }
        AndroidLogger.w(TAG, "Script: Calling function '" + funcName + "'");
        throw new ChameleonScriptingException(ExceptionType.OperationNotSupportedException);
    }

    public static class ScriptingAPIFunctions {

        public static ScriptVariable Exit(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if(argList.size() != 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException, "Invalid number of parameters.");
            }
            String scriptExitMsg = String.format(BuildConfig.DEFAULT_LOCALE, "Script exited with CODE = %d.", argList.get(0).getValueAsInt());
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
            AndroidLogger.i(TAG, "Printf [sprintf var str value] -> \"" + sprintfText.getValueAsString() + "\"");
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
                AndroidLogger.d(TAG, "MATCHING RAW FMT PART: '" + rawStringPart + "'");
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
                                String.format(BuildConfig.DEFAULT_LOCALE, "String format error '%s' is invalid!", rawStringPart),
                                null,
                                ChameleonScripting.getRunningInstance().getExecutingLineOfCode()
                        );
                        consoleOutput.append(rawStringPart);
                        continue;
                    }
                    char fmtSpec = rawStringPart.charAt(fmtSearchPos);
                    try {
                        if(fmtSpec == 's' || fmtSpec == 'S' || fmtSpec == 'c') {
                            consoleOutput.append(String.format(BuildConfig.DEFAULT_LOCALE, rawStringPart, argList.get(varIndex).getValueAsString()));
                        }
                        else if(!argList.get(varIndex).isIntegerType()) {
                            rawStringPart = "%s" + rawStringPart.substring(fmtSearchPos + 1);
                            consoleOutput.append(String.format(BuildConfig.DEFAULT_LOCALE, rawStringPart, argList.get(varIndex).getValueAsString()));
                        }
                        else {
                            consoleOutput.append(String.format(BuildConfig.DEFAULT_LOCALE, rawStringPart, argList.get(varIndex).getValueAsInt()));
                        }
                    } catch(Exception strFmtEx) {
                        AndroidLogger.printStackTrace(strFmtEx);
                        ScriptingGUIConsole.appendConsoleOutputRecordErrorWarning(
                                String.format(BuildConfig.DEFAULT_LOCALE, "String format error '%s' is invalid!", rawStringPart),
                                null,
                                ChameleonScripting.getRunningInstance().getExecutingLineOfCode()
                        );
                        consoleOutput.append(rawStringPart);
                    }
                }
            }
            AndroidLogger.i(TAG, "Sprintf -> \"" + consoleOutput.toString() + "\"");
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

        public static ScriptVariable Strcat(List<ScriptVariable> argList) throws ChameleonScriptingException {
            StringBuilder catStr = new StringBuilder();
            for (int ai = 0; ai < argList.size(); ai++) {
                String curArgStr = argList.get(ai).getValueAsString();
                catStr.append(curArgStr);
            }
            return new ScriptVariable(catStr.toString());
        }

        public static ScriptVariable GetConstantString(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() != 2) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            int numCopies = argList.get(1).getValueAsInt();
            String builderStr = argList.get(0).getValueAsString();
            StringBuilder constStr = new StringBuilder();
            for (int ci = 0; ci < numCopies; ci++) {
                constStr.append(builderStr);
            }
            return new ScriptVariable(constStr.toString());
        }

        public static ScriptVariable GetSubarray(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() != 3) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            } else if (argList.get(0).getType() != ScriptVariable.VariableType.VariableTypeArrayMap) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            int startIdx = argList.get(1).getValueAsInt();
            int endLength = argList.get(2).getValueAsInt();
            return argList.get(0).getSubArray(startIdx, endLength);
        }

        public static ScriptVariable ArrayToString(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() != 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            } else if (!argList.get(0).isArrayType()) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            String arrReprStr = "{ ";
            ScriptVariable arrVar = argList.get(0);
            int arrLength = arrVar.length();
            AndroidLogger.i(TAG, "ArrayToString -> Array Length = " + arrLength);
            for (int ai = 0; ai < arrLength; ai++) {
                String nextSpace = (ai + 1 == arrLength) ? " " : ", ";
                arrReprStr += String.format(BuildConfig.DEFAULT_LOCALE, "%s%s", arrVar.getValueAt(ai).getValueAsString(), nextSpace);
            }
            arrReprStr += "}";
            AndroidLogger.i(TAG, "ArrayToString -> \"" + arrReprStr + "\"");
            return new ScriptVariable(arrReprStr);
        }

        public static ScriptVariable AsHexString(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() == 0) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            } else if (argList.size() >= 2) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptVariable inputVar = argList.get(0);
            byte[] byteArr = {};
            int byteMask = 0x000000ff;
            String binStr = "";
            switch (inputVar.getType()) {
                case VariableTypeBoolean:
                    if (inputVar.getValueAsBoolean()) {
                        return new ScriptVariable("01");
                    } else {
                        return new ScriptVariable("00");
                    }
                case VariableTypeInteger:
                    int ivar = inputVar.getValueAsInt();
                    byteArr = new byte[4];
                    byteArr[0] = (byte) ((ivar >> 24) & byteMask);
                    byteArr[1] = (byte) ((ivar >> 16) & byteMask);
                    byteArr[2] = (byte) ((ivar >> 8) & byteMask);
                    byteArr[3] = (byte) (ivar & byteMask);
                    binStr = Utils.bytes2Hex(byteArr);
                    return new ScriptVariable(binStr);
                case VariableTypeBytes:
                    byteArr = inputVar.getValueAsBytes();
                    binStr = Utils.bytes2Hex(byteArr);
                    return new ScriptVariable(binStr);
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                case VariableTypeStorageFilePath:
                case VariableTypeRawFileFilePath:
                    byteArr = inputVar.getValueAsString().getBytes();
                    binStr = "";
                    for (int bi = 0; bi < byteArr.length; bi++) {
                        binStr += Utils.ToBinaryString(byteArr[bi]);
                    }
                    return new ScriptVariable(binStr);
                default:
                    throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }

        }

        public static ScriptVariable AsBinaryString(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() == 0) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            } else if (argList.size() >= 2) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptVariable inputVar = argList.get(0);
            byte[] byteArr = {};
            String binStr = "";
            switch (inputVar.getType()) {
                case VariableTypeBoolean:
                    if (inputVar.getValueAsBoolean()) {
                        return new ScriptVariable("1");
                    } else {
                        return new ScriptVariable("0");
                    }
                case VariableTypeInteger:
                    binStr = Utils.ToBinaryString(inputVar.getValueAsInt());
                    return new ScriptVariable(binStr);
                case VariableTypeBytes:
                    byteArr = inputVar.getValueAsBytes();
                    binStr = "";
                    for (int bi = 0; bi < byteArr.length; bi++) {
                        binStr += Utils.ToBinaryString(byteArr[bi]);
                    }
                    return new ScriptVariable(binStr);
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                case VariableTypeStorageFilePath:
                case VariableTypeRawFileFilePath:
                    byteArr = inputVar.getValueAsString().getBytes();
                    binStr = "";
                    for (int bi = 0; bi < byteArr.length; bi++) {
                        binStr += Utils.ToBinaryString(byteArr[bi]);
                    }
                    return new ScriptVariable(binStr);
                default:
                    throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }

        }

        public static ScriptVariable AsByteArray(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() == 0) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            } else if (argList.size() >= 2) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptVariable inputVar = argList.get(0);
            byte[] byteArr = {};
            switch (inputVar.getType()) {
                case VariableTypeInteger:
                    int ival = inputVar.getValueAsInt();
                    byteArr = new byte[4];
                    int byteMask = 0x000000ff;
                    byteArr[0] = (byte) ((ival >> 24) & byteMask);
                    byteArr[1] = (byte) ((ival >> 16) & byteMask);
                    byteArr[2] = (byte) ((ival >> 8) & byteMask);
                    byteArr[3] = (byte) (ival & byteMask);
                    return new ScriptVariable(byteArr);
                case VariableTypeBytes:
                    return new ScriptVariable(inputVar.getValueAsBytes());
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                    String hexStr = inputVar.getValueAsString();
                    byteArr = hexStr.getBytes();
                    return new ScriptVariable(byteArr);
                default:
                    throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
        }

        public static ScriptVariable GetLength(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() != 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ScriptVariable inputVar = argList.get(0);
            int len = 0;
            switch (inputVar.getType()) {
                case VariableTypeBoolean:
                case VariableTypeInteger:
                    return new ScriptVariable(1);
                case VariableTypeBytes:
                    len = inputVar.getValueAsBytes().length;
                    return new ScriptVariable(len);
                case VariableTypeArrayMap:
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                case VariableTypeStorageFilePath:
                case VariableTypeRawFileFilePath:
                    len = inputVar.getValueAsString().length();
                    return new ScriptVariable(len);
                default:
                    throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
        }

        public static ScriptVariable IntegerRange(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() != 2 && argList.size() != 3) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            int startInt = argList.get(0).getValueAsInt();
            int endInt = argList.get(1).getValueAsInt();
            int delta = 1;
            if (argList.size() == 3) {
                delta = argList.get(2).getValueAsInt();
            }
            if (startInt > endInt || delta < 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            ArrayList<ScriptVariable> rarr = new ArrayList<ScriptVariable>();
            for (int i = startInt; i <= endInt; i += delta) {
                rarr.add(new ScriptVariable(i));
            }
            return new ScriptVariable(rarr);
        }

        public static ScriptVariable GetTimestamp(List<ScriptVariable> argList) throws ChameleonScriptingException {
            String timeStamp = Utils.getTimestamp();
            return new ScriptVariable(timeStamp);
        }

        public static ScriptVariable RandomBytes(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() != 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            int numBytes = argList.get(0).getValueAsInt();
            byte[] rdmByteArr = Utils.getRandomBytes(numBytes);
            ArrayList<ScriptVariable> alst = new ArrayList<ScriptVariable>();
            for (int bi = 0; bi < numBytes; bi++) {
                alst.add(new ScriptVariable(rdmByteArr[bi]));
            }
            return new ScriptVariable(alst);
        }

        public static ScriptVariable RandomInt32(List<ScriptVariable> argList) throws ChameleonScriptingException {
             byte[] rdmBytes = Utils.getRandomBytes(4);
             int rdmInt = 0;
             for (int bi = 0; bi < 4; bi++) {
                 rdmInt |= (rdmBytes[bi] << 8 * (3 - bi));
             }
             return new ScriptVariable(rdmInt);
        }

        public static ScriptVariable IsChameleonRevG(List<ScriptVariable> argList) throws ChameleonScriptingException {
            boolean isRevG = !ChameleonIO.REVE_BOARD;
            return new ScriptVariable(isRevG);
        }

        public static ScriptVariable IsChameleonRevE(List<ScriptVariable> argList) throws ChameleonScriptingException {
            boolean isRevE = ChameleonIO.REVE_BOARD;
            return new ScriptVariable(isRevE);
        }

        public static ScriptVariable GetChameleonDesc(List<ScriptVariable> argList) throws ChameleonScriptingException {
            int chamBoardType = ChameleonIO.CHAMELEON_MINI_BOARD_TYPE;
            String chamDesc = ChameleonIO.getDeviceDescription(chamBoardType);
            return new ScriptVariable(chamDesc);
        }

        public static ScriptVariable GetEnv(List<ScriptVariable> argList) throws ChameleonScriptingException {
            if (argList.size() != 1) {
                throw new ChameleonScriptingException(ExceptionType.InvalidArgumentException);
            }
            String envVarName = argList.get(0).getValueAsString();
            String envVarValue = getEnvironmentVariableByName(envVarName);
            return new ScriptVariable(envVarValue);
        }

    };

    public static String getEnvironmentVariableByName(String envVarName) throws ChameleonScriptingException {
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
        AndroidLogger.i(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "FUNCTION %s(...) called with ##% 2d ARGS", funcName, svList.size()));
        int varIndex = 0;
        for(ScriptVariable svar : svList) {
            AndroidLogger.i(TAG, String.format(BuildConfig.DEFAULT_LOCALE, "    &&&& [VARIDX=% 2d] '%s' (quoted)", varIndex, svList.get(varIndex++).getValueAsString()));
        }
    }

}
