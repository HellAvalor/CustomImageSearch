package com.andreykaraman.idstest;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.util.ArrayList;

public class MainActivity extends SherlockFragmentActivity {
    TabHost tabHost;
    ViewPager viewPager;
    TabsAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_tabs_pager);
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        viewPager = (ViewPager) findViewById(R.id.pager);

        tabsAdapter = new TabsAdapter(this, tabHost, viewPager);

        tabsAdapter.addTab(tabHost.newTabSpec("simple").setIndicator(getResources().getString(R.string.search)),
                FragmentSearch.SearchFragment.class, null);
        tabsAdapter.addTab(tabHost.newTabSpec("simple").setIndicator(getResources().getString(R.string.bookmarks)),
                FragmentBookmarks.BookmarksFragment.class, null);
        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }

    public static class TabsAdapter extends FragmentPagerAdapter
            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context context;
        private final TabHost tabHost;
        private final ViewPager viewPager;
        private final ArrayList<TabInfo> tabInfos = new ArrayList<TabInfo>();

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            context = activity;
            this.tabHost = tabHost;
            viewPager = pager;
            this.tabHost.setOnTabChangedListener(this);
            viewPager.setAdapter(this);
            viewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(context));
            String tag = tabSpec.getTag();
            TabInfo info = new TabInfo(tag, clss, args);
            tabInfos.add(info);
            tabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return tabInfos.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = tabInfos.get(position);
            return Fragment.instantiate(context, info.tabClass.getName(), info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
            int position = tabHost.getCurrentTab();
            viewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            TabWidget widget = tabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            tabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        static final class TabInfo {
            private final String tag;
            private final Class<?> tabClass;
            private final Bundle args;

            TabInfo(String tag, Class<?> tabClass, Bundle args) {
                this.tag = tag;
                this.tabClass = tabClass;
                this.args = args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context context;

            public DummyTabFactory(Context context) {
                this.context = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(context);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }
    }
}
