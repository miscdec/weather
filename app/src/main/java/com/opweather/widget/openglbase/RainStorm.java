package com.opweather.widget.openglbase;

public class RainStorm extends Rain {
    public RainStorm() {
        this.numLines = 300;
        this.HEIGHT_CHANGE_RANGE = 3.0f;
        this.WIDTH_CHANGE_RANGE = 5.0f;
        init();
    }
}
