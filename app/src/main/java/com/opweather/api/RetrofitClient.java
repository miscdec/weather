package com.opweather.api;

public class RetrofitClient {

  /*  //public static final String HOST = "https://free-api.heweather.com/s6/weather/";
    public static final String HOST = "http://i1.weather.oppomobile.com/chinaWeather/";
    private static ApiService apiService;
    protected static final Object monitor = new Object();
    private static Retrofit retrofit;

    private RetrofitClient() {

    }


    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new GzipRequestInterceptor());
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
    }*/
}
