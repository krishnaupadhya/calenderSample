package com.android.calender;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Akanksha on 11-Aug-16.
 */
public class TwoWayCalendarActivity extends AppCompatActivity implements DateCalendarHelper.IJRCheckInDateSelector, DateCalendarHelper.IJRCheckOutDateSelector {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DateCalenderAdapter mCalenderdapter;
    ArrayList<String> tabTitles = new ArrayList<String>();
    private String mCheckInDateWithYear, mCheckOutDateWithYear, mFrom, mIntentType;
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_way_calender);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar();
        getIntentData();
        initializeViews();
    }


    private void initToolBar() {
        setSupportActionBar(mToolBar);
        setTitle(getResources().getString(R.string.select_dates));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getIntentData() {
        //checkin date selected previously
        Intent i = getIntent();

        //       checkin date selected previously
        if (i.hasExtra(Constants.INTENT_EXTRA_UPDATED_START_DATE)) {
            mCheckInDateWithYear = i.getStringExtra(Constants.INTENT_EXTRA_UPDATED_START_DATE);
        } else if (i.hasExtra(Constants.INTENT_EXTRA_RESETED_START_DATE)) {
            mCheckInDateWithYear = i.getStringExtra(Constants.INTENT_EXTRA_RESETED_START_DATE);
        } else if (i.hasExtra(Constants.KEY_DATE)) {
            mCheckInDateWithYear = i.getStringExtra(Constants.KEY_DATE);
        }

        //checkout date selected previously
        if (i.hasExtra(Constants.INTENT_EXTRA_UPDATED_END_DATE)) {
            mCheckOutDateWithYear = i.getStringExtra(Constants.INTENT_EXTRA_UPDATED_END_DATE);
        }

        if (getIntent().hasExtra(Constants.INTENT_EXTRA_SELECTED_INTENT_TYPE)) {
            mFrom = getIntent().getStringExtra(Constants.INTENT_EXTRA_SELECTED_INTENT_TYPE);
        }
    }

    private void initializeViews() {
        tabLayout = (TabLayout) findViewById(R.id.movie_details_tabs);
        viewPager = (ViewPager) findViewById(R.id.calender_view_pager);

        setHeaderTitleArray();

        setupViewPager(viewPager);
    }

    private void setHeaderTitleArray() {
        tabTitles = new ArrayList<>();
        if (mCheckInDateWithYear != null) {
            tabTitles.add(AppUtility.calenderFormatDate(TwoWayCalendarActivity.this, mCheckInDateWithYear, "dd MMMM yy"/*CJRConstants.KEY_CALENDER_DATE_FORMAT*/, "EEE, dd MMM"));
        } else {
            tabTitles.add(getResources().getString(R.string.start_date));
        }
        if (mCheckOutDateWithYear != null) {
            tabTitles.add(AppUtility.calenderFormatDate(TwoWayCalendarActivity.this, mCheckOutDateWithYear, "dd MMMM yy", "EEE, dd MMM"));
        } else {
            tabTitles.add(getResources().getString(R.string.end_date));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * method used to call the viewpager adapter and set the view pager
     *
     * @param viewPager recieved view pager object
     */
    private void setupViewPager(ViewPager viewPager) {
        try {
            mCalenderdapter = new DateCalenderAdapter(getApplicationContext(), getSupportFragmentManager(),
                    tabTitles);
            viewPager.setAdapter(mCalenderdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setHeaderTitleArray();

                    updateTabLayout(position);

                    if (position == 0 && mCalenderdapter.getFragment(position) != null) {
                        DateCalendarFragment mCalenderFragment = (DateCalendarFragment) mCalenderdapter
                                .getFragment(position);

                        if (mCheckInDateWithYear == null) {
                            mCalenderFragment.loadCalender(Constants.INTENT_EXTRA_SELECTED_START_DATE, false);
                        } else {
                            mCalenderFragment.loadCalenderWithCheckOutData(mCheckInDateWithYear, mCheckOutDateWithYear);
                        }
                    } else if (position == 1 && mCalenderdapter.getFragment(position) != null) {
                        DateCalendarFragment mCalenderFragment = (DateCalendarFragment) mCalenderdapter
                                .getFragment(position);

                        if (mCheckInDateWithYear == null) {
                            mCalenderFragment.loadCalender(Constants.INTENT_EXTRA_SELECTED_END_DATE, true);
                        } else {
                            mCalenderFragment.loadCalenderWithCheckInData(mCheckInDateWithYear, mCheckOutDateWithYear);
                        }
                    } else if (mCalenderdapter.getFragment(position) == null && mIntentType != null &&
                            mIntentType.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_START_DATE)) {
                        AppUtility.refreshAppToUpdateLocale(TwoWayCalendarActivity.this);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            tabLayout.setupWithViewPager(viewPager);
            constructTabLayoutView(0);

            // set the viewpager position based on check-in/check-out data sent from intent
            if (mFrom.equalsIgnoreCase(Constants.INTENT_EXTRA_SELECTED_START_DATE)) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        } catch (Exception e) {
        }
    }

    /**
     * method used to update the tablayout
     *
     * @param position recieved position to update the tab
     */
    private void updateTabLayout(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View Tabview = tab.getCustomView();
            TextView tvDate = (TextView) Tabview.findViewById(R.id.tabTextdate);
            TextView tvDay = (TextView) Tabview.findViewById(R.id.tabTextday);

            if (i == 0) {
                if (mCheckInDateWithYear != null) {
                    tvDate.setVisibility(View.VISIBLE);
                    tvDate.setText(getResources().getString(R.string.start_date));
                    tvDay.setText(AppUtility.calenderFormatDate(TwoWayCalendarActivity.this, mCheckInDateWithYear, "dd MMMM yy", "EEE, dd MMM"));
                }
            } else {
                if (mCheckOutDateWithYear != null) {
                    tvDate.setVisibility(View.VISIBLE);
                    tvDate.setText(getResources().getString(R.string.end_date));
                    tvDay.setText(AppUtility.calenderFormatDate(TwoWayCalendarActivity.this, mCheckOutDateWithYear, "dd MMMM yy", "EEE, dd MMM"));
                } else {
                    tvDate.setVisibility(View.GONE);
                    tvDay.setText(getResources().getString(R.string.end_date));
                }
            }

            if (i == position) {
                tvDate.setTextColor(ContextCompat.getColor(this, R.color.color_tab_selected));
                tvDay.setTextColor(ContextCompat.getColor(this, R.color.color_tab_selected));
            } else {
                tvDate.setTextColor(ContextCompat.getColor(this, R.color.color_error));
                tvDay.setTextColor(ContextCompat.getColor(this, R.color.coloe_display_name));
            }
        }
    }

    /**
     * method used to construct tab layout based on the postion of the tab,
     * setTabView method will construct the cutsom tab view
     *
     * @param position recieved postion of thetab
     */
    private void constructTabLayoutView(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View v;
            if (i == position) {
                v = setTabView(i, true);
                v.setSelected(true);
            } else {
                v = setTabView(i, false);
                v.setSelected(false);
            }
            tab.setCustomView(v);
        }
    }

    /**
     * method used to set the tabs
     *
     * @param position recieved int which is position of the tab
     * @param selected recieved boolean ,selected tab
     * @return returns the view
     */
    public View setTabView(int position, boolean selected) {
        View v = LayoutInflater.from(this).inflate(R.layout.date_calendar_tab_view /*movie_tab_view*/, null);
        TextView tvDate = (TextView) v.findViewById(R.id.tabTextdate);
        TextView tvDay = (TextView) v.findViewById(R.id.tabTextday);
        String mSelectedDate = tabTitles.get(position);
        tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        if (position == 0) {
            if (mSelectedDate.equalsIgnoreCase(getResources().getString(R.string.start_date))) {
                tvDate.setVisibility(View.GONE);
            } else {
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText(getResources().getString(R.string.start_date));
            }
            tvDay.setText(mSelectedDate);
        } else {
            if (mSelectedDate.equalsIgnoreCase(getResources().getString(R.string.end_date))) {
                tvDate.setVisibility(View.GONE);
            } else {
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText(getResources().getString(R.string.end_date));
            }
            tvDay.setText(mSelectedDate);
        }
        if (selected) {
            tvDate.setTextColor(ContextCompat.getColor(this, R.color.color_tab_selected));
            tvDay.setTextColor(ContextCompat.getColor(this, R.color.color_tab_selected));
        } else {
            tvDate.setTextColor(ContextCompat.getColor(this, R.color.frm_andto_txt));
            tvDay.setTextColor(ContextCompat.getColor(this, R.color.week_header_color));
        }

        return v;
    }


    @Override
    public void onCheckInDateSelected(String inCheckInDate, String inCheckOutDate, String inIntentType) {
        mCheckInDateWithYear = inCheckInDate;
        mCheckOutDateWithYear = inCheckOutDate;
        mIntentType = inIntentType;
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onCheckOutDateSelected(String inCheckInDate, String inCheckOutDate) {
        mCheckInDateWithYear = inCheckInDate;
        mCheckOutDateWithYear = inCheckOutDate;

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View Tabview = tab.getCustomView();
            TextView tvDate = (TextView) Tabview.findViewById(R.id.tabTextdate);
            TextView tvDay = (TextView) Tabview.findViewById(R.id.tabTextday);

            if (i == 0) {
                if (mCheckInDateWithYear != null) {
                    tvDate.setVisibility(View.VISIBLE);
                    tvDate.setText(getResources().getString(R.string.start_date));
                    tvDay.setText(AppUtility.calenderFormatDate(TwoWayCalendarActivity.this, mCheckInDateWithYear, "dd MMMM yy", "EEE, dd MMM"));
                }
            } else {
                if (mCheckOutDateWithYear != null) {
                    tvDate.setVisibility(View.VISIBLE);
                    tvDate.setText(getResources().getString(R.string.end_date));
                    tvDay.setText(AppUtility.calenderFormatDate(TwoWayCalendarActivity.this, mCheckOutDateWithYear, "dd MMMM yy", "EEE, dd MMM"));
                }
            }
        }
    }
}
