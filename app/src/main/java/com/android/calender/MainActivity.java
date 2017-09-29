package com.android.calender;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private SimpleDateFormat mDateFormat;
    private RelativeLayout mDepartDateRelLyt, mReturnDateRelLyt;
    private TextView mSourceDate;
    private String mCheckOutDateWithYear, mCheckInDateWithYear;
    private TextView mReturnDate;
    private TextView mDepartOn, mReturnOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mDateFormat = new SimpleDateFormat("dd MMM yy");
        mDepartDateRelLyt = (RelativeLayout) findViewById(R.id.departure_date_lyt);
        mSourceDate = (TextView) findViewById(R.id.source_date);
        mDepartDateRelLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDepartCalender();
            }
        });

        mDepartOn = (TextView) findViewById(R.id.label_depart_on);
        mReturnOn = (TextView) findViewById(R.id.label_return_on);

        mReturnDateRelLyt = (RelativeLayout) findViewById(R.id.return_date_container);
        mReturnDate = (TextView) findViewById(R.id.return_date);
        mReturnDateRelLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchReturnCalender();
            }
        });
        mCheckOutDateWithYear = mReturnDate.getTag().toString();
        mCheckInDateWithYear = mSourceDate.getTag().toString();
        setRoundTripView();

    }

    /**
     * Launch calender screen to select checkout date
     */
    private void launchReturnCalender() {
        if (mSourceDate.getText().toString().equalsIgnoreCase(getResources().getString(R.string
                .start_date))) {
            Toast.makeText(this, getResources().getString(R.string
                    .error) + "" + getResources().getString(R.string
                    .flight_select_depart_date), Toast.LENGTH_LONG).show();
            return;
        } else {
            Intent mCheckOutIntent = new Intent(this, TwoWayCalendarActivity.class);
            mCheckOutIntent.putExtra(Constants.KEY_DATE, mReturnDate.getText()
                    .toString());
            mCheckOutIntent.putExtra(Constants.INTENT_EXTRA_SELECTED_INTENT_TYPE,
                    Constants.INTENT_EXTRA_SELECTED_END_DATE);
            mCheckOutIntent.putExtra(Constants.KEY_DATE_NEXT_TYPE,
                    mCheckInDateWithYear);

            if (mCheckInDateWithYear != null && !mCheckInDateWithYear.equalsIgnoreCase
                    (Constants.KEY_START_DATE) &&
                    mCheckOutDateWithYear != null) {
                mCheckOutIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_START_DATE,
                        mCheckInDateWithYear);

                //if check out date is not already selected
                if (mCheckOutDateWithYear != null && mCheckOutDateWithYear.equalsIgnoreCase
                        (getResources().getString(R.string.end_date))) {
                    mCheckOutDateWithYear = null;
                }
                mCheckOutIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_END_DATE,
                        mCheckOutDateWithYear);
            }
            mCheckOutIntent.putExtra(Constants.KEY_CALENDER_START_DATE, mSourceDate.getText()
                    .toString());
            mCheckOutIntent.putExtra(Constants.KEY_DATE,
                    mCheckInDateWithYear);
            startActivityForResult(mCheckOutIntent, Constants
                    .REQUEST_CODE_RETURN_DATE);
        }

    }


    private String getTodaysDate() {
        Calendar c = Calendar.getInstance();
        return mDateFormat.format(c.getTime());
    }

    private void setRoundTripView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        mDepartDateRelLyt.setLayoutParams(params);
        mReturnDateRelLyt.setLayoutParams(params);
        mReturnDateRelLyt.setVisibility(View.VISIBLE);

        initializeViewForDomesticRoundTrip();
    }

    public void initializeViewForDomesticRoundTrip() {
        Resources res = getResources();
        if (mReturnDate.getText().toString().equalsIgnoreCase(res.getString(R.string
                .end_date))) {
            if (mReturnOn != null)
                mReturnOn.setVisibility(View.GONE);
        } else {
            mReturnDate.setVisibility(View.VISIBLE);
            if (mReturnOn != null)
                mReturnOn.setVisibility(View.VISIBLE);
        }
    }

    private void setCheckOutLyt(String checkOutDate, boolean b) {


        mReturnDate.setTag(AppUtility.formatDate(this, checkOutDate, Constants
                .KEY_DISPLAY_DATE_FORMAT, Constants
                .KEY_MONTH_DATE_FORMAT));
        mReturnDate.setText(AppUtility.formatDate(this, checkOutDate, Constants
                .KEY_DISPLAY_DATE_FORMAT, Constants
                .KEY_CALENDER_DATE_FORMAT));
        mReturnDate.setTextColor(getResources().getColor(R.color.black));
        //mReturnDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        //CJRAppUtility.setRobotoRegularFont(this.getActivity(), mReturnDate, Typeface.NORMAL);
        //mReturnDate.setPadding(0, 5, 0, 0);
        //mReturnDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        //mCal_ret_date.setVisibility(View.GONE);
        mReturnOn.setVisibility(View.VISIBLE);
        //sendDateSelectedGTMEvent(checkOutDate);
        mCheckOutDateWithYear = checkOutDate;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            //check-in date from Calender screen
            case Constants.REQUEST_CODE_DEPART_DATE: {
                if (data != null && data.hasExtra(Constants
                        .INTENT_EXTRA_SELECTED_START_DATE) &&
                        data.hasExtra(Constants.INTENT_EXTRA_SELECTED_END_DATE)) {
                    //set depart date
                    String checkInDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_START_DATE);
                    setCheckInLyt(checkInDate, false);
                    //set return date
                    String checkOutDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_END_DATE);
                    setCheckOutLyt(checkOutDate, false);

                } else if (data != null && data.hasExtra(Constants
                        .INTENT_EXTRA_SELECTED_START_DATE)) {
                    String checkInDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_START_DATE);
                    setCheckInLyt(checkInDate, false);

                } else if (data != null && data.hasExtra(Constants
                        .INTENT_EXTRA_SELECTED_END_DATE)) {
                    //set return date
                    String checkOutDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_END_DATE);
                    setCheckOutLyt(checkOutDate, false);
                }
                break;
            }
            case (Constants.REQUEST_CODE_RETURN_DATE): {
                if (data != null && data.hasExtra(Constants
                        .INTENT_EXTRA_SELECTED_START_DATE) &&
                        data.hasExtra(Constants.INTENT_EXTRA_SELECTED_END_DATE)) {
                    //set depart date
                    String checkInDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_START_DATE);
                    setCheckInLyt(checkInDate, false);
                    //set return date
                    String checkOutDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_END_DATE);
                    setCheckOutLyt(checkOutDate, false);
                } else if (data != null && data.hasExtra(Constants
                        .INTENT_EXTRA_SELECTED_END_DATE)) {
                    //set return date
                    String checkOutDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_END_DATE);
                    setCheckOutLyt(checkOutDate, false);
                } else if (data != null && data.hasExtra(Constants
                        .INTENT_EXTRA_SELECTED_START_DATE)) {
                    String checkInDate = data.getStringExtra(Constants
                            .INTENT_EXTRA_SELECTED_START_DATE);
                    setCheckInLyt(checkInDate, false);

                }
                break;
            }
        }
    }

    private void resetCheckOutDate() {
        if (!mReturnDate.getText().toString().equalsIgnoreCase(getResources().getString(R.string
                .end_date))) {
            mReturnDate.setText(getResources().getString(R.string.end_date));
            mReturnDate.setTextColor(getResources().getColor(R.color.gray));
            mCheckOutDateWithYear = getResources().getString(R.string.end_date);
        }
    }


    private void setCheckInLyt(String checkInDate, boolean b) {
        resetCheckOutDate();

        mSourceDate.setTag(AppUtility.formatDate(this, checkInDate, Constants
                .KEY_DISPLAY_DATE_FORMAT, Constants
                .KEY_MONTH_DATE_FORMAT));
        mSourceDate.setText(AppUtility.formatDate(this, checkInDate, Constants
                .KEY_DISPLAY_DATE_FORMAT, Constants
                .KEY_CALENDER_DATE_FORMAT));

        mSourceDate.setTextColor(getResources().getColor(R.color.black));
        //mSourceDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        //AppUtility.setRobotoRegularFont(this, mSourceDate, Typeface.NORMAL);
        // mSourceDate.setPadding(0, 5, 0, 0);
        //mSourceDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        //mCal_dep_date.setVisibility(View.GONE);
        mDepartOn.setVisibility(View.VISIBLE);
        //sendDateSelectedGTMEvent(checkInDate);
        mCheckInDateWithYear = checkInDate;
    }

    public void launchDepartCalender() {


        Intent mCheckInIntent;
//        if (!mDomesticRoundTrip
//                .isChecked()) {
//            mCheckInIntent = new Intent(this, AJRFlightOneWayCalender.class);
//        } else {
        mCheckInIntent = new Intent(this, TwoWayCalendarActivity.class);
//        }

        if (mSourceDate.getText().toString().equalsIgnoreCase(getResources().getString(R.string
                .start_date))) {
            mCheckInDateWithYear = getTodaysDate();
        } else {
            mCheckInDateWithYear = mSourceDate.getText().toString();
            mCheckInDateWithYear = AppUtility.formatDate(this, mCheckInDateWithYear,
                    Constants.KEY_CALENDER_DATE_FORMAT, Constants
                            .KEY_MONTH_DATE_FORMAT);
        }

        mCheckInIntent.putExtra(Constants.KEY_DATE, mCheckInDateWithYear);
        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_SELECTED_INTENT_TYPE,
                Constants.INTENT_EXTRA_SELECTED_START_DATE);

//        if (!mDomesticRoundTrip.isChecked()) {
//            if (!mCheckInDateWithYear.equalsIgnoreCase(Constants.KEY_START_DATE)) {
//                mCheckInIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_START_DATE,
//                        mCheckInDateWithYear);
//            }
//        } else {
        mCheckInIntent.putExtra(Constants.KEY_DATE_NEXT_TYPE,
                mCheckOutDateWithYear);

        if (mCheckInDateWithYear != null && !mCheckInDateWithYear.equalsIgnoreCase
                (Constants.KEY_START_DATE) &&
                mCheckOutDateWithYear != null && !mCheckOutDateWithYear.equalsIgnoreCase
                (getResources().getString(R.string
                        .end_date))) {
            mCheckInIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_START_DATE,
                    mCheckInDateWithYear);
            mCheckInIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_END_DATE,
                    mCheckOutDateWithYear);
        }
        if (mCheckInDateWithYear != null && !mCheckInDateWithYear.equalsIgnoreCase
                (Constants.KEY_START_DATE) &&
                mCheckOutDateWithYear != null && mCheckOutDateWithYear.equalsIgnoreCase
                (getResources().getString(R.string
                        .end_date))) {
            if (mCheckInDateWithYear != null) {
                mCheckInIntent.putExtra(Constants.INTENT_EXTRA_RESETED_START_DATE,
                        mCheckInDateWithYear);
            }
        }
//        }

        mCheckInIntent.putExtra(Constants.KEY_DATE,
                mCheckOutDateWithYear);

        mCheckInIntent.putExtra(Constants.KEY_CALENDER_END_DATE, mReturnDate.getText()
                .toString());

//        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_SEARCH_NO_OF_ADULT_PASSENGERS,
//                mAdults);
//        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_SEARCH_NO_OF_CHILD_PASSENGERS,
//                mChildren);
//        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_SEARCH_NO_OF_INFANTS_PASSENGERS,
//                mInfants);
//        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_DESTINATION_CITY_CODE,
//                mDestCityCode.getText().toString());
//        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_SOURCE_CITY_CODE,
//                mSourceCityCode.getText().toString());
//        if (mClassTypevalue != null) {
//            mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_CLASS_TYPE, getClassTypeCode());
//        }
//        mCheckInIntent.putExtra(Constants.FLIGHT_FARE_API_CALLED, true);
        //mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_FARES_API, true);
        //makeCalendarFaresApiCall();
        startActivityForResult(mCheckInIntent, Constants.REQUEST_CODE_DEPART_DATE);
    }
}
