package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.format.Time;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public class LogEntryMetadataRecord extends LogEntryBase {

    private String recordTitle;
    private String recordText;
    private String recordTimestamp;

    private LinearLayout recordContainer;

    public LogEntryMetadataRecord(LayoutInflater inflater, String title, String text) {
        recordTitle = title;
        recordText = text;
        recordTimestamp = Utils.getTimestamp();
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recordContainer = (LinearLayout) inflater.inflate(R.layout.log_metadata_record, null);
        TextView tvRecTitle = (TextView) recordContainer.findViewById(R.id.record_title_text);
        tvRecTitle.setText(recordTitle + " -- " + recordTimestamp);
        TextView tvRecData = (TextView) recordContainer.findViewById(R.id.record_data_text);
        tvRecData.setText(recordText);
    }

    public String writeXMLFragment(int indentLevel) {
        return null;
    }

    public String toString() {
        return recordTitle + ": " + recordText + " (" + recordTimestamp + ")";
    }

    public View getLayoutContainer() {
        return recordContainer;
    }





}
