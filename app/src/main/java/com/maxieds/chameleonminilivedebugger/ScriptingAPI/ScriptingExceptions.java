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

import java.util.Locale;

public class ScriptingExceptions {

    private static final String TAG = ScriptingExceptions.class.getSimpleName();

    public enum ExceptionType {
        GenericException,
        OperationNotSupportedException,
        InvalidLengthException,
        InvalidTypeException,
        FormatErrorException,
        JavaErrorException,
        PermissionsErrorException,
        InvalidDataException,
        ArithmeticErrorException,
        IllegalArgumentException,
        IllegalOperationException,
        IllegalStateException,
        NumberFormatException,
        IOException,
        FileNotFoundException,
        DirectoryNotFoundException,
        OutOfMemoryException,
        VariableNotFoundException,
        NotImplementedException,
        InvalidArgumentException,
        IndexOutOfBoundsException,
        KeyNotFoundException,
        ChameleonDisconnectedException,
    }

    public static class ChameleonScriptingException extends RuntimeException {

        public ChameleonScriptingException(ExceptionType exType) {
            super(exType.name());
            Log.i(TAG, exType.name());
            this.printStackTrace();
        }

        public ChameleonScriptingException(ExceptionType exType, String msg) {
            super(String.format(Locale.getDefault(), "%s => %s", exType.name(), msg));
        }

        public int getInvokingLineNumber() {
            StackTraceElement[] stackTraceData = getStackTrace();
            if(stackTraceData.length > 1) {
                return stackTraceData[1].getLineNumber();
            }
            return 0;
        }

        public String getInvokingSourceFile() {
            StackTraceElement[] stackTraceData = getStackTrace();
            if(stackTraceData.length > 1) {
                return stackTraceData[1].getFileName();
            }
            return "";
        }

        public String getInvokingClassName() {
            StackTraceElement[] stackTraceData = getStackTrace();
            if(stackTraceData.length > 1) {
                return stackTraceData[1].getClassName();
            }
            return "";
        }

        public String getInvokingMethodName() {
            StackTraceElement[] stackTraceData = getStackTrace();
            if(stackTraceData.length > 1) {
                return stackTraceData[1].getMethodName();
            }
            return "";
        }

    }

}
