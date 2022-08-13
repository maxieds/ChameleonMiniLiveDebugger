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

import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class ApduGUITools {

    private static final String TAG = ApduGUITools.class.getSimpleName();

    public static void apduUpdateCLA(String CLA) {
        ApduUtils.apduTransceiveCmd.CLA = CLA;
        ApduUtils.updateAssembledAPDUCmd();
    }

    public static void apduClearCommand() {
        try {
            ((LinearLayout) ((ScrollView) ApduUtils.tabView.findViewById(R.id.apduSearchResultsScrollView)).getChildAt(0)).removeAllViewsInLayout();
        } catch(NullPointerException npe) {
            AndroidLogger.printStackTrace(npe);
            return;
        }
        ApduUtils.apduTransceiveCmd.clear();
        ApduUtils.updateAssembledAPDUCmd();
    }

    public static AlertDialog getApduManualDataEntryDialog() {
        AlertDialog.Builder adbuilder = new AlertDialog.Builder(LiveLoggerActivity.getLiveLoggerInstance());
        adbuilder.setTitle("Set APDU Command Components: ");
        String instrMsg = LiveLoggerActivity.getLiveLoggerInstance().getString(R.string.apduEntryInstructions);
        adbuilder.setMessage(instrMsg);

        EditText apduCmdEntry = new EditText(LiveLoggerActivity.getLiveLoggerInstance());
        apduCmdEntry.setHint("xx|xx|xx|xx|data-bytes");
        final EditText apduCmdEntryFinal = apduCmdEntry;
        adbuilder.setView(apduCmdEntryFinal);

        adbuilder.setNegativeButton("Cancel", null);
        adbuilder.setNeutralButton("Parse As Data Only", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dataBytes = apduCmdEntryFinal.getText().toString().toLowerCase();
                dataBytes.replaceAll("[ \n\t\r|]*", ""); // remove whitespace
                if (dataBytes.length() < 8) {
                    return;
                }
                if(!Utils.stringIsHexadecimal(dataBytes.substring(0, 2))) {
                    dataBytes = ApduUtils.apduTransceiveCmd.CLA + dataBytes.substring(2);
                }
                if(!Utils.stringIsHexadecimal(dataBytes.substring(2, 4))) {
                    dataBytes = dataBytes.substring(0, 2) + ApduUtils.apduTransceiveCmd.INS + dataBytes.substring(4);
                }
                if(!Utils.stringIsHexadecimal(dataBytes.substring(2, 4))) {
                    dataBytes = dataBytes.substring(0, 4) + ApduUtils.apduTransceiveCmd.P1 + dataBytes.substring(6);
                }
                if(!Utils.stringIsHexadecimal(dataBytes.substring(2, 4))) {
                    dataBytes = dataBytes.substring(0, 6) + ApduUtils.apduTransceiveCmd.P2;
                }
                ApduUtils.apduTransceiveCmd.setPayloadData(dataBytes);
                ApduUtils.apduTransceiveCmd.computeLELCBytes();
                ApduUtils.updateAssembledAPDUCmd();
                dialog.dismiss();
            }
        });
        adbuilder.setPositiveButton("Parse Input", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dataBytes = apduCmdEntryFinal.getText().toString().toLowerCase();
                dataBytes.replaceAll("[ \n\t\r|]*", ""); // remove whitespace
                if(dataBytes.length() < 8) {
                    return;
                }
                ApduUtils.apduTransceiveCmd.CLA = dataBytes.substring(0, 2);
                ApduUtils.apduTransceiveCmd.INS = dataBytes.substring(2, 4);
                ApduUtils.apduTransceiveCmd.P1 = dataBytes.substring(4, 6);
                ApduUtils.apduTransceiveCmd.P2 = dataBytes.substring(6, 8);
                if(!Utils.stringIsHexadecimal(dataBytes.substring(0, 2))) {
                    dataBytes = ApduUtils.apduTransceiveCmd.CLA + dataBytes.substring(2);
                }
                if(!Utils.stringIsHexadecimal(dataBytes.substring(2, 4))) {
                    dataBytes = dataBytes.substring(0, 2) + ApduUtils.apduTransceiveCmd.INS + dataBytes.substring(4);
                }
                if(!Utils.stringIsHexadecimal(dataBytes.substring(2, 4))) {
                    dataBytes = dataBytes.substring(0, 4) + ApduUtils.apduTransceiveCmd.P1 + dataBytes.substring(6);
                }
                if(!Utils.stringIsHexadecimal(dataBytes.substring(2, 4))) {
                    dataBytes = dataBytes.substring(0, 6) + ApduUtils.apduTransceiveCmd.P2;
                }
                if(dataBytes.length() >= 9) {
                    ApduUtils.apduTransceiveCmd.setPayloadData(dataBytes.substring(8, dataBytes.length()));
                }
                ApduUtils.apduTransceiveCmd.computeLELCBytes();
                ApduUtils.updateAssembledAPDUCmd();
                dialog.dismiss();
            }
        });
        return adbuilder.create();
    }

    public static void sendAPDUToChameleon(String sendMode, boolean sendRaw) {
        String sendCmd = sendRaw ? "SEND_RAW " : "SEND ";
        String sendBytesStr = ApduUtils.apduTransceiveCmd.getPayloadData(sendMode);
        String respData = ChameleonIO.getSettingFromDevice(sendCmd + sendBytesStr);
        GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("APDU TRANSFER", sendCmd + "Response:\n" + respData));
    }

    public static void searchAPDUDatabase(String searchText) {
        LinearLayout layoutList = (LinearLayout) ((ScrollView) ApduUtils.tabView.findViewById(R.id.apduSearchResultsScrollView)).getChildAt(0);
        for(int cmd = 0; cmd < ApduUtils.fullInsList.length; cmd++) {
            String summaryStr = ApduUtils.fullInsList[cmd].getSummary();
            if(summaryStr.toLowerCase(BuildConfig.DEFAULT_LOCALE).contains(searchText)) {
                LinearLayout searchResult = (LinearLayout) LiveLoggerActivity.defaultInflater.inflate(R.layout.apdu_search_item, null);
                if(searchResult != null) {
                    String[] cmdDescParts = ApduUtils.fullInsList[cmd].apduCmdDesc.split("[\\(\\)]");
                    ((TextView) searchResult.findViewById(R.id.apduCmdDesc)).setText(cmdDescParts[0]);
                    ((TextView) searchResult.findViewById(R.id.apduByteData)).setText(summaryStr.toLowerCase().split(" : ")[1]);
                    ((Button) searchResult.findViewById(R.id.copyCmdButton)).setTag(Integer.toString(cmd));
                    layoutList.addView(searchResult);
                }
            }
        }
        TextView apduSearchText = (TextView) ApduUtils.tabView.findViewById(R.id.apduSearchText);
        if(apduSearchText != null) {
            apduSearchText.setHint("Search by Text or Byte Strings ...");
        }
    }

    public static void copyAPDUCommand(int tagIndex) {
        ApduUtils.apduTransceiveCmd = ApduUtils.fullInsList[tagIndex];
        ApduUtils.updateAssembledAPDUCmd();
    }
}
