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

import com.maxieds.chameleonminilivedebugger.LiveLoggerActivity;
import com.maxieds.chameleonminilivedebugger.Utils;

import java.util.Locale;

public class ScriptingTypes {

    private static final String TAG = ScriptingTypes.class.getSimpleName();

    /*
     * Definitions for storage of variable types:
     */
    public enum ScriptVariable {
        VariableTypeInteger,
        VariableTypeBoolean,
        VariableTypeBytes,
        VariableTypeHexString,
        VariableTypeAsciiString,
        VariableTypeRawFileFilePath,
        VariableTypeStorageFilePath;

        private String  varName;
        private boolean varIsInit;
        private int     varValueAsInt;
        private boolean varValueAsBoolean;
        private byte[]  varValueAsByteArray;
        private String  varValueAsString;

        private ScriptVariable() {
            varName = "";
            varIsInit = false;
            varValueAsInt = 0;
            varValueAsBoolean = false;
            varValueAsByteArray = new byte[0];
            varValueAsString = "";
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

        public boolean setValueAsInt(int nextValue) {
            varValueAsInt = nextValue;
            varIsInit = true;
            return true;
        }

        public int getValueAsInt() {
            if(this == VariableTypeInteger) {
                return varValueAsInt;
            }
            else if(this == VariableTypeBoolean) {
                return varValueAsBoolean ? 1 : 0;
            }
            else if((this == VariableTypeBytes) && (varValueAsByteArray.length <= 8)) {
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

        public boolean setValueAsBoolean(boolean nextValue) {
            varValueAsBoolean = nextValue;
            varIsInit = true;
            return true;
        }

        public boolean setValueAsBoolean(String nextValue) {
            try {
                if (nextValue.equalsIgnoreCase("TRUE") || !nextValue.equals("0")) {
                    varValueAsBoolean = true;
                    varIsInit = true;
                    return true;
                } else if (nextValue.equalsIgnoreCase("FALSE") || Integer.parseInt(nextValue, 16) != 0) {
                    varValueAsBoolean = false;
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
            if(this == VariableTypeBoolean) {
                return varValueAsBoolean;
            }
            else if(this == VariableTypeInteger) {
                return varValueAsInt != 0;
            }
            else if(isBytesType()) {
                return getValueAsBytes().length > 0;
            }
            else {
                return !getValueAsString().equals("");
            }
        }

        public boolean setValueAsBytes(String nextValue) {
            varValueAsByteArray = Utils.hexString2Bytes(nextValue);
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

        public boolean setValueAsString(String nextValue) {
            varValueAsString = nextValue;
            varIsInit = true;
            return true;
        }

        public String getValueAsString() {
            switch(this) {
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
                    return String.format(Locale.ENGLISH, "%d", varValueAsInt);
                default:
                    return "";
            }
        }

        public boolean isIntegerType() {
            switch(this) {
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
            switch(this) {
                case VariableTypeBoolean:
                case VariableTypeInteger:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isBytesType() {
            return this == VariableTypeBytes;
        }

        public boolean isStringType() {
            switch(this) {
                case VariableTypeHexString:
                case VariableTypeAsciiString:
                case VariableTypeStorageFilePath:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isFilePathType() {
            switch(this) {
                case VariableTypeRawFileFilePath:
                case VariableTypeStorageFilePath:
                    return true;
                default:
                    return false;
            }
        }

        public String getTypeName() {
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
            return "NULL";
        }

    }

    /* Include support for constants */


}
