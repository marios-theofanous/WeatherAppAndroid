package com.theofanous.marios.weatherapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Marios on 03/01/2017.
 */

public class WeatherContract {
    private WeatherContract(){}

    public static class DayEntry implements BaseColumns{
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_DT = "dt";
        public static final String COLUMN_NAME_MAX_TEMP = "max_temp";
        public static final String COLUMN_NAME_MIN_TEMP = "min_temp";
        public static final String COLUMN_NAME_DAY_TEMP = "day_temp";
        public static final String COLUMN_NAME_NIGHT_TEMP = "night_temp";
        public static final String COLUMN_NAME_PRESSURE = "pressure";
        public static final String COLUMN_NAME_HUMIDITY = "humidity";
        public static final String COLUMN_NAME_WIND_SPEED = "wind_speed";
        public static final String COLUMN_NAME_ICON = "icon";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DayEntry.TABLE_NAME + " (" +
                    DayEntry._ID + " INTEGER PRIMARY KEY," +
                    DayEntry.COLUMN_NAME_CITY + " TEXT," +
                    DayEntry.COLUMN_NAME_ICON + " TEXT," +
                    DayEntry.COLUMN_NAME_COUNTRY + " TEXT," +
                    DayEntry.COLUMN_NAME_DT + " BIGINT," +
                    DayEntry.COLUMN_NAME_MAX_TEMP + " INTEGER," +
                    DayEntry.COLUMN_NAME_MIN_TEMP + " INTEGER," +
                    DayEntry.COLUMN_NAME_DAY_TEMP + " INTEGER," +
                    DayEntry.COLUMN_NAME_NIGHT_TEMP + " INTEGER," +
                    DayEntry.COLUMN_NAME_PRESSURE + " FLOAT," +
                    DayEntry.COLUMN_NAME_HUMIDITY + " INTEGER," +
                    DayEntry.COLUMN_NAME_WIND_SPEED + " FLOAT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DayEntry.TABLE_NAME;

    public static class WeatherDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Weather.db";

        public WeatherDbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}


