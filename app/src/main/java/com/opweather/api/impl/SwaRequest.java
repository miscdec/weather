package com.opweather.api.impl;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.opweather.api.CommonConfig;
import com.opweather.api.WeatherRequest;
import com.opweather.api.WeatherResponse.CacheListener;
import com.opweather.api.WeatherResponse.NetworkListener;
import com.opweather.api.parser.ResponseParser;
import com.opweather.api.parser.SwaResponseParser;
import com.opweather.widget.openglbase.RainSurfaceView;

public class SwaRequest extends WeatherRequest {
    public static final String DATA_SOURCE_NAME = "HuaFeng";

    public SwaRequest(String key) {
        super(key);
    }

    public SwaRequest(int type, String key) {
        super(type, key);
    }

    public SwaRequest(String key, NetworkListener networkListener, CacheListener cacheListener) {
        super(key, networkListener, cacheListener);
    }

    public SwaRequest(int type, String key, NetworkListener networkListener, CacheListener cacheListener) {
        super(type, key, networkListener, cacheListener);
    }

    public String getRequestUrl(int type) {
        switch (type) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                return CommonConfig.getSwaCurrentUrl(getRequestKey());
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                return CommonConfig.getSwaHourForecastsUrl(getRequestKey());
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                return CommonConfig.getSwaDailyForecastsUrl(getRequestKey());
            case 8:
                return CommonConfig.getSwaAqiUrl(getRequestKey());
            case 16:
                return CommonConfig.getSwaIndexUrl(getRequestKey());
            case ItemTouchHelper.END:
                return CommonConfig.getSwaAlertsUrl(getRequestKey());
            default:
                return null;
        }
    }

    public ResponseParser getResponseParser() {
        return new SwaResponseParser(getRequestKey());
    }

    public String getMemCacheKey() {
        return "HuaFeng#" + getRequestKey();
    }

    public String getDiskCacheKey(int type) {
        return "HuaFeng#" + getRequestKey() + "." + type;
    }
}
