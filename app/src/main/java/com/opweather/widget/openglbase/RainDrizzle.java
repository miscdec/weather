package com.opweather.widget.openglbase;

public class RainDrizzle extends Rain {
    public RainDrizzle() {
        numLines = 150;
        HEIGHT_CHANGE_RANGE = 1.5f;
        WIDTH_CHANGE_RANGE = 4.0f;
        RECT_WIDTH_CHANGE_RANGE = 1.5f;
        init();
    }
}
