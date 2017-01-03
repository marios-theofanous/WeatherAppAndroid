package com.theofanous.marios.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {



    String iconStr, locationStr, dateStr;
    double maxTempD, minTempD, pressureD, windSpeedD;
    int humidityInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        //Get the data from the intent
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            iconStr = bundle.getString(MainActivity.ICON_KEY);
        }

    }

}
