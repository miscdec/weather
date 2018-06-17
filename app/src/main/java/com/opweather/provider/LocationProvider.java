package com.opweather.provider;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.opweather.bean.CityData;
import com.opweather.bean.LocationData;
import com.opweather.bean.OpCity;
import com.opweather.constants.GlobalConfig;
import com.opweather.db.ChinaCityDB;
import com.opweather.provider.apihelper.AccuWeatherHelper;
import com.opweather.ui.MainActivity;
import com.opweather.util.GpsUtils;


import java.util.Date;
import java.util.Locale;

public class LocationProvider {
    private static final String TAG = LocationProvider.class.getSimpleName();
    private boolean isLocating = false;
    private Handler judgeIsChinaHandler;
    private Handler locationHandler;
    private AccuWeatherHelper locationHelper;
    private Context mContext;
    private OnLocationListener mListener;
    public AMapLocationClient mLocationClient;

    public interface OnLocationListener {
        void onError(int i);

        void onLocationChanged(CityData cityData);
    }

    final void bridge$lambda$0$LocationProvider(Location location) {
        notifyLocationChangedForOversea(location);
    }

    public LocationProvider(Context context) {
        mContext = context;
        locationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 200:
                        LocationData ld = (LocationData) msg.obj;
                        CityData city = new CityData();
                        city.setName(ld.getEnglishName());
                        city.setLocalName(ld.getLocalizedName());
                        city.setLatitude(ld.getGeoPosition().getLatitude());
                        city.setLongitude(ld.getGeoPosition().getLongitude());
                        city.setLocationId(ld.getKey());
                        city.setProvider(2048);
                        city.setLocatedCity(true);
                        if (TextUtils.isEmpty(city.getLocalName())) {
                            city.setLocalName(city.getName());
                        }
                        if (mListener != null) {
                            mListener.onLocationChanged(city);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        locationHelper = new AccuWeatherHelper(locationHandler);
        initLocation();
    }

    public void setOnLocationListener(OnLocationListener l) {
        mListener = l;
        locationHelper.setOnLocationListener(mListener);
    }

    public void initLocation() {
        isLocating = true;
        mLocationClient = new AMapLocationClient(mContext);
        mLocationClient.setLocationListener(new LocationProvider$$Lambda$0(this));
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationMode.Hight_Accuracy);
        option.setMockEnable(true);
        option.setOnceLocation(true);
        option.setNeedAddress(true);
        mLocationClient.setLocationOption(option);
    }

    public void startLocation() {
        mLocationClient.startLocation();
    }

    private void notifyLocationChangedForAmap(final AMapLocation location) {
        String city = location.getCity();
        String code = location.getAdCode();
        String country = location.getCountry();
        String province = location.getProvince();

        Log.d(TAG, "location.getLatitude(): " + location.getLatitude());
        Log.d(TAG, "location.getLongitude(): " + location.getLongitude());
        if (TextUtils.isEmpty(city) && TextUtils.isEmpty(code) && TextUtils.isEmpty(country)) {
            Log.d(TAG, "location is null");
            judgeIsChinaHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case GlobalConfig.MESSAGE_ACCU_GET_COUNTRY_CHINA /*201*/:
                            String cityName = (String) msg.obj;
                            Log.d(LocationProvider.TAG, "cityName is :" + cityName);
                            OpCity cc = ChinaCityDB.openCityDB(mContext).getChinaCityByPinyin(mContext, cityName);
                            if (cc != null) {
                                CityData city = new CityData();
                                city.setName(location.getCity());
                                city.setLocalName(getName(cc));
                                city.setLatitude(location.getLatitude());
                                city.setLongitude(location.getLongitude());
                                city.setProvider(4096);
                                city.setLocationId(cc.getAreaId());
                                city.setLocatedCity(true);
                                if (mListener != null) {
                                    mListener.onLocationChanged(city);
                                    return;
                                }
                                return;
                            } else if (mListener != null) {
                                mListener.onError(3);
                                return;
                            } else {
                                return;
                            }
                        default:
                            return;
                    }
                }
            };
            AccuWeatherHelper locationHelper = new AccuWeatherHelper(judgeIsChinaHandler);
            CityData tmpCity = new CityData();
            tmpCity.setLatitude(location.getLatitude());
            tmpCity.setLongitude(location.getLongitude());
            tmpCity.setLocationDataRequestedTimestamp(new Date().getTime());
            locationHelper.getAccWeatherinfo(mContext, tmpCity);
            locationHelper.setOnLocationListener(mListener);
        } else if (country == null || !country.equals("中国") || province.equals("台湾省")) {
            Log.d(TAG, "fetchAccuLocationData");
            fetchAccuLocationData(location.getLatitude(), location.getLongitude());
        } else {
            Log.d(TAG, "country is china" + location.getProvince());
            OpCity cc = ChinaCityDB.openCityDB(mContext).getChinaCity(mContext, location.getAdCode(), location
                    .getCity());
            if (cc != null) {
                CityData cityData = new CityData();
                cityData.setName(location.getCity());
                cityData.setLocalName(getName(cc));
                cityData.setLatitude(location.getLatitude());
                cityData.setLongitude(location.getLongitude());
                cityData.setProvider(4096);
                cityData.setLocationId(cc.getAreaId());
                cityData.setLocatedCity(true);
                if (mListener != null) {
                    mListener.onLocationChanged(cityData);
                }
            } else if (mListener != null) {
                mListener.onError(3);
            }
        }
    }

    private void notifyLocationChangedForOversea(Location location) {
        if (location != null) {
            Log.d(TAG, "fetchAccuLocationData");
            if (location.getLatitude() > 0.0d || location.getLongitude() > 0.0d) {
                fetchAccuLocationData(location.getLatitude(), location.getLongitude());
                return;
            } else if (mListener != null) {
                mListener.onError(3);
                return;
            } else {
                return;
            }
        }
        throw new IllegalArgumentException("location can't be empty");
    }

    private String getName(OpCity city) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        Log.d(TAG, "locale: " + locale.toString());
        if (locale.toString().contains("Hans") || locale.toString().equals("zh_CN")) {
            return city.getNameChs();
        }
        if (locale.toString().contains("Hant") || locale.toString().equals("zh_TW")) {
            return city.getNameCht();
        }
        return city.getNameEn();
    }

    public void onLocationChanged(final AMapLocation amapLocation) {
        if (amapLocation == null || amapLocation.getErrorCode() != 0) {
            if (amapLocation != null) {
                Log.e("AmapErr", "Location ERR:" + amapLocation.getErrorCode() + "LocationDetail: " + amapLocation
                        .getLocationDetail() + "errorInfo: " + amapLocation.getErrorInfo());
            }
            if (mListener != null) {
                mListener.onError(amapLocation != null ? amapLocation.getErrorCode() : 3);
            }
        } else if (MainActivity.MOCK_TEST_FLAG) {
            GeocodeSearch gs = new GeocodeSearch(mContext);
            gs.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    if (!(regeocodeResult == null || regeocodeResult.getRegeocodeAddress() == null)) {
                        RegeocodeAddress s = regeocodeResult.getRegeocodeAddress();
                        Log.d(LocationProvider.TAG, "city name = " + s.getCity());
                        Log.d(LocationProvider.TAG, "ad code = " + s.getAdCode());
                        Log.d(LocationProvider.TAG, "country = " + s.getProvince());
                        amapLocation.setCity(s.getCity());
                        amapLocation.setAdCode(s.getAdCode());
                        if (!TextUtils.isEmpty(amapLocation.getCity())) {
                            amapLocation.setCountry("中国");
                        }
                    }
                    notifyLocationChangedForAmap(amapLocation);
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                    Log.d(LocationProvider.TAG, "onGeocodeSearched");
                }
            });
            gs.getFromLocationAsyn(new RegeocodeQuery(new LatLonPoint(amapLocation.getLatitude(), amapLocation
                    .getLongitude()), 15.0f, GeocodeSearch.GPS));
        } else {
            Log.d(TAG, "get location start");
            notifyLocationChangedForAmap(amapLocation);
            isLocating = false;
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
        mListener = null;
    }

    private void fetchAccuLocationData(double latitude, double longitude) {
        CityData tmpCity = new CityData();
        tmpCity.setLatitude(latitude);
        tmpCity.setLongitude(longitude);
        tmpCity.setLocationDataRequestedTimestamp(new Date().getTime());
        locationHelper.getAccWeatherLocationData(mContext, tmpCity);
    }
}
