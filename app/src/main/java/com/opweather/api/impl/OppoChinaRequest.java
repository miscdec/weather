package com.opweather.api.impl;

import com.opweather.api.CommonConfig;
import com.opweather.api.WeatherRequest;
import com.opweather.opapi.ResponseParser;
import com.opweather.opapi.WeatherResponse.CacheListener;
import com.opweather.opapi.WeatherResponse.NetworkListener;

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

    public String getRequestUrl(int type) {
        return type == 16 ? null : CommonConfig.getOppoChinaUrl(getRequestKey());
    }

    @Override
    public ResponseParser getResponseParser() {
        return null;
    }

   /* public ResponseParser getResponseParser() {
        return new OppoChinaResponseParserV3(this);
    }*/

    public String getMemCacheKey() {
        return "Oppo.China#" + getRequestKey();
    }

    public String getDiskCacheKey(int type) {
        return "Oppo.China#" + getRequestKey();
    }
}
