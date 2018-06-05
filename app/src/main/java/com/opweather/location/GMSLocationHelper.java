package com.opweather.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.opweather.bean.CityData;
import com.opweather.db.CityWeatherDB;
import com.opweather.util.PermissionUtil;


public class GMSLocationHelper extends ContextWrapper {
    public static final String CACHE_PROVIDER = "oneplus_cache";
    public static final String KEY_LOCATION = "GMSLocationProvider";
    public static final String KEY_LOCATION_SUCCESS = "location_success";
    public static final int REQUEST_CHECK_SETTINGS = 3;
    private static final String TAG = "GMSLocationHelper";
    //private FusedLocationProviderClient mFusedLocationClient;
    private final Handler mHandler;
    //private LocationCallback mLocationCallback;
    //private LocationRequest mLocationRequest;
    //private LocationSettingsRequest mLocationSettingsRequest;
    private OneplusLocationListener mOneplusLocationListener;
    //private SettingsClient mSettingsClient;

    @SuppressLint({"MissingPermission"})
    public GMSLocationHelper(Context base) {
        super(base);
        this.mHandler = new Handler(Looper.getMainLooper());
        //initLocation();
    }

   /* @SuppressLint({"MissingPermission"})
    private void initLocation() {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient((Context) this);
        this.mSettingsClient = LocationServices.getSettingsClient((Context) this);
        createLocationRequest();
        createLocationCallback();
        buildLocationSettingsRequest();
    }

    private void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(10000);
        this.mLocationRequest.setFastestInterval(5000);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void createLocationCallback() {
        this.mLocationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult from google");
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    Log.e(TAG, "from callback,location is null");
                    GMSLocationHelper.this.notifyLocationFaild("location is null");
                    return;
                }
                GMSLocationHelper.this.notifyLocationChange(locationResult.getLastLocation());
            }
        };
    }

    private void buildLocationSettingsRequest() {
        Builder builder = new Builder();
        builder.addLocationRequest(this.mLocationRequest);
        this.mLocationSettingsRequest = builder.build();
    }

    public void setOneplusLocationListener(OneplusLocationListener locationListener) {
        this.mOneplusLocationListener = locationListener;
    }

    public void startLocation() {
        if (PermissionUtil.hasGrantedPermissions(this, "android.permission.ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE")) {
            this.mSettingsClient.checkLocationSettings(this.mLocationSettingsRequest).addOnSuccessListener(new GMSLocationHelper$$Lambda$0(this)).addOnFailureListener(new GMSLocationHelper$$Lambda$1(this));
            return;
        }
        Log.e(TAG, "no permission");
        notifyLocationFaild("no permission");
    }

    final *//* synthetic *//* void lambda$startLocation$1$GMSLocationHelper(LocationSettingsResponse locationSettingsResponse) {
        this.mFusedLocationClient.requestLocationUpdates(this.mLocationRequest, this.mLocationCallback, null);
        this.mHandler.postDelayed(new GMSLocationHelper$$Lambda$2(this), 50000);
    }

    final *//* synthetic *//* void lambda$null$0$GMSLocationHelper() {
        this.mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
        Log.e(TAG, "location timeout");
        notifyLocationFaild("location timeout");
    }

    final *//* synthetic *//* void lambda$startLocation$2$GMSLocationHelper(Exception e) {
        Log.e(TAG, "location fail :" + e.getMessage());
        switch (((ApiException) e).getStatusCode()) {
            case ConnectionResult.RESOLUTION_REQUIRED:
            case ConnectionResult.INTERRUPTED:
                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                try {
                    CityData cityData = CityWeatherDB.getInstance(getBaseContext()).getLocationCity();
                    if (cityData != null && !TextUtils.isEmpty(cityData.getLocationId()) && !"0".equals(cityData.getLocationId())) {
                        notifyLocationUseCache();
                    } else if (getBaseContext() instanceof Activity) {
                        ((ResolvableApiException) e).startResolutionForResult((Activity) getBaseContext(), REQUEST_CHECK_SETTINGS);
                    } else {
                        Log.e(TAG, "Context");
                        notifyLocationFaild("Context status can,t update");
                    }
                } catch (SendIntentException e2) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                Log.e(TAG, errorMessage);
                notifyLocationFaild(errorMessage);
            default:
                break;
        }
    }

    public void stopLocation() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
    }

    private void notifyLocationChange(Location location) {
        if (location != null) {
            this.mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
            Bundle bundle = new Bundle();
            bundle.putBoolean(KEY_LOCATION, true);
            bundle.putBoolean(KEY_LOCATION_SUCCESS, true);
            location.setExtras(bundle);
            this.mOneplusLocationListener.onLocationChanged(location);
            return;
        }
        throw new RuntimeException("location can't be null");
    }

    private void notifyLocationFaild(String reason) {
        this.mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
        this.mHandler.removeCallbacksAndMessages(null);
        Location location = new Location(reason);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_LOCATION, true);
        bundle.putBoolean(KEY_LOCATION_SUCCESS, false);
        location.setExtras(bundle);
        this.mOneplusLocationListener.onLocationChanged(location);
    }

    private void notifyLocationUseCache() {
        Log.d(TAG, "notifyLocationUseCache");
        this.mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
        this.mHandler.removeCallbacksAndMessages(null);
        Location location = new Location(CACHE_PROVIDER);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_LOCATION, true);
        location.setExtras(bundle);
        this.mOneplusLocationListener.onLocationChanged(location);
    }*/
}
