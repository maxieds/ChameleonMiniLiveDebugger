package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_TOOLS;
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
    public static TabFragment[] tabFragments = {
            TabFragment.newInstance(TAB_LOG),
            TabFragment.newInstance(TAB_TOOLS),
            TabFragment.newInstance(TAB_LOG_TOOLS),
            TabFragment.newInstance(TAB_EXPORT),
    };
    FragmentManager fm;

    /**
     * Reload the content tabs.
     */
    public TabFragmentPagerAdapter reloadTabData() {
        for (int i = 0; i < tabFragments.length; i++) {
            fm.beginTransaction().remove(tabFragments[i]).commit();
        }
        tabFragments[0] = TabFragment.newInstance(TAB_LOG);
        tabFragments[1] = TabFragment.newInstance(TAB_TOOLS);
        tabFragments[2] = TabFragment.newInstance(TAB_LOG_TOOLS);
        tabFragments[3] = TabFragment.newInstance(TAB_EXPORT);
        notifyDataSetChanged();
        return this;
    }

    /**
     * Corresponding titles of each tab.
     */
    private String tabTitles[] = new String[]{
            "Log", "Tools Menu", "Log Tools", "Export",
    };

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
        //return TabFragment.newInstance(tabid);
    }

    /**
     * Get the displayed title of the tab at this index.
     * @param position
     * @return String tab title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

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