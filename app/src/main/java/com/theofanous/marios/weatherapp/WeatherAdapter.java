package com.theofanous.marios.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class WeatherAdapter extends ArrayAdapter<DayData>{
    Context context;
    int layoutResourceId;
    List<DayData> dayData = null;

    public WeatherAdapter(Context context, int resource, List<DayData> dayData) {
        super(context, resource, dayData);
        this.context=context;
        this.layoutResourceId=resource;
        this.dayData=dayData;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder=new Holder();
            holder.icon = (ImageView)row.findViewById(R.id.weatherIcon);
            holder.day = (TextView)row.findViewById(R.id.dayTextView);
            holder.dayIcon = (ImageView) row.findViewById(R.id.dayIcon);
            holder.nightIcon = (ImageView) row.findViewById(R.id.nightIcon);
            holder.dayTemp = (TextView)row.findViewById(R.id.dayTempTextView);
            holder.nightTemp = (TextView)row.findViewById(R.id.nightTempTextView);
            row.setTag(holder);
        } else {
            holder = (Holder)row.getTag();
        }


        DayData data = dayData.get(position);
        holder.dayIcon.setImageResource(R.drawable.ic_sunny);
        holder.nightIcon.setImageResource(R.drawable.ic_moon);
        holder.icon.setImageResource(IconFetcher.returnIconString(data.weatherIconId));
        holder.dayTemp.setText(HelperMethods.getTemp(getContext(), data.dayTemp));
        holder.nightTemp.setText(HelperMethods.getTemp(getContext(), data.nightTemp));
        holder.day.setText(HelperMethods.getNameOfDayFromUnix(data.dt));
        return row;
    }

    static class Holder {
        ImageView icon;
        ImageView dayIcon;
        ImageView nightIcon;
        TextView day;
        TextView dayTemp;
        TextView nightTemp;
    }
}
