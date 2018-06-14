package com.opweather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.opweather.util.OrientationSensorUtil;


public abstract class BaseWeatherView extends View implements AbsWeather {
    private boolean mDay;
    private int mDayBackgroundColor;
    protected OrientationSensorUtil.OrientationInfoListener mListener;
    private int mNightBackgroundColor;

    protected abstract void onCreateOrientationInfoListener();

    public BaseWeatherView(Context context, boolean day) {
        super(context, null);
        onCreateOrientationInfoListener();
        mDay = day;
    }

    public BaseWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateOrientationInfoListener();
    }

    public BaseWeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreateOrientationInfoListener();
    }

    public void setDayBackgroundColor(int color) {
        mDayBackgroundColor = color;
    }

    public void setNightBackgroundColor(int color) {
        mNightBackgroundColor = color;
    }

    protected void onDetachedFromWindow() {
        OrientationSensorUtil.removeOrientationInfoListener(mListener);
        super.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        OrientationSensorUtil.addOrientationInfoListener(mListener);
    }

    public void setDay(boolean day) {
        if (isDay() != day) {
            mDay = day;
        }
    }

    public boolean isDay() {
        return mDay;
    }

    public void onViewPause() {
        OrientationSensorUtil.removeOrientationInfoListener(mListener);
    }

    public void onViewStart() {
        OrientationSensorUtil.addOrientationInfoListener(mListener);
    }
}
