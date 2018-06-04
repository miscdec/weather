package com.opweather.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {
    public static final int ALL_PERMISSION_REQUEST = 202;

    static class AnonymousClass_1 implements OnClickListener {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ String val$permission;
        final /* synthetic */ int val$requestCode;

        AnonymousClass_1(Activity activity, String str, int i) {
            this.val$activity = activity;
            this.val$permission = str;
            this.val$requestCode = i;
        }

        public void onClick(DialogInterface dialog, int which) {
            ActivityCompat.requestPermissions(this.val$activity, new String[]{this.val$permission}, this
                    .val$requestCode);
        }
    }

    @TargetApi(23)
    public static boolean check(Activity activity, String permission, String alert, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) == 0) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            AlertUtils.showPermissionDialog(activity, alert, new AnonymousClass_1(activity, permission, requestCode));
            return false;
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        return false;
    }

    @TargetApi(23)
    public static boolean check(Activity activity, String[] permissions, int requestCode) {
        int i = 0;
        while (i < permissions.length && ContextCompat.checkSelfPermission(activity, permissions[i]) == 0) {
            i++;
        }
        if (i >= permissions.length) {
            return true;
        }
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
        return false;
    }

    public static boolean check(Context context, String[] permissions) {
        for (String str : permissions) {
            if (ContextCompat.checkSelfPermission(context, str) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean check(Activity activity, int requestCode, String... permissions) {
        boolean hasPermission = true;
        int length = permissions.length;
        for (int i = 0; i < length; i++) {
            boolean i2;
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) == 0) {
                i2 = true;
            } else {
                i2 = false;
            }
            hasPermission &= i2;
        }
        if (!hasPermission) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
        return hasPermission;
    }

    public static void requestPermission(Activity activity, String permission, int request) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, request);
    }

    public static void requestPermission(Activity activity, String[] permissions, int request) {
        ActivityCompat.requestPermissions(activity, permissions, request);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    public static boolean shouldShowRequestPermissionsRationale(Activity activity, String... permissions) {
        boolean shouldShow = false;
        for (String p : permissions) {
            shouldShow |= activity.shouldShowRequestPermissionRationale(p);
        }
        if (shouldShow) {
            AlertUtils.showNonePermissionDialog(activity);
        }
        return shouldShow;
    }

    public static boolean hasGrantedPermission(Context context, String permission) {
        return hasGrantedPermissions(context, permission);
    }

    public static boolean hasGrantedPermissions(Context context, String... permissions) {
        boolean hasPermission = true;
        int length = permissions.length;
        for (int i = 0; i < length; i++) {
            boolean i2;
            if (ContextCompat.checkSelfPermission(context, permissions[i]) == 0) {
                i2 = true;
            } else {
                i2 = false;
            }
            hasPermission &= i2;
        }
        return hasPermission;
    }
}
