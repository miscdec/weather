package com.opweather.widget.openglbase;

import android.content.Context;

public class RainThunderShowerRender extends RainBaseRender {
    public RainThunderShowerRender(Context context, boolean day) {
        super(context, day);
        mRain = new RainThunderShower();
        SPEED = -1.5f;
        z = (float) ((-Rain.Z_RANDOM_RANGE) + 2);
    }
}
