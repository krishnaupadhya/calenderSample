package com.android.calender;

/**
 * Created by prashant on 13/09/16.
 */
public class CJRFlightDatePriceInfo implements IJRDataModel {

    private String date;

    private int fare;

    private String colorCode;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
