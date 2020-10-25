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
import android.widget.LinearLayout;

import com.maxieds.chameleonminilivedebugger.R;
import com.maxieds.chameleonminilivedebugger.TabFragment;

import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptParser;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptLexer;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        public void saveState(boolean push) {} // TODO
        public void restoreState(boolean pop) {} // TODO

    }

    public static class ChameleonScriptInstance {

        public enum ScriptRuntimeState {
            INITIALIZED,
            RUNNING,
            EXCEPTION,
            PAUSED,
            BREAKPOINT,
            FINISHED_OK,
            TERMINATED_OK,
            TERMINATED_ERROR,
        }

        private boolean initialized;
        private String scriptFilePath;
        private FileInputStream scriptFileStream;
        private String outputFilePath;
        private FileOutputStream outputFileStream;
        private String loggingFilePath;
        private FileOutputStream loggingFileStream;
        private String debuggingFilePath;
        private FileOutputStream debuggingFileStream;
        private long lastStartTime;
        private long runningTime;
        private long limitScriptExecTime;
        private int scriptExecLine;
        private StringBuilder consoleOutput;
        private LinearLayout registerViewMainLayout;
        private LinearLayout consoleViewMainLayout;
        private List<String> breakpointLabels;
        private List<Integer> breakpointLines;
        private boolean atBreakpoint;
        private Map<String, ScriptingTypes.ScriptVariable> scriptVariablesHashMap;
        private ScriptRuntimeState scriptState;
        private ChameleonDeviceState chameleonDeviceState;
        private Thread scriptRunnerThread;

        /* ANTLR4 types: https://www.antlr.org/api/Java/ */
        ANTLRInputStream scriptInputStream;
        ChameleonScriptLexer scriptLexer;
        CommonTokenStream scriptTokenStream;
        ChameleonScriptParser scriptParser;
        ParseTree scriptParseTree;
        //parser.removeErrorListeners()
        //parser.addErrorListener(yourListenerInstance)

        public ChameleonScriptInstance(String scriptFile) {
            initialized = true;
            scriptFilePath = scriptFile;
            outputFilePath = ScriptingFileIO.getScriptOutputFilePath(scriptFilePath);
            loggingFilePath = ScriptingFileIO.getScriptLoggingFilePath(scriptFilePath);
            debuggingFilePath = ScriptingFileIO.getScriptDebuggingFilePath(scriptFilePath);
            try {
                scriptFileStream = new FileInputStream(scriptFilePath);
                outputFileStream = new FileOutputStream(outputFilePath);
                loggingFileStream = new FileOutputStream(loggingFilePath);
                debuggingFileStream = debuggingFilePath.equals(ScriptingTypes.NULL) ? null : new FileOutputStream(debuggingFilePath);
            } catch(FileNotFoundException ioe) {
                ioe.printStackTrace();
                scriptFileStream = null;
                outputFileStream = loggingFileStream = debuggingFileStream = null;
                initialized = false;
            }
            runningTime = lastStartTime = 0;
            limitScriptExecTime = -1;
            scriptExecLine = 0;
            consoleOutput = new StringBuilder();
            try {
                registerViewMainLayout = TabFragment.UITAB_DATA[TabFragment.TAB_SCRIPTING].tabMenuItemLayouts[TabFragment.TAB_SCRIPTING_MITEM_REGISTER_VIEW].findViewById(R.id.scriptingTabRegisterViewMainLayoutContainer);
                consoleViewMainLayout = TabFragment.UITAB_DATA[TabFragment.TAB_SCRIPTING].tabMenuItemLayouts[TabFragment.TAB_SCRIPTING_MITEM_CONSOLE_VIEW].findViewById(R.id.scriptingTabConsoleViewMainLayoutContainer);
            } catch(Exception ex) {
                ex.printStackTrace();
                registerViewMainLayout = consoleViewMainLayout = null;
                initialized = false;
            }
            breakpointLabels = new ArrayList<String>();
            breakpointLines = new ArrayList<Integer>();
            atBreakpoint = false;
            scriptVariablesHashMap = new HashMap<String, ScriptingTypes.ScriptVariable>();
            scriptState = ScriptRuntimeState.INITIALIZED;
            chameleonDeviceState = new ChameleonDeviceState();
            scriptRunnerThread = null;
            scriptInputStream = null;
            scriptTokenStream = null;
            scriptParseTree = null;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public boolean variableNameExists(String varName) {
            return scriptVariablesHashMap.get(varName) != null;
        }

        public ScriptingTypes.ScriptVariable lookupVariableByName(String varName) throws ScriptingExecptions.ChameleonScriptingException {
            ScriptingTypes.ScriptVariable svar = scriptVariablesHashMap.get(varName);
            if(svar == null) {
                throw new ScriptingExecptions.ChameleonScriptingException(ScriptingExecptions.ExceptionType.VariableNotFoundException, "varName");
            }
            return svar;
        }

        public void setVariableByName(ScriptingTypes.ScriptVariable scriptVar) throws ScriptingExecptions.ChameleonScriptingException {
            ScriptingTypes.ScriptVariable svar = scriptVariablesHashMap.put(scriptVar.getName(), scriptVar);
        }

        public List<ChameleonScriptErrorListener.SyntaxError> listSyntaxErrors(String scriptFileText) {
            //CodePointCharStream inputStream = fromString(sourceCode);
            //CLexer lexer = new CLexer(inputStream);
            //CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
            //CParser parser = new CParser(commonTokenStream);
            //SyntaxErrorListener listener = new SyntaxErrorListener();
            //parser.addErrorListener(listener);
            //parser.functionDefinition();
            //return listener.getSyntaxErrors();
            return null;
        }

        private boolean runScriptPreambleActions() {
            // save Chameleon device state and push onto stack ...
            // change to CWD ...
            // set the script paused icon in toolbar to true ...
            // temporarily disable status bar updates ...
            // disable adding breakpoints
            // report errors with: parser.getNumberOfSyntaxErrors();
            return true;
        }

        public boolean runScriptFromStart() throws ScriptingExecptions.ChameleonScriptingException {
            if(!runScriptPreambleActions()) {
                return false;
            }
            try {
                scriptInputStream = new ANTLRInputStream(activeChameleonScript.scriptFileStream);
            } catch(Exception ioe) {
                ioe.printStackTrace();
                initialized = false;
                return false;
            }
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
            if(consoleOutput != null) {
                consoleOutput.append(consoleOutputLine);
                Log.i(TAG, " >>> " + consoleOutputLine);
                return true;
            }
            return false;
        }

        public String getConsoleOutput() {
            if(consoleOutput != null) {
                return consoleOutput.toString();
            }
            return null;
        }

        public void clearConsoleViewGUI() {
            if(consoleViewMainLayout != null) {
                consoleViewMainLayout.removeAllViews();
            }
        }

        public void clearRegisterViewGUI() {
            if(registerViewMainLayout != null) {
                registerViewMainLayout.removeAllViews();
            }
        }

    }

    private static ChameleonScriptInstance activeChameleonScript = null;

    public static ChameleonScriptInstance getRunningInstance() {
        return activeChameleonScript;
    }

    public boolean runScriptFromStart(String scriptPath) throws ScriptingExecptions.ChameleonScriptingException {
        return false;
    }

}
