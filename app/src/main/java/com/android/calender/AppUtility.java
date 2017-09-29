package com.android.calender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Krishna Upadhya on 9/28/2017.
 */

public class AppUtility {


    public static String calenderFormatDate(Activity mContext, String inputDate, String inputFormat, String outputFormat) {
        try {
            Locale appLocale = new Locale(LocaleHelper.getDefaultLanguage());
            DateFormat originalFormat = new SimpleDateFormat(inputFormat, appLocale);
            DateFormat targetFormat = new SimpleDateFormat(outputFormat);
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
    public static void refreshAppToUpdateLocale(Activity context) {
        if (context == null) return;
        String language = LocaleHelper.getLanguage(context.getApplicationContext());
        LocaleHelper.setLocale(context.getApplicationContext(), language);
        Intent intent = new Intent();
        intent.setClassName(context.getApplicationContext().getPackageName(), MainActivity.class.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.CLEAR_MOBILE_DATA, true);
        intent.putExtra(Constants.EXTRA_INTENT_RESULTANT_FRAGMENT_TYPE, Constants.URL_TYPE_FEATURED);
        intent.putExtra(Constants.EXTRA_INTENT_RESULTANT_FRAGMENT_POSITION, 0);
        intent.putExtra(Constants.EXTRA_INTENT_RESULTANT_FRAGMENT_TYPE, Constants.URL_TYPE_MAIN);
        context.startActivity(intent);
        context.finish();
    }

    public static String formatDate(Context mContext, String inputDate, String inputFormat, String outputFormat) {
        try {
            Locale appLocale = new Locale(LocaleHelper.getDefaultLanguage());
            DateFormat originalFormat = new SimpleDateFormat(inputFormat, appLocale);
            DateFormat targetFormat = new SimpleDateFormat(outputFormat);
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
    public static int getScreenGridUnitBy32(Context context) {
        return getScreenWidth((Activity) context) / 32;
    }
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int width;
        if (activity != null && !activity.isFinishing()) {
            activity.getWindowManager().getDefaultDisplay()
                    .getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
        } else {
            width = 0;
        }
        return width;
    }
}
