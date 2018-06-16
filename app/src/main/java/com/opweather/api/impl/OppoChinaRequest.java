package com.opweather.api.impl;

import com.opweather.api.CommonConfig;
import com.opweather.api.WeatherRequest;
import com.opweather.api.parser.OppoChinaResponseParserV3;
import com.opweather.api.parser.ResponseParser;
import com.opweather.api.WeatherResponse.CacheListener;
import com.opweather.api.WeatherResponse.NetworkListener;

public class OppoChinaRequest extends WeatherRequest {
    public static final String DATA_SOURCE_NAME = "Oppo.China";

    public OppoChinaRequest(String key) {
        super(key);
    }

    public OppoChinaRequest(int type, String key) {
        super(type, key);
    }

    public OppoChinaRequest(String key, NetworkListener networkListener, CacheListener cacheListener) {
        super(key, networkListener, cacheListener);
    }

    public OppoChinaRequest(int type, String key, NetworkListener networkListener, CacheListener cacheListener) {
        super(type, key, networkListener, cacheListener);
    }

    @Override
    public String getRequestUrl(int type) {
        return type == 16 ? null : CommonConfig.getOppoChinaUrl(getRequestKey());
    }

    @Override
    public ResponseParser getResponseParser() {
        return new OppoChinaResponseParserV3(this);
    }

    @Override
    public String getMemCacheKey() {
        return "Oppo.China#" + getRequestKey();
    }

    @Override
    public String getDiskCacheKey(int type) {
        return "Oppo.China#" + getRequestKey();
    }
}
