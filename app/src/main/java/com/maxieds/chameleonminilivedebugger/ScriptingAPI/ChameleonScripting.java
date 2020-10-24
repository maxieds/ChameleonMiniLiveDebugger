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

import android.view.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class ChameleonScripting {

    private static final String TAG = ChameleonScripting.class.getSimpleName();

    private static class ChameleonDeviceState {

        private static Stack<ChameleonDeviceState> SAVED_DEVICE_STATES = new Stack<ChameleonDeviceState>();

        public String CONFIG;
        public String UID;
        public String LOGMODE;
        public int SETTING;
        public boolean READONLY;
        public boolean FIELD;
        public int THRESHOLD;
        public int TIMEOUT;

        public void saveState() {}
        public void restoreState() {}

    }

    public static class ChameleonScriptInstance {

        public enum ScriptRuntimeState {
            RUNNING,
            EXCEPTION,
            PAUSED,
            BREAKPOINT,
            FINISHED_OK,
            TERMINATED_OK,
            TERMINATED_ERROR,
        }

        // TODO: ANTLR4 types ...
        private String scriptFilePath;
        private FileInputStream scriptFileStream;
        private String outputFilePath;
        private FileOutputStream outputFileStream;
        private String loggingFilePath;
        private FileOutputStream loggingFileStream;
        private long lastStartTime;
        private long runningTime;
        private int scriptExecLine;
        private StringBuilder consoleOutput;
        private View registerViewMainLayout;
        private View consoleViewMainLayout;
        private ArrayList<String> breakpointLabels;
        private ArrayList<Integer> breakpointLines;
        private boolean atBreakpoint;
        private Map<String, ScriptingTypes.ScriptVariable> scriptVariables;
        private ScriptRuntimeState scriptState;
        private ChameleonDeviceState chameleonDeviceState;

        public boolean runScriptFromStart(String scriptPath) throws ScriptingExecptions.ChameleonScriptingException {
            return false;
        }

        public boolean pauseRunningScript() throws ScriptingExecptions.ChameleonScriptingException {
            return false;
        }

        public boolean killRunningScript() throws ScriptingExecptions.ChameleonScriptingException {
            return false;
        }

        public boolean stepRunningScript() throws ScriptingExecptions.ChameleonScriptingException {
            return false;
        }

        public boolean writeLogFile(String logLine) {
            return false;
        }

        public boolean writeConsoleOutput(String consoleOutputLine) {
            return false;
        }

        public String getConsoleOutput() {
            return null;
        }

    }

}
