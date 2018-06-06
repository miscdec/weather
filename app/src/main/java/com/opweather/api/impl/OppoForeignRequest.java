package com.opweather.api.impl;

import com.opweather.api.CommonConfig;
import com.opweather.api.WeatherRequest;
import com.opweather.api.parser.ResponseParser;
import com.opweather.api.WeatherResponse.CacheListener;
import com.opweather.api.WeatherResponse.NetworkListener;

public class OppoForeignRequest extends WeatherRequest {
    public static final String DATA_SOURCE_NAME = "Oppo.Foreign";

    public OppoForeignRequest(String key) {
        super(key);
    }

    public OppoForeignRequest(int type, String key) {
        super(type, key);
    }

    public OppoForeignRequest(String key, NetworkListener networkListener, CacheListener cacheListener) {
        super(key, networkListener, cacheListener);
    }

    public OppoForeignRequest(int type, String key, NetworkListener networkListener, CacheListener cacheListener) {
        super(type, key, networkListener, cacheListener);
    }

    public String getRequestUrl(int type) {
        return (type == 8 || type == 2 || type == 16) ? null : CommonConfig.getOppoForeignUrl(getRequestKey());
    }

    public ResponseParser getResponseParser() {
        //return new OppoForeignResponseParser(this);
        return null;
    }

    public String getMemCacheKey() {
        return "Oppo.Foreign#" + getRequestKey();
    }

    public String getDiskCacheKey(int type) {
        return "Oppo.Foreign#" + getRequestKey();
    }
}
