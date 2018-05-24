package com.opweather.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lyh on 3/12.
 */

public class RetrofitClient {

    public static final String HOST = "https://free-api.heweather.com/s6/weather/";
    private static ApiService apiService;
    protected static final Object monitor = new Object();
    private static Retrofit retrofit;

    private RetrofitClient() {

    }


    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();
    }


    public static ApiService getRetrofitClientInstance() {
        synchronized (monitor) {
            if (apiService == null) {
                apiService = retrofit.create(ApiService.class);
            }
            return apiService;
        }
    }
}
