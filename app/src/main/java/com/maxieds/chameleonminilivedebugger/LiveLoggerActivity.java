package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SEARCH;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;

public class LiveLoggerActivity extends AppCompatActivity {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();

    public static LayoutInflater defaultInflater;
    public static LinearLayout logDataFeed;
    public static List<LogEntryBase> logDataEntries = new ArrayList<LogEntryBase>();
    public static boolean logDataFeedConfigured = false;

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

        //getActionBar().hide();
        //Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        //toolbar.setSubtitle("Portable logging interface for the ChameleonMini");
        //toolbar.setLogo(getResources().getDrawable(R.drawable.chameleonlogo24));
        //setActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle("Portable logging interface");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.chameleonlogo24);
        actionBar.setDisplayShowHomeEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_pager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // the view pager hides the tab icons by default, so we reset them:
        tabLayout.getTabAt(TAB_LOG).setIcon(R.drawable.nfc24v1);
        tabLayout.getTabAt(TAB_TOOLS).setIcon(R.drawable.tools24);
        tabLayout.getTabAt(TAB_EXPORT).setIcon(R.drawable.insertbinary24);
        tabLayout.getTabAt(TAB_SEARCH).setIcon(R.drawable.searchicon24);

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS", "No USB drivers available."));
            return;
        }
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            Log.w(TAG, "USB connection is null.");
            appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS", "USB connection is null."));
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            return;
        }
        UsbSerialPort port = driver.getPorts().get(0);
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            //Log.w(TAG, ChameleonIO.setLoggerConfigMode(port, ChameleonIO.TIMEOUT).name());
            LogEntryMetadataRecord statusRecord = new LogEntryMetadataRecord(defaultInflater, "Status:", "Text.");
            appendNewLog(statusRecord);
            port.close();
            Log.w(TAG, "OK ... Tested the USB device config.");
        } catch(IOException ioe) {
            Log.w(TAG, "IO ERROR: " + ioe.getMessage());
            ioe.printStackTrace();
        }

        Context context = getApplicationContext();

    }
}
