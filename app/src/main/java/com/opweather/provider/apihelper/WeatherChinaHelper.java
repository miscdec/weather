package com.opweather.provider.apihelper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.opweather.bean.CityData;
import com.opweather.bean.CommonCandidateCity;
import com.opweather.bean.OpCity;
import com.opweather.db.ChinaCityDB;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.List;

public class WeatherChinaHelper implements IWeatherAPIHelper, ICitySearchAPIHelper {
    private static final String API_URL_FORECAST = "http://open.weather.com" +
            ".cn/data/?areaid=%s&type=%s&date=%s&appid=%s";
    private static final String API_URL_FORECAST_WITH_KEY = "http://open.weather.com" +
            ".cn/data/?areaid=%s&type=%s&date=%s&appid=%s&key=%s";
    private static final String APP_ID = "b82a9c10f8f857eb";
    private static final String FORECAST_DATA = "forecast_v";
    private static final String PRIVATE_KEY = "3739d9_SmartWeatherAPI_2178c79";
    private final String TAG = getClass().getSimpleName();
    private Handler mWeatherProviderHandler;

    private class SearchCityTask extends AsyncTask<Void, Void, Boolean> {
        private List<CommonCandidateCity> mCandidateList;
        private Context mContext;
        private String mKeyword;
        private String mLocale;

        public SearchCityTask(Context context) {
            this.mContext = context;
        }

        public void setKeyword(String keyword) {
            this.mKeyword = keyword;
        }

        public void setLocale(String locale) {
            this.mLocale = locale;
        }

        protected Boolean doInBackground(Void... params) {
            mCandidateList = convertToCandidateCity(ChinaCityDB.openCityDB(mContext).queryCityByName(mContext,
                    mKeyword));
            return mCandidateList != null;
        }

        protected void onPostExecute(Boolean success) {
            if (success) {
                mWeatherProviderHandler.sendMessage(WeatherChinaHelper.this.getMessage(1073745920, mCandidateList));
            } else {
                mWeatherProviderHandler.sendEmptyMessage(-2147479552);
            }
        }
    }

    public WeatherChinaHelper(Handler handler) {
        this.mWeatherProviderHandler = handler;
    }

    @Override
    public void getWeatherAPIResponse(Context context, CityData city, int type) {
    }

    public boolean executeTask(Context context, CityData city, int type) {
        return false;
    }

    public void searchCitiesByKeyword(Context context, String keyword, String locale) {
        Log.d(TAG, "Search city for keyword '" + keyword + "' by using locale '" + locale + "'");
        SearchCityTask task = new SearchCityTask(context);
        task.setKeyword(keyword);
        task.setLocale(locale);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private List<CommonCandidateCity> convertToCandidateCity(List<OpCity> list) {
        if (list == null) {
            return null;
        }
        List<CommonCandidateCity> localList = new ArrayList();
        for (OpCity chinaCity : list) {
            localList.add(new CommonCandidateCity(chinaCity.getAreaId(), chinaCity.getNameChs(), chinaCity.getNameCht
                    (), chinaCity.getNameEn(), chinaCity.getProvinceChs(), chinaCity.getProvinceCht(), chinaCity
                    .getProvinceEn(), chinaCity.getCountryNameChs(), chinaCity.getCountryNameCht(), chinaCity
                    .getCountryNameEn(), chinaCity.getCountryNameEn(), chinaCity.isInChina() ? 1 : RainSurfaceView
                    .RAIN_LEVEL_SHOWER));
        }
        return localList;
    }

    private Message getMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }
}
