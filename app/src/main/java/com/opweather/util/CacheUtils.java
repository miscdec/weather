package com.opweather.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class CacheUtils {
    public static final int CONFIG_CACHE_MOBILE_TIMEOUT = 7200000;
    public static final int CONFIG_CACHE_WIFI_TIMEOUT = 1800000;

    public static String getUrlCache(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int networkState = NetUtil.getNetworkState(context);
        File file = new File(context.getCacheDir() + File.separator + replaceUrlWithPlus(url));
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        long expiredTime = System.currentTimeMillis() - file.lastModified();
        Log.i("lwp", url + ": expiredTime=" + (expiredTime / 1000));
        if (networkState != 0 && expiredTime < 0) {
            return null;
        }
        if (networkState == 1 && expiredTime > 1800000) {
            return null;
        }
        if (networkState == 2 && expiredTime > 7200000) {
            return null;
        }
        try {
            return FileUtils.readTextFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setUrlCache(Context context, String data, String url) {
        if (context.getCacheDir() != null) {
            try {
                FileUtils.writeTextFile(new File(context.getCacheDir() + File.separator + replaceUrlWithPlus(url)), data);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static void clearCache(Context context, File cacheFile) {
        if (cacheFile == null) {
            try {
                File cacheDir = context.getCacheDir();
                if (cacheDir.exists()) {
                    clearCache(context, cacheDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cacheFile.isFile()) {
            cacheFile.delete();
        } else if (cacheFile.isDirectory()) {
            File[] files = cacheFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    clearCache(context, file);
                }
            }
        }
    }

    public static String replaceUrlWithPlus(String url) {
        return url != null ? url.replaceAll("http://(.)*?/", StringUtils.EMPTY_STRING).replaceAll("[.:/,%?&= ]", "+").replaceAll("[+]+", "+") : null;
    }
}
