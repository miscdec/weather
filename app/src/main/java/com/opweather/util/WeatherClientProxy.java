package com.opweather.util;

import android.content.Context;
import android.text.TextUtils;

import com.opweather.api.WeatherClient;
import com.opweather.api.WeatherRequest;
import com.opweather.api.impl.AccuRequest;
import com.opweather.api.impl.OppoChinaRequest;
import com.opweather.api.impl.OppoForeignRequest;
import com.opweather.bean.CityData;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.WeatherException;
import com.opweather.api.WeatherResponse;
import com.opweather.widget.openglbase.RainSurfaceView;


public class WeatherClientProxy {
    private CacheMode mCacheMode;
    private final Context mContext;

    public enum CacheMode {
        LOAD_DEFAULT,
        LOAD_CACHE_ELSE_NETWORK,
        LOAD_NO_CACHE,
        LOAD_CACHE_ONLY
    }

    public interface OnResponseListener {
        void onCacheResponse(RootWeather rootWeather);

        void onErrorResponse(WeatherException weatherException);

        void onNetworkResponse(RootWeather rootWeather);
    }

    private class InnerOldListenerSupport implements WeatherResponse.NetworkListener, WeatherResponse.CacheListener {
        private final OnResponseListener mListener;

        InnerOldListenerSupport(OnResponseListener listener) {
            mListener = listener;
        }

        public void onResponse(RootWeather weather) {
            mListener.onCacheResponse(weather);
        }

        public void onResponseSuccess(RootWeather weather) {
            mListener.onNetworkResponse(weather);
        }

        public void onResponseError(WeatherException e) {
            mListener.onErrorResponse(e);
        }
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

    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    public void requestWeatherInfo(int type, CityData city, OnResponseListener listener) {
        InnerOldListenerSupport listenerSupport = new InnerOldListenerSupport(listener);
        WeatherRequest request = getRequest(city, type);
        request.setCacheListener(listenerSupport);
        request.setNetworkListener(listenerSupport);
        switch (mCacheMode.ordinal()) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                request.setCacheMode(WeatherRequest.CacheMode.LOAD_DEFAULT);
                break;
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                request.setCacheMode(WeatherRequest.CacheMode.LOAD_CACHE_ELSE_NETWORK);
                break;
            case RainSurfaceView.RAIN_LEVEL_DOWNPOUR:
                request.setCacheMode(WeatherRequest.CacheMode.LOAD_CACHE_ONLY);
                break;
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                request.setCacheMode(WeatherRequest.CacheMode.LOAD_NO_CACHE);
                break;
        }
        WeatherClient.getInstance(mContext).execute(request);
    }

    public void requestWeatherInfo(CityData city, OnResponseListener listener) {
        requestWeatherInfo(16, city, listener);
    }

    public static boolean isValidable(Context context, RootWeather weather) {
        if (weather == null) {
            return false;
        }
        if (WeatherResHelper.weatherToResID(context, weather.getCurrentWeatherId()) == 9999) {
            return false;
        }
        if (TextUtils.isEmpty(weather.getCurrentWeatherText(context))) {
            return false;
        }
        if (weather.getTodayCurrentTemp() == Integer.MIN_VALUE) {
            return false;
        }
        if (weather.getTodayHighTemperature() == Integer.MIN_VALUE) {
            return false;
        }
        return weather.getTodayLowTemperature() != Integer.MIN_VALUE;
    }

    public static boolean needPullWeather(Context context, String cityId, RootWeather weather) {
        return !isValidable(context, weather) || DateTimeUtils.isNeedUpdateWeather(context, cityId);
    }

    private WeatherRequest getRequest(CityData city, int type) {
        if (city.getProvider() == 4096) {
            return new OppoChinaRequest(47, city.getLocationId(), null, null);
        }
        return city.getProvider() == 8192 ? new OppoForeignRequest(47, city.getLocationId(), null, null) : new
                AccuRequest(47, city.getLocationId(), null, null);
    }
}
