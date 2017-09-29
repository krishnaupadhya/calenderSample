package com.android.calender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private SimpleDateFormat mDateFormat;
    private RelativeLayout mDepartDateRelLyt;
    private TextView mSourceDate;
    private String mCheckOutDateWithYear, mCheckInDateWithYear;

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
    }

    private String getTodaysDate() {
        Calendar c = Calendar.getInstance();
        return mDateFormat.format(c.getTime());
    }

    public void launchDepartCalender() {

        Intent mCheckInIntent;

        mCheckInIntent = new Intent(this, AJRFlightTwoWayCalendar.class);


        if (mSourceDate.getText().toString().equalsIgnoreCase(getResources().getString(R.string
                .flight_depart))) {
            mCheckInDateWithYear = getTodaysDate();
        } else {
            mCheckInDateWithYear = mSourceDate.getText().toString();
            mCheckInDateWithYear = AppUtility.formatDate(this, mCheckInDateWithYear,
                    Constants.TRAVEL_FLIGHT_HOMESCREEN_DATE_FORMAT, Constants
                            .TRAVEL_VERTICALS_HOMESCREEN_DATE_FORMAT);
        }

        mCheckInIntent.putExtra(Constants.FLIGHT_BOOK_DATE, mCheckInDateWithYear);
        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_SELECTED_INTENT_TYPE,
                Constants.INTENT_EXTRA_SELECTED_DEPART_DATE);
        mCheckInIntent.putExtra(Constants.INTENT_EXTRA_IS_ONE_WAY, false);


//        if (!mDomesticRoundTrip.isChecked()) {
//            if (!mCheckInDateWithYear.equalsIgnoreCase(Constants.FLIGHT_DEPART)) {
//                mCheckInIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_DEPART_DATE,
//                        mCheckInDateWithYear);
//            }
//        } else {
        mCheckInIntent.putExtra(Constants.FLIGHT_TICKET_DATE_NEXT_TYPE,
                mCheckOutDateWithYear);

//            if (mCheckInDateWithYear != null && !mCheckInDateWithYear.equalsIgnoreCase
//                    (Constants.FLIGHT_DEPART) &&
//                    mCheckOutDateWithYear != null && !mCheckOutDateWithYear.equalsIgnoreCase
//                    (getResources().getString(R.string
//                            .select_return_date))) {
//                mCheckInIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_DEPART_DATE,
//                        mCheckInDateWithYear);
//                mCheckInIntent.putExtra(Constants.INTENT_EXTRA_UPDATED_RETURN_DATE,
//                        mCheckOutDateWithYear);
//            }
//            if (mCheckInDateWithYear != null && !mCheckInDateWithYear.equalsIgnoreCase
//                    (Constants.FLIGHT_DEPART) &&
//                    mCheckOutDateWithYear != null && mCheckOutDateWithYear.equalsIgnoreCase
//                    (getResources().getString(R.string
//                            .select_return_date))) {
//                if (mCheckInDateWithYear != null) {
//                    mCheckInIntent.putExtra(Constants.INTENT_EXTRA_RESETED_DEPART_DATE,
//                            mCheckInDateWithYear);
//                }
//            }
        //}

//        mCheckInIntent.putExtra(Constants.HOTEL_BOOK_DATE_NEXT_TYPE,
//                mCheckOutDateWithYear);
//
//        mCheckInIntent.putExtra(Constants.ConstantsConstants, mReturnDate.getText()
//                .toString());
////
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
//        //mCheckInIntent.putExtra(Constants.INTENT_EXTRA_FLIGHT_FARES_API, true);
//        makeCalendarFaresApiCall();
        startActivityForResult(mCheckInIntent, Constants.REQUEST_CODE_DEPART_DATE);
    }
}
