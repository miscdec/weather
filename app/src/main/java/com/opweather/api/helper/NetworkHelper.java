package com.opweather.api.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.opweather.api.WeatherException;

public class NetworkHelper {
    private static final String WEATHER_REQUESTS = "CustomTags:WEATHER_REQUESTS";
    private static boolean ENABLE_HTTP_CACHE;
    private static final Object classLock;
    private static NetworkHelper sInstance;
    private Context mContext;
    private RequestQueue mRequestQueue;

    public interface ResponseListener {
        void onError(WeatherException weatherException);

        void onResponse(byte[] bArr, String str);
    }

    class AnonymousClass_1 implements Response.ErrorListener {
        final /* synthetic */ ResponseListener val$innerListener;

        AnonymousClass_1(ResponseListener responseListener) {
            this.val$innerListener = responseListener;
        }

        public void onErrorResponse(VolleyError error) {
            if (this.val$innerListener != null) {
                this.val$innerListener.onError(new WeatherException(error.getMessage()));
            }
        }
    }

    static {
        ENABLE_HTTP_CACHE = true;
        classLock = NetworkHelper.class;
    }

    public static NetworkHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (classLock) {
                if (sInstance == null) {
                    sInstance = new NetworkHelper(context);
                }
            }
        }
        return sInstance;
    }

    private NetworkHelper(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void get(String url, ResponseListener listener) {
        get(url, listener, ENABLE_HTTP_CACHE);
    }

    public void get(String url, ResponseListener listener, boolean useHttpCache) {
        ResponseListener innerListener = listener;
        if (!StringUtils.isBlank(url)) {
            ByteArrayRequest request = new ByteArrayRequest(url, false, innerListener, new AnonymousClass_1(innerListener));
            request.setShouldCache(useHttpCache);
            request.setTag(WEATHER_REQUESTS);
            addToRequestQueue(request);
        } else if (innerListener != null) {
            innerListener.onError(new WeatherException("Url is null, not support request type!"));
        }
    }

    public void cancelAll() {
        this.mRequestQueue.cancelAll(WEATHER_REQUESTS);
    }
}
