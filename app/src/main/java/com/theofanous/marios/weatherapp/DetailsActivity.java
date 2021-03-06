package com.theofanous.marios.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements DetailFragment.DetailLoadedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Get the data from the intent
        Intent intent = getIntent();
        if(intent!=null){
            WeatherContract.WeatherDbHelper weatherDbHelper = new WeatherContract.WeatherDbHelper(getBaseContext());
            WeatherData data = MainActivity.getFromDb(weatherDbHelper.getReadableDatabase());

            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
            if(detailFragment!=null)
                detailFragment.go(data, intent.getIntExtra("POSITION", 0));

        }
    }


    @Override
    public void onComplete(long dt) {
        Calendar weatherTime = Calendar.getInstance();
        weatherTime.setTimeInMillis(dt*1000);
        String day = weatherTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
        getSupportActionBar().setTitle(getString(R.string.Weather_on)+" "+day);
    }
}
