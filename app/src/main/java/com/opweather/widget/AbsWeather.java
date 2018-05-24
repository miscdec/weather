package com.opweather.widget;

public interface AbsWeather {
    void onPageSelected(boolean z);

    void onViewPause();

    void onViewStart();

    void setAlpha(float f);

    void setDay(boolean z);

    void startAnimate();

    void stopAnimate();
}
