package com.opweather.api.helper;

import com.android.volley.Cache.Entry;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

public class OppoHttpHeaderParser extends HttpHeaderParser {
    public static final long CACHE_TIME = 30000;

    public static Entry parseCacheHeaders(NetworkResponse response, long cacheTime) {
        Entry entry = parseCacheHeaders(response);
        if (entry == null) {
            return null;
        }
        entry.softTtl = System.currentTimeMillis() + cacheTime;
        entry.ttl = entry.softTtl;
        return entry;
    }
}
