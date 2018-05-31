package com.opweather.util;

import android.content.Context;
import android.text.TextUtils;

import com.opweather.bean.CityData;
import com.opweather.opapi.RootWeather;
import com.opweather.opapi.WeatherException;
import com.opweather.opapi.WeatherRequest;
import com.opweather.widget.WeatherCircleView;
import com.opweather.widget.openglbase.RainSurfaceView;

public class WeatherClientProxy {
    private CacheMode mCacheMode;
    private  Context mContext;

    public interface OnResponseListener {
        void onCacheResponse(RootWeather rootWeather);

        void onErrorResponse(WeatherException weatherException);

        void onNetworkResponse(RootWeather rootWeather);
    }

    public enum CacheMode {
        LOAD_DEFAULT,
        LOAD_CACHE_ELSE_NETWORK,
        LOAD_NO_CACHE,
        LOAD_CACHE_ONLY
    }

    public WeatherClientProxy(Context context) {
        mCacheMode = CacheMode.LOAD_DEFAULT;
        mContext = context;
    }

    public WeatherClientProxy setCacheMode(CacheMode mode) {
        if (mode == null) {
            throw new NullPointerException("mode should not be null.");
        }
        mCacheMode = mode;
        return this;
    }



}
