package com.android.calender;


import java.util.Observable;

/**
 * Created by sunil on 1/3/17.
 */

public class CJRFareCalendarObserver extends Observable {
    private CJRFlightCalendarPriceModel calendarPriceModel;

    public CJRFlightCalendarPriceModel getCalendarPriceModel() {
        return calendarPriceModel;
    }

    public void setCalendarPriceModel(CJRFlightCalendarPriceModel calendarPriceModel) {
        this.calendarPriceModel = calendarPriceModel;
        setChanged();
        notifyObservers();
    }
}
