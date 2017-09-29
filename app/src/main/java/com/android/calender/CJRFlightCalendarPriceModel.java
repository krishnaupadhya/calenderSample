package com.android.calender;

import android.text.TextUtils;


import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by prashant on 13/09/16.
 */
public class CJRFlightCalendarPriceModel implements IJRDataModel {

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private int code;

    @SerializedName("body")
    private String body;

    @SerializedName("extra")
    private String extra;

    private boolean isForHorizontalList;


    private ArrayList<CJRFlightDatePriceInfo> mDatePriceInfoOnward;
    private ArrayList<CJRFlightDatePriceInfo> mDatePriceInfoReturn;

    public void parse(String jsonString ) {
        if(TextUtils.isEmpty(jsonString) ) {
            return;
        }

        mDatePriceInfoOnward = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(jsonString);
            message = object.getString("message");
            code = object.getInt("code");
            extra = object.getString("extra");

            JSONObject dataObject = object.getJSONObject("body");
            JSONObject faresObject = dataObject.getJSONObject("fares");
            Iterator<String> keys = null;//faresObject.keys();

            if(faresObject.has("onward") && faresObject.has("return"))
            {
                JSONObject onwardObject = faresObject.getJSONObject("onward");
                JSONObject returnObject = faresObject.getJSONObject("return");

                mDatePriceInfoOnward = new ArrayList<>();
                mDatePriceInfoReturn = new ArrayList<>();
                keys = onwardObject.keys();
                fillTheArray(onwardObject,keys,false);
                keys = returnObject.keys();
                fillTheArray(returnObject,keys,true);
            }else {
                mDatePriceInfoOnward = new ArrayList<>();
                keys = faresObject.keys();
                fillTheArray(faresObject,keys,false);
            }

        } catch(Exception e ) {
            e.printStackTrace();
        }
    }

    private void fillTheArray(JSONObject jsonObj, Iterator<String> keys, boolean isRoundTrip) {
        while (keys.hasNext()) {
            String key = keys.next();
            CJRFlightDatePriceInfo priceInfo = new CJRFlightDatePriceInfo();
            priceInfo.setDate(key);
            if(jsonObj.isNull(key)) {
                continue;
            }
            try {
                JSONObject jsonObject = jsonObj.getJSONObject(key);
                priceInfo.setFare(jsonObject.getInt("fare"));
                priceInfo.setColorCode(jsonObject.getString("color"));
            } catch (Exception e) {
                e.printStackTrace();
                priceInfo.setFare(-1);
                priceInfo.setColorCode("");
            }

            if(isRoundTrip) {
                mDatePriceInfoReturn.add(priceInfo);
//                Collections.sort(mDatePriceInfoReturn, new CJRFlightDatePriceInfoCompartor());

            }else{
                mDatePriceInfoOnward.add(priceInfo);
//                Collections.sort(mDatePriceInfoOnward, new CJRFlightDatePriceInfoCompartor());

            }
        }
        if(isRoundTrip) {
            Collections.sort(mDatePriceInfoReturn, new CJRFlightDatePriceInfoCompartor());
        } else {
            Collections.sort(mDatePriceInfoOnward, new CJRFlightDatePriceInfoCompartor());
        }
    }


    public ArrayList<CJRFlightDatePriceInfo> getOnwardDatePriceInfo() {
        return mDatePriceInfoOnward;
    }

    public ArrayList<CJRFlightDatePriceInfo> getReturnDatePriceInfo() {
        return mDatePriceInfoReturn;
    }

    public boolean isForHorizontalList() {
        return isForHorizontalList;
    }

    public void setForHorizontalList(boolean forHorizontalList) {
        isForHorizontalList = forHorizontalList;
    }

    private class CJRFlightDatePriceInfoCompartor implements java.util.Comparator< CJRFlightDatePriceInfo> {

        @Override
        public int compare(CJRFlightDatePriceInfo lhs, CJRFlightDatePriceInfo rhs) {
            try {
                if (lhs != null && rhs != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = simpleDateFormat.parse(lhs.getDate());
                    Date date1 = simpleDateFormat.parse(rhs.getDate());
                    if(date.compareTo(date1) < 0) {
                        return -1;
                    } else if (date.compareTo(date1) > 0 ) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } catch (Exception e ) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
