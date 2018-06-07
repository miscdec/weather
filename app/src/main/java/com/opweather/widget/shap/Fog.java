package com.opweather.widget.shap;

import com.opweather.widget.anim.BaseAnimation;
import com.opweather.widget.anim.FogAnimation;

import javax.microedition.khronos.opengles.GL10;

public class Fog extends BaseShape {
    private static final int COUNT = 400;
    private FogParticle[] mObjects;

    public Fog() {
        mObjects = new FogParticle[400];
    }

    public void setDeadLine() {
    }

    public void onCreate() {
        for (int i = 0; i < 400; i++) {
            float[] p = BaseAnimation.orginXYZ();
            mObjects[i] = new FogParticle(p[0], p[1], p[2]);
            float[][] c = FogAnimation.randomColor();
            mObjects[i].setDay(isDay());
            mObjects[i].setColor(c[0][0], c[0][1], c[0][2], getAlpha());
            mObjects[i].setNightColor(c[1][0], c[1][1], c[1][2], getAlpha());
            mObjects[i].setDeadLine(getDeadLineLeftX(), getDeadLineRightX(), getDeadLineUpY(), getDeadLineDownY());
        }
    }

    public void draw(GL10 gl) {
        for (int i = 0; i < 400; i++) {
            mObjects[i].setAlpha(getAlpha());
            mObjects[i].draw(gl);
            mObjects[i].move();
            if (mObjects[i].dead()) {
                float[] p = BaseAnimation.nextXYZ();
                mObjects[i].setXYZ(p[0], p[1], p[2]);
                mObjects[i].init(p[0], p[1], p[2]);
                mObjects[i].setDay(isDay());
                float[][] c = FogAnimation.randomColor();
                mObjects[i].setColor(c[0][0], c[0][1], c[0][2], getAlpha());
                mObjects[i].setNightColor(c[1][0], c[1][1], c[1][2], getAlpha());
            }
        }
    }
}
