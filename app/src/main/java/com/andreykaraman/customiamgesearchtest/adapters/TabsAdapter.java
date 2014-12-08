package com.andreykaraman.customiamgesearchtest.adapters;

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

import java.util.ArrayList;

/**
 * Created by KaramanA on 04.12.2014.
 */
public class TabsAdapter extends FragmentPagerAdapter
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

    public void addTab(TabHost.TabSpec tabSpec, Class<?> tabClass, Bundle args) {
        tabSpec.setContent(new TabFactory(context));
        String tag = tabSpec.getTag();
        TabInfo info = new TabInfo(tag, tabClass, args);
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

    static class TabFactory implements TabHost.TabContentFactory {
        private final Context context;

        public TabFactory(Context context) {
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