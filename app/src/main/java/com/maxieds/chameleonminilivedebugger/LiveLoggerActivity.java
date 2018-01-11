package com.maxieds.chameleonminilivedebugger;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;

public class LiveLoggerActivity extends AppCompatActivity {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();
    public static LiveLoggerActivity runningActivity;

    public static LayoutInflater defaultInflater;
    public static Context defaultContext;
    public static LinearLayout logDataFeed;
    public static List<LogEntryBase> logDataEntries = new ArrayList<LogEntryBase>();
    public static int RECORDID = 0;
    public static boolean logDataFeedConfigured = false;
    public static UsbSerialDevice serialPort;
    public static SpinnerAdapter spinnerRButtonAdapter;
    public static SpinnerAdapter spinnerRButtonLongAdapter;
    public static SpinnerAdapter spinnerLButtonAdapter;
    public static SpinnerAdapter spinnerLButtonLongAdapter;
    public static SpinnerAdapter spinnerLEDRedAdapter;
    public static SpinnerAdapter spinnerLEDGreenAdapter;
    public static SpinnerAdapter spinnerLogModeAdapter;
    public static SpinnerAdapter spinnerCmdShellAdapter;
    private static ViewPager viewPager;

    public static void appendNewLog(LogEntryBase logEntry) {
        logDataFeed.addView(logEntry.getLayoutContainer());
        logDataEntries.add(logEntry);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // fix bug where the tabs are blank when the application is relaunched:
        super.onCreate(savedInstanceState);
        if(!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN) ||
                    intentAction.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                Log.w(TAG, "onCreate(): Main Activity is not the root.  Finishing Main Activity instead of re-launching.");
                finish();
                LiveLoggerActivity.runningActivity.closeSerialPort(serialPort);
                serialPort = LiveLoggerActivity.runningActivity.configureSerialPort(null, usbReaderCallback);
                ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
                //LiveLoggerActivity.runningActivity.sendBroadcast(intent); // permission denied!
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
        actionBar.setSubtitle("Portable logging interface v" + String.valueOf(BuildConfig.VERSION_NAME));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.chameleonlogo24);
        actionBar.setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.tab_pager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this));
        viewPager.setOffscreenPageLimit(TabFragmentPagerAdapter.TAB_COUNT - 1);
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

        // the reader is going to return junk for the settings bar title if we don't pause for a second:
        try {
            Thread.sleep(500);
        } catch(Exception e) {}
        ChameleonIO.deviceStatus.updateAllStatusAndPost(true);

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent == null)
            return;
        else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
            closeSerialPort(serialPort);
            serialPort = configureSerialPort(null, usbReaderCallback);
            ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
        }
        else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
            ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
            if(ChameleonIO.WAITING_FOR_RESPONSE)
                ChameleonIO.WAITING_FOR_RESPONSE = false;
            closeSerialPort(serialPort);
        }
    }

    public static String getSettingFromDevice(UsbSerialDevice cmPort, String query) {
        ChameleonIO.WAITING_FOR_RESPONSE = true;
        ChameleonIO.DEVICE_RESPONSE = "";
        ChameleonIO.SerialRespCode rcode = ChameleonIO.executeChameleonMiniCommand(cmPort, query, ChameleonIO.TIMEOUT);
        for(int i = 1; i < 15; i++) {
            if(!ChameleonIO.WAITING_FOR_RESPONSE)
                break;
            try {
                Thread.sleep(50);
            } catch(InterruptedException ie) {
                ChameleonIO.WAITING_FOR_RESPONSE = false;
                return "";
            }
        }
        //appendNewLog(new LogEntryMetadataRecord(defaultInflater, "INFO: Device query of " + query + " returned status " + ChameleonIO.DEVICE_RESPONSE_CODE, ChameleonIO.DEVICE_RESPONSE));
        return ChameleonIO.DEVICE_RESPONSE;
    }

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
            return serialPort;
        }
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if(serialPort != null && serialPort.open()) {
            serialPort.setBaudRate(115200);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serialPort.read(readerCallback);
        }
        else {
            appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB ERROR: ", "Unable to configure serial device."));
            serialPort = null;
            return serialPort;
        }

        ChameleonIO.setLoggerConfigMode(serialPort, ChameleonIO.TIMEOUT);
        //ChameleonIO.setReaderConfigMode(serialPort, ChameleonIO.TIMEOUT);
        ChameleonIO.enableLiveDebugging(serialPort, ChameleonIO.TIMEOUT);
        ChameleonIO.PAUSED = false;
        appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS: ", "Successfully configured the device in passive logging mode."));
        return serialPort;

    }

    private boolean closeSerialPort(UsbSerialDevice serialPort) {
        if(serialPort != null)
            serialPort.close();
        ChameleonIO.PAUSED = true;
        return true;
    }

    // this is what's going to get called when the LIVE config spontaneously prints its log data to console:
    private UsbSerialInterface.UsbReadCallback usbReaderCallback = new UsbSerialInterface.UsbReadCallback() {

        @Override
        public void onReceivedData(byte[] liveLogData) {
            if(ChameleonIO.PAUSED) {
                //appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB RESPONSE: ", Utils.bytes2Hex(liveLogData) + " | " + Utils.bytes2Ascii(liveLogData)));
                //ChameleonIO.PAUSED = false;
                return;
            }
            else if(ChameleonIO.WAITING_FOR_RESPONSE) {
                String strLogData = new String(liveLogData);
                ChameleonIO.DEVICE_RESPONSE_CODE = strLogData.split("[\n\r]+")[0];
                ChameleonIO.DEVICE_RESPONSE = strLogData.replace(ChameleonIO.DEVICE_RESPONSE_CODE, "").replaceAll("[\n\r\t]*", "");
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
            //else
            //    appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB ERROR: ", "Invalid raw log data sent by device."));
        }

    };

    public void actionButtonExit(View view) {
        closeSerialPort(serialPort);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

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

    public void actionButtonClearUserText(View view) {
        TextView userInputText = (TextView) findViewById(R.id.userInputFormattedBytes);
        userInputText.setText("");
        userInputText.setHint("01 23 45 67 89 ab cd ef");
    }

    public void actionButtonRefreshDeviceStatus(View view) {
        ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
    }

    public void actionButtonClearAllLogs(View view) {
        if(RECORDID > 0) {
            logDataEntries.clear();
            RECORDID = 0;
            logDataFeed.removeAllViewsInLayout();
        }
    }

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
        else if(createCmd.equals("ULTRALIGHT")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_ULTRALIGHT", ChameleonIO.TIMEOUT);
            return;
        }
        else if(createCmd.equals("CLASSIC-1K")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_1K", ChameleonIO.TIMEOUT);
            return;
        }
        else if(createCmd.equals("CLASSIC-4K")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_4K", ChameleonIO.TIMEOUT);
            return;
        }
        else if(createCmd.equals("CLASSIC-1K7B")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_1K_7B", ChameleonIO.TIMEOUT);
            return;
        }
        else if(createCmd.equals("CLASSIC-4K7B")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_CLASSIC_4K_7B", ChameleonIO.TIMEOUT);
            return;
        }
        else if(createCmd.equals("CFGNONE")) {
            ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=NONE", ChameleonIO.TIMEOUT);
            return;
        }
        //else if(createCmd.equals("RESET")) {
        //    ChameleonIO.executeChameleonMiniCommand(serialPort, "RESET", ChameleonIO.TIMEOUT);
        //    configureSerialPort(null);
        //    return;
        //}
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
        else if(createCmd.equals("GETUID")) {
            String rParam = getSettingFromDevice(serialPort, "GETUID");
            msgParam = "GETUID: " + rParam.replaceAll("..(?!$)", "$0:") + "(" + rParam + ").";
        }
        else if(createCmd.equals("CHARGING")) {
            msgParam = getSettingFromDevice(serialPort, "CHARGING?");
        }
        else if(createCmd.equals("STRENGTH")) {
            msgParam = getSettingFromDevice(serialPort, "RSSI?");
        }
        else if(createCmd.equals("VERSION")) {
            msgParam = getSettingFromDevice(serialPort, "VERSION?");
        }
        else if(createCmd.equals("IDENTIFY")) {
            msgParam = getSettingFromDevice(serialPort, "IDENTIFY?");
        }
        else if(createCmd.equals("ONCLICK")) {
            msgParam = "SYSTICK Millis := " + getSettingFromDevice(serialPort, "SYSTICK?");
        }
        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(createCmd, msgParam));
    }

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

    public void actionButtonHideRecord(View view) {
        LinearLayout mainContainer = (LinearLayout) ((Button) view).getParent().getParent();
        mainContainer.setVisibility(LinearLayout.GONE);
    }

    public void actionButtonUncheckAll(View view) {
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).setChecked(false);
            }
        }
    }

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

    public void actionButtonProcessBatch(View view) {
        String actionFlag = ((Button) view).getTag().toString();
        //if(actionFlag.equals("PARSE_APDU")) {
        //    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("STATUS", "Parsing of APDU commands and status codes not yet supported."));
        //    return;
        //}
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                boolean isChecked = ((CheckBox) logEntryView.findViewById(R.id.entrySelect)).isChecked();
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
                    ChameleonIO.executeChameleonMiniCommand(serialPort, "UID=" + uid, ChameleonIO.TIMEOUT);
                }
                else if(isChecked && actionFlag.equals("PRINT")) {
                    byte[] rawBytes = ((LogEntryUI) logDataEntries.get(vi)).getEntryData();
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("PRINT", Utils.bytes2Hex(rawBytes) + "\n------\n" + Utils.bytes2Ascii(rawBytes)));
                }
                else if(isChecked && actionFlag.equals("HIDE")) {
                    logEntryView.setVisibility(View.GONE);
                }
                //else if(isChecked && actionFlag.equals("TRIM_CMD")) {
                //    ((LogEntryUI) logDataEntries.get(vi)).trimCommandText();
                //}
                else if(isChecked && actionFlag.equals("COPY")) {
                    EditText etUserBytes = (EditText) findViewById(R.id.userInputFormattedBytes);
                    String appendBytes = Utils.bytes2Hex(((LogEntryUI) logDataEntries.get(vi)).getEntryData());
                    etUserBytes.append(appendBytes);
                }
            }
        }
    }

    public void actionButtonAboutTheApp(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.SpinnerTheme);
        String rawAboutStr = getString(R.string.aboutapp);
        rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_CODE%%", String.valueOf(BuildConfig.VERSION_CODE));
        rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_NAME%%", String.valueOf(BuildConfig.VERSION_NAME));
        builder1.setMessage(Html.fromHtml(rawAboutStr, Html.FROM_HTML_MODE_LEGACY));
        builder1.setCancelable(true);
        builder1.setTitle("About the Application:");
        builder1.setIcon(R.drawable.olben64);
        builder1.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void actionButtonRunCommand(View view) {
        String cmCmd = ((Button) view).getTag().toString();
        ChameleonIO.executeChameleonMiniCommand(serialPort, cmCmd, ChameleonIO.TIMEOUT);
    }

    public void actionButtonWriteFile(View view) {
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
            return;
        }
        try {
            outfile.createNewFile();
            if (fileType.equals("out")) {
                mimeType = "plain/text";
                writeFormattedLogFile(outfile);
            }
            else if (fileType.equals("html")) {
                mimeType = "text/html";
                writeHTMLLogFile(outfile);
            }
            else if (fileType.equals("bin")) {
                mimeType = "application/octet-stream";
                writeBinaryLogFile(outfile);
            }
        } catch(Exception ioe) {
            appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", ioe.getMessage()));
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
        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("STATUS", "Saved log file to \"" + outfilePath + "\"."));
    }

    public static void actionSpinnerSetCommand(View view) {
        String sopt = ((Spinner) view).getSelectedItem().toString();
        if(sopt.substring(0, 2).equals("--"))
            sopt = "NONE";
        String cmCmd = ((Spinner) view).getTag().toString() + sopt;
        ChameleonIO.executeChameleonMiniCommand(serialPort, cmCmd, ChameleonIO.TIMEOUT);
    }

    public static AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            actionSpinnerSetCommand(arg1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {}

    };

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

    public static boolean writeFormattedLogFile(File fd) throws Exception {
        Log.i(TAG, String.valueOf("00".getBytes(StandardCharsets.US_ASCII)));

        FileOutputStream fout = new FileOutputStream(fd);
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                String dataLine = ((LogEntryUI) logDataEntries.get(vi)).toString() + "\n";
                fout.write(dataLine.getBytes(StandardCharsets.US_ASCII));
            }
            else {
                String lineStr = "\n## " + ((LogEntryMetadataRecord) logDataEntries.get(vi)).toString() + "\n";
                fout.write(lineStr.getBytes(StandardCharsets.US_ASCII));
            }
        }
        fout.close();
        return true;
    }

    public static boolean writeHTMLLogFile(File fd) throws Exception {
        FileOutputStream fout = new FileOutputStream(fd);
        String htmlHeader = "<html><head><title>Chameleon Mini Live Debugger -- Logging Output</title></head><body>\n\n";
        fout.write(htmlHeader.getBytes(StandardCharsets.US_ASCII));
        String defaultBgColor = String.format("#%06X", (0xFFFFFF & R.color.colorPrimaryDarkLog));
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                String bgColor = String.format("#%06X", (0xFFFFFF & logEntryView.getDrawingCacheBackgroundColor()));
                if(bgColor.equals(defaultBgColor))
                    bgColor = "#ffffff";
                String lineData = "<code bgcolor='" + bgColor + "'>" + ((LogEntryUI) logDataEntries.get(vi)).toString() + "</code><br/>\n";
                fout.write(lineData.getBytes(StandardCharsets.US_ASCII));
            }
            else {
                String lineData = "<b><code>" + ((LogEntryMetadataRecord) logDataEntries.get(vi)).toString() + "</code></b><br/>\n";
                fout.write(lineData.getBytes(StandardCharsets.US_ASCII));
            }
        }
        String htmlFooter = "</body></html>";
        fout.write(htmlFooter.getBytes(StandardCharsets.US_ASCII));
        fout.close();
        return true;
    }

    public static boolean writeBinaryLogFile(File fd) throws Exception {
        FileOutputStream fout = new FileOutputStream(fd);
        short localTicks = 0;
        for (int vi = 0; vi < logDataFeed.getChildCount(); vi++) {
            View logEntryView = logDataFeed.getChildAt(vi);
            if (logDataEntries.get(vi) instanceof LogEntryUI) {
                LogEntryUI logEntry = (LogEntryUI) logDataEntries.get(vi);
                byte[] entryBytes = logEntry.packageBinaryLogData(localTicks);
                localTicks = logEntry.getNextOffsetTime(localTicks);
                fout.write(entryBytes);
            }
        }
        fout.close();
        return true;
    }

    public void actionButtonExportLogDownload(View view) {
        String action = ((Button) view).getTag().toString();
        if(action.equals("LOGDOWNLOAD"))
            ExportTools.downloadByZModem("LOGDOWNLOAD", "devicelog", false);
        else if(action.equals("LOGDOWNLOAD2LIVE"))
            ExportTools.downloadByZModem("LOGDOWNLOAD", "devicelog", true);
        else if(action.equals("DOWNLOAD"))
            ExportTools.downloadByZModem("DOWNLOAD", "carddata", false);
    }

}
