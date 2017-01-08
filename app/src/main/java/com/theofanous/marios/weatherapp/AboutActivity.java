package com.theofanous.marios.weatherapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        toolbar.setTitle(getString(R.string.about_title));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView first = (TextView) findViewById(R.id.about_first);

        String firstStr = getString(R.string.designed_by)+
                " Nikita Golubev " +
                getString(R.string.and)+
                " EpicCoders " +
                getString(R.string.from)+" "+
                getString(R.string.flaticon);

        first.setText(firstStr);
    }
}
