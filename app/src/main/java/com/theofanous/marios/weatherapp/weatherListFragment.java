package com.theofanous.marios.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class WeatherListFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    OnRefreshListener onRefreshListener;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dayListListener.onDaySelected(position);
    }

    @Override
    public void onRefresh() {
        try{
            onRefreshListener = (WeatherListFragment.OnRefreshListener) getActivity();
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + " must implement DayListListener");
        }
        onRefreshListener.refresh();
    }

    public interface DayListListener {
        void onDaySelected(int position);
    }

    public interface OnRefreshListener{
        void refresh();
    }

    ListView weatherListview;
    DayListListener dayListListener;

    public WeatherListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_list, container, false);
        weatherListview = (ListView) view.findViewById(R.id.weather_listview);
        weatherListview.setOnItemClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
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
