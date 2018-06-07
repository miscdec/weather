package com.opweather.widget.openglbase;

import android.content.Context;

public class RainShowerRender extends RainBaseRender {
    public RainShowerRender(Context context, boolean day) {
        super(context, day);
        this.mRain = new RainShower();
        this.SPEED = -1.3f;
        this.z = (float) ((-Rain.Z_RANDOM_RANGE) + 2);
    }
}
