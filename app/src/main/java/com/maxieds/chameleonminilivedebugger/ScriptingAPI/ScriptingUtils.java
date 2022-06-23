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

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class ScriptingUtils {

    private static final String TAG = ScriptingUtils.class.getSimpleName();

    private static final long VIBRATE_PULSE_BASE_DURATION = 125L;
    private static final long[] VIBRATE_PATTERN_ONERROR = new long[] { // X -- -- X X -- -- XXXXXX
            VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            2 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            2 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            2 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            2 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            6 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
    };
    private static final long[] VIBRATE_PATTERN_DEFAULT_BRIEF = new long[] { // -- --
            2 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            2 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
    };
    private static final long[] VIBRATE_PATTERN_DEFAULT_LONGER = new long[] { // ---- ---- --
            4 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            4 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
            2 * VIBRATE_PULSE_BASE_DURATION,
            VIBRATE_PULSE_BASE_DURATION,
    };

    public static boolean signalStateChangeByVibration(ChameleonScripting.ChameleonScriptInstance.ScriptRuntimeState state) {
        if(!ScriptingConfig.VIBRATE_PHONE_ON_EXIT) {
            return false;
        }
        long[] vibrationPattern;
        switch(state) {
            case INITIALIZED:
                vibrationPattern = VIBRATE_PATTERN_DEFAULT_BRIEF;
            case EXCEPTION:
                vibrationPattern = VIBRATE_PATTERN_ONERROR;
                break;
            case PAUSED:
                vibrationPattern = VIBRATE_PATTERN_DEFAULT_BRIEF;
                break;
            case BREAKPOINT:
                vibrationPattern = VIBRATE_PATTERN_DEFAULT_LONGER;
                break;
            case FINISHED:
            case DONE:
                vibrationPattern = VIBRATE_PATTERN_DEFAULT_LONGER;
                break;
            default:
                return false;
        }
        Vibrator deviceVibrator = (Vibrator) ScriptingConfig.SCRIPTING_CONFIG_ACTIVITY_CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);
        if(deviceVibrator != null && !deviceVibrator.hasVibrator()) {
            return false;
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceVibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1));
        }
        else {
            deviceVibrator.vibrate(vibrationPattern, -1);
        }
        return true;
    }

    public static String rawStringToSpecialCharEncoding(String inputRawMsg) {
        inputRawMsg = inputRawMsg.replaceAll("\\\\n", "\n");
        inputRawMsg = inputRawMsg.replaceAll("\\\\t", "\t");
        inputRawMsg = inputRawMsg.replaceAll("\\\\r", "\r");
        return inputRawMsg;
    }


}
