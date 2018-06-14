package com.opweather.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.os.EnvironmentCompat;

public class OpenGLUtil {
    public static boolean isSupportGLES20(Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo()
                .reqGlEsVersion >= 131072 || (VERSION.SDK_INT >= 15 && (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith(EnvironmentCompat.MEDIA_UNKNOWN) || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86")));
    }
}
