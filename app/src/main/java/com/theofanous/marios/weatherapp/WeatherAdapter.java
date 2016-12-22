package com.theofanous.marios.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.SharedPreferencesCompat;
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
            holder.temp = (TextView)row.findViewById(R.id.tempTextView);
            row.setTag(holder);
        } else {
            holder = (Holder)row.getTag();
        }

        //TODO make these take all parameters from daydata obj
        DayData data = dayData.get(position);
        holder.icon.setImageResource(R.drawable.ic_sunny);
        holder.temp.setText(String.valueOf(data.mainTemp)+"°");
        holder.day.setText(DateHelper.getNameOfDayFromUnix(data.dt));

        return row;
    }

    static class Holder {
        ImageView icon;
        TextView day;
        TextView temp;
    }
}
