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

import android.widget.Button;
import android.widget.CheckBox;

public class ScriptingGUI {

    private static final String TAG = ScriptingGUI.class.getSimpleName();

    public static void scriptGUIOpenFileButton(Button clickedBtn, String btnTag) {

    }

    public static void scriptGUIHandlePerformTaskClick(Button clickedBtn, String btnTag) {
        switch(btnTag) {
            case "SCRIPTING_BTN_RUN_FROM_START":
                break;
            case "SCRIPTING_BTN_KILL_SCRIPT":
                break;
            case "SCRIPTING_BTN_PAUSE_SCRIPT":
                break;
            case "SCRIPTING_BTN_STEP_SCRIPT":
                break;
            default:
                break;
        }
    }

    public static void scriptGUIHandleCheckboxClick(CheckBox clickedCbox, String cbTag) {


    }

    /* Console output tab message types: */
    // scripting_gui_error.xml
    // scripting_gui_livelog.xml
    // scripting_gui_console_output.xml (msg | summary)

}
