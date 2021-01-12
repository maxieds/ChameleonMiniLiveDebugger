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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxieds.chameleonminilivedebugger.AndroidSettingsStorage;
import com.maxieds.chameleonminilivedebugger.BuildConfig;
import com.maxieds.chameleonminilivedebugger.R;
import com.maxieds.chameleonminilivedebugger.Utils;

import java.io.File;
import java.util.Locale;

public class ScriptingGUIMain {

    private static final String TAG = ScriptingGUIMain.class.getSimpleName();

    public static void scriptGUIHandlePerformTaskClick(Button clickedBtn, String btnTag) {
        switch(btnTag) {
            case "SCRIPTING_BTN_RUN_FROM_START":
                ChameleonScripting.runScriptFromStart();
                break;
            case "SCRIPTING_BTN_KILL_SCRIPT":
                ChameleonScripting.getRunningInstance().killRunningScript();
                break;
            case "SCRIPTING_BTN_PAUSE_SCRIPT":
                ChameleonScripting.getRunningInstance().pauseRunningScript();
                break;
            case "SCRIPTING_BTN_STEP_SCRIPT":
                ChameleonScripting.getRunningInstance().stepRunningScript();
                break;
            default:
                /* TODO: Breakpoint actions */
                break;
        }
    }

    public static boolean initializeScriptingTabGUI(View cfgBaseLayout) {
        ScriptingConfig.initializeScriptingConfig();
        EditText selectedScriptText = cfgBaseLayout.findViewById(R.id.scriptingLoadImportTabScriptFileText);
        ScriptingGUIMain.displayEditTextValue(ScriptingConfig.LAST_SCRIPT_LOADED_PATH, selectedScriptText);
        Button setLoadedScriptBtn = cfgBaseLayout.findViewById(R.id.scriptingLoadImportTabScriptFileSetBtn);
        if(setLoadedScriptBtn == null) {
            return false;
        }
        setLoadedScriptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View cbView) {
                String nextPath = ScriptingFileIO.selectFileFromGUIList(ScriptingConfig.DEFAULT_SCRIPT_LOAD_FOLDER);
                if(nextPath == null || nextPath.equals("")) {
                    return;
                }
                ScriptingConfig.LAST_SCRIPT_LOADED_PATH = nextPath;
                AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.SCRIPTING_CONFIG_LAST_SCRIPT_LOADED_PATH);
                ScriptingGUIMain.displayEditTextValue(nextPath, selectedScriptText);
                //Log.i(TAG, "NEXT PATH: " + nextPath);
                //Log.i(TAG, "CONTENTS: [" + AndroidFileChooser.getFileContentsAsString(nextPath) + "]"); // WORKS :)
            }
        });
        CheckBox cbLimitExecTime = cfgBaseLayout.findViewById(R.id.scriptingLoadImportTabLimitExecTimeCbox);
        cbLimitExecTime.setChecked(ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME);
        EditText limitExecTimeSecsText = cfgBaseLayout.findViewById(R.id.scriptingRuntimeLimitExecSecondsText);
        limitExecTimeSecsText.setText(String.format(Locale.getDefault(), "%d", ScriptingConfig.DEFAULT_LIMIT_SCRIPT_EXEC_TIME_SECONDS));
        /* Setup breakpoints GUI displays: */
        ScriptingBreakPoint.breakpointsGUIDisplayContainer = (LinearLayout) cfgBaseLayout.findViewById(R.id.scriptingMainTabBreakpointsListView);
        ImageButton addBPLineBtn = cfgBaseLayout.findViewById(R.id.scriptingBPAddLineAppendBtn);
        if(addBPLineBtn == null) {
            return false;
        }
        addBPLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View cbView) {
                int lineNumber = -1;
                try {
                    lineNumber = Integer.parseInt(((TextView) cfgBaseLayout.findViewById(R.id.scriptingBPAddLineText)).getText().toString());
                } catch(Exception nfe) {
                    nfe.printStackTrace();
                    return;
                }
                ScriptingBreakPoint.addBreakpoint(lineNumber);
                Utils.dismissAndroidKeyboard(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT);
                ((TextView) cfgBaseLayout.findViewById(R.id.scriptingBPAddLineText)).setText("");
                ((TextView) cfgBaseLayout.findViewById(R.id.scriptingBPAddLineText)).setHint("@line");
            }
        });
        ImageButton addBPLabelBtn = cfgBaseLayout.findViewById(R.id.scriptingBPAddLabelAppendBtn);
        if(addBPLabelBtn == null) {
            return false;
        }
        addBPLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View cbView) {
                String lineLabel = "";
                try {
                    lineLabel = ((TextView) cfgBaseLayout.findViewById(R.id.scriptingBPAddLabelText)).getText().toString();
                } catch(Exception nfe) {
                    nfe.printStackTrace();
                    return;
                }
                ScriptingBreakPoint.addBreakpoint(lineLabel);
                Utils.dismissAndroidKeyboard(ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT);
                ((TextView) cfgBaseLayout.findViewById(R.id.scriptingBPAddLabelText)).setText("");
                ((TextView) cfgBaseLayout.findViewById(R.id.scriptingBPAddLabelText)).setHint("@label");
            }
        });
        return true;
    }

    public static boolean initializeScriptingConfigGUI(View cfgBaseLayout) {
        ScriptingConfig.initializeScriptingConfig();
        if(cfgBaseLayout == null) {
            return false;
        }
        int[] checkBoxResIds = new int[] {
                R.id.scriptingConfigCBoxSaveConsoleOutputToFile,
                R.id.scriptingConfigCBoxAppendConsoleOutput,
                R.id.scriptingConfigCBoxDatestampOutputFiles,
                R.id.scriptingConfigCBoxVerboseLogging,
                R.id.scriptingConfigCBoxVibrateOnExit,
                R.id.scriptingConfigCBoxRestoreChamDeviceState,
                R.id.scriptingConfigCBoxTerminateOnException,
                R.id.scriptingConfigCBoxIgnoreLiveLogging
        };
        boolean[] checkBoxValues = new boolean[] {
                ScriptingConfig.SAVE_CONSOLE_OUTPUT_FILE,
                ScriptingConfig.APPEND_CONSOLE_OUTPUT_FILE,
                ScriptingConfig.DATESTAMP_OUTPUT_FILES,
                ScriptingConfig.VERBOSE_ERROR_LOGGING,
                ScriptingConfig.VIBRATE_PHONE_ON_EXIT,
                ScriptingConfig.SAVE_RESTORE_CHAMELEON_STATE,
                ScriptingConfig.TERMINATE_ON_EXCEPTION,
                ScriptingConfig.IGNORE_LIVE_LOGGING
        };
        String[] androidSettingsKey = new String[] {
                AndroidSettingsStorage.SCRIPTING_CONFIG_SAVE_CONSOLE_OUTPUT_FILE,
                AndroidSettingsStorage.SCRIPTING_CONFIG_APPEND_CONSOLE_OUTPUT_FILE,
                AndroidSettingsStorage.SCRIPTING_CONFIG_DATESTAMP_OUTPUT_FILES,
                AndroidSettingsStorage.SCRIPTING_CONFIG_VERBOSE_ERROR_LOGGING,
                AndroidSettingsStorage.SCRIPTING_CONFIG_VIBRATE_PHONE_ON_EXIT,
                AndroidSettingsStorage.SCRIPTING_CONFIG_SAVE_RESTORE_CHAMELEON_STATE,
                AndroidSettingsStorage.SCRIPTING_CONFIG_TERMINATE_ON_EXCEPTION,
                AndroidSettingsStorage.SCRIPTING_CONFIG_IGNORE_LIVE_LOGGING
        };
        for(int cbIdx = 0; cbIdx < checkBoxResIds.length; cbIdx++) {
            CheckBox cbView = cfgBaseLayout.findViewById(checkBoxResIds[cbIdx]);
            if(cbView == null) {
                continue;
            }
            cbView.setChecked(checkBoxValues[cbIdx]);
            final int cbIdxConstValue = cbIdx;
            cbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View cboxView) {
                    AndroidSettingsStorage.updateValueByKey(androidSettingsKey[cbIdxConstValue]);
                    AndroidSettingsStorage.restorePreviousSettings(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE,
                                                                   AndroidSettingsStorage.AndroidSettingsType.SCRIPTING_CONFIG);
                }
            });
        }
        int[] envVarResIds = new int[] {
                R.id.scriptingConfigEnvVarEV0,
                R.id.scriptingConfigEnvVarEV1,
                R.id.scriptingConfigEnvVarEVK0,
                R.id.scriptingConfigEnvVarEVK1,
                R.id.scriptingConfigDefaultOutFileBaseName,
                R.id.scriptingConfigDefaultLogFileBaseName,
                R.id.scriptingConfigDatestampFormat
        };
        String[] envVarDefaults = new String[] {
                ScriptingConfig.ENV0_VALUE,
                ScriptingConfig.ENV1_VALUE,
                ScriptingConfig.ENVKEY0_VALUE,
                ScriptingConfig.ENVKEY1_VALUE,
                ScriptingConfig.OUTPUT_FILE_BASENAME,
                ScriptingConfig.OUTPUT_LOGFILE_BASENAME,
                ScriptingConfig.DATESTAMP_FORMAT
        };
        String[] androidSettingsKeyEnvVars = new String[] {
                AndroidSettingsStorage.SCRIPTING_CONFIG_ENV0_VALUE,
                AndroidSettingsStorage.SCRIPTING_CONFIG_ENV1_VALUE,
                AndroidSettingsStorage.SCRIPTING_CONFIG_ENVKEY0_VALUE,
                AndroidSettingsStorage.SCRIPTING_CONFIG_ENVKEY1_VALUE,
                AndroidSettingsStorage.SCRIPTING_CONFIG_OUTPUT_FILE_BASENAME,
                AndroidSettingsStorage.SCRIPTING_CONFIG_OUTPUT_LOGFILE_BASENAME,
                AndroidSettingsStorage.SCRIPTING_CONFIG_DATESTAMP_FORMAT
        };
        for(int evIdx = 0; evIdx < envVarResIds.length; evIdx++) {
            EditText etEnvVarInput = cfgBaseLayout.findViewById(envVarResIds[evIdx]);
            if(etEnvVarInput == null) {
                continue;
            }
            etEnvVarInput.setText(envVarDefaults[evIdx]);
            final int evIdxConstValue = evIdx;
            etEnvVarInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, int start, final int before, int count) {}
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(final Editable s) {
                    AndroidSettingsStorage.updateValueByKey(androidSettingsKeyEnvVars[evIdxConstValue]);
                    AndroidSettingsStorage.restorePreviousSettings(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE,
                                                                   AndroidSettingsStorage.AndroidSettingsType.SCRIPTING_CONFIG);
                }
            });
        }
        int[][] getFileFromPickerResIds = new int[][] {
                { R.id.scriptingConfigDefaultScriptLocPathText,      R.id.scriptingConfigDefaultScriptLocPathSetBtn },
                { R.id.scriptingConfigDefaultOutputFilePathText,     R.id.scriptingConfigDefaultOutputFilePathSetBtn },
                { R.id.scriptingConfigDefaultLoggingFilePathText,    R.id.scriptingConfigDefaultLoggingFilePathSetBtn },
                { R.id.scriptingConfigDefaultScriptCWDText,          R.id.scriptingConfigDefaultScriptCWDSetBtn },
                { R.id.scriptingConfigExtraKeysFileText,             R.id.scriptingConfigExtraKeysFileSetBtn }
        };
        String[] defaultFilePathValues = new String[] {
                ScriptingConfig.DEFAULT_SCRIPT_LOAD_FOLDER,
                ScriptingConfig.DEFAULT_FILE_OUTPUT_FOLDER,
                ScriptingConfig.DEFAULT_LOGGING_OUTPUT_FOLDER,
                ScriptingConfig.DEFAULT_SCRIPT_CWD,
                ScriptingConfig.EXTRA_KEYS_FILE
        };
        boolean[] pickerTypeIsFolder = new boolean[] {
                true,
                true,
                true,
                true,
                false
        };
        String[] androidSettingsFileUpdateKeys = new String[] {
                AndroidSettingsStorage.SCRIPTING_CONFIG_DEFAULT_SCRIPT_LOAD_FOLDER,
                AndroidSettingsStorage.SCRIPTING_CONFIG_DEFAULT_FILE_OUTPUT_FOLDER,
                AndroidSettingsStorage.SCRIPTING_CONFIG_DEFAULT_LOGGING_OUTPUT_FOLDER,
                AndroidSettingsStorage.SCRIPTING_CONFIG_DEFAULT_SCRIPT_CWD,
                AndroidSettingsStorage.SCRIPTING_CONFIG_EXTRA_KEYS_FILE
        };
        for(int pidx = 0; pidx < getFileFromPickerResIds.length; pidx++) {
            EditText etFilePickerValueDisplay = cfgBaseLayout.findViewById(getFileFromPickerResIds[pidx][0]);
            if(etFilePickerValueDisplay == null) {
                continue;
            }
            etFilePickerValueDisplay.setText(defaultFilePathValues[pidx]);
            ImageButton choosePathBtn = cfgBaseLayout.findViewById(getFileFromPickerResIds[pidx][1]);
            if(choosePathBtn == null) {
                continue;
            }
            final int pidxConstValue = pidx;
            choosePathBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View btnView) {
                    String nextPath = "";
                    String baseDirPath = AndroidSettingsStorage.getStringValueByKey(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE, androidSettingsFileUpdateKeys[pidxConstValue]);
                    File baseDirFile = ScriptingFileIO.getStoragePathFromRelative(baseDirPath, false, true);
                    if(baseDirFile != null) {
                        baseDirPath = baseDirFile.getPath();
                    }
                    else {
                        baseDirPath = "";
                    }
                    if(pickerTypeIsFolder[pidxConstValue]) {
                        nextPath = ScriptingFileIO.selectDirectoryFromGUIList(baseDirPath);
                    }
                    else {
                        nextPath = ScriptingFileIO.selectFileFromGUIList(baseDirPath);
                    }
                    if(nextPath == null || nextPath.equals("")) {
                        return;
                    }
                    ScriptingGUIMain.displayEditTextValue(nextPath, btnView.getRootView().findViewById(getFileFromPickerResIds[pidxConstValue][0]));
                    switch(pidxConstValue) {
                        case 0:
                            ScriptingConfig.DEFAULT_SCRIPT_LOAD_FOLDER = nextPath;
                            break;
                        case 1:
                            ScriptingConfig.DEFAULT_FILE_OUTPUT_FOLDER = nextPath;
                            break;
                        case 2:
                            ScriptingConfig.DEFAULT_LOGGING_OUTPUT_FOLDER = nextPath;
                            break;
                        case 3:
                            ScriptingConfig.DEFAULT_SCRIPT_CWD = nextPath;
                            break;
                        case 4:
                            ScriptingConfig.EXTRA_KEYS_FILE = nextPath;
                            break;
                        default:
                            break;
                    }
                    AndroidSettingsStorage.updateValueByKey(androidSettingsFileUpdateKeys[pidxConstValue]);
                    AndroidSettingsStorage.restorePreviousSettings(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE,
                                                                   AndroidSettingsStorage.AndroidSettingsType.SCRIPTING_CONFIG);
                }
            });
        }
        return true;
    }

    public static boolean displayEditTextValue(String fullValue, EditText etView) {
        try {
            String[] textValues = ScriptingFileIO.shortenStoragePath(fullValue, ScriptingFileIO.DISPLAY_TEXT_MAX_LENGTH);
            etView.setText(textValues[ScriptingFileIO.SHORTENED_PATH_INDEX]);
            etView.setTag(textValues[ScriptingFileIO.COMPLETE_PATH_INDEX]);
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

}
