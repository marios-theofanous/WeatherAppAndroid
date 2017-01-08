package com.theofanous.marios.weatherapp;

import android.app.Instrumentation;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Marios on 05/01/2017.
 */
public class IconFetcherTest {
    @Test
    public void returnIconString() throws Exception {

        //These are real values that the api should return
        assertEquals(R.drawable.ic_sunny, IconFetcher.returnIconString("01d"));
        assertEquals(R.drawable.ic_cloudy, IconFetcher.returnIconString("02d"));
        //By default it should return the moon drawable, just in case
        assertEquals(R.drawable.ic_moon, IconFetcher.returnIconString("WHATEVER ELSE"));
    }

}