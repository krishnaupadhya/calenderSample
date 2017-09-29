package com.android.calender;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

/**
 * Created by Akanksha on 11-Aug-16.
 */
public class CJRFlightCalenderAdapter extends FragmentPagerAdapter {
    private final SparseArray<Fragment> mPageReferences;
    private ArrayList<String> mTitles;

    public CJRFlightCalenderAdapter(Context context, FragmentManager fm,
                                    ArrayList<String> inTitles) {
        super(fm);
        mPageReferences = new SparseArray<Fragment>();
        mTitles= inTitles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        FJRFlightCalendarFragment fragment = new FJRFlightCalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        mPageReferences.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    public Fragment getFragment(int position) {
        try {
            return mPageReferences.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            mPageReferences.remove(position);
            Fragment fragment = (Fragment) object;
            View view = (View) fragment.getView();
            ((ViewPager) container).removeView(view);
        } catch (Exception e) {
             e.printStackTrace();
        }
        super.destroyItem(container, position, object);
    }
}