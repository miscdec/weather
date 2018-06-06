package com.opweather.api.cache;

import com.opweather.api.nodes.RootWeather;

public interface Cache {
    void clear();

    void close();

    void flush();

    byte[] getFromDiskCache(String str);

    RootWeather getFromMemCache(String str);

    void putToDisk(String str, byte[] bArr);

    void putToMemory(String str, RootWeather rootWeather);
}
