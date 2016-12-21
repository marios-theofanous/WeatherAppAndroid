package com.theofanous.marios.weatherapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marios on 21/12/2016.
 */

public class weatherData {
    String cityName, country;
    double lon, lat;
    List<dayData> list = new ArrayList<>();


    public weatherData(String cityName, String country, double lon, double lat, List<dayData> list) {
        this.cityName = cityName;
        this.country = country;
        this.lon = lon;
        this.lat = lat;
        this.list = list;
    }
}

class dayData {
    double mainTemp, minTemp, maxTemp;
    int humidity, weatherIconId;
    String weatherMain;

    public dayData(double mainTemp, double minTemp, double maxTemp,
                   int humidity, int weatherIconId, String weatherMain) {
        this.mainTemp = mainTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.weatherIconId = weatherIconId;
        this.weatherMain = weatherMain;
    }
}
