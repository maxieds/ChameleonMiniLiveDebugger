package com.maxieds.chameleonminilivedebugger;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Log Entry Metadata Record</h1>
 * Implements a stylized status-like meta log entry in the Log tab.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 * @see LiveLoggerActivity.logDataEntries
 */
public class LogEntryMetadataRecord extends LogEntryBase {

    /**
     * Class-specific variables for the log entry.
     */
    private String recordTitle;
    private String recordText;
    private String recordTimestamp;
    protected TextView tvRecTitle, tvRecData;
    private LinearLayout recordContainer;

    /**
     * Constructor.
     * @param inflater
     * @param title Main summary title of the status message / annotation entry.
     * @param text Lower specific subtitle text stored with the entry.
     * @see LiveLoggerActivity.defaultInflater
     * @see LogEntryMetadataRecord.createDefaultEventRecord
     */
    public LogEntryMetadataRecord(LayoutInflater inflater, String title, String text) {
        recordTitle = title;
        recordText = text;
        recordTimestamp = Utils.getTimestamp();
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

    /**
     * Stub method.
     * @param indentLevel
     * @return
     */
    public String writeXMLFragment(int indentLevel) {
        return null;
    }

    /**
     * String description of the log entry.
     * @return String representation of the object
     */
    public String toString() {
        return recordTitle + ": " + recordText + " (@" + recordTimestamp + ")";
    }

    /**
     * Returns the layout container (LinearLayout object) associated with this log entry.
     * @return (LinearLayout) View
     */
    public View getLayoutContainer() {
        return recordContainer;
    }

    /**
     * A map of predefined annotation / status types to their icons shown in the Log tab.
     */
    private static Map<String, Integer> prefixIconMap = new HashMap<String, Integer>();
    static {
        prefixIconMap.put("READER", Integer.valueOf(R.drawable.binarymobile24));
        prefixIconMap.put("SNIFFER", R.drawable.binarysearch24);
        prefixIconMap.put("STATUS", R.drawable.phonebubble24);
        prefixIconMap.put("NEW EVENT", R.drawable.statusicon24);
        prefixIconMap.put("ERROR", R.drawable.erroricon24);
        prefixIconMap.put("CARD INFO", R.drawable.cardicon24);
        prefixIconMap.put("GETUID", R.drawable.usericon24);
        prefixIconMap.put("STRENGTH", R.drawable.signalicon24);
        prefixIconMap.put("CHARGING", R.drawable.batteryicon24);
        prefixIconMap.put("VERSION", R.drawable.firmwareicon24);
        prefixIconMap.put("RSSI", R.drawable.voltageicon24);
        prefixIconMap.put("LOCATION", R.drawable.location24);
        prefixIconMap.put("DOOR", R.drawable.dooricon24);
        prefixIconMap.put("VENDING", R.drawable.vending24);
        prefixIconMap.put("PHONE", R.drawable.phone24);
        prefixIconMap.put("ONCLICK", R.drawable.powaction24);
        prefixIconMap.put("IDENTIFY", R.drawable.find24);
        prefixIconMap.put("PRINT", R.drawable.dotdotdotbubble24);
        prefixIconMap.put("EXPORT", R.drawable.export24);
    }

    /**
     * Creates a new log entry of a predefined type.
     * @param eventID Type of the status message
     * @param eventMsg Description (if any) associated with the message.
     * @return LogEntryMetadataRecord record
     * @see Types of enries: LogEntryMetadataRecord.prefixIconMap
     */
    public static LogEntryMetadataRecord createDefaultEventRecord(String eventID, String eventMsg) {

        if(eventMsg == null)
            eventMsg = "";

        Integer iconResIDInt = prefixIconMap.get(eventID);
        int iconResID = 0;
        if(iconResIDInt == null)
            iconResID = R.drawable.msgbubble24;
        else
            iconResID = iconResIDInt.intValue();
        LogEntryMetadataRecord eventRecord = new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, eventID, eventMsg);
        eventRecord.tvRecTitle.setCompoundDrawablesWithIntrinsicBounds(iconResID, 0, 0, 0);
        return eventRecord;

    }

}