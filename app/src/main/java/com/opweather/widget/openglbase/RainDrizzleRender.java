package com.opweather.widget.openglbase;

import android.content.Context;

public class RainDrizzleRender extends RainBaseRender {
    public RainDrizzleRender(Context context, boolean day) {
        super(context, day);
        mRain = new RainDrizzle();
        SPEED = -0.5f;
    }
}
