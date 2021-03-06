package com.android.calender;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Akanksha on 11-Aug-16.
 */
public class FJRFlightCalendarFragment extends Fragment {

    private CJRFlightCalendarHelper mCalenderHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender_picker_lyt, container, false);
        int position = 0;
        if(getArguments() != null)
            position = getArguments().getInt("position");
        mCalenderHelper.initView(view,position , true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CJRFareCalendarDataTransformer.getObserver().addObserver(mCalenderHelper);
    }

    @Override
    public void onStop() {
        super.onStop();
        CJRFareCalendarDataTransformer.getObserver().deleteObservers();
    }

    //attach fragment
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(mCalenderHelper == null) {
            mCalenderHelper = new CJRFlightCalendarHelper();
        }
        mCalenderHelper.onAttachToFragment(activity);
    }

    //detach fragment
    @Override
    public void onDetach() {
        super.onDetach();
        if(mCalenderHelper != null) {
            mCalenderHelper.onDetachFromFragment();
        }
    }

    public void loadCalender(String mIntentType, boolean isScrolledToCheckOut) {
        //load calender data based on intent data sent
        mCalenderHelper.setIntentType(mIntentType, isScrolledToCheckOut);
    }

    public void loadCalenderWithCheckInData(String mCheckInDateWithYear, String mCheckOutDateWithYear) {
        mCalenderHelper.setCheckInDateSelected(mCheckInDateWithYear, mCheckOutDateWithYear, Constants.INTENT_EXTRA_SELECTED_RETURN_DATE);
    }

    public void loadCalenderWithCheckOutData(String mCheckInDateWithYear, String mCheckOutDateWithYear) {
        mCalenderHelper.setCheckOutDateSelected(mCheckInDateWithYear, mCheckOutDateWithYear, Constants.INTENT_EXTRA_SELECTED_DEPART_DATE);
    }
}

