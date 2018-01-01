package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SEARCH;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;

public class LiveLoggerActivity extends AppCompatActivity {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();

    public static LayoutInflater defaultInflater;
    public static Context defaultContext;
    public static LinearLayout logDataFeed;
    public static List<LogEntryBase> logDataEntries = new ArrayList<LogEntryBase>();
    public static boolean logDataFeedConfigured = false;
    UsbSerialDevice serialPort;

    public static void appendNewLog(LogEntryBase logEntry) {
        logDataFeed.addView(logEntry.getLayoutContainer());
        logDataEntries.add(logEntry);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_live_logger);
        logDataFeed = new LinearLayout(getApplicationContext());
        defaultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        defaultContext = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle("Portable logging interface");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.chameleonlogo24);
        actionBar.setDisplayShowHomeEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_pager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this));
        viewPager.setOffscreenPageLimit(TabFragmentPagerAdapter.TAB_COUNT - 1);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // the view pager hides the tab icons by default, so we reset them:
        tabLayout.getTabAt(TAB_LOG).setIcon(R.drawable.nfc24v1);
        tabLayout.getTabAt(TAB_TOOLS).setIcon(R.drawable.tools24);
        tabLayout.getTabAt(TAB_EXPORT).setIcon(R.drawable.insertbinary24);
        tabLayout.getTabAt(TAB_SEARCH).setIcon(R.drawable.searchicon24);

        configureSerialPort(true);
        ChameleonIO.setLoggerConfigMode(serialPort, ChameleonIO.TIMEOUT);
        //ChameleonIO.setReaderConfigMode(serialPort, ChameleonIO.TIMEOUT);
        ChameleonIO.enableLiveDebugging(serialPort, ChameleonIO.TIMEOUT);
        ChameleonIO.PAUSED = false;
        appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS: ", "Successfully configured the device in passive logging mode."));

    }

    private boolean configureSerialPort(boolean appendErrorLogs) {

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbDevice device = null;
        UsbDeviceConnection connection = null;
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if(!usbDevices.isEmpty()) {
            for(Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if(deviceVID == ChameleonIO.CMUSB_VENDORID && devicePID == ChameleonIO.CMUSB_PRODUCTID) {
                    connection = usbManager.openDevice(device);
                    break;
                }
            }
        }
        if((device == null || connection == null) && appendErrorLogs) {
            appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS: ", "Connection to device unavailable."));
        }
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if(serialPort != null && serialPort.open()) {
            serialPort.setBaudRate(115200);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serialPort.read(usbReaderCallback);
            return true;
        }
        else if(appendErrorLogs) {
            appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB ERROR: ", "Unable to configure serial device."));
            return false;
        }
        return false;

    }

    private boolean closeSerialPort() {
        serialPort.close();
        ChameleonIO.PAUSED = true;
        return true;
    }

    // this is what's going to get called when the LIVE config spontaneously prints its log data to console:
    private UsbSerialInterface.UsbReadCallback usbReaderCallback = new UsbSerialInterface.UsbReadCallback() {

        @Override
        public void onReceivedData(byte[] liveLogData) {
            if(ChameleonIO.PAUSED) {
                appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB RESPONSE: ", Utils.bytes2Hex(liveLogData) + " | " + Utils.bytes2Ascii(liveLogData)));
                ChameleonIO.PAUSED = false;
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
            //else
            //    appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB ERROR: ", "Invalid raw log data sent by device."));
        }

    };

    public void actionButtonCreateNewEvent(View view) {
        String createCmd = ((Button) view).getText().toString();
        String msgParam = "";
        if(createCmd.equals("READER")) {
            ChameleonIO.setReaderConfigMode(serialPort, ChameleonIO.TIMEOUT);
            return;
        }
        else if(createCmd.equals("SNIFFER")) {
            ChameleonIO.setLoggerConfigMode(serialPort, ChameleonIO.TIMEOUT);
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
        else if(createCmd.equals("LOCAL UID")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "GETUID", ChameleonIO.TIMEOUT);
        }
        else if(createCmd.equals("CHARGING")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CHARGING?", ChameleonIO.TIMEOUT);
        }
        else if(createCmd.equals("STRENGTH")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "RSSI?", ChameleonIO.TIMEOUT);
        }
        else if(createCmd.equals("LOCAL UID")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "GETUID", ChameleonIO.TIMEOUT);
        }
        else if(createCmd.equals("FIRMWARE")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "VERSION?", ChameleonIO.TIMEOUT);
        }
        else if(createCmd.equals("IDENTIFY")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "IDENTIFY", ChameleonIO.TIMEOUT);
        }
        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(createCmd, msgParam));
    }

    private String userInputStack;

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

}
