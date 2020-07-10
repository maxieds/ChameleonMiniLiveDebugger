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

package com.maxieds.chameleonminilivedebugger;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

public class ChameleonPeripherals {

    private static final String TAG = ChameleonPeripherals.class.getSimpleName();

    public static SpinnerAdapter spinnerRButtonAdapter;
    public static SpinnerAdapter spinnerRButtonLongAdapter;
    public static SpinnerAdapter spinnerLButtonAdapter;
    public static SpinnerAdapter spinnerLButtonLongAdapter;
    public static SpinnerAdapter spinnerLEDRedAdapter;
    public static SpinnerAdapter spinnerLEDGreenAdapter;
    public static SpinnerAdapter spinnerButtonMyAdapter;
    public static SpinnerAdapter spinnerLogModeAdapter;

    /**
     * Queries and restores the current defaults of the device peripheral actions indicated in the
     * Tools Menu spinners.
     * @param view
     * @see res/layout/tools_tab_commands.xml.xml
     */
    public static void actionButtonRestorePeripheralDefaults(View view) {
        if (Settings.getActiveSerialIOPort() != null) {
            // next, query the defaults from the device to get accurate settings (if the device is connected):
            int[] spinnerIDs = {
                    R.id.RButtonSpinner,
                    R.id.RButtonLongSpinner,
                    R.id.LButtonSpinner,
                    R.id.LButtonLongSpinner,
                    R.id.LEDRedSpinner,
                    R.id.LEDGreenSpinner,
                    R.id.ButtonMyRevEBoardSpinner
            };
            String[] queryCmds = {
                    "RBUTTON?",
                    "RBUTTON_LONG?",
                    "LBUTTON?",
                    "LBUTTON_LONG?",
                    "LEDRED?",
                    "LEDGREEN?",
                    "button?"
            };
            for (int i = 0; i < spinnerIDs.length; i++) {
                Log.i(TAG, queryCmds[i]);
                Spinner curSpinner = (Spinner) LiveLoggerActivity.getInstance().findViewById(spinnerIDs[i]);
                if(curSpinner == null) {
                    continue;
                }
                String deviceSetting = ChameleonIO.getSettingFromDevice(queryCmds[i]);
                curSpinner.setSelection(((ArrayAdapter<String>) curSpinner.getAdapter()).getPosition(deviceSetting));
            }
            // handle FIELD and READ-ONLY switches:
            String fieldSetting = ChameleonIO.getSettingFromDevice("FIELD?");
            Switch fieldSwitch = LiveLoggerActivity.getInstance().findViewById(R.id.fieldOnOffSwitch);
            if(fieldSwitch == null) {
                return;
            }
            fieldSwitch.setChecked(fieldSetting.equals("0") ? false : true);
            String roQueryCmd = ChameleonIO.REVE_BOARD ? "readonly?" : "READONLY?";
            String roSetting = ChameleonIO.getSettingFromDevice(roQueryCmd);
            Switch roSwitch = LiveLoggerActivity.getInstance().findViewById(R.id.readonlyOnOffSwitch);
            if(roSwitch == null) {
                return;
            }
            roSwitch.setChecked(roSetting.equals("0") ? false : true);
        }
    }

}
