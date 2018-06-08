package com.opweather.util;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

import com.amap.api.services.geocoder.GeocodeSearch;

public class GpsUtils {
    public static final boolean isOPen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(GeocodeSearch.GPS) || locationManager.isProviderEnabled("network");
    }

    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    public static boolean isH2OS() {
        return true;/*ReflectUtil.isFeatureSupported("OP_FEATURE_SKU_CHINA");*/
    }
}
