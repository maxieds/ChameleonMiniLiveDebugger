package com.maxieds.chameleonminilivedebugger;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.Locale;

/**
 * <h1>Tab Fragment</h1>
 * Implements a Fragment for individual tab data in the application.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public class TabFragment extends Fragment {

    private static final String TAG = TabFragment.class.getSimpleName();

    private static int TAB_MENU_ITEM_COLUMNS = 3;
    private static final int TAB_COUNT = 4;

    /**
     * Definitions of the in-order tab indices.
     */
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int TAB_LOG = 0;
    public static final int TAB_TOOLS = 1;
    public static final int TAB_EXPORT = 2;
    public static final int TAB_CONFIG = 3;

    public static final int TAB_LOG_MITEM_LOGS = 0;
    public static final int TAB_LOG_MITEM_LOGTOOLS = 1;
    public static final int TAB_LOG_MITEM_SEARCH = 2;

    public static final int TAB_TOOLS_MITEM_SLOTS = 0;
    public static final int TAB_TOOLS_MITEM_TAGCONFIG = 1;
    public static final int TAB_TOOLS_MITEM_SCRIPTING = 2;
    public static final int TAB_TOOLS_MITEM_CMDS = 3;
    public static final int TAB_TOOLS_MITEM_PERIPHERALS = 4;
    public static final int TAB_TOOLS_MITEM_APDU = 5;

    public static final int TAB_EXPORT_MITEM_EXPORTLOGS = 0;
    public static final int TAB_EXPORT_MITEM_DOWNLOAD = 1;
    public static final int TAB_EXPORT_MITEM_UPLOAD = 2;

    public static final int TAB_CONFIG_MITEM_CONNECT = 0;
    public static final int TAB_CONFIG_MITEM_SETTINGS = 1;
    public static final int TAB_CONFIG_MITEM_DEVINFO = 2;

    public static class UITab {

        public int tabIndex;
        public int tabIcon;
        public String tabText;
        public int lastMenuIndex;
        public View tabInflatedView;
        public boolean tabViewInit;
        public boolean firstTabLoad;

        public String[] menuItemText;
        public int[] menuItemLayout;

        public UITab(int tidx, int ticon, String text,
                     String[] mitemText, int[] mitemLayout) {
            tabIndex = tidx;
            tabIcon = ticon;
            tabText = text;
            initializeLayout();
            menuItemText = new String[mitemText.length];
            menuItemLayout = new int[mitemLayout.length];
            System.arraycopy(mitemText, 0, menuItemText, 0, mitemText.length);
            System.arraycopy(mitemLayout, 0, menuItemLayout, 0, mitemLayout.length);
        }

        public void initializeLayout() {
            lastMenuIndex = 0;
            tabInflatedView = null;
            tabViewInit = false;
            firstTabLoad = true;
        }

        public boolean selectMenuItem(int midx) {
            if((midx == lastMenuIndex && !firstTabLoad) || midx < 0 || midx >= menuItemText.length) {
                return false;
            }
            else if(tabInflatedView == null) {
                return false;
            }
            deselectMenuItem(lastMenuIndex);
            lastMenuIndex = midx;
            GridLayout menuItemsNav = (GridLayout) tabInflatedView.findViewById(R.id.tabMenuItemsNav);
            String indexRefTag = String.format(Locale.ENGLISH, "%d:%d", tabIndex, midx);
            Button menuItem = (Button) menuItemsNav.findViewWithTag(indexRefTag);
            menuItem.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
            return true;
        }

        private boolean deselectMenuItem(int midx) {
            if(midx < 0 || midx >= menuItemText.length) {
                return false;
            }
            else if(tabInflatedView == null) {
                return false;
            }
            GridLayout menuItemsNav = (GridLayout) tabInflatedView.findViewById(R.id.tabMenuItemsNav);
            String indexRefTag = String.format(Locale.ENGLISH, "%d:%d", tabIndex, midx);
            Button menuItem = (Button) menuItemsNav.findViewWithTag(indexRefTag);
            menuItem.setBackgroundColor(tabInflatedView.getContext().getResources().getColor(android.R.color.transparent));
            menuItem.setTextColor(Utils.getColorFromTheme(R.attr.colorPrimaryDark));
            menuItem.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            return true;
        }

        //@SuppressLint("ResourceType")
        public View createTabView() {
            if(tabViewInit || tabInflatedView == null) {
                return null;
            }
            GridLayout menuItemsNav = (GridLayout) tabInflatedView.findViewById(R.id.tabMenuItemsNav);
            GradientDrawable gradientBg = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {
                            Utils.getColorFromTheme(R.attr.colorAccent),
                            Utils.getColorFromTheme(R.attr.colorAccentHighlight)
            });
            menuItemsNav.setBackgroundColor(Utils.getColorFromTheme(R.attr.colorPrimaryDark));
            menuItemsNav.setBackgroundDrawable(gradientBg);
            gradientBg.setCornerRadius(56f);
            int totalItems = 0;
            while(totalItems < menuItemText.length) {
                int themeResID = ThemesConfiguration.appThemeResID;
                Button menuItemClick = new Button(new ContextThemeWrapper(tabInflatedView.getContext(), themeResID), null, themeResID);
                menuItemClick.setText(menuItemText[totalItems]);
                menuItemClick.setBackgroundColor(Utils.getColorFromTheme(android.R.color.transparent));
                menuItemClick.setTextColor(Utils.getColorFromTheme(R.attr.colorPrimaryDark));
                menuItemClick.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                menuItemClick.offsetTopAndBottom(0);
                menuItemClick.setPadding(20, 8, 20, 8);
                menuItemClick.setMaxHeight(100);
                menuItemClick.setMinWidth(menuItemsNav.getMeasuredWidth() / 3);
                String indexRefTag = String.format(Locale.ENGLISH, "%d:%d", tabIndex, totalItems);
                menuItemClick.setTag(indexRefTag);
                menuItemClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UITab.onMenuItemClick(view);
                    }
                });
                menuItemsNav.addView(menuItemClick);
                totalItems++;
            }
            tabViewInit = true;
            return tabInflatedView;
        }

        public static void onMenuItemClick(View btn) {
            Button btnClicked = (Button) btn;
            String[] tagIndices = btnClicked.getTag().toString().split(":");
            if(tagIndices.length != 2) {
                return;
            }
            int tabIdx = Integer.parseInt(tagIndices[0]);
            int menuItemIdx = Integer.parseInt(tagIndices[1]);
            TabFragment.UITAB_DATA[tabIdx].changeMenuItemDisplay(menuItemIdx);
        }

        public boolean changeMenuItemDisplay(int mitemIdx) {
            if((mitemIdx == lastMenuIndex && !firstTabLoad) ||
                    mitemIdx < 0 || mitemIdx >= menuItemText.length) {
                return false;
            }
            else if(tabInflatedView == null) {
                return false;
            }
            LinearLayout containerLayout = (LinearLayout) tabInflatedView.findViewById(R.id.tabMainContent);
            containerLayout.removeAllViews();
            View tabMainLayoutView = TabFragment.defaultInflater.inflate(menuItemLayout[mitemIdx], containerLayout, false);
            containerLayout.addView(tabMainLayoutView);
            UITabUtils.initializeTabMainContent(tabIndex, mitemIdx, tabMainLayoutView);
            selectMenuItem(mitemIdx);
            firstTabLoad = false;
            return true;
        }

    }

    public static UITab[] UITAB_DATA = new UITab[TAB_COUNT];
    static {
        UITAB_DATA[TAB_LOG] = new UITab(
                TAB_LOG,
                R.drawable.nfc24v1,
                "Logging",
                new String[] {
                        "Live Logs",
                        "Log Tools",
                        "Search Logs"
                },
                new int[]{
                        R.layout.log_tab_logs,
                        R.layout.log_tab_logtools,
                        R.layout.log_tab_search
                });
        UITAB_DATA[TAB_TOOLS] = new UITab(
                TAB_TOOLS,
                R.drawable.tools24,
                "Tools",
                new String[] {
                        "Config Slots",
                        "Tag Config",
                        "Scripting",
                        "Commands",
                        "Peripherals",
                        "APDU"
                },
                new int[]{
                        R.layout.tools_tab_slots,
                        R.layout.tools_tab_tag_config,
                        R.layout.tools_tab_scripting,
                        R.layout.tools_tab_commands,
                        R.layout.tools_tab_peripherals,
                        R.layout.tools_tab_apdu
                });
        UITAB_DATA[TAB_EXPORT] = new UITab(
                TAB_EXPORT,
                R.drawable.insertbinary24,
                "Export",
                new String[] {
                        "Export Logs",
                        "Download",
                        "Upload",
                        "Clone MFC Tags"
                },
                new int[]{
                        R.layout.export_tab_save_logs,
                        R.layout.export_tab_download,
                        R.layout.export_tab_upload,
                        R.layout.export_tab_android_interfaces,
                        R.layout.export_tab_android_interfaces
                });
        UITAB_DATA[TAB_CONFIG] = new UITab(
                TAB_CONFIG,
                R.drawable.configtab24,
                "Config",
                new String[] {
                        "Connect to Devices",
                        "General Settings"
                },
                new int[] {
                        R.layout.config_tab_connect,
                        R.layout.config_tab_settings,
                        R.layout.config_tab_device_settings
                });
    }

    /**
     * Local tab-specific data stored by the class.
     */
    private static int tabLayoutResRef = R.layout.tab_menu_item_template;
    public static LayoutInflater defaultInflater = null;
    private int tabNumber;

    /**
     * Effectively the default constructor used to obtain a new tab of the specified index.
     * @param page
     * @return
     */
    public static TabFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabFragment fragment = new TabFragment();
        fragment.tabNumber = page;
        fragment.setArguments(args);
        return fragment;
    }

    public String getTabTitle() {
        return UITAB_DATA[tabNumber].tabText;
    }

    public int getTabIcon() {
        return UITAB_DATA[tabNumber].tabIcon;
    }

    /**
     * Called when the tab is created.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabNumber = getArguments().getInt(ARG_PAGE);
    }

    /**
     * Inflates the layout and sets up the configuration of the widgets associated with each tab index.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View inflated tab
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LiveLoggerActivity.defaultInflater = inflater;
        TabFragment.defaultInflater = inflater;
        View view = inflater.inflate(tabLayoutResRef, container, false);
        UITAB_DATA[tabNumber].initializeLayout();
        if(UITAB_DATA[tabNumber].tabInflatedView != null) {
            ((LinearLayout) UITAB_DATA[tabNumber].tabInflatedView).removeAllViews();
        }
        UITAB_DATA[tabNumber].tabInflatedView = view;
        UITAB_DATA[tabNumber].createTabView();
        UITAB_DATA[tabNumber].changeMenuItemDisplay(UITAB_DATA[tabNumber].lastMenuIndex);
        return view;
    }

    /**
     * Called when the tab view is destroyed.
     * (Nothing but the default behavior implemented here.)
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}