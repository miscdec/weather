package com.opweather.widget;

import android.content.Context;
import android.util.AttributeSet;

public class HailView extends BaseWeatherView {
    public HailView(Context context) {
        this(context, null);
    }

    public HailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void startAnimate() {
    }

    public void stopAnimate() {
    }

    public void onPageSelected(boolean isCurrent) {
    }

    private void init() {
    }

    protected void onCreateOrientationInfoListener() {
    }
}
