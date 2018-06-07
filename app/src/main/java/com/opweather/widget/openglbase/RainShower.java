package com.opweather.widget.openglbase;

import android.os.Handler;

public class RainShower extends Rain {
    private boolean showerRain;

    public RainShower() {
        this.showerRain = false;
        this.numLines = 400;
        this.HEIGHT_START_OFFSET = 0.06666667f;
        this.WIDTH_START_OFFSET = 0.2f;
        this.HEIGHT_CHANGE_RANGE = 1.5f;
        this.WIDTH_CHANGE_RANGE = 4.5f;
        this.Y_RANDOM_RANGE *= 3;
        this.numRect = 0;
        init();
        startShower();
    }

    private void startShower() {
        this.showerRain = false;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                RainShower.this.startRain();
            }
        }, 5000);
    }

    private void startRain() {
        this.showerRain = true;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                RainShower.this.startShower();
            }
        }, 3000);
    }

    protected void RandomLine(int index) {
        this.vertexArray[this.numOfOneGroup * index] = 0.0f;
        this.vertexArray[(this.numOfOneGroup * index) + 1] = 0.0f;
        this.vertexArray[(this.numOfOneGroup * index) + 3] = 0.0f;
        this.vertexArray[(this.numOfOneGroup * index) + 4] = 0.0f;
        if (this.showerRain || index % 10 == 0) {
            super.RandomLine(index);
        }
    }
}
