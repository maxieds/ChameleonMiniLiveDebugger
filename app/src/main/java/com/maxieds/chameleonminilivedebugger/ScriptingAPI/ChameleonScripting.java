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

import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import com.maxieds.chameleonminilivedebugger.BuildConfig;
import com.maxieds.chameleonminilivedebugger.ChameleonIO;
import com.maxieds.chameleonminilivedebugger.ChameleonSerialIOInterface;
import com.maxieds.chameleonminilivedebugger.ChameleonSettings;
import com.maxieds.chameleonminilivedebugger.LiveLoggerActivity;
import com.maxieds.chameleonminilivedebugger.R;
import com.maxieds.chameleonminilivedebugger.SerialIOReceiver;
import com.maxieds.chameleonminilivedebugger.TabFragment;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptLexer;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptParser;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptVisitor;
import com.maxieds.chameleonminilivedebugger.Utils;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ChameleonScripting {

    private static final String TAG = ChameleonScripting.class.getSimpleName();

    private static class ChameleonDeviceState {

        /* A stack of the most relevant saved states: */
        private static Stack<ChameleonDeviceState> SAVED_DEVICE_STATES = new Stack<ChameleonDeviceState>();

        /* The settings for the current Chameleon device and running
         * slot configuration:
         */
        public String CONFIG;
        public String UID;
        public String LOGMODE;
        public String SETTING;
        public String READONLY;
        public String FIELD;
        public String THRESHOLD;
        public String TIMEOUT;

        /* For completeness, restore any disturbed values in the ChameleonIO class: */
        public boolean CHAMIO_PAUSED;
        public boolean CHAMIO_DOWNLOAD;
        public boolean CHAMIO_UPLOAD;
        public boolean CHAMIO_WAITING_FOR_XMODEM;

        public ChameleonDeviceState() {}

        public void saveState(boolean push) {
            int shortTimeout = 750;
            ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
            if(serialIOPort != null) {
                if (!ChameleonIO.REVE_BOARD) {
                    CONFIG = ChameleonIOHandler.executeChameleonCommandForResult("CONFIG?", shortTimeout).getValueAt("data").getValueAsString();
                    UID = ChameleonIOHandler.executeChameleonCommandForResult("UID?", shortTimeout).getValueAt("data").getValueAsString();
                    LOGMODE = ChameleonIOHandler.executeChameleonCommandForResult("LOGMODE?", shortTimeout).getValueAt("data").getValueAsString();
                    SETTING = ChameleonIOHandler.executeChameleonCommandForResult("SETTING?", shortTimeout).getValueAt("data").getValueAsString();
                    READONLY = ChameleonIOHandler.executeChameleonCommandForResult("READONLY?", shortTimeout).getValueAt("data").getValueAsString();
                    FIELD = ChameleonIOHandler.executeChameleonCommandForResult("FIELD?", shortTimeout).getValueAt("data").getValueAsString();
                    THRESHOLD = ChameleonIOHandler.executeChameleonCommandForResult("THRESHOLD?", shortTimeout).getValueAt("data").getValueAsString();
                    TIMEOUT = ChameleonIOHandler.executeChameleonCommandForResult("TIMEOUT?", shortTimeout).getValueAt("data").getValueAsString();
                }
                else {
                    CONFIG = ChameleonIOHandler.executeChameleonCommandForResult("config?", shortTimeout).getValueAt("data").getValueAsString();
                    UID = ChameleonIOHandler.executeChameleonCommandForResult("uid?", shortTimeout).getValueAt("data").getValueAsString();
                    SETTING = ChameleonIOHandler.executeChameleonCommandForResult("setting?", shortTimeout).getValueAt("data").getValueAsString();
                    READONLY = ChameleonIOHandler.executeChameleonCommandForResult("readonly?", shortTimeout).getValueAt("data").getValueAsString();
                }
            }
            CHAMIO_PAUSED = ChameleonIO.PAUSED;
            CHAMIO_DOWNLOAD = ChameleonIO.DOWNLOAD;
            CHAMIO_UPLOAD = ChameleonIO.UPLOAD;
            CHAMIO_WAITING_FOR_XMODEM = ChameleonIO.WAITING_FOR_XMODEM;
            if(push) {
                SAVED_DEVICE_STATES.push(this);
            }
        }

        public void restoreState(boolean pop) {
            ChameleonIO.PAUSED = CHAMIO_PAUSED;
            ChameleonIO.DOWNLOAD = CHAMIO_DOWNLOAD;
            ChameleonIO.UPLOAD = CHAMIO_UPLOAD;
            ChameleonIO.WAITING_FOR_RESPONSE = CHAMIO_WAITING_FOR_XMODEM;
            ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
            if(serialIOPort != null) {
                if (!ChameleonIO.REVE_BOARD) {
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "CONFIG=%s", CONFIG));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "UID=%s", UID));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "LOGMODE=%s", LOGMODE));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "SETTING=%s", SETTING));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "READONLY=%s", READONLY));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "FIELD=%s", FIELD));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "THRESHOLD=%s", THRESHOLD));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "TIMEOUT=%s", TIMEOUT));
                } else {
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "config=%s", CONFIG));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "uid=%s", UID));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "setting=%s", SETTING));
                    ChameleonIOHandler.executeChameleonCommandForResult(String.format(BuildConfig.DEFAULT_LOCALE, "readonly=%s", READONLY));
                }
            }
            if(pop) {
                SAVED_DEVICE_STATES.pop();
            }
        }

    }

    public static class ChameleonScriptInstance {

        public enum ScriptRuntimeState {
            INITIALIZED,
            RUNNING,
            EXCEPTION,
            PAUSED,
            BREAKPOINT,
            FINISHED,
            DONE,
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
        private Stack< Map<String, ScriptingTypes.ScriptVariable> > nestedBlocksContextStack;
        private ScriptRuntimeState scriptState;
        private ChameleonDeviceState chameleonDeviceState;
        private Thread scriptRunnerThread;

        /* ANTLR4 types: https://www.antlr.org/api/Java/ */
        ANTLRInputStream scriptInputStream;
        ChameleonScriptLexer scriptLexer;
        CommonTokenStream scriptTokenStream;
        ChameleonScriptParser scriptParser;
        ParseTree scriptParseTree;
        ChameleonScriptVisitor scriptVisitor;

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
            nestedBlocksContextStack = new Stack<>();
            scriptState = ScriptRuntimeState.INITIALIZED;
            chameleonDeviceState = new ChameleonDeviceState();
            scriptRunnerThread = null;
            try {
                scriptInputStream = new ANTLRInputStream(scriptFileStream);
                scriptLexer = new ChameleonScriptLexer(scriptInputStream);
                scriptTokenStream = new CommonTokenStream(scriptLexer);
                scriptParser = new ChameleonScriptParser(scriptTokenStream);
                scriptParser.removeErrorListeners();
                scriptParser.addErrorListener(new ChameleonScriptErrorListener());
            } catch(IOException ioe) {
                ioe.printStackTrace();
                initialized = false;
            }
            scriptParseTree = null;
            scriptVisitor = null;
        }

        public void cleanupRuntimeData(boolean restoreChameleonState) {
            if(!isInitialized()) {
                return;
            }
            initialized = false;
            try {
                scriptFileStream.close();
                outputFileStream.close();
                loggingFileStream.close();
                debuggingFileStream.close();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            scriptState = ScriptRuntimeState.DONE;
            if(restoreChameleonState) {
                chameleonDeviceState.restoreState(true);
            }
        }

        public boolean isInitialized() {
            return initialized;
        }

        public List<ChameleonScriptErrorListener.SyntaxError> listSyntaxErrors(String scriptFileText) {
            // report errors with: parser.getNumberOfSyntaxErrors();
            // display them in a nice presentation / GUI fragment and vibrate for the user ...
            return null;
        }

        private boolean runScriptPreambleActions() {
            // TODO: Check the syntax errors ...
            chameleonDeviceState.saveState(ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE);
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusScriptingIsExec, R.drawable.toolbar_paused_icon16);
            ScriptingBreakPoint.bpDisabled = true;
            ChameleonIO.DeviceStatusSettings.stopPostingStats();
            SerialIOReceiver.setRedirectInterface(new ChameleonIOHandler());
            lastStartTime = System.currentTimeMillis();
            return true;
        }

        public boolean runScriptFromStart() {
            if(!runScriptPreambleActions()) {
                return false;
            }
            long execTimeLimit = ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME ?  ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME_SECONDS : 0;
            scriptRunnerThread = new Thread() {
                @Override
                public void run() {

                    // TODO: scriptParser -> run through all of its actions ???
                    //scriptParser.setBuildParseTree(true);
                    //scriptParseTree = ;
                    //scriptParseTree.inspect(scriptParser);
                    //ParseTreeWalker.DEFAULT.walk(new VarListener(), scriptParseTree);


                    // post UI updates on the GUI thread ...
                    // post console output (either in realtime, or at the conclusion of the run, or on RT error)
                    // restore state (Chameleon, app, phone), reset the serial I/O redirect handler,
                    // start posting stats again ...
                    // re-enable editing / modifying / adding breakpoints ...
                    // clear out all of the buffered serial I/O data ...
                    // notify user and/or vibrate on exit ...
                }
            };
            scriptRunnerThread.start();
            if(execTimeLimit > 0) {
                Handler setTimeLimitHandler = new Handler();
                Runnable enforceTimeLimitRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (scriptRunnerThread.isAlive()) {
                            scriptRunnerThread.interrupt();
                            killRunningScript();
                        }
                    }
                };
                setTimeLimitHandler.postDelayed(enforceTimeLimitRunnable, execTimeLimit * 1000);
            }
            return true;
        }

        public boolean pauseRunningScript() throws ScriptingExceptions.ChameleonScriptingException {
            return false; // TODO
        }

        public boolean stepRunningScript() throws ScriptingExceptions.ChameleonScriptingException {
            return false; // TODO
        }

        public boolean killRunningScript() throws ScriptingExceptions.ChameleonScriptingException {
            // cleanup, notify user of termination, restore the previous app state ...
            return false; // TODO
        }

        public boolean variableNameExists(String varName) {
            return scriptVariablesHashMap.get(varName) != null;
        }

        public ScriptingTypes.ScriptVariable lookupVariableByName(String varName) throws ScriptingExceptions.ChameleonScriptingException {
            ScriptingTypes.ScriptVariable svar = scriptVariablesHashMap.get(varName);
            if(svar == null) {
                throw new ScriptingExceptions.ChameleonScriptingException(ScriptingExceptions.ExceptionType.VariableNotFoundException, "varName");
            }
            return svar;
        }

        public void setVariableByName(ScriptingTypes.ScriptVariable scriptVar) throws ScriptingExceptions.ChameleonScriptingException {
            scriptVariablesHashMap.put(scriptVar.getName(), scriptVar);
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
            return "";
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

    public static boolean runScriptFromStart() {
        String scriptPath = ScriptingConfig.LAST_SCRIPT_LOADED_PATH;
        if(ScriptingFileIO.getStoragePathFromRelative(scriptPath, false, false) == null) {
            Utils.displayToastMessageShort(String.format(BuildConfig.DEFAULT_LOCALE, "Invalid script file path \"%s\".", scriptPath));
            return false;
        }
        activeChameleonScript = new ChameleonScriptInstance(scriptPath);
        return getRunningInstance().runScriptFromStart();
    }

}
