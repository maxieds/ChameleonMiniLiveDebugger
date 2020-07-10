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

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_CONFIG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;

/**
 * <h1>Tab Fragment Pager Adapter</h1>
 * Implements a FragmentPagerAdapter for the tabs in the application.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = TabFragmentPagerAdapter.class.getSimpleName();

    /**
     * Stores the data for each tab (only one instance created at runtime).
     */
    public static final int TAB_COUNT = 4;
    public TabFragment[] tabFragments = {
            TabFragment.newInstance(TAB_LOG),
            TabFragment.newInstance(TAB_TOOLS),
            TabFragment.newInstance(TAB_EXPORT),
            TabFragment.newInstance(TAB_CONFIG),
    };
    FragmentManager fm;

    /**
     * Store the context used to initialize the object.
     */
    private Context context;

    /**
     * Constructor.
     * @param fmParam
     * @param context
     */
    public TabFragmentPagerAdapter(FragmentManager fmParam, Context context) {
        super(fmParam);
        fm = fmParam;
        this.context = context;
    }

    /**
     * Returns the total number of tabs in the application.
     * @return
     */
    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    /**
     * Overridden method returns the constant POSITION_UNCHANGED to indicate that no
     * tab change has occurred.
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * Get the Fragment data associated with the tab at this index.
     * @param tabid
     * @return Fragment inflated tab display data
     */
    @Override
    public Fragment getItem(int tabid) {
        return tabFragments[tabid];
    }

    /**
     * Get the displayed title of the tab at this index.
     * @param position
     * @return String tab title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return tabFragments[position].getTabTitle();
    }

    public int getTabIcon(int position) { return tabFragments[position].getTabIcon(); }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        Log.w(TAG, "destroyItem called on tab #" + String.valueOf(position));
    }

    @Override
    public Object instantiateItem(View collection, int position) {
        Log.w(TAG, "instantiateItem called on tab #" + String.valueOf(position));
        return null;
    }

}