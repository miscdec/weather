package com.opweather.widget.shap;

import javax.microedition.khronos.opengles.GL10;

public interface IShap {
    void draw(GL10 gl10);

    boolean isDay();

    void onCreate();

    void setAlpha(float f);

    void setDay(boolean z);

    void setDeadLine(float f, float f2, float f3, float f4);
}
