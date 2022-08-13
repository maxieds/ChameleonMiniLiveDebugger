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

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SCRIPTING;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS_MITEM_SLOTS;

import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.maxieds.chameleonminilivedebugger.ScriptingAPI.ScriptingGUIMain;

import java.io.File;
import java.util.List;

/**
 * <h1>Live Logger Activity</h1>
 * Implementation of the main running activity in the application.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public class LiveLoggerActivity extends ChameleonMiniLiveDebuggerActivity implements ActivityPermissions {

     private static final String TAG = LiveLoggerActivity.class.getSimpleName();

     /**
      * Static variables used across classes.
      */
     private static ViewPager viewPager;
     private static int selectedTab = TAB_TOOLS;
     private static final String cmldPackageName = "com.maxieds.chameleonminilivedebugger";

     public static LiveLoggerActivity getLiveLoggerInstance() {
          return (LiveLoggerActivity) runningActivity;
     }

     private static View liveLoggerActivityMainContentView = null;

     public static View getContentView(@IdRes int viewResId) {
          View mainContentView = liveLoggerActivityMainContentView;
          if(mainContentView == null || mainContentView.findViewById(viewResId) == null) {
               mainContentView = LiveLoggerActivity.getLiveLoggerInstance().findViewById(android.R.id.content).getRootView();
               if (mainContentView == null || mainContentView.findViewById(viewResId) == null) {
                    mainContentView = LiveLoggerActivity.getLiveLoggerInstance().getWindow().getDecorView().findViewById(android.R.id.content);
                    if (mainContentView == null || mainContentView.findViewById(viewResId) == null) {
                         mainContentView = LiveLoggerActivity.getLiveLoggerInstance().getWindow().getDecorView().getRootView();
                         if (mainContentView == null || mainContentView.findViewById(viewResId) == null) {
                              mainContentView = LiveLoggerActivity.getLiveLoggerInstance().getWindow().getDecorView();
                              if (mainContentView == null || mainContentView.findViewById(viewResId) == null) {
                                   mainContentView = LiveLoggerActivity.getInstance().findViewById(viewResId);
                                   if (mainContentView == null) {
                                        mainContentView = LiveLoggerActivity.getLiveLoggerInstance().findViewById(android.R.id.content);;
                                        if (mainContentView != null) {
                                             return mainContentView.findViewById(viewResId);
                                        } else {
                                             return null;
                                        }
                                   }
                                   return mainContentView;
                              }
                              return mainContentView.findViewById(viewResId);
                         }
                         return mainContentView.findViewById(viewResId);
                    }
                    return mainContentView.findViewById(viewResId);
               }
          }
          return mainContentView.findViewById(viewResId);
     }

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
          ImageView iconView = findViewById(iconID);
          if(iconView != null) {
               iconView.setAlpha(255);
               iconView.setImageDrawable(getResources().getDrawable(iconDrawable));
          }
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
          ImageView iconView = findViewById(iconID);
          if(iconView != null) {
               iconView.setAlpha(156);
          }
     }

     /**
      * Default handler for  all uncaught exceptions.
      */
     private void setUnhandledExceptionHandler() {
          final AppCompatActivity liveLoggerActivityContext = this;
          Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
               @Override
               public void uncaughtException(Thread paramThread, Throwable paramExcpt) {

                    // For debugging with logcat:
                    paramExcpt.printStackTrace();
                    Throwable[] suppressedExcptLst = paramExcpt.getSuppressed();
                    for(Throwable thEx : suppressedExcptLst) {
                         thEx.printStackTrace();
                    }

                    // Start the crash report activity to display a frontend error explanation to users:
                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                    Utils.clearToastMessage();
                    Intent startCrashRptIntent = new Intent(liveLoggerActivityContext, CrashReportActivity.class);
                    startCrashRptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startCrashRptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startCrashRptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startCrashRptIntent.setAction(CrashReportActivity.INTENT_ACTION_START_ACTIVITY);
                    startCrashRptIntent.setType ("plain/text");
                    String stackTraceAsText = Utils.getStackTraceAsText(paramExcpt);
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_STACK_TRACE, stackTraceAsText);
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_INVOKING_EXCPTMSG, paramExcpt.getMessage());
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_TIMESTAMP, Utils.getTimestamp());
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_CHAMELEON_DEVICE_TYPE, ChameleonIO.CHAMELEON_MINI_BOARD_TYPE_DESC);
                    String chameleonSerialType = ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX < 0 ? "NULL" :
                                      (ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX == ChameleonSettings.USBIO_IFACE_INDEX ? "USB" : "BT");
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_SERIAL_CONNECTION_TYPE, chameleonSerialType);
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_CHAMELEON_CONFIG, ChameleonIO.DeviceStatusSettings.CONFIG);
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_CHAMELEON_LOGMODE, ChameleonIO.DeviceStatusSettings.LOGMODE);
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_CHAMELEON_TIMEOUT, ChameleonIO.DeviceStatusSettings.TIMEOUT);
                    startCrashRptIntent.putExtra(CrashReportActivity.INTENT_LOG_FILE_DOWNLOAD_PATH, AndroidLogger.downloadCurrentLogFile(false));
                    startActivity(startCrashRptIntent);
                    liveLoggerActivityContext.finish();
                    System.exit(-1);
               }
          });
     }

     private static BroadcastReceiver serialIOActionReceiver = null;
     private static IntentFilter serialIOActionFilter = null;
     private static boolean serialIOReceiversRegistered = false;

     public void reconfigureSerialIODevices() {
          if(!serialIOReceiversRegistered) {
               if(serialIOActionReceiver == null) {
                    serialIOActionReceiver = new BroadcastReceiver() {
                         public void onReceive(Context context, Intent intent) {
                              AndroidLogger.i(TAG, intent.getAction());
                              if (intent.getAction() == null) {
                                   return;
                              } else {
                                   onNewIntent(intent);
                              }
                         }
                    };
                    serialIOActionFilter = new IntentFilter();
                    serialIOActionFilter.addAction(SerialUSBInterface.ACTION_USB_PERMISSION);
                    serialIOActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                    serialIOActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                    serialIOActionFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                    serialIOActionFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_NOTIFY_BTDEV_CONNECTED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED);
                    serialIOActionFilter.addAction(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS);
                    serialIOActionFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                    registerReceiver(serialIOActionReceiver, serialIOActionFilter);
                    SerialUSBInterface.registerUSBPermission(null, this);
                    serialIOReceiversRegistered = true;
               }
          }
          ChameleonSettings.initializeSerialIOConnections();
          Handler configDeviceHandler = new Handler();
          Runnable configDeviceRunnable = new Runnable() {
               public void run() {
                    if (ChameleonSettings.getActiveSerialIOPort() == null) {
                         return;
                    }
                    ChameleonIO.detectChameleonType();
                    if(TabFragment.UITAB_DATA != null &&
                            TabFragment.UITAB_DATA[TabFragment.TAB_TOOLS] != null &&
                            TabFragment.UITAB_DATA[TabFragment.TAB_CONFIG] != null) {
                         TabFragment.UITAB_DATA[TabFragment.TAB_TOOLS].changeMenuItemDisplay(TAB_TOOLS_MITEM_SLOTS, true);
                         TabFragment.UITAB_DATA[TabFragment.TAB_CONFIG].changeMenuItemDisplay(TabFragment.TAB_CONFIG_MITEM_LOGGING, true);
                    }
                    ChameleonPeripherals.actionButtonRestorePeripheralDefaults(null);
                    /* Call twice: Make sure the device returned the correct data to display */
                    ChameleonIO.DeviceStatusSettings.updateAllStatusAndPost(false);
                    ChameleonIO.DeviceStatusSettings.updateAllStatusAndPost(false);
                    ChameleonIO.DeviceStatusSettings.startPostingStats(0);
               }
          };
          ChameleonIO.DeviceStatusSettings.stopPostingStats();
          configDeviceHandler.postDelayed(configDeviceRunnable, 0);
     }

     /**
      * Initializes the activity state and variables.
      * Called when the activity is created.
      * @param savedInstanceState
      */
     @Override
     protected void onCreate(Bundle savedInstanceState) {

          super.onCreate(savedInstanceState);
          if(getInstance() == null) {
               AndroidLogger.i(TAG, "Created new activity");
          } else if(!isTaskRoot()) {
               AndroidLogger.i(TAG, "ReLaunch Intent Action: " + getIntent().getAction());
               final Intent intent = getIntent();
               final String intentAction = intent != null ? intent.getAction() : null;
               if (intentAction != null && (intentAction.equals(UsbManager.ACTION_USB_DEVICE_DETACHED) || intentAction.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))) {
                    if(LiveLoggerActivity.getLiveLoggerInstance() != null) {
                         LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(intent);
                    }
                    AndroidLogger.i(TAG, "onCreate(): Main Activity is not the root.  Finishing Main Activity instead to handle USB connection instead of re-launching.");
                    finish();
                    return;
               } else if (intentAction != null && (intentAction.equals(BluetoothDevice.ACTION_FOUND) || intentAction.equals(BluetoothDevice.ACTION_ACL_CONNECTED))) {
                    if(LiveLoggerActivity.getLiveLoggerInstance() != null) {
                         LiveLoggerActivity.getLiveLoggerInstance().onNewIntent(intent);
                    }
                    AndroidLogger.i(TAG, "onCreate(): Main Activity is not the root.  Finishing Main Activity instead to handle USB connection instead of re-launching.");
                    finish();
                    return;
               }
          }

          setUnhandledExceptionHandler();

          boolean completeRestart = (getLiveLoggerInstance() == null);

          AndroidSettingsStorage.loadPreviousSettings();
          if(ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT) {
               GUILogUtils.clearAllLogs();
          }
          Utils.clearToastMessage();
          ThemesConfiguration.setLocalTheme(ThemesConfiguration.storedAppTheme, true, this);
          //ThemesConfiguration.setThemeHandler.postDelayed(ThemesConfiguration.setThemeRunner, 400L);

          setContentView(R.layout.activity_live_logger);

          Toolbar actionBar = (Toolbar) findViewById(R.id.toolbarActionBar);
          actionBar.setSubtitle("Portable logger | v" + String.valueOf(BuildConfig.VERSION_NAME));
          getWindow().setTitleColor(ThemesConfiguration.getThemeColorVariant(R.attr.actionBarBackgroundColor));
          getWindow().setStatusBarColor(ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark));
          getWindow().setNavigationBarColor(ThemesConfiguration.getThemeColorVariant(R.attr.colorPrimaryDark));

          configureTabViewPager();

          if(completeRestart) {
               String[] permissions;
               if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    permissions = new String[]{
                            "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.WRITE_EXTERNAL_STORAGE",
                            "android.permission.INTERNET",
                            "android.permission.USB_PERMISSION",
                            "android.permission.ACCESS_COARSE_LOCATION",
                            "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.VIBRATE",
                            "android.permission.BLUETOOTH",
                            "android.permission.BLUETOOTH_ADMIN",
                    };
               } else {
                    permissions = new String[] {
                            "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.WRITE_EXTERNAL_STORAGE",
                            "android.permission.INTERNET",
                            "android.permission.USB_PERMISSION",
                            "android.permission.VIBRATE",
                            "android.permission.BLUETOOTH_SCAN",
                            "android.permission.BLUETOOTH_CONNECT",
                            "android.permission.BLUETOOTH_ADVERTISE",
                    };
               }
               if (android.os.Build.VERSION.SDK_INT >= 23) {
                    for (int permIdx = 0; permIdx < permissions.length; permIdx++) {
                         String permission = permissions[permIdx];
                         ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                         ActivityCompat.requestPermissions(this, new String[] { permission }, CMLD_PERMS_ALL_REQUEST_CODE);
                    }
               }
               getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
               setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); /* Keep app from crashing when the screen rotates */
          }

          clearStatusIcon(R.id.statusIconUSB);
          clearStatusIcon(R.id.statusIconBT);
          clearStatusIcon(R.id.statusIconUlDl);
          clearStatusIcon(R.id.statusIconNewMsg);
          clearStatusIcon(R.id.statusIconNewXFer);
          clearStatusIcon(R.id.signalStrength);
          clearStatusIcon(R.id.statusCodecRXDataEvent);
          clearStatusIcon(R.id.statusScriptingIsExec);

          if(BuildConfig.PAID_APP_VERSION) {
               String userGreeting = getString(R.string.appInitialUserGreetingMsg);
               GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("WELCOME", userGreeting));
               String disclaimerStmt = getString(R.string.appPaidFlavorDisclaimerEULA);
               GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("DISCLAIMER", disclaimerStmt));
          }
          else {
               String userGreeting = getString(R.string.appInitialUserGreetingMsg);
               GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("WELCOME", userGreeting));
          }

          if(getIntent() != null && getIntent().getBooleanExtra(CrashReportActivity.INTENT_CMLD_RECOVERED_FROM_CRASH, false)) {
               Utils.displayToastMessageLong("Chameleon Mini Live Debugger recovered from crash.");
          }

          final LiveLoggerActivity llActivityFinal = this;
          final Intent onCreateActivityIntentFinal = getIntent();
          Handler runAfterGUIInitDeviceHandler = new Handler();
          Runnable runAfterGUIInitDeviceRunnable = new Runnable() {
               final LiveLoggerActivity llActivity = llActivityFinal;
               final Intent onCreateActivityIntent = onCreateActivityIntentFinal;
               public void run() {
                    liveLoggerActivityMainContentView = findViewById(android.R.id.content);
                    reconfigureSerialIODevices();
                    if (onCreateActivityIntent != null) {
                         llActivity.onNewIntent(onCreateActivityIntent);
                    }
               }
          };
          /* Waiting for more than a second to give the GUI time to display on launch
           * before the BT scanning starts.
           */
          runAfterGUIInitDeviceHandler.postDelayed(runAfterGUIInitDeviceRunnable, 100);

     }

     private static String INTENT_RESTART_ACTIVITY = "LiveLoggerActivity.Intent.Category.RESTART_ACTIVITY";

     @Override
     public void recreate() {
          Intent intent = getIntent();
          if (intent == null) {
               intent = new Intent(Intent.ACTION_MAIN);
          }
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

          GUILogUtils.logDataFeedConfigured = false;
          GUILogUtils.logDataFeed = new LinearLayout(getApplicationContext());
          if(GUILogUtils.logDataEntries != null)
               GUILogUtils.logDataEntries.clear();

          viewPager = (ViewPager) findViewById(R.id.tab_pager);
          if(viewPager != null) {
               viewPager.setId(View.generateViewId());
               TabFragmentPagerAdapter tfPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this);
               viewPager.setAdapter(tfPagerAdapter);
               if (tabChangeListener != null) {
                    viewPager.removeOnPageChangeListener(tabChangeListener);
               }
               tabChangeListener = new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                         if (position < 0) {
                              LiveLoggerActivity.selectedTab = 0;
                              return;
                         }
                         LiveLoggerActivity.selectedTab = position;
                         switch (position) {
                              case TAB_LOG:
                                   LiveLoggerActivity.getLiveLoggerInstance().clearStatusIcon(R.id.statusIconNewMsg);
                                   LiveLoggerActivity.getLiveLoggerInstance().clearStatusIcon(R.id.statusIconNewXFer);
                                   LiveLoggerActivity.getLiveLoggerInstance().clearStatusIcon(R.id.statusIconUlDl);
                                   break;
                              default:
                                   break;
                         }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
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

               /* The view pager hides the tab icons by default, so we reset them to custom icons: */
               tabLayout.getTabAt(TAB_LOG).setIcon(tfPagerAdapter.getTabIcon(TAB_LOG));
               tabLayout.getTabAt(TAB_LOG).setContentDescription(R.string.appGuiTabLogContentDesc);
               tabLayout.getTabAt(TAB_TOOLS).setIcon(tfPagerAdapter.getTabIcon(TAB_TOOLS));
               tabLayout.getTabAt(TAB_TOOLS).setContentDescription(R.string.appGuiTabToolsContentDesc);
               tabLayout.getTabAt(TAB_EXPORT).setIcon(tfPagerAdapter.getTabIcon(TAB_EXPORT));
               tabLayout.getTabAt(TAB_EXPORT).setContentDescription(R.string.appGuiTabExportContentDesc);
               tabLayout.getTabAt(TAB_SCRIPTING).setIcon(tfPagerAdapter.getTabIcon(TAB_SCRIPTING));
               tabLayout.getTabAt(TAB_SCRIPTING).setContentDescription(R.string.appGuiTabScriptingContentDesc);
               tabLayout.getTabAt(TAB_CONFIG).setIcon(tfPagerAdapter.getTabIcon(TAB_CONFIG));
               tabLayout.getTabAt(TAB_CONFIG).setContentDescription(R.string.appGuiTabConfigContentDesc);
          }

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
          if(intent == null || intent.getAction() == null) {
               return;
          }
          AndroidLogger.i(TAG, "NEW INTENT: " + intent.getAction());
          if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND) ||
                  intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED) ||
                  intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) ||
                  intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) ||
                  intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED) ||
                  intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST) ||
                  intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
               if (ChameleonSettings.getActiveSerialIOPort() != null) {
                    /* Another Chameleon devices is already attached and configured, so we ignore the notification. */
               } else if (!ChameleonSettings.allowBluetooth) {
                    /* Warn the user to turn on Bluetooth connections so it is processed: */
                    String userToastMsg = getResources().getString(R.string.bluetoothEnableConnectionInstructions);
                    Utils.displayToastMessageShort(userToastMsg);
               } else if (ChameleonSettings.getBluetoothIOInterface() != null) {
                    ChameleonSettings.getBluetoothIOInterface().broadcastIntent(intent);
               }
          } else if(intent.getAction().equals(SerialUSBInterface.ACTION_USB_PERMISSION)) {
               SerialUSBInterface.usbPermissionsGranted = true;
          } else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
               SerialUSBInterface.registerUSBPermission(intent, this);
               if(ChameleonSettings.serialIOPorts[ChameleonSettings.USBIO_IFACE_INDEX].configureSerial() != 0) {
                    ChameleonSettings.stopSerialIOConnectionDiscovery();
                    ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = ChameleonSettings.USBIO_IFACE_INDEX;
                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                    if(ChameleonLogUtils.CONFIG_CLEAR_LOGS_NEW_DEVICE_CONNNECT) {
                         GUILogUtils.clearAllLogs();
                    }
                    reconfigureSerialIODevices();
                    setStatusIcon(R.id.statusIconUSB, R.drawable.usbconnected16);
               }
          }
          else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
               ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
               if(serialIOPort != null && serialIOPort.isWiredUSB()) {
                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                    ChameleonIO.DeviceStatusSettings.setToolbarStatsToDefault();
                    if(ChameleonIO.WAITING_FOR_RESPONSE) {
                         ChameleonIO.WAITING_FOR_RESPONSE = false;
                    }
                    serialIOPort.shutdownSerial();
                    ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
                    int lastActiveSlotNumber = ChameleonIO.DeviceStatusSettings.DIP_SETTING;
                    if (lastActiveSlotNumber <= ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS.length) {
                         ChameleonConfigSlot.CHAMELEON_DEVICE_CONFIG_SLOTS[lastActiveSlotNumber - 1].disableLayout();
                    }
                    setStatusIcon(R.id.statusIconUSB, R.drawable.usbdisconnected16);
                    clearStatusIcon(R.id.statusIconUSB);
                    ChameleonSettings.initializeSerialIOConnections();
               }
          }
          else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) ||
                  intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)) {
               ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
               if(serialIOPort != null && serialIOPort.isBluetooth()) {
                    ChameleonSettings.stopSerialIOConnectionDiscovery();
                    ChameleonIO.DeviceStatusSettings.stopPostingStats();
                    serialIOPort.shutdownSerial();
                    ChameleonIO.DeviceStatusSettings.setToolbarStatsToDefault();
                    if(ChameleonIO.WAITING_FOR_RESPONSE) {
                         ChameleonIO.WAITING_FOR_RESPONSE = false;
                    }
                    ChameleonSettings.SERIALIO_IFACE_ACTIVE_INDEX = -1;
                    clearStatusIcon(R.id.statusIconBT);
                    ChameleonSettings.initializeSerialIOConnections();
               }
               /*else if (ChameleonSettings.getBluetoothIOInterface() != null) {
                    ChameleonSettings.stopSerialIOConnectionDiscovery();
                    ChameleonSettings.initializeSerialIOConnections();
                    ChameleonSettings.getBluetoothIOInterface().stopScanningDevices();
                    ChameleonSettings.getBluetoothIOInterface().startScanningDevices();
               }*/
          } else if(intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_DEVICE_CONNECTION_LOST)) {
               /* Do nothing. This intent is only broadcast by the IO classes after calling shutdownSerial(). */
          }
          else if(intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_DATA_RECEIVED)) {
               byte[] serialByteData = intent.getByteArrayExtra(ChameleonSerialIOInterface.SERIALIO_BYTE_DATA);
               if (ChameleonLogUtils.ResponseIsLiveLoggingBytes(serialByteData) == 0) {
                    String dataMsg = String.format(BuildConfig.DEFAULT_LOCALE, "Unexpected serial I/O data received (data as log below)");
                    GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", dataMsg));
                    GUILogUtils.appendNewLog(LogEntryUI.newInstance(serialByteData, ""));
               }
          }
          else if(intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_LOGDATA_RECEIVED)) {
               byte[] logDataBytes = intent.getByteArrayExtra(ChameleonSerialIOInterface.SERIALIO_BYTE_DATA);
               boolean duplicateLogData = false;
               if(ChameleonLogUtils.CONFIG_COLLAPSE_COMMON_LOG_ENTRIES) {
                    for(int chIdx = 0; chIdx < GUILogUtils.logDataEntries.size(); chIdx++) {
                         if(!(GUILogUtils.logDataEntries.get(chIdx) instanceof LogEntryUI)) {
                              continue;
                         }
                         LogEntryUI logEntryUI = (LogEntryUI) GUILogUtils.logDataEntries.get(chIdx);
                         if(logEntryUI.logEntryDataEquals(logDataBytes)) {
                              logEntryUI.appendDuplicate(logDataBytes[2], logDataBytes[3]);
                              GUILogUtils.logDataFeed.removeViewAt(chIdx);
                              GUILogUtils.logDataEntries.remove(chIdx);
                              GUILogUtils.appendNewLog(logEntryUI);
                              duplicateLogData = true;
                              break;
                         }
                    }
               }
               if (!duplicateLogData) {
                    GUILogUtils.appendNewLog(LogEntryUI.newInstance(logDataBytes, ""));
               }
          }
          else if(intent.getAction().equals(ChameleonSerialIOInterface.SERIALIO_NOTIFY_STATUS)) {
               String msgType = intent.getStringExtra(ChameleonSerialIOInterface.SERIALIO_STATUS_TYPE);
               String statusMsg = intent.getStringExtra(ChameleonSerialIOInterface.SERIALIO_STATUS_MSG);
               GUILogUtils.appendNewLog(new LogEntryMetadataRecord(defaultInflater, msgType, statusMsg));
          }
     }

     /**
      * Called when the activity is paused or put into the background.
      * @ref onResume()
      */
     @Override
     public void onPause() {
          AndroidSettingsStorage.saveAllSettings();
          Utils.clearToastMessage();
          if(ChameleonSettings.getActiveSerialIOPort() != null) {
               ChameleonSettings.getActiveSerialIOPort().stopScanningDevices();
               ChameleonSettings.getActiveSerialIOPort().shutdownSerial();
               ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
          } else {
               ChameleonSettings.stopSerialIOConnectionDiscovery();
          }
          BluetoothUtils.resetBluetoothAdapterAtClose(this);
          AndroidLogger.closeLogDataOutputFile();
          super.onPause();
     }

     /**
      * Called when the activity is resumes or put into the foreground.
      * @ref onPause()
      */
     @Override
     public void onResume() {
          super.onResume();
          Utils.clearToastMessage();
          AndroidSettingsStorage.loadPreviousSettings();
          BluetoothUtils.resetBluetoothAdapterAtStart(this);
          if(ChameleonSettings.getActiveSerialIOPort() != null) {
               reconfigureSerialIODevices();
               ChameleonSettings.getActiveSerialIOPort().startScanningDevices();
               ChameleonIO.DeviceStatusSettings.startPostingStats(0);
          } else {
               ChameleonSettings.initializeSerialIOConnections();
          }
     }

     @Override
     public void onDestroy() {
          AndroidSettingsStorage.saveAllSettings();
          Utils.clearToastMessage();
          if(ChameleonSettings.getActiveSerialIOPort() != null) {
               ChameleonSettings.getActiveSerialIOPort().stopScanningDevices();
               ChameleonSettings.getActiveSerialIOPort().shutdownSerial();
               ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
          } else {
               ChameleonSettings.stopSerialIOConnectionDiscovery();
          }
          BluetoothUtils.resetBluetoothAdapterAtClose(this);
          AndroidLogger.closeLogDataOutputFile();
          super.onDestroy();
     }

     public String[] getPermissionsByGroup(String groupName) {
          Context activityCtx = getContext();
          PackageManager activityPkgMgr = activityCtx.getPackageManager();
          List<PermissionGroupInfo> lstGroups = activityPkgMgr.getAllPermissionGroups(0);
          for (PermissionGroupInfo pginfo : lstGroups) {
               if(pginfo.name.equals(groupName)) {
                    try {

                         List<PermissionInfo> groupPermsList = activityPkgMgr.queryPermissionsByGroup(pginfo.name, 0);
                         int numGroupPerms = groupPermsList.size();
                         String[] groupPermsArray = new String[numGroupPerms];
                         for(int gpIdx = 0; gpIdx < numGroupPerms; gpIdx++) {
                              activityPkgMgr.addPermission(groupPermsList.get(gpIdx));
                              groupPermsArray[gpIdx] = groupPermsList.get(gpIdx).loadLabel(activityPkgMgr).toString();
                         }
                         return groupPermsArray;
                    } catch (Exception expt) {
                         AndroidLogger.printStackTrace(expt);
                    }
               }
          }
          return null;
     }

     public boolean checkPermissionsAcquired(@NonNull String[] permissions, boolean requestIfNot, View viewToNotify) {
          boolean checkPermsStatus = false;
          if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
               checkPermsStatus = true;
               for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                         checkPermsStatus = false;
                         break;
                    }
               }
          }
          if(viewToNotify != null && !checkPermsStatus && requestIfNot) {
               int hashKeyRequestCode = ActivityPermissions.addToRequestQueue(viewToNotify);
               if(hashKeyRequestCode < 0) {
                    return false;
               }
               ActivityCompat.requestPermissions(this, permissions, hashKeyRequestCode);
               return false;
          }
          return checkPermsStatus;
     }

     public boolean checkPermissionsAcquired(@NonNull String[] permissions, boolean requestIfNot) {
          return checkPermissionsAcquired(permissions, requestIfNot, null);
     }

     public boolean checkPermissionsAcquired(@NonNull String[] permissions) {
          return checkPermissionsAcquired(permissions, true);
     }

     public boolean checkPermissionsAcquired(@NonNull String groupName, boolean requestIfNot, View viewToNotify) {
          String[] groupPermissions = getPermissionsByGroup(groupName);
          if(groupPermissions == null) {
               return false;
          }
          else {
               return checkPermissionsAcquired(groupPermissions, requestIfNot, viewToNotify);
          }
     }

     public boolean checkPermissionsAcquired(@NonNull String groupName, boolean requestIfNot) {
          return checkPermissionsAcquired(groupName, requestIfNot, null);
     }

     public boolean checkPermissionsAcquired(@NonNull String groupName) {
          return checkPermissionsAcquired(groupName, true);
     }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          if(!ActivityPermissions.acquireRequestQueueLock()) {
               return;
          }
          String objHashKey = ActivityPermissions.resolveRequestCodeKey(requestCode);
          if(!ActivityPermissions.REQUEST_QUEUE.containsKey(objHashKey)) {
               ActivityPermissions.PERMS_REQUEST_QUEUE_LOCK.release();
               return;
          }
          View invokingRequestObj = ActivityPermissions.REQUEST_QUEUE.get(objHashKey);
          invokingRequestObj.notify();
          ActivityPermissions.REQUEST_QUEUE.remove(objHashKey);
          ActivityPermissions.PERMS_REQUEST_QUEUE_LOCK.release();
     }

     public void requestAllCMLDPermissionsFromUser() {
          Intent requestCMLDPermsIntent = new Intent(
                  Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS,
                  Uri.fromParts("package", this.cmldPackageName, null)
          );
          startActivityForResult(requestCMLDPermsIntent, ActivityPermissions.CMLD_PERMS_ALL_REQUEST_CODE);
          Runnable displayToastMsgRunner = new Runnable() {
               @Override
               public void run() {
                    Utils.displayToastMessageShort("Requested all CMLD permissions.");
               }
          };
          runOnUiThread(displayToastMsgRunner);
     }

     /**
      * Exits the application.
      * @param view
      * @see res/layout/activity_live_logger.xml
      */
     public void actionButtonExit(@NonNull View view) {
          ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
          ChameleonSerialIOInterface serialIOPort = ChameleonSettings.getActiveSerialIOPort();
          if(serialIOPort != null) {
               serialIOPort.shutdownSerial();
          }
          NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          if(notifyMgr != null) {
               notifyMgr.cancel(1);
          }
          finish();
     }

     /**
      * Manual refreshing of the device status settings requested by the user on button press at the
      * top right (second rightmost button) of the activity window.
      * @param view
      */
     public void actionButtonRefreshDeviceStatus(@NonNull View view) {
          ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
          ChameleonIO.deviceStatus.updateAllStatusAndPost(false); /* Make sure the device returned the correct data to display */
     }

     public void actionButtonAppSettings(@NonNull View view) {
          ThemesConfiguration.actionButtonAppSettings(view);
     }

     /**
      * Clears all logging data from the Log tab.
      * @param view
      */
     public void actionButtonClearAllLogs(@NonNull View view) {
          GUILogUtils.clearAllLogs();
     }

     /**
      * Handles button presses for most of the commands implemented in the Tools Menu.
      * @param view calling Button
      */
     public void actionButtonCreateNewEvent(@NonNull View view) {
          if(ChameleonSettings.getActiveSerialIOPort() == null) {
               GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Cannot run command since serial IO over USB/BT is not configured."));
               return;
          }
          Button srcBtn = (Button) view;
          String createCmd = srcBtn.getText().toString();
          String btnTagValue = srcBtn.getTag() != null ? srcBtn.getTag().toString() : null;
          if(btnTagValue != null && !btnTagValue.equals("")) {
               String msgParam = "";
               createCmd = "CONFIG=" + btnTagValue;
               if(!ChameleonIO.REVE_BOARD) {
                    msgParam = ChameleonIO.getSettingFromDevice(createCmd);
               }
               else {
                    msgParam = ChameleonIO.getSettingFromDevice(createCmd);
               }
               GUILogUtils.appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(createCmd, msgParam));
               ChameleonIO.deviceStatus.updateAllStatusAndPost(false);
               ChameleonIO.deviceStatus.updateAllStatusAndPost(false); /* Make sure the device returned the correct data to display */
          }
          else {
               ChameleonCommands.createNewCommandEvent(createCmd);
          }
     }

     public void actionButtonModifyUID(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String uidAction = srcBtn.getTag().toString();
               UIDCommands.modifyUID(uidAction);
          }
     }

     /**
      * Constructs and displays a dialog providing meta information about the application.
      * @param view
      * @ref R.string.aboutapp
      */
     public void actionButtonAboutTheApp(@NonNull View view) {
          AlertDialog alertDialog = MainActivityNavActions.getAboutTheAppDialog();
          alertDialog.show();
     }

     /**
      * Runs a command indicated in the TAG parameter of the pressed button.
      * @param view pressed Button
      */
     public void actionButtonRunCommand(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String cmCmd = srcBtn.getTag().toString();
               ChameleonCommands.runCommand(cmCmd);
          }
     }

     public void actionButtonCollapseSimilar(@NonNull View view) {
          GUILogUtils.collapseSimilarLogs();
     }

     public void actionButtonSelectedHighlight(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               int highlightColor = Color.parseColor(srcBtn.getTag().toString());
               GUILogUtils.selectedHighlightedLogs(highlightColor);
          }
     }

     public void actionButtonUncheckAll(@NonNull View view) {
          GUILogUtils.uncheckAllLogs();
     }

     public void actionButtonSetSelectedXFer(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               int directionFlag = Integer.parseInt(srcBtn.getTag().toString());
               GUILogUtils.setSelectedXFerOnLogs(directionFlag);
          }
     }

     public void actionButtonProcessBatch(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String actionFlag = srcBtn.getTag().toString();
               GUILogUtils.processBatchOfSelectedLogs(actionFlag);
          }
     }

     /**
      * Wrapper around the first three buttons at the top of the Export tab for writing the
      * logs to Plaintext / HTML / native binary files formats.
      * @param view pressed Button
      */
     public void actionButtonWriteFile(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String fileType = srcBtn.getTag().toString();
               ExternalFileIO.exportOutputFile(fileType);
          }
     }

     /**
      * Called when the Export tab button for writing the DUMP_MFU command output is requested by the user.
      * @param view
      */
     public void actionButtonDumpMFU(@NonNull View view) {
          ExportTools.saveBinaryDumpMFU("mfultralight");
     }

     /**
      * Called when the Export tab button for cloning the DUMP_MFU command output is requested by the user.
      * @param view
      */
     public void actionButtonCloneMFU(@NonNull View view) {
          ChameleonCommands.cloneMFU();
     }

     /**
      * Called to load the stock card images from stored in the res/raw/* directory.
      * @param view
      */
     public void actionButtonCloneStockDumpImages(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String stockChipType = srcBtn.getTag().toString();
               ChameleonCommands.cloneStockDumpImages(stockChipType);
          }
     }

     /**
      * Called when one of the command Spinner buttons changes state.
      * @param view calling Spinner
      * @ref TabFragment.connectCommandListSpinnerAdapter
      * @ref TabFragment.connectPeripheralSpinnerAdapter
      */
     public static void actionSpinnerSetCommand(@NonNull View view) {
          Spinner srcView = (Spinner) view;
          if(srcView != null) {
               String sopt = srcView.getSelectedItem().toString();
               if (sopt.substring(0, 2).equals("--"))
                    sopt = "NONE";
               String cmCmd = srcView.getTag().toString() + sopt;
               ChameleonIO.executeChameleonMiniCommand(cmCmd, ChameleonIO.TIMEOUT);
          }
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
     public void actionButtonExportLogDownload(@NonNull View view) {
          Button srcBtn = (Button) view;
          if (srcBtn != null) {
               String action = srcBtn.getTag().toString();
               ExportTools.exportLogDownload(action);
          }
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          String toastStatusMsg = "";
          if (requestCode == CMLD_PERMS_ALL_REQUEST_CODE) {
               if(BluetoothUtils.isStatusResultCodeError(resultCode)) {
                    toastStatusMsg = BluetoothUtils.getStatusResultCodeErrorString(resultCode);
                    finish();
               } else {
                    toastStatusMsg = "All CMLD app permissions requested. Enable all to ensure the app runs correctly.";
               }
          } else if (resultCode == BluetoothUtils.ACTVITY_REQUEST_BLUETOOTH_ENABLED_CODE) {
               if(BluetoothUtils.isStatusResultCodeError(resultCode)) {
                    toastStatusMsg = BluetoothUtils.getStatusResultCodeErrorString(resultCode);
                    finish();
               } else {
                    toastStatusMsg = "Bluetooth permissions enabled.";
               }
          } else if (resultCode == BluetoothUtils.ACTVITY_REQUEST_BLUETOOTH_DISCOVERABLE_CODE) {
               if(BluetoothUtils.isStatusResultCodeError(resultCode)) {
                    toastStatusMsg = BluetoothUtils.getStatusResultCodeErrorString(resultCode);
                    finish();
               } else {
                    toastStatusMsg = "Bluetooth (discoverable) permissions enabled.";
               }
          } else if(resultCode == CMLD_PERMGROUP_STORAGE_REQUEST_CODE ||
                  resultCode == AndroidFileChooser.CMLD_PERMGROUP_STORAGE_REQUEST_CODE) {
               if(resultCode == Activity.RESULT_CANCELED) {
                    finish();
                    return;
               } else {
                    toastStatusMsg = "Storage permissions enabled.";
               }
          }  else if(resultCode == CMLD_PERMGROUP_MINIMAL_REQUEST_CODE) {
               if(resultCode == Activity.RESULT_CANCELED) {
                    finish();
                    return;
               } else {
                    toastStatusMsg = "USB and other minimal CMLD app permissions enabled.";
               }
          } else {
               try {
                    ExternalFileIO.handleActivityResult(this, requestCode, resultCode, data);
               } catch(RuntimeException  rte) {
                    if(rte.getMessage() != null && rte.getMessage().length() > 0) {
                         AndroidLogger.printStackTrace(rte);
                         return;
                    }
               }
          }
          super.onActivityResult(requestCode, resultCode, data);
          final String toastStatusMsgFinal = toastStatusMsg;
          if (!toastStatusMsg.equals("")) {
               Runnable displayToastMsgRunner = new Runnable() {
                    final String statusMsg = toastStatusMsgFinal;
                    @Override
                    public void run() {
                         Utils.displayToastMessageShort(statusMsg);
                    }
               };
               runOnUiThread(displayToastMsgRunner);
          }
     }

     /**
      * Wrapper around the card upload feature.
      * The method has the user pick a saved card file from the /sdcard/Download/* folder, then
      * initiates the upload with the function in ExportTools.
      * @param view pressed Button
      */
     public void actionButtonUploadCard(@NonNull View view) {
          if(ChameleonSettings.getActiveSerialIOPort() == null) {
               return;
          }
          ChameleonCommands.uploadCardImageByXModem();
     }

     public void actionButtonPerformSearch(@NonNull View view) {
          /* Hide the search keyboard obstruction on the screen after the button is pressed: */
          View focusView = this.getCurrentFocus();
          if (focusView != null) {
               InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
          }
          GUILogUtils.performLogSearch();
     }

     public void actionButtonApduCLA(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String CLA = srcBtn.getTag().toString();
               ApduGUITools.apduUpdateCLA(CLA);
          }
     }

     public void actionButtonApduClear(@NonNull View view) {
          ApduGUITools.apduClearCommand();
     }

     public void actionButtonApduManualDataEntry(@NonNull View view) {
          AlertDialog alertDialog = ApduGUITools.getApduManualDataEntryDialog();
          alertDialog.show();
     }

     public void actionButtonGetBits(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String action = srcBtn.getTag().toString();
               UIDCommands.getBitsHelper(action);
          }
     }

     public void actionButtonSendAPDU(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String sendMode = srcBtn.getTag().toString();
               ApduGUITools.sendAPDUToChameleon(sendMode, false);
          }
     }

     public void actionButtonSendRawAPDU(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String sendMode = srcBtn.getTag().toString();
               ApduGUITools.sendAPDUToChameleon(sendMode, true);
          }
     }

     public void actionButtonAPDUSearchCmd(@NonNull View view) {
          TextView tvRef = (TextView) ApduUtils.tabView.findViewById(R.id.apduSearchText);
          if(tvRef != null) {
               String searchText = tvRef.getText().toString().toLowerCase();
               ApduGUITools.searchAPDUDatabase(searchText);
          }
     }

     public void actionButtonAPDUCmd(@NonNull View view) {
          Button srcBtn = (Button) view;
          if(srcBtn != null) {
               String tagIndex = srcBtn.getTag().toString();
               int apduCmdIndex = Integer.valueOf(tagIndex);
               ApduGUITools.copyAPDUCommand(apduCmdIndex);
          }
     }

     public static void setSignalStrengthIndicator(int threshold) {
          MainActivityNavActions.setSignalStrengthIndicator(threshold);
     }

     public void actionButtonSetMinimumLogDataLength(@NonNull View view) {
          EditText logMinDataLengthField = (EditText) findViewById(R.id.loggingLogDataMinBytesField);
          if(logMinDataLengthField == null) {
               return;
          }
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
               AndroidLogger.printStackTrace(ex);
               AndroidLogger.i(TAG, ex.getMessage());
               ChameleonLogUtils.LOGGING_MIN_DATA_BYTES = loggingMinDataLength;
          }
     }

     public void actionButtonDESFireTerminalCommand(@NonNull View view) {
          Button runCmdBtn = (Button) view;
          if(runCmdBtn == null) {
               return;
          }
          String cmdTag = runCmdBtn.getTag().toString();
          EditText piccSetBytesText = (EditText) findViewById(R.id.mfDESFireTagSetPICCDataBytes);
          if(piccSetBytesText != null) {
               String piccSetBytes = piccSetBytesText.getText().toString();
               String[] cmdTagComps = cmdTag.split(":");
               if (cmdTagComps.length != 2) {
                    String toastErrorMsg = "Invalid button tag. Consider reporting this as an issue on GitHub.";
                    Utils.displayToastMessageShort(toastErrorMsg);
                    return;
               }
               int cmdReqNumBytes = Integer.parseInt(cmdTagComps[1]);
               if (piccSetBytes.length() != 2 * cmdReqNumBytes) {
                    String toastErrorMsg = String.format(BuildConfig.DEFAULT_LOCALE, "Invalid number of bytes. The command requires %d bytes.", cmdReqNumBytes);
                    Utils.displayToastMessageShort(toastErrorMsg);
                    return;
               }
               String chamCmd = String.format(BuildConfig.DEFAULT_LOCALE, cmdTagComps[0], piccSetBytes);
               ChameleonIO.executeChameleonMiniCommand(chamCmd, ChameleonIO.TIMEOUT);
          }
     }

     public void actionButtonScriptingGUIHandlePerformTaskClick(@NonNull View view) {
          ScriptingGUIMain.scriptGUIHandlePerformTaskClick((Button) view, view.getTag().toString());
     }

     public void copyButtonTagToClipboard(@NonNull View btn) {
          Button srcBtn = (Button) btn;
          if(srcBtn != null) {
               String clipBoardText = srcBtn.getTag().toString();
               Utils.copyTextToClipboard(this, clipBoardText, true);
          }
     }

     public void actionButtonDownloadCurrentLogFile(@NonNull View btn) {
          AndroidLogger.downloadCurrentLogFile(true);
     }

     public void actionButtonClearAllLogFiles(@NonNull View btn) {
          AndroidLogger.closeLogDataOutputFile();
          String localAppStoragePath = LiveLoggerActivity.getLiveLoggerInstance().getFilesDir().getAbsolutePath();
          String logDataOutputFilePath = localAppStoragePath + "//" + AndroidLogger.LOGDATA_FILE_LOCAL_DIRPATH;
          File logDataOutputFolder = new File(logDataOutputFilePath);
          if (!logDataOutputFolder.exists() || !logDataOutputFolder.delete()) {
               Utils.displayToastMessageShort("Error clearing stored log files.");
               Intent requestCMLDPermsIntent = new Intent(
                       Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                       Uri.fromParts("package", this.cmldPackageName, null)
               );
               startActivityForResult(requestCMLDPermsIntent, CMLD_PERMS_ALL_REQUEST_CODE);
          } else {
               Utils.displayToastMessageShort("Deleted all logs.");
          }
     }

}