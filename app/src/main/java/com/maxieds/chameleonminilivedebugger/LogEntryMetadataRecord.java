package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.format.Time;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public class LogEntryMetadataRecord extends LogEntryBase {

    private String recordTitle;
    private String recordText;
    private String recordTimestamp;
    protected TextView tvRecTitle, tvRecData;

    private LinearLayout recordContainer;

    public LogEntryMetadataRecord(LayoutInflater inflater, String title, String text) {
        recordTitle = title;
        recordText = text;
        recordTimestamp = Utils.getTimestamp();
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recordContainer = (LinearLayout) inflater.inflate(R.layout.log_metadata_record, null);
        tvRecTitle = (TextView) recordContainer.findViewById(R.id.record_title_text);
        tvRecTitle.setText(recordTitle + " -- " + recordTimestamp);
        tvRecData = (TextView) recordContainer.findViewById(R.id.record_data_text);
        tvRecData.setText(recordText);
        if(recordText.equals("")) {
            tvRecData.setVisibility(TextView.INVISIBLE);
            tvRecData.setEnabled(false);
            tvRecData.setHeight(0);
        }
    }

    public String writeXMLFragment(int indentLevel) {
        return null;
    }

    public String toString() {
        return recordTitle + ": " + recordText + " (@" + recordTimestamp + ")";
    }

    public View getLayoutContainer() {
        return recordContainer;
    }

    private static Map<String, Integer> prefixIconMap = new HashMap<String, Integer>();
    static {
        prefixIconMap.put("READER", Integer.valueOf(R.drawable.binarymobile24));
        prefixIconMap.put("SNIFFER", R.drawable.binarysearch24);
        prefixIconMap.put("STATUS", R.drawable.msgbubble24);
        prefixIconMap.put("NEW EVENT", R.drawable.statusicon24);
        prefixIconMap.put("ERROR", R.drawable.erroricon24);
        prefixIconMap.put("CARD INFO", R.drawable.cardicon24);
        prefixIconMap.put("LOCAL UID", R.drawable.usericon24);
        prefixIconMap.put("STRENGTH", R.drawable.signalicon24);
        prefixIconMap.put("CHARGING", R.drawable.batteryicon24);
        prefixIconMap.put("FIRMWARE", R.drawable.firmwareicon24);
        prefixIconMap.put("RSSI", R.drawable.voltageicon24);
        prefixIconMap.put("LOCATION", R.drawable.location24);
        prefixIconMap.put("DOOR", R.drawable.dooricon24);
        prefixIconMap.put("VENDING", R.drawable.vending24);
        prefixIconMap.put("PHONE", R.drawable.phone24);
        prefixIconMap.put("ONCLICK", R.drawable.powaction24);
        prefixIconMap.put("IDENTIFY", R.drawable.questionsearch24);
    }

    public static LogEntryMetadataRecord createDefaultEventRecord(String eventID, String eventMsg) {

        if(eventMsg == null)
            eventMsg = "";

        int iconResID = prefixIconMap.get(eventID).intValue();
        LogEntryMetadataRecord eventRecord = new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, eventID, eventMsg);
        eventRecord.tvRecTitle.setCompoundDrawablesWithIntrinsicBounds(iconResID, 0, 0, 0);
        return eventRecord;

    }

}
