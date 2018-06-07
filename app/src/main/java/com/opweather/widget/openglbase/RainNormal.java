package com.opweather.widget.openglbase;

public class RainNormal extends Rain {
    public RainNormal() {
        this.numLines = 250;
        this.HEIGHT_CHANGE_RANGE = 1.5f;
        this.WIDTH_CHANGE_RANGE = 5.0f;
        this.RECT_WIDTH_CHANGE_RANGE = 3.0f;
        init();
    }
}
