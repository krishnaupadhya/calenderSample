package com.android.calender;

import android.app.Activity;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CJRHotelCalenderUtils {
    /**
     * Compare if date is the last day of the month then you can't select any more checkout dates
     *
     * @param inFormattedDate
     * @return
     */
    public static boolean compareIfLastAvailableCheckInDate(Activity mActivity, String inFormattedDate, Locale appLocale) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yy", appLocale);
            Date date = Calendar.getInstance().getTime();
            String mTodayDate = sdf.format(date);

            Date date1 = sdf.parse(inFormattedDate);
            Calendar calender = Calendar.getInstance();
            calender.setTime(sdf.parse(mTodayDate));
            //calender.add(Calendar.DATE, -1);
            calender.add(Calendar.YEAR, 1);
            String mOneNightDate = sdf.format(calender.getTime());
            Date date2 = sdf.parse(mOneNightDate);

            if (date1.compareTo(date2) == 0) {
                //CJRAppUtility.showAlert(mActivity, mActivity.getResources().getString(R.string.error), mActivity.getResources().getString(R.string.select_other_check_in_date));
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.error) + "\n" + mActivity.getResources().getString(R.string.select_other_check_in_date), Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (ParseException ex) {
                ex.printStackTrace();

        } catch (Exception e) {
           e.printStackTrace();
        }
        return false;
    }

    public static Date returnDateValue(Activity activity, String date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yy");
            try {
                Date dateValue = format.parse(date);
                return dateValue;
            } catch (ParseException e) {

                e.printStackTrace();
            }
        }
        return null;
    }
}
