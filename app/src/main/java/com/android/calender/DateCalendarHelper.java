package com.android.calender;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.DefaultDayViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class DateCalendarHelper implements CalendarPickerView.OnDateSelectedListener, View.OnClickListener,Observer {
    protected Activity mActivity;
    private CalendarPickerView calendar;
    private long mPreviouslySelectedDate;
    private TextView mSelectedDate;
    private String inputFormat, outputFormat, todayDateString, mIntentType;
    private Date dateToday;
    private RelativeLayout mFooterLayout;
    private String selectedDateWithYear;
    private Locale appLocale;
    private IJRCheckInDateSelector mCheckInDateListener;
    private IJRCheckOutDateSelector mCheckOutDateListener;
    private String mSelectedCheckInDate, mSelectedCheckOutDate;
    private Calendar nextYear,currentYear, previousMonth;
    private boolean isScrolledToCheckOut;
    private String mOtherDate;
    private TextView mTodayDate, mTommorrowDate, mTodayLabel, mTomorrowLabel;
    private Calendar todayDate, tomorrowDate, dayAftertomorrowDate;
    private LinearLayout mTodaysDateSelector, mTomorrowsDateSelector;
    private CalendarPriceModel priceInfo;
    private HashMap<String , DatePriceInfo> mDatePriceMap;
    private int fragmentPos = 0;
    private boolean mUseLoaderAdapter = false;

    public void initView(View view, int position , boolean useLoaderAdapter) {
        if(mActivity != null) {
            mCheckInDateListener = (IJRCheckInDateSelector) mActivity;
            mCheckOutDateListener = (IJRCheckOutDateSelector) mActivity;
        }

        mUseLoaderAdapter = useLoaderAdapter;
        fragmentPos = position;

        //hide calender header view for hotels
        LinearLayout mCalenderHeader = (LinearLayout) view.findViewById(R.id.selected_date_lyt);
        mCalenderHeader.setVisibility(View.GONE);

        getDataFromIntent();

        priceInfo = CalendarDataTransformer.getObserver().getCalendarPriceModel();

        //initialize calender view
        initializeCalenderView(view);

        //initialize adjacent btns
        initUIViews(view);

        //set today's date
        setInitialDate(view);

        setFooterLyt(view);
    }

    private void initializeCalenderView(View view) {
        inputFormat = Constants.TRAIN_CALENDER_INPUT_TIME_FORMAT;
        outputFormat = Constants.TRAIN_CALENDER_OUTPUT_TIME_FORMAT;

        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        currentYear = Calendar.getInstance();

        previousMonth = Calendar.getInstance();
        previousMonth.setTime(previousMonth.getTime());
        previousMonth.add(Calendar.DAY_OF_YEAR, -30);



        calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);

        //date to be selected in UI at the beginning
        Date mDateToBeSelected;
        if(mPreviouslySelectedDate != 0 /*&& checkIfDateIsWithinRange()*/) {
            mDateToBeSelected = new Date();
            mDateToBeSelected.setTime(mPreviouslySelectedDate);
        } else {
            mDateToBeSelected = new Date();
        }

        //set which type of calender should be inflated
        setCalenderUI();

        calendar.setCustomDayView(new DefaultDayViewAdapter());
        calendar.setOnDateSelectedListener(this);
    }

    private void setCalenderUI() {
        if(mSelectedCheckInDate != null && mSelectedCheckOutDate != null) {
            setRangeCalenderUI();
        } else if(mSelectedCheckInDate != null) {
            setSingleCalenderUI(mSelectedCheckInDate);
        } else if(mSelectedCheckOutDate != null) {
            setSingleCalenderUI(mSelectedCheckOutDate);
        } else {
            setSingleCalenderUI(null);
        }
    }

    private void setSingleCalenderUI(String mSelectedCheckInDate) {
        Date selectedCheckInDate = CalenderUtils.returnDateValue(mActivity, mSelectedCheckInDate);
        boolean shouldStopLoader = false;
        if(!TextUtils.isEmpty(mSelectedCheckInDate)) {
            shouldStopLoader = true;
        }
        setfareMap(selectedCheckInDate , shouldStopLoader);

        calendar.init(previousMonth.getTime(), nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(selectedCheckInDate);
    }

    private void setRangeCalenderUI() {
        ArrayList<Date> dates = new ArrayList<Date>();
        if(mSelectedCheckInDate != null) {
            Date selectedCheckInDate = CalenderUtils.returnDateValue(mActivity, mSelectedCheckInDate);
            Date selectedCheckOutDate = CalenderUtils.returnDateValue(mActivity, mSelectedCheckOutDate);

            dates.add(selectedCheckInDate);
            dates.add(selectedCheckOutDate);



            calendar.init(previousMonth.getTime(), nextYear.getTime()) //
                    .inMode(CalendarPickerView.SelectionMode.RANGE) //
                    .withSelectedDates(dates);
        }
    }

    private void initUIViews(View view) {
        mSelectedDate = (TextView) view.findViewById(R.id.selected_date);

        int gridUnit = AppUtility.getScreenGridUnitBy32(mActivity);
        mSelectedDate.setPadding(0, gridUnit/2, 0, gridUnit);

        View sepView = view.findViewById(R.id.sep_view);
        sepView.setVisibility(View.GONE);

        mFooterLayout = (RelativeLayout) view.findViewById(R.id.footer_lyt);
    }

    private void setInitialDate(View view) {
        String mUserSelectedDate = "";

        Calendar today = Calendar.getInstance(appLocale);
        SimpleDateFormat originalFormat = new SimpleDateFormat(Constants.TRAIN_CALENDER_OUTPUT_TIME_FORMAT, appLocale);
        todayDateString = originalFormat.format(today.getTime());
        dateToday = today.getTime();

        if(mPreviouslySelectedDate != 0) {
            Date date = new Date();
            date.setTime(mPreviouslySelectedDate);
//            if(LocaleHelper.getDefaultLanguage().equalsIgnoreCase("hi") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("ta") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("te") ||
//                    LocaleHelper.getDefaultLanguage().equalsIgnoreCase("kn") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("pa") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("mr")
//                    || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("gu") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("bn") ||
//                    LocaleHelper.getDefaultLanguage().equalsIgnoreCase("or") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("ml")) {
//                mUserSelectedDate = CJRAppUtility.convertDateToLocale(mActivity, date.toString(), "EEE MMM dd HH:mm:ss zzz yyyy", outputFormat, mPreviouslySelectedDate);
//            } else {
                mUserSelectedDate = CalenderUtils.formatDate(mActivity, date.toString(), inputFormat, outputFormat);
//            }
            mSelectedDate.setText(mUserSelectedDate);
        } else if(todayDateString != null && !TextUtils.isEmpty(todayDateString)) {
            mSelectedDate.setText(todayDateString);
        } else {
            mSelectedDate.setVisibility(View.GONE);
        }
        LinearLayout mAdjacentBtns = (LinearLayout) view.findViewById(R.id.adjacent_btn_lyt);
        mAdjacentBtns.setVisibility(View.GONE);

    }

    private void getDataFromIntent() {
       appLocale = new Locale(LocaleHelper.getDefaultLanguage());

        Intent i = mActivity.getIntent();
        mIntentType = i.getStringExtra(Constants.INTENT_EXTRA_SELECTED_INTENT_TYPE);

        if(i.hasExtra(Constants.INTENT_EXTRA_SELECTED_DATE_TIME)) {
            mPreviouslySelectedDate = i.getLongExtra(Constants.INTENT_EXTRA_SELECTED_DATE_TIME, 0);
        }
        if(i.hasExtra(Constants.EXTRA_INTENT_CALENDER_SELECTED_DATE)) {
            selectedDateWithYear = i.getStringExtra(Constants.EXTRA_INTENT_CALENDER_SELECTED_DATE);
        }
        if(mPreviouslySelectedDate == 0 && selectedDateWithYear != null && !TextUtils.isEmpty(selectedDateWithYear)) {
            Date date = null;
            try {
                date = new SimpleDateFormat(Constants.KEY_MONTH_DATE_FORMAT, appLocale ).parse(selectedDateWithYear);
                mPreviouslySelectedDate = date.getTime();
            } catch (ParseException e) {
               
                    e.printStackTrace();
            
                AppUtility.refreshAppToUpdateLocale(mActivity);
            }
        }

        //checkin date selected previously
        if(i.hasExtra(Constants.INTENT_EXTRA_UPDATED_START_DATE) ) {
            mSelectedCheckInDate = i.getStringExtra(Constants.INTENT_EXTRA_UPDATED_START_DATE);
        } else if(i.hasExtra(Constants.INTENT_EXTRA_RESETED_START_DATE)) {
            mSelectedCheckInDate = i.getStringExtra(Constants.INTENT_EXTRA_RESETED_START_DATE);
        } else if(i.hasExtra(Constants.KEY_DATE)) {
            mSelectedCheckInDate = i.getStringExtra(Constants.KEY_DATE);
        }

        if(mPreviouslySelectedDate == 0 && mSelectedCheckInDate != null && !TextUtils.isEmpty(mSelectedCheckInDate)) {
            Date date = null;
            try {
                date = new SimpleDateFormat(Constants.KEY_MONTH_DATE_FORMAT, appLocale ).parse(mSelectedCheckInDate);
                mPreviouslySelectedDate = date.getTime();
            } catch (ParseException e) {
                    e.printStackTrace();
                AppUtility.refreshAppToUpdateLocale(mActivity);
            }
        }

        //checkout date selected previously
        if(i.hasExtra(Constants.INTENT_EXTRA_UPDATED_END_DATE)) {
            mSelectedCheckOutDate = i.getStringExtra(Constants.INTENT_EXTRA_UPDATED_END_DATE);
        }

        if(i.hasExtra(Constants.KEY_DATE)) {
            mOtherDate = i.getStringExtra(Constants.KEY_DATE);
            if(mIntentType != null && mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_END_DATE)
                    && mOtherDate != null && !mOtherDate.equalsIgnoreCase(mActivity.getResources().getString(R.string.end_date))) {
                if(TextUtils.isEmpty(mOtherDate)) {
                    mOtherDate = i.getStringExtra(Constants.KEY_DATE);
                }
            }
        }

    }

    public void onAttachToFragment(Activity activity) {
        mActivity = activity;
    }

    public void onDetachFromFragment() {
        mActivity = null;
    }

    @Override
    public void onClick(View v) {
        if( v == mTodaysDateSelector) {
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yy", appLocale);
            if(mIntentType != null && mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_START_DATE)) {
                String formattedDate = df.format(todayDate.getTime());
                returnToFlightsHome(formattedDate, true);
            } else {
                String formattedDate = df.format(tomorrowDate.getTime());
                returnToFlightsHome(formattedDate, true);
            }
        }

        if( v == mTomorrowsDateSelector) {
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yy", appLocale);
            if(mIntentType != null && mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_START_DATE)) {
                String formattedDate = df.format(tomorrowDate.getTime());
                returnToFlightsHome(formattedDate, false);
            } else {
                String formattedDate = df.format(dayAftertomorrowDate.getTime());
                returnToFlightsHome(formattedDate, false);
            }
        }
    }

    @Override
    public void onDateSelected(Date date) {
        String displayDate;
//        if(LocaleHelper.getDefaultLanguage().equalsIgnoreCase("hi") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("ta") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("te") ||
//                LocaleHelper.getDefaultLanguage().equalsIgnoreCase("kn") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("pa") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("mr")
//                || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("gu") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("bn") ||
//                LocaleHelper.getDefaultLanguage().equalsIgnoreCase("or") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("ml")) {
//            displayDate = CJRAppUtility.convertDateToLocaleValue(date, inputFormat, outputFormat);
//        } else {
            displayDate = CalenderUtils.formatDate(mActivity, date.toString(), inputFormat, outputFormat);
//        }
        mSelectedDate.setText(displayDate);

        //if selected date is today's or tomorrrow's date then update UI
//        if(LocaleHelper.getDefaultLanguage().equalsIgnoreCase("hi") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("ta") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("te") ||
//                LocaleHelper.getDefaultLanguage().equalsIgnoreCase("kn") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("pa") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("mr")
//                || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("gu") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("bn") ||
//                LocaleHelper.getDefaultLanguage().equalsIgnoreCase("or") || LocaleHelper.getDefaultLanguage().equalsIgnoreCase("ml")) {
//            selectedDateWithYear = CJRAppUtility.convertDateToLocaleValue(date, inputFormat, "dd MMMM yy");
//        } else {
            selectedDateWithYear = CalenderUtils.formatDate(mActivity, date.toString(), inputFormat, "dd MMMM yy");
//        }
        returnToFlightsHome(selectedDateWithYear, false);
    }

    private void returnToFlightsHome(String date, boolean isFromTodayBtn) {
        if (mIntentType != null && mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_START_DATE)) {
            if(isFromTodayBtn) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        setCalenderUI();
                    }
                }, 500);            }
            //Compare if date is the last day of the month then you can't select any more checkout dates
            if (CalenderUtils.compareIfLastAvailableCheckInDate(mActivity, date, appLocale)) {
                return;
            } else {
                //if fresh date is selected in checkin then clear check out date
                if(mSelectedCheckOutDate != null) { mSelectedCheckOutDate = null; }
                mSelectedCheckInDate = date;
                mCheckInDateListener.onCheckInDateSelected(mSelectedCheckInDate, mSelectedCheckOutDate, mIntentType);
            }
        } else {
            final Intent mResultIntent = new Intent();

            if (mIntentType != null) {

                if (mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_START_DATE)) {
                    mResultIntent.putExtra(Constants.INTENT_EXTRA_SELECTED_START_DATE, date);
                } else {
                    //If user tries to select a checkout date before check in date is selected by entering inside via checkin and swipe to check out
                    if (mOtherDate != null && mOtherDate.equalsIgnoreCase("Check-out Date")) {
//                        CJRAppUtility.showAlert(mActivity, mActivity.getResources().getString(R.string.error), mActivity.getResources().getString(R.string.select_check_in_date_alert_msg));
                        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.error) + "\n" + mActivity.getResources().getString(R.string.select_check_in_date_alert_msg), Toast.LENGTH_SHORT).show();
                        setCalenderUI();
                        return;
                    }
                    //check if check in date is not less than check out date. Also handled condition for swipe from check-in
                    if (compareTwoDates(date, mOtherDate, mActivity.getResources().getString(R.string.end_date))) {
                        setCalenderUI();
                        return;
                    }

                    if (mSelectedCheckInDate != null) {
                        mResultIntent.putExtra(Constants.INTENT_EXTRA_SELECTED_START_DATE, mSelectedCheckInDate);
                    }
                    mResultIntent.putExtra(Constants.INTENT_EXTRA_SELECTED_END_DATE, date);
                }
            }

            if(mSelectedCheckInDate != null) {
                updateCalenderUI(date);
            }

            mCheckOutDateListener.onCheckOutDateSelected(mSelectedCheckInDate, mSelectedCheckOutDate);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    launchHomePage(mResultIntent);
                }
            }, 300);
        }
    }

    private void launchHomePage(Intent mResultIntent) {
        if(mActivity != null) {
            mActivity.setResult(Activity.RESULT_OK, mResultIntent);
            mActivity.finish();
        }
    }

    private void showUnevenCheckOutDateSelectedAlert() {
        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.error) + "\n" + mActivity.getResources().getString(R.string.flight_return_date_before_depart_date), Toast.LENGTH_SHORT).show();
        //CJRAppUtility.showAlert(mActivity, mActivity.getResources().getString(R.string.error), mActivity.getResources().getString(R.string.flight_return_date_before_depart_date));
    }

    private void showUnevenCheckInDateSelectedAlert() {
        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.error) + "\n" + mActivity.getResources().getString(R.string.flight_depart_date_after_return_date), Toast.LENGTH_SHORT).show();
        //CJRAppUtility.showAlert(mActivity, mActivity.getResources().getString(R.string.error), mActivity.getResources().getString(R.string.flight_depart_date_after_return_date));
    }

    /**
     * Comparing checkin and check-out dates to verify if the order of selected dates is correct
     * @param inFormattedDate - date to be sent back to previous activity
     * @param inOtherDate - Date which is already selected previously
     * @param inDateType - whether the date selected is checkIn or check-out date
     * @return
     */
    private boolean compareTwoDates(String inFormattedDate, String inOtherDate, String inDateType) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.KEY_DISPLAY_DATE_FORMAT, appLocale);
            Date date1 = formatter.parse(inFormattedDate);
            Date date2 = null;

            //If user tries to select a checkout date by scolling through tabs
            if(isScrolledToCheckOut && mSelectedCheckInDate != null) {
                date2 = formatter.parse(mSelectedCheckInDate);
            } else {
                if(!inOtherDate.equalsIgnoreCase(mActivity.getResources().getString(R.string.end_date))) {
                    date2 = formatter.parse(inOtherDate);
                } else {
                    if(mSelectedCheckInDate != null) {
                        date2 = formatter.parse(mSelectedCheckInDate);
                    } else {
                        showUnevenCheckOutDateSelectedAlert();
                        return true;
                    }
                }
            }

            //In case of check-out date throw error if selected date is smaller than check-in date
            if(inDateType.equalsIgnoreCase(mActivity.getResources().getString(R.string.end_date)) ) {
                if (date1.before(date2)) {
                    showUnevenCheckOutDateSelectedAlert();
                    return true;
                }
            }
            else {
                //In case of check-in date throw error if selected date is greater than check-out date
                if (date1.compareTo(date2) > 0) {
                    showUnevenCheckInDateSelectedAlert();
                    return true;
                }
            }
        } catch (ParseException e){ e.printStackTrace();
        }
        return false;
    }

    int scrolly;
    private void updateCalenderUI(String date) {
        scrolly = calendar.getFirstVisiblePosition();

        mSelectedCheckOutDate = date;

        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        if(mSelectedCheckInDate != null) {
            Date selectedCheckInDate = CalenderUtils.returnDateValue(mActivity, mSelectedCheckInDate);
            Date selectedCheckOutDate = CalenderUtils.returnDateValue(mActivity, date);

            dates.add(selectedCheckInDate);
            dates.add(selectedCheckOutDate);

            if(mDatePriceMap == null  || mDatePriceMap.size() == 0) {
                if(mUseLoaderAdapter) {
                    calendar.setCustomDayView(new CalenderLoaderDayViewAdapter());
                } else {
                    calendar.setCustomDayView(new DefaultDayViewAdapter());
                }

            } else {
                calendar.setCustomDayView(new CalenderDayViewAdapter());

            }

            List<CalendarCellDecorator> decoratorList = new ArrayList<>();
            decoratorList.add(new DateCalendarCellDecorator(mActivity.getApplicationContext() , mDatePriceMap  , true , new Handler()));
            calendar.setDecorators(decoratorList);

            calendar.init(previousMonth.getTime(), nextYear.getTime()) //
                    .inMode(CalendarPickerView.SelectionMode.RANGE) //
                    .withSelectedDates(dates);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(scrolly != 0) {
                        calendar.smoothScrollToPosition(scrolly);
                    }
                }
            }, 3000);
        }
    }

    @Override
    public void onDateUnselected(Date date) {

    }

    public void setCheckInDateSelected(String checkInDate, String checkOutDate, String intentType) {
        if(checkInDate != null) {
            mSelectedCheckInDate = checkInDate;
            mSelectedCheckOutDate = checkOutDate;

            mOtherDate = checkInDate;
        }
        mIntentType = intentType;

        //date to be selected in UI at the beginning
        setCalenderUI();
        setFooterVisibility(mSelectedCheckInDate);
    }

    public void setCheckOutDateSelected(String checkInDate, String checkOutDate, String intentType) {
        mSelectedCheckInDate = checkInDate;
        mSelectedCheckOutDate = checkOutDate;


        mIntentType = intentType;
        mOtherDate = checkOutDate;

        setCalenderUI();
        setFooterVisibility(mSelectedCheckInDate);

    }

    @Override
    public void update(Observable observable, Object data) {

        priceInfo = CalendarDataTransformer.getObserver().getCalendarPriceModel();

        Date selectedCheckInDate = CalenderUtils.returnDateValue(mActivity, mSelectedCheckInDate);

        setfareMap(selectedCheckInDate , true);

        setCalenderUI();

    }

    //callback to be triggered on selecting a checkIn button
    public interface IJRCheckInDateSelector {
        public void onCheckInDateSelected(String inCheckInDate, String inCheckOutDate, String intentType);
    }

    //callback to be triggered on selecting a checkOutbutton
    public interface IJRCheckOutDateSelector {
        public void onCheckOutDateSelected(String inCheckInDate, String inCheckOutDate);
    }

    public void setIntentType(String inIntentType, boolean inIsScrolledToCheckOut) {
        mIntentType = inIntentType;
        isScrolledToCheckOut = inIsScrolledToCheckOut;

        if(mIntentType != null && mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_END_DATE)) {
            setFooterVisibility(mSelectedCheckInDate);
        } else {
            setFooterVisibility(null);
        }
    }


    private void setFooterVisibility(String mCheckInDatePreviouslySelected) {
        //based on intent value show or hide footer lyt for checkin and check out tabs
        if(mIntentType != null && mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_END_DATE)) {
            if(mTodayDate != null && mTommorrowDate != null) {
                mTodayLabel.setText(mActivity.getString(R.string.hotel_for_one_night));
                mTomorrowLabel.setText(mActivity.getResources().getString(R.string.hotel_for_two_nights));

                setCheckInFooterViews("CheckOUT", mCheckInDatePreviouslySelected);
            } else {
                mFooterLayout.setVisibility(View.GONE);
            }

        } else {
            mFooterLayout.setVisibility(View.GONE);
            mTodayLabel.setText(mActivity.getResources().getString(R.string.bus_today_btn));
            mTomorrowLabel.setText(mActivity.getResources().getString(R.string.bus_tomorrow_btn));

            setCheckInFooterViews("CheckIN", null);
        }
    }

    private void setFooterLyt(View view) {
        mTodaysDateSelector = (LinearLayout) view.findViewById(R.id.todays_date_selector);
        mTodaysDateSelector.setOnClickListener(this);

        mTomorrowsDateSelector = (LinearLayout) view.findViewById(R.id.tomorrows_date_selector);
        mTomorrowsDateSelector.setOnClickListener(this);


        if(mTodayDate == null && mTommorrowDate == null) {
            mTodayDate = (TextView) view.findViewById(R.id.todays_date);
            mTommorrowDate = (TextView) view.findViewById(R.id.tomorrows_date);

            mTodayLabel = (TextView) view.findViewById(R.id.label_today);
            mTomorrowLabel = (TextView) view.findViewById(R.id.label_tommorrow);

            int unit = AppUtility.getScreenGridUnitBy32(mActivity);
            mTodayDate.setPadding(unit, 0, unit, unit);
            mTommorrowDate.setPadding(unit, 0, unit, unit);

            //view.findViewById(R.id.sep).setPadding(0, 0, 0, unit + unit);
            view.findViewById(R.id.label_today).setPadding(unit, unit, unit, 0);
            view.findViewById(R.id.label_tommorrow).setPadding(unit, unit, unit, 0);
        }

        if(mIntentType != null && mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_END_DATE)) {
            setCheckInFooterViews("CheckOUT", mOtherDate);
        } else {
            setCheckInFooterViews("CheckIN", null);
        }

        setFooterVisibility(mOtherDate);
    }

    /**
     * Set Different footer dates for checkin and checkout
     * @param isFrom
     */
    private void setCheckInFooterViews(String isFrom, String mCheckInDatePreviouslySelected) {
        try {
            Calendar mTodayCalender = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", appLocale);

            //set seperate one and 2 night dates based on check in date
            if(mCheckInDatePreviouslySelected != null && !mCheckInDatePreviouslySelected.equalsIgnoreCase("Check-in Date")
                    ) {
                Calendar mOneNightCalender = Calendar.getInstance();
                mOneNightCalender.setTime(df.parse(mCheckInDatePreviouslySelected));
                mOneNightCalender.add(Calendar.DATE, 1);
                tomorrowDate = mOneNightCalender;
                String mOneNightDate = df.format(mOneNightCalender.getTime());

                Calendar mTwoNightCalender = Calendar.getInstance();
                mTwoNightCalender.setTime(df.parse(mCheckInDatePreviouslySelected));
                mTwoNightCalender.add(Calendar.DATE, 2);
                dayAftertomorrowDate = mTwoNightCalender;
                String mTwoNightDate = df.format(mTwoNightCalender.getTime());


                mTodayDate.setText(AppUtility.calenderFormatDate(mActivity, mOneNightDate, Constants.KEY_DISPLAY_DATE_FORMAT, Constants.KEY_DISPLAY_DATE_MONTH));
                mTommorrowDate.setText(AppUtility.calenderFormatDate(mActivity, mTwoNightDate, Constants.KEY_DISPLAY_DATE_FORMAT, Constants.KEY_DISPLAY_DATE_MONTH));

            }
            // else normally it is tomorrow and day after tomorrow dates
            else {
                todayDate = mTodayCalender;

                Calendar mTomoCalender = Calendar.getInstance();
                mTomoCalender.add(Calendar.DATE, 1);
                tomorrowDate = mTomoCalender;
                String mTomDate = df.format(mTomoCalender.getTime());

                if (isFrom != null && isFrom.equalsIgnoreCase("CheckIN")) {
                    mTodayDate.setText(AppUtility.calenderFormatDate(mActivity, df.format(mTodayCalender.getTime()), Constants.KEY_DISPLAY_DATE_FORMAT, Constants.KEY_DISPLAY_DATE_MONTH));
                    mTommorrowDate.setText(AppUtility.calenderFormatDate(mActivity, mTomDate, Constants.KEY_DISPLAY_DATE_FORMAT, Constants.KEY_DISPLAY_DATE_MONTH));
                }

                Calendar mDayAfterTomoCalender = Calendar.getInstance();
                mDayAfterTomoCalender.add(Calendar.DATE, 2);
                dayAftertomorrowDate = mDayAfterTomoCalender;
                String mDayAfterTomDate = df.format(mDayAfterTomoCalender.getTime());

                if (isFrom != null && isFrom.equalsIgnoreCase("CheckOUT")) {
                    mTodayDate.setText(AppUtility.calenderFormatDate(mActivity, mTomDate, Constants.KEY_DISPLAY_DATE_FORMAT, Constants.KEY_DISPLAY_DATE_MONTH));
                    mTommorrowDate.setText(AppUtility.calenderFormatDate(mActivity, mDayAfterTomDate, Constants.KEY_DISPLAY_DATE_FORMAT, Constants.KEY_DISPLAY_DATE_MONTH));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setfareMap(Date date , boolean shouldStopLoader ) {

        updatePriceMapBasedOnType();

        if(mDatePriceMap != null && mDatePriceMap.size() > 0 ) {
//            calendar.getDecorators().clear();

            calendar.setCustomDayView(new CalenderDayViewAdapter());

        } else {
            calendar.setCustomDayView(new DefaultDayViewAdapter());
        }
        List<CalendarCellDecorator> decoratorList = new ArrayList<>();
        decoratorList.add(new DateCalendarCellDecorator(mActivity , mDatePriceMap  , shouldStopLoader , new Handler()));
        calendar.setDecorators(decoratorList);
        calendar.selectDate(date);

    }

    private void updatePriceMapBasedOnType(){
        if(priceInfo == null) {
            return;
        }
        mDatePriceMap = new HashMap();
        if(fragmentPos == 0)
        {
            if(priceInfo.getOnwardDatePriceInfo() != null)
                for (int i = 0; i < priceInfo.getOnwardDatePriceInfo().size(); i++) {
                    mDatePriceMap.put(priceInfo.getOnwardDatePriceInfo().get(i).getDate(), priceInfo.getOnwardDatePriceInfo().get(i));
                }
        }else{
            if(priceInfo.getReturnDatePriceInfo() != null)
                for (int i = 0; i < priceInfo.getReturnDatePriceInfo().size(); i++) {
                    mDatePriceMap.put(priceInfo.getReturnDatePriceInfo().get(i).getDate(), priceInfo.getReturnDatePriceInfo().get(i));
                }
        }
    }
}
