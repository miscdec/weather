package com.opweather.widget.openglbase;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.support.v4.widget.AutoScrollHelper;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.constants.WeatherType;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public abstract class BaseGLRenderer implements Renderer {
    protected boolean animEnable;
    protected float mAngleX;
    protected float mAngleY;
    protected float mAngleZ;
    private boolean mDay;
    private float mMaxY;
    private float mMaxZ;
    private float mMinY;
    private float mMinZ;
    protected float z;

    public abstract void onDrawing(GL10 gl10);

    public abstract void setAlpha(float f);

    public BaseGLRenderer(boolean day) {
        mMinY = -1.0f;
        mMaxY = 1.0f;
        mMinZ = -1.0f;
        mMaxZ = 10.0f;
        mAngleZ = 0.0f;
        z = -6.0f;
        animEnable = true;
        mDay = day;
    }

    public void onSurfaceChangedLoaded(GL10 gl, int width, int height, float minX, float maxX, float minY, float
            maxY, float minZ, float maxZ) {
    }

    public void onDrawFrame(GL10 gl) {
        if (animEnable) {
            gl.glClear(16640);
            gl.glLoadIdentity();
            gl.glTranslatef(AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED, z);
            gl.glFrontFace(2305);
            gl.glEnable(2884);
            gl.glCullFace(WeatherType.ACCU_WEATHER_MOSTLY_CLEAR);
            gl.glEnableClientState(32884);
            gl.glRotatef(mAngleX, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, AutoScrollHelper
                    .RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED);
            gl.glRotatef(mAngleY, AutoScrollHelper.RELATIVE_UNSPECIFIED, DefaultRetryPolicy
                    .DEFAULT_BACKOFF_MULT, AutoScrollHelper.RELATIVE_UNSPECIFIED);
            gl.glRotatef(mAngleZ, AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            onDrawing(gl);
            gl.glDisableClientState(32884);
            gl.glDisable(2884);
            gl.glFinish();
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {
            height = 1;
        }
        float aspect = ((float) width) / ((float) height);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(5889);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, aspect, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 200.0f);
        gl.glMatrixMode(5888);
        gl.glFrustumf(-aspect, aspect, mMinY, mMaxY, mMinZ, mMaxZ);
        gl.glLoadIdentity();
        onSurfaceChangedLoaded(gl, width, height, -aspect, aspect, mMinY, mMaxY, mMinZ, mMaxZ);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
        gl.glClearColor(AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED);
        gl.glClearDepthf(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        gl.glEnable(3042);
        gl.glEnable(2929);
        gl.glBlendFunc(770, 771);
        gl.glDepthFunc(515);
        gl.glHint(3152, 4354);
        gl.glDisable(WeatherType.OPPO_FOREIGN_WEATHER_STORM_TO_HEAVY_STORM);
        gl.glShadeModel(7425);
    }

    public void setAngleX(float x) {
        mAngleX = x;
    }

    public void setAngleY(float y) {
        mAngleY = y;
    }

    public void setAngleZ(float z) {
        mAngleZ = z;
    }

    public void AddAngleX(float x) {
        mAngleX += x;
    }

    public void AddAngleY(float y) {
        mAngleY += y;
    }

    public void AddAngleZ(float z) {
        mAngleZ += z;
    }

    public void setDay(boolean day) {
        mDay = day;
    }

    public boolean isDay() {
        return mDay;
    }

    public void setAnimEnable(boolean enable) {
        animEnable = enable;
        Log.i("janu", "animEnable : " + enable + "    class : " + getClass().getName());
    }
}
