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

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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

    private static int TAB_MENU_ITEM_DEFAULT_COLUMNS = 3;
    private static final int TAB_COUNT = 5;

    /**
     * Definitions of the in-order tab indices.
     */
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int TAB_LOG = 0;
    public static final int TAB_TOOLS = 1;
    public static final int TAB_EXPORT = 2;
    public static final int TAB_SCRIPTING = 3;
    public static final int TAB_CONFIG = 4;

    public static final int TAB_LOG_MITEM_COLUMNS = 4;
    public static final int TAB_LOG_MITEM_LOGS = 0;
    public static final int TAB_LOG_MITEM_LOGTOOLS = 1;
    public static final int TAB_LOG_MITEM_EXPORTLOGS = 2;
    public static final int TAB_LOG_MITEM_SEARCH = 3;

    public static final int TAB_TOOLS_MITEM_COLUMNS = TAB_MENU_ITEM_DEFAULT_COLUMNS;
    public static final int TAB_TOOLS_MITEM_SLOTS = 0;
    public static final int TAB_TOOLS_MITEM_TAGCONFIG = 1;
    public static final int TAB_TOOLS_MITEM_CMDS = 3;
    public static final int TAB_TOOLS_MITEM_PERIPHERALS = 4;
    public static final int TAB_TOOLS_MITEM_APDU = 2;

    public static final int TAB_EXPORT_MITEM_COLUMNS = TAB_MENU_ITEM_DEFAULT_COLUMNS;
    public static final int TAB_EXPORT_MITEM_UPLOAD_DOWNLOAD = 0;
    public static final int TAB_EXPORT_MITEM_CLONE = 1;

    public static final int TAB_SCRIPTING_MITEM_COLUMNS = 2;
    public static final int TAB_SCRIPTING_MITEM_LOAD_IMPORT = 0;
    public static final int TAB_SCRIPTING_MITEM_CONSOLE_VIEW = 1;
    public static final int TAB_SCRIPTING_MITEM_REGISTER_VIEW = 2;

    public static final int TAB_CONFIG_MITEM_COLUMNS = 4;
    public static final int TAB_CONFIG_MITEM_SETTINGS = 0;
    public static final int TAB_CONFIG_MITEM_CONNECT = 1;
    public static final int TAB_CONFIG_MITEM_LOGGING = 2;
    public static final int TAB_CONFIG_MITEM_SCRIPTING = 3;

    public static class UITab {

        public int tabIndex;
        public int tabIcon;
        public String tabText;
        public int tabNumColumns;
        public int lastMenuIndex;
        public View tabInflatedView;
        public View[] tabMenuItemLayouts;
        public boolean tabViewInit;
        public boolean firstTabLoad;

        public String[] menuItemText;
        public int[] menuItemLayout;

        public UITab(int tidx, int ticon, String text,
                     int tabNumCols,
                     String[] mitemText, int[] mitemLayout) {
            tabIndex = tidx;
            tabIcon = ticon;
            tabText = text;
            tabNumColumns = tabNumCols;
            initializeLayout();
            menuItemText = new String[mitemText.length];
            menuItemLayout = new int[mitemLayout.length];
            tabMenuItemLayouts = new View[mitemLayout.length];
            for(int vidx = 0; vidx < tabMenuItemLayouts.length; vidx++) {
                tabMenuItemLayouts[vidx] = null;
            }
            System.arraycopy(mitemText, 0, menuItemText, 0, mitemText.length);
            System.arraycopy(mitemLayout, 0, menuItemLayout, 0, mitemLayout.length);
        }

        public void initializeLayout() {
            lastMenuIndex = 0;
            tabInflatedView = null;
            tabViewInit = false;
            firstTabLoad = true;
        }

        public boolean selectMenuItem(int midx, boolean performBtnClick) {
            if(midx < 0 || midx >= menuItemText.length) {
                return false;
            }
            else if(tabInflatedView == null) {
                return false;
            }
            deselectMenuItem(lastMenuIndex);
            lastMenuIndex = midx;
            GridLayout menuItemsNav = (GridLayout) tabInflatedView.findViewById(R.id.tabMenuItemsNav);
            String indexRefTag = String.format(Locale.getDefault(), "%d:%d", tabIndex, midx);
            Button menuItem = (Button) menuItemsNav.findViewWithTag(indexRefTag);
            menuItem.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
            final Button menuItemFinal = menuItem;
            if(performBtnClick) {
                Handler performBtnClickHandler = new Handler();
                Runnable performBtnClickRunnable = new Runnable() {
                    @Override
                    public void run() {
                        menuItemFinal.performClick();
                        if (tabIndex == TAB_LOG && midx == TAB_LOG_MITEM_LOGS) {
                            MainActivityLogUtils.moveLiveLogTabScrollerToBottom();
                        }
                    }
                };
                performBtnClickHandler.postDelayed(performBtnClickRunnable, 100);
            }
            return true;
        }

        public boolean selectMenuItem(int midx) {
            return selectMenuItem(midx, true);
        }

        private boolean deselectMenuItem(int midx) {
            if(midx < 0 || midx >= menuItemText.length) {
                return false;
            }
            else if(tabInflatedView == null) {
                return false;
            }
            GridLayout menuItemsNav = (GridLayout) tabInflatedView.findViewById(R.id.tabMenuItemsNav);
            String indexRefTag = String.format(Locale.getDefault(), "%d:%d", tabIndex, midx);
            Button menuItem = (Button) menuItemsNav.findViewWithTag(indexRefTag);
            menuItem.setBackgroundColor(tabInflatedView.getContext().getResources().getColor(android.R.color.transparent));
            menuItem.setTextColor(Utils.getColorFromTheme(R.attr.colorPrimaryDark));
            menuItem.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            return true;
        }

        public View createTabView() {
            if(tabViewInit || tabInflatedView == null) {
                return null;
            }
            GridLayout menuItemsNav = (GridLayout) tabInflatedView.findViewById(R.id.tabMenuItemsNav);
            if(menuItemsNav == null) {
                return null;
            }
            menuItemsNav.setColumnCount(tabNumColumns);
            GradientDrawable gradientBg = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {
                            Utils.getColorFromTheme(R.attr.colorAccent),
                            Utils.getColorFromTheme(R.attr.colorAccentHighlight)
            });
            gradientBg.setCornerRadius(56f);
            menuItemsNav.setBackground(gradientBg);
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
                menuItemClick.setMinWidth(menuItemsNav.getMeasuredWidth() / tabNumColumns);
                String indexRefTag = String.format(Locale.getDefault(), "%d:%d", tabIndex, totalItems);
                menuItemClick.setTag(indexRefTag);
                menuItemClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UITab.onMenuItemClick(view);
                    }
                });
                menuItemClick.setFocusableInTouchMode(false);
                menuItemClick.setFocusable(false);
                menuItemClick.setClickable(true);
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
            TabFragment.UITAB_DATA[tabIdx].changeMenuItemDisplay(menuItemIdx, true);
        }

        public boolean changeMenuItemDisplay(int mitemIdx, boolean updateDeviceGUI) {
            if(tabInflatedView == null) {
                return false;
            }
            LinearLayout containerLayout = (LinearLayout) tabInflatedView.findViewById(R.id.tabMainContent);
            containerLayout.removeAllViews();
            containerLayout.clearFocus();
            View tabMainLayoutView = tabMenuItemLayouts[mitemIdx];
            if((tabMainLayoutView == null) && (mitemIdx >= 0) && (mitemIdx < menuItemText.length)) {
                tabMainLayoutView = TabFragment.defaultInflater.inflate(menuItemLayout[mitemIdx], containerLayout, false);
                tabMenuItemLayouts[mitemIdx] = tabMainLayoutView;
                selectMenuItem(mitemIdx);
                firstTabLoad = false;
            }
            else if(updateDeviceGUI) {
                UITabUtils.initializeTabMainContent(tabIndex, mitemIdx, tabMainLayoutView);
            }
            if(tabMainLayoutView.getParent() != null) {
                ((LinearLayout) tabMainLayoutView.getParent()).removeView(tabMainLayoutView);
            }
            containerLayout.addView(tabMainLayoutView);
            selectMenuItem(mitemIdx, false);
            return true;
        }

        public boolean changeMenuItemDisplay(int mitemIdx) {
            return changeMenuItemDisplay(mitemIdx, false);
        }

    }

    public static UITab[] UITAB_DATA = new UITab[TAB_COUNT];
    static {
        UITAB_DATA[TAB_LOG] = new UITab(
                TAB_LOG,
                R.drawable.tab_logging_icon48,
                "Logs",
                TAB_LOG_MITEM_COLUMNS,
                new String[] {
                        "Live Logs",
                        "Log Tools",
                        "Export",
                        "Search"
                },
                new int[]{
                        R.layout.log_tab_logs,
                        R.layout.log_tab_logtools,
                        R.layout.log_tab_export_logs,
                        R.layout.log_tab_search
                });
        UITAB_DATA[TAB_TOOLS] = new UITab(
                TAB_TOOLS,
                R.drawable.tab_tools_icon48,
                "Tools",
                TAB_TOOLS_MITEM_COLUMNS,
                new String[] {
                        "Slot View",
                        "Set Tag Types",
                        "Raw APDU",
                        "Commands",
                        "Peripheral Actions",

                },
                new int[]{
                        R.layout.tools_tab_slots,
                        R.layout.tools_tab_tag_config,
                        R.layout.tools_tab_apdu,
                        R.layout.tools_tab_commands,
                        R.layout.tools_tab_peripherals,
                });
        UITAB_DATA[TAB_EXPORT] = new UITab(
                TAB_EXPORT,
                R.drawable.tab_export_icon48,
                "Device I/O",
                TAB_EXPORT_MITEM_COLUMNS,
                new String[] {
                        "XModem",
                        "Clone Tags",
                },
                new int[]{
                        R.layout.export_tab_upload_download,
                        R.layout.export_tab_clone
                });
        UITAB_DATA[TAB_SCRIPTING] = new UITab(
                TAB_SCRIPTING,
                R.drawable.tab_scripting_icon48,
                "Scripts",
                TAB_SCRIPTING_MITEM_COLUMNS,
                new String[] {
                        "Main",
                        "Console View"
                },
                new int[] {
                        BuildConfig.DEBUG ? R.layout.scripting_tab_load_import : R.layout.tab_under_construction,
                        BuildConfig.DEBUG ? R.layout.scripting_tab_console_view : R.layout.tab_under_construction,
                });
        UITAB_DATA[TAB_CONFIG] = new UITab(
                TAB_CONFIG,
                R.drawable.tab_settings_icon48,
                "Settings",
                TAB_CONFIG_MITEM_COLUMNS,
                new String[] {
                        "General",
                        "Devices",
                        "Logging",
                        "Scripting"
                },
                new int[] {
                        R.layout.config_tab_general_settings,
                        R.layout.config_tab_connect,
                        R.layout.config_tab_logging,
                        R.layout.config_tab_scripting
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
        if(view != null) {
            UITabUtils.initializeTabMainContent(tabNumber, UITAB_DATA[tabNumber].lastMenuIndex, view);
        }
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