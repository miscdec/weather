package com.opweather.provider;

import android.location.Location;

import com.opweather.location.OneplusLocationListener;


final /* synthetic */ class LocationProvider$$Lambda$1 implements OneplusLocationListener {
    private final LocationProvider arg$1;

    LocationProvider$$Lambda$1(LocationProvider locationProvider) {
        arg$1 = locationProvider;
    }

    public void onLocationChanged(Location location) {
        arg$1.bridge$lambda$0$LocationProvider(location);
    }
}
