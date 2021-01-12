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
import android.view.View;
import android.widget.LinearLayout;

import com.maxieds.androidfilepickerlightlibrary.FileChooserException;
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
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptParserBaseListener;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ChameleonScriptParserVisitor;
import com.maxieds.chameleonminilivedebugger.Utils;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

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
        private LinearLayout consoleViewMainLayout;
        private List<String> breakpointLabels;
        private List<Integer> breakpointLines;
        private boolean atBreakpoint;
        private Map<String, ScriptingTypes.ScriptVariable> scriptVariablesHashMap;
        private Stack< Map<String, ScriptingTypes.ScriptVariable> > nestedBlocksContextStack;
        private ScriptRuntimeState scriptState;
        private ChameleonDeviceState chameleonDeviceState;
        private Thread scriptRunnerThread;
        private Future<?> scriptRunnerThreadExecRef;

        ANTLRInputStream scriptInputStream;
        ChameleonScriptLexer scriptLexer;
        CommonTokenStream scriptTokenStream;
        ChameleonScriptParser scriptParser;
        ParseTree scriptParseTree;
        ChameleonScriptVisitorExtended scriptVisitor;
        ChameleonScriptErrorListener scriptErrorListener;

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
                if(TabFragment.UITAB_DATA[TabFragment.TAB_SCRIPTING].tabMenuItemLayouts[TabFragment.TAB_SCRIPTING_MITEM_CONSOLE_VIEW] == null) {
                    TabFragment.UITAB_DATA[TabFragment.TAB_SCRIPTING].changeMenuItemDisplay(TabFragment.TAB_SCRIPTING_MITEM_CONSOLE_VIEW, true);
                }
                consoleViewMainLayout = TabFragment.UITAB_DATA[TabFragment.TAB_SCRIPTING].tabMenuItemLayouts[TabFragment.TAB_SCRIPTING_MITEM_CONSOLE_VIEW].findViewById(R.id.scriptingTabConsoleViewMainLayoutContainer);
            } catch(Exception ex) {
                ex.printStackTrace();
                consoleViewMainLayout = null;
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
            scriptInputStream = null;
            scriptLexer = null;
            scriptTokenStream = null;
            scriptParser = null;
            scriptErrorListener = null;
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
            if(scriptState != ScriptRuntimeState.EXCEPTION) {
                scriptState = ScriptRuntimeState.DONE;
            }
            ScriptingBreakPoint.bpDisabled = false;
            if(restoreChameleonState) {
                chameleonDeviceState.restoreState(true);
            }
            SerialIOReceiver.resetRedirectInterface();
        }

        public boolean isInitialized() {
            return initialized;
        }

        public boolean loadedScriptHasSyntaxErrors() {
            return scriptParser.getNumberOfSyntaxErrors() > 0;
        }

        public List<ChameleonScriptErrorListener.SyntaxError> getSyntaxErrors() {
            return scriptErrorListener.getSyntaxErrors();
        }

        private boolean runScriptPreambleActions() {
            if(loadedScriptHasSyntaxErrors()) {
                List<ChameleonScriptErrorListener.SyntaxError> syntaxErrorsList = getSyntaxErrors();
                for(ChameleonScriptErrorListener.SyntaxError syntaxError : syntaxErrorsList) {
                    String syntaxErrorNotifyMsg = String.format(Locale.getDefault(), "%s\n%s",
                            syntaxError.getException().getClass().getSimpleName(), syntaxError.getMessage());
                    String[] syntaxErrorDetailsList = new String[] {
                            String.format(Locale.getDefault(), "@LINE-NO:  %d", syntaxError.getLine()),
                            String.format(Locale.getDefault(), "@CHAR-POS: %d", syntaxError.getCharPositionInLine()),
                            String.format(Locale.getDefault(), "@SYMBOL:   '%s'", syntaxError.getOffendingSymbol().toString()),
                            String.format(Locale.getDefault(), "@TOKEN:    %s", syntaxError.getException().getOffendingToken().getText())
                    };
                    ScriptingGUIConsole.appendConsoleOutputRecordErrorWarning(syntaxErrorNotifyMsg, syntaxErrorDetailsList, syntaxError.getLine());
                    Log.w(TAG, "SYNTAX ERROR: " + syntaxErrorNotifyMsg + "\n" + String.join("\n  > ", syntaxErrorDetailsList));
                }
                ScriptingUtils.signalStateChangeByVibration(ScriptRuntimeState.EXCEPTION);
                return false;
            }
            chameleonDeviceState.saveState(ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE);
            LiveLoggerActivity.getLiveLoggerInstance().setStatusIcon(R.id.statusScriptingIsExec, R.drawable.toolbar_paused_icon16);
            ScriptingBreakPoint.bpDisabled = true;
            ChameleonIO.DeviceStatusSettings.stopPostingStats();
            SerialIOReceiver.setRedirectInterface(new ChameleonIOHandler());
            lastStartTime = System.currentTimeMillis();
            return true;
        }

        public boolean runScriptFromStart() {

            scriptRunnerThread = new Thread() {
                @Override
                public void run() {

                    try {
                        scriptInputStream = new ANTLRInputStream(scriptFileStream);
                        scriptLexer = new ChameleonScriptLexer(scriptInputStream);
                        scriptTokenStream = new CommonTokenStream(scriptLexer);
                        scriptParser = new ChameleonScriptParser(scriptTokenStream);
                        scriptParser.removeErrorListeners();
                        scriptErrorListener = new ChameleonScriptErrorListener();
                        scriptParser.addErrorListener(scriptErrorListener);
                        scriptParser.setBuildParseTree(true);
                        scriptParseTree = scriptParser.file_contents().getChild(0);
                        scriptVisitor = new ChameleonScriptVisitorExtended(getRunningInstance());
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                        initialized = false;
                    }
                    if(!runScriptPreambleActions()) {
                        return;
                    }

                    scriptState = ScriptRuntimeState.RUNNING;
                    Handler setTimeLimitHandler = new Handler();
                    Runnable enforceTimeLimitRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (scriptRunnerThread.isAlive()) {
                                scriptRunnerThread.interrupt();
                                scriptState = ScriptRuntimeState.EXCEPTION;
                                killRunningScript();
                            }
                        }
                    };
                    long execTimeLimit = ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME ?  ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME_SECONDS : 0;
                    if(execTimeLimit > 0) {
                        setTimeLimitHandler.postDelayed(enforceTimeLimitRunnable, execTimeLimit * 1000);
                    }

                    scriptVisitor.visit(scriptParseTree);
                    writeLogFile(String.format(Locale.getDefault(), "TEXT PARSE TREE for file \"%s\":\n\n%s\n", scriptFilePath, scriptParseTree.toStringTree()));
                    runningTime = System.currentTimeMillis() - lastStartTime;
                    setTimeLimitHandler.removeCallbacks(enforceTimeLimitRunnable);
                    scriptState = ScriptRuntimeState.FINISHED;
                    ScriptingUtils.signalStateChangeByVibration(scriptState);

                    String scriptRuntimeSummaryMsg = String.format(Locale.getDefault(), "The script finished running normally in %g min (%g sec).",
                            runningTime / 60000.0, runningTime / 1000.0);
                    scriptRuntimeSummaryMsg += "It generated the following output:\n\n";
                    scriptRuntimeSummaryMsg += getConsoleOutput();
                    ScriptingGUIConsole.appendConsoleOutputRecordScriptRuntimeSummary(scriptRuntimeSummaryMsg, null);

                    SerialIOReceiver.resetRedirectInterface();
                    ChameleonIO.DeviceStatusSettings.startPostingStats(0);
                    cleanupRuntimeData(ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE);

                }
            };
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runner) {
                    final Thread thread = new Thread(runner);
                    thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread paramThread, Throwable paramExcpt) {
                            LiveLoggerActivity.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.displayToastMessageShort("Runtime error encountered.");
                                }
                            });
                            String ewarnMsg = "Unexpected exception caught.";
                            try {
                                RuntimeException rtEx = (RuntimeException) paramExcpt;
                                ewarnMsg = String.format(Locale.getDefault(), "%s: \n%s", rtEx.getClass().getSimpleName(), rtEx.getMessage());
                                ScriptingGUIConsole.appendConsoleOutputRecordErrorWarning(ewarnMsg, null, getExecutingLineOfCode());
                            } catch(Exception ex) {
                                ex.printStackTrace();
                            }
                            ChameleonScripting.getRunningInstance().killRunningScript();
                            paramThread.interrupt();
                        }
                    });
                    return thread;
                }
            };
            ExecutorService thExecPool = Executors.newSingleThreadExecutor(threadFactory);
            //thExecPool.execute(scriptRunnerThread);
            scriptRunnerThreadExecRef = thExecPool.submit(scriptRunnerThread);
            return true;

        }

        public boolean stepRunningScript() {
            if(scriptState != ScriptRuntimeState.BREAKPOINT && scriptState != ScriptRuntimeState.PAUSED) {
                return false;
            }
            ScriptingBreakPoint.bpDisabled = true;
            scriptState = ScriptRuntimeState.RUNNING;
            ScriptingUtils.signalStateChangeByVibration(scriptState);
            return true;
        }

        public boolean pauseRunningScript() {
            scriptRunnerThread.interrupt();
            scriptState = ScriptRuntimeState.PAUSED;
            ScriptingUtils.signalStateChangeByVibration(scriptState);
            ScriptingBreakPoint.bpDisabled = false;
            return true;
        }

        public boolean killRunningScript(String scriptKillNotifyMsg) {
            if(!scriptRunnerThread.isInterrupted()) {
                if(!scriptRunnerThreadExecRef.isCancelled()) {
                    scriptRunnerThreadExecRef.cancel(true);
                }
                if(!scriptRunnerThread.isInterrupted()) {
                    scriptRunnerThread.interrupt();
                }
                ScriptingGUIConsole.appendConsoleOutputRecordInfoMessage(scriptKillNotifyMsg, null, scriptExecLine);
                ScriptingGUIConsole.appendConsoleOutputRecordInfoMessage(getConsoleOutput(), null, scriptExecLine);
                scriptState = ScriptRuntimeState.PAUSED;
                ScriptingUtils.signalStateChangeByVibration(scriptState);
                cleanupRuntimeData(ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE);
                ScriptingBreakPoint.bpDisabled = false;
                return true;
            }
            return false;
        }

        public boolean killRunningScript() {
            return killRunningScript("Script killed.");
        }

        public int getExecutingLineOfCode() {
            return scriptExecLine;
        }

        public void setActiveLineOfCode(int nextLOC) {
            scriptExecLine = nextLOC;
        }

        public boolean variableNameExists(String varName) {
            return scriptVariablesHashMap.get(varName) != null;
        }

        public ScriptingTypes.ScriptVariable lookupVariableByName(String varName) throws ScriptingExceptions.ChameleonScriptingException {
            ScriptingTypes.ScriptVariable svar = scriptVariablesHashMap.get(varName);
            return svar;
        }

        public void setVariableByName(String varName, ScriptingTypes.ScriptVariable scriptVar) throws ScriptingExceptions.ChameleonScriptingException {
            scriptVariablesHashMap.put(varName, scriptVar);
        }

        public boolean writeLogFile(String logLine) {
            if(!ScriptingConfig.VERBOSE_ERROR_LOGGING) {
                return false;
            }
            try {
                loggingFileStream.write(String.format(Locale.getDefault(), ">> [%s] %s\n", Utils.getTimestamp(), logLine).getBytes());
                Log.i(TAG, "LOG FILE LINE>> " + logLine);
            } catch(IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
            return true;
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

        public LinearLayout getConsoleViewMainLayout() {
            return consoleViewMainLayout;
        }

        public void clearConsoleViewGUI() {
            if(consoleViewMainLayout != null) {
                consoleViewMainLayout.removeAllViews();
            }
        }

        public boolean postBreakpointLabel(String bpLabel, ParserRuleContext lastRuleCtx) {
            scriptState = ScriptRuntimeState.BREAKPOINT;
            atBreakpoint = true;
            ScriptingGUIConsole.appendConsoleOutputRecordBreakpoint(bpLabel, lastRuleCtx.getStart().getLine());
            ScriptingUtils.signalStateChangeByVibration(scriptState);
            return false;
        }

    }

    private static ChameleonScriptInstance activeChameleonScript = null;

    public static ChameleonScriptInstance getRunningInstance() {
        return activeChameleonScript;
    }

    public static boolean runScriptFromStart() {
        String scriptPath = ScriptingFileIO.expandStoragePath(ScriptingConfig.LAST_SCRIPT_LOADED_PATH);
        Log.i(TAG, "Attempting to run script from file path: " + scriptPath);
        if(ScriptingFileIO.getStoragePathFromRelative(scriptPath, false, false) == null) {
            Utils.displayToastMessageShort(String.format(BuildConfig.DEFAULT_LOCALE, "Invalid script file path \"%s\".", scriptPath));
            return false;
        }
        if(activeChameleonScript != null && !activeChameleonScript.scriptRunnerThread.isInterrupted()) {
            activeChameleonScript.scriptRunnerThread.interrupt();
            activeChameleonScript.cleanupRuntimeData(ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE);
        }
        activeChameleonScript = new ChameleonScriptInstance(scriptPath);
        activeChameleonScript.clearConsoleViewGUI();
        return getRunningInstance().runScriptFromStart();
    }

}
