package com.maxieds.chameleonminilivedebugger;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

public class ApduGUITools {

    private static final String TAG = ApduGUITools.class.getSimpleName();

    public static void apduUpdateCLA(String CLA) {
        ApduUtils.apduTransceiveCmd.CLA = CLA;
        ApduUtils.updateAssembledAPDUCmd();
    }

    public static void apduClearCommand() {
        ApduUtils.apduTransceiveCmd.clear();
        ((LinearLayout) ((ScrollView) ApduUtils.tabView.findViewById(R.id.apduSearchResultsScrollView)).getChildAt(0)).removeAllViewsInLayout();
        ApduUtils.updateAssembledAPDUCmd();
    }

    public static AlertDialog getApduManualDataEntryDialog() {
        AlertDialog.Builder adbuilder = new AlertDialog.Builder(LiveLoggerActivity.getInstance());
        adbuilder.setTitle("Set APDU Command Components: ");
        String instrMsg = LiveLoggerActivity.getInstance().getString(R.string.apduEntryInstructions);
        adbuilder.setMessage(instrMsg);

        EditText apduCmdEntry = new EditText(LiveLoggerActivity.getInstance());
        apduCmdEntry.setHint(ApduUtils.apduTransceiveCmd.assembleAPDUString());
        final EditText apduCmdEntryFinal = apduCmdEntry;
        adbuilder.setView(apduCmdEntryFinal);

        adbuilder.setNegativeButton("Cancel", null);
        adbuilder.setNeutralButton("Parse As Data Only", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dataBytes = apduCmdEntryFinal.getText().toString().toLowerCase();
                dataBytes.replaceAll("[ \n\t\r]*", ""); // remove whitespace
                if (!Utils.stringIsHexadecimal(dataBytes)) {
                    return;
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
                dataBytes.replaceAll("[ \n\t\r]*", ""); // remove whitespace
                if(!Utils.stringIsHexadecimal(dataBytes) || dataBytes.length() < 8) {
                    return;
                }
                ApduUtils.apduTransceiveCmd.CLA = dataBytes.substring(0, 2);
                ApduUtils.apduTransceiveCmd.INS = dataBytes.substring(2, 4);
                ApduUtils.apduTransceiveCmd.P1 = dataBytes.substring(4, 6);
                ApduUtils.apduTransceiveCmd.P2 = dataBytes.substring(6, 8);
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

    public static void sendAPDUToChameleon() {
        String sendBytesStr = ApduUtils.apduTransceiveCmd.getPayloadData();
        String respData = ChameleonIO.getSettingFromDevice("SEND " + sendBytesStr);
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("APDU", "SEND Response:\n" + respData));
        respData = ChameleonIO.getSettingFromDevice("SEND_RAW " + sendBytesStr);
        MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("APDU", "SEND_RAW Response:\n" + respData));
    }

    public static void searchAPDUDatabase(String searchText) {
        LinearLayout layoutList = (LinearLayout) ((ScrollView) ApduUtils.tabView.findViewById(R.id.apduSearchResultsScrollView)).getChildAt(0);
        for(int cmd = 0; cmd < ApduUtils.fullInsList.length; cmd++) {
            String summaryStr = ApduUtils.fullInsList[cmd].getSummary();
            if(summaryStr.toLowerCase(Locale.ENGLISH).contains(searchText)) {
                LinearLayout searchResult = (LinearLayout) LiveLoggerActivity.defaultInflater.inflate(R.layout.apdu_search_item, null);
                String[] cmdDescParts = ApduUtils.fullInsList[cmd].apduCmdDesc.split("[\\(\\)]");
                ((TextView) searchResult.findViewById(R.id.apduCmdDesc)).setText(cmdDescParts[0]);
                ((TextView) searchResult.findViewById(R.id.apduByteData)).setText(summaryStr.toLowerCase().split(" : ")[1]);
                ((Button) searchResult.findViewById(R.id.copyCmdButton)).setTag(Integer.toString(cmd));
                layoutList.addView(searchResult);
            }
        }
        ((TextView) ApduUtils.tabView.findViewById(R.id.apduSearchText)).setHint("Search by Text or Byte Strings ...");
    }

    public static void copyAPDUCommand(int tagIndex) {
        ApduUtils.apduTransceiveCmd = ApduUtils.fullInsList[tagIndex];
        ApduUtils.updateAssembledAPDUCmd();
    }
}
