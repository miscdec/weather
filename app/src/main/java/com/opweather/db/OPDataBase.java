package com.opweather.db;

import android.database.sqlite.SQLiteDatabase;

import com.opweather.db.CityWeatherDBHelper.CityListEntry;
import com.opweather.db.CityWeatherDBHelper.WeatherEntry;
import com.opweather.db.CityWeatherDBHelper.ForecastEntry;

/**
 * Created by leeyh on 5/15.
 */
public class OPDataBase {
    private static final String COMMA_SEP = ",";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";

    private static final String CITY = "city";

    public static void createCityList(SQLiteDatabase db) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE ").append(CITY).append(" (");
        buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
        buffer.append(CityListEntry.COLUMN_1_PROVIDER).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_2_NAME).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_3_DISPLAY_NAME).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_1_LOCATION_ID).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_5_ADMINISTRATIVE_NAME).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_6_DISPLAY_ADMINISTRATIVE_NAME).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_7_COUNTRY).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_8_DISPLAY_COUNTRY).append(TEXT_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_9_DISPLAY_ORDER).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(CityListEntry.COLUMN_10_LAST_REFRESH_TIME).append(TEXT_TYPE);
        buffer.append(");");
        buffer.append("CREATE TRIGGER city_order_trigger AFTER INSERT ON ").append(CITY).append(" BEGIN ");
        buffer.append("UPDATE ").append(CITY).append(" SET ");
        buffer.append(CityListEntry.COLUMN_9_DISPLAY_ORDER).append(" = NEW.");
        buffer.append("_id").append(" WHERE ");
        buffer.append("_id").append(" = NEW.").append("_id");
        buffer.append("; END;");
        db.execSQL(buffer.toString());
    }

    public static void createWeather(SQLiteDatabase db) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE ").append(WeatherEntry.TABLE_NAME).append(" (");
        buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
        buffer.append(WeatherEntry.COLUMN_1_LOCATION_ID).append(TEXT_TYPE).append(" UNIQUE").append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_2_TIMESTAMP).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_3_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_4_REALFEEL_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_5_HIGH_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_6_LOW_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_7_HUMIDITY).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_8_SUNRISE_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_9_SUNSET_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_10_WEATHER_ID).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append("FOREIGN KEY (").append(WeatherEntry.COLUMN_1_LOCATION_ID).append(") ");
        buffer.append("REFERENCES ").append(CITY).append("(").append(WeatherEntry.COLUMN_1_LOCATION_ID).append(")");
        buffer.append(");");
        db.execSQL(buffer.toString());
    }

    public static void createForecast(SQLiteDatabase db) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE ").append(ForecastEntry.TABLE_NAME).append(" (");
        buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
        buffer.append(WeatherEntry.COLUMN_1_LOCATION_ID).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_2_TIMESTAMP).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_5_HIGH_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_6_LOW_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append(WeatherEntry.COLUMN_10_WEATHER_ID).append(INTEGER_TYPE).append(COMMA_SEP);
        buffer.append("FOREIGN KEY (").append(WeatherEntry.COLUMN_1_LOCATION_ID).append(") ");
        buffer.append("REFERENCES ").append(CITY).append("(").append(WeatherEntry.COLUMN_1_LOCATION_ID).append(")");
        buffer.append(");");
        db.execSQL(buffer.toString());
    }

    public static void updataDatabase(SQLiteDatabase db, String table, String createTableSql) {
        String tempTableName = "_temp_" + table;
        String createTempTable = "alter table " + table + " rename to " + tempTableName;
        String copyData = "insert into " + table + " select * from " + tempTableName;
        String deleteTemp = "drop table " + tempTableName;
        db.execSQL(createTempTable);
        db.execSQL(createTableSql);
        db.execSQL(copyData);
        db.execSQL(deleteTemp);
    }

}
