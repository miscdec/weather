package com.opweather.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

public class SystemBarTintManager {
    public static final int DEFAULT_TINT_COLOR = -1728053248;
    private static String sNavBarOverride;
    private final SystemBarConfig mConfig;
    private boolean mNavBarAvailable;
    private boolean mNavBarTintEnabled;
    private View mNavBarTintView;
    private boolean mStatusBarAvailable;
    private boolean mStatusBarTintEnabled;
    private View mStatusBarTintView;

    public static class SystemBarConfig {
        private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
        private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
        private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
        private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
        private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
        private final int mActionBarHeight;
        private final boolean mHasNavigationBar;
        private final boolean mInPortrait;
        private final int mNavigationBarHeight;
        private final int mNavigationBarWidth;
        private final float mSmallestWidthDp;
        private final int mStatusBarHeight;
        private final boolean mTranslucentNavBar;
        private final boolean mTranslucentStatusBar;

        private SystemBarConfig(Activity activity, boolean translucentStatusBar, boolean traslucentNavBar) {
            boolean z = true;
            Resources res = activity.getResources();
            mInPortrait = res.getConfiguration().orientation == 1;
            mSmallestWidthDp = getSmallestWidthDp(activity);
            mStatusBarHeight = getInternalDimensionSize(res, STATUS_BAR_HEIGHT_RES_NAME);
            mActionBarHeight = getActionBarHeight(activity);
            mNavigationBarHeight = getNavigationBarHeight(activity);
            mNavigationBarWidth = getNavigationBarWidth(activity);
            if (mNavigationBarHeight <= 0) {
                z = false;
            }
            mHasNavigationBar = z;
            mTranslucentStatusBar = translucentStatusBar;
            mTranslucentNavBar = traslucentNavBar;
        }

        @TargetApi(14)
        private int getActionBarHeight(Context context) {
            if (VERSION.SDK_INT < 14) {
                return 0;
            }
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(16843499, tv, true);
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        @TargetApi(14)
        private int getNavigationBarHeight(Context context) {
            Resources res = context.getResources();
            if (VERSION.SDK_INT < 14 || !hasNavBar(context)) {
                return 0;
            }
            return getInternalDimensionSize(res, mInPortrait ? NAV_BAR_HEIGHT_RES_NAME :
                    NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME);
        }

        @TargetApi(14)
        private int getNavigationBarWidth(Context context) {
            return (VERSION.SDK_INT < 14 || !hasNavBar(context)) ? 0 : getInternalDimensionSize(context.getResources
                    (), NAV_BAR_WIDTH_RES_NAME);
        }

        @TargetApi(14)
        private boolean hasNavBar(Context context) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
            if (resourceId != 0) {
                boolean hasNav = res.getBoolean(resourceId);
                if ("1".equals(sNavBarOverride)) {
                    return false;
                }
                return "0".equals(sNavBarOverride) ? true : hasNav;
            } else {
                return !ViewConfiguration.get(context).hasPermanentMenuKey();
            }
        }

        private int getInternalDimensionSize(Resources res, String key) {
            int resourceId = res.getIdentifier(key, "dimen", "android");
            return resourceId > 0 ? res.getDimensionPixelSize(resourceId) : 0;
        }

        @SuppressLint({"NewApi"})
        private float getSmallestWidthDp(Activity activity) {
            DisplayMetrics metrics = new DisplayMetrics();
            if (VERSION.SDK_INT >= 16) {
                activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            } else {
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }
            return Math.min(((float) metrics.widthPixels) / metrics.density, ((float) metrics.heightPixels) / metrics
                    .density);
        }

        public boolean isNavigationAtBottom() {
            return mSmallestWidthDp >= 600.0f || mInPortrait;
        }

        public int getStatusBarHeight() {
            return mStatusBarHeight;
        }

        public int getActionBarHeight() {
            return mActionBarHeight;
        }

        public boolean hasNavigtionBar() {
            return mHasNavigationBar;
        }

        public int getNavigationBarHeight() {
            return mNavigationBarHeight;
        }

        public int getNavigationBarWidth() {
            return mNavigationBarWidth;
        }

        public int getPixelInsetTop(boolean withActionBar) {
            int i = 0;
            int i2 = mTranslucentStatusBar ? mStatusBarHeight : 0;
            if (withActionBar) {
                i = mActionBarHeight;
            }
            return i + i2;
        }

        public int getPixelInsetBottom() {
            return (mTranslucentNavBar && isNavigationAtBottom()) ? mNavigationBarHeight : 0;
        }

        public int getPixelInsetRight() {
            return (!mTranslucentNavBar || isNavigationAtBottom()) ? 0 : mNavigationBarWidth;
        }
    }

    static {
        if (VERSION.SDK_INT >= 19) {
            try {
                Method m = Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String
                        .class});
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, new Object[]{"qemu.hw.mainkeys"});
            } catch (Throwable th) {
                sNavBarOverride = null;
            }
        }
    }

    @TargetApi(19)
    public SystemBarTintManager(Activity activity) {
        Window win = activity.getWindow();
        ViewGroup decorViewGroup = (ViewGroup) win.getDecorView();
        if (VERSION.SDK_INT >= 19) {
            TypedArray a = activity.obtainStyledAttributes(new int[]{16843759, 16843760});
            mStatusBarAvailable = a.getBoolean(0, false);
            mNavBarAvailable = a.getBoolean(1, false);
            a.recycle();
            LayoutParams winParams = win.getAttributes();
            if ((winParams.flags & 67108864) != 0) {
                mStatusBarAvailable = true;
            }
            if ((winParams.flags & 134217728) != 0) {
                mNavBarAvailable = true;
            }
        }
        mConfig = new SystemBarConfig(activity, mStatusBarAvailable, mNavBarAvailable);
        if (!mConfig.hasNavigtionBar()) {
            mNavBarAvailable = false;
        }
        if (mStatusBarAvailable) {
            setupStatusBarView(activity, decorViewGroup);
        }
        if (mNavBarAvailable) {
            setupNavBarView(activity, decorViewGroup);
        }
    }

    public void setStatusBarTintEnabled(boolean enabled) {
        mStatusBarTintEnabled = enabled;
        if (mStatusBarAvailable) {
            mStatusBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public void setNavigationBarTintEnabled(boolean enabled) {
        mNavBarTintEnabled = enabled;
        if (mNavBarAvailable) {
            mNavBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public void setTintColor(int color) {
        setStatusBarTintColor(color);
        setNavigationBarTintColor(color);
    }

    public void setTintResource(int res) {
        setStatusBarTintResource(res);
        setNavigationBarTintResource(res);
    }

    public void setTintDrawable(Drawable drawable) {
        setStatusBarTintDrawable(drawable);
        setNavigationBarTintDrawable(drawable);
    }

    public void setTintAlpha(float alpha) {
        setStatusBarAlpha(alpha);
        setNavigationBarAlpha(alpha);
    }

    public void setStatusBarTintColor(int color) {
        if (mStatusBarAvailable) {
            mStatusBarTintView.setBackgroundColor(color);
        }
    }

    public void setStatusBarTintResource(int res) {
        if (mStatusBarAvailable) {
            mStatusBarTintView.setBackgroundResource(res);
        }
    }

    public void setStatusBarTintDrawable(Drawable drawable) {
        if (mStatusBarAvailable) {
            mStatusBarTintView.setBackgroundDrawable(drawable);
        }
    }

    @TargetApi(11)
    public void setStatusBarAlpha(float alpha) {
        if (mStatusBarAvailable && VERSION.SDK_INT >= 11) {
            mStatusBarTintView.setAlpha(alpha);
        }
    }

    public void setNavigationBarTintColor(int color) {
        if (mNavBarAvailable) {
            mNavBarTintView.setBackgroundColor(color);
        }
    }

    public void setNavigationBarTintResource(int res) {
        if (mNavBarAvailable) {
            mNavBarTintView.setBackgroundResource(res);
        }
    }

    public void setNavigationBarTintDrawable(Drawable drawable) {
        if (mNavBarAvailable) {
            mNavBarTintView.setBackgroundDrawable(drawable);
        }
    }

    @TargetApi(11)
    public void setNavigationBarAlpha(float alpha) {
        if (mNavBarAvailable && VERSION.SDK_INT >= 11) {
            mNavBarTintView.setAlpha(alpha);
        }
    }

    public SystemBarConfig getConfig() {
        return mConfig;
    }

    public boolean isStatusBarTintEnabled() {
        return mStatusBarTintEnabled;
    }

    public boolean isNavBarTintEnabled() {
        return mNavBarTintEnabled;
    }

    private void setupStatusBarView(Context context, ViewGroup decorViewGroup) {
        mStatusBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, mConfig.getStatusBarHeight());
        params.gravity = 48;
        if (mNavBarAvailable && !mConfig.isNavigationAtBottom()) {
            params.rightMargin = mConfig.getNavigationBarWidth();
        }
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
        mStatusBarTintView.setVisibility(View.GONE);
        decorViewGroup.addView(mStatusBarTintView);
    }

    private void setupNavBarView(Context context, ViewGroup decorViewGroup) {
        FrameLayout.LayoutParams params;
        mNavBarTintView = new View(context);
        if (mConfig.isNavigationAtBottom()) {
            params = new FrameLayout.LayoutParams(-1, mConfig.getNavigationBarHeight());
            params.gravity = 80;
        } else {
            params = new FrameLayout.LayoutParams(mConfig.getNavigationBarWidth(), -1);
            params.gravity = 5;
        }
        mNavBarTintView.setLayoutParams(params);
        mNavBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
        mNavBarTintView.setVisibility(View.GONE);
        decorViewGroup.addView(mNavBarTintView);
    }
}
