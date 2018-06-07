package com.opweather.widget.openglbase;

import android.content.Context;

public class RainDownpourRender extends RainBaseRender {
    public RainDownpourRender(Context context, boolean day) {
        super(context, day);
        this.mRain = new RainDownpour();
    }
}
