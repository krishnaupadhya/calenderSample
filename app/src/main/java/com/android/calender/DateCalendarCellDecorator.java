package com.android.calender;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DateCalendarCellDecorator implements CalendarCellDecorator {

    private final Context context;
    private final HashMap<String, DatePriceInfo> mDatePriceInfoArrayList;
    private final boolean mShouldStopLoader;
    private final Handler mHandler;

    private Date todaysDate;

    public DateCalendarCellDecorator(Context context, HashMap<String, DatePriceInfo> map , boolean shouldStopLoader , Handler handler) {
        this.context = context;
        mDatePriceInfoArrayList = map;

        todaysDate = Calendar.getInstance().getTime();
        mShouldStopLoader = shouldStopLoader;
        mHandler = handler;
    }

    @Override
    public void decorate(CalendarCellView cellView, final Date date) {
        try {
            //get date to be shown with the fare
            final String dateString = getDateString(date);
            //date key to compare with the list of fare data
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String key = mFormat.format(date);

            final TextView dateTextView = (TextView) cellView.findViewById(R.id.flight_calendar_custom_date_cell);
            final TextView priceTextView = (TextView) cellView.findViewById(R.id.flight_calendar_custom_price_cell);

            if(!cellView.isSelected()) {
                cellView.getDayOfMonthTextView().setTextColor(ContextCompat.getColor(context , R.color.date_grey));
            }

            final DottedProgressBar progressBar = (DottedProgressBar) cellView.findViewById(R.id.progressBar);
            if(progressBar != null  ) {
                if(mShouldStopLoader) {
                    progressBar.stopProgress();
                    progressBar.setVisibility(View.GONE);

                } else {
                    if(cellView.isCurrentMonth()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        } );

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        } );

                    }
                }
            }

            DatePriceInfo priceInfo = mDatePriceInfoArrayList == null ? null : mDatePriceInfoArrayList.get(key);

            SpannableString spannedNormalString = null;


            if(priceTextView != null) {
                priceTextView.setText("");
            }

            if(!cellView.isCurrentMonth() ) {
                cellView.getDayOfMonthTextView().setTextColor(ContextCompat.getColor(context , R.color.fareCalender_line ));
                return;
            }

            if(dateTextView != null ) {
                dateTextView.setText(null);
                dateTextView.setText(dateString);
                dateTextView.setTag(date);

                if(priceTextView != null ) {
                    priceTextView.setVisibility(View.VISIBLE);
                    if(!cellView.isSelected()) {
                        priceTextView.setTextColor(ContextCompat.getColor(context, R.color.date_grey));
                    } else {
                        priceTextView.setTextColor(dateTextView.getCurrentTextColor());
                    }
                    String formatedPriceFare = null;

                    if(priceInfo != null ) {

                        if (priceInfo.getFare() != -1) {
                            formatedPriceFare = String.valueOf(priceInfo.getFare());
                        }

                        if (!TextUtils.isEmpty(formatedPriceFare) && formatedPriceFare.length() > 3) {
                            formatedPriceFare = formatedPriceFare.substring(0, formatedPriceFare.length() - 3) + "," +
                                    formatedPriceFare.substring(formatedPriceFare.length() - 3, formatedPriceFare.length());
                        }

                        if (!TextUtils.isEmpty(formatedPriceFare) && formatedPriceFare.length() > 6) {
                            formatedPriceFare = formatedPriceFare.substring(0, formatedPriceFare.length() - 6) + "," +
                                    formatedPriceFare.substring(formatedPriceFare.length() - 6, formatedPriceFare.length());
                        }

                        if (!TextUtils.isEmpty(formatedPriceFare)) {
                            formatedPriceFare = "₹" + formatedPriceFare;
                            if (!cellView.isSelected()) {
                                priceTextView.setTextColor(Color.parseColor("#" + priceInfo.getColorCode()));
                            } else {
                                priceTextView.setTextColor(dateTextView.getCurrentTextColor());
                            }
                        }
                    }
                    priceTextView.setText(formatedPriceFare);
                    if(TextUtils.isEmpty(formatedPriceFare)) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                priceTextView.setVisibility(View.GONE);
                            }
                        });
                    }

                }

                return;
            }

        } catch (Exception e) {
            System.out.println("exception DateCalendarCellDecorator " + e.getMessage());
        }
    }

    private String getDateString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dateValue = String.valueOf(calendar.get(Calendar.DATE));

        if(dateValue.length() > 1) {
            return String.format("%02d", calendar.get(Calendar.DATE));
        } else {
            return String.format("%1d", calendar.get(Calendar.DATE));
        }
    }
}
