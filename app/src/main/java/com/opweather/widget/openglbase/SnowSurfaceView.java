package com.opweather.widget.openglbase;

import android.content.Context;

import com.opweather.util.OrientationSensorUtil;

public class SnowSurfaceView extends OPGLSurfaceView {
    public SnowSurfaceView(Context context, boolean day) {
        super(context, day);
        mRenderer = new SnowRenderer(day);
        setRenderer(mRenderer);
        requestFocus();
        setFocusableInTouchMode(true);
        setRenderMode(1);
    }

    public void setZAngle(float lr) {
        mRenderer.setAngleZ(-lr);
    }

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        mRenderer.setAlpha(alpha);
    }

    public void setXAngle(float x) {
        float angle;
        if (x <= -180.0f || x >= 90.0f) {
            angle = x - 270.0f;
        } else {
            angle = x + 90.0f;
        }
        mRenderer.setAngleX(angle);
    }

    public void startAnimate() {
        setRenderMode(1);
    }

    public void stopAnimate() {
        setRenderMode(0);
    }

    public void onPageSelected(boolean isCurrent) {
    }

    protected void onCreateOrientationInfoListener() {
        mListener = new OrientationSensorUtil.OrientationInfoListener() {
            public void onOrientationInfoChange(float x, float y, float z) {
                setXAngle(y);
                setZAngle(z);
            }
        };
    }
}
