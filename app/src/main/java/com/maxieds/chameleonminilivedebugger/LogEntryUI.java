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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Locale;

import static java.lang.Math.abs;

/**
 * <h1>Log Entry UI Record</h1>
 * Implements a live log data entry.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 * @ref LiveLoggerActivity.logDataEntries
 */
public class LogEntryUI extends LogEntryBase {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();

    /**
     * Timing information for the log.
     */
    public static int curSystickTimestamp = -1; // milliseconds
    public static long lastSystemMillis = System.currentTimeMillis();

    /**
     * GUI display widgets associated with the log entry.
     */
    private LinearLayout mainEntryContainer;
    private CheckBox entrySelect;
    private ImageView inoutDirIndicator, apduParseStatus;
    private TextView tvLabel, tvNumBytes, tvNumMillis, tvLogType, tvEntropy;
    private TextView tvDataHexBytes, tvDataAscii, tvApdu, tvDuplicateCount;

    /**
     * Metadata associated with the log entry.
     */
    private int recordID;
    private int numBytes;
    private int diffTimeMillis;
    private int logType;
    private String logLabel;
    private byte[] entryData;
    private int dataDirection;
    private int numDuplicates;

    /**
     * Effective constructor for the class.
     * @param rawLogBytes
     * @param logLabel
     * @return LogEntryUI new log entry
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static LogEntryUI newInstance(byte[] rawLogBytes, String logLabel) {
        if(rawLogBytes.length < 4) {
            Log.w(TAG, "Invalid log tag data sent.");
            return null;
        }
        int logCode = (int) rawLogBytes[0];
        int payloadNumBytes = (int) rawLogBytes[1];
        int timestamp = (((int) rawLogBytes[2]) << 8) | ((int) rawLogBytes[3]);
        int diffTimeMs = curSystickTimestamp == -1 ? timestamp : timestamp - curSystickTimestamp;
        long systemTimeMillis = System.currentTimeMillis();
        if(diffTimeMs < 0) {
            diffTimeMs = (int) (lastSystemMillis - systemTimeMillis);
        }
        curSystickTimestamp = timestamp;
        lastSystemMillis = systemTimeMillis;
        byte[] payloadBytes = new byte[rawLogBytes.length - 4];
        if(payloadBytes.length < payloadNumBytes) {
            Log.w(TAG, "Invalid payload bytes sent.");
        }
        else
            System.arraycopy(rawLogBytes, 4, payloadBytes, 0, payloadBytes.length);
        LogEntryUI newLogDataEntry = new LogEntryUI();
        return newLogDataEntry.configureLogEntry(LiveLoggerActivity.defaultContext, logLabel, diffTimeMs, ChameleonLogUtils.getDataDirection(logCode), logCode, payloadBytes);
    }

    /**
     * Configures / sets parameters based on the parsed native LIVE logging data.
     * @param context
     * @param label
     * @param diffTimeMs
     * @param ltype
     * @param edata
     * @return LogEntryUI the configured log entry
     * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/Page_Log.html
     */
    public LogEntryUI configureLogEntry(Context context, String label, int diffTimeMs, int dataDir, int ltype, byte[] edata) {
        numBytes = edata.length;
        diffTimeMillis = diffTimeMs;
        dataDirection = dataDir;
        logType = ltype;
        logLabel = label;
        entryData = edata;
        numDuplicates = 0;
        LayoutInflater inflater = LiveLoggerActivity.defaultInflater;
        mainEntryContainer = (LinearLayout) inflater.inflate(R.layout.log_entry_ui, null);
        configureLayout(mainEntryContainer);
        return this;
    }

    /**
     * The GUI container for the log entry layout.
     * @return LinearLayout widget container
     * @see res/layout/log_entry_ui.xml
     */
    public LinearLayout getMainEntryContainer() {
        return mainEntryContainer;
    }

    public View cloneLayoutContainer() {
        LinearLayout mainEntryContainerClone = (LinearLayout) LiveLoggerActivity.defaultInflater.inflate(R.layout.log_entry_ui, null);
        mainEntryContainerClone.setAlpha(LOGENTRY_GUI_ALPHA);
        ImageView inoutDirIndicatorClone = (ImageView) mainEntryContainerClone.findViewById(R.id.inputDirIndicatorImg);
        inoutDirIndicatorClone.setImageDrawable(inoutDirIndicator.getDrawable());
        ImageView apduParseStatusClone = (ImageView) mainEntryContainerClone.findViewById(R.id.apduParseStatusImg);
        apduParseStatusClone.setImageDrawable(apduParseStatusClone.getDrawable());
        TextView tvLabelClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_label);
        tvLabelClone.setText(tvLabel.getText());
        TextView tvNumBytesClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_data_num_bytes);
        tvNumBytesClone.setText(tvNumBytes.getText());
        TextView tvNumMillisClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_offset_millis);
        tvNumMillisClone.setText(tvNumMillis.getText());
        TextView tvLogTypeClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_log_type);
        tvLogTypeClone.setText(tvLogType.getText());
        TextView tvEntropyClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_entropy_compression_ratio);
        tvEntropyClone.setText(tvEntropy.getText());
        TextView tvDataHexBytesClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_logdata_hex);
        tvDataHexBytesClone.setText(tvDataHexBytes.getText());
        TextView tvDataAsciiClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_logdata_ascii);
        tvDataAsciiClone.setText(tvDataAscii.getText());
        TextView tvApduClone = (TextView) mainEntryContainerClone.findViewById(R.id.text_apdu);
        tvApduClone.setText(tvApdu.getText());
        if(tvApduClone.getText().toString().equals("APDU: NONE RECOGNIZED")) {
            tvApduClone.setVisibility(TextView.GONE);
        }
        return mainEntryContainerClone;
    }

    /**
     * Populates the inflated layout with log data about the entry.
     * @param mainContainerRef
     */
    public void configureLayout(LinearLayout mainContainerRef) {
        mainEntryContainer = mainContainerRef;
        mainEntryContainer.setAlpha(LOGENTRY_GUI_ALPHA);
        entrySelect = (CheckBox) mainContainerRef.findViewById(R.id.entrySelect);
        inoutDirIndicator = (ImageView) mainContainerRef.findViewById(R.id.inputDirIndicatorImg);
        inoutDirIndicator.setImageDrawable(LiveLoggerActivity.getLiveLoggerInstance().getResources().getDrawable(getDataDirectionMarker()));
        apduParseStatus = (ImageView) mainContainerRef.findViewById(R.id.apduParseStatusImg);
        tvLabel = (TextView) mainContainerRef.findViewById(R.id.text_label);
        recordID = ++MainActivityLogUtils.RECORDID;
        tvLabel.setText(logLabel + String.format(Locale.ENGLISH, "%06d", MainActivityLogUtils.RECORDID));
        tvNumBytes = (TextView) mainContainerRef.findViewById(R.id.text_data_num_bytes);
        tvNumBytes.setText(String.valueOf(numBytes) + "B");
        tvNumMillis = (TextView) mainContainerRef.findViewById(R.id.text_offset_millis);
        tvNumMillis.setText((diffTimeMillis >=0 ? "+" : "~") + String.valueOf(abs(diffTimeMillis)) + "ms");
        tvLogType = (TextView) mainContainerRef.findViewById(R.id.text_log_type);
        tvLogType.setText(ChameleonLogUtils.LogCode.lookupByLogCode(logType).getShortCodeName(logType));
        tvEntropy = (TextView) mainContainerRef.findViewById(R.id.text_entropy_compression_ratio);
        tvEntropy.setText(String.format(Locale.ENGLISH, "CPR/ENT: %1.4g", Utils.computeByteArrayEntropy(entryData)));
        tvDataHexBytes = (TextView) mainContainerRef.findViewById(R.id.text_logdata_hex);
        tvDataHexBytes.setText(Utils.bytes2Hex(entryData));
        tvDataAscii = (TextView) mainContainerRef.findViewById(R.id.text_logdata_ascii);
        tvDataAscii.setText(Utils.bytes2Ascii(entryData));
        tvApdu = (TextView) mainContainerRef.findViewById(R.id.text_apdu);
        tvApdu.setText(ApduUtils.classifyApdu(entryData));
        tvDuplicateCount = (TextView) mainEntryContainer.findViewById(R.id.text_duplicate_count);
        tvDuplicateCount.setVisibility(View.GONE);
        if(tvApdu.getText().toString().equals("NONE")) {
            tvApdu.setText("APDU: NONE RECOGNIZED");
            tvApdu.setVisibility(TextView.GONE);
        }
        else
            apduParseStatus.setImageDrawable(LiveLoggerActivity.defaultContext.getResources().getDrawable(R.drawable.known16));
    }

    /**
     * Returns the payload bytes associated with the log.
     * @return byte[] data bytes
     * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/Page_Log.html
     */
    public byte[] getEntryData() {
        return entryData;
    }

    /**
     * Returns a String representation of the log payload data bytes.
     * @return String payload data
     * @ref LogEntryUI.getEntryData
     */
    public String getPayloadData() {
        String hexBytes = Utils.bytes2Hex(entryData);
        return hexBytes.replace(" ", "");
    }

    /**
     * Returns the numeric application-local identifier of the log index.
     * @return
     */
    public int getRecordIndex() { return recordID; }

    /**
     * Helper method for determining timing data in the log.
     * @param offsetTimeMillis
     * @return short next offset time
     * @ref ExportTools.writeBinaryLogFile
     */
    public short getNextOffsetTime(short offsetTimeMillis) {
        return (short) (offsetTimeMillis + abs(diffTimeMillis));
    }

    /**
     * In the reverse direction (from parsed log to composite data), returns a packed byte
     * representation of the native log information.
     * @param offsetTimeMillis
     * @return byte[] packaged raw binary log data
     * @url http://rawgit.com/emsec/ChameleonMini/master/Doc/Doxygen/html/Page_Log.html
     * @ref ExportTools.writeBinaryLogFile
     */
    public byte[] packageBinaryLogData(short offsetTimeMillis) {
        byte[] headerBytes = {
                (byte) ChameleonLogUtils.LogCode.lookupByLogCode(logType).toInteger(),
                (byte) entryData.length,
                (byte) ((offsetTimeMillis & 0x0000ff00) >>> 8),
                (byte) (offsetTimeMillis & 0x000000ff),
        };
        byte[] fullBytes = new byte[entryData.length + 4];
        System.arraycopy(headerBytes, 0, fullBytes, 0, 4);
        System.arraycopy(entryData, 0, fullBytes, 4, entryData.length);
        return fullBytes;
    }

    public boolean logEntryDataEquals(byte[] nextLogEntryData) {
        int nextLogType = (int) nextLogEntryData[0];
        if(logType != nextLogType) {
            return false;
        }
        byte[] nextPayloadBytes = new byte[nextLogEntryData.length - 4];
        System.arraycopy(nextLogEntryData, 4, nextPayloadBytes, 0, nextLogEntryData.length - 4);
        return Arrays.equals(entryData, nextPayloadBytes);
    }

    public boolean appendDuplicate(byte offsetTimeMSB, byte offsetTimeLSB) {
        short offsetTimeMillis = (short) ((((short) offsetTimeMSB) << 8) | ((short) offsetTimeLSB));
        return appendDuplicate(offsetTimeMillis);
    }

    public boolean appendDuplicate(short offsetTimeMillis) {

        // Add one and determine how to display the +NUM marker:
        numDuplicates += 1;
        boolean drawCountInHex = Math.log10(numDuplicates) > 5.0 ? true : false;
        String duplicateNumberText = "";
        if(drawCountInHex) {
            duplicateNumberText = String.format(Locale.ENGLISH, "0x%04x", numDuplicates);
        }
        else {
            duplicateNumberText = String.format(Locale.ENGLISH, "%06d", numDuplicates);
        }
        duplicateNumberText += " -- IDENTICAL LOGS";
        tvDuplicateCount.setVisibility(View.VISIBLE);
        tvDuplicateCount.setText(duplicateNumberText);

        // Update the time / ms marker:
        int timestamp = (int) offsetTimeMillis;
        long systemTimeMillis = System.currentTimeMillis();
        diffTimeMillis = curSystickTimestamp == -1 ? timestamp : timestamp - curSystickTimestamp;
        if(diffTimeMillis < 0) {
            diffTimeMillis = (int) (lastSystemMillis - systemTimeMillis);
        }
        curSystickTimestamp = timestamp;
        lastSystemMillis = systemTimeMillis;
        tvNumMillis.setText((diffTimeMillis >=0 ? "+" : "~") + String.valueOf(abs(diffTimeMillis)) + "ms");

        return true;
    }

    /**
     * Stub method.
     * @param indentLevel
     * @return
     */
    @Override
    public String writeXMLFragment(int indentLevel) {
        return null;
    }

    /**
     * String description of the log entry.
     * @return String representation of the object
     */
    @Override
    public String toString() {
        ChameleonLogUtils.LogCode logCode = ChameleonLogUtils.LogCode.lookupByLogCode(logType);
        String recordFmt = String.format(Locale.ENGLISH, "%06d -- %-32s [%-3s bytes] (%s%-6s ms) [%s] {%s}", recordID, logCode.name(),
                String.valueOf(entryData.length), diffTimeMillis >= 0 ? "+" : "~", String.valueOf(abs(diffTimeMillis)),
                Utils.bytes2Hex(entryData), tvApdu.getText().toString());
        return recordFmt;
    }

    public String getLogCodeName() {
        return ChameleonLogUtils.LogCode.lookupByLogCode(logType).name();
    }

    public String getAPDUString() {
        return tvApdu.getText().toString();
    }

    public String getPayloadDataString(boolean byteString) {
        if(byteString) {
            Log.i(TAG, "Returning bytes: " + tvDataHexBytes.getText().toString());
            return tvDataHexBytes.getText().toString();
        }
        else {
            Log.i(TAG, "Returning ascii: " + tvDataAscii.getText().toString());
            return tvDataAscii.getText().toString();
        }
    }

    public int getDataDirectionMarker() {
        if(dataDirection == ChameleonLogUtils.DATADIR_INCOMING)
            return R.drawable.incoming_arrow16;
        else if(dataDirection == ChameleonLogUtils.DATADIR_OUTGOING)
            return R.drawable.outgoing_arrow16;
        else
            return R.drawable.xfer16;
    }

    /**
     * Returns the layout container (LinearLayout object) associated with this log entry.
     * @return (LinearLayout) View
     */
    @Override
    public View getLayoutContainer() {
        return mainEntryContainer;
    }

}