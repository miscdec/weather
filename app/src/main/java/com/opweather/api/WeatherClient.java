package com.opweather.api;

import android.content.Context;

import com.opweather.api.helper.NetworkHelper;
import com.opweather.api.impl.WeatherRequestExecuter;
import com.opweather.api.cache.WeatherCache;

public class WeatherClient {
    private static WeatherClient sInstance;
    private Context mContext;

    public static WeatherClient getInstance(Context context) {
        if (context == null) {
            throw new NullPointerException("Context should not be null!");
        }
        if (sInstance == null) {
            synchronized (WeatherClient.class) {
                if (sInstance == null) {
                    sInstance = new WeatherClient(context);
                }
            }
        }
        return sInstance;
    }

    private WeatherClient(Context context) {
        mContext = context.getApplicationContext();
        WeatherCache.getInstance(mContext);
    }

    public void execute(WeatherRequest request) {
        setDefaultLocale(request);
        new WeatherRequestExecuter(mContext).execute(request);
    }

    private void setDefaultLocale(WeatherRequest request) {
        if (request.getLocale() == null) {
            request.setLocale(mContext.getResources().getConfiguration().locale);
        }
    }

    public void cancelAll() {
        NetworkHelper.getInstance(mContext).cancelAll();
    }
}
