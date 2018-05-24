package com.opweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.opweather.db.CityWeatherDBHelper.*;

/**
 * Created by leeyh on 5/15.
 */
public class CityWeatherDB {
    private static final String CITY = "city";
    private static CityWeatherDB mSelf;
    private Context mContext;
    private CityWeatherDBHelper mDBHelper;
    private SQLiteDatabase mDb;
    //private List<CityListDBListener> mListenerList;
    private SQLiteDatabase mReadableDb;

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
        if (this.mDBHelper == null) {
            this.mDBHelper = new CityWeatherDBHelper(context);
        }
        if (this.mReadableDb == null) {
            this.mReadableDb = this.mDBHelper.getReadableDatabase();
        }
        return this.mReadableDb;
    }

    private SQLiteDatabase getReadableSQLiteDatabase(Context context) {
        if (this.mDBHelper == null) {
            this.mDBHelper = new CityWeatherDBHelper(context);
        }
        if (this.mReadableDb == null) {
            this.mReadableDb = this.mDBHelper.getReadableDatabase();
        }
        return this.mReadableDb;
    }

    public int getMaxIDValue() {
        Cursor c = getSQLiteDatabase(mContext).query("city", null, null, null, null, null, "_id DESC LIMIT 0,1");
        return (c == null || !c.moveToFirst()) ? 0 : c.getInt(c.getColumnIndex("_id"));
    }

    public long addCity(int provider, String name, String displayName, String locationId, String refreshTime){
        ContentValues values = new ContentValues();
       /* if (getSize() == 0) {
            values.put("_id", Integer.valueOf(1));
        }*/
        values.put(CityListEntry.COLUMN_1_PROVIDER, Integer.valueOf(provider));
        values.put(CityListEntry.COLUMN_2_NAME, name);
        values.put(CityListEntry.COLUMN_3_DISPLAY_NAME, displayName);
        values.put(WeatherEntry.COLUMN_1_LOCATION_ID, locationId);
        values.put(CityListEntry.COLUMN_9_DISPLAY_ORDER, Integer.valueOf(getMaxIDValue() + 1));
        values.put(CityListEntry.COLUMN_10_LAST_REFRESH_TIME, refreshTime);
        long recordId = getSQLiteDatabase(this.mContext).insert(CITY, null, values);
        if (recordId >= 0) {
            //triggerDataChangeListener(recordId, 1);
        }
        return recordId;
    }


    private Cursor getLocationId(String locationId) {
        return getSQLiteDatabase(this.mContext).query(CITY, null, "locationId = ?", new String[]{locationId}, null, null, "displayOrder ASC", null);
    }

    public String getLastRefreshTime(String locationId) {
        String refreshTime = "";
        Cursor cursor = getReadableSQLiteDatabase(this.mContext).query(CITY, null, "locationId = ?", new String[]{locationId}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                refreshTime = cursor.getString(cursor.getColumnIndex(CityListEntry.COLUMN_10_LAST_REFRESH_TIME));
            }
            cursor.close();
        }
        return refreshTime;
    }



}
