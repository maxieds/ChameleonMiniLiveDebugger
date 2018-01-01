package com.maxieds.chameleonminilivedebugger;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SEARCH;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;

public class LiveLoggerActivity extends FragmentActivity {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_live_logger);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_pager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // the view pager hides the tab icons by default, so we reset them:
        tabLayout.getTabAt(TAB_LOG).setIcon(R.drawable.nfc24v1);
        tabLayout.getTabAt(TAB_TOOLS).setIcon(R.drawable.tools24);
        tabLayout.getTabAt(TAB_EXPORT).setIcon(R.drawable.insertbinary24);
        tabLayout.getTabAt(TAB_SEARCH).setIcon(R.drawable.searchicon24);

        //ScrollView logScroller = (ScrollView) ((TabFragmentPagerAdapter) viewPager.getAdapter()).getItem(TAB_LOG).getView().findViewById(R.id.log_scroll_view);
        ScrollView logScroller = (ScrollView) findViewById(R.id.log_scroll_view);
        LinearLayout logFeed = new LinearLayout(this);
        logFeed.setOrientation(LinearLayout.VERTICAL);
        logScroller.addView(logFeed);


        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            Log.w(TAG, "USB connection is null.");
            System.out.println("USB connection is null.");
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            return;
        }
        UsbSerialPort port = driver.getPorts().get(0);
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            //Log.w(TAG, ChameleonIO.setLoggerConfigMode(port, ChameleonIO.TIMEOUT).name());
            LogEntryMetadataRecord statusRecord = new LogEntryMetadataRecord(getApplicationContext(), "Status:", "Text.");
            logFeed.addView(statusRecord.getLayoutContainer());
            logFeed.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            port.close();
            Log.w(TAG, "OK ... Tested the USB device config.");
        } catch(IOException ioe) {
            Log.w(TAG, "IO ERROR: " + ioe.getMessage());
            ioe.printStackTrace();
        }

        Context context = getApplicationContext();

    }
}
