package com.theofanous.marios.weatherapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String LOG_TAG = "MainActivityLog";
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 5;
    double lon, lat;
    GoogleApiClient mGoogleApiClient;

    Location mLastLocation;
    LocationRequest mLocationRequest;

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_COARSE_LOCATION);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000000)
                .setFastestInterval(1000000);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_COARSE_LOCATION: {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_LONG).show();
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    new retrieveWeatherData().execute();
                } else {
                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_COARSE_LOCATION);
        }
        startLocationUpdates();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            new retrieveWeatherData().execute();
        }else if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "This app requires locations permissions", Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(getApplicationContext(), "Have you enabled location?", Toast.LENGTH_LONG).show();
    }


    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(1000000)
                .setFastestInterval(1000000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLastLocation = location;
                    //new retrieveWeatherData().execute();
                }
            });
        }

    }





    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class retrieveWeatherData extends AsyncTask<Void, Void, WeatherData> {


        @Override
        protected WeatherData doInBackground(Void... params) {
            try{
                if(mLastLocation==null){
                    Log.i(LOG_TAG, "mLastLocation is empty in retrieve weather data");
                    return null;
                }
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+String.valueOf(mLastLocation.getLatitude())+
                        "&lon="+String.valueOf(mLastLocation.getLongitude())+"&appid="+BuildConfig.WEATHER_API_KEY+
                        "&units=metric"+"&cnt="+10+"&mode=json");

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
                //Parsing the JSON file
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONObject city = jsonObject.getJSONObject("city");
                String cityName = city.getString("name");
                String cityCountry = city.getString("country");
                JSONArray array = jsonObject.getJSONArray("list");
                List<DayData> list = new ArrayList<>();
                for(int i=0; i<array.length(); i++){
                    JSONObject weather = array.getJSONObject(i);
                    double dayTemp = weather.getJSONObject("temp").getDouble("day");
                    double nightTemp = weather.getJSONObject("temp").getDouble("night");
                    double minTemp = weather.getJSONObject("temp").getDouble("min");
                    double maxTemp = weather.getJSONObject("temp").getDouble("max");
                    int humidity = weather.getInt("humidity");

                    String weatherIconId = weather.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("icon");

                    String weatherMain = weather.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("main");
                    long dt = weather.getLong("dt");

                    list.add(new DayData(dayTemp, nightTemp, minTemp, maxTemp, humidity, weatherIconId, weatherMain, dt));
                }
                WeatherData weatherData = new WeatherData(cityName,cityCountry, list);
                return weatherData;

            }catch (Exception e){
                Log.e("ERROR RETRIEVING DATA", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherData s) {
            if(s==null){
                Toast.makeText(getApplicationContext(), R.string.retrieval_error, Toast.LENGTH_LONG).show();
            } else {
                //TODO use custom list adapter and give it the daydata list inside weather data
                WeatherListFragment fragment = (WeatherListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                if(fragment!=null){
                    fragment.onDataChanged(s);
                }

            }
        }
    }
}

