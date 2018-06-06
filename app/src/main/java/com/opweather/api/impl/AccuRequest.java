package com.opweather.api.impl;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.opweather.api.CommonConfig;
import com.opweather.api.WeatherRequest;
import com.opweather.api.WeatherResponse;
import com.opweather.api.helper.LogUtils;
import com.opweather.api.parser.AccuResponseParser;
import com.opweather.api.parser.ResponseParser;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.Locale;

public class AccuRequest extends WeatherRequest {
    private static final Locale ACCU_DEFAULT_LOCALE;
    public static final String DATA_SOURCE_NAME = "Accu";

    static {
        ACCU_DEFAULT_LOCALE = Locale.US;
    }

    public AccuRequest(String key) {
        super(key);
    }

    public AccuRequest(int type, String key) {
        super(type, key);
    }

    public AccuRequest(String key, WeatherResponse.NetworkListener networkListener, WeatherResponse.CacheListener
            cacheListener) {
        super(key, networkListener, cacheListener);
    }

    public AccuRequest(int type, String key, WeatherResponse.NetworkListener networkListener, WeatherResponse
            .CacheListener cacheListener) {
        super(type, key, networkListener, cacheListener);
    }

    public String getRequestUrl(int type) {
        switch (type) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                return CommonConfig.getAccuCurrentUrl(getRequestKey(), getAccuLocal());
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                return CommonConfig.getAccuDailyForecastsUrl(getRequestKey(), getAccuLocal());
            case ItemTouchHelper.END:
                return CommonConfig.getAccuAlertsUrl(getRequestKey(), getAccuLocal());
            default:
                return null;
        }
    }

    public ResponseParser getResponseParser() {
        return new AccuResponseParser(getRequestKey());
    }

    public String getMemCacheKey() {
        return "Accu#" + getRequestKey();
    }

    public String getDiskCacheKey(int type) {
        return getRequestUrl(type);
    }

    public String getAccuLocal() {
        Locale locale = getLocale(ACCU_DEFAULT_LOCALE);
        LogUtils.d("AccuLocal", locale.toString(), new Object[0]);
        if (!locale.getLanguage().equalsIgnoreCase("zh")) {
            locale = Locale.US;
        }
        return locale.getLanguage().concat("-").concat(locale.getCountry());
    }
}
