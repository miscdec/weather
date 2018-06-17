package com.opweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CityWeatherDBHelper extends SQLiteOpenHelper {
    private static final String COMMA_SEP = ",";
    private static final String DB_NAME = "city_list.db";
    private static final int DB_VERSION = 8;
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";

    public static abstract class CityListEntry implements BaseColumns {
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

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            StringBuffer buffer;
            if (1 == oldVersion) {
                String alterTable = "ALTER TABLE " .concat("city").concat(" ADD COLUMN ");
                db.execSQL(alterTable.concat(CityListEntry.COLUMN_5_ADMINISTRATIVE_NAME).concat(TEXT_TYPE));
                db.execSQL(alterTable.concat(CityListEntry.COLUMN_6_DISPLAY_ADMINISTRATIVE_NAME).concat(TEXT_TYPE));
                db.execSQL(alterTable.concat("country").concat(TEXT_TYPE));
                db.execSQL(alterTable.concat(CityListEntry.COLUMN_8_DISPLAY_COUNTRY).concat(TEXT_TYPE));
                db.execSQL(alterTable.concat(CityListEntry.COLUMN_9_DISPLAY_ORDER).concat(INTEGER_TYPE));
                db.execSQL(alterTable.concat(CityListEntry.COLUMN_10_LAST_REFRESH_TIME).concat(TEXT_TYPE));
                db.rawQuery("UPDATE " .concat("city").concat(" SET ").concat(CityListEntry.COLUMN_9_DISPLAY_ORDER)
                        .concat(" = ").concat("_id"), null);
                buffer = new StringBuffer();
                buffer.append("CREATE TRIGGER city_order_trigger AFTER INSERT ON ").append("city").append(" BEGIN ");
                buffer.append("UPDATE ").append("city").append(" SET ");
                buffer.append(CityListEntry.COLUMN_9_DISPLAY_ORDER).append(" = NEW.");
                buffer.append("_id").append(" WHERE ");
                buffer.append("_id").append(" = NEW.").append("_id");
                buffer.append("; END;");
                db.execSQL(buffer.toString());
                buffer = new StringBuffer();
                buffer.append("CREATE TABLE ").append(WeatherEntry.TABLE_NAME).append(" (");
                buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
                buffer.append("locationId").append(TEXT_TYPE).append(" UNIQUE").append(COMMA_SEP);
                buffer.append("timestamp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_3_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_4_REALFEEL_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("highTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("lowTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_7_HUMIDITY).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_8_SUNRISE_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_9_SUNSET_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("weatherId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("FOREIGN KEY (").append("locationId").append(") ");
                buffer.append("REFERENCES ").append("city").append("(").append("locationId").append(")");
                buffer.append(");");
                db.execSQL(buffer.toString());
                buffer = new StringBuffer();
                buffer.append("CREATE TABLE ").append(ForecastEntry.TABLE_NAME).append(" (");
                buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
                buffer.append("locationId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("timestamp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("highTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("lowTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("weatherId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("FOREIGN KEY (").append("locationId").append(") ");
                buffer.append("REFERENCES ").append("city").append("(").append("locationId").append(")");
                buffer.append(");");
            } else if (2 == oldVersion) {
                db.execSQL("ALTER TABLE " .concat("city").concat(" ADD COLUMN ").concat(CityListEntry
                        .COLUMN_9_DISPLAY_ORDER).concat(INTEGER_TYPE));
                db.rawQuery("UPDATE " .concat("city").concat(" SET ").concat(CityListEntry.COLUMN_9_DISPLAY_ORDER)
                        .concat(" = ").concat("_id"), null);
                buffer = new StringBuffer();
                buffer.append("CREATE TRIGGER city_order_trigger AFTER INSERT ON ").append("city").append(" BEGIN ");
                buffer.append("UPDATE ").append("city").append(" SET ");
                buffer.append(CityListEntry.COLUMN_9_DISPLAY_ORDER).append(" = NEW.");
                buffer.append("_id").append(" WHERE ");
                buffer.append("_id").append(" = NEW.").append("_id");
                buffer.append("; END;");
                db.execSQL(buffer.toString());
                db.execSQL("DROP TABLE IF EXISTS " .concat(WeatherEntry.TABLE_NAME));
                buffer = new StringBuffer();
                buffer.append("CREATE TABLE ").append(WeatherEntry.TABLE_NAME).append(" (");
                buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
                buffer.append("locationId").append(TEXT_TYPE).append(" UNIQUE").append(COMMA_SEP);
                buffer.append("timestamp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_3_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_4_REALFEEL_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("highTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("lowTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_7_HUMIDITY).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_8_SUNRISE_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_9_SUNSET_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("weatherId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("FOREIGN KEY (").append("locationId").append(") ");
                buffer.append("REFERENCES ").append("city").append("(").append("locationId").append(")");
                buffer.append(");");
                db.execSQL(buffer.toString());
                buffer = new StringBuffer();
                buffer.append("CREATE TABLE ").append(ForecastEntry.TABLE_NAME).append(" (");
                buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
                buffer.append("locationId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("timestamp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("highTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("lowTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("weatherId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("FOREIGN KEY (").append("locationId").append(") ");
                buffer.append("REFERENCES ").append("city").append("(").append("locationId").append(")");
                buffer.append(");");
                db.execSQL(buffer.toString());
            } else if (3 == oldVersion) {
                db.execSQL("ALTER TABLE " .concat("city").concat(" ADD COLUMN ").concat(CityListEntry
                        .COLUMN_9_DISPLAY_ORDER).concat(INTEGER_TYPE));
                db.rawQuery("UPDATE " .concat("city").concat(" SET ").concat(CityListEntry.COLUMN_9_DISPLAY_ORDER)
                        .concat(" = ").concat("_id"), null);
                buffer = new StringBuffer();
                buffer.append("CREATE TRIGGER city_order_trigger AFTER INSERT ON ").append("city").append(" BEGIN ");
                buffer.append("UPDATE ").append("city").append(" SET ");
                buffer.append(CityListEntry.COLUMN_9_DISPLAY_ORDER).append(" = NEW.");
                buffer.append("_id").append(" WHERE ");
                buffer.append("_id").append(" = NEW.").append("_id");
                buffer.append("; END;");
                db.execSQL(buffer.toString());
                db.execSQL("DROP TABLE IF EXISTS " .concat(WeatherEntry.TABLE_NAME));
                db.execSQL("DROP TABLE IF EXISTS " .concat(ForecastEntry.TABLE_NAME));
                buffer = new StringBuffer();
                buffer.append("CREATE TABLE ").append(WeatherEntry.TABLE_NAME).append(" (");
                buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
                buffer.append("locationId").append(TEXT_TYPE).append(" UNIQUE").append(COMMA_SEP);
                buffer.append("timestamp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_3_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_4_REALFEEL_TEMPERATURE).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("highTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("lowTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_7_HUMIDITY).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_8_SUNRISE_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append(WeatherEntry.COLUMN_9_SUNSET_TIME).append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("weatherId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("FOREIGN KEY (").append("locationId").append(") ");
                buffer.append("REFERENCES ").append("city").append("(").append("locationId").append(")");
                buffer.append(");");
                db.execSQL(buffer.toString());
                buffer = new StringBuffer();
                buffer.append("CREATE TABLE ").append(ForecastEntry.TABLE_NAME).append(" (");
                buffer.append("_id").append(INTEGER_TYPE).append(" PRIMARY KEY,");
                buffer.append("locationId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("timestamp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("highTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("lowTemp").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("weatherId").append(INTEGER_TYPE).append(COMMA_SEP);
                buffer.append("FOREIGN KEY (").append("locationId").append(") ");
                buffer.append("REFERENCES ").append("city").append("(").append("locationId").append(")");
                buffer.append(");");
                db.execSQL(buffer.toString());
            } else if (4 == oldVersion) {
                db.execSQL("ALTER TABLE " .concat("city").concat(" ADD COLUMN ").concat(CityListEntry
                        .COLUMN_9_DISPLAY_ORDER));
                db.rawQuery("UPDATE " .concat("city").concat(" SET ").concat(CityListEntry.COLUMN_9_DISPLAY_ORDER)
                        .concat(" = ").concat("_id"), null);
                buffer = new StringBuffer();
                buffer.append("CREATE TRIGGER city_order_trigger AFTER INSERT ON ").append("city").append(" BEGIN ");
                buffer.append("UPDATE ").append("city").append(" SET ");
                buffer.append(CityListEntry.COLUMN_9_DISPLAY_ORDER).append(" = NEW.");
                buffer.append("_id").append(" WHERE ");
                buffer.append("_id").append(" = NEW.").append("_id");
                buffer.append("; END;");
                db.execSQL(buffer.toString());
            } else if (5 == oldVersion) {
                buffer = new StringBuffer();
                buffer.append("CREATE TRIGGER city_order_trigger AFTER INSERT ON ").append("city").append(" BEGIN ");
                buffer.append("UPDATE ").append("city").append(" SET ");
                buffer.append(CityListEntry.COLUMN_9_DISPLAY_ORDER).append(" = NEW.");
                buffer.append("_id").append(" WHERE ");
                buffer.append("_id").append(" = NEW.").append("_id");
                buffer.append("; END;");
                db.execSQL(buffer.toString());
            } else if (6 == oldVersion) {
                db.execSQL("ALTER TABLE " .concat("city").concat(" ADD COLUMN ").concat(CityListEntry
                        .COLUMN_10_LAST_REFRESH_TIME).concat(TEXT_TYPE));
                db.rawQuery("UPDATE " .concat("city").concat(" SET ").concat(CityListEntry
                        .COLUMN_10_LAST_REFRESH_TIME).concat(" = ").concat("_id"), null);
            } else if (7 == oldVersion) {
                OPDataBase.updataDatabase(db, "city", OPDataBase.getCreateCityListSQL());
            } else {
                db.execSQL("DROP TABLE IF EXISTS " .concat("city"));
                db.execSQL("DROP TABLE IF EXISTS " .concat(WeatherEntry.TABLE_NAME));
                db.execSQL("DROP TABLE IF EXISTS " .concat(ForecastEntry.TABLE_NAME));
                onCreate(db);
            }
        }
    }
}
