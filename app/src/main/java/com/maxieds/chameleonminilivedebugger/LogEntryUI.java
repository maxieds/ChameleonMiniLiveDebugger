package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView tvLabel, tvNumBytes, tvNumMillis, tvLogType;
    private TextView tvDataHexBytes, tvDataAscii, tvApdu;

    /**
     * Metadata associated with the log entry.
     */
    private int recordID;
    private int numBytes;
    private int diffTimeMillis;
    private int logType;
    private String logLabel;
    private byte[] entryData;

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
            //System.arraycopy(rawLogBytes, 4, payloadBytes, 0, payloadBytes.length);
        }
        else
            System.arraycopy(rawLogBytes, 4, payloadBytes, 0, payloadBytes.length);
        LogEntryUI newLogDataEntry = new LogEntryUI();
        return newLogDataEntry.configureLogEntry(LiveLoggerActivity.defaultContext, logLabel, diffTimeMs, logCode, payloadBytes);
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

    /**
     * The GUI container for the log entry layout.
     * @return LinearLayout widget container
     * @see res/layout/log_entry_ui.xml
     */
    public LinearLayout getMainEntryContainer() {
        return mainEntryContainer;
    }

    /**
     * Populates the inflated layout with log data about the entry.
     * @param mainContainerRef
     */
    public void configureLayout(LinearLayout mainContainerRef) {
        mainEntryContainer = mainContainerRef;
        entrySelect = (CheckBox) mainContainerRef.findViewById(R.id.entrySelect);
        inoutDirIndicator = (ImageView) mainContainerRef.findViewById(R.id.inputDirIndicatorImg);
        apduParseStatus = (ImageView) mainContainerRef.findViewById(R.id.apduParseStatusImg);
        tvLabel = (TextView) mainContainerRef.findViewById(R.id.text_label);
        recordID = ++LiveLoggerActivity.RECORDID;
        tvLabel.setText(logLabel + String.format("%06d", LiveLoggerActivity.RECORDID));
        tvNumBytes = (TextView) mainContainerRef.findViewById(R.id.text_data_num_bytes);
        tvNumBytes.setText(String.valueOf(numBytes) + "B");
        tvNumMillis = (TextView) mainContainerRef.findViewById(R.id.text_offset_millis);
        tvNumMillis.setText((diffTimeMillis >=0 ? "+" : "~") + String.valueOf(abs(diffTimeMillis)) + "ms");
        tvLogType = (TextView) mainContainerRef.findViewById(R.id.text_log_type);
        tvLogType.setText(LogUtils.LogCode.lookupByLogCode(logType).getShortCodeName(logType));
        tvDataHexBytes = (TextView) mainContainerRef.findViewById(R.id.text_logdata_hex);
        tvDataHexBytes.setText(Utils.bytes2Hex(entryData));
        tvDataAscii = (TextView) mainContainerRef.findViewById(R.id.text_logdata_ascii);
        tvDataAscii.setText(Utils.bytes2Ascii(entryData));
        tvApdu = (TextView) mainContainerRef.findViewById(R.id.text_apdu);
        tvApdu.setText(ApduUtils.classifyApdu(entryData));
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
                (byte) LogUtils.LogCode.lookupByLogCode(logType).toInteger(),
                (byte) entryData.length,
                (byte) ((offsetTimeMillis & 0x0000ff00) >>> 8),
                (byte) (offsetTimeMillis & 0x000000ff),
        };
        byte[] fullBytes = new byte[entryData.length + 4];
        System.arraycopy(headerBytes, 0, fullBytes, 0, 4);
        System.arraycopy(entryData, 0, fullBytes, 4, entryData.length);
        return fullBytes;
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
        LogUtils.LogCode logCode = LogUtils.LogCode.lookupByLogCode(logType);
        String recordFmt = String.format("%06d -- %-32s [%-3s bytes] (%s%-6s ms) [%s] {%s}", recordID, logCode.name(),
                String.valueOf(entryData.length), diffTimeMillis >= 0 ? "+" : "~", String.valueOf(abs(diffTimeMillis)),
                Utils.bytes2Hex(entryData), tvApdu.getText().toString());
        return recordFmt;
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