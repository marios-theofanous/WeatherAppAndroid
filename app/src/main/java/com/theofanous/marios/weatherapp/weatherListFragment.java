package com.theofanous.marios.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class WeatherListFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dayListListener.onDaySelected(position);
    }

    public interface DayListListener {
        void onDaySelected(int position);
    }

    ListView weatherListview;
    DayListListener dayListListener;

    public WeatherListFragment() {
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
        weatherListview.setOnItemClickListener(this);
        ((MainActivity)getActivity()).TaskHelper(this);
        return view;
    }

    public void onDataChanged (WeatherData weatherData){
        weatherListview.setAdapter(new WeatherAdapter(getActivity(), R.layout.weatherlist_item_row, weatherData.list));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            dayListListener = (DayListListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement DayListListener");
        }
    }

}
