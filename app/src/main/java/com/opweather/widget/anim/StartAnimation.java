package com.opweather.widget.anim;

import java.util.Random;

public class StartAnimation extends BaseAnimation {
    public static float[] orginXY() {
        Random random = getRandom();
        float x = (getRangeX() * ((float) random.nextInt(10000))) / 10000.0f;
        float y = (getRangeY() * ((float) random.nextInt(10000))) / 10000.0f;
        float z = (float) random.nextInt(120);
        nextSeed();
        return new float[]{x, y, z};
    }

    public float[] next() {
        return null;
    }
}
