package com.theofanous.marios.weatherapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, WeatherListFragment.DayListListener,
        DetailFragment.DetailLoadedListener, WeatherListFragment.OnRefreshListener {

    WeatherContract.WeatherDbHelper mDbHelper;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if(id==R.id.action_about){
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        //Set the default preference in case it is the first time the app opens
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

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
                    Log.v(LOG_TAG, "PERMISSION GRANTED");
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    new retrieveWeatherData().execute();
                } else {
                    Log.v(LOG_TAG, "PERMISSION DENIED");
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
            Toast.makeText(getApplicationContext(), getString(R.string.loc_perm_needed), Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(getApplicationContext(), getString(R.string.enabled_location), Toast.LENGTH_LONG).show();
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

        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment_land);

        //Are we in two pane mode or does it need to start details activity
        if(detailFragment!=null && detailFragment.isVisible()){
            detailFragment.go(weatherData, position);
        }else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("POSITION", position);
            startActivity(intent);
        }
    }

    @Override
    public void onComplete(long dt) {
        //Required by detail fragment but not needed here
    }

    @Override
    public void refresh() {
        new retrieveWeatherData().execute(true);
    }

    class retrieveWeatherData extends AsyncTask<Boolean, Void, WeatherData> {

        boolean unableToRetrieve = false, dataUpdated = false;

        @Override
        protected WeatherData doInBackground(Boolean... params) {

            if(mDbHelper==null){
                mDbHelper = new WeatherContract.WeatherDbHelper(getBaseContext());
            }

            Boolean bool;
            if(params.length>0)
                bool=params[0];
            else
                bool=false;

            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            WeatherData data = getFromDb(db);
            db.close();

            if(data.list.size()==0 || HelperMethods.olderThanTwoDays(data.list.get(0).dt) || bool){
                weatherData = callApi();
                if(weatherData!=null){
                    dataUpdated = true;
                    writeToDB(weatherData);
                    return weatherData;
                }else{
                    unableToRetrieve = true;
                }
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
            db.close();
        }

        WeatherData callApi(){
            try{
                if(mLastLocation==null){
                    Log.i(LOG_TAG, "mLastLocation is empty in retrieve weather data");
                    return null;
                }
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+String.valueOf(mLastLocation.getLatitude())+
                        "&lon="+String.valueOf(mLastLocation.getLongitude())+"&appid="+BuildConfig.WEATHER_API_KEY+
                        "&units=metric"+"&cnt="+16+"&mode=json");

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
            WeatherListFragment fragment = (WeatherListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment4);
            if(s==null || unableToRetrieve){
                Toast.makeText(getApplicationContext(), R.string.unable_to_retrieve, Toast.LENGTH_LONG).show();
            } else {
                //Sets the action bar to show the city and country detected
                getSupportActionBar().setTitle(weatherData.cityName+", "+weatherData.country);
                DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment_land);
                if(detailFragment!=null && detailFragment.isVisible())
                    detailFragment.go(weatherData, 0);
                if(fragment!=null){
                    fragment.onDataChanged(s);

                }
            }
            if(dataUpdated)
                Toast.makeText(MainActivity.this, getString(R.string.data_retrieved), Toast.LENGTH_SHORT).show();
            if(fragment!=null)
                fragment.swipeRefreshLayout.setRefreshing(false);
        }
    }

    static WeatherData getFromDb(SQLiteDatabase db){
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
        cursor.close();
        return new WeatherData(city, country, list);
    }
}

