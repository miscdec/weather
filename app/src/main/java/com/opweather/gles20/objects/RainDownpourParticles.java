package com.opweather.gles20.objects;

import android.content.Context;

import com.opweather.gles20.util.Geometry;
import com.opweather.gles20.util.Geometry.Vector;


public class RainDownpourParticles extends RainParticles {
    public RainDownpourParticles(Vector direction, int color1, int color2, Context mContext) {
        super(direction, color1, color2, mContext);
        this.numLines = 250;
        this.numRect = 1;
        this.HEIGHT_CHANGE_RANGE = 0.12f;
        this.WIDTH_CHANGE_RANGE = 6.0f;
        this.LINE_HEIGHT_START_OFFSET = 0.53333336f;
        this.RAIN_SPEED = 0.09f;
        this.MAX_RAIN_DROPS_COUNT = 800;
        this.numOfGroup = 5;
        init(direction);
    }
}
