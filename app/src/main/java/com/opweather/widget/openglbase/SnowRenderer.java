package com.opweather.widget.openglbase;

import com.opweather.widget.anim.BaseAnimation;
import com.opweather.widget.anim.SnowAnimation;
import com.opweather.widget.shap.IShap;
import com.opweather.widget.shap.Icosahedron;
import com.opweather.widget.shap.Snow;

import javax.microedition.khronos.opengles.GL10;

public class SnowRenderer extends BaseGLRenderer {
    Icosahedron icosahedron;
    BaseAnimation mWeatherAnimation;
    IShap snow;

    public SnowRenderer(boolean day) {
        super(day);
        mWeatherAnimation = new SnowAnimation();
        snow = new Snow();
        snow.setDay(day);
    }

    public void onSurfaceChangedLoaded(GL10 gl, int width, int height, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        BaseAnimation.setCenterXYZ((maxX + minX) / 2.0f, (maxY + minY) / 2.0f, (maxZ + minZ) / 2.0f);
        BaseAnimation.setRange(2.0f * maxX, maxY - minY, maxZ - minZ);
        snow.setDeadLine(minX * 10.0f, maxX * 10.0f, Float.MIN_VALUE, 10.0f * minY);
        snow.onCreate();
    }

    public void onDrawing(GL10 gl) {
        snow.draw(gl);
    }

    public void setAlpha(float alpha) {
        snow.setAlpha(alpha);
    }

    public void setDay(boolean day) {
        super.setDay(day);
        snow.setDay(day);
    }
}
