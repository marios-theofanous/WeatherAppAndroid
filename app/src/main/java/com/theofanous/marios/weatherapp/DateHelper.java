package com.theofanous.marios.weatherapp;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Marios on 22/12/2016.
 */

public class DateHelper {
    static String getNameOfDayFromUnix(long unixTime){

        Calendar weatherTime = Calendar.getInstance();
        weatherTime.setTimeInMillis(unixTime*1000);
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.DAY_OF_WEEK)==weatherTime.get(Calendar.DAY_OF_WEEK))
            return "Today";
        else if (now.get(Calendar.DAY_OF_WEEK)-weatherTime.get(Calendar.DAY_OF_WEEK)==1)
            return "Yesterday";
        else if (now.get(Calendar.DAY_OF_WEEK)-weatherTime.get(Calendar.DAY_OF_WEEK)==-1)
            return "Tomorrow";
        else
            return weatherTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);


    }
}
