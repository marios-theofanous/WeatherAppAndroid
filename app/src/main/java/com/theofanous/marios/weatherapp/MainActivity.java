package com.theofanous.marios.weatherapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
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
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, WeatherListFragment.DayListListener {

    WeatherContract.WeatherDbHelper mDbHelper;

    public static final String ICON_KEY = "ICON_KEY";
    public static final String LOCATION_KEY = "LOCATION_KEY";
    public static final String DATE_KEY = "DATE_KEY";
    public static final String MAX_TEMP_KEY = "MAX_TEMP_KEY";
    public static final String MIN_TEMP_KEY = "MIN_TEMP_KEY";
    public static final String PRESSURE_KEY = "PRESSURE_KEY";
    public static final String HUMIDITY_KEY = "HUMIDITY_KEY";
    public static final String WIND_SPEED_KEY = "WIND_SPEED_KEY";
    private static final String LIST_FRAGMENT_TAG = "LIST_FRAGMENT_TAG";

    private final String LOG_TAG = "MainActivityLog";
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 5;
    double lon, lat;
    GoogleApiClient mGoogleApiClient;
    WeatherData weatherData;
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
    protected void onResume() {
        super.onResume();
        Fragment f = getSupportFragmentManager()
                .findFragmentByTag(LIST_FRAGMENT_TAG);

        if(f!=null && weatherData!=null){
            ((WeatherListFragment)f).onDataChanged(weatherData);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new WeatherContract.WeatherDbHelper(getBaseContext());

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

        if(getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG)==null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_main, new WeatherListFragment(), LIST_FRAGMENT_TAG)
                    .commit();
        }
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

    @Override
    public void onDaySelected(int position) {
        Toast.makeText(this, String.valueOf(position), Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, DetailsActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(LOCATION_KEY, weatherData.cityName+", "+weatherData.country);
//        DayData dayData = weatherData.list.get(position);
//        bundle.putLong(DATE_KEY, dayData.dt);
//        bundle.putDouble(MAX_TEMP_KEY, dayData.maxTemp);
//        bundle.putDouble(MIN_TEMP_KEY, dayData.minTemp);
//        bundle.putDouble(PRESSURE_KEY, dayData.pressure);
//        bundle.putInt(HUMIDITY_KEY, dayData.humidity);
//        bundle.putDouble(WIND_SPEED_KEY, dayData.windSpeed);
//        intent.putExtras(bundle);
//        startActivity(intent);
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment3);
        if(detailFragment!=null && detailFragment instanceof DetailFragment){
            detailFragment.go(position);
        }else {
            detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.activity_main, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }



    }

    void TaskHelper(WeatherListFragment f){
        if(weatherData!=null)
            f.onDataChanged(weatherData);
    }

    class retrieveWeatherData extends AsyncTask<Void, Void, WeatherData> {


        @Override
        protected WeatherData doInBackground(Void... params) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String[] values = {
                    WeatherContract.DayEntry._ID,
                    WeatherContract.DayEntry.COLUMN_NAME_COUNTRY,
                    WeatherContract.DayEntry.COLUMN_NAME_CITY,
                    WeatherContract.DayEntry.COLUMN_NAME_ICON,
                    WeatherContract.DayEntry.COLUMN_NAME_DT,
                    WeatherContract.DayEntry.COLUMN_NAME_MAX_TEMP,
                    WeatherContract.DayEntry.COLUMN_NAME_MIN_TEMP,
                    WeatherContract.DayEntry.COLUMN_NAME_DAY_TEMP,
                    WeatherContract.DayEntry.COLUMN_NAME_NIGHT_TEMP,
                    WeatherContract.DayEntry.COLUMN_NAME_PRESSURE,
                    WeatherContract.DayEntry.COLUMN_NAME_HUMIDITY,
                    WeatherContract.DayEntry.COLUMN_NAME_WIND_SPEED,
            };

            String sortOrder =
                    WeatherContract.DayEntry.COLUMN_NAME_DT + " ASC";

            Cursor cursor = db.query(
                    WeatherContract.DayEntry.TABLE_NAME,
                    values,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            WeatherData data;
            List<DayData> list  = new ArrayList<DayData>();
            String country = "", city = "";
            while (cursor.moveToNext()){
                long dt = cursor.getLong(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_DT));
                country = cursor.getString(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_COUNTRY));
                city = cursor.getString(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_CITY));
                String icon = cursor.getString(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_ICON));
                int maxTemp = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_MAX_TEMP));
                int minTemp = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_MIN_TEMP));
                int dayTemp = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_DAY_TEMP));
                int nightTemp = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_NIGHT_TEMP));
                double pressure = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_PRESSURE));
                int humidity = cursor.getInt(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_HUMIDITY));
                double windSpeed = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(WeatherContract.DayEntry.COLUMN_NAME_WIND_SPEED));

                list.add(new DayData(dayTemp, nightTemp, minTemp, maxTemp, humidity, icon, dt, pressure, windSpeed));
            }
            data = new WeatherData(city, country, list);
            cursor.close();

            if(data.list.size()==0 || HelperMethods.olderThanADay(data.list.get(0).dt)){
                //TODO CALL API TO GET DATA
                weatherData = callApi();
                if(weatherData!=null){
                    writeToDB(weatherData);
                    return weatherData;
                }else
                    Log.e(LOG_TAG, "weather data is null after call to api");
            }
            weatherData = data;
            return weatherData;
        }


        void writeToDB(WeatherData weatherData){
            List<DayData> dayDatas = weatherData.list;
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.execSQL(WeatherContract.SQL_DELETE_ENTRIES);
            db.execSQL(WeatherContract.SQL_CREATE_ENTRIES);
            for(int i=0; i<dayDatas.size();i++){
                DayData day = dayDatas.get(i);
                ContentValues values = new ContentValues();
                values.put(WeatherContract.DayEntry.COLUMN_NAME_CITY, weatherData.cityName);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_COUNTRY, weatherData.country);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_DT, day.dt);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_ICON, day.weatherIconId);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_MAX_TEMP, day.maxTemp);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_MIN_TEMP, day.minTemp);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_DAY_TEMP, day.dayTemp);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_NIGHT_TEMP, day.nightTemp);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_PRESSURE, day.pressure);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_HUMIDITY, day.humidity);
                values.put(WeatherContract.DayEntry.COLUMN_NAME_WIND_SPEED, day.windSpeed);
                db.insert(WeatherContract.DayEntry.TABLE_NAME, null, values);
            }
        }

        WeatherData callApi(){
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
                    double pressure = weather.getDouble("pressure");
                    double speed = weather.getDouble("speed");

                    list.add(new DayData(dayTemp, nightTemp, minTemp, maxTemp, humidity, weatherIconId, dt, pressure, speed));
                }
                weatherData = new WeatherData(cityName,cityCountry, list);
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
                //Sets the action bar to show the city and country detected

                getSupportActionBar().setTitle(weatherData.cityName+", "+weatherData.country);
                WeatherListFragment fragment = (WeatherListFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
                if(fragment!=null){
                    fragment.onDataChanged(s);
                }

            }
        }
    }
}

