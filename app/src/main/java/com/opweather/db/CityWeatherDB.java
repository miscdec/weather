package com.opweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.opweather.bean.CityData;
import com.opweather.bean.WeatherData;
import com.opweather.db.CityWeatherDBHelper.CityListEntry;
import com.opweather.db.CityWeatherDBHelper.ForecastEntry;
import com.opweather.db.CityWeatherDBHelper.WeatherEntry;
import com.opweather.ui.CityListActivity;
import com.opweather.util.PreferenceUtils;
import com.opweather.util.StringUtils;
import com.opweather.util.SystemSetting;
import com.opweather.util.WeatherLog;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.List;

public class CityWeatherDB {
    private final String TAG = getClass().getSimpleName();
    private static CityWeatherDB mSelf;
    private Context mContext;
    private CityWeatherDBHelper mDBHelper;
    private SQLiteDatabase mDb;
    private List<CityListDBListener> mListenerList;
    private SQLiteDatabase mReadableDb;
    public final int TYPE_CITY_ADDED = 1;
    public final int TYPE_CITY_DELETED = 2;
    public final int TYPE_CITY_UPDATED = 4;

    public interface CityListDBListener {

        void onCityAdded(long j);

        void onCityDeleted(long j);

        void onCityUpdated(long j);
    }

    public static synchronized CityWeatherDB getInstance(Context context) {
        CityWeatherDB cityWeatherDB;
        synchronized (CityWeatherDB.class) {
            if (mSelf == null) {
                mSelf = new CityWeatherDB(context.getApplicationContext());
            }
            cityWeatherDB = mSelf;
        }
        return cityWeatherDB;
    }

    private CityWeatherDB(Context context) {
        mContext = context;
        mDBHelper = new CityWeatherDBHelper(context);
        mDb = mDBHelper.getWritableDatabase();
        mListenerList = new ArrayList<>();
    }

    public void close() {
        if (mDBHelper != null) {
            mDBHelper.close();
            mDBHelper = null;
            mDb.close();
            mDb = null;
        }
        mSelf = null;
    }

    private SQLiteDatabase getSQLiteDatabase(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new CityWeatherDBHelper(context);
        }
        if (mDb == null) {
            mDb = mDBHelper.getWritableDatabase();
        }
        return mDb;
    }

    private SQLiteDatabase getReadableSQLiteDatabase(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new CityWeatherDBHelper(context);
        }
        if (mReadableDb == null) {
            mReadableDb = mDBHelper.getReadableDatabase();
        }
        return mReadableDb;
    }

    public int getMaxIDValue() {
        Cursor c = getSQLiteDatabase(mContext).query("city", null, null, null,
                null, null, "_id DESC LIMIT 0,1");
        return (c == null || !c.moveToFirst()) ? 0 : c.getInt(c.getColumnIndex("_id"));
    }

    public long addCity(int provider, String name, String displayName, String locationId, String refreshTime) {
        ContentValues values = new ContentValues();
        if (getSize() == 0) {
            values.put("_id", 1);
        }
        values.put(CityListEntry.COLUMN_1_PROVIDER, provider);
        values.put(CityListEntry.COLUMN_2_NAME, name);
        values.put(CityListEntry.COLUMN_3_DISPLAY_NAME, displayName);
        values.put(WeatherEntry.COLUMN_1_LOCATION_ID, locationId);
        values.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, getMaxIDValue() + 1);
        values.put(CityListEntry.COLUMN_10_LAST_REFRESH_TIME, refreshTime);
        long recordId = getSQLiteDatabase(mContext).insert("city", null, values);
        if (recordId >= 0) {
            triggerDataChangeListener(recordId, 1);
        }
        return recordId;
    }

    public long addCity(int provider, String name, String displayName, String locationId, String refreshTime, boolean
            checkUnique) {
        if (checkUnique) {
            Cursor cursor = getLocationId(locationId);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                if (cursor.getCount() != 1) {
                    while (cursor.moveToNext()) {
                        if (cursor.getString(RainSurfaceView.RAIN_LEVEL_DOWNPOUR).equals(name)) {
                            cursor.close();
                            return -1;
                        }
                    }
                } else if (cursor.getString(RainSurfaceView.RAIN_LEVEL_RAINSTORM).equals(locationId)) {
                    cursor.close();
                    return -1;
                }
            }
        }
        return addCity(provider, name, displayName, locationId, refreshTime);
    }

    private Cursor getLocationId(String locationId) {
        return getSQLiteDatabase(mContext).query("city", null, "locationId = " +
                "?", new String[]{locationId}, null, null, "displayOrder ASC", null);
    }

    public long addCity(CityData city, boolean checkUnique) {
        return addCity(city.getProvider(), city.getName(), city.getLocalName(), city.getLocationId(), city
                .getLastRefreshTime(), checkUnique);
    }

    public long updateLastRefreshTime(String locationId, String refreshTime) {
        System.out.println("locationId:" + String.valueOf(locationId));
        ContentValues values = new ContentValues();
        values.put(CityListEntry.COLUMN_10_LAST_REFRESH_TIME, refreshTime);
        long recordId = (long) getSQLiteDatabase(mContext).update("city",
                values, WeatherEntry.COLUMN_1_LOCATION_ID.concat(" = ?"), new String[]{locationId});
        if (recordId >= 0) {
            triggerDataChangeListener(0, RainSurfaceView.RAIN_LEVEL_RAINSTORM);
        }
        return recordId;
    }

    public String getLastRefreshTime(String locationId) {
        String refreshTime = StringUtils.EMPTY_STRING;
        Cursor cursor = getReadableSQLiteDatabase(mContext).query("city", null,
                "locationId = ?", new String[]{locationId}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                refreshTime = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_10_LAST_REFRESH_TIME));
            }
            cursor.close();
        }
        return refreshTime;
    }

    public long addCurrentCity(CityData city) {
        int i;
        long recordId;
        ContentValues values = new ContentValues();
        values.put("_id", 0);
        values.put(CityListEntry.COLUMN_1_PROVIDER, city.getProvider());
        values.put(CityListEntry.COLUMN_2_NAME, city.getName());
        values.put(CityListEntry.COLUMN_3_DISPLAY_NAME, city.getLocalName());
        values.put(WeatherEntry.COLUMN_1_LOCATION_ID, city.getLocationId());
        String str = CityListEntry.COLUMN_9_DISPLAY_ORDER;
        if (city.isDefault()) {
            i = -1;
        } else {
            i = 0;
        }
        values.put(str, i);
        values.put(CityListEntry.COLUMN_10_LAST_REFRESH_TIME, city.getLastRefreshTime());
        if (getCity(0) != null) {
            recordId = (long) getSQLiteDatabase(mContext).update("city",
                    values, "_id" .concat(" = ?"), new String[]{"0"});
            if (recordId >= 0) {
                recordId = 0;
                triggerDataChangeListener(0, RainSurfaceView.RAIN_LEVEL_RAINSTORM);
            }
            if (city.isDefault()) {
                PreferenceUtils.commitString(mContext, CityListActivity.DEFAULT_CITY, city.getLocationId());
            }
        } else {
            values.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, -1);
            recordId = getSQLiteDatabase(mContext).insert("city", null, values);
            if (recordId >= 0) {
                triggerDataChangeListener(0, 1);
                PreferenceUtils.commitString(mContext, CityListActivity.DEFAULT_CITY, city.getLocationId());
            }
        }
        return recordId;
    }

    public long update(String name, CityData city) {
        if (city == null) {
            return 0;
        }
        ContentValues values = new ContentValues();
        if (city.getProvider() > 0) {
            values.put(CityListEntry.COLUMN_1_PROVIDER, city.getProvider());
        }
        if (!TextUtils.isEmpty(city.getName())) {
            values.put(CityListEntry.COLUMN_2_NAME, city.getName());
        }
        if (!TextUtils.isEmpty(city.getLocalName())) {
            values.put(CityListEntry.COLUMN_3_DISPLAY_NAME, city.getLocalName());
        }
        if (!TextUtils.isEmpty(city.getLocationId())) {
            values.put(WeatherEntry.COLUMN_1_LOCATION_ID, city.getLocationId());
        }
        if (!TextUtils.isEmpty(city.getLastRefreshTime())) {
            values.put(CityListEntry.COLUMN_10_LAST_REFRESH_TIME, city.getLastRefreshTime());
        }
        long recordId = (long) getSQLiteDatabase(mContext).update("city",
                values, CityListEntry.COLUMN_3_DISPLAY_NAME.concat(" = ?"), new String[]{String.valueOf(name)});
        if (recordId < 0) {
            return recordId;
        }
        triggerDataChangeListener(0, RainSurfaceView.RAIN_LEVEL_RAINSTORM);
        return recordId;
    }

    public void updateDataToFirst(String locationId) {
        ContentValues values = new ContentValues();
        values.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, -1);
        long recordId = (long) getSQLiteDatabase(mContext).update("city",
                values, WeatherEntry.COLUMN_1_LOCATION_ID.concat(" = ?"), new String[]{String.valueOf(locationId)});
        if (recordId >= 0) {
            triggerDataChangeListener(recordId, RainSurfaceView.RAIN_LEVEL_RAINSTORM);
        }
    }

    public void resetOrder(String locationId) {
        WeatherLog.d("updateDataToFirst, locationId:" + locationId);
        int index_id = getIndexIdFromLocationId(locationId);
        ContentValues values = new ContentValues();
        values.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, index_id);
        getSQLiteDatabase(mContext).update("city", values, WeatherEntry
                .COLUMN_1_LOCATION_ID.concat(" = ?"), new String[]{String.valueOf(locationId)});
    }

    public long addWeather(WeatherData weatherData) {
        long recordId;
        WeatherData oldData = getWeather(weatherData.getLocationId());
        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_1_LOCATION_ID, weatherData.getLocationId());
        values.put(WeatherEntry.COLUMN_2_TIMESTAMP, weatherData.getTimestamp());
        values.put(WeatherEntry.COLUMN_3_TEMPERATURE, weatherData.getCurrentTemp());
        values.put(WeatherEntry.COLUMN_4_REALFEEL_TEMPERATURE, weatherData.getCurrentRealFeelTemp());
        values.put(WeatherEntry.COLUMN_5_HIGH_TEMPERATURE, weatherData.getHighTemp());
        values.put(WeatherEntry.COLUMN_6_LOW_TEMPERATURE, weatherData.getLowTemp());
        values.put(WeatherEntry.COLUMN_7_HUMIDITY, weatherData.getHumidity());
        values.put(WeatherEntry.COLUMN_8_SUNRISE_TIME, weatherData.getSunriseTime());
        values.put(WeatherEntry.COLUMN_9_SUNSET_TIME, weatherData.getSunsetTime());
        values.put(WeatherEntry.COLUMN_10_WEATHER_ID, weatherData.getWeatherDescriptionId());
        if (oldData == null) {
            recordId = getSQLiteDatabase(mContext).insert(WeatherEntry.TABLE_NAME, null, values);
            if (recordId >= 0) {
                return recordId;
            }
        }
        recordId = (long) getSQLiteDatabase(mContext).update(WeatherEntry.TABLE_NAME, values, WeatherEntry
                .COLUMN_1_LOCATION_ID.concat(" = ?"), new String[]{weatherData.getLocationId()});
        if (recordId >= 0) {
        }
        return recordId;
    }

    public long addForecast(String locationId, List<WeatherData> forecastList) {
        long j = -1;
        try {
            getSQLiteDatabase(mContext).beginTransaction();
            getSQLiteDatabase(mContext).delete(ForecastEntry.TABLE_NAME, "locationId = ?", new
                    String[]{locationId});
            for (WeatherData data : forecastList) {
                ContentValues values = new ContentValues();
                values.put(WeatherEntry.COLUMN_1_LOCATION_ID, locationId);
                values.put(WeatherEntry.COLUMN_2_TIMESTAMP, data.getTimestamp());
                values.put(WeatherEntry.COLUMN_5_HIGH_TEMPERATURE, data.getHighTemp());
                values.put(WeatherEntry.COLUMN_6_LOW_TEMPERATURE, data.getLowTemp());
                values.put(WeatherEntry.COLUMN_10_WEATHER_ID, data.getWeatherDescriptionId());
                j = mDb.insert(ForecastEntry.TABLE_NAME, null, values);
                if (j < 0) {
                    throw new SQLiteException();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            mDb.endTransaction();
        }
        return j >= 0 ? j : j;
    }

    public Cursor getCity(String cityName) {
        return getSQLiteDatabase(mContext).query("city", null, "name = ?", new
                String[]{cityName}, null, null, "displayOrder ASC", null);
    }

    public ContentValues getCity(long id) {
        Cursor cursor = getSQLiteDatabase(mContext).query("city", null, "_id = " +
                "?", new String[]{String.valueOf(id)}, null, null, "displayOrder ASC", null);
        ContentValues city = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                city = new ContentValues();
                city.put("_id", cursor.getLong(0));
                city.put(CityListEntry.COLUMN_1_PROVIDER, cursor.getInt(1));
                city.put(CityListEntry.COLUMN_2_NAME, cursor.getString(RainSurfaceView.RAIN_LEVEL_SHOWER));
                city.put(CityListEntry.COLUMN_3_DISPLAY_NAME, cursor.getString(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
                city.put(WeatherEntry.COLUMN_1_LOCATION_ID, cursor.getString(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
                city.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, cursor.getInt(9));
                city.put(CityListEntry.COLUMN_10_LAST_REFRESH_TIME, cursor.getInt(10));
            } else {
                Log.e(TAG, "the id " + id + " is not found in city table");
            }
            cursor.close();
        }
        return city;
    }

    public WeatherData getWeather(String locationId) {
        Cursor cursor = getSQLiteDatabase(mContext).query(WeatherEntry.TABLE_NAME, null, "locationId = ?", new
                String[]{locationId}, null, null, null, null);
        WeatherData weather = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                weather = new WeatherData();
                weather.setLocationId(cursor.getString(1));
                weather.setTimestamp(cursor.getLong(RainSurfaceView.RAIN_LEVEL_SHOWER));
                weather.setCurrentTemp(cursor.getInt(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
                weather.setCurrentRealFeelTemp(cursor.getInt(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
                weather.setHighTemp(cursor.getInt(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER));
                weather.setLowTemp(cursor.getInt(6));
                weather.setHumidity(cursor.getInt(7));
                weather.setSunriseTime((long) cursor.getInt(8));
                weather.setSunsetTime((long) cursor.getInt(9));
                weather.setWeatherDescriptionId(cursor.getInt(10));
            } else {
                Log.i(TAG, "location id " + locationId + " is not found in weather table");
            }
            cursor.close();
        }
        return weather;
    }

    public List<WeatherData> getForecast(String locationId) {
        Cursor cursor = getSQLiteDatabase(mContext).query(ForecastEntry.TABLE_NAME, null, "locationId = ?", new
                String[]{locationId}, null, null, "_id ASC", null);
        List<WeatherData> forecastList = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    WeatherData data = new WeatherData();
                    data.setLocationId(cursor.getString(1));
                    data.setTimestamp((long) cursor.getInt(RainSurfaceView.RAIN_LEVEL_SHOWER));
                    data.setHighTemp(cursor.getInt(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
                    data.setLowTemp(cursor.getInt(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
                    data.setWeatherDescriptionId(cursor.getInt(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER));
                    forecastList.add(data);
                } while (cursor.moveToNext());
            } else {
                Log.i(TAG, "location id " + locationId + " is not found in forecast table");
            }
            cursor.close();
        }
        return forecastList;
    }

    public long getSize() {
        return DatabaseUtils.queryNumEntries(mDb, "city");
    }

    public Cursor getAllCities() {
        return getSQLiteDatabase(mContext).rawQuery("SELECT * FROM city where locationid != 0 ORDER BY " +
                "displayOrder ASC LIMIT 0,8", null);
    }

    public CityData getLocationCity() {
        Cursor cursor = getSQLiteDatabase(mContext).rawQuery("SELECT * FROM city where _id = 0 ORDER BY " +
                "displayOrder ASC LIMIT 0,8", null);
        if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
            return null;
        }
        int provider = cursor.getInt(cursor.getColumnIndex(CityListEntry.COLUMN_1_PROVIDER));
        String cityName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_2_NAME));
        String cityDisplayName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_3_DISPLAY_NAME));
        String cityLocationId = cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_1_LOCATION_ID));
        String index = cursor.getString(cursor.getColumnIndex("_id"));
        cursor.close();
        CityData city = new CityData();
        city.setProvider(provider);
        city.setName(cityName);
        city.setLocalName(cityDisplayName);
        city.setLocationId(cityLocationId);
        city.setDefault(true);
        city.setLocatedCity("0" .equals(index));
        return city;
    }

    public CityData getCityFromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("cursor can't be empty");
        }
        int provider = cursor.getInt(cursor.getColumnIndex(CityListEntry.COLUMN_1_PROVIDER));
        String cityName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_2_NAME));
        String cityDisplayName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_3_DISPLAY_NAME));
        String cityLocationId = cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_1_LOCATION_ID));
        String index = cursor.getString(cursor.getColumnIndex("_id"));
        cursor.close();
        CityData city = new CityData();
        city.setProvider(provider);
        city.setName(cityName);
        city.setLocalName(cityDisplayName);
        city.setLocationId(cityLocationId);
        city.setDefault(true);
        city.setLocatedCity("0" .equals(index));
        return city;
    }

    public CityData getCityFromLocationId(int locationid) {
        Cursor cursor = getSQLiteDatabase(mContext).rawQuery("SELECT * FROM city where locationid =" +
                locationid, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        int provider = cursor.getInt(cursor.getColumnIndex(CityListEntry.COLUMN_1_PROVIDER));
        String cityName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_2_NAME));
        String cityDisplayName = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_3_DISPLAY_NAME));
        String cityLocationId = cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_1_LOCATION_ID));
        cursor.close();
        CityData city = new CityData();
        city.setProvider(provider);
        city.setName(cityName);
        city.setLocalName(cityDisplayName);
        city.setLocationId(cityLocationId);
        return city;
    }

    public int getIndexIdFromLocationId(String locationid) {
        if (TextUtils.isEmpty(locationid)) {
            return -1;
        }
        Cursor cursor = getSQLiteDatabase(mContext).rawQuery("SELECT * FROM city where locationid =" +
                locationid, null);
        if (cursor.getCount() <= 0 || !cursor.moveToFirst()) {
            return -1;
        }
        int i = cursor.getInt(cursor.getColumnIndex("_id"));
        cursor.close();
        return i;
    }

    public List<ContentValues> getAllCityList() {
        Cursor cursor = getSQLiteDatabase(mContext).rawQuery("SELECT * FROM city ORDER BY displayOrder ASC", null);
        ArrayList<ContentValues> cityList = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ContentValues value = new ContentValues();
                    value.put("_id", cursor.getLong(0));
                    value.put(CityListEntry.COLUMN_1_PROVIDER, cursor.getInt(1));
                    value.put(CityListEntry.COLUMN_2_NAME, cursor.getString(RainSurfaceView.RAIN_LEVEL_SHOWER));
                    value.put(CityListEntry.COLUMN_3_DISPLAY_NAME, cursor.getString(RainSurfaceView
                            .RAIN_LEVEL_DOWNPOUR));
                    value.put(WeatherEntry.COLUMN_1_LOCATION_ID, cursor.getString(RainSurfaceView
                            .RAIN_LEVEL_RAINSTORM));
                    value.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, cursor.getInt(9));
                    value.put(CityListEntry.COLUMN_10_LAST_REFRESH_TIME, cursor.getInt(10));
                    cityList.add(value);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return cityList;
    }

    public int deleteCity(long id) {
        int rowAffected = getSQLiteDatabase(mContext).delete("city", "_id = ?",
                new String[]{String.valueOf(id)});
        if (rowAffected > 0) {
            triggerDataChangeListener(id, RainSurfaceView.RAIN_LEVEL_SHOWER);
        }
        return rowAffected;
    }

    public long reOrderAllCities(List<ContentValues> orderedCityList) {
        long j = -1;
        SQLiteDatabase db = getSQLiteDatabase(mContext);
        try {
            db.beginTransaction();
            for (int i = 0; i < orderedCityList.size(); i++) {
                ContentValues values = (ContentValues) orderedCityList.get(i);
                values.remove(CityListEntry.COLUMN_9_DISPLAY_ORDER);
                values.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, i);
                j = (long) db.update("city", values, "_id = ?", new String[]{values
                        .getAsString("_id")});
                if (j <= 0) {
                    throw new SQLiteException();
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            db.endTransaction();
        }
        if (j > 0) {
            triggerDataChangeListener(-1, RainSurfaceView.RAIN_LEVEL_RAINSTORM);
        }
        return j;
    }

    public void addDataChangeListener(CityListDBListener listener) {
        mListenerList.add(listener);
    }

    public void removeDataChangeListener(CityListDBListener listener) {
        mListenerList.remove(listener);
    }

    private void triggerDataChangeListener(long id, int changeType) {
        switch (changeType) {
            case TYPE_CITY_ADDED:
                for (CityListDBListener listener : mListenerList) {
                    listener.onCityAdded(id);
                }
            case TYPE_CITY_DELETED:
                for (CityListDBListener listener2 : mListenerList) {
                    listener2.onCityDeleted(id);
                }
            case TYPE_CITY_UPDATED:
                for (CityListDBListener listener22 : mListenerList) {
                    listener22.onCityUpdated(id);
                }
            default:
                break;
        }
    }

    public void changeDefaultCity(int position) {
        Cursor cursor = getAllCities();
        String cityLocationId = StringUtils.EMPTY_STRING;
        if (cursor.getCount() > position) {
            cursor.moveToPosition(position);
            cityLocationId = cursor.getString(RainSurfaceView.RAIN_LEVEL_RAINSTORM);
            SystemSetting.setLocationOrDefaultCity(mContext, getCityFromCursor(cursor));
        }
        resetOrder(PreferenceUtils.getString(mContext, CityListActivity.DEFAULT_CITY, StringUtils.EMPTY_STRING));
        updateDataToFirst(cityLocationId);
        PreferenceUtils.commitString(mContext, CityListActivity.DEFAULT_CITY, cityLocationId);
    }
}
