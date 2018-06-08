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
import com.opweather.db.CityWeatherDB;
import com.opweather.provider.apihelper.AccuWeatherHelper;
import com.opweather.ui.MainActivity;
import com.opweather.util.GpsUtils;
import com.opweather.util.PreferenceUtils;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.Date;
import java.util.Locale;

public class LocationProvider {
    private static final String TAG = LocationProvider.class.getSimpleName();
    private boolean isLocating;
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

    public LocationProvider(Context context) {
        isLocating = false;
        mContext = context;
        locationHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GlobalConfig.MESSAGE_ACCU_GET_LOCATION_SUCC:
                        LocationData ld = (LocationData) msg.obj;
                        CityData city = new CityData();
                        city.setName(ld.getEnglishName());
                        city.setLocalName(ld.getLocalizedName());
                        city.setLatitude(ld.getGeoPosition().getLatitude());
                        city.setLongitude(ld.getGeoPosition().getLongitude());
                        city.setLocationId(ld.getKey());
                        city.setProvider(CitySearchProvider.PROVIDER_ACCU_WEATHER);
                        city.setLocatedCity(true);
                        if (TextUtils.isEmpty(city.getLocalName())) {
                            city.setLocalName(city.getName());
                        }
                        if (mListener != null) {
                            mListener.onLocationChanged(city);
                        }
                    default:
                        break;
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
        option.setGpsFirst(PreferenceUtils.getBoolean(mContext, "gps_first"));
        mLocationClient.setLocationOption(option);
    }

    final void bridge$lambda$0$LocationProvider(Location location) {
        notifyLocationChangedForOversea(location);
    }

    public void startLocation() {
        mLocationClient.startLocation();
    }

    private void notifyLocationChangedForAmap(final AMapLocation location) {
        String city = location.getCity();
        String code = location.getAdCode();
        String country = location.getCountry();
        String province = location.getProvince();
        boolean isGMSLocation = false;
        if (location.getExtras() != null) {
            isGMSLocation = location.getExtras().getBoolean("GMSLocationProvider");
        }
        Log.d(TAG, "location.getLatitude(): " + location.getLatitude());
        Log.d(TAG, "location.getLongitude(): " + location.getLongitude());
        Log.d(TAG, "isisGMSLocation : " + isGMSLocation);
        if (!isGMSLocation && TextUtils.isEmpty(city) && TextUtils.isEmpty(code) && TextUtils.isEmpty(country)) {
            Log.d(TAG, "location is null");
            judgeIsChinaHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case GlobalConfig.MESSAGE_ACCU_GET_COUNTRY_CHINA:
                            String cityName = (String) msg.obj;
                            Log.d(TAG, "cityName is :" + cityName);
                            OpCity cc = ChinaCityDB.openCityDB(mContext).getChinaCityByPinyin(mContext, cityName);
                            if (cc != null) {
                                CityData city = new CityData();
                                city.setName(location.getCity());
                                city.setLocalName(getName(cc));
                                city.setLatitude(location.getLatitude());
                                city.setLongitude(location.getLongitude());
                                city.setProvider(CitySearchProvider.PROVIDER_WEATHER_CHINA);
                                city.setLocationId(cc.getAreaId());
                                city.setLocatedCity(true);
                                if (mListener != null) {
                                    mListener.onLocationChanged(city);
                                }
                            } else if (mListener != null) {
                                mListener.onError(RainSurfaceView.RAIN_LEVEL_DOWNPOUR);
                            }
                        default:
                            break;
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
            Log.d(TAG, "country is china " + location.getProvince());
            OpCity cc = ChinaCityDB.openCityDB(mContext).getChinaCity(mContext, location.getAdCode(), location
                    .getCity());
            if (cc != null) {
                CityData cityData = new CityData();
                cityData.setName(location.getCity());
                cityData.setLocalName(getName(cc));
                cityData.setLatitude(location.getLatitude());
                cityData.setLongitude(location.getLongitude());
                cityData.setProvider(CitySearchProvider.PROVIDER_WEATHER_CHINA);
                cityData.setLocationId(cc.getAreaId());
                cityData.setLocatedCity(true);
                if (mListener != null) {
                    mListener.onLocationChanged(cityData);
                }
            } else if (mListener != null) {
                mListener.onError(RainSurfaceView.RAIN_LEVEL_DOWNPOUR);
            }
        }
    }

    private void notifyLocationChangedForOversea(Location location) {
        if (location != null) {
            String provider = location.getProvider();
            switch (provider.hashCode()) {
                case RainSurfaceView.RAIN_LEVEL_DRIZZLE:
                    CityData cityData = CityWeatherDB.getInstance(mContext).getLocationCity();
                    if (cityData != null && !TextUtils.isEmpty(cityData.getLocationId()) && !"0" .equals(cityData
                            .getLocationId()) && mListener != null) {
                        mListener.onLocationChanged(cityData);
                        return;
                    }
                    return;
                default:
                    boolean success = false;
                    if (location.getExtras() != null) {
                        success = location.getExtras().getBoolean("GMSLocationProvider");
                    }
                    Log.d(TAG, "fetchAccuLocationData");
                    if (success) {
                        fetchAccuLocationData(location.getLatitude(), location.getLongitude());
                        return;
                    } else if (mListener != null) {
                        mListener.onError(RainSurfaceView.RAIN_LEVEL_DOWNPOUR);
                        return;
                    } else {
                        return;
                    }
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
        return (locale.toString().contains("Hant") || locale.toString().equals("zh_TW")) ? city.getNameCht() : city
                .getNameEn();
    }

    public void onLocationChanged(final AMapLocation amapLocation) {
        if (amapLocation == null || amapLocation.getErrorCode() != 0) {
            if (amapLocation != null) {
                Log.e("AmapErr", "Location ERR:" + amapLocation.getErrorCode() + "LocationDetail: " + amapLocation
                        .getLocationDetail() + "errorInfo: " + amapLocation.getErrorInfo());
            }
            if (mListener != null) {
                mListener.onError(amapLocation != null ? amapLocation.getErrorCode() : RainSurfaceView
                        .RAIN_LEVEL_DOWNPOUR);
            }
        } else if (MainActivity.MOCK_TEST_FLAG) {
            GeocodeSearch gs = new GeocodeSearch(mContext);
            gs.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    if (!(regeocodeResult == null || regeocodeResult.getRegeocodeAddress() == null)) {
                        RegeocodeAddress s = regeocodeResult.getRegeocodeAddress();
                        Log.d(TAG, "city name = " + s.getCity());
                        Log.d(TAG, "ad code = " + s.getAdCode());
                        Log.d(TAG, "country = " + s.getProvince());
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
                    Log.d(TAG, "onGeocodeSearched");
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
