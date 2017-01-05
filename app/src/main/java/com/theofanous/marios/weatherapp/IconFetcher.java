package com.theofanous.marios.weatherapp;

/**
 * Created by Marios on 30/12/2016.
 */

public class IconFetcher {
    public static int returnIconString(String weatherIcon){
        switch (weatherIcon.substring(0,weatherIcon.length()-1)){
            case "01":
                return R.drawable.ic_sunny;
            case "02":
                return R.drawable.ic_cloudy;
            case "03":
                return R.drawable.ic_cloudy;
            case "04":
                return R.drawable.ic_cloudy;
            case "09":
                return R.drawable.ic_shower;
            case "10":
                return R.drawable.ic_rain;
            case "11":
                return R.drawable.ic_storm;
            case "13":
                return R.drawable.ic_snow;
            case "50":
                return R.drawable.ic_haze;
            default:
                return R.drawable.ic_moon;
        }
    }
}
