package com.theofanous.marios.weatherapp;

import java.util.ArrayList;
import java.util.List;

public class WeatherData {
    String cityName, country;
    List<DayData> list = new ArrayList<>();


    public WeatherData(String cityName, String country, List<DayData> list) {
        this.cityName = cityName;
        this.country = country;
        this.list = list;
    }
}

class DayData {
    double dayTemp, nightTemp, minTemp, maxTemp;
    long dt;
    int humidity;
    String weatherMain, weatherIconId;

    public DayData(double dayTemp, double nightTemp, double minTemp, double maxTemp,
                   int humidity, String weatherIconId, String weatherMain, long dt) {
        this.dayTemp = dayTemp;
        this.nightTemp = nightTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.weatherIconId = weatherIconId;
        this.weatherMain = weatherMain;
        this.dt = dt;
    }
}
