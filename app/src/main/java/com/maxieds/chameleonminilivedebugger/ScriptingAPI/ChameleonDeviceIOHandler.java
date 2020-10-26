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

import com.maxieds.chameleonminilivedebugger.ChameleonIO;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariable;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingTypes.ScriptVariableArrayMap;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChameleonDeviceIOHandler {

    private static final String TAG = ChameleonDeviceIOHandler.class.getSimpleName();

    // notify the main serial receiver to give all its bytes to us ...
    // run this in a background thread ???
    // rework the ChameleonIO.getSettingFromDevice(...) implementation for uses here ???
    public static void handleChameleonSerialExchange(byte[] dataBytes) {
    }

    // Named fields include:
    // ->cmdName
    // ->respCode
    // ->respText
    // ->data (possibly empty)
    // ->isError
    // ->isTimeout
    public static ScriptVariable parseChameleonCommandResponse(String cmdData, String response, boolean isTimeout) {

        ScriptVariable cmdRespVar = new ScriptVariableArrayMap();
        Pattern cmdNamePattern = Pattern.compile("\\([a-zA-Z0-9]+\\)[=\\? ]");
        Matcher cmdNameMatcher = cmdNamePattern.matcher(cmdData);
        String cmdName = cmdData;
        if(cmdNameMatcher.find()) {
            cmdName = cmdNameMatcher.group();
        }
        cmdRespVar.setValueAt("cmdName", ScriptVariable.newInstance().set(cmdName));

        try {
            String[] splitRespData = response.split("[\n\r\t][\n\r\t]+");
            String[] cmdRespText = splitRespData[0].split(":");
            int respCode = Integer.parseInt(cmdRespText[0], 10);
            cmdRespVar.setValueAt("respCode", ScriptVariable.newInstance().set(respCode));
            cmdRespVar.setValueAt("respText", ScriptVariable.newInstance().set(cmdRespText[1]));
            if (splitRespData.length > 1) {
                String[] dataArr = new String[splitRespData.length - 1];
                System.arraycopy(splitRespData, 1, dataArr, 0, splitRespData.length - 1);
                String dataValue = StringUtils.join(dataArr, "\n");
                cmdRespVar.setValueAt("data", ScriptVariable.newInstance().set(dataValue));
            }
            ChameleonIO.SerialRespCode chameleonRespCode = ChameleonIO.SerialRespCode.lookupByResponseCode(respCode);
            if (chameleonRespCode == ChameleonIO.SerialRespCode.OK ||
                    chameleonRespCode == ChameleonIO.SerialRespCode.OK_WITH_TEXT ||
                    chameleonRespCode == ChameleonIO.SerialRespCode.TRUE) {
                cmdRespVar.setValueAt("isError", ScriptVariable.newInstance().set(false));
            } else {
                cmdRespVar.setValueAt("isError", ScriptVariable.newInstance().set(true));
            }
            if (isTimeout || chameleonRespCode == ChameleonIO.SerialRespCode.TIMEOUT) {
                cmdRespVar.setValueAt("isTimeout", ScriptVariable.newInstance().set(true));
            } else {
                cmdRespVar.setValueAt("isTimeout", ScriptVariable.newInstance().set(false));
            }
            return cmdRespVar;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

}
