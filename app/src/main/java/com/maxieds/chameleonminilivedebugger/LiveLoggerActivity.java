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

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SCRIPTING;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_SLOTS;

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
     private static LiveLoggerActivity runningActivity = null;
     Bundle localSavedInstanceState;

     public static LiveLoggerActivity getInstance() { return runningActivity; }

     /**
      * Static variables used across classes.
      */
     public static LayoutInflater defaultInflater;
     public static Context defaultContext;
     private static ViewPager viewPager;
     private static int selectedTab = TAB_TOOLS;

     public static int getSelectedTab() { return selectedTab; }
     public static void setSelectedTab(int tabIdx) {
          if (selectedTab < 0) {
               selectedTab = 0;
          } else {
               selectedTab = tabIdx;
          }
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
          ((ImageView) findViewById(iconID)).setAlpha(127);
     }

     /**
      * Default handler for  all uncaught exceptions.
      */
     private Thread.UncaughtExceptionHandler unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
          @Override
          public void uncaughtException(Thread thread, Throwable ex) {
               String msgParam = "An unknown error happened in the application. Please upgrade to the latest version if a newer one is available, or ";
               msgParam += "contact the developer at maxieds@gmail.com to report whatever action you took to generate this error!";
               MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("UNRECOGNIZED EXCEPTION", msgParam));
               System.exit(-1);
          }
     };

     private static BroadcastReceiver serialIOActionReceiver = null;
     private static IntentFilter serialIOActionFilter = null;
     private static boolean serialIOReceiversRegistered = false;

     private static ArrayList<Intent> deleyedIntentQueue = new ArrayList<Intent>();
     private static boolean delayedIntentQueueMutex = false;
     private static Handler delayedIntentHandler = new Handler();
     private static Runnable delayedIntentRunnable = new Runnable() {
          synchronized public void run() {
               while (true) {
                    if (!delayedIntentQueueMutex) {
                         delayedIntentQueueMutex = true;
                         for (int iqi = 0; iqi < deleyedIntentQueue.size(); iqi++) {
                              LiveLoggerActivity.getInstance().onNewIntent(deleyedIntentQueue.get(iqi));
                         }
                         deleyedIntentQueue.clear();
                         delayedIntentQueueMutex = false;
                         break;
                    }
                    try {
                         Thread.sleep(50);
                    }
                    catch(Exception excpt) {
                         excpt.printStackTrace();
                    }
               }
          }
     };

     public static void addNewDelayedIntentHandler(Intent nextIntent) {
          while (true) {
               if (!delayedIntentQueueMutex) {
                    delayedIntentQueueMutex = true;
                    deleyedIntentQueue.add(nextIntent);
                    delayedIntentQueueMutex = false;
                    delayedIntentHandler.postDelayed(delayedIntentRunnable, 0);
                    break;
               }
               try {
                    Thread.sleep(50);
               } catch (Exception excpt) {
                    excpt.printStackTrace();
               }
          }
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
          if(runningActivity == null || !isTaskRoot()) {
               Log.i(TAG, "Created new activity");
          }
          if(!isTaskRoot()) {
               Log.i(TAG, "ReLaunch Intent Action: " + getIntent().getAction());
               final Intent intent = getIntent();
               final String intentAction = intent.getAction();
               if (intentAction != null && (intentAction.equals(UsbManager.ACTION_USB_DEVICE_DETACHED) || intentAction.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))) {
                    Log.i(TAG, "onCreate(): Main Activity is not the root.  Finishing Main Activity instead of re-launching.");
                    finish();
                    serialIOReceiversRegistered = false;
                    return;
               }
          }

          boolean completeRestart = (runningActivity == null);
          runningActivity = this;
          localSavedInstanceState = savedInstanceState;
          defaultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          defaultContext = getApplicationContext();

          AndroidSettingsStorage.loadPreviousSettings(AndroidSettingsStorage.DEFAULT_CMLDAPP_PROFILE);
          if(ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT) {
               MainActivityLogUtils.clearAllLogs();
          }
          ThemesConfiguration.setLocalTheme(ThemesConfiguration.storedAppTheme, false); // set the base colors, not the backgrounds initially
          ThemesConfiguration.setThemeHandler.postDelayed(ThemesConfiguration.setThemeRunner, 400);

          setContentView(R.layout.activity_live_logger);

          Toolbar actionBar = (Toolbar) findViewById(R.id.toolbarActionBar);
          actionBar.setSubtitle("Portable device logger | v" + String.valueOf(BuildConfig.VERSION_NAME));
          clearStatusIcon(R.id.statusIconUlDl);
          getWindow().setTitleColor(ThemesConfiguration.getThemeColorVariant(R.attr.actionBarBackgroundColor));
          getWindow().setStatusBarColor(ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark));
          getWindow().setNavigationBarColor(ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark));

          configureTabViewPager();

          if(completeRestart) {
               String[] permissions = {
                       "android.permission.READ_EXTERNAL_STORAGE",
                       "android.permission.WRITE_EXTERNAL_STORAGE",
                       "android.permission.INTERNET",
                       "android.permission.USB_PERMISSION",
                       "android.permission.BLUETOOTH",
                       "android.permission.BLUETOOTH_ADMIN",
                       "android.permission.ACCESS_COARSE_LOCATION"
               };
               if (android.os.Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(permissions, 200);
               }
               getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
               setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // keep app from crashing when the screen rotates
          }

          if(!serialIOReceiversRegistered) {
               if(Settings.serialIOPorts == null) {
                    Settings.initSerialIOPortObjects();
               }
               if((Settings.serialIOPorts[Settings.USBIO_IFACE_INDEX].configureSerial() != 0) && (Settings.getActiveSerialIOPort() != null)) {
                    Handler configDeviceHandler = new Handler();
                    Runnable configDeviceRunnable = new Runnable() {
                         public void run() {
                              ChameleonIO.detectChameleonType();
                              ChameleonIO.initializeDevice();
                              ChameleonIO.DeviceStatusSettings.startPostingStats(400);
                         }
                    };
                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                    configDeviceHandler.postDelayed(configDeviceRunnable, 600);
               }
               if(serialIOActionReceiver == null) {
                    try {
                         unregisterReceiver(serialIOActionReceiver);
                    } catch (Exception excpt) {
                         excpt.printStackTrace();
                    }
                    serialIOActionReceiver = new BroadcastReceiver() {
                         public void onReceive(Context context, Intent intent) {
                              Log.i(TAG, intent.getAction());
                              if (intent.getAction() == null) {
                                   return;
                              } else if (intent.getAction().equals(SerialUSBInterface.ACTION_USB_PERMISSION)) {
                                   onNewIntent(intent);
                              } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) ||
                                      intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                                   onNewIntent(intent);
                              } else if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND) ||
                                      intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                                   onNewIntent(intent);
                              } else if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED) ||
                                      intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) ||
                                      intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED) ||
                                      intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                                   onNewIntent(intent);
                              } else if (intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED) ||
                                      intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST) ||
                                      intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED) ||
                                      intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED) ||
                                      intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS)) {
                                   onNewIntent(intent);
                              }
                         }
                    };
                    serialIOActionFilter = new IntentFilter();
                    serialIOActionFilter.addAction(SerialUSBInterface.ACTION_USB_PERMISSION);
                    serialIOActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                    serialIOActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                    serialIOActionFilter.addAction(BluetoothDevice.ACTION_FOUND);
                    serialIOActionFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    serialIOActionFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    serialIOActionFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
                    serialIOActionFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                    serialIOActionFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS);
                    registerReceiver(serialIOActionReceiver, serialIOActionFilter);
                    SerialUSBInterface.registerUSBPermission(null, this);
                    serialIOReceiversRegistered = true;
               }
               Handler initSettingsDeviceHandler = new Handler();
               Runnable initSettingsDeviceRunnable = new Runnable() {
                    public void run() {
                         Settings.initializeSerialIOConnections();
                    }
               };
               initSettingsDeviceHandler.postDelayed(initSettingsDeviceRunnable, 800);
          }

          String userGreeting = getString(R.string.initialUserGreetingMsg);
          MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("STATUS", userGreeting));

          clearStatusIcon(R.id.statusIconNewMsg);
          clearStatusIcon(R.id.statusIconNewXFer);
          clearStatusIcon(R.id.signalStrength);
          clearStatusIcon(R.id.statusIconBT);

     }

     private static String INTENT_RESTART_ACTIVITY = "LiveLoggerActivity.Intent.Category.RESTART_ACTIVITY";

     @Override
     public void recreate() {
          Intent intent = getIntent();
          intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
          intent.addCategory(INTENT_RESTART_ACTIVITY);
          finish();
          overridePendingTransition(0, 0);
          startActivity(intent);
          overridePendingTransition(0, 0);
     }

     private static ViewPager.OnPageChangeListener tabChangeListener = null;

     /**
      * Configures the tabs part of the main UI.
      * @ref onCreate
      * @ref onOptionsItemSelected
      */
     protected void configureTabViewPager() {

          MainActivityLogUtils.logDataFeedConfigured = false;
          MainActivityLogUtils.logDataFeed = new LinearLayout(getApplicationContext());
          if(MainActivityLogUtils.logDataEntries != null)
               MainActivityLogUtils.logDataEntries.clear();

          viewPager = (ViewPager) findViewById(R.id.tab_pager);
          viewPager.setId(View.generateViewId());
          TabFragmentPagerAdapter tfPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this);
          viewPager.setAdapter(tfPagerAdapter);
          if(tabChangeListener != null) {
               viewPager.removeOnPageChangeListener(tabChangeListener);
          }
          tabChangeListener = new ViewPager.OnPageChangeListener() {
               @Override
               public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

               @Override
               public void onPageSelected(int position) {
                    if(position < 0) {
                         LiveLoggerActivity.selectedTab = 0;
                         return;
                    }
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
          };
          viewPager.addOnPageChangeListener(tabChangeListener);

          TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
          tabLayout.removeAllTabs();
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_LOG)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_TOOLS)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_EXPORT)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_SCRIPTING)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_CONFIG)));
          tabLayout.setupWithViewPager(viewPager);

          viewPager.setOffscreenPageLimit(TabFragmentPagerAdapter.TAB_COUNT - 1);
          viewPager.setCurrentItem(selectedTab);
          tfPagerAdapter.notifyDataSetChanged();

          // the view pager hides the tab icons by default, so we reset them:
          tabLayout.getTabAt(TAB_LOG).setIcon(tfPagerAdapter.getTabIcon(TAB_LOG));
          tabLayout.getTabAt(TAB_TOOLS).setIcon(tfPagerAdapter.getTabIcon(TAB_TOOLS));
          tabLayout.getTabAt(TAB_EXPORT).setIcon(tfPagerAdapter.getTabIcon(TAB_EXPORT));
          tabLayout.getTabAt(TAB_SCRIPTING).setIcon(tfPagerAdapter.getTabIcon(TAB_SCRIPTING));
          tabLayout.getTabAt(TAB_CONFIG).setIcon(tfPagerAdapter.getTabIcon(TAB_CONFIG));

     }

     public boolean onOptionsItemSelectedHelper(View view) {
          return onOptionsItemSelected((MenuItem) view);
     }

     /**
      * Handles newly attached / detached USB devices.
      * @param intent
      */
     @Override
     public void onNewIntent(Intent intent) {
          super.onNewIntent(intent);
          Log.i(TAG, "NEW INTENT: " + intent.getAction());
          if(intent == null) {
               return;
          }
          else if(intent.getAction().equals(SerialUSBInterface.ACTION_USB_PERMISSION)) {
               SerialUSBInterface.usbPermissionsGranted = true;
          }
          else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
               SerialUSBInterface.registerUSBPermission(intent, this);
               if(Settings.serialIOPorts[Settings.USBIO_IFACE_INDEX].configureSerial() != 0) {
                    Settings.stopSerialIOConnectionDiscovery();
                    Settings.SERIALIO_IFACE_ACTIVE_INDEX = Settings.USBIO_IFACE_INDEX;
                    Handler configDeviceHandler = new Handler();
                    Runnable configDeviceRunnable = new Runnable() {
                         public void run() {
                              ChameleonIO.detectChameleonType();
                              ChameleonIO.initializeDevice();
                              UITabUtils.initializeToolsTab(TAB_TOOLS_MITEM_SLOTS, TabFragment.UITAB_DATA[TAB_TOOLS].tabInflatedView);
                              ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                              ChameleonIO.DeviceStatusSettings.startPostingStats(0);
                         }
                    };
                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                    configDeviceHandler.postDelayed(configDeviceRunnable, 400);
                    ChameleonPeripherals.actionButtonRestorePeripheralDefaults(null);
                    if(ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT) {
                         MainActivityLogUtils.clearAllLogs();
                    }
                    setStatusIcon(R.id.statusIconUSB, R.drawable.usbconnected16);
               }
          }
          else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
               ChameleonIO.DeviceStatusSettings.stopPostingStats();
               if(ChameleonIO.WAITING_FOR_RESPONSE) {
                    ChameleonIO.WAITING_FOR_RESPONSE = false;
               }
               ChameleonSerialIOInterface serialIOPort = Settings.getActiveSerialIOPort();
               if(serialIOPort != null) {
                    serialIOPort.shutdownSerial();
               }
               Settings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
               setStatusIcon(R.id.statusIconUSB, R.drawable.usbdisconnected16);
               unregisterReceiver(SerialUSBInterface.usbPermissionsReceiver);
               SerialUSBInterface.usbPermissionsReceiverConfig = false;
               Settings.initializeSerialIOConnections();
          }
          else if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
               BluetoothGattConnector btGattConnect = ((BluetoothSerialInterface) Settings.serialIOPorts[Settings.BTIO_IFACE_INDEX]).getBluetoothGattConnector();
               if(btGattConnect == null) {
                    return;
               }
               BluetoothDevice btLocalDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               if(btLocalDevice == null) {
                    return;
               }
               btGattConnect.notifyBluetoothSerialInterfaceDeviceConnected(btLocalDevice);
          }
          else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED) ||
                  intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED)) {
               ChameleonIO.DeviceStatusSettings.stopPostingStats();
               Settings.stopSerialIOConnectionDiscovery();
               if(Settings.serialIOPorts[Settings.BTIO_IFACE_INDEX].configureSerial() != 0) {
                    Settings.SERIALIO_IFACE_ACTIVE_INDEX = Settings.BTIO_IFACE_INDEX;
                    Handler configDeviceHandler = new Handler();
                    Runnable configDeviceRunnable = new Runnable() {
                         public void run() {
                              if(Settings.getActiveSerialIOPort() != null &&
                                      ((BluetoothSerialInterface) Settings.getActiveSerialIOPort()).isDeviceConnected()) {
                                   ChameleonIO.detectChameleonType();
                                   ChameleonIO.initializeDevice();
                                   UITabUtils.initializeToolsTab(TAB_TOOLS_MITEM_SLOTS, TabFragment.UITAB_DATA[TAB_TOOLS].tabInflatedView);
                                   ChameleonPeripherals.actionButtonRestorePeripheralDefaults(null);
                                   ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
                                   ChameleonIO.DeviceStatusSettings.startPostingStats(0);
                                   setStatusIcon(R.id.statusIconBT, R.drawable.bluetooth16);
                              }
                              else {
                                   configDeviceHandler.postDelayed(this, 500);
                              }
                         }
                    };
                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                    configDeviceHandler.postDelayed(configDeviceRunnable, 200);
               }
          }
          else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) ||
                  intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST)) {
               ChameleonIO.DeviceStatusSettings.stopPostingStats();
               if(ChameleonIO.WAITING_FOR_RESPONSE) {
                    ChameleonIO.WAITING_FOR_RESPONSE = false;
               }
               ChameleonSerialIOInterface serialIOPort = Settings.getActiveSerialIOPort();
               if(serialIOPort != null) {
                    serialIOPort.shutdownSerial();
               }
               Settings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
               clearStatusIcon(R.id.statusIconBT);
               Settings.initializeSerialIOConnections();
          }
          else if(intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED)) {
               byte[] serialByteData = intent.getByteArrayExtra("DATA");
               String dataMsg = String.format(Locale.ENGLISH, "Unexpected serial I/O data received:\n%s\n%s",
                       Utils.bytes2Hex(serialByteData), Utils.bytes2Ascii(serialByteData));
               MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("STATUS", dataMsg));
          }
          else if(intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED)) {
               byte[] logDataBytes = intent.getByteArrayExtra("DATA");
               if(ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES) {
                    for(int chIdx = 0; chIdx < MainActivityLogUtils.logDataEntries.size(); chIdx++) {
                         if(!(MainActivityLogUtils.logDataEntries.get(chIdx) instanceof LogEntryUI)) {
                              continue;
                         }
                         LogEntryUI logEntryUI = (LogEntryUI) MainActivityLogUtils.logDataEntries.get(chIdx);
                         if(logEntryUI.logEntryDataEquals(logDataBytes)) {
                              logEntryUI.appendDuplicate(logDataBytes[2], logDataBytes[3]);
                              MainActivityLogUtils.logDataFeed.removeViewAt(chIdx);
                              MainActivityLogUtils.logDataEntries.remove(chIdx);
                              MainActivityLogUtils.appendNewLog(logEntryUI);
                              return;
                         }
                    }
               }
               MainActivityLogUtils.appendNewLog(LogEntryUI.newInstance(logDataBytes, ""));
          }
          else if(intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS)) {
               String msgType = intent.getStringExtra("STATUS-TYPE");
               String statusMsg = intent.getStringExtra("STATUS-MSG");
               MainActivityLogUtils.appendNewLog(new LogEntryMetadataRecord(defaultInflater, msgType, statusMsg));
          }
     }

     /**
      * Called when the activity is paused or put into the background.
      * @ref onResume()
      */
     @Override
     public void onPause() {
          super.onPause();
     }

     /**
      * Called when the activity is resumes or put into the foreground.
      * @ref onPause()
      */
     @Override
     public void onResume() {
          super.onResume();
     }

     /**
      * Exits the application.
      * @param view
      * @see res/layout/activity_live_logger.xml
      */
     public void actionButtonExit(View view) {
          ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
          ChameleonSerialIOInterface serialIOPort = Settings.getActiveSerialIOPort();
          if(serialIOPort != null) {
               serialIOPort.shutdownSerial();
          }
          ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
          finish();
     }

     /**
      * Manual refreshing of the device status settings requested by the user on button press at the
      * top right (second rightmost button) of the activity window.
      * @param view
      */
     public void actionButtonRefreshDeviceStatus(View view) {
          ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
     }

     public void actionButtonAppSettings(View view) {
          ThemesConfiguration.actionButtonAppSettings(view);
     }

     /**
      * Clears all logging data from the Log tab.
      * @param view
      */
     public void actionButtonClearAllLogs(View view) {
          MainActivityLogUtils.clearAllLogs();
     }

     /**
      * Handles button presses for most of the commands implemented in the Tools Menu.
      * @param view calling Button
      */
     public void actionButtonCreateNewEvent(View view) {
          if(Settings.getActiveSerialIOPort() == null) {
               MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Cannot run command since serial IO over USB/BT is not configured."));
               return;
          }
          String createCmd = ((Button) view).getText().toString();
          String btnTagValue = ((Button) view).getTag() != null ? ((Button) view).getTag().toString() : null;
          if(btnTagValue != null && !btnTagValue.equals("")) {
               String msgParam = "";
               createCmd = "CONFIG=" + btnTagValue;
               if(!ChameleonIO.REVE_BOARD) {
                    msgParam = ChameleonIO.getSettingFromDevice(createCmd);
               }
               else {
                    msgParam = ChameleonIO.getSettingFromDevice(createCmd);
               }
               MainActivityLogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(createCmd, msgParam));
               ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
          }
          else {
               ChameleonCommands.createNewCommandEvent(createCmd);
          }
     }

     public void actionButtonModifyUID(View view) {
          String uidAction = ((Button) view).getTag().toString();
          UIDCommands.modifyUID(uidAction);
     }

     /**
      * Constructs and displays a dialog providing meta information about the application.
      * @param view
      * @ref R.string.aboutapp
      */
     public void actionButtonAboutTheApp(View view) {
          AlertDialog alertDialog = MainActivityNavActions.getAboutTheAppDialog();
          alertDialog.show();
     }

     /**
      * Runs a command indicated in the TAG parameter of the pressed button.
      * @param view pressed Button
      */
     public void actionButtonRunCommand(View view) {
          String cmCmd = ((Button) view).getTag().toString();
          ChameleonCommands.runCommand(cmCmd);
     }

     public void actionButtonCollapseSimilar(View view) {
          MainActivityLogUtils.collapseSimilarLogs();
     }

     public void actionButtonSelectedHighlight(View view) {
          int highlightColor = Color.parseColor(((Button) view).getTag().toString());
          MainActivityLogUtils.selectedHighlightedLogs(highlightColor);
     }

     public void actionButtonUncheckAll(View view) {
          MainActivityLogUtils.uncheckAllLogs();
     }

     public void actionButtonSetSelectedXFer(View view) {
          int directionFlag = Integer.parseInt(((Button) view).getTag().toString());
          MainActivityLogUtils.setSelectedXFerOnLogs(directionFlag);
     }

     public void actionButtonProcessBatch(View view) {
          String actionFlag = ((Button) view).getTag().toString();
          MainActivityLogUtils.processBatchOfSelectedLogs(actionFlag);
     }

     /**
      * Wrapper around the first three buttons at the top of the Export tab for writing the
      * logs to Plaintext / HTML / native binary files formats.
      * @param view pressed Button
      */
     public void actionButtonWriteFile(View view) {
          String fileType = ((Button) view).getTag().toString();
          ExternalFileIO.exportOutputFile(fileType);
     }

     /**
      * Called when the Export tab button for writing the DUMP_MFU command output is requested by the user.
      * @param view
      */
     public void actionButtonDumpMFU(View view) {
          ExportTools.saveBinaryDumpMFU("mfultralight");
     }

     /**
      * Called when the Export tab button for cloning the DUMP_MFU command output is requested by the user.
      * @param view
      */
     public void actionButtonCloneMFU(View view) {
          ChameleonCommands.cloneMFU();
     }

     /**
      * Called to load the stock card images from stored in the res/raw/* directory.
      * @param view
      */
     public void actionButtonCloneStockDumpImages(View view) {
          String stockChipType = ((Button) view).getTag().toString();
          ChameleonCommands.cloneStockDumpImages(stockChipType);
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
          ChameleonIO.executeChameleonMiniCommand(cmCmd, ChameleonIO.TIMEOUT);
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
      * Wrapper around the button pressed for the download of stored log data and card information
      * by XModem in the Export tab.
      * @param view
      */
     public void actionButtonExportLogDownload(View view) {
          String action = ((Button) view).getTag().toString();
          ExportTools.exportLogDownload(action);
     }

     /**
      * Constant for the file chooser dialog in the upload card data process.
      */
     public static final int FILE_SELECT_CODE = 0;

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
          if(Settings.getActiveSerialIOPort() == null)
               return;
          ChameleonCommands.uploadCardImageByXModem();
     }

     public void actionButtonPerformSearch(View view) {
          // hide the search keyboard obstructing the results after the button press:
          View focusView = this.getCurrentFocus();
          if (focusView != null) {
               InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
          }
          MainActivityLogUtils.performLogSearch();
     }

     public void actionButtonApduCLA(View view) {
          String CLA = ((Button) view).getTag().toString();
          ApduGUITools.apduUpdateCLA(CLA);
     }

     public void actionButtonApduClear(View view) {
          ApduGUITools.apduClearCommand();
     }

     public void actionButtonApduManualDataEntry(View view) {
          AlertDialog alertDialog = ApduGUITools.getApduManualDataEntryDialog();
          alertDialog.show();
     }

     public void actionButtonGetBits(View view) {
          String action = ((Button) view).getTag().toString();
          UIDCommands.getBitsHelper(action);
     }

     public void actionButtonSendAPDU(View view) {
          String sendMode = ((Button) view).getTag().toString();
          ApduGUITools.sendAPDUToChameleon(sendMode);
     }

     public void actionButtonAPDUSearchCmd(View view) {
          String searchText = ((TextView) ApduUtils.tabView.findViewById(R.id.apduSearchText)).getText().toString().toLowerCase();
          ApduGUITools.searchAPDUDatabase(searchText);
     }

     public void actionButtonAPDUCopyCmd(View view) {
          String tagIndex = ((Button) view).getTag().toString();
          int apduCmdIndex = Integer.valueOf(tagIndex);
          ApduGUITools.copyAPDUCommand(apduCmdIndex);
     }

     public static void setSignalStrengthIndicator(int threshold) {
          MainActivityNavActions.setSignalStrengthIndicator(threshold);
     }

     public void actionButtonDisplayHelp(View view) {
          AlertDialog alertDialog = MainActivityNavActions.getHelpTopicsDialog();
          alertDialog.show();
     }

     public void actionButtonSetMinimumLogDataLength(View view) {
          EditText logMinDataLengthField = (EditText) findViewById(R.id.loggingLogDataMinBytesField);
          String fieldText = logMinDataLengthField.getText().toString();
          if(fieldText.length() == 0) {
               return;
          }
          int loggingMinDataLength = ChameleonLogUtils.LOGGING_MIN_DATA_BYTES;
          try {
               loggingMinDataLength = Integer.parseInt(fieldText, 10);
               if(loggingMinDataLength < 0) {
                    return;
               }
               ChameleonLogUtils.LOGGING_MIN_DATA_BYTES = loggingMinDataLength;
               AndroidSettingsStorage.updateValueByKey(AndroidSettingsStorage.LOGGING_MIN_DATA_BYTES);
          }
          catch(Exception ex) {
               ex.printStackTrace();
               Log.i(TAG, ex.getMessage());
               ChameleonLogUtils.LOGGING_MIN_DATA_BYTES = loggingMinDataLength;
          }
     }

     public void actionButtonDESFireTerminalCommand(View view) {
          Button runCmdBtn = (Button) view;
          if(runCmdBtn == null) {
               return;
          }
          String cmdTag = runCmdBtn.getTag().toString();
          EditText piccSetBytesText = (EditText) findViewById(R.id.mfDESFireTagSetPICCDataBytes);
          String piccSetBytes = "";
          if(piccSetBytesText != null) {
               cmdTag = String.format(Locale.ENGLISH, cmdTag, piccSetBytesText.getText().toString());
          }
          ChameleonIO.executeChameleonMiniCommand(cmdTag, ChameleonIO.TIMEOUT);
     }

}