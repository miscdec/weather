package com.opweather.provider;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.opweather.bean.CommonCandidateCity;
import com.opweather.provider.apihelper.AccuWeatherHelper;
import com.opweather.provider.apihelper.ICitySearchAPIHelper;
import com.opweather.provider.apihelper.WeatherChinaHelper;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CitySearchProvider implements ICitySearchProvider {
    public static final int DOMMY_BEGIN_FLAG = 1;
    public static final int DOMMY_END_FLAG = 1;
    public static final int GET_SEARCH_RESULT_FAIL = Integer.MIN_VALUE;
    public static final int GET_SEARCH_RESULT_SUCC = 1073741824;
    public static final int PROVIDER_ACCU_WEATHER = 2048;
    public static final int PROVIDER_WEATHER_CHINA = 4096;
    public static final int PROVIDER_YAHOO_WEATHER = 8192;
    public static final int SEARCH_CITY_BY_KEYWORD = 2097152;
    private final String TAG = getClass().getSimpleName();
    private ArrayList<CommonCandidateCity> mCandidateCity;
    private ICitySearchAPIHelper mCitySearchHelper;
    private Context mContext;
    private Handler mProviderHandler;
    private Handler mUIHandler;

    public CitySearchProvider(Context context, Handler uiHandler) {
        this(context, 2048, uiHandler);
    }

    public CitySearchProvider(Context context, int provider, Handler uiHandler) {
        Log.d(TAG, "init CitySearchProvider");
        mContext = context;
        mUIHandler = uiHandler;
        mProviderHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1073745920) {
                    Log.d(TAG, "get candidate city list");
                    mCandidateCity = (ArrayList) msg.obj;
                    mUIHandler.sendEmptyMessage(PROVIDER_WEATHER_CHINA);
                } else if (msg.what == 1073743872) {
                    mCandidateCity = (ArrayList) msg.obj;
                    mUIHandler.sendEmptyMessage(PROVIDER_ACCU_WEATHER);
                } else if ((msg.what & Integer.MIN_VALUE) != 0) {
                    Log.d(TAG, "search city fail");
                    mUIHandler.sendEmptyMessage(15);
                }
            }
        };
        if (provider == 2048) {
            mCitySearchHelper = new AccuWeatherHelper(mProviderHandler);
        } else {
            mCitySearchHelper = new WeatherChinaHelper(mProviderHandler);
        }
    }

    public void searchCitiesByKeyword(String keyword, String locale) {
        mCandidateCity = null;
        try {
            mCitySearchHelper.searchCitiesByKeyword(mContext, new String(keyword.getBytes("UTF-8")), locale);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CommonCandidateCity> getCandidateCityList() {
        return mCandidateCity;
    }
}
