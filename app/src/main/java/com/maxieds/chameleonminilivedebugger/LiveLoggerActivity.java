package com.maxieds.chameleonminilivedebugger;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;

import io.fabric.sdk.android.Fabric;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_APDU;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_SEARCH;
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
     public static LiveLoggerActivity runningActivity = null;
     Bundle localSavedInstanceState;

     /**
      * Static variables used across classes.
      */
     public static LayoutInflater defaultInflater;
     public static Context defaultContext;
     public static LinearLayout logDataFeed;
     public static List<LogEntryBase> logDataEntries = new ArrayList<LogEntryBase>();
     public static int RECORDID = 0;
     public static boolean logDataFeedConfigured = false;
     public static ScrollView logScrollView;
     public static SpinnerAdapter spinnerRButtonAdapter;
     public static SpinnerAdapter spinnerRButtonLongAdapter;
     public static SpinnerAdapter spinnerLButtonAdapter;
     public static SpinnerAdapter spinnerLButtonLongAdapter;
     public static SpinnerAdapter spinnerLEDRedAdapter;
     public static SpinnerAdapter spinnerLEDGreenAdapter;
     public static SpinnerAdapter spinnerButtonMyAdapter;
     public static SpinnerAdapter spinnerLogModeAdapter;
     public static SpinnerAdapter spinnerCmdShellAdapter;
     public static MenuItem selectedThemeMenuItem;
     public static boolean userIsScrolling = false;
     private static ViewPager viewPager;
     private static int selectedTab = TAB_LOG;

     /**
      * Configuration of the USB serial port.
      */
     public static UsbSerialDevice serialPort;
     public static final Semaphore serialPortLock = new Semaphore(1, true);
     private boolean usbReceiversRegistered = false;
     public static final int USB_DATA_BITS = 16;

     /**
      * Appends a new log to the logging interface tab.
      * @param logEntry
      * @see LogEntryUI
      * @see LogEntryMetadataRecord
      */
     public static void appendNewLog(LogEntryBase logEntry) {
          if(LiveLoggerActivity.selectedTab != TAB_LOG && LiveLoggerActivity.runningActivity != null) {
               if(logEntry instanceof LogEntryUI)
                    runningActivity.setStatusIcon(R.id.statusIconNewXFer, R.drawable.statusxfer16);
               else
                    runningActivity.setStatusIcon(R.id.statusIconNewMsg, R.drawable.statusnewmsg16);
          }
          if(logDataFeed != null && logDataEntries != null) {
               logDataFeed.addView(logEntry.getLayoutContainer());
               logDataEntries.add(logEntry);
          }
          if(LiveLoggerActivity.runningActivity == null) {
               return;
          }
          if(logEntry instanceof LogEntryMetadataRecord) { // switch to the log tab to display the results:
               Log.i(TAG, String.format("LogEntryMetaData record height: %d", logEntry.getLayoutContainer().getHeight()));
               TabLayout tabLayout = (TabLayout) LiveLoggerActivity.runningActivity.findViewById(R.id.tab_layout);
               if(tabLayout != null) {
                    tabLayout.getTabAt(TAB_LOG).select();
               }
               if(logScrollView != null) {
                    logScrollView.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                              ScrollView logScroller = (ScrollView) LiveLoggerActivity.runningActivity.findViewById(R.id.log_scroll_view);
                              if(logScroller == null) {
                                   return;
                              }
                              LinearLayout lastLogElt = (LinearLayout) logDataFeed.getChildAt(logDataFeed.getChildCount() - 1);
                              lastLogElt.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                              int bottomEltHeight = lastLogElt.getMeasuredHeight();
                              Log.i(TAG, String.format("ScrollView bottom element height = %d + getBottom() = %d", bottomEltHeight, logScroller.getBottom()));
                              logScroller.scrollTo(0, logScroller.getBottom() + bottomEltHeight);
                         }
                    }, 100);
               }
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
      * Obtains the color associated with the theme.
      * @param attrID
      * @return
      */
     @ColorInt
     public int getThemeColorVariant(int attrID) {
          return getTheme().obtainStyledAttributes(new int[] {attrID}).getColor(0, attrID);
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
               appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("UNRECOGNIZED EXCEPTION", msgParam));
               Crashlytics.log(Log.ERROR, TAG, msgParam + "\n\n" + Log.getStackTraceString(ex));
               Crashlytics.logException(ex);
               System.exit(-1);
          }
     };

     /**
      * Attempts to set themes will a background before the current tab has been loaded will
      * result in a NullPointerException getting issued by the system. We fix this by setting the
      * theme about 1 second after the application's onCreate(...) method is invoked.
      */
     String storedAppTheme = "Light Green";
     Handler setThemeHandler = new Handler();
     Runnable setThemeRunner = new Runnable() {
          @Override
          public void run() {
               setLocalTheme(storedAppTheme, true);
          }
     };

     private static BroadcastReceiver usbActionReceiver = null;
     private static IntentFilter usbActionFilter = null;

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
               /*boolean fbIsInit = false;
               try {
                    super.onCreate(savedInstanceState); // should fix most of the crashes in the ANR report on Play Store
               } catch(IllegalStateException ise) { // not sure why this gets reported on Android 8.0:
                    Log.e(TAG, String.format(Locale.ENGLISH, "ISE (on Android %s, API %s / %s): %s",
                          android.os.Build.VERSION.RELEASE, android.os.Build.VERSION.SDK_INT,
                          String.valueOf(android.os.Build.VERSION.RELEASE), ise.getMessage()));
                    ise.printStackTrace();
                    Fabric.with(this, new Crashlytics());
                    fbIsInit = true;
                    Crashlytics.log(Log.ERROR, TAG, "Encountered unexpected ISE (under normal conditions): " + ise.getMessage());
                    Crashlytics.logException(ise);
               }
               if(!fbIsInit) {
                    Fabric.with(this, new Crashlytics());
               }*/
               Fabric.with(this, new Crashlytics());
               Log.w(TAG, "Created new activity");
          }
          if(!isTaskRoot()) {
               Log.w(TAG, "ReLaunch Intent Action: " + getIntent().getAction());
               final Intent intent = getIntent();
               final String intentAction = intent.getAction();
               if (intentAction != null && (intentAction.equals(UsbManager.ACTION_USB_DEVICE_DETACHED) || intentAction.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))) {
                    Log.w(TAG, "onCreate(): Main Activity is not the root.  Finishing Main Activity instead of re-launching.");
                    finish();
                    ChameleonIO.USB_CONFIGURED = false;
                    return;
               }
          }

          boolean completeRestart = (runningActivity == null);
          runningActivity = this;
          localSavedInstanceState = savedInstanceState;
          defaultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          defaultContext = getApplicationContext();

          if(completeRestart) {
               SharedPreferences preferences = getSharedPreferences(LiveLoggerActivity.TAG, Context.MODE_PRIVATE);
               storedAppTheme = preferences.getString("ThemeUI", "Standard Green");
               setLocalTheme(storedAppTheme, false); // set the base colors, not the backgrounds initially
               setThemeHandler.postDelayed(setThemeRunner, 400);
               //setLocalTheme(storedAppTheme, true);
          }
          setContentView(R.layout.activity_live_logger);

          Toolbar actionBar = (Toolbar) findViewById(R.id.toolbarActionBar);
          actionBar.setSubtitle("Portable logging interface v" + String.valueOf(BuildConfig.VERSION_NAME));
          clearStatusIcon(R.id.statusIconUlDl);
          getWindow().setTitleColor(getThemeColorVariant(R.attr.actionBarBackgroundColor));
          getWindow().setStatusBarColor(getThemeColorVariant(R.attr.colorPrimaryDark));
          getWindow().setNavigationBarColor(getThemeColorVariant(R.attr.colorPrimaryDark));

          configureTabViewPager();

          if(completeRestart) {
               String[] permissions = {
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.INTERNET",
                    "com.android.example.USB_PERMISSION"
               };
               if (android.os.Build.VERSION.SDK_INT >= 23)
                    requestPermissions(permissions, 200);
               getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
               setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // keep app from crashing when the screen rotates
          }

          if(!usbReceiversRegistered) {
               serialPort = configureSerialPort(null, usbReaderCallback);
               if (serialPort != null)
                    ChameleonIO.deviceStatus.updateAllStatusAndPost(true);

               usbActionReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                         if (intent.getAction() != null && (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) || intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED))) {
                              onNewIntent(intent);
                         }
                    }
               };
               usbActionFilter = new IntentFilter();
               usbActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
               usbActionFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
               registerReceiver(usbActionReceiver, usbActionFilter);
               usbReceiversRegistered = true;
          }

          String userGreeting = "Dear new user, \n\nThank you for using and testing this app. It is still under development. We are working to fix any new errors" +
               " on untested devices using Crashlytics. While this will eventually fix most unforseen errors that slip through testing, *PLEASE* " +
               "if you consistently get a runtime error using a feature notify the developer at maxieds@gmail.com so it can be fixed quickly for all users.\n\n" +
               "Enjoy the app and using your Chameleon Mini device!";
          appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("STATUS", userGreeting));

          clearStatusIcon(R.id.statusIconNewMsg);
          clearStatusIcon(R.id.statusIconNewXFer);
          clearStatusIcon(R.id.signalStrength);

     }

     private static String INTENT_RESTART_ACTIVITY = "LiveLoggerActivity.Intent.Category.RESTART_ACTIVITY";

     @Override
     public void recreate() {

          unregisterReceiver(usbActionReceiver);
          usbReceiversRegistered = false;
          runningActivity = null;

          //super.recreate();
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

          logDataFeedConfigured = false;
          logDataFeed = new LinearLayout(getApplicationContext());
          if(logDataEntries != null)
               logDataEntries.clear();

          viewPager = (ViewPager) findViewById(R.id.tab_pager);
          viewPager.setId(View.generateViewId()); // should fix some ResourceNotFoundExceptions generated
          TabFragmentPagerAdapter tfPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this);
          viewPager.setAdapter(tfPagerAdapter);
          if(tabChangeListener != null) {
               viewPager.removeOnPageChangeListener(tabChangeListener);
          }
          tabChangeListener = new ViewPager.OnPageChangeListener() {
               @Override
               public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               }

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
               public void onPageScrollStateChanged(int state) {
               }
          };
          viewPager.addOnPageChangeListener(tabChangeListener);

          TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
          tabLayout.removeAllTabs();
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_LOG)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_TOOLS)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_LOG_TOOLS)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_EXPORT)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_SEARCH)));
          tabLayout.addTab(tabLayout.newTab().setText(tfPagerAdapter.getPageTitle(TAB_APDU)));
          tabLayout.setupWithViewPager(viewPager);

          viewPager.setOffscreenPageLimit(TabFragmentPagerAdapter.TAB_COUNT - 1);
          viewPager.setCurrentItem(selectedTab);
          tfPagerAdapter.notifyDataSetChanged();

          // the view pager hides the tab icons by default, so we reset them:
          tabLayout.getTabAt(TAB_LOG).setIcon(R.drawable.nfc24v1);
          tabLayout.getTabAt(TAB_TOOLS).setIcon(R.drawable.tools24);
          tabLayout.getTabAt(TAB_LOG_TOOLS).setIcon(R.drawable.logtools24);
          tabLayout.getTabAt(TAB_EXPORT).setIcon(R.drawable.insertbinary24);
          tabLayout.getTabAt(TAB_SEARCH).setIcon(R.drawable.binarysearch24v2);
          tabLayout.getTabAt(TAB_APDU).setIcon(R.drawable.command24);

     }

     /**
      * Sets the local theme (before the ful UI updating to implement the theme change) based on
      * the passed theme text description.
      * @param themeDesc
      * @ref res/values/style.xml
      */
     private void setLocalTheme(String themeDesc, boolean canResetBackgroundData) {
          int themeID;
          boolean resetBackground = false;
          int bgResID = 0;
          switch(themeDesc) {
               case "Amber":
                    themeID = R.style.AppThemeAmber;
                    break;
               case "Animal":
                    themeID = R.style.AppThemeAnimal;
                    resetBackground = true;
                    bgResID = R.drawable.animal_print_background;
                    break;
               case "Atlanta":
                    themeID = R.style.AppThemeAtlanta;
                    break;
               case "Black":
                    themeID = R.style.AppThemeBlack;
                    break;
               case "Blue":
                    themeID = R.style.AppThemeBlue;
                    break;
               case "Chocolate":
                    themeID = R.style.AppThemeChocolate;
                    break;
               case "Chicky":
                    themeID = R.style.AppThemeChicky;
                    break;
               case "Goldenrod":
                    themeID = R.style.AppThemeGoldenrod;
                    break;
               case "Standard Green":
                    themeID = R.style.AppThemeGreen;
                    break;
               case "Lightblue":
                    themeID = R.style.AppThemeLightblue;
                    break;
               case "Lightening":
                    themeID = R.style.AppThemeLightening;
                    resetBackground = true;
                    bgResID = R.drawable.lightening_gradient;
                    break;
               case "Linux Green On Black":
                    themeID = R.style.AppThemeLinuxGreenOnBlack;
                    break;
               case "Miss Swirly":
                    themeID = R.style.AppThemeMissSwirly;
                    resetBackground = true;
                    bgResID = R.drawable.mrswirly_background;
                    break;
               case "Purple":
                    themeID = R.style.AppThemePurple;
                    break;
               case "Red":
                    themeID = R.style.AppThemeRed;
                    break;
               case "Redmond":
                    themeID = R.style.AppThemeRedmond;
                    break;
               case "Teal":
                    themeID = R.style.AppThemeTeal;
                    break;
               case "Urbana Desfire":
                    themeID = R.style.AppThemeUrbanaDesfire;
                    break;
               case "White":
                    themeID = R.style.AppThemeWhite;
                    break;
               case "Winter":
                    themeID = R.style.AppThemeWinter;
                    break;
               default:
                    themeID = R.style.AppThemeGreen;
          }
          Log.w(TAG, themeDesc);
          Log.w(TAG, String.valueOf(themeID));
          setTheme(themeID);
          if(canResetBackgroundData && resetBackground) {
               ((ScrollView) findViewById(R.id.log_scroll_view)).setBackgroundColor(getResources().getColor(R.color.transparent, getTheme()));
               ((ScrollView) findViewById(R.id.log_scroll_view)).setBackground(getResources().getDrawable(bgResID, getTheme()));
               LiveLoggerActivity.logDataFeed.setBackgroundColor(getResources().getColor(R.color.transparent, getTheme()));
          }

     }

     public void actionButtonAppSettings(View view) {
          AlertDialog.Builder dialog = new AlertDialog.Builder(this);
          final View dialogView = getLayoutInflater().inflate(R.layout.theme_config, null);
          if(!BuildConfig.FLAVOR.equals("paid")) { // restore the "bonus" for upgrading to the paid flavor:
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonAtlanta)).setEnabled(false);
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonBlack)).setEnabled(false);
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonChocolate)).setEnabled(false);
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonGoldenrod)).setEnabled(false);
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonLightblue)).setEnabled(false);
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonPurple)).setEnabled(false);
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonUrbanaDesfire)).setEnabled(false);
               ((RadioButton) dialogView.findViewById(R.id.themeRadioButtonWinter)).setEnabled(false);
          }
          // set the correct current theme as the selected radio button:
          RadioGroup themeRadioGroup = (RadioGroup) dialogView.findViewById(R.id.themeRadioGroup);
          for(int rb = 0; rb < themeRadioGroup.getChildCount(); rb++) {
               RadioButton curThemeBtn = (RadioButton) themeRadioGroup.getChildAt(rb);
               if(curThemeBtn.isEnabled() && curThemeBtn.getText().toString().equals("Theme: " + storedAppTheme)) {
                    curThemeBtn.setChecked(true);
                    break;
               }
          }
          // finish constructing the theme selection dialog:
          ScrollView themesScroller = new ScrollView(this);
          themesScroller.addView(dialogView);
          dialog.setView(themesScroller);
          dialog.setIcon(R.drawable.settingsgears24);
          dialog.setTitle( "Application Theme Configuration: \n(Clears all logs.)");
          dialog.setPositiveButton( "Set Theme", new DialogInterface.OnClickListener(){
               @Override
               public void onClick(DialogInterface dialog, int whichBtn) {

                    int getSelectedOption = ((RadioGroup) dialogView.findViewById(R.id.themeRadioGroup)).getCheckedRadioButtonId();
                    String themeID = ((RadioButton) dialogView.findViewById(getSelectedOption)).getText().toString();
                    String themeDesc = themeID.substring("Theme: ".length());
                    setLocalTheme(themeDesc, true);
                    storedAppTheme = themeDesc;

                    // store the theme setting for when the app reopens:
                    SharedPreferences sharedPrefs = getSharedPreferences(LiveLoggerActivity.TAG, Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sharedPrefs.edit();
                    spEditor.putString("ThemeUI", themeDesc);
                    spEditor.commit();

                    // finally, apply the theme settings by (essentially) restarting the activity UI:
                    //onCreate(localSavedInstanceState);
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("THEME", "New theme installed: " + themeDesc));

                    LiveLoggerActivity.runningActivity.recreate();

               }

          });
          dialog.setNegativeButton( "Cancel", null);
          dialog.setInverseBackgroundForced(true);
          dialog.show();
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
          if(intent == null)
               return;
          else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
               if(serialPort != null && ChameleonIO.USB_CONFIGURED)
                    return;
               ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
               closeSerialPort(serialPort);
               serialPort = configureSerialPort(null, usbReaderCallback);
               LiveLoggerActivity.runningActivity.actionButtonRestorePeripheralDefaults(null);
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
               ChameleonIO.USB_CONFIGURED = true;
          }
          else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
               ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
               if(ChameleonIO.WAITING_FOR_RESPONSE)
                    ChameleonIO.WAITING_FOR_RESPONSE = false;
               closeSerialPort(serialPort);
               ChameleonIO.USB_CONFIGURED = false;
          }
     }

     /**
      * Called when the activity is paused or put into the background.
      * @ref onResume()
      */
     @Override
     public void onPause() {
          super.onPause();
          // clear the status icon before the application is abruptly terminated:
          /*
          ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
          */
     }

     /**
      * Called when the activity is resumes or put into the foreground.
      * @ref onPause()
      */
     @Override
     public void onResume() {
          super.onResume();
          /*
          // setup the system status bar icon:
          NotificationCompat.Builder statusBarIconBuilder = new NotificationCompat.Builder(this);
          statusBarIconBuilder.setSmallIcon(R.drawable.chameleonstatusbaricon16);
          statusBarIconBuilder.setContentTitle(getResources().getString(R.string.app_name) + " -- v" + BuildConfig.VERSION_NAME);
          statusBarIconBuilder.setContentText(getResources().getString(R.string.app_desc));
          statusBarIconBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
          Intent resultIntent = new Intent(this, LiveLoggerActivity.class);
          PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
          statusBarIconBuilder.setContentIntent(resultPendingIntent);
          Notification notification = statusBarIconBuilder.build();
          notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
          NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
          notifyMgr.notify(1, notification);
          */
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
          return getSettingFromDevice(cmPort, query, null);
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
     public static String getSettingFromDevice(UsbSerialDevice cmPort, String query, String hint) {
          ChameleonIO.DEVICE_RESPONSE = new String[1];
          ChameleonIO.DEVICE_RESPONSE[0] = (hint == null) ? "TIMEOUT" : hint;
          ChameleonIO.LASTCMD = query;
          if(cmPort == null)
               return ChameleonIO.DEVICE_RESPONSE[0];
          try {
               if(!serialPortLock.tryAcquire(ChameleonIO.LOCK_TIMEOUT, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                    return ChameleonIO.DEVICE_RESPONSE[0];
               }
          } catch(Exception inte) {
               inte.printStackTrace();
               serialPortLock.release();
               return ChameleonIO.DEVICE_RESPONSE[0];
          }
          ChameleonIO.WAITING_FOR_RESPONSE = true;
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
          int deviceRespCode = -1;
          try {
               if(ChameleonIO.DEVICE_RESPONSE_CODE.length() >= 3) {
                    deviceRespCode = Integer.valueOf(ChameleonIO.DEVICE_RESPONSE_CODE.substring(0, 3));
               }
               else {
                    deviceRespCode = Integer.valueOf(ChameleonIO.DEVICE_RESPONSE_CODE);
               }
          } catch(NumberFormatException nfe) {
               Log.e(TAG, "NFE encountered: " + nfe.getMessage());
               nfe.printStackTrace();
               if(ChameleonIO.DEVICE_RESPONSE_CODE == null) {
                    ChameleonIO.DEVICE_RESPONSE_CODE = "";
               }
               Crashlytics.log(Log.ERROR, TAG, "Encountered unexpected NFE: " +
                               nfe.getMessage() + "(DEVICE_RESPONSE_CODE = \"" + ChameleonIO.DEVICE_RESPONSE_CODE + "\")");
               Crashlytics.logException(nfe);
               serialPortLock.release();
               return ChameleonIO.DEVICE_RESPONSE_CODE;
          }
          serialPortLock.release();
          if(deviceRespCode != ChameleonIO.SerialRespCode.OK.toInteger() &&
             deviceRespCode != ChameleonIO.SerialRespCode.OK_WITH_TEXT.toInteger()) {
               return ChameleonIO.DEVICE_RESPONSE_CODE;
          }
          String retValue = ChameleonIO.DEVICE_RESPONSE[0] != null ? ChameleonIO.DEVICE_RESPONSE[0] : "";
          if(retValue.equals("201:INVALID COMMAND USAGE")) {
               retValue += " (Are you in READER mode?)";
               Crashlytics.log(Log.WARN, TAG, "201: INVALID COMMAND USAGE for RESPONSE_CODE = " + ChameleonIO.DEVICE_RESPONSE_CODE);
          }
          return retValue;
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
                         ChameleonIO.REVE_BOARD = false;
                         connection = usbManager.openDevice(device);
                         break;
                    }
                    else if(deviceVID == ChameleonIO.CMUSB_REVE_VENDORID && devicePID == ChameleonIO.CMUSB_REVE_PRODUCTID) {
                         ChameleonIO.REVE_BOARD = true;
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
               serialPort.setDataBits(USB_DATA_BITS); // slight optimization? ... yes, better
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
          ChameleonIO.enableLiveDebugging(serialPort, ChameleonIO.TIMEOUT);
          ChameleonIO.PAUSED = false;
          String usbDeviceData = String.format(Locale.ENGLISH, "Manufacturer: %s\nProduct Name: %s\nVersion: %s\nDevice Serial: %s\nUSB Dev: %s",
               device.getManufacturerName(), device.getProductName(), device.getVersion(), device.getSerialNumber(), device.getDeviceName());
          appendNewLog(new LogEntryMetadataRecord(defaultInflater, "USB STATUS: ", "Successfully configured the device in passive logging mode...\n" + usbDeviceData));
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
          ChameleonIO.UPLOAD = false;
          ChameleonIO.WAITING_FOR_XMODEM = false;
          ChameleonIO.WAITING_FOR_RESPONSE = false;
          ChameleonIO.EXPECTING_BINARY_DATA = false;
          ChameleonIO.LASTCMD = "";
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
               Log.d(TAG, "USBReaderCallback Received Data: " + Utils.bytes2Hex(liveLogData));
               Log.d(TAG, "    => " + Utils.bytes2Ascii(liveLogData));
               if(LogUtils.ResponseIsLiveLoggingBytes(liveLogData)) {
                    final LogEntryUI liveLoggingEntry = LogEntryUI.newInstance(liveLogData, "");
                    if(liveLoggingEntry != null) {
                         runOnUiThread(new Runnable() {
                              public void run() {
                                   appendNewLog(liveLoggingEntry);
                              }
                         });
                    }
                    return;
               }
               else if(ChameleonIO.PAUSED) {
                    return;
               }
               else if(ChameleonIO.DOWNLOAD) {
                    ExportTools.performXModemSerialDownload(liveLogData);
                    return;
               }
               else if(ChameleonIO.UPLOAD) {
                    ExportTools.performXModemSerialUpload(liveLogData);
                    return;
               }
               else if(ChameleonIO.WAITING_FOR_XMODEM) {
                    String strLogData = new String(liveLogData);
                    if(strLogData.length() >= 11 && strLogData.substring(0, 11).equals("110:WAITING")) {
                         ChameleonIO.WAITING_FOR_XMODEM = false;
                         return;
                    }
               }
               else if(ChameleonIO.WAITING_FOR_RESPONSE && ChameleonIO.isCommandResponse(liveLogData)) {
                    String[] strLogData = (new String(liveLogData)).split("[\n\r\t]+");
                    ChameleonIO.DEVICE_RESPONSE_CODE = strLogData[0];
                    if(strLogData.length >= 2)
                         ChameleonIO.DEVICE_RESPONSE = Arrays.copyOfRange(strLogData, 1, strLogData.length);
                    else
                         ChameleonIO.DEVICE_RESPONSE[0] = strLogData[0];
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
          ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
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
                    R.id.LEDGreenSpinner,
                    R.id.ButtonMyRevEBoardSpinner
               };
               String[] queryCmds = {
                    "RBUTTON?",
                    "RBUTTON_LONG?",
                    "LBUTTON?",
                    "LBUTTON_LONG?",
                    "LEDRED?",
                    "LEDGREEN?",
                    "buttonmy?"
               };
               for (int i = 0; i < spinnerIDs.length; i++) {
                    Log.i(TAG, queryCmds[i]);
                    Spinner curSpinner = (Spinner) LiveLoggerActivity.runningActivity.findViewById(spinnerIDs[i]);
                    String deviceSetting = getSettingFromDevice(LiveLoggerActivity.serialPort, queryCmds[i]);
                    curSpinner.setSelection(((ArrayAdapter<String>) curSpinner.getAdapter()).getPosition(deviceSetting));
               }
               // handle FIELD and READ-ONLY switches:
               String fieldSetting = getSettingFromDevice(LiveLoggerActivity.serialPort, "FIELD?");
               ((Switch) findViewById(R.id.fieldOnOffSwitch)).setChecked(fieldSetting.equals("0") ? false : true);
               String roQueryCmd = ChameleonIO.REVE_BOARD ? "readonlymy?" : "READONLY?";
               String roSetting = getSettingFromDevice(LiveLoggerActivity.serialPort, roQueryCmd);
               ((Switch) findViewById(R.id.readonlyOnOffSwitch)).setChecked(roSetting.equals("0") ? false : true);
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
          if(serialPort == null) {
               appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Cannot run command since USB is not configured."));
               return;
          }
          String createCmd = ((Button) view).getText().toString();
          String msgParam = "";
          if(createCmd.equals("READER")) {
               ChameleonIO.setReaderConfigMode(serialPort, ChameleonIO.TIMEOUT);
               try {
                    Thread.sleep(100);
               } catch(InterruptedException ie) {}
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
               msgParam = "Set Chameleon mode to READER.";
          }
          else if(createCmd.equals("SNIFFER")) {
               ChameleonIO.setLoggerConfigMode(serialPort, ChameleonIO.TIMEOUT);
               try {
                    Thread.sleep(100);
               } catch(InterruptedException ie) {}
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
               msgParam = "Set Chameleon mode to SNIFFER.";
          }
          else if(createCmd.equals("DETECT")) {
               msgParam = getSettingFromDevice(serialPort, "configmy=MF_DETECTION");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("ULTRALIGHT")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=MF_ULTRALIGHT");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=MF_ULTRALIGHT");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("CLASSIC-1K")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=MF_CLASSIC_1K");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=MF_CLASSIC_1K");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("CLASSIC-4K")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=MF_CLASSIC_4K");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=MF_CLASSIC_4K");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("CLASSIC-1K7B")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=MF_CLASSIC_1K_7B");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=MF_CLASSIC_1K_7B");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("CLASSIC-4K7B")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=MF_CLASSIC_4K_7B");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=MF_CLASSIC_4K_7B");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("MF-DESFIRE-EV1-4K")) {
               ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=MF_DESFIRE_EV1_4K", ChameleonIO.TIMEOUT);
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
               msgParam = "NOTE: You must use the firmware from https://github.com/maxieds/ChameleonMini to have the DESFire chip support enabled.";
          }
          else if(createCmd.equals("MFU-EV1-80B")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=MF_ULTRALIGHT_EV1_80B");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=MF_ULTRALIGHT_EV1_80B");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("MFU-EV1-164B")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=MF_ULTRALIGHT_EV1_80B");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=MF_ULTRALIGHT_EV1_80B");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("CFGNONE")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=NONE");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy=NONE");
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          }
          else if(createCmd.equals("LIST CONFIG")) {
               if(!ChameleonIO.REVE_BOARD)
                    msgParam = getSettingFromDevice(serialPort, "CONFIG=?");
               else
                    msgParam = getSettingFromDevice(serialPort, "configmy");
               msgParam = " => " + msgParam;
               msgParam = msgParam.replaceAll(",", "\n => ");
               createCmd = "CONFIG?";
          }
          else if(createCmd.equals("RESET") || createCmd.equals("resetmy")) { // need to re-establish the usb connection:
               ChameleonIO.executeChameleonMiniCommand(serialPort, createCmd, ChameleonIO.TIMEOUT);
               ChameleonIO.deviceStatus.statsUpdateHandler.removeCallbacks(ChameleonIO.deviceStatus.statsUpdateRunnable);
               closeSerialPort(serialPort);
               configureSerialPort(null, usbReaderCallback);
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
               msgParam = "Reconfigured the Chameleon USB settings.";
          }
          else if(createCmd.equals("RANDOM UID")) {
               ChameleonIO.deviceStatus.LASTUID = ChameleonIO.deviceStatus.UID;
               String uidCmd = ChameleonIO.REVE_BOARD ? "uidmy=" : "UID=";
               byte[] randomBytes = Utils.getRandomBytes(ChameleonIO.deviceStatus.UIDSIZE);
               String sendCmd = uidCmd + Utils.bytes2Hex(randomBytes).replace(" ", "").toUpperCase();
               Log.d(TAG, "RANDOM UID: (sendCmd = " + sendCmd + ")");
               getSettingFromDevice(serialPort, sendCmd);
               msgParam = "Next UID set to " + Utils.bytes2Hex(randomBytes).replace(" ", ":").toUpperCase();
               ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
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
               String queryCmd = ChameleonIO.REVE_BOARD ? "uidmy?" : "GETUID";
               String rParam = getSettingFromDevice(serialPort, queryCmd);
               msgParam = "GETUID: " + rParam;
          }
          else if(createCmd.equals("SEND") || createCmd.equals("SEND_RAW")) {
               String bytesToSend = ((TextView) findViewById(R.id.userInputFormattedBytes)).getText().toString().replaceAll(" ", "");
               if(bytesToSend.length() != 2 || !Utils.stringIsHexadecimal(bytesToSend)) {
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Input to send to card must be a _single hexadecimal_ byte!"));
                    return;
               }
               msgParam = "Card Response (if any): \n" + getSettingFromDevice(serialPort, createCmd + " " + bytesToSend);
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
                    if (serialPort != null && isChecked && actionFlag.equals("SEND")) {
                         String byteString = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                         appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                         ChameleonIO.executeChameleonMiniCommand(serialPort, "SEND " + byteString, ChameleonIO.TIMEOUT);
                    }
                    else if(serialPort != null && isChecked && actionFlag.equals("SEND_RAW")) {
                         String byteString = ((LogEntryUI) logDataEntries.get(vi)).getPayloadData();
                         appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CARD INFO", "Sending: " + byteString + "..."));
                         ChameleonIO.executeChameleonMiniCommand(serialPort, "SEND_RAW " + byteString, ChameleonIO.TIMEOUT);
                    }
                    else if(serialPort != null && isChecked && actionFlag.equals("CLONE_UID")) {
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

     public void actionButtonModifyUID(View view) {
          if(ChameleonIO.deviceStatus.UID == null || ChameleonIO.deviceStatus.UID.equals("DEVICE UID") || ChameleonIO.deviceStatus.UID.equals("NO UID."))
               return;
          ChameleonIO.deviceStatus.LASTUID = ChameleonIO.deviceStatus.UID;
          String uidAction = ((Button) view).getTag().toString();
          byte[] uid = Utils.hexString2Bytes(ChameleonIO.deviceStatus.UID);
          int uidSize = uid.length - 1;
          if(uidAction.equals("INCREMENT_RIGHT"))
               uid[uidSize] += (byte) 0x01;
          else if(uidAction.equals("DECREMENT_RIGHT"))
               uid[uidSize] -= (byte) 0x01;
          else if(uidAction.equals("SHIFT_RIGHT")) {
               byte[] nextUID = new byte[uid.length];
               System.arraycopy(uid, 1, nextUID, 0, uid.length - 1);
               uid = nextUID;
          }
          else if(uidAction.equals("INCREMENT_LEFT"))
               uid[0] += (byte) 0x80;
          else if(uidAction.equals("DECREMENT_LEFT"))
               uid[0] -= (byte) 0x80;
          else if(uidAction.equals("SHIFT_LEFT")){
               byte[] nextUID = new byte[uid.length];
               System.arraycopy(uid, 0, nextUID, 1, uid.length - 1);
               uid = nextUID;
          }
          else if(uidAction.equals("LAST_UID")) {
               uid = Utils.hexString2Bytes(ChameleonIO.deviceStatus.LASTUID);
               ChameleonIO.deviceStatus.LASTUID = ChameleonIO.deviceStatus.UID;
          }
          String uidCmd = ChameleonIO.REVE_BOARD ? "uidmy" : "UID";
          String cmdStatus = getSettingFromDevice(serialPort, String.format(Locale.ENGLISH, "%s=%s", uidCmd, Utils.bytes2Hex(uid).replace(" ", "").toUpperCase()));
          ChameleonIO.deviceStatus.updateAllStatusAndPost(true);
          appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("UID", "Next device UID set to " + Utils.bytes2Hex(uid).replace(" ", ":").toUpperCase()));
     }

     /**
      * Constructs and displays a dialog providing meta information about the application.
      * @param view
      * @ref R.string.aboutapp
      */
     public void actionButtonAboutTheApp(View view) {
          AlertDialog.Builder adBuilder = new AlertDialog.Builder(this, R.style.SpinnerTheme);
          String rawAboutStr = getString(R.string.apphtmlheader) + getString(R.string.aboutapp) + getString(R.string.privacyhtml) + getString(R.string.apphtmlfooter);
          rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_CODE%%", String.valueOf(BuildConfig.VERSION_CODE));
          rawAboutStr = rawAboutStr.replace("%%ANDROID_VERSION_NAME%%", String.valueOf(BuildConfig.VERSION_NAME));
          rawAboutStr = rawAboutStr.replace("%%ANDROID_FLAVOR_NAME%%", String.valueOf(BuildConfig.FLAVOR) + ", " + BuildConfig.BUILD_TIMESTAMP);
          rawAboutStr = rawAboutStr.replace("%%GIT_COMMIT_HASH%%", String.valueOf(BuildConfig.GIT_COMMIT_HASH));
          rawAboutStr = rawAboutStr.replace("%%GIT_COMMIT_DATE%%", String.valueOf(BuildConfig.GIT_COMMIT_DATE));
          rawAboutStr = rawAboutStr.replace("%%ABOUTLINKCOLOR%%", String.format(Locale.ENGLISH, "#%06X", 0xFFFFFF & getTheme().obtainStyledAttributes(new int[] {R.attr.colorAboutLinkColor}).getColor(0, getResources().getColor(R.color.colorBigVeryBadError))));
          //builder1.setMessage(Html.fromHtml(rawAboutStr, Html.FROM_HTML_MODE_LEGACY));

          WebView wv = new WebView(this);
          wv.getSettings().setJavaScriptEnabled(false);
          wv.loadDataWithBaseURL(null, rawAboutStr, "text/html", "UTF-8", "");
          wv.setBackgroundColor(getThemeColorVariant(R.attr.colorAccentHighlight));
          wv.getSettings().setLoadWithOverviewMode(true);
          wv.getSettings().setUseWideViewPort(true);
          //wv.getSettings().setBuiltInZoomControls(true);
          wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
          wv.setInitialScale(10);

          adBuilder.setCancelable(true);
          adBuilder.setTitle("About the Application:");
          adBuilder.setIcon(R.drawable.chameleonicon_about64);
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
          if(!ChameleonIO.REVE_BOARD && (cmCmd.equals("DUMP_MFU") || cmCmd.equals("IDENTIFY") || cmCmd.equals("CLONE"))) {
               int oldTimeout = ChameleonIO.TIMEOUT;
               ChameleonIO.TIMEOUT = 5000; // extend the timeout on these long commands
               String mfuBytes = getSettingFromDevice(serialPort, cmCmd);
               ChameleonIO.TIMEOUT = oldTimeout;
               ChameleonIO.DEVICE_RESPONSE[0] = Arrays.toString(ChameleonIO.DEVICE_RESPONSE);
               ChameleonIO.DEVICE_RESPONSE[0] = ChameleonIO.DEVICE_RESPONSE[0].substring(1, ChameleonIO.DEVICE_RESPONSE[0].length() - 1);
               mfuBytes = mfuBytes.replace(",", "");
               mfuBytes = mfuBytes.replace("\n", "");
               mfuBytes = mfuBytes.replace("\r", "");
               if(cmCmd.equals("DUMP_MFU")) {
                    String mfuBytesPrettyPrint = Utils.prettyPrintMFU(mfuBytes);
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("DUMP_MFU", mfuBytesPrettyPrint));
               }
               else
                    appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(cmCmd, mfuBytes));
          }
          else {
               String rdata = getSettingFromDevice(serialPort, cmCmd);
               appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord(cmCmd, rdata));
          }
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
      * Called when the Export tab button for cloning the DUMP_MFU command output is requested by the user.
      * @param view
      */
     public void actionButtonCloneMFU(View view) {
        /*String mfuBytes = getSettingFromDevice(serialPort, "DUMP_MFU");
        ChameleonIO.DEVICE_RESPONSE[0] = Arrays.toString(ChameleonIO.DEVICE_RESPONSE);
        ChameleonIO.DEVICE_RESPONSE[0] = ChameleonIO.DEVICE_RESPONSE[0].substring(1, ChameleonIO.DEVICE_RESPONSE[0].length() - 1);
        mfuBytes = mfuBytes.replace(",", "");
        mfuBytes = mfuBytes.replace("\n", "");
        mfuBytes = mfuBytes.replace("\r", "");
        String mfuBytesPrettyPrint = Utils.prettyPrintMFU(mfuBytes);
        appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("DUMP_MFU", mfuBytesPrettyPrint));
        ExportTools.cloneBinaryDumpMFU(Utils.hexString2Bytes(mfuBytes));*/
          String dumpMFUOutput = getSettingFromDevice(serialPort, "DUMP_MFU");
          appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("DUMP_MFU", dumpMFUOutput));
          ChameleonIO.executeChameleonMiniCommand(serialPort, "CLONE", ChameleonIO.TIMEOUT);
          String cloneCmdOutput = ChameleonIO.DEVICE_RESPONSE_CODE;
          cloneCmdOutput += Arrays.asList(ChameleonIO.DEVICE_RESPONSE).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", "\n");
          appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("CLONE", cloneCmdOutput));
     }

     /**
      * Called to load the stock card images from stored in the res/raw/* directory.
      * @param view
      */
     public void actionButtonCloneStockDumpImages(View view) {

          String stockChipType = ((Button) view).getTag().toString();
          String chipType;
          int cardFilePath;
          if(stockChipType.equals("MFC1K_RCFK")) {
               chipType = "MF_CLASSIC_1K";
               cardFilePath = R.raw.mfc1k_random_content_fixed_keys;
          }
          else if(stockChipType.equals("MFC4K_RCFK")) {
               chipType = "MF_CLASSIC_4K";
               cardFilePath = R.raw.mfc4k_random_content_fixed_keys;
          }
          else if(stockChipType.equals("MFC1K")) {
               chipType = "MF_CLASSIC_1K";
               cardFilePath = R.raw.mifare_classic_1k;
          }
          else {
               chipType = "MF_ULTRALIGHT";
               cardFilePath = R.raw.mifare_ultralight;
          }
          ChameleonIO.executeChameleonMiniCommand(serialPort, "CONFIG=" + chipType, ChameleonIO.TIMEOUT);
          ExportTools.uploadCardFromRawByXModem(cardFilePath);
          ChameleonIO.deviceStatus.updateAllStatusAndPost(true);

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
          else if(action.equals("DOWNLOAD")) {
               String dldCmd = ChameleonIO.REVE_BOARD ? "downloadmy" : "DOWNLOAD";
               ExportTools.downloadByXModem(dldCmd, "carddata-" + ChameleonIO.deviceStatus.CONFIG, false);
          }
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
          if(serialPort == null)
               return;
          // should potentially fix a slight "bug" where the card uploads but fails to get transferred to the
          // running device profile due to differences in the current configuration's memsize setting.
          // This might be more of a bug with the Chameleon software, but not entirely sure.
          // Solution: Clear out the current setting slot to CONFIG=NONE before performing the upload:
          //getSettingFromDevice(serialPort, "CONFIG=NONE");

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

     public void actionButtonPerformSearch(View view) {

          // hide the search keyboard obstructing the results after the button press:
          View focusView = this.getCurrentFocus();
          if (focusView != null) {
               InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
          }
          long startTime = System.currentTimeMillis();

          // clear out the existing search data first:
          ScrollView searchResultsScroller = (ScrollView) findViewById(R.id.searchResultsScrollView);
          if(searchResultsScroller.getChildCount() != 0) {
               searchResultsScroller.removeViewAt(0);
          }
          LinearLayout searchResultsContainer = new LinearLayout(getApplicationContext());
          searchResultsContainer.setOrientation(LinearLayout.VERTICAL);
          searchResultsScroller.addView(searchResultsContainer);

          boolean selectedBytes = ((RadioButton) findViewById(R.id.radio_search_bytes)).isChecked();
          String searchString = ((TextView) findViewById(R.id.userInputSearchData)).getText().toString();
          if(searchString.equals(""))
               return;
          else if(selectedBytes && !Utils.stringIsHexadecimal(searchString)) {
               searchResultsContainer.addView(LogEntryMetadataRecord.createDefaultEventRecord("ERROR", "Not a hexadecimal string.").getLayoutContainer());
               return;
          }
          else if(selectedBytes) {
               searchString = searchString.replace("[\n\t\r]+", "").replaceAll("..(?!$)", "$0 ");
          }
          searchString = searchString.toLowerCase(Locale.ENGLISH);

          boolean searchStatus = ((CheckBox) findViewById(R.id.entrySearchIncludeStatus)).isChecked();
          boolean searchAPDU = ((CheckBox) findViewById(R.id.entrySearchAPDU)).isChecked();
          boolean searchLogPayload = ((CheckBox) findViewById(R.id.entrySearchRawLogData)).isChecked();
          boolean searchLogHeaders = ((CheckBox) findViewById(R.id.entrySearchLogHeaders)).isChecked();
          int matchCount = 0;
          Log.i(TAG, "Searching for: " + searchString);
          for(int vi = 0; vi < logDataEntries.size(); vi++) {
               if (logDataEntries.get(vi) instanceof LogEntryMetadataRecord) {
                    if (searchStatus && logDataEntries.get(vi).toString().toLowerCase(Locale.ENGLISH).contains(searchString)) {
                         searchResultsContainer.addView(logDataEntries.get(vi).cloneLayoutContainer());
                         matchCount++;
                    }
                    continue;
               }
               Log.i(TAG, ((LogEntryUI) logDataEntries.get(vi)).getPayloadDataString(selectedBytes));
               if (searchAPDU && ((LogEntryUI) logDataEntries.get(vi)).getAPDUString().toLowerCase(Locale.ENGLISH).contains(searchString) ||
                    searchLogHeaders && ((LogEntryUI) logDataEntries.get(vi)).getLogCodeName().toLowerCase(Locale.ENGLISH).contains(searchString) ||
                    searchLogPayload && ((LogEntryUI) logDataEntries.get(vi)).getPayloadDataString(selectedBytes).toLowerCase(Locale.ENGLISH).contains(searchString)) {
                    LinearLayout searchResult = (LinearLayout) logDataEntries.get(vi).cloneLayoutContainer();
                    searchResult.setVisibility(LinearLayout.VISIBLE);
                    searchResult.setEnabled(true);
                    searchResult.setMinimumWidth(350);
                    searchResult.setMinimumHeight(150);
                    LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    searchResultsContainer.addView(searchResult, lllp);
                    Log.i(TAG, "Case II: Record " + vi + " matches");
                    matchCount++;
               }
          }
          double diffSeconds = (double) (System.currentTimeMillis() - startTime) / 1000.0;
          String resultStr = String.format(Locale.ENGLISH, "Explored #%d logs in %4g seconds for a total of #%d matching records.",
               logDataEntries.size(), diffSeconds, matchCount);
          searchResultsContainer.addView(LogEntryMetadataRecord.createDefaultEventRecord("SEARCH", resultStr).getLayoutContainer());
     }

     public void actionButtonApduCLA(View view) {
          String CLA = ((Button) view).getTag().toString();
          ApduUtils.apduTransceiveCmd.CLA = CLA;
          ApduUtils.updateAssembledAPDUCmd();
     }

     public void actionButtonApduClear(View view) {
          ApduUtils.apduTransceiveCmd.clear();
          //((TextView) ((ScrollView) ApduUtils.tabView.findViewById(R.id.apduSearchResultsScrollView)).getChildAt(0)).setText("");
          ((LinearLayout) ((ScrollView) ApduUtils.tabView.findViewById(R.id.apduSearchResultsScrollView)).getChildAt(0)).removeAllViewsInLayout();
          ApduUtils.updateAssembledAPDUCmd();
     }

     public void actionButtonApduManualDataEntry(View view) {

          AlertDialog.Builder adbuilder = new AlertDialog.Builder(this);
          adbuilder.setTitle("Set APDU Command Components: ");
          String instrMsg = "Enter the APDU command components manually into the following box. ";
          instrMsg += "The bytes are in hexadecimal in the following order: CLA | INS | P1 | P2 | [LE] | DATA | [LC]. ";
          instrMsg += "Note that the LE/LC fields should not be included and will be calculated based on your input later.";
          adbuilder.setMessage(instrMsg);

          EditText apduCmdEntry = new EditText(this);
          apduCmdEntry.setHint(ApduUtils.apduTransceiveCmd.assembleAPDUString());
          final EditText apduCmdEntryFinal = apduCmdEntry;
          adbuilder.setView(apduCmdEntryFinal);

          adbuilder.setNegativeButton("Cancel", null);
          adbuilder.setNeutralButton("Parse As Data Only", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    String dataBytes = apduCmdEntryFinal.getText().toString().toLowerCase();
                    dataBytes.replaceAll("[ \n\t\r]*", ""); // remove whitespace
                    if (!Utils.stringIsHexadecimal(dataBytes)) {
                         return;
                    }
                    ApduUtils.apduTransceiveCmd.setPayloadData(dataBytes);
                    ApduUtils.apduTransceiveCmd.computeLELCBytes();
                    ApduUtils.updateAssembledAPDUCmd();
                    dialog.dismiss();
               }
          });
          adbuilder.setPositiveButton("Parse Input", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    String dataBytes = apduCmdEntryFinal.getText().toString().toLowerCase();
                    dataBytes.replaceAll("[ \n\t\r]*", ""); // remove whitespace
                    if(!Utils.stringIsHexadecimal(dataBytes) || dataBytes.length() < 8) {
                         return;
                    }
                    ApduUtils.apduTransceiveCmd.CLA = dataBytes.substring(0, 2);
                    ApduUtils.apduTransceiveCmd.INS = dataBytes.substring(2, 4);
                    ApduUtils.apduTransceiveCmd.P1 = dataBytes.substring(4, 6);
                    ApduUtils.apduTransceiveCmd.P2 = dataBytes.substring(6, 8);
                    if(dataBytes.length() >= 9) {
                         ApduUtils.apduTransceiveCmd.setPayloadData(dataBytes.substring(8, dataBytes.length()));
                    }
                    ApduUtils.apduTransceiveCmd.computeLELCBytes();
                    ApduUtils.updateAssembledAPDUCmd();
                    dialog.dismiss();
               }
          });
          adbuilder.show();

     }

     public void actionButtonGetBits(View view) {
          String action = ((Button) view).getTag().toString();
          String dataBytesStr = new String();
          if(action.equals("UID") && serialPort != null) {
               dataBytesStr = ChameleonIO.deviceStatus.UID;
          }
          else if(action.equals("RANDOM")) {
               if(ChameleonIO.deviceStatus.UIDSIZE == 0)
                    dataBytesStr = Utils.bytes2Hex(Utils.getRandomBytes(7));
               else
                    dataBytesStr = Utils.bytes2Hex(Utils.getRandomBytes(ChameleonIO.deviceStatus.UIDSIZE));
          }
          ApduUtils.apduTransceiveCmd.setPayloadData(dataBytesStr);
          ApduUtils.updateAssembledAPDUCmd();
     }

     public void actionButtonSendAPDU(View view) {
          String sendBytesStr = ApduUtils.apduTransceiveCmd.getPayloadData();
          String respData = getSettingFromDevice(serialPort, "SEND " + sendBytesStr);
          appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("APDU", "SEND Response:\n" + respData));
          respData = getSettingFromDevice(serialPort, "SEND_RAW " + sendBytesStr);
          appendNewLog(LogEntryMetadataRecord.createDefaultEventRecord("APDU", "SEND_RAW Response:\n" + respData));
     }

     public void actionButtonAPDUSearchCmd(View view) {
          String searchText = ((TextView) ApduUtils.tabView.findViewById(R.id.apduSearchText)).getText().toString().toLowerCase();
          LinearLayout layoutList = (LinearLayout) ((ScrollView) ApduUtils.tabView.findViewById(R.id.apduSearchResultsScrollView)).getChildAt(0);
          for(int cmd = 0; cmd < ApduUtils.fullInsList.length; cmd++) {
               String summaryStr = ApduUtils.fullInsList[cmd].getSummary();
               if(summaryStr.toLowerCase(Locale.ENGLISH).contains(searchText)) {
                    LinearLayout searchResult = (LinearLayout) LiveLoggerActivity.defaultInflater.inflate(R.layout.apdu_search_result, null);
                    String[] cmdDescParts = ApduUtils.fullInsList[cmd].apduCmdDesc.split("[\\(\\)]");
                    ((TextView) searchResult.findViewById(R.id.apduCmdDesc)).setText(cmdDescParts[0]);
                    ((TextView) searchResult.findViewById(R.id.apduByteData)).setText(summaryStr.toLowerCase().split(" : ")[1]);
                    ((Button) searchResult.findViewById(R.id.copyCmdButton)).setTag(Integer.toString(cmd));
                    layoutList.addView(searchResult);
               }
          }
          ((TextView) ApduUtils.tabView.findViewById(R.id.apduSearchText)).setHint("Search by Text or Byte Strings ...");
     }

     public void actionButtonAPDUCopyCmd(View view) {
          String tagIndex = ((Button) view).getTag().toString();
          int apduCmdIndex = Integer.valueOf(tagIndex);
          ApduUtils.apduTransceiveCmd = ApduUtils.fullInsList[apduCmdIndex];
          ApduUtils.updateAssembledAPDUCmd();
     }

     public static void setSignalStrengthIndicator(int threshold) {
          double signalStrength = threshold / 4500.0;
          if (signalStrength >= 0.80)
               LiveLoggerActivity.runningActivity.setStatusIcon(R.id.signalStrength, R.drawable.signalbars5);
          else if (signalStrength >= 0.60)
               LiveLoggerActivity.runningActivity.setStatusIcon(R.id.signalStrength, R.drawable.signalbars4);
          else if (signalStrength >= 0.40)
               LiveLoggerActivity.runningActivity.setStatusIcon(R.id.signalStrength, R.drawable.signalbars3);
          else if (signalStrength >= 0.20)
               LiveLoggerActivity.runningActivity.setStatusIcon(R.id.signalStrength, R.drawable.signalbars2);
          else
               LiveLoggerActivity.runningActivity.setStatusIcon(R.id.signalStrength, R.drawable.signalbars1);
     }

     public void actionButtonDisplayHelp(View view) {

          AlertDialog.Builder adBuilder = new AlertDialog.Builder(this, R.style.SpinnerTheme);
          View adView = defaultInflater.inflate(R.layout.help_dialog, null);
          final View adViewFinal = adView;

          Button cmdRespButton = (Button) adView.findViewById(R.id.cmdRespButton);
          cmdRespButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    WebView wv = (WebView) adViewFinal.findViewById(R.id.wvDisplayHelpTopic);
                    String rawHTMLString = getString(R.string.apphtmlheader) + getString(R.string.helpCmdResp) + getString(R.string.apphtmlfooter);
                    wv.loadData(rawHTMLString, "text/html", "UTF-8");
               }
          });
          Button revECmdsButton = (Button) adView.findViewById(R.id.revECmdsButton);
          revECmdsButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    WebView wv = (WebView) adViewFinal.findViewById(R.id.wvDisplayHelpTopic);
                    String rawHTMLString = getString(R.string.apphtmlheader) + getString(R.string.helpRevECmds) + getString(R.string.apphtmlfooter);
                    wv.loadDataWithBaseURL(null, rawHTMLString, "text/html", "UTF-8", "");
               }
          });
          Button revGCmdsButton = (Button) adView.findViewById(R.id.revGCmdsButton);
          revGCmdsButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    WebView wv = (WebView) adViewFinal.findViewById(R.id.wvDisplayHelpTopic);
                    String rawHTMLString = getString(R.string.apphtmlheader) + getString(R.string.helpRevGCmds) + getString(R.string.apphtmlfooter);
                    wv.loadData(rawHTMLString, "text/html", "UTF-8");
               }
          });
          Button buttonSettingsButton = (Button) adView.findViewById(R.id.buttonSettingsButton);
          buttonSettingsButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    WebView wv = (WebView) adViewFinal.findViewById(R.id.wvDisplayHelpTopic);
                    String rawHTMLString = getString(R.string.apphtmlheader) + getString(R.string.helpButtonSettings) + getString(R.string.apphtmlfooter);
                    wv.loadData(rawHTMLString, "text/html", "UTF-8");
               }
          });
          Button buttonLEDButton = (Button) adView.findViewById(R.id.ledSettingsButton);
          buttonLEDButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    WebView wv = (WebView) adViewFinal.findViewById(R.id.wvDisplayHelpTopic);
                    String rawHTMLString = getString(R.string.apphtmlheader) + getString(R.string.helpLEDSettings) + getString(R.string.apphtmlfooter);
                    wv.loadData(rawHTMLString, "text/html", "UTF-8");
               }
          });
          Button loggingButton = (Button) adView.findViewById(R.id.loggingButton);
          loggingButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    WebView wv = (WebView) adViewFinal.findViewById(R.id.wvDisplayHelpTopic);
                    String rawHTMLString = getString(R.string.apphtmlheader) + getString(R.string.helpLogging) + getString(R.string.apphtmlfooter);
                    wv.loadData(rawHTMLString, "text/html", "UTF-8");
               }
          });

          WebView wv = (WebView) adView.findViewById(R.id.wvDisplayHelpTopic);
          wv.getSettings().setJavaScriptEnabled(false);
          wv.setBackgroundColor(getThemeColorVariant(R.attr.colorAccentHighlight));
          wv.getSettings().setLoadWithOverviewMode(true);
          wv.getSettings().setUseWideViewPort(true);
          wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
          wv.setInitialScale(10);

          adBuilder.setCancelable(true);
          adBuilder.setTitle("Application Help Topics:");
          adBuilder.setMessage("This window displays help information for the following topics. Click on each button to display the respective help topic.");
          adBuilder.setIcon(R.drawable.help64);
          adBuilder.setPositiveButton(
               "Done",
               new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                         dialog.cancel();
                    }
               });
          adBuilder.setView(adView);
          AlertDialog alertDialog = adBuilder.create();

          alertDialog.show();
     }

}