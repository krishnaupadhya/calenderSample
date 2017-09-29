package com.android.calender;

public class CalendarDataTransformer
{
    private static FareCalendarObserver observer;

    private CalendarDataTransformer(){}

    public static FareCalendarObserver getObserver()
    {
        if(observer == null)
        {
            synchronized(FareCalendarObserver.class) {
                if(observer == null) {
                    observer = new FareCalendarObserver();
                }
            }

        }
        return observer;
    }
}
