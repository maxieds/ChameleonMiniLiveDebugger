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

import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <h1>Log Entry Metadata Record</h1>
 * Implements a stylized status-like meta log entry in the Log tab.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 * @ref LiveLoggerActivity.logDataEntries
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
     * @ref LiveLoggerActivity.defaultInflater
     * @ref LogEntryMetadataRecord.createDefaultEventRecord
     */
    public LogEntryMetadataRecord(LayoutInflater inflater, String title, String text) {
        recordTitle = title;
        recordText = text;
        recordTimestamp = Utils.getTimestamp();
        recordContainer = (LinearLayout) inflater.inflate(R.layout.log_metadata_record, null);
        recordContainer.setAlpha(LOGENTRY_GUI_ALPHA);
        tvRecTitle = (TextView) recordContainer.findViewById(R.id.record_title_text);
        tvRecTitle.setText(recordTitle + " -- " + recordTimestamp);
        tvRecData = (TextView) recordContainer.findViewById(R.id.record_data_text);
        tvRecData.setText(recordText);
        tvRecData.setAlpha(LOGENTRY_GUI_ALPHA);
        if(recordText.equals("")) {
            tvRecData.setVisibility(TextView.INVISIBLE);
            tvRecData.setEnabled(false);
            tvRecData.setHeight(0);
        }
    }

    public View cloneLayoutContainer() {
        LinearLayout recordContainerClone = (LinearLayout) LiveLoggerActivity.defaultInflater.inflate(R.layout.log_metadata_record, null);
        TextView tvRecTitleClone = (TextView) recordContainerClone.findViewById(R.id.record_title_text);
        tvRecTitleClone.setText(tvRecTitle.getText());
        tvRecTitleClone.setCompoundDrawables(tvRecTitle.getCompoundDrawables()[0], null, null, null);
        TextView tvRecDataClone = (TextView) recordContainerClone.findViewById(R.id.record_data_text);
        tvRecDataClone.setText(recordText);
        if(recordText.equals("")) {
            tvRecDataClone.setVisibility(TextView.INVISIBLE);
            tvRecDataClone.setEnabled(false);
            tvRecDataClone.setHeight(0);
        }
        return recordContainerClone;
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
        prefixIconMap.put("WELCOME", R.drawable.welcome_icon24);
        prefixIconMap.put("DISCLAIMER", R.drawable.disclaimer_stmt_icon24);
        prefixIconMap.put("READER", Integer.valueOf(R.drawable.binarymobile24));
        prefixIconMap.put("SNIFFER", R.drawable.binarysearch24);
        prefixIconMap.put("STATUS", R.drawable.phonebubble24);
        prefixIconMap.put("NEW EVENT", R.drawable.statusicon24);
        prefixIconMap.put("ERROR", R.drawable.erroricon24);
        prefixIconMap.put("UNRECOGNIZED EXCEPTION", R.drawable.bug);
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
        prefixIconMap.put("SEARCH", R.drawable.searchicon24);
        prefixIconMap.put("THEME", R.drawable.themecheck24);
        prefixIconMap.put("DUMP_MFU", R.drawable.phonebubble24);
        prefixIconMap.put("APDU TRANSFER", R.drawable.sendarrow24v2);
        prefixIconMap.put("CLONE", R.drawable.clone);
        prefixIconMap.put("SET ACTIVITY", R.drawable.activity_annotation24);
        prefixIconMap.put("ON FOOT", R.drawable.onfoot_annotation24);
        prefixIconMap.put("HOME", R.drawable.home_annotation24);
        prefixIconMap.put("WORK", R.drawable.work_annotation24);
        prefixIconMap.put("DEVICE PROFILE", R.drawable.chameleon_device_profile_annotation24);
        prefixIconMap.put("TODO LIST", R.drawable.todo26);
    }

    /**
     * Creates a new log entry of a predefined type.
     * @param eventID Type of the status message
     * @param eventMsg Description (if any) associated with the message.
     * @return LogEntryMetadataRecord record
     * @ref Types of enries: LogEntryMetadataRecord.prefixIconMap
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

        // some metadata types require preprocessing of the eventMsg:
        if(eventID.equals("LOCATION")) {
            String[] locCoords = Utils.getGPSLocationCoordinates();
            String locationDetails = "";
            locationDetails += String.format(Locale.ENGLISH, "Coordinates: (%s lat, %s long)\n", locCoords[0], locCoords[1]);
            try {
                Geocoder gc = new Geocoder(LiveLoggerActivity.getInstance(), Locale.getDefault());
                List<Address> gcAddrs = gc.getFromLocation(Double.parseDouble(locCoords[0]), Double.parseDouble(locCoords[0]), 1);
                if (gcAddrs != null && gcAddrs.size() > 0) {
                    locationDetails += String.format(Locale.ENGLISH, "Address: %s, %s, %s %s\n",
                                                     gcAddrs.get(0).getAddressLine(0), gcAddrs.get(0).getLocality(),
                                                     gcAddrs.get(0).getAdminArea(), gcAddrs.get(0).getPostalCode(),
                                                     gcAddrs.get(0).getCountryName());
                    String knownLocID = gcAddrs.get(0).getFeatureName();
                    if(knownLocID != null) {
                        locationDetails += String.format(Locale.ENGLISH, "Known ID: %s", knownLocID);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(eventMsg.length() > 0) {
                eventMsg = locationDetails + "\n\n" + eventMsg;
            }
        }
        else if(eventID.equals("TODO LIST")) {
            String[] listLines = eventMsg.split("\n");
            eventMsg = "";
            for(int lineNo = 0; lineNo < listLines.length; lineNo++) {
                String lineData = listLines[lineNo];
                eventMsg += " • " + lineData + "\n";
            }
        }

        LogEntryMetadataRecord eventRecord = new LogEntryMetadataRecord(LiveLoggerActivity.defaultInflater, eventID, eventMsg);
        eventRecord.tvRecTitle.setCompoundDrawablesWithIntrinsicBounds(iconResID, 0, 0, 0);
        return eventRecord;

    }

}