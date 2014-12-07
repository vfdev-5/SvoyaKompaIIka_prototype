package com.vfdev.android.svoyakompaiika;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by vfomin on 11/26/14.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragments;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        // Setup fragments:
        mFragments = new ArrayList<Fragment>();
    }

    public void appendFragment(Fragment f) {
        mFragments.add(f);
    }


    @Override
    public Fragment getItem(int position) {

        if (position >=0 && position < mFragments.size()) {
            return mFragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
//        switch (position) {
//            case POINTS_PAGE:
//                return getResources().getString(R.string.list_tab_name).toUpperCase(l);
//            case MAP_PAGE:
//                return getResources().getString(R.string.map_tab_name).toUpperCase(l);
//        }
        if (position >=0 && position < mFragments.size()) {
            Bundle bundle = mFragments.get(position).getArguments();
            if (bundle != null) {
                return bundle.getString("Title");
            }
        }
        return "Fragment_title";

    }

}