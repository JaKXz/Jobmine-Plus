package com.jobmineplus.mobile.activities.jbmnpls;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.jobmineplus.mobile.R;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public abstract class JbmnplsPageActivityBase extends JbmnplsActivityBase implements ViewPager.OnPageChangeListener {
    private TabAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;
    private int currentIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view if not set yet
        if (findViewById(R.id.pager) == null) {
            setContentView(R.layout.tabs);
        }
        mAdapter = new TabAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setOnPageChangeListener(this);
    }

    public void createTab(String title, Fragment fragment) {
        mAdapter.createTab(title, fragment);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public String getCurrentTabName() {
        return (String) mAdapter.getPageTitle(currentIndex);
    }

    public Fragment getCurrentFragment() {
        return getFragment(currentIndex);
    }

    public int numOfTabs() {
        return mAdapter.getCount();
    }

    public Fragment getFragment(int index) {
        return mAdapter.getItem(index);
    }

    public Fragment getFragment(String title) {
        return mAdapter.getItem(title);
    }

    //=====================
    //  Empty Text
    //=====================
    public void setEmptyText(CharSequence text) {
        ((TextView)findViewById(R.id.emptyText)).setText(text);
    }

    public void setIsEmpty(boolean isEmpty) {
        View text = findViewById(R.id.emptyText);
        View layout = findViewById(R.id.layout);
        if (isEmpty) {
            text.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
        } else {
            text.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        }
    }

    //=====================
    //  View Pager Changer
    //=====================
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int index) {
        currentIndex = index;
    }

    //=================
    //  Custom Adapter
    //=================
    protected final class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        private final HashMap<String, Fragment> tabFragments = new HashMap<String, Fragment>();
        private final ArrayList<String> tabTitles = new ArrayList<String>();

        public void createTab(String title, Fragment fragment) {
            if (!tabTitles.contains(title)) {
                tabTitles.add(title);
            }
            Fragment prev = tabFragments.get(title);
            if (prev == null || !prev.equals(fragment)) {
                tabFragments.put(title, fragment);
            }
        }

        @Override
        public int getCount() {
            return tabTitles.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return FragmentPagerAdapter.POSITION_NONE;
        }

        public Fragment getItem(String title) {
            return tabFragments.get(title);
        }

        @Override
        public Fragment getItem(int position) {
            String key = (String) getPageTitle(position);
            return getItem(key);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }
    }
}
