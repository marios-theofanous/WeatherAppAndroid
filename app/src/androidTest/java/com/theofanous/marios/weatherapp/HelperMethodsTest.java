package com.theofanous.marios.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class HelperMethodsTest {

    long Sun = 1483248964;
    long Mon = 1483335364;
    long dayInMillis = 86400000;
    Calendar calendar;

    @Before
    public void setUp() throws Exception {
        calendar = Calendar.getInstance();

    }

    @Test
    public void olderThanADay() throws Exception {

        //Dividing by 1000 since olderThanTwoDays multiplies by 1000
        //Today
        assertFalse(
                HelperMethods.olderThanTwoDays(calendar.getTimeInMillis()/1000)
        );

        //Minus a day and some seconds
        calendar.setTimeInMillis(calendar.getTimeInMillis()-dayInMillis-10);
        assertTrue(
                HelperMethods.olderThanTwoDays(calendar.getTimeInMillis()/1000)
        );

        //Tomorrow
        calendar.setTimeInMillis(calendar.getTimeInMillis()+dayInMillis+10);
        assertFalse(
                HelperMethods.olderThanTwoDays(calendar.getTimeInMillis()/1000)
        );
    }

    @Test
    public void getNameOfDayFromUnix() throws Exception {
        assertEquals(
                "Today",HelperMethods.getNameOfDayFromUnix(InstrumentationRegistry.getTargetContext(), calendar.getTimeInMillis()/1000)
        );
        assertEquals(
                "Yesterday",HelperMethods.getNameOfDayFromUnix(InstrumentationRegistry.getTargetContext(), (calendar.getTimeInMillis()-dayInMillis-10)/1000)
        );
        assertEquals(
                "Tomorrow",HelperMethods.getNameOfDayFromUnix(InstrumentationRegistry.getTargetContext(), (calendar.getTimeInMillis()+dayInMillis+10)/1000)
        );
        assertEquals(
                "Sunday",HelperMethods.getNameOfDayFromUnix(InstrumentationRegistry.getTargetContext(), Sun)
        );
        assertEquals(
                "Monday",HelperMethods.getNameOfDayFromUnix(InstrumentationRegistry.getTargetContext(), Mon)
        );
    }

    @Test
    public void getTemp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences unitPreference = PreferenceManager.getDefaultSharedPreferences(context);

        //Selecting Metric
        unitPreference.edit().putString(context.getString(R.string.pref_temp_key),context.getString(R.string.pref_units_metric_key)).apply();
        assertEquals("0°C", HelperMethods.getTemp(context, 0));
        assertEquals("32°C", HelperMethods.getTemp(context, 32));
        assertEquals("-32°C", HelperMethods.getTemp(context, -32));

        //Selecting Imperial
        unitPreference.edit().putString(context.getString(R.string.pref_temp_key),context.getString(R.string.pref_units_imperial_key)).apply();
        assertEquals("32°F", HelperMethods.getTemp(context, 0));
        assertEquals("2°F", HelperMethods.getTemp(context, -17));
        assertEquals("-22°F", HelperMethods.getTemp(context, -30));
    }

    @Test
    public void celciusToFahrenheit() throws Exception {
        assertEquals("32°F", HelperMethods.celciusToFahrenheit(0));
    }

}