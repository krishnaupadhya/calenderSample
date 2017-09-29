package com.android.calender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by Krishna Upadhya on 9/28/2017.
 */

public class LocaleHelper {
    public static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    public static final String SELECTED_LANGUAGE_ID = "Locale.Helper.Selected.Language.Id";

    public static void onCreate(Context context) {
        String lang = getSavedLanguage(context, getDefaultLanguage());
        int langId = getSavedLanguageId(context, getDefaultLanguageId());
        setLocale(context, lang);
        saveLanguageId(context, langId);
    }

    public static void onCreate(Context context, String defaultLanguage) {
        String lang = getSavedLanguage(context, defaultLanguage);
        int langId = getSavedLanguageId(context, getDefaultLanguageId());
        setLocale(context, lang);
        saveLanguageId(context, langId);

    }

    public static String getLanguage(Context context) {
        return getSavedLanguage(context, getDefaultLanguage());
    }

    public static String getDefaultLanguage() {
        String language = Locale.getDefault().getLanguage();
//        if (language.equalsIgnoreCase(CJRConstants.ENGLISH_CODE) || language.equalsIgnoreCase(CJRConstants.HINDI_CODE) || language.equalsIgnoreCase(CJRConstants.TAMIL_CODE) ||
//                language.equalsIgnoreCase(CJRConstants.TELUGU_CODE) || language.equalsIgnoreCase(CJRConstants.KANNADA_CODE) || language.equalsIgnoreCase(CJRConstants.PUNJABI_CODE) ||
//                language.equalsIgnoreCase(CJRConstants.MARATHI_CODE) || language.equalsIgnoreCase(CJRConstants.GUJRATI_CODE)
//                || language.equalsIgnoreCase(CJRConstants.BANGALI_CODE) || language.equalsIgnoreCase(CJRConstants.MALYALAM_CODE) || language.equalsIgnoreCase(CJRConstants.ORIYA_CODE)) {
//            return language;
//        } else {
            return "en";
//        }
    }

    /**
     * Method to get the language ID stored in {@link Constants} for the current default language
     * @return int
     */
    public static int getDefaultLanguageId() {
        String language = Locale.getDefault().getLanguage();
        if (language.equalsIgnoreCase(Constants.ENGLISH_CODE)) {
            return Constants.ENGLISH_CODE_ID;
//        } else if(language.equalsIgnoreCase(CJRConstants.HINDI_CODE)){
//            return CJRConstants.HINDI_CODE_ID;
//        } else if(language.equalsIgnoreCase(CJRConstants.TAMIL_CODE)) {
//            return CJRConstants.TAMIL_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.TELUGU_CODE)) {
//            return CJRConstants.TELUGU_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.KANNADA_CODE)) {
//            return CJRConstants.KANNADA_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.PUNJABI_CODE)) {
//            return CJRConstants.PUNJABI_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.MARATHI_CODE)) {
//            return CJRConstants.MARATHI_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.GUJRATI_CODE)) {
//            return CJRConstants.GUJRATI_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.BANGALI_CODE)) {
//            return CJRConstants.BANGALI_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.MALYALAM_CODE)) {
//            return CJRConstants.MALYALAM_CODE_ID;
//        }else if(language.equalsIgnoreCase(CJRConstants.ORIYA_CODE)) {
//            return CJRConstants.ORIYA_CODE_ID;
        } else {
            return 1;
        }
    }

    public static String getSavedLanguage(Context context, String defaultLanguage) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
        } catch (Exception e) {
            return defaultLanguage;
        }
    }

    /**
     * Method to get the language id of the saved language from shared preferences
     * @param context {@link Context}
     * @param defaultLanguageId
     * @return Id
     */
    public static int getSavedLanguageId(Context context, int defaultLanguageId) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getInt(SELECTED_LANGUAGE_ID, defaultLanguageId);
        } catch (Exception e) {
            return defaultLanguageId;
        }
    }

    public static void setLocale(Context context, String language) {
        saveLanguage(context, language);
        updateResources(context, language);
    }

    public static void saveLanguage(Context context, String language) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SELECTED_LANGUAGE, language);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to save the selected language ID to shared prefernces
     * @param context {@link Context}
     * @param languageId int
     */
    public static void saveLanguageId(Context context, int languageId) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(SELECTED_LANGUAGE_ID, languageId);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateResources(Context context, String language) {
        try {
            Locale locale;
            if(!TextUtils.isEmpty(language) && language.equals("en")){
                locale = new Locale("en_US");
            }else{
                locale = new Locale(language);
            }
            Locale.setDefault(locale);
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
