package com.opweather.provider.apihelper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.opweather.bean.CandidateCity;
import com.opweather.bean.CityData;
import com.opweather.bean.CommonCandidateCity;
import com.opweather.bean.LocationData;
import com.opweather.constants.GlobalConfig;
import com.opweather.provider.CitySearchProvider;
import com.opweather.provider.LocationProvider;
import com.opweather.util.CacheUtils;
import com.opweather.util.EncodeUtil;
import com.opweather.util.NetUtil;
import com.opweather.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccuWeatherHelper implements IWeatherAPIHelper, ICitySearchAPIHelper {
    private static final String API_KEY = "eey3z2dBNI896hIG08j7q1uxXzTxJqkZ";
    private static final String API_URL = "http://api.accuweather.com/";
    public static final String LOCATION_DATA = "locations";
    private static final String TAG;
    private static final String VERSION = "v1";
    private static final String WEATHER_FILE_LOCATION_DATA = "_location_data";
    private static final int WEATHER_TYPE_LOCATION_DATA = 8388608;
    private String mLocale;
    private LocationProvider.OnLocationListener mOnLocationListener;
    private Handler mProviderHandler;

    private class JudgeTask extends AsyncTask<Integer, Void, Integer> {
        private CityData mCity;
        private Context mContext;

        public JudgeTask(Context context, CityData city) {
            this.mContext = context;
            this.mCity = city;
        }

        protected Integer doInBackground(Integer... params) {
            return AccuWeatherHelper.this.executeTask(this.mContext, this.mCity, params[0].intValue()) ? Integer.valueOf(params[0].intValue() | 1073741824) : Integer.valueOf(params[0].intValue() | Integer.MIN_VALUE);
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if ((result.intValue() & Integer.MIN_VALUE) == Integer.MIN_VALUE) {
                AccuWeatherHelper.this.mProviderHandler.sendEmptyMessage(CitySearchProvider.GET_SEARCH_RESULT_FAIL);
                return;
            }
            switch (result.intValue() & -1073741825) {
                case WEATHER_TYPE_LOCATION_DATA:
                    LocationData ld = AccuWeatherParser.parseLocation(CacheUtils.getUrlCache(this.mContext, this.mCity.getLocalName() + WEATHER_FILE_LOCATION_DATA));
                    if (ld.getCountry().getID().equals("CN")) {
                        String str = StringUtils.EMPTY_STRING;
                        if (ld.getSupplementalAdminAreas() == null || ld.getSupplementalAdminAreas().length <= 0) {
                            str = ld.getAdministrativeArea().getEnglishName() + " " + ld.getAdministrativeArea().getEnglishName();
                        } else {
                            str = ld.getAdministrativeArea().getEnglishName() + " " + ld.getSupplementalAdminAreas()[0].getEnglishName();
                        }
                        if (!str.equals(StringUtils.EMPTY_STRING) && str.split(" ").length > 0) {
                            AccuWeatherHelper.this.mProviderHandler.sendMessage(AccuWeatherHelper.this.getMessage(GlobalConfig.MESSAGE_ACCU_GET_COUNTRY_CHINA, str));
                            return;
                        }
                        return;
                    }
                    CityData city = new CityData();
                    city.setName(ld.getEnglishName());
                    city.setLocalName(ld.getLocalizedName());
                    city.setLatitude(ld.getGeoPosition().getLatitude());
                    city.setLongitude(ld.getGeoPosition().getLongitude());
                    city.setLocationId(ld.getKey());
                    city.setLocatedCity(true);
                    city.setProvider(CitySearchProvider.PROVIDER_ACCU_WEATHER);
                    if (TextUtils.isEmpty(city.getLocalName())) {
                        city.setLocalName(city.getName());
                    }
                    if (AccuWeatherHelper.this.mOnLocationListener != null) {
                        AccuWeatherHelper.this.mOnLocationListener.onLocationChanged(city);
                    }
                default:
                    break;
            }
        }
    }

    private class SearchCityTask extends AsyncTask<Void, Void, Boolean> {
        private final String API_RUL;
        private ArrayList<CandidateCity> mCandidateList;
        private Context mContext;
        private String mKeyword;
        private String mLocale;

        public SearchCityTask(Context context) {
            this.API_RUL = "http://api.accuweather.com/locations/v1/cities/autocomplete.json?apikey=eey3z2dBNI896hIG08j7q1uxXzTxJqkZ";
            this.mContext = context;
        }

        public void setKeyword(String keyword) {
            try {
                this.mKeyword = URLEncoder.encode(keyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public void setLocale(String locale) {
            this.mLocale = locale;
        }

        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Get city list from url http://api.accuweather.com/locations/v1/cities/autocomplete.json?apikey=eey3z2dBNI896hIG08j7q1uxXzTxJqkZ&language=" + this.mLocale + "&q=" + this.mKeyword);
            this.mCandidateList = null;
            try {
                String strResult = NetUtil.httpGet("http://api.accuweather.com/locations/v1/cities/autocomplete.json?apikey=eey3z2dBNI896hIG08j7q1uxXzTxJqkZ&language=" + this.mLocale + "&q=" + this.mKeyword);
                if (!TextUtils.isEmpty(strResult)) {
                    this.mCandidateList = AccuWeatherParser.getSearchCityResult(strResult);
                    if (this.mCandidateList != null) {
                        Log.d(TAG, "get candidate city list from AccuWeather");
                        Iterator it = this.mCandidateList.iterator();
                        while (it.hasNext()) {
                            CandidateCity city = (CandidateCity) it.next();
                            Log.d(TAG, "Type : " + city.getType() + ", LocalizedName : " + city.getLocalizedName() + ", Country : " + city.getCountry().getLocalizedName() + ", AdministrativeArea : " + city.getAdministrativeArea().getLocalizedName());
                        }
                        return Boolean.valueOf(true);
                    }
                }
                return Boolean.valueOf(false);
            } catch (Exception e) {
                return Boolean.valueOf(false);
            }
        }

        protected void onPostExecute(Boolean success) {
            if (success.booleanValue()) {
                Log.d(TAG, "search city name from AccuWeather success");
                AccuWeatherHelper.this.mProviderHandler.sendMessage(AccuWeatherHelper.this.getMessage(1073743872, AccuWeatherHelper.this.convertToCandidateCity(this.mCandidateList)));
                return;
            }
            Log.d(TAG, "search city name from AccuWeather fail");
            AccuWeatherHelper.this.mProviderHandler.sendEmptyMessage(-2147481600);
        }
    }

    private class WeatherTask extends AsyncTask<Integer, Void, Integer> {
        private CityData mCity;
        private Context mContext;

        public WeatherTask(Context context, CityData city) {
            this.mContext = context;
            this.mCity = city;
        }

        protected Integer doInBackground(Integer... params) {
            return AccuWeatherHelper.this.executeTask(this.mContext, this.mCity, params[0].intValue()) ? Integer.valueOf(params[0].intValue() | 1073741824) : Integer.valueOf(params[0].intValue() | Integer.MIN_VALUE);
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if ((result.intValue() & Integer.MIN_VALUE) == Integer.MIN_VALUE) {
                AccuWeatherHelper.this.mProviderHandler.sendEmptyMessage(CitySearchProvider.GET_SEARCH_RESULT_FAIL);
                if (AccuWeatherHelper.this.mOnLocationListener != null) {
                    AccuWeatherHelper.this.mOnLocationListener.onError(1);
                }
                Log.d(TAG, "get real-time weather fail");
                return;
            }
            switch (result.intValue() & -1073741825) {
                case WEATHER_TYPE_LOCATION_DATA:
                    LocationData ld = AccuWeatherParser.parseLocation(CacheUtils.getUrlCache(this.mContext, this.mCity.getLocalName() + WEATHER_FILE_LOCATION_DATA));
                    CityData city = new CityData();
                    city.setName(ld.getEnglishName());
                    city.setLocalName(ld.getLocalizedName());
                    city.setLatitude(ld.getGeoPosition().getLatitude());
                    city.setLongitude(ld.getGeoPosition().getLongitude());
                    city.setLocationId(ld.getKey());
                    city.setLocatedCity(true);
                    city.setProvider(CitySearchProvider.PROVIDER_ACCU_WEATHER);
                    if (TextUtils.isEmpty(city.getLocalName())) {
                        city.setLocalName(city.getName());
                    }
                    if (AccuWeatherHelper.this.mOnLocationListener != null) {
                        AccuWeatherHelper.this.mOnLocationListener.onLocationChanged(city);
                    }
                default:
                    break;
            }
        }
    }

    static {
        TAG = AccuWeatherHelper.class.getSimpleName();
    }

    public AccuWeatherHelper(Handler handler) {
        this.mProviderHandler = handler;
    }

    public void setOnLocationListener(LocationProvider.OnLocationListener l) {
        this.mOnLocationListener = l;
    }

    private String composeWeatherAPIRequest(String locationKey, int type, double lat, double lon) {
        Log.d(TAG, "get weather info of " + type + " from " + locationKey);
        switch (type) {
            case WEATHER_TYPE_LOCATION_DATA:
                return "http://api.accuweather.com/locations/v1/cities/geoposition/search.json?q=" + lat + "," + lon + "&apikey=" + API_KEY + "&language=" + this.mLocale;
            default:
                return null;
        }
    }

    private void setLocale(Context context) {
        this.mLocale = EncodeUtil.androidLocaleToAccuFormat(context.getResources().getConfiguration().locale);
    }

    public void getAccWeatherLocationData(Context context, CityData city) {
        setLocale(context);
        getWeatherAPIResponse(context, city, WEATHER_TYPE_LOCATION_DATA);
    }

    public void getAccWeatherinfo(Context context, CityData city) {
        setLocale(context);
        new JudgeTask(context, city).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Integer[]{Integer.valueOf(WEATHER_TYPE_LOCATION_DATA)});
    }

    @Override
    public void getWeatherAPIResponse(Context context, CityData city, int type) {
        new WeatherTask(context, city).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Integer[]{Integer.valueOf(type)});
    }

    private boolean executeTask(Context context, CityData city, int type) {
        try {
            String strResult = NetUtil.httpGet(composeWeatherAPIRequest(city.getLocationId(), type, city.getLatitude(), city.getLongitude()));
            if (!TextUtils.isEmpty(strResult)) {
                switch (type) {
                    case WEATHER_TYPE_LOCATION_DATA:
                        if (AccuWeatherParser.parseLocation(strResult) != null) {
                            CacheUtils.setUrlCache(context, strResult, city.getLocationId() + WEATHER_FILE_LOCATION_DATA);
                            Log.d(TAG, "get location data weather info from AccuWeather, file name : " + city.getLocalName() + WEATHER_FILE_LOCATION_DATA);
                            return true;
                        }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Message getMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    public void searchCitiesByKeyword(Context context, String keyword, String locale) {
        Log.d(TAG, "Search city for keyword '" + keyword + "' by using locale '" + locale + "'");
        SearchCityTask task = new SearchCityTask(context);
        task.setKeyword(keyword);
        task.setLocale(locale);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private List<CommonCandidateCity> convertToCandidateCity(List<CandidateCity> list) {
        if (list == null) {
            return null;
        }
        List<CommonCandidateCity> localList = new ArrayList();
        for (CandidateCity cityAccu : list) {
            localList.add(new CommonCandidateCity(cityAccu.getKey(), cityAccu.getLocalizedName(), cityAccu.getAdministrativeArea().getLocalizedName(), cityAccu.getCountry().getLocalizedName(), cityAccu.getCountry().getID(), 2));
        }
        return localList;
    }
}
