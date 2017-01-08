package com.theofanous.marios.weatherapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Locale;


public class HelperMethods {

    static boolean olderThanTwoDays(long time){
        Calendar weatherTime = Calendar.getInstance();
        weatherTime.setTimeInMillis(time*1000);
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.DATE)-weatherTime.get(Calendar.DATE)>=2)
            return true;
        return false;
    }

    static String getNameOfDayFromUnix(Context context, long unixTime){

        Calendar weatherTime = Calendar.getInstance();
        weatherTime.setTimeInMillis(unixTime*1000);
        Calendar now = Calendar.getInstance();

        if(now.get(Calendar.DATE)==weatherTime.get(Calendar.DATE))
            return context.getString(R.string.today);
        else if (now.get(Calendar.DATE)-weatherTime.get(Calendar.DATE)==1)
            return context.getString(R.string.yesterday);
        else if (now.get(Calendar.DATE)-weatherTime.get(Calendar.DATE)==-1)
            return context.getString(R.string.tomorrow);
        else
            return weatherTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
    }

    static String getTemp(Context context, double celsiusTemp){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unitPref = sharedPreferences.getString(context.getString(R.string.pref_temp_key),"");

        if(unitPref.equalsIgnoreCase(context.getString(R.string.pref_units_metric))){
            return String.valueOf((int)celsiusTemp)+"°C";
        } else{
            return celciusToFahrenheit(celsiusTemp);
        }
    }

    static String celciusToFahrenheit(double CelsiusTemp){
        return String.valueOf((int)CelsiusTemp*9/5+32)+"°F";
    }
}
