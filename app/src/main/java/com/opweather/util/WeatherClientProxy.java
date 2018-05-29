package com.opweather.util;

import android.content.Context;
import android.text.TextUtils;

import com.opweather.bean.CityData;
import com.opweather.opapi.RootWeather;
import com.opweather.opapi.WeatherException;
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

    private class InnerOldListenerSupport implements NetworkListener, CacheListener {
        private final OnResponseListener mListener;

        InnerOldListenerSupport(OnResponseListener listener) {
            this.mListener = listener;
        }

        public void onResponse(RootWeather weather) {
            this.mListener.onCacheResponse(weather);
        }

        public void onResponseSuccess(RootWeather weather) {
            this.mListener.onNetworkResponse(weather);
        }

        public void onResponseError(WeatherException e) {
            this.mListener.onErrorResponse(e);
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
        switch (AnonymousClass_1.$SwitchMap$net$oneplus$weather$util$WeatherClientProxy$CacheMode[this.mCacheMode.ordinal()]) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                request.setCacheMode(cn.oneplus.weather.api.WeatherRequest.CacheMode.LOAD_DEFAULT);
                break;
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                request.setCacheMode(cn.oneplus.weather.api.WeatherRequest.CacheMode.LOAD_CACHE_ELSE_NETWORK);
                break;
            case RainSurfaceView.RAIN_LEVEL_DOWNPOUR:
                request.setCacheMode(cn.oneplus.weather.api.WeatherRequest.CacheMode.LOAD_CACHE_ONLY);
                break;
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                request.setCacheMode(cn.oneplus.weather.api.WeatherRequest.CacheMode.LOAD_NO_CACHE);
                break;
        }
        WeatherClient.getInstance(this.mContext).execute(request);
    }

    public void requestWeatherInfo(CityData city, OnResponseListener listener) {
        requestWeatherInfo(WeatherCircleView.DIN_ANGEL, city, listener);
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
        return city.getProvider() == 8192 ? new OppoForeignRequest(47, city.getLocationId(), null, null) : new AccuRequest(47, city.getLocationId(), null, null);
    }
}
