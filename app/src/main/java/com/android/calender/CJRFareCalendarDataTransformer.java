package com.android.calender;

/**
 * Created by sunil on 1/3/17.
 */

public class CJRFareCalendarDataTransformer
{
    private static CJRFareCalendarObserver observer;

    private CJRFareCalendarDataTransformer(){}

    public static CJRFareCalendarObserver getObserver()
    {
        if(observer == null)
        {
            synchronized(CJRFareCalendarObserver.class) {
                if(observer == null) {
                    observer = new CJRFareCalendarObserver();
                }
            }

        }
        return observer;
    }
}
