package com.opweather.api.impl;

import android.content.Context;
import android.os.AsyncTask;
import com.opweather.api.RequestExecuter;
import com.opweather.api.WeatherRequest;
import com.opweather.api.cache.Cache;
import com.opweather.api.cache.WeatherCache;

public abstract class AbstractExecuter implements RequestExecuter {

    private class CacheAsyncTask extends AsyncTask<byte[], Void, Void> {
        private Context mContext;
        private String mUrl;

        public CacheAsyncTask(Context context, String url) {
            mContext = context;
            mUrl = url;
        }

        @Override
        protected Void doInBackground(byte[]... bytes) {
            byte[] data = bytes[0];
            Cache cache = WeatherCache.getInstance(mContext);
            cache.putToDisk(mUrl, data);
            cache.flush();
            return null;
        }
    }

    public abstract void execute(WeatherRequest weatherRequest);

    protected void addToDiskCache(Context context, String key, byte[] value) {
        new CacheAsyncTask(context, key).execute(value);
    }

    protected void removeWeatherTimeDiskCache(Context context, String key, byte[] value) {
        new CacheAsyncTask(context, key).execute(value);
    }
}
