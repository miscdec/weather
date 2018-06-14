package com.opweather.gles20.objects;

import android.content.Context;

import com.opweather.gles20.util.Geometry;


public class RainStromParticles extends RainParticles {
    public RainStromParticles(Geometry.Vector direction, int color1, int color2, Context mContext) {
        super(direction, color1, color2, mContext);
        this.numLines = 300;
        this.numRect = 2;
        this.HEIGHT_CHANGE_RANGE = 0.15f;
        this.WIDTH_CHANGE_RANGE = 7.0f;
        this.LINE_HEIGHT_START_OFFSET = 0.46666667f;
        this.RAIN_SPEED = 0.15f;
        this.MAX_RAIN_DROPS_COUNT = 1000;
        this.numOfGroup = 5;
        init(direction);
    }
}
