package com.opweather.util;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {
    public static boolean isFeatureSupported(String featureName) {
        try {
            Class cla = Class.forName("android.util.OpFeatures");
            Method method = cla.getDeclaredMethod("isSupport", new Class[]{int[].class});
            Field field = cla.getDeclaredField(featureName);
            method.setAccessible(true);
            field.setAccessible(true);
            Object[] objArr = new Object[1];
            objArr[0] = new int[]{field.getInt(null)};
            return ((Boolean) method.invoke(null, objArr)).booleanValue();
        } catch (Exception e) {
            Log.i("isFeatureSupported", featureName + " is not supported");
            return false;
        }
    }
}
