package com.opweather.provider;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

final /* synthetic */ class LocationProvider$$Lambda$0 implements AMapLocationListener {
    private final LocationProvider arg$1;

    LocationProvider$$Lambda$0(LocationProvider locationProvider) {
        this.arg$1 = locationProvider;
    }

    public void onLocationChanged(AMapLocation aMapLocation) {
        this.arg$1.onLocationChanged(aMapLocation);
    }
}
