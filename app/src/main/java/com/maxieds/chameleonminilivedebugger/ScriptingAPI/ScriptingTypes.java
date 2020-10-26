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
import com.maxieds.chameleonminilivedebugger.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        private void setLocalVariableDefaults() {
            varName = "";
            varType = VariableType.VariableTypeNone;
            varIsInit = false;
            varValueAsInt = 0;
            varValueAsBoolean = false;
            varValueAsByteArray = new byte[0];
            varValueAsString = "";
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

        public boolean setName(String nextVarName) {
            varName = nextVarName;
            return true;
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

        public int getValueAsInt() throws ScriptingExecptions.ChameleonScriptingException {
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
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.InvalidTypeException);
        }

        public short getValueAsShort() throws ScriptingExecptions.ChameleonScriptingException {
            int int32Value = getValueAsInt();
            return (short) (int32Value & 0x0000ffff);
        }

        public byte getValueAsByte() throws ScriptingExecptions.ChameleonScriptingException {
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
                nfe.printStackTrace();
                return false;
            }
            return false;
        }

        public boolean getValueAsBoolean() throws ScriptingExecptions.ChameleonScriptingException {
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

        public byte[] getValueAsBytes() throws ScriptingExecptions.ChameleonScriptingException {
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
                System.arraycopy(returnBytes, 0, intBytes, 0, lastByteArrayPos);
                return returnBytes;
            }
            else if(isStringType()) {
                return varValueAsString.getBytes();
            }
            else {
                throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.InvalidTypeException);
            }
        }

        public ScriptVariable set(String nextValue) {
            varValueAsString = nextValue;
            varType = VariableType.VariableTypeAsciiString;
            varIsInit = true;
            return this;
        }

        public String getValueAsString() throws ScriptingExecptions.ChameleonScriptingException {
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
                    return String.format(BuildConfig.DEFAULT_LOCALE, "%d", varValueAsInt);
                default:
                    throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.InvalidTypeException);
            }
        }

        public boolean isIntegerType() throws ScriptingExecptions.ChameleonScriptingException {
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

        public VariableType getType() throws ScriptingExecptions.ChameleonScriptingException {
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
            return VariableType.VariableTypeNone;
        }

        public static ScriptVariable newInstance() {
            return new ScriptVariable();
        }

        public static ScriptVariable parseHexString(String literalText) throws ScriptingExecptions.ChameleonScriptingException {
            ScriptVariable nextVar = new ScriptVariable();
            if(!Utils.stringIsHexadecimal(literalText)) {
                throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.FormatErrorException);
            }
            nextVar.set(literalText);
            return nextVar;
        }

        public static ScriptVariable parseRawString(String literalText) throws ScriptingExecptions.ChameleonScriptingException {
            literalText.replace("\\", "\\\\");
            ScriptVariable nextVar = new ScriptVariable();
            nextVar.set(literalText);
            return nextVar;
        }

        public static ScriptVariable parseInt(String literalText) throws ScriptingExecptions.ChameleonScriptingException {
            ScriptVariable nextVar = new ScriptVariable();
            try {
                int intVal = Integer.parseInt(literalText);
                nextVar.set(intVal);
                return nextVar;
            } catch(NumberFormatException nfe) {
                throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NumberFormatException, nfe);
            }
        }

        public static ScriptVariable parseBoolean(String literalText) throws ScriptingExecptions.ChameleonScriptingException {
            ScriptVariable nextVar = new ScriptVariable();
            try {
                boolean logicalValue = Boolean.parseBoolean(literalText);
                nextVar.set(logicalValue);
                return nextVar;
            } catch(Exception ex) {
                throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.FormatErrorException, ex);
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

        public int length() throws ScriptingExecptions.ChameleonScriptingException {
            if(isStringType()) {
                return getValueAsString().length();
            }
            else if(varType != VariableType.VariableTypeNone) {
                return 1;
            }
            return 0;
        }

        public ScriptVariable getValueAt(int index) throws ScriptingExecptions.ChameleonScriptingException {
            if(index == 0) {
                return this;
            }
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.IllegalOperationException);
        }

        public ScriptVariable getValueAt(String hashIndex) throws ScriptingExecptions.ChameleonScriptingException {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.IllegalOperationException);
        }

        public void setValueAt(int index, ScriptVariable varObj) throws ScriptingExecptions.ChameleonScriptingException {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.IllegalOperationException);
        }

        public void setValueAt(String hashIndex, ScriptVariable varObj) throws ScriptingExecptions.ChameleonScriptingException {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.IllegalOperationException);
        }

        public String getBinaryString() {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
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
                                              throws ScriptingExecptions.ChameleonScriptingException {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

        public ScriptVariable unaryOperation(Operation opType) throws ScriptingExecptions.ChameleonScriptingException {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

    }

    public static class ScriptVariableArrayMap extends ScriptVariable {

        private List<ScriptVariable> arrayList;
        private Map<String, ScriptVariable> hashMap;

        public ScriptVariableArrayMap() {
            arrayList = new ArrayList<ScriptVariable>();
            hashMap = new HashMap<String, ScriptVariable>();
        }

        @Override
        public byte[] getValueAsBytes() {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

        @Override
        public String getValueAsString() {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

        @Override
        public int length() throws ScriptingExecptions.ChameleonScriptingException {
            return arrayList.size() + hashMap.size();
        }

        @Override
        public ScriptVariable getValueAt(int index) throws ScriptingExecptions.ChameleonScriptingException {
            return null;
        }

        @Override
        public ScriptVariable getValueAt(String hashIndex) throws ScriptingExecptions.ChameleonScriptingException {
            return null;
        }

        @Override
        public void setValueAt(int index, ScriptVariable varObj) throws ScriptingExecptions.ChameleonScriptingException {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

        @Override
        public void setValueAt(String hashIndex, ScriptVariable varObj) throws ScriptingExecptions.ChameleonScriptingException {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

        @Override
        public String getBinaryString() {
            throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

        @Override
        public ScriptVariable binaryOperation(ScriptVariable.Operation opType, ScriptVariable rhsVar)
                                              throws ScriptingExecptions.ChameleonScriptingException {
             throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

        @Override
        public ScriptVariable unaryOperation(ScriptVariable.Operation opType) throws ScriptingExecptions.ChameleonScriptingException {
             throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.NotImplementedException);
        }

    }

}