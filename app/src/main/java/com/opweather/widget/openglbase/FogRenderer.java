package com.opweather.widget.openglbase;

import com.opweather.widget.anim.BaseAnimation;
import com.opweather.widget.anim.FogAnimation;
import com.opweather.widget.shap.Fog;
import com.opweather.widget.shap.IShap;
import com.opweather.widget.shap.Icosahedron;

import javax.microedition.khronos.opengles.GL10;

public class FogRenderer extends BaseGLRenderer {
    IShap fog;
    Icosahedron icosahedron;
    BaseAnimation mWeatherAnimation;

    public FogRenderer(boolean day) {
        super(day);
        mWeatherAnimation = new FogAnimation();
        fog = new Fog();
        fog.setDay(day);
    }

    public void onSurfaceChangedLoaded(GL10 gl, int width, int height, float minX, float maxX, float minY, float
            maxY, float minZ, float maxZ) {
        BaseAnimation.setCenterXYZ((maxX + minX) / 2.0f, (maxY + minY) / 2.0f, (maxZ + minZ) / 2.0f);
        BaseAnimation.setRange(2.0f * maxX, maxY - minY, maxZ - minZ);
        fog.setDeadLine(minX * 10.0f, maxX * 10.0f, Float.MIN_VALUE, 10.0f * minY);
        fog.onCreate();
    }

    public void onDrawing(GL10 gl) {
        fog.draw(gl);
    }

    public void setAlpha(float alpha) {
        fog.setAlpha(alpha);
    }
}
