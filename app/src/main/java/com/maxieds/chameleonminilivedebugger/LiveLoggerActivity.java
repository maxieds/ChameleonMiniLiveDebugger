package com.maxieds.chameleonminilivedebugger;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;

/**
 * <h1>Live Logger Activity</h1>
 * Implementation of the main running activity in the application.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public class LiveLoggerActivity extends AppCompatActivity {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();

    /**
     * We assume there is only one instance of the singleton activity running at a time.
     */
    public static LiveLoggerActivity runningActivity;

    /**
     * Static variables used across classes.
     */
    public static LayoutInflater defaultInflater;
    public static Context defaultContext;
    public static LinearLayout logDataFeed;
    public static List<LogEntryBase> logDataEntries = new ArrayList<LogEntryBase>();
    public static int RECORDID = 0;
    public static boolean logDataFeedConfigured = false;
    public static SpinnerAdapter spinnerRButtonAdapter;
    public static SpinnerAdapter spinnerRButtonLongAdapter;
    public static SpinnerAdapter spinnerLButtonAdapter;
    public static SpinnerAdapter spinnerLButtonLongAdapter;
    public static SpinnerAdapter spinnerLEDRedAdapter;
    public static SpinnerAdapter spinnerLEDGreenAdapter;
    public static SpinnerAdapter spinnerLogModeAdapter;
    public static SpinnerAdapter spinnerCmdShellAdapter;
    public static boolean userIsScrolling = false;
    private static ViewPager viewPager;
    private static int selectedTab = TAB_LOG;

    /**
     * Configuration of the USB serial port.
     */
    public static UsbSerialDevice serialPort;
    public static final Semaphore serialPortLock = new Semaphore(1, true);

    /**
     * Appends a new log to the logging interface tab.
     * @param logEntry
     * @see LogEntryUI
     * @see LogEntryMetadataRecord
     */
    public static void appendNewLog(LogEntryBase logEntry) {
        if(LiveLoggerActivity.selectedTab != TAB_LOG) {
            if(logEntry instanceof LogEntryUI)
                runningActivity.setStatusIcon(R.id.statusIconNewXFer, R.drawable.statusxfer16);
            else
                runningActivity.setStatusIcon(R.id.statusIconNewMsg, R.drawable.statusnewmsg16);
        }
        logDataFeed.addView(logEntry.getLayoutContainer());
        logDataEntries.add(logEntry);
    }

    /**
     * Sets one of the small status icons indicated at the top of the activity window.
     * @param iconID
     * @param iconDrawable
     * @ref R.id.statusIconUSB
     * @ref R.id.statusIconUlDl
     * @ref R.id.statusIconNewMsg
     * @ref R.id.statusIconNewXFer
     */
    public void setStatusIcon(int iconID, int iconDrawable) {
        ((ImageView) findViewById(iconID)).setAlpha(255);
        ((ImageView) findViewById(iconID)).setImageDrawable(getResources().getDrawable(iconDrawable));
    }

    /**
     * Clears the corresponding status icon indicated at the top of the activity window.
     * @param iconID
     * @ref R.id.statusIconUSB
     * @ref R.id.statusIconUlDl
     * @ref R.id.statusIconNewMsg
     * @ref R.id.statusIconNewXFer
     */
    public void clearStatusIcon(int iconID) {
        ((ImageView) findViewById(iconID)).setAlpha(0);
    }

    /**
     * Initializes the activity state and variables.
     * Called when the activity is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // fix bug where the tabs are blank when the application is relaunched:
        super.onCreate(savedInstanceState);
        if(!isTaskRoot()) {
            Log.w(TAG, "ReLaunch Intent Action: " + getIntent().getAction());
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intentAction != null && (intentAction.equals(UsbManager.ACTION_USB_DEVICE_DETACHED) || intentAction.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))) {
                Log.w(TAG, "onCreate(): Main Activity is not the root.  Finishing Main Activity instead of re-launching.");
                finish();
                LiveLoggerActivity.runningActivity.onNewIntent(intent);
                return;
            }
        }

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        runningActivity = this;
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_live_logger);

        logDataFeed = new LinearLayout(getApplicationContext());
        defaultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        defaultContext = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle("Portable logging interface v" + String.valueOf(BuildConfig.VERSION_NAME) + "-" + BuildConfig.BUILD_TYPE);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.chameleonlogo24);
        actionBar.setDisplayShowHomeEnabled(true);
        clearStatusIcon(R.id.statusIconUlDl);

        viewPager = (ViewPager) findViewById(R.id.tab_pager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this));
        viewPager.setOffscreenPageLimit(TabFragmentPagerAdapter.TAB_COUNT - 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                LiveLoggerActivity.selectedTab = position;
                switch (position) {
                    case TAB_LOG:
                        LiveLoggerActivity.runningActivity.clearStatusIcon(R.id.statusIconNewMsg);
                        LiveLoggerActivity.runningActivity.clearStatusIcon(R.id.statusIconNewXFer);
                        LiveLoggerActivity.runningActivity.clearStatusIcon(R.id.statusIconUlDl);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // the view pager hides the tab icons by default, so we reset them:
        tabLayout.getTabAt(TAB_LOG).setIcon(R.drawable.nfc24v1);
        tabLayout.getTabAt(TAB_TOOLS).setIcon(R.drawable.tools24);
        tabLayout.getTabAt(TAB_EXPORT).setIcon(R.drawable.insertbinary24);
        tabLayout.getTabAt(TAB_LOG_TOOLS).setIcon(R.drawable.logtools24);

        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.INTERNET",
                "com.android.example.USB_PERMISSION"
        };
        if(android.os.Build.VERSION.SDK_INT >= 23)
            requestPermissions(permissions, 200);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // keep app from crashing when the screen rotates

        serialPort = configureSerialPort(null, usbReaderCallback);
        if(serialPort != null)
            ChameleonIO.deviceStatus.updateAllStatusAndPost(true);

        BroadcastReceiver usbActionReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() != null && (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) || intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED))) {
                    onNewIntent(intent);
                }
            }
        };
        IntentFilter usbActionFilter = new IntentFilter();
        usbActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbActionReceiver, usbActionFilter);

        clearStatusIcon(R.id.statusIconNewMsg);
        clearStatusIcon(R.id.statusIconNewXFer);

    }

    /**
     * Handles newly attached / detached USB devices.
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent == null)
            return;
        else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
            ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
            closeSerialPort(serialPort);
            serialPort = configureSerialPort(null, usbReaderCallback);
            LiveLoggerActivity.runningActivity.actionButtonRestorePeripheralDefaults(null);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
        }
        else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
            ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
            if(ChameleonIO.WAITING_FOR_RESPONSE)
                ChameleonIO.WAITING_FOR_RESPONSE = false;
            closeSerialPort(serialPort);
        }
    }

    /**
     * Queries the Chameleon device with the query command and returns its response
     * (sans the preceeding ascii status code).
     * @param cmPort
     * @param query
     * @return String device response
     * @ref ChameleonIO.DEVICE_RESPONSE
     * @ref ChameleonIO.DEVICE_RESPONSE_CODE
     * @ref LiveLoggerActivity.usbReaderCallback
     */
    public static String getSettingFromDevice(UsbSerialDevice cmPort, String query) {
        ChameleonIO.WAITING_FOR_RESPONSE = true;
        ChameleonIO.DEVICE_RESPONSE = "0";
        if(cmPort == null)
            return ChameleonIO.DEVICE_RESPONSE;
        ChameleonIO.SerialRespCode rcode = ChameleonIO.executeChameleonMiniCommand(cmPort, query, ChameleonIO.TIMEOUT);
        for(int i = 0; i < ChameleonIO.TIMEOUT / 50; i++) {
            if(!ChameleonIO.WAITING_FOR_RESPONSE)
                break;
            try {
                Thread.sleep(50);
            } catch(InterruptedException ie) {
                ChameleonIO.WAITING_FOR_RESPONSE = false;
                break;
            }
        }
        return ChameleonIO.DEVICE_RESPONSE;
    }

    /**
     * Establishes the connection between the application and the Chameleon device.
     * @param serialPort
     * @param readerCallback
     * @return the configured serial port (or null on error)
     */
    public UsbSerialDevice configureSerialPort(UsbSerialDevice serialPort, UsbSerialInterface.UsbReadCallback readerCallback) {

        if(serialPort != null)
            closeSerialPort(serialPort);

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbDevice device = null;
        UsbDeviceConnection connection = null;
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if(usbDevices != null && !usbDevices.isEmpty()) {
            for(Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                if(device == null)
                    continue;
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if(deviceVID == ChameleonIO.CMUSB_VENDORID && devicePID == ChameleonIO.CMUSB_PRODUCTID) {
                    connection = usbManager.openDevice(device);
                    break;
                }
            }
        }
        if(device == null || connection == null) {
            appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS: ", "Connection to device unavailable."));
            serialPort = null;
            setStatusIcon(R.id.statusIconUSB, R.drawable.usbdisconnected16);
            return serialPort;
        }
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if(serialPort != null && serialPort.open()) {
            //serialPort.setBaudRate(115200);
            serialPort.setBaudRate(256000);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serialPort.read(readerCallback);
        }
        else {
            appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB ERROR: ", "Unable to configure serial device."));
            serialPort = null;
            setStatusIcon(R.id.statusIconUSB, R.drawable.usbdisconnected16);
            return serialPort;
        }

        ChameleonIO.setLoggerConfigMode(serialPort, ChameleonIO.TIMEOUT);
        //ChameleonIO.setReaderConfigMode(serialPort, ChameleonIO.TIMEOUT);
        ChameleonIO.enableLiveDebugging(serialPort, ChameleonIO.TIMEOUT);
        ChameleonIO.PAUSED = false;
        appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS: ", "Successfully configured the device in passive logging mode."));
        setStatusIcon(R.id.statusIconUSB, R.drawable.usbconnected16);
        return serialPort;

    }

    /**
     * Closes the connection between the application and the Chameleon device.
     * @param serialPort
     * @return boolean success of operation (true)
     */
    public boolean closeSerialPort(UsbSerialDevice serialPort) {
        if(serialPort != null)
            serialPort.close();
        ChameleonIO.PAUSED = true;
        ExportTools.EOT = true;
        ExportTools.transmissionErrorOccurred = true;
        ChameleonIO.DOWNLOAD = false;
        ChameleonIO.WAITING_FOR_XMODEM = false;
        setStatusIcon(R.id.statusIconUSB, R.drawable.usbdisconnected16);
        return true;
    }

    /**
     * Sets up the handling of the serial data responses received from the device
     * (command responses and spontaneous LIVE log data).
     */
    public UsbSerialInterface.UsbReadCallback usbReaderCallback = new UsbSerialInterface.UsbReadCallback() {
        // this is what's going to get called when the LIVE config spontaneously prints its log data to console:
        @Override
        public void onReceivedData(byte[] liveLogData) {
            //Log.i(TAG, "USBReaderCallback Received Data: " + Utils.bytes2Hex(liveLogData));
            //Log.i(TAG, "    => " + Utils.bytes2Ascii(liveLogData));
            if(ChameleonIO.PAUSED) {
                return;
            }
            else if(ChameleonIO.DOWNLOAD) {
                Log.i(TAG, "USBReaderCallback / DOWNLOAD");
                ExportTools.performXModemSerialDownload(liveLogData);
            }
            else if(ChameleonIO.UPLOAD) {
                //Log.i(TAG, "USBReaderCallback / UPLOAD");
                ExportTools.performXModemSerialUpload(liveLogData);
            }
            else if(ChameleonIO.WAITING_FOR_XMODEM) {
                //Log.i(TAG, "USBReaderCallback / WAITING_FOR_XMODEM");
                String strLogData = new String(liveLogData);
                if(strLogData.length() >= 11 && strLogData.substring(0, 11).equals("110:WAITING")) {
                    ChameleonIO.WAITING_FOR_XMODEM = false;
                }
            }
            else if(ChameleonIO.WAITING_FOR_RESPONSE && ChameleonIO.isCommandResponse(liveLogData)) {
                String strLogData = new String(liveLogData);
                //Log.i(TAG, strLogData);
                ChameleonIO.DEVICE_RESPONSE_CODE = strLogData.split("[\n\r]+")[0];
                ChameleonIO.DEVICE_RESPONSE = strLogData.replace(ChameleonIO.DEVICE_RESPONSE_CODE, "").replaceAll("[\n\r]*", "");
                if(ChameleonIO.EXPECTING_BINARY_DATA) {
                    int binaryBufSize = liveLogData.length - ChameleonIO.DEVICE_RESPONSE_CODE.length() - 2;
                    ChameleonIO.DEVICE_RESPONSE_BINARY = new byte[binaryBufSize];
                    System.arraycopy(liveLogData, liveLogData.length - binaryBufSize, ChameleonIO.DEVICE_RESPONSE_BINARY, 0, binaryBufSize);
                    ChameleonIO.EXPECTING_BINARY_DATA = false;
                }
                ChameleonIO.WAITING_FOR_RESPONSE = false;
                return;
            }
            final LogEntryUI nextLogEntry = LogEntryUI.newInstance(liveLogData, "");
            if(nextLogEntry != null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        appendNewLog(nextLogEntry);
                    }
                });
            }
        }
    };

    /**
     * Exits the application.
     * @param view
     * @see res/layout/activity_live_logger.xml
     */
    public void actionButtonExit(View view) {
        ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
        closeSerialPort(serialPort);
        finish();
    }

    /**
     * Queries and restores the current defaults of the device peripheral actions indicated in the
     * Tools Menu spinners.
     * @param view
     * @see res/layout/tools_menu_tab.xml
     */
    public void actionButtonRestorePeripheralDefaults(View view) {
            if (LiveLoggerActivity.serialPort != null) {
                // next, query the defaults from the device to get accurate settings (if the device is connected):
                int[] spinnerIDs = {
                        R.id.RButtonSpinner,
                        R.id.RButtonLongSpinner,
                        R.id.LButtonSpinner,
                        R.id.LButtonLongSpinner,
                        R.id.LEDRedSpinner,
                        R.id.LEDGreenSpinner
                };
                String[] queryCmds = {
                        "RBUTTON?",
                        "RBUTTON_LONG?",
                        "LBUTTON?",
                        "LBUTTON_LONG?",
                        "LEDRED?",
                        "LEDGREEN?"
                };
                for (int i = 0; i < spinnerIDs.length; i++) {
                    Log.i(TAG, queryCmds[i]);
                    Spinner curSpinner = (Spinner) LiveLoggerActivity.runningActivity.findViewById(spinnerIDs[i]);
                    String deviceSetting = getSettingFromDevice(LiveLoggerActivity.serialPort, queryCmds[i]);
                    curSpinner.setSelection(((ArrayAdapter<String>) curSpinner.getAdapter()).getPosition(deviceSetting));
                }
        }

    }

    /**
     * Clears the text appended to certain commands run from the Tools Menu.
     * @param view
     * @ref R.id.userInputFormattedBytes
     * @see res/layout/tools_menu_tab.xml
     */
    public void actionButtonClearUserText(View view) {
        TextView userInputText = (TextView) findViewById(R.id.userInputFormattedBytes);
        userInputText.setText("");
        userInputText.setHint("01 23 45 67 89 ab cd ef");
    }

    /**
     * Manual refreshing of the device status settings requested by the user on button press at the
     * top right (second rightmost button) of the activity window.
     * @param view
     */
    public void actionButtonRefreshDeviceStatus(View view) {
        ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
    }

    /**
     * Clears all logging data from the Log tab.
     * @param view
     */
    public void actionButtonClearAllLogs(View view) {
        if(RECORDID > 0) {
            logDataEntries.clear();
            RECORDID = 0;
            logDataFeed.removeAllViewsInLayout();
        }
    }

    /**
     * Removes repeated log entries in sequential order in the logging tab.
     * Useful for pretty-fying / cleaning up the log entries when a device posts repeated
     * APDU command requests, or zero bits.
     * @param view
     */
    public void actionButtonCollapseSimilar(View view) {
        if(RECORDID == 0)
            return;
        byte[] curBits = null;
        boolean newBits = true;
        for(int v = 0; v < logDataEntries.size(); v++) {
            LogEntryBase lde = logDataEntries.get(v);
            if(lde instanceof LogEntryMetadataRecord) {
                newBits = true;
                continue;
            }
            else if(lde instanceof LogEntryUI && newBits) {
                byte[] nextDataPattern = ((LogEntryUI) lde).getEntryData();
                curBits = new byte[nextDataPattern.length];
                System.arraycopy(nextDataPattern, 0, curBits, 0, nextDataPattern.length);
                newBits = false;
            }
            else if(Arrays.equals(curBits, ((LogEntryUI) lde).getEntryData())) {
                logDataFeed.getChildAt(v).setVisibility(LinearLayout.GONE);
            }
            else {
                newBits = true;
            }
        }
    }

    /**
     * Handles button presses for most of the commands implemented in the Tools Menu.
     * @param view calling Button
     */
    public void actionButtonCreateNewEvent(View view) {
        String createCmd = ((Button) view).getText().toString();
        String msgParam = "";
        if(createCmd.equals("READER")) {
            ChameleonIO.setReaderConfigMode(serialPort, ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("SNIFFER")) {
            ChameleonIO.setLoggerConfigMode(serialPort, ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("ULTRALIGHT")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_ULTRALIGHT", ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("CLASSIC-1K")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_1K", ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("CLASSIC-4K")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_4K", ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("CLASSIC-1K7B")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_1K_7B", ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("CLASSIC-4K7B")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_4K_7B", ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("CFGNONE")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=NONE", ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
            return;
        }
        else if(createCmd.equals("RESET")) { // need to re-establish the usb connection:
            ChameleonIO.executeChameleonMiniCommand(serialPort, "RESET", ChameleonIO.TIMEOUT);
            ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
            closeSerialPort(serialPort);
            configureSerialPort(null, usbReaderCallback);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
            return;
        }
        else if(createCmd.equals("Log Replay")) {
            appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("STATUS", "RE: LOG REPLAY: This is a wishlist feature. It might be necessary to add it to the firmware and implement it in hardware. Not currently implemented."));
            return;
        }
        else if(createCmd.equals("STATUS") || createCmd.equals("NEW EVENT") ||
                createCmd.equals("ERROR") || createCmd.equals("LOCATION") ||
                createCmd.equals("CARD INFO")) {
            try {
                displayUserInputPrompt("Description of the new event? ");
                Looper.loop();
            }
            catch(RuntimeException msgReady) {}
            msgParam = userInputStack;
            userInputStack = null;
        }
        else if(createCmd.equals("ONCLICK")) {
            msgParam = "SYSTICK Millis := " + getSettingFromDevice(serialPort, "SYSTICK?");
        }
        else if(createCmd.equals("GETUID")) {
            String rParam = getSettingFromDevice(serialPort, "GETUID");
            msgParam = "GETUID: " + rParam.replaceAll("..(?!$)", "$0:") + "(" + rParam + ").";
        }
        else if(createCmd.equals("SEND") || createCmd.equals("SEND_RAW")) {
            String bytesToSend = ((TextView) findViewById(R.id.userInputFormattedBytes)).getText().toString().replaceAll(" ", "");
            if(bytesToSend.length() != 2 || !Utils.stringIsHexadecimal(bytesToSend)) {
                appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Input to send to card must be a _single hexadecimal_ byte!"));
                return;
            }
            msgParam = "Card Response (if any): " + getSettingFromDevice(serialPort, createCmd + " " + bytesToSend);
        }
        else if(createCmd.equals("AUTOCAL")) {
            msgParam = getSettingFromDevice(serialPort, "AUTOCALIBRATE");
        }
        else {
            msgParam = getSettingFromDevice(serialPort, createCmd);
        }
        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(createCmd, msgParam));
    }

    /**
     * Highlights the selected logs (by checkmark in the Log tab) in the color of the passed button.
     * @param view pressed Button
     */
    public void actionButtonSelectedHighlight(View view) {
        int highlightColor = Color.parseColor(((Button) view).getTag().toString());
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                boolean isChecked = ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).isChecked();
                if (isChecked)
                    logEntryView.setBackgroundColor(highlightColor);
            }
        }
    }

    /**
     * Unchecks all of the selected logs in the Log tab.
     * @param view
     */
    public void actionButtonUncheckAll(View view) {
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).setChecked(false);
            }
        }
    }

    /**
     * Used to mark whether the APDU response in the log is incoming / outgoing from
     * card <--> reader. Mostly reserved for future use as the Chameleon currently only logs responses
     * in one direction anyhow.
     * @param view
     */
    public void actionButtonSetSelectedXFer(View view) {

        int directionFlag = Integer.parseInt(((Button) view).getTag().toString());
        Drawable dirArrowIcon = getResources().getDrawable(R.drawable.xfer16);
        if(directionFlag == 1)
            dirArrowIcon = getResources().getDrawable(R.drawable.incoming16v2);
        else if(directionFlag == 2)
            dirArrowIcon = getResources().getDrawable(R.drawable.outgoing16v2);

        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                boolean isChecked = ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).isChecked();
                if (isChecked) {
                    ImageView xferMarker = (ImageView) logEntryView.findViewById(R.id.inputDirIndicatorImg);
                    xferMarker.setImageDrawable(dirArrowIcon);
                }
            }
        }

    }

    /**
     * Handles parsing of the buttons in the Logging Tools menu to be applied to all selected logs.
     * @param view pressed Button
     */
    public void actionButtonProcessBatch(View view) {
        String actionFlag = ((Button) view).getTag().toString();
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                boolean isChecked = ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).isChecked();
                int recordIdx = ((LogEntryUI) logDataEntries.get(vi)).getRecordIndex();
                if (isChecked && actionFlag.equals("SEND")) {
                    String byteString = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                    ChameleonIO.executeChameleonMiniCommand(serialPort, "SEND " + byteString, ChameleonIO.TIMEOUT);
                }
                else if(isChecked && actionFlag.equals("SEND_RAW")) {
                    String byteString = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                    ChameleonIO.executeChameleonMiniCommand(serialPort, "SEND_RAW " + byteString, ChameleonIO.TIMEOUT);
                }
                else if(isChecked && actionFlag.equals("CLONE_UID")) {
                    String uid = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                    if(uid.length() != 2 * ChameleonIO.deviceStatus.UIDSIZE) {
                        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", String.format("Number of bytes for record #%d != the required %d bytes!", recordIdx, ChameleonIO.deviceStatus.UIDSIZE)));
                    }
                    else {
                        ChameleonIO.executeChameleonMiniCommand(serialPort, "UID=" + uid, ChameleonIO.TIMEOUT);
                    }
                }
                else if(isChecked && actionFlag.equals("PRINT")) {
                    byte[] rawBytes = ((LogEntryUI) logDataEntries.get(vi)).getEntryData();
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("PRINT", Utils.bytes2Hex(rawBytes) + "\n------\n" + Utils.bytes2Ascii(rawBytes)));
                }
                else if(isChecked && actionFlag.equals("HIDE")) {
                    logEntryView.setVisibility(View.GONE);
                }
                else if(isChecked && actionFlag.equals("COPY")) {
                    EditText etUserBytes = (EditText) findViewById(R.id.userInputFormattedBytes);
                    String appendBytes = Utils.bytes2Hex(((LogEntryUI) logDataEntries.get(vi)).getEntryData());
                    etUserBytes.append(appendBytes);
                }
            }
        }
    }

    /**
     * Constructs and displays a dialog providing meta information about the application.
     * @param view
     * @ref R.string.aboutapp
     */
    public void actionButtonAboutTheApp(View view) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this, R.style.SpinnerTheme);
        String rawAboutStr = getString(R.string.apphtmlheader) + getString(R.string.aboutapp) + getString(R.string.apphtmlfooter);
        rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_CODE%%", String.valueOf(BuildConfig.VERSION_CODE));
        rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_NAME%%", String.valueOf(BuildConfig.VERSION_NAME));
        //builder1.setMessage(Html.fromHtml(rawAboutStr, Html.FROM_HTML_MODE_LEGACY));

        WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(false);
        wv.loadDataWithBaseURL(null, rawAboutStr, "text/html", "UTF-8", "");
        wv.setBackgroundColor(Color.parseColor("#98fb98"));
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        //wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        wv.setInitialScale(10);

        adBuilder.setCancelable(true);
        adBuilder.setTitle("About the Application:");
        adBuilder.setIcon(R.drawable.olben64);
        adBuilder.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        adBuilder.setView(wv);
        AlertDialog alertDialog = adBuilder.create();

        alertDialog.show();

    }

    /**
     * Runs a command indicated in the TAG parameter of the pressed button.
     * @param view pressed Button
     */
    public void actionButtonRunCommand(View view) {
        String cmCmd = ((Button) view).getTag().toString();
        ChameleonIO.executeChameleonMiniCommand(serialPort, cmCmd, ChameleonIO.TIMEOUT);
    }

    /**
     * Wrapper around the first three buttons at the top of the Export tab for writing the
     * logs to Plaintext / HTML / native binary files formats.
     * @param view pressed Button
     */
    public void actionButtonWriteFile(View view) {
        LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusdownload16);
        String fileType = ((Button) view).getTag().toString(), mimeType = "message/rfc822";
        String outfilePath = "logdata-" + Utils.getTimestamp().replace(":", "") + "." + fileType;
        File downloadsFolder = new File("//sdcard//Download//");
        File outfile = new File(downloadsFolder, outfilePath);
        boolean docsFolderExists = true;
        if (!downloadsFolder.exists()) {
            docsFolderExists = downloadsFolder.mkdir();
        }
        if (docsFolderExists) {
            outfile = new File(downloadsFolder.getAbsolutePath(),outfilePath);
        }
        else {
            appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to save output in Downloads folder."));
            LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            return;
        }
        try {
            outfile.createNewFile();
            if (fileType.equals("out")) {
                mimeType = "plain/text";
                ExportTools.writeFormattedLogFile(outfile);
            }
            else if (fileType.equals("html")) {
                mimeType = "text/html";
                ExportTools.writeHTMLLogFile(outfile);
            }
            else if (fileType.equals("bin")) {
                mimeType = "application/octet-stream";
                ExportTools.writeBinaryLogFile(outfile);
            }
        } catch(Exception ioe) {
            appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
            LiveLoggerActivity.runningActivity.setStatusIcon(R.id.statusIconUlDl, R.drawable.statusxferfailed16);
            ioe.printStackTrace();
            return;
        }
        DownloadManager downloadManager = (DownloadManager) defaultContext.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(outfile.getName(), outfile.getName(), true, "text/plain",
                outfile.getAbsolutePath(), outfile.length(),true);

        boolean saveFileChecked = ((RadioButton) findViewById(R.id.radio_save_storage)).isChecked();
        boolean emailFileChecked = ((RadioButton) findViewById(R.id.radio_save_email)).isChecked();
        boolean shareFileChecked = ((RadioButton) findViewById(R.id.radio_save_share)).isChecked();
        if(emailFileChecked || shareFileChecked) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType(mimeType);
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outfile));
            i.putExtra(Intent.EXTRA_SUBJECT, "Chameleon Mini Log Data Output (Log Attached)");
            i.putExtra(Intent.EXTRA_TEXT, "See subject.");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(i, "Share the file ... "));
        }
        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("EXPORT", "Saved log file to \"" + outfilePath + "\"."));
    }

    /**
     * Called when the Export tab button for writing the DUMP_MFU command output is requested by the user.
     * @param view
     */
    public void actionButtonDumpMFU(View view) {
        ExportTools.saveBinaryDumpMFU("mfultralight");
    }

    /**
     * Called when one of the command Spinner buttons changes state.
     * @param view calling Spinner
     * @ref TabFragment.connectCommandListSpinnerAdapter
     * @ref TabFragment.connectPeripheralSpinnerAdapter
     */
    public static void actionSpinnerSetCommand(View view) {
        String sopt = ((Spinner) view).getSelectedItem().toString();
        if(sopt.substring(0, 2).equals("--"))
            sopt = "NONE";
        String cmCmd = ((Spinner) view).getTag().toString() + sopt;
        ChameleonIO.executeChameleonMiniCommand(serialPort, cmCmd, ChameleonIO.TIMEOUT);
    }

    /**
     * Listener object for new Spinner selections.
     * @ref LiveLoggerActivity.actionSpinnerSetCommand
     */
    public static AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            actionSpinnerSetCommand(arg1);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {}
    };

    /**
     * Stores the user input for descriptions of the new annotation events available in the
     * Logging Tools tab.
     */
    private String userInputStack;

    /**
     * Prompts for a user description of the indicated annotation event from the
     * Log Tools tab.
     * @param promptMsg
     * @ref LiveLoggerActivity.userInputStack
     * @see res/layout/log_tools_tab.xml
     */
    public void displayUserInputPrompt(String promptMsg) {
        final EditText userInput = new EditText(this);
        userInput.setHint("What is the event description?");
        new AlertDialog.Builder(this)
                .setTitle(promptMsg)
                //.setMessage("Enter annotation for the current log.")
                .setView(userInput)
                .setPositiveButton("Submit Message", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        userInputStack = userInput.getText().toString();
                        throw new RuntimeException("The user input is ready.");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    /**
     * Wrapper around the button pressed for the download of stored log data and card information
     * by XModem in the Export tab.
     * @param view
     */
    public void actionButtonExportLogDownload(View view) {
        String action = ((Button) view).getTag().toString();
        if(action.equals("LOGDOWNLOAD"))
            ExportTools.downloadByXModem("LOGDOWNLOAD", "devicelog", false);
        else if(action.equals("LOGDOWNLOAD2LIVE"))
            ExportTools.downloadByXModem("LOGDOWNLOAD", "devicelog", true);
        else if(action.equals("DOWNLOAD"))
            ExportTools.downloadByXModem("DOWNLOAD", "carddata-" + ChameleonIO.deviceStatus.CONFIG, false);
    }

    /**
     * Constant for the file chooser dialog in the upload card data process.
     */
    private static final int FILE_SELECT_CODE = 0;

    /**
     * Called after the user chooses a file in the upload card dialog.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    String filePath = "<FileNotFound>";
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        filePath = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        filePath = "//sdcard//Download//" + filePath;
                    }
                    throw new RuntimeException(filePath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Wrapper around the card upload feature.
     * The method has the user pick a saved card file from the /sdcard/Download/* folder, then
     * initiates the upload with the function in ExportTools.
     * @param view pressed Button
     */
    public void actionButtonUploadCard(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(Uri.parse("//sdcard//Download//"), "*/*");
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a Card File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException e) {
            appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Unable to choose card file: " + e.getMessage()));
        }
        String cardFilePath = "";
        try {
            Looper.loop();
        } catch(RuntimeException rte) {
            cardFilePath = rte.getMessage().split("java.lang.RuntimeException: ")[1];
            Log.i(TAG, "Chosen Card File: " + cardFilePath);
        }
        ExportTools.uploadCardFileByXModem(cardFilePath);
    }

}