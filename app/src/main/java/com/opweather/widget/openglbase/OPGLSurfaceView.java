package com.opweather.widget.openglbase;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;

import com.opweather.util.OrientationSensorUtil;
import com.opweather.widget.AbsWeather;

public abstract class OPGLSurfaceView extends GLSurfaceView implements AbsWeather {
    private boolean mDay;
    protected OrientationSensorUtil.OrientationInfoListener mListener;
    protected BaseGLRenderer mRenderer;

    protected abstract void onCreateOrientationInfoListener();

    public OPGLSurfaceView(Context context, AttributeSet attrs, boolean day) {
        super(context, attrs);
        onCreateOrientationInfoListener();
        mDay = day;
        setZOrderOnTop(true);
        getHolder().setFormat(ListPopupWindow.WRAP_CONTENT);
        setEGLConfigChooser(ItemTouchHelper.RIGHT, 8, 8, 8, ItemTouchHelper.START, 0);
    }

    public OPGLSurfaceView(Context context, boolean day) {
        this(context, null, day);
        onCreateOrientationInfoListener();
    }

    public void setDay(boolean day) {
        mDay = day;
        if (mRenderer != null) {
            mRenderer.setDay(day);
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

    protected void onDetachedFromWindow() {
        OrientationSensorUtil.removeOrientationInfoListener(mListener);
        super.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        OrientationSensorUtil.addOrientationInfoListener(mListener);
    }
}
