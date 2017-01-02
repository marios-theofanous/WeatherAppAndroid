package com.theofanous.marios.weatherapp;

/**
 * Created by Marios on 30/12/2016.
 */

public class IconFetcher {
    public static int returnIconString(String weatherIcon){
        switch (weatherIcon.substring(0,1)){
            case "01":
                return R.drawable.ic_sunny;
            default:
                return R.drawable.ic_moon;
        }
    }
}
