package com.theofanous.marios.weatherapp;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private ImageView icon;
    private TextView location, date, maxTemp, minTemp, dayTemp, nightTemp, pressure, humidity, windSpeed;
    WeatherData weatherData;
    DayData dayData;
    int position;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        //Get a hold of every element in the fragment
        icon = (ImageView) view.findViewById(R.id.detail_weather_icon);
        location = (TextView) view.findViewById(R.id.detail_location_name);
        date = (TextView) view.findViewById(R.id.detail_date);
        dayTemp = (TextView) view.findViewById(R.id.detail_dayTemp);
        nightTemp = (TextView) view.findViewById(R.id.detail_nightTemp);
        maxTemp = (TextView) view.findViewById(R.id.maxTemp);
        minTemp = (TextView) view.findViewById(R.id.detail_minTemp);
        pressure = (TextView) view.findViewById(R.id.detail_pressure);
        humidity = (TextView) view.findViewById(R.id.detail_humidity);
        windSpeed = (TextView) view.findViewById(R.id.detail_wind_speed);


        try {
            Bundle bundle = getArguments();
            position = bundle.getInt("POSITION");

            weatherData = ((MainActivity)getActivity()).weatherData;
            dayData = weatherData.list.get(position);

            //Set values
            icon.setImageResource(IconFetcher.returnIconString(dayData.weatherIconId));
            location.setText(weatherData.cityName+", "+weatherData.country);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            date.setText(simpleDateFormat.format(dayData.dt*1000));
            dayTemp.setText(getString(R.string.dayTemp)+" "+HelperMethods.getTemp(getContext(), dayData.dayTemp));
            nightTemp.setText(getString(R.string.nightTemp)+" "+HelperMethods.getTemp(getContext(), dayData.dayTemp));
            maxTemp.setText(getString(R.string.max_temp)+" "+HelperMethods.getTemp(getContext(), dayData.maxTemp));
            minTemp.setText(getString(R.string.min_temp)+" "+HelperMethods.getTemp(getContext(), dayData.minTemp));
            pressure.setText(getString(R.string.pressure)+" "+String.valueOf(dayData.pressure)+" hPa");
            humidity.setText(getString(R.string.humidity)+" "+String.valueOf(dayData.humidity)+"%");
            windSpeed.setText(getString(R.string.wind_speed)+" "+String.valueOf(dayData.windSpeed)+" m/s");
        }catch (Exception e){

        }finally {
            return view;
        }

//        location.setText(String.valueOf(position));


    }

    public void go(int position) {
        weatherData = ((MainActivity)getActivity()).weatherData;
        dayData = weatherData.list.get(position);

        //Set values
        icon.setImageResource(IconFetcher.returnIconString(dayData.weatherIconId));
        location.setText(weatherData.cityName+", "+weatherData.country);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(simpleDateFormat.format(dayData.dt*1000));
        dayTemp.setText(getString(R.string.dayTemp)+" "+HelperMethods.getTemp(getContext(), dayData.dayTemp));
        nightTemp.setText(getString(R.string.nightTemp)+" "+HelperMethods.getTemp(getContext(), dayData.dayTemp));
        maxTemp.setText(getString(R.string.max_temp)+" "+HelperMethods.getTemp(getContext(), dayData.maxTemp));
        minTemp.setText(getString(R.string.min_temp)+" "+HelperMethods.getTemp(getContext(), dayData.minTemp));
        pressure.setText(getString(R.string.pressure)+" "+String.valueOf(dayData.pressure)+" hPa");
        humidity.setText(getString(R.string.humidity)+" "+String.valueOf(dayData.humidity)+"%");
        windSpeed.setText(getString(R.string.wind_speed)+" "+String.valueOf(dayData.windSpeed)+" m/s");
    }
}
