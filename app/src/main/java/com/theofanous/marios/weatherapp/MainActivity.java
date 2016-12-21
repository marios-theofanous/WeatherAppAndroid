package com.theofanous.marios.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new retrieveWeatherData().execute();
    }

    class retrieveWeatherData extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            try{
                //TODO make the URL flexible
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&appid="+BuildConfig.WEATHER_API_KEY);

                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line).append("\n");
                }
                //Closing streams
                bufferedReader.close();
                urlConnection.disconnect();
                //Returning the JSON file in string format
                return stringBuilder.toString();
            }catch (Exception e){
                Log.e("ERROR RETRIEVING DATA", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(s==null){
                Toast.makeText(getApplicationContext(), R.string.retrieval_error, Toast.LENGTH_LONG).show();
            } else {
                TextView response = (TextView) findViewById(R.id.response);
                response.setText(s);
            }
        }
    }
}

