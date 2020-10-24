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
import com.maxieds.chameleonminilivedebugger.LiveLoggerActivity;
import com.maxieds.chameleonminilivedebugger.Utils;

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
            VariableTypeArray,
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

        public boolean set(int nextValue) {
            varValueAsInt = nextValue;
            varType = VariableType.VariableTypeInteger;
            varIsInit = true;
            return true;
        }

        public int getValueAsInt() {
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
            return 0;
        }

        public boolean set(boolean nextValue) {
            varValueAsBoolean = nextValue;
            varType = VariableType.VariableTypeBoolean;
            varIsInit = true;
            return true;
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

        public boolean getValueAsBoolean() {
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

        public boolean set(byte[] nextValue) {
            varValueAsByteArray = nextValue;
            varType = VariableType.VariableTypeBytes;
            return varValueAsByteArray.length > 0;
        }

        public byte[] getValueAsBytes() {
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
                return new byte[0];
            }
        }

        public boolean set(String nextValue) {
            varValueAsString = nextValue;
            varType = VariableType.VariableTypeAsciiString;
            varIsInit = true;
            return true;
        }

        public String getValueAsString() {
            switch(varType) {
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                case VariableTypeStorageFilePath:
                    return varValueAsString;
                case VariableTypeRawFileFilePath:
                    return LiveLoggerActivity.getLiveLoggerInstance().getResources().getResourceName(varValueAsInt);
                case VariableTypeBytes:
                    return Utils.bytes2Hex(varValueAsByteArray);
                case VariableTypeBoolean:
                    return varValueAsBoolean ? "true" : "false";
                case VariableTypeInteger:
                    return String.format(BuildConfig.DEFAULT_LOCALE, "%d", varValueAsInt);
                default:
                    return "";
            }
        }

        public boolean isIntegerType() {
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

        public String getTypeName() { // Override: Array types ...
            if(isIntegerType()) {
                return "Integer";
            }
            else if(isBooleanType()) {
                return "Boolean";
            }
            else if(isBytesType()) {
                return "Bytes";
            }
            else if(isStringType()) {
                return "String";
            }
            return "None";
        }

        public boolean isArray() {
            return false;
        }

        public VariableType getArrayType() {
            return VariableType.VariableTypeNone;
        }

        public int length() {
            if(isStringType()) {
                return getValueAsString().length();
            }
            else if(varType != VariableType.VariableTypeNone) {
                return 1;
            }
            return 0;
        }

        public ScriptVariable getValueAt(int index) throws ScriptingExecptions.ChameleonScriptingException {
            return null; // TODO
        }

        public ScriptVariable getValueAt(String hashIndex) throws ScriptingExecptions.ChameleonScriptingException {
            return null; // TODO
        }

        public void setValueAt(int index) throws ScriptingExecptions.ChameleonScriptingException {}

        public void setValueAt(String hashIndex) throws ScriptingExecptions.ChameleonScriptingException {}

        public String getBinaryString() {
            return null; // TODO
        }

        public enum BinaryOperation {
            BINOP_PLUS,
            BINOP_MINUS,
            BINOP_TIMES,
            BINOP_BITWISE_AND,
            BINOP_BITWISE_OR,
            BINOP_BITWISE_XOR,
            BINOP_LOGICAL_AND,
            BINOP_LOGICAL_OR,
            BINOP_SHIFT_LEFT,
            BINOP_SHIFT_LEFT_LLL,
            BINOP_SHIFT_RIGHT,
            BINOP_SHIFT_RIGHT_RRR,
        }

        public ScriptVariable binaryOperator(ScriptVariable rhsVar, ScriptVariable lhsVar, BinaryOperation opType) { return null; }

        public enum UnaryOperation {
            UOP_BITWISE_NOT,
            UOP_LOGICAL_NOT,
        }

        public ScriptVariable unaryOperator(ScriptVariable rhsVar, ScriptVariable lhsVar, UnaryOperation opType) { return null; }

    }

    // TODO: Need array type

}
