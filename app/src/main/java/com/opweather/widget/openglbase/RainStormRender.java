package com.opweather.widget.openglbase;

import android.content.Context;

public class RainStormRender extends RainBaseRender {
    public RainStormRender(Context context, boolean day) {
        super(context, day);
        mRain = new RainStorm();
        SPEED = -1.7f;
        z = (float) ((-Rain.Z_RANDOM_RANGE) + 2);
    }
}
