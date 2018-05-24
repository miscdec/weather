package com.opweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by leeyh on 5/15.
 */
public class CityWeatherDBHelper extends SQLiteOpenHelper {
    private static final String COMMA_SEP = ",";
    private static final String DB_NAME = "city_list.db";
    private static final int DB_VERSION = 8;
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";


    public static abstract class CityListEntry implements BaseColumns{
        public static final String COLUMN_10_LAST_REFRESH_TIME = "lastRefreshTime";
        public static final String COLUMN_1_PROVIDER = "provider";
        public static final String COLUMN_2_NAME = "name";
        public static final String COLUMN_3_DISPLAY_NAME = "displayName";
        public static final String COLUMN_4_LOCATION_ID = "locationId";
        public static final String COLUMN_5_ADMINISTRATIVE_NAME = "adminName";
        public static final String COLUMN_6_DISPLAY_ADMINISTRATIVE_NAME = "displayAdminName";
        public static final String COLUMN_7_COUNTRY = "country";
        public static final String COLUMN_8_DISPLAY_COUNTRY = "displayCountry";
        public static final String COLUMN_9_DISPLAY_ORDER = "displayOrder";
        public static final String TABLE_NAME = "city";
    }

    public static abstract class ForecastEntry implements BaseColumns {
        public static final String COLUMN_1_LOCATION_ID = "locationId";
        public static final String COLUMN_2_TIMESTAMP = "timestamp";
        public static final String COLUMN_3_HIGH_TEMPERATURE = "highTemp";
        public static final String COLUMN_4_LOW_TEMPERATURE = "lowTemp";
        public static final String COLUMN_5_WEATHER_ID = "weatherId";
        public static final String TABLE_NAME = "forecast";
    }

    public static abstract class WeatherEntry implements BaseColumns {
        public static final String COLUMN_10_WEATHER_ID = "weatherId";
        public static final String COLUMN_1_LOCATION_ID = "locationId";
        public static final String COLUMN_2_TIMESTAMP = "timestamp";
        public static final String COLUMN_3_TEMPERATURE = "temperature";
        public static final String COLUMN_4_REALFEEL_TEMPERATURE = "realFeelTemp";
        public static final String COLUMN_5_HIGH_TEMPERATURE = "highTemp";
        public static final String COLUMN_6_LOW_TEMPERATURE = "lowTemp";
        public static final String COLUMN_7_HUMIDITY = "humidity";
        public static final String COLUMN_8_SUNRISE_TIME = "sunriseTime";
        public static final String COLUMN_9_SUNSET_TIME = "sunsetTime";
        public static final String TABLE_NAME = "weather";
    }


    public CityWeatherDBHelper(Context context) {
        super(context, DB_NAME, null, 8);
    }

    public void onCreate(SQLiteDatabase db) {
        OPDataBase.createCityList(db);
        OPDataBase.createWeather(db);
        OPDataBase.createForecast(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
