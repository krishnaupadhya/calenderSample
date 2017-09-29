package com.android.calender;


import java.util.Observable;


public class FareCalendarObserver extends Observable {
    private CalendarPriceModel calendarPriceModel;

    public CalendarPriceModel getCalendarPriceModel() {
        return calendarPriceModel;
    }

    public void setCalendarPriceModel(CalendarPriceModel calendarPriceModel) {
        this.calendarPriceModel = calendarPriceModel;
        setChanged();
        notifyObservers();
    }
}
