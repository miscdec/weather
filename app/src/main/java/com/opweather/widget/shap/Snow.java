package com.opweather.widget.shap;

import com.opweather.widget.anim.BaseAnimation;
import com.opweather.widget.anim.SnowAnimation;

import javax.microedition.khronos.opengles.GL10;

public class Snow extends BaseShape {
    private static final int COUNT = 200;
    private Icosahedron[] mObjects;

    public Snow() {
        mObjects = new Icosahedron[200];
    }

    public void setDeadLine() {
    }

    public void onCreate() {
        for (int i = 0; i < 200; i++) {
            float[] p = BaseAnimation.orginXYZ();
            mObjects[i] = new Icosahedron(p[0], p[1], p[2]);
            float[][] c = SnowAnimation.randomColor();
            mObjects[i].setColor(c[0][0], c[0][1], c[0][2], getAlpha());
            mObjects[i].setDay(isDay());
            mObjects[i].setNightColor(c[1][0], c[1][1], c[1][2], getAlpha());
            mObjects[i].setDeadLine(getDeadLineLeftX(), getDeadLineRightX(), getDeadLineUpY(), getDeadLineDownY());
        }
    }

    public void draw(GL10 gl) {
        for (int i = 0; i < 200; i++) {
            mObjects[i].setAlpha(getAlpha());
            mObjects[i].setDay(isDay());
            mObjects[i].draw(gl);
            mObjects[i].move();
            if (mObjects[i].dead()) {
                float[] p = BaseAnimation.nextXYZ();
                mObjects[i].setXYZ(p[0], p[1], p[2]);
                mObjects[i].init(p[0], p[1], p[2]);
                mObjects[i].setDay(isDay());
                float[][] c = SnowAnimation.randomColor();
                mObjects[i].setColor(c[0][0], c[0][1], c[0][2], getAlpha());
                mObjects[i].setNightColor(c[1][0], c[1][1], c[1][2], getAlpha());
            }
        }
    }
}
