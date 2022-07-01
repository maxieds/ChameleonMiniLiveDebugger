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

import com.maxieds.chameleonminilivedebugger.AndroidLog;
import com.maxieds.chameleonminilivedebugger.BuildConfig;
import com.maxieds.chameleonminilivedebugger.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScriptingTypes {

    private static final String TAG = ScriptingTypes.class.getSimpleName();

    public static final String NULL = "<NULL>";

    /*
     * Definitions for storage of variable types:
     */
    public static class ScriptVariable {

        public enum VariableType {
            VariableTypeNone,
            VariableTypeInteger,
            VariableTypeBoolean,
            VariableTypeBytes,
            VariableTypeHexString,
            VariableTypeAsciiString,
            VariableTypeRawFileFilePath,
            VariableTypeStorageFilePath,
            VariableTypeArrayMap
        }

        private VariableType varType;
        private String  varName;
        private boolean varIsInit;
        private int     varValueAsInt;
        private boolean varValueAsBoolean;
        private byte[]  varValueAsByteArray;
        private String  varValueAsString;

        private ArrayList<ScriptVariable> arrayList;
        private HashMap<String, ScriptVariable> hashMap;

        private void setLocalVariableDefaults() {
            varName = "";
            varType = VariableType.VariableTypeArrayMap;
            varIsInit = false;
            varValueAsInt = 0;
            varValueAsBoolean = false;
            varValueAsByteArray = new byte[0];
            varValueAsString = "";
            arrayList = new ArrayList<ScriptVariable>();
            hashMap = new HashMap<String, ScriptVariable>();
        }
        public ScriptVariable() {
            setLocalVariableDefaults();
        }

        public ScriptVariable(int intLiteral) {
            setLocalVariableDefaults();
            set(intLiteral);
        }

        public ScriptVariable(boolean boolLiteral) {
            setLocalVariableDefaults();
            set(boolLiteral);
        }

        public ScriptVariable(byte[] byteArrayLiteral) {
            setLocalVariableDefaults();
            set(byteArrayLiteral);
        }

        public ScriptVariable(String strLiteral) {
            setLocalVariableDefaults();
            set(strLiteral);
        }

        public ScriptVariable(List<ScriptVariable> listItems) {
            setLocalVariableDefaults();
            setArrayListItems(listItems);
        }

        public ScriptVariable setName(String nextVarName) {
            varName = nextVarName;
            return this;
        }

        public String getName() {
            return varName;
        }

        public boolean isInit() {
            return varIsInit;
        }

        public ScriptVariable set(int nextValue) {
            varValueAsInt = nextValue;
            varType = VariableType.VariableTypeInteger;
            varIsInit = true;
            return this;
        }

        public int getValueAsInt() throws ScriptingExceptions.ChameleonScriptingException {
            if(varType == VariableType.VariableTypeInteger) {
                return varValueAsInt;
            }
            else if(varType == VariableType.VariableTypeBoolean) {
                return varValueAsBoolean ? 1 : 0;
            }
            else if((varType == VariableType.VariableTypeBytes) && (varValueAsByteArray.length <= 8)) {
                int varValue = 0, bytePosShiftLeftMost = 2 * varValueAsByteArray.length;
                for(int bytePos = 1; bytePos <= varValueAsByteArray.length; bytePos++) {
                    varValue |= varValueAsByteArray[bytePos] << (bytePosShiftLeftMost - 2 * bytePos);
                }
                return varValue;
            }
            else if(isStringType()) {
                return Integer.decode(varValueAsString).intValue();
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.InvalidTypeException);
        }

        public short getValueAsShort() throws ScriptingExceptions.ChameleonScriptingException {
            int int32Value = getValueAsInt();
            return (short) (int32Value & 0x0000ffff);
        }

        public byte getValueAsByte() throws ScriptingExceptions.ChameleonScriptingException {
            int int32Value = getValueAsInt();
            return (byte) (int32Value & 0x000000ff);
        }

        public ScriptVariable set(boolean nextValue) {
            varValueAsBoolean = nextValue;
            varType = VariableType.VariableTypeBoolean;
            varIsInit = true;
            return this;
        }

        public boolean setAsBooleanLiteral(String nextValue) {
            try {
                if (nextValue.equalsIgnoreCase("TRUE") || !nextValue.equals("0")) {
                    varValueAsBoolean = true;
                    varType = VariableType.VariableTypeBoolean;
                    varIsInit = true;
                    return true;
                } else if (nextValue.equalsIgnoreCase("FALSE") || Integer.parseInt(nextValue, 16) != 0) {
                    varValueAsBoolean = false;
                    varType = VariableType.VariableTypeBoolean;
                    varIsInit = true;
                    return true;
                }
            } catch(NumberFormatException nfe) {
                AndroidLog.printStackTrace(nfe);
                return false;
            }
            return false;
        }

        public boolean getValueAsBoolean() throws ScriptingExceptions.ChameleonScriptingException {
            if(varType == VariableType.VariableTypeBoolean) {
                return varValueAsBoolean;
            }
            else if(varType == VariableType.VariableTypeInteger) {
                return varValueAsInt != 0;
            }
            else if(isBytesType()) {
                return getValueAsBytes().length > 0;
            }
            else {
                return !getValueAsString().equals("");
            }
        }

        public ScriptVariable set(byte[] nextValue) {
            varValueAsByteArray = nextValue;
            varType = VariableType.VariableTypeBytes;
            return this;
        }

        public byte[] getValueAsBytes() throws ScriptingExceptions.ChameleonScriptingException {
            if(isBytesType()) {
                return varValueAsByteArray;
            }
            else if(isIntegerType()) {
                int varIntValue = getValueAsInt();
                byte[] intBytes = new byte[] {
                        (byte) ((varIntValue & 0xff000000) >> 24),
                        (byte) ((varIntValue & 0x00ff0000) >> 16),
                        (byte) ((varIntValue & 0x0000ff00) >> 8),
                        (byte) (varIntValue & 0x000000ff)
                };
                int lastByteArrayPos = 1;
                for(int bytePos = 1; bytePos < intBytes.length; bytePos++) {
                    if(intBytes[bytePos - 1] != 0x00) {
                        lastByteArrayPos = bytePos;
                        break;
                    }
                }
                byte[] returnBytes = new byte[lastByteArrayPos];
                System.arraycopy(intBytes, 0, returnBytes, 0, lastByteArrayPos);
                return returnBytes;
            }
            else if(isStringType()) {
                return varValueAsString.getBytes();
            }
            else {
                throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.InvalidTypeException);
            }
        }

        public ScriptVariable set(String nextValue) {
            varValueAsString = nextValue;
            varType = VariableType.VariableTypeAsciiString;
            varIsInit = true;
            return this;
        }

        public String getValueAsString() throws ScriptingExceptions.ChameleonScriptingException {
            switch(varType) {
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                case VariableTypeStorageFilePath:
                    return varValueAsString;
                case VariableTypeRawFileFilePath:
                    return ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT.getInstance().getResources().getResourceName(varValueAsInt);
                case VariableTypeBytes:
                    return Utils.bytes2Hex(varValueAsByteArray);
                case VariableTypeBoolean:
                    return varValueAsBoolean ? "true" : "false";
                case VariableTypeInteger:
                    return String.format(Locale.getDefault(), "%d", varValueAsInt);
                case VariableTypeArrayMap:
                    String[] arrayListStrDescList = new String[arrayList.size()];
                    for(int arrListIdx = 0; arrListIdx < arrayList.size(); arrListIdx++) {
                        arrayListStrDescList[arrListIdx] = arrayList.get(arrListIdx).getValueAsString();
                    }
                    String[] hashMapStrDescList = new String[hashMap.size()];
                    int hmCount = 0;
                    for(String hashKey : hashMap.keySet()) {
                        hashMapStrDescList[hmCount] = hashMap.get(hashKey).getValueAsString();
                        ++hmCount;
                    }
                    String arrValueDesc = String.format(Locale.getDefault(), "[ ");
                    if(arrayListStrDescList.length > 0) {
                        arrValueDesc += String.join(", ", arrayListStrDescList);
                        if(hashMapStrDescList.length > 0) {
                            arrValueDesc += " ; ";
                        }
                    }
                    if(hashMapStrDescList.length > 0) {
                        arrValueDesc += String.join(", ", hashMapStrDescList);
                    }
                    arrValueDesc += " ]";
                    return arrValueDesc;
                default:
                    throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.InvalidTypeException);
            }
        }

        public boolean isIntegerType() throws ScriptingExceptions.ChameleonScriptingException {
            switch(varType) {
                case VariableTypeInteger:
                case VariableTypeBoolean:
                case VariableTypeRawFileFilePath:
                    return true;
                case VariableTypeHexString:
                    if(varValueAsString.length() <= 8) {
                        return true;
                    }
                case VariableTypeBytes:
                    if(getValueAsBytes().length == 1) {
                        return true;
                    }
                default:
                    return false;
            }
        }

        public boolean isBooleanType() {
            switch(varType) {
                case VariableTypeBoolean:
                case VariableTypeInteger:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isBytesType() {
            return varType == VariableType.VariableTypeBytes;
        }

        public boolean isStringType() {
            switch(varType) {
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                case VariableTypeStorageFilePath:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isFilePathType() {
            switch(varType) {
                case VariableTypeRawFileFilePath:
                case VariableTypeStorageFilePath:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isArrayType() {
            switch(varType) {
                case VariableTypeBytes:
                case VariableTypeArrayMap:
                    return true;
                default:
                    return false;
            }
        }

        public VariableType getType() throws ScriptingExceptions.ChameleonScriptingException {
            if(isIntegerType()) {
                return VariableType.VariableTypeInteger;
            }
            else if(isBooleanType()) {
                return VariableType.VariableTypeBoolean;
            }
            else if(isBytesType()) {
                return VariableType.VariableTypeBytes;
            }
            else if(isStringType()) {
                return VariableType.VariableTypeAsciiString;
            }
            else if(isArrayType()) {
                return VariableType.VariableTypeArrayMap;
            }
            return VariableType.VariableTypeNone;
        }

        public static ScriptVariable newInstance() {
            return new ScriptVariable();
        }

        public static ScriptVariable parseHexString(String literalText) throws ScriptingExceptions.ChameleonScriptingException {
            ScriptVariable nextVar = new ScriptVariable();
            if(!Utils.stringIsHexadecimal(literalText)) {
                throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.FormatErrorException);
            }
            nextVar.set(literalText);
            return nextVar;
        }

        public static ScriptVariable parseRawString(String literalText) throws ScriptingExceptions.ChameleonScriptingException {
            literalText.replace("\\", "\\\\");
            ScriptVariable nextVar = new ScriptVariable();
            nextVar.set(literalText);
            return nextVar;
        }

        public static ScriptVariable parseInt(String literalText) throws ScriptingExceptions.ChameleonScriptingException {
            ScriptVariable nextVar = new ScriptVariable();
            try {
                int intVal = Integer.parseInt(literalText);
                nextVar.set(intVal);
                return nextVar;
            } catch(NumberFormatException nfe) {
                throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NumberFormatException, nfe.getMessage());
            }
        }

        public static ScriptVariable parseBoolean(String literalText) throws ScriptingExceptions.ChameleonScriptingException {
            ScriptVariable nextVar = new ScriptVariable();
            try {
                boolean logicalValue = Boolean.parseBoolean(literalText);
                nextVar.set(logicalValue);
                return nextVar;
            } catch(Exception ex) {
                throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.FormatErrorException, ex.getMessage());
            }
        }

        public static ScriptVariable parseBytes(String literalText) {
            ScriptVariable nextVar = new ScriptVariable();
            nextVar.set(Utils.hexString2Bytes(literalText));
            return nextVar;
        }

        public boolean isArray() {
            return varType == VariableType.VariableTypeArrayMap;
        }

        public int length() throws ScriptingExceptions.ChameleonScriptingException {
            if(isStringType()) {
                return getValueAsString().length();
            }
            else if(varType != VariableType.VariableTypeArrayMap) {
                return arrayList.size();
            }
            return 0;
        }

        public ScriptVariable getValueAt(int index) throws ScriptingExceptions.ChameleonScriptingException {
            if(index == 0 && getType() != VariableType.VariableTypeArrayMap) {
                return this;
            }
            if(getType() == VariableType.VariableTypeArrayMap) {
                if (index < 0 || index >= arrayList.size()) {
                    throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IndexOutOfBoundsException);
                }
                return arrayList.get(index);
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IllegalOperationException);
        }

        public ScriptVariable getValueAt(String hashIndex) throws ScriptingExceptions.ChameleonScriptingException {
            if(getType() == VariableType.VariableTypeArrayMap) {
                if (hashIndex == null || hashMap.get(hashIndex) == null) {
                    return newInstance().set("<null-data>");
                    //throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IndexOutOfBoundsException);
                }
                return hashMap.get(hashIndex);
            }
            return set("<no-data>");
        }

        public void setValueAt(int index, ScriptVariable varObj) throws ScriptingExceptions.ChameleonScriptingException {
            if(getType() == VariableType.VariableTypeArrayMap) {
                if (index < 0 || index >= arrayList.size()) {
                    throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IndexOutOfBoundsException);
                }
                arrayList.set(index, varObj);
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IllegalOperationException);
        }

        public void setValueAt(String hashIndex, ScriptVariable varObj) throws ScriptingExceptions.ChameleonScriptingException {
            if(getType() == VariableType.VariableTypeArrayMap) {
                if (hashIndex == null) {
                    throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IllegalArgumentException);
                }
                hashMap.put(hashIndex, varObj);
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IllegalOperationException);
        }

        public ScriptVariable getSubArray(int startIdx) throws ScriptingExceptions.ChameleonScriptingException {
            if(getType() == VariableType.VariableTypeArrayMap) {
                if (startIdx < 0 || startIdx >= arrayList.size()) {
                    throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IndexOutOfBoundsException);
                }
                ScriptVariable svSubArr = new ScriptVariable();
                svSubArr.arrayList = new ArrayList<ScriptVariable>(arrayList.subList(startIdx, arrayList.size()));
                svSubArr.hashMap = hashMap;
                return svSubArr;
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NotImplementedException);
        }
        public ScriptVariable getSubArray(int startIdx, int endLength) throws ScriptingExceptions.ChameleonScriptingException {
            if(getType() == VariableType.VariableTypeArrayMap) {
                if (startIdx < 0 || startIdx >= arrayList.size() ||
                        (endLength >= 0 && endLength + startIdx > arrayList.size()) ||
                        (endLength >= 0 && endLength <= startIdx) || (endLength < 0 && arrayList.size() + endLength <= 0)) {
                    throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.IndexOutOfBoundsException);
                }
                ScriptVariable svSubArr = new ScriptVariable();
                int endArrIndex = endLength >= 0 ? startIdx + endLength : arrayList.size() + endLength;
                svSubArr.arrayList = new ArrayList<ScriptVariable>(arrayList.subList(startIdx, endArrIndex));
                svSubArr.hashMap = hashMap;
                return svSubArr;
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NotImplementedException);
        }
        public void insertSubArray(int startIdx, ScriptVariable rhs) throws ScriptingExceptions.ChameleonScriptingException {
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NotImplementedException);
        }
        public void insertSubArray(int startIdx, int endLength, ScriptVariable rhs) throws ScriptingExceptions.ChameleonScriptingException {
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NotImplementedException);
        }

        public String getBinaryString() {
            if(getType() == VariableType.VariableTypeArrayMap) {
                StringBuilder binText = new StringBuilder();
                for(int b = 0; b < arrayList.size(); b++) {
                    binText.append(arrayList.get(b).getBinaryString());
                }
                for(ScriptVariable svar : hashMap.values()) {
                    binText.append(svar.getBinaryString());
                }
                return binText.toString();
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NotImplementedException);
        }

        public enum Operation {
            BINOP_PLUS,
            BINOP_BITWISE_AND,
            BINOP_BITWISE_OR,
            BINOP_BITWISE_XOR,
            BINOP_SHIFT_LEFT,
            BINOP_SHIFT_RIGHT,
            UOP_BITWISE_NOT
        }

        public ScriptVariable binaryOperation(Operation opType, ScriptVariable rhsVar)
                                              throws ScriptingExceptions.ChameleonScriptingException {

            if(opType == Operation.BINOP_PLUS && getType() != VariableType.VariableTypeArrayMap) {
                /*
                 * Handle three cases, if the remainder does not degrade nicely to one of these,
                 * then throw an exception at the non-standard use case:
                 * (1) numeric types; (2) arrays (concat); )3) string handling (append).
                 * Note that when the rhs is an array subclass, need to call the subclass handling
                 * function to store the result by swapping the order of the variables:
                 */
                if(isArrayType() && rhsVar.isArrayType()) {
                    if(varType != VariableType.VariableTypeArrayMap && rhsVar.getType() != VariableType.VariableTypeArrayMap) {
                        byte[] lhsBytes = getValueAsBytes(), rhsBytes = rhsVar.getValueAsBytes();
                        String byteStr = String.valueOf(lhsBytes) + String.valueOf(rhsBytes);
                        set(byteStr.getBytes());
                    }
                    else {
                        return rhsVar.binaryOperation(opType, this);
                    }
                }
                else if(isIntegerType() && rhsVar.isIntegerType()) {
                    set(getValueAsInt() + rhsVar.getValueAsInt());
                }
                else if(isStringType() && rhsVar.isStringType()) {
                    set(getValueAsString() + rhsVar.getValueAsString());
                }
                else {
                    throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.ArithmeticErrorException);
                }
                AndroidLog.i(TAG, "BINARY-OP: New Value " + getValueAsString());
                return this;
            }
            else if(opType == Operation.BINOP_PLUS) {
                if(rhsVar.getType() == VariableType.VariableTypeArrayMap) {
                    Collections.addAll(Arrays.asList(arrayList), Arrays.asList(rhsVar.arrayList));
                    hashMap.putAll(rhsVar.hashMap);
                }
                else if(rhsVar.getType() == VariableType.VariableTypeBytes) {
                    byte[] bytesArr = rhsVar.getValueAsBytes();
                    for(int b = 0; b < bytesArr.length; b++) {
                        arrayList.add(ScriptVariable.newInstance().set(bytesArr[b]));
                    }
                }
                else {
                    arrayList.add(rhsVar);
                }
                return this;
            }
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NotImplementedException);
        }

        public ScriptVariable unaryOperation(Operation opType) throws ScriptingExceptions.ChameleonScriptingException {
            throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.NotImplementedException);
        }

        public ScriptVariable setArrayListItems(ScriptVariable[] listItems) {
            varType = VariableType.VariableTypeArrayMap;
            for(ScriptVariable svar : listItems) {
                arrayList.add(svar);
            }
            return this;
        }

        public ScriptVariable setArrayListItems(List<ScriptVariable> listItems) {
            varType = VariableType.VariableTypeArrayMap;
            for(ScriptVariable svar : listItems) {
                arrayList.add(svar);
            }
            return this;
        }

    }

    public static boolean verifyArgumentListHasPattern(List<ScriptVariable> varsList, ScriptVariable.VariableType[][] patternsMatchList) {
        if(varsList == null) {
            return false;
        }
        else if(varsList.size() == 0 && (patternsMatchList.length == 0 || patternsMatchList == null)) {
            return true;
        }
        for(ScriptVariable.VariableType[] varArgsListType : patternsMatchList) {
            if(varsList.size() != varArgsListType.length) {
                continue;
            }
            boolean completeMatch = true;
            for(int vaIdx = 0; vaIdx < varArgsListType.length; vaIdx++) {
                boolean matchesSoFar = true;
                switch(varArgsListType[vaIdx]) {
                    case VariableTypeNone:
                        matchesSoFar = false;
                        break;
                    case VariableTypeInteger:
                        if(!varsList.get(vaIdx).isIntegerType()) {
                            matchesSoFar = false;
                        }
                        break;
                    case VariableTypeBoolean:
                        if(!varsList.get(vaIdx).isBooleanType()) {
                            matchesSoFar = false;
                        }
                        break;
                    case VariableTypeBytes:
                        if(!varsList.get(vaIdx).isBytesType()) {
                            matchesSoFar = false;
                        }
                        break;
                    case VariableTypeArrayMap:
                        if(!varsList.get(vaIdx).isArrayType()) {
                            matchesSoFar = false;
                        }
                        break;
                    default:
                        if(!varsList.get(vaIdx).isStringType()) {
                            matchesSoFar = false;
                        }
                }
                if(!matchesSoFar) {
                    completeMatch = false;
                    break;
                }
                if(completeMatch) {
                    return true;
                }
            }
        }
        return false;
    }

}