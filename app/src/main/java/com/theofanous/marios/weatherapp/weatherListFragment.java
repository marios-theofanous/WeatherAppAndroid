package com.theofanous.marios.weatherapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

public class weatherListFragment extends Fragment {

    ListView weatherListview;

    public weatherListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_list, container, false);
        weatherListview = (ListView) view.findViewById(R.id.weather_listview);
        return view;
    }

    public void onDataChanged (WeatherData weatherData){
        weatherListview.setAdapter(new WeatherAdapter(getActivity(), R.layout.weatherlist_item_row, weatherData.list));
    }
}
