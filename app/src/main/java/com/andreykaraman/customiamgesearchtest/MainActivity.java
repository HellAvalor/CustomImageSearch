package com.andreykaraman.customiamgesearchtest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created by KaramanA on 04.12.2014.
 */
public class MainActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Fragment newFragment = TabFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.add(R.id.content_container, newFragment, "tabs");
            ft.commit();
        }
    }
}
