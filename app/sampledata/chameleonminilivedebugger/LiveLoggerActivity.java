package com.maxieds.chameleonminilivedebugger;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class LiveLoggerActivity extends FragmentActivity {

    private static final String TAG = LiveLoggerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_logger);

        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_pager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), LiveLoggerActivity.this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
