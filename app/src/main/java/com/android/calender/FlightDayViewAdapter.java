package com.android.calender;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.DayViewAdapter;

/**
 * Created by prashant on 10/09/16.
 */
public class FlightDayViewAdapter implements DayViewAdapter {
    @Override
    public void makeCellView(CalendarCellView parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_calender_custom_cell , null , false);
        parent.addView(view);
        parent.setDayOfMonthTextView((TextView) view.findViewById(R.id.flight_calendar_custom_date_cell));
//        parent.getDayOfMonthTextView().setTextColor(ContextCompat.getColor(parent.getContext() , R.color.black));
    }
}
