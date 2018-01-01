package com.maxieds.chameleonminilivedebugger;

import android.app.Application;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Created by maxie on 12/31/17.
 */

public class LogEntryUI extends LogEntryBase {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();

    public static final float ENTRY_UIWEIGHT = (float) 0.10;
    public static int curSystickTimestamp = -1; // milliseconds

    private LinearLayout mainEntryContainer;
    private CheckBox entrySelect;
    private ImageView inoutDirIndicator, apduParseStatus;
    private TextView tvLabel, tvNumBytes, tvNumMillis, tvLogType;
    private TextView tvDataHexBytes, tvDataAscii;

    private int numBytes;
    private int diffTimeMillis;
    private int logType;
    private String logLabel;
    private byte[] entryData;

    public static LogEntryUI newInstance(byte[] rawLogBytes, String logLabel) {
        if(rawLogBytes.length < 4) {
            Log.w(TAG, "Invalid log tag data sent.");
            return null;
        }
        int logCode = (int) rawLogBytes[0];
        int payloadNumBytes = (int) rawLogBytes[1];
        int timestamp = (((int) rawLogBytes[2]) << 8) | ((int) rawLogBytes[3]);
        int diffTimeMs = curSystickTimestamp == -1 ? timestamp : timestamp - curSystickTimestamp;
        curSystickTimestamp = timestamp;
        byte[] payloadBytes = new byte[rawLogBytes.length - 4];
        if(payloadBytes.length < payloadNumBytes) {
            Log.w(TAG, "Ihnvalid payload bytes sent.");
        }
        else
            System.arraycopy(rawLogBytes, 4, payloadBytes, 0, payloadBytes.length);
        LogEntryUI newLogDataEntry = new LogEntryUI();
        return newLogDataEntry.configureLogEntry(LiveLoggerActivity.defaultContext, logLabel, diffTimeMs, logCode, payloadBytes);
    }

    public LogEntryUI configureLogEntry(Context context, String label, int diffTimeMs, int ltype, byte[] edata) {
        numBytes = edata.length;
        diffTimeMillis = diffTimeMs;
        logType = ltype;
        logLabel = label;
        entryData = edata;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainEntryContainer = (LinearLayout) inflater.inflate(R.layout.log_entry_ui, null);
        configureLayout(mainEntryContainer);
        return this;
    }

    public LinearLayout getMainEntryContainer() {
        return mainEntryContainer;
    }

    public void configureLayout(LinearLayout mainContainerRef) {
        mainEntryContainer = mainContainerRef;
        entrySelect = (CheckBox) mainContainerRef.findViewById(R.id.entrySelect);
        inoutDirIndicator = (ImageView) mainContainerRef.findViewById(R.id.inputDirIndicatorImg);
        apduParseStatus = (ImageView) mainContainerRef.findViewById(R.id.apduParseStatusImg);
        tvLabel = (TextView) mainContainerRef.findViewById(R.id.text_label);
        tvLabel.setText(logLabel);
        tvNumBytes = (TextView) mainContainerRef.findViewById(R.id.text_data_num_bytes);
        tvNumBytes.setText(String.valueOf(numBytes) + "B");
        tvNumMillis = (TextView) mainContainerRef.findViewById(R.id.text_offset_millis);
        tvNumMillis.setText((diffTimeMillis >=0 ? "+" : "") + String.valueOf(diffTimeMillis) + "ms");
        tvLogType = (TextView) mainContainerRef.findViewById(R.id.text_log_type);
        tvLogType.setText(LogUtils.LogCode.lookupByLogCode(logType).getShortCodeName(logType));
        tvDataHexBytes = (TextView) mainContainerRef.findViewById(R.id.text_logdata_hex);
        tvDataHexBytes.setText(Utils.bytes2Hex(entryData));
        tvDataAscii = (TextView) mainContainerRef.findViewById(R.id.text_logdata_ascii);
        tvDataAscii.setText(Utils.bytes2Ascii(entryData));
    }

    public boolean isSelected() {
        return entrySelect.isSelected();
    }

    @Override
    public String writeXMLFragment(int indentLevel) {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public View getLayoutContainer() {
        return mainEntryContainer;
    }

}
