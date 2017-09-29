package com.android.calender;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Akanksha on 19-Jul-16.
 */
public class CalenderUtils {

    public static String formatDate(Activity mContext, String inputDate, String inputFormat, String outputFormat) {
        try {
            Locale appLocale = Locale.ENGLISH;
            DateFormat originalFormat = new SimpleDateFormat(inputFormat, appLocale);
            DateFormat targetFormat = new SimpleDateFormat(outputFormat, appLocale);
            Date dateObject = originalFormat.parse(inputDate);
            String formattedDate = targetFormat.format(dateObject);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Get a diff between two dates
     * @param dateToday today's date
     * @param dateSelected the newest date
     * @param timeUnit the unit in which you want the diff
     * @param appLocale
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date dateToday, Date dateSelected, TimeUnit timeUnit, Locale appLocale) {
        long diffInMillies = 0;
        diffInMillies = dateSelected.getTime() - dateToday.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static void handleAdjacentDateSelection(RelativeLayout mLytToday, RelativeLayout mLytTomorrow,
                                                   TextView mTxtToday, TextView mTxtTomorrow, boolean today, boolean tomo) {
        //layout changes
        mLytTomorrow.setSelected(tomo);
        mTxtTomorrow.setSelected(tomo);
        mLytToday.setSelected(today);
        mTxtToday.setSelected(today);
    }

    public static Date returnDateValue(Activity context, String date, Locale appLocale) {
        if(date != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yy", appLocale);
            try {
                Date dateValue = format.parse(date);
                return dateValue;
            } catch (ParseException e) {
                //if(CJRAppCommonUtility.isDebug) {
                    e.printStackTrace();
//                }
//                CJRAppUtility.refreshAppToUpdateLocale(context);
            }
        }
        return null;
    }
}
