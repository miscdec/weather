package com.opweather.widget.openglbase;

import android.content.Context;

public class RainNormalRender extends RainBaseRender {
    public RainNormalRender(Context context, boolean day) {
        super(context, day);
        mRain = new RainNormal();
        SPEED = -0.65f;
        z = (float) ((-Rain.Z_RANDOM_RANGE) + 2);
    }
}
