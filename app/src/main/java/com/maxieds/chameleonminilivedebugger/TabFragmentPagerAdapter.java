package com.maxieds.chameleonminilivedebugger;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_EXPORT;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_LOG_TOOLS;
import static com.maxieds.chameleonminilivedebugger.TabFragment.TAB_TOOLS;

/**
 * Created by mschmidt34 on 12/31/2017.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public static final int TAB_COUNT = 4;
        public static TabFragment[] tabFragments = {
                TabFragment.newInstance(TAB_LOG),
                TabFragment.newInstance(TAB_TOOLS),
                TabFragment.newInstance(TAB_LOG_TOOLS),
                TabFragment.newInstance(TAB_EXPORT),
        };

        private String tabTitles[] = new String[] {
                "Log", "Tools Menu", "Log Tools", "Export",
        };
        private Context context;

        public TabFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_UNCHANGED;
        }

        @Override
        public Fragment getItem(int tabid) {
            return tabFragments[tabid];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
}
