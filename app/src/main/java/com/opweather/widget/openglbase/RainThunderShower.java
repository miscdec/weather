package com.opweather.widget.openglbase;

import android.os.Handler;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class RainThunderShower extends Rain {
    private int delayTime;
    float mAlpha;
    private boolean showerRain;
    float thunderAlpha;
    private Handler thunderHandler;
    private boolean thunderHandlerReady;
    int thunderIndex;
    float[][] thunderList;

    public RainThunderShower() {
        this.showerRain = false;
        this.thunderAlpha = 0.7f;
        this.mAlpha = 0.0f;
        this.thunderHandlerReady = false;
        this.thunderIndex = 0;
        this.thunderList = new float[][]{new float[]{0.0f, 0.4f}, new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.6f}, new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.3f}, new float[]{4000.0f, 0.0f}};
        this.numLines = 300;
        this.HEIGHT_START_OFFSET = 0.06666667f;
        this.WIDTH_START_OFFSET = 0.2f;
        this.HEIGHT_CHANGE_RANGE = 1.5f;
        this.WIDTH_CHANGE_RANGE = 4.5f;
        this.Y_RANDOM_RANGE *= 3;
        this.numRect = 0;
        init();
        startShower();
        startThunder();
        this.thunderHandler = new Handler();
    }

    private void startShower() {
        this.showerRain = false;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                RainThunderShower.this.startRain();
            }
        }, 5000);
    }

    private void startRain() {
        this.showerRain = true;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                RainThunderShower.this.startShower();
            }
        }, 3000);
    }

    private void startThunder() {
        float[] thunder = this.thunderList[this.thunderIndex % this.thunderList.length];
        this.thunderIndex++;
        this.mAlpha = thunder[1];
        this.delayTime = (int) thunder[0];
        if (this.delayTime > 2000) {
            this.delayTime = new Random(System.currentTimeMillis()).nextInt(5000) + 2000;
        }
        this.thunderHandlerReady = true;
    }

    public void draw(GL10 gl, float xoffset, float yoffset) {
        gl.glClearColor(this.thunderAlpha, this.thunderAlpha, this.thunderAlpha, this.thunderAlpha);
        if (this.thunderAlpha < this.mAlpha) {
            this.thunderAlpha += 0.25f;
        } else {
            this.thunderAlpha = 0.0f;
            if (this.thunderHandlerReady) {
                this.thunderHandlerReady = false;
                this.thunderHandler.postDelayed(new Runnable() {
                    public void run() {
                        RainThunderShower.this.startThunder();
                    }
                }, (long) this.delayTime);
            }
        }
        super.draw(gl, xoffset, yoffset);
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
