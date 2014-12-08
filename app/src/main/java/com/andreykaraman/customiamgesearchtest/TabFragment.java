package com.andreykaraman.customiamgesearchtest;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragment;
import com.andreykaraman.customiamgesearchtest.adapters.TabsAdapter;


/**
 * Created by KaramanA on 04.12.2014.
 */
public class TabFragment extends SherlockFragment {

    TabHost tabHost;
    ViewPager viewPager;
    TabsAdapter tabsAdapter;

    static TabFragment newInstance() {
        return new TabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabs_pager, container, false);

        tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
        tabHost.setup();

        viewPager = (ViewPager) v.findViewById(R.id.pager);

        tabsAdapter = new TabsAdapter(getActivity(), tabHost, viewPager);

        tabsAdapter.addTab(tabHost.newTabSpec("simple").setIndicator(getResources().getString(R.string.search)),
                FragmentSearch.class, null);
        tabsAdapter.addTab(tabHost.newTabSpec("simple").setIndicator(getResources().getString(R.string.bookmarks)),
                FragmentBookmarks.class, null);

        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }


}
