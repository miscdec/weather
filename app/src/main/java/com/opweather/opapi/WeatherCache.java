package com.opweather.opapi;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.opweather.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.internal.cache.DiskLruCache;

public class WeatherCache implements Cache {
    private static final int DEFAULT_DISK_CACHE_SIZE = 1048576;
    private static final int DEFAULT_MEM_CACHE_SIZE = 8;
    private static final int DISK_CACHE_INDEX = 0;
    private static final String TAG = "WeatherCache";
    private static final int VERSION_CODE = 1;
    private static final String WEATHER_CACHE_DIR = "weather";
    private static final Object classLock;
    private static final Object mDiskCacheLock;
    private static WeatherCache sInstance;
    private Context mContext;
    private boolean mDiskCacheStarting;
    private DiskLruCache mDiskLruCache;
    private LruCache<String, RootWeather> mMemoryCache;

    private class CacheAsyncTask extends AsyncTask<Void, Void, Void> {
        private CacheAsyncTask() {
        }

        protected Void doInBackground(Void... params) {
            try {
            } catch (Exception e) {
                Log.d(TAG, "failed to write disk");
            }
            return null;
        }
    }

    static {
        classLock = WeatherCache.class;
        mDiskCacheLock = new Object();
    }

    public static WeatherCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (classLock) {
                if (sInstance == null) {
                    sInstance = new WeatherCache(context);
                }
            }
        }
        return sInstance;
    }

    private WeatherCache(Context context) {
        this.mDiskCacheStarting = true;
        this.mContext = context.getApplicationContext();
        init();
    }

    private void init() {
        LogUtils.d(TAG, "Memory cache created (size = 8)", new Object[0]);
        this.mMemoryCache = new LruCache(8);
        initDiskCacheBackground();
    }

    private void initDiskCacheBackground() {
        new CacheAsyncTask().execute(new Void[0]);
    }

    public static long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= 9) {
            return path.getUsableSpace();
        }
        StatFs stats = new StatFs(path.getPath());
        return ((long) stats.getBlockSize()) * ((long) stats.getAvailableBlocks());
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = context.getCacheDir().getPath();
        if ("mounted".equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()) {
            File ext = getExternalCacheDir(context);
            if (ext != null) {
                cachePath = ext.getPath();
            }
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(9)
    public static boolean isExternalStorageRemovable() {
        return Build.VERSION.SDK_INT >= 9 ? Environment.isExternalStorageRemovable() : true;
    }

    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= 8) {
            return context.getExternalCacheDir();
        }
        return new File(Environment.getExternalStorageDirectory().getPath() + ("/Android/data/" + context
                .getPackageName() + "/cache/"));
    }

    public static String hashKeyForDisk(String key) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            return bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(key.hashCode());
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = DISK_CACHE_INDEX; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 255);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    public void clear() {
        if (this.mMemoryCache != null) {
            this.mMemoryCache.evictAll();
            LogUtils.d(TAG, "Memory cache cleared", new Object[0]);
        }
        synchronized (mDiskCacheLock) {
            this.mDiskCacheStarting = true;
            if (!(this.mDiskLruCache == null || this.mDiskLruCache.isClosed())) {
                try {
                    this.mDiskLruCache.delete();
                    LogUtils.d(TAG, "Disk cache cleared", new Object[0]);
                } catch (IOException e) {
                    LogUtils.e(TAG, "clear - " + e, new Object[0]);
                }
                this.mDiskLruCache = null;
            }
        }
    }

    public void flush() {
        synchronized (mDiskCacheLock) {
            if (this.mDiskLruCache != null) {
                try {
                    this.mDiskLruCache.flush();
                    LogUtils.d(TAG, "Disk cache flushed", new Object[0]);
                } catch (IOException e) {
                    LogUtils.e(TAG, "flush - " + e, new Object[0]);
                }
            }
        }
    }

    @Override
    public byte[] getFromDiskCache(String str) {
        return new byte[0];
    }

    @Override
    public RootWeather getFromMemCache(String str) {
        return null;
    }

    @Override
    public void putToDisk(String str, byte[] bArr) {

    }

    @Override
    public void putToMemory(String str, RootWeather rootWeather) {

    }

    public void close() {
        synchronized (mDiskCacheLock) {
            if (this.mDiskLruCache != null) {
                try {
                    if (!this.mDiskLruCache.isClosed()) {
                        this.mDiskLruCache.close();
                        this.mDiskLruCache = null;
                        LogUtils.d(TAG, "Disk cache closed", new Object[0]);
                    }
                } catch (IOException e) {
                    LogUtils.e(TAG, "close - " + e, new Object[0]);
                }
            }
        }
    }
}
