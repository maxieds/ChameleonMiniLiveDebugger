package com.maxieds.chameleonminilivedebugger;

import android.app.Application;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by maxie on 12/31/17.
 */

public class LogEntryUI extends LogEntryBase {

    public static final float ENTRY_UIWEIGHT = (float) 0.10;

    private LinearLayout mainEntryContainer;
    private CheckBox entrySelect;
    private ImageView inoutDirIndicator, apduParseStatus;
    private TextView tvLabel, tvNumBytes, tvNumMillis, tvLogType;
    private TextView tvDataHexBytes, tvDataAscii, tvDataRevhexBytes, tvDataRevhexAscii;

    private int numBytes;
    private int diffTimeMillis;
    private int logType;
    private String logLabel;
    private byte[] entryData;

    public LogEntryUI(Context context, String label, int diffTimeMs, int ltype, byte[] edata) {
        numBytes = edata.length;
        diffTimeMillis = diffTimeMs;
        logType = ltype;
        logLabel = label;
        entryData = edata;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainEntryContainer = (LinearLayout) inflater.inflate(R.layout.log_entry_ui, null);
        configureLayout(mainEntryContainer);
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
        tvNumBytes.setText(String.valueOf(numBytes));
        tvNumMillis = (TextView) mainContainerRef.findViewById(R.id.text_offset_millis);
        tvNumMillis.setText(String.valueOf(diffTimeMillis));
        tvLogType = (TextView) mainContainerRef.findViewById(R.id.text_log_type);
        tvLogType.setText(LogUtils.LogCode.lookupByLogCode(logType).getShortCodeName(logType));
        tvDataHexBytes = (TextView) mainContainerRef.findViewById(R.id.text_logdata_hex);
        tvDataHexBytes.setText(Utils.bytes2Hex(entryData));
        tvDataAscii = (TextView) mainContainerRef.findViewById(R.id.text_logdata_ascii);
        tvDataAscii.setText(Utils.bytes2Ascii(entryData));
        tvDataRevhexBytes = (TextView) mainContainerRef.findViewById(R.id.text_logdata_revhex);
        tvDataRevhexBytes.setText(Utils.bytes2Hex(Utils.reverseBits(entryData)));
        tvDataRevhexAscii = (TextView) mainContainerRef.findViewById(R.id.text_logdata_revhex_ascii);
        tvDataRevhexAscii.setText(Utils.bytes2Hex(Utils.reverseBits(entryData)));
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
