package com.opweather.widget.openglbase;

import android.content.Context;
import android.opengl.GLU;
import android.support.v4.widget.AutoScrollHelper;

import com.android.volley.DefaultRetryPolicy;

import javax.microedition.khronos.opengles.GL10;

public class RainBaseRender extends BaseGLRenderer {
    protected float SPEED;
    protected Rain mRain;

    public RainBaseRender(Context context, boolean day) {
        super(day);
        SPEED = -0.8f;
        mRain = new Rain();
        mRain.setDay(day);
        z = (float) (((-Rain.Z_RANDOM_RANGE) * 2) / 3);
    }

    public void onDrawFrame(GL10 gl) {
        if (animEnable) {
            gl.glClear(16640);
            gl.glLoadIdentity();
            gl.glTranslatef(AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED, z);
            GLU.gluLookAt(gl, AutoScrollHelper.RELATIVE_UNSPECIFIED, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f);
            gl.glRotatef(mAngleX, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                    AutoScrollHelper.RELATIVE_UNSPECIFIED);
            gl.glRotatef(mAngleY, AutoScrollHelper.RELATIVE_UNSPECIFIED, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT,
                    AutoScrollHelper.RELATIVE_UNSPECIFIED);
            gl.glRotatef(mAngleZ, AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            mRain.draw(gl, AutoScrollHelper.RELATIVE_UNSPECIFIED, SPEED);
            gl.glLoadIdentity();
        }
    }

    public void onDrawing(GL10 gl) {
        gl.glTranslatef(AutoScrollHelper.RELATIVE_UNSPECIFIED, AutoScrollHelper.RELATIVE_UNSPECIFIED, z);
        GLU.gluLookAt(gl, AutoScrollHelper.RELATIVE_UNSPECIFIED, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f);
        mRain.draw(gl, AutoScrollHelper.RELATIVE_UNSPECIFIED, SPEED);
    }

    public void setAlpha(float alpha) {
        mRain.setAlpha(alpha);
    }

    public void setDay(boolean day) {
        super.setDay(day);
        mRain.setDay(day);
    }
}
