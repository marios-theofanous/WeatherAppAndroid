package com.theofanous.marios.weatherapp;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Marios on 22/12/2016.
 */

public class HelperMethods {
    public static final String PREFERENCES_TEMP_KEY = "PREFERENCES_TEMP_KEY";

    static boolean olderThanADay(long time){
        Calendar weatherTime = Calendar.getInstance();
        weatherTime.setTimeInMillis(time*1000);
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.DATE)-weatherTime.get(Calendar.DATE)>=1)
            return true;
        return false;
    }

    static String getNameOfDayFromUnix(long unixTime){

        Calendar weatherTime = Calendar.getInstance();
        weatherTime.setTimeInMillis(unixTime*1000);
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.DATE)==weatherTime.get(Calendar.DATE))
            return "Today";
        else if (now.get(Calendar.DATE)-weatherTime.get(Calendar.DATE)==1)
            return "Yesterday";
        else if (now.get(Calendar.DATE)-weatherTime.get(Calendar.DATE)==-1)
            return "Tomorrow";
        else
            return weatherTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
    }

    static String getTemp(Context context, double celsiusTemp){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );

        if(sharedPreferences.getInt(context.getString(R.string.preference_temp_key),0)==0){
            return String.valueOf((int)celsiusTemp)+"°C";
        } else{
            return celciusToFahrenheit(celsiusTemp);
        }
    }

    static String celciusToFahrenheit(double CelsiusTemp){
        return String.valueOf((int)CelsiusTemp*9/5+32)+"°F";
    }
}
