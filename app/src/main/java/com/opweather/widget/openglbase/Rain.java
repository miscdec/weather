package com.opweather.widget.openglbase;

import android.os.SystemClock;

import com.android.volley.DefaultRetryPolicy;
import com.opweather.constants.WeatherType;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;


public class Rain {
    public static int Z_RANDOM_RANGE = 30;
    protected static float[][] colorValue = new float[][]{new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f,
            1.0f}, new float[]{1.0f, 0.7921569f, 0.0f}};
    protected float HEIGHT_CHANGE_RANGE;
    protected float HEIGHT_START_OFFSET;
    protected float RECT_WIDTH_CHANGE_RANGE;
    protected float WIDTH_CHANGE_RANGE;
    protected float WIDTH_START_OFFSET;
    protected int X_RANDOM_RANGE;
    protected int Y_OFFSET_LIMIT;
    protected int Y_RANDOM_RANGE;
    float[] alphaArray;
    float[][] colorArray;
    protected float[][] colorValueNight;
    protected boolean isDay;
    protected float mAlpha;
    protected int numLines;
    protected int numOfOneGroup;
    protected int numRect;
    protected FloatBuffer vertex;
    float[] vertexArray;
    protected FloatBuffer vertexRect;
    float[] vertexRectArray;
    float[] widthArray;

    protected void RandomLine(int index) {
        Random random = new Random(SystemClock.currentThreadTimeMillis() * ((long) index));
        float x = (((float) (X_RANDOM_RANGE * random.nextInt(10000))) / 10000.0f) - ((float) (X_RANDOM_RANGE / 2));
        float y = ((float) Y_RANDOM_RANGE) * (0.5f + (((float) random.nextInt(10000)) / 10000.0f));
        float randomZ = (((float) random.nextInt(10000)) / 10000.0f) - 0.5f;
        float z = ((float) Z_RANDOM_RANGE) * randomZ;
        vertexArray[numOfOneGroup * index] = x;
        vertexArray[(numOfOneGroup * index) + 1] = y;
        vertexArray[(numOfOneGroup * index) + 2] = z;
        vertexArray[(numOfOneGroup * index) + 3] = x;
        float radio = HEIGHT_START_OFFSET + (((float) random.nextInt(10000)) / 10000.0f);
        float[] fArr = vertexArray;
        int i = (numOfOneGroup * index) + 4;
        float f = HEIGHT_CHANGE_RANGE;
        if (radio > 1.0f) {
            radio = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        fArr[i] = (f * radio) + y;
        vertexArray[(numOfOneGroup * index) + 5] = z;
        if (isDay) {
            colorArray[index] = colorValue[random.nextInt(10000) % colorValue.length];
        } else {
            colorArray[index] = colorValueNight[random.nextInt(10000) % colorValueNight.length];
        }
        alphaArray[index] = 1.0f - Math.abs(randomZ / 0.5f);
        widthArray[index] = WIDTH_CHANGE_RANGE * (WIDTH_START_OFFSET + (((float) random.nextInt(10000)
        ) / 10000.0f));
    }

    protected void RandomRect(int index) {
        Random random = new Random(SystemClock.currentThreadTimeMillis() * ((long) index));
        float x = (((float) (X_RANDOM_RANGE * random.nextInt(10000))) / 10000.0f) - ((float) (this
                .X_RANDOM_RANGE / 2));
        float y = ((float) Y_RANDOM_RANGE) * (0.5f + (((float) random.nextInt(10000)) / 10000.0f));
        float nextInt = ((float) Z_RANDOM_RANGE) * ((((float) random.nextInt(10000)) / 10000.0f) - 0.5f);
        nextInt = (float) ((-Z_RANDOM_RANGE) / 2);
        if (isDay) {
            colorArray[index] = colorValue[random.nextInt(10000) % colorValue.length];
        } else {
            colorArray[index] = colorValueNight[random.nextInt(10000) % colorValueNight.length];
        }
        float widht = RECT_WIDTH_CHANGE_RANGE * (WIDTH_START_OFFSET + (((float) random.nextInt(10000)) /
                10000.0f));
        float radio = HEIGHT_START_OFFSET + (((float) random.nextInt(10000)) / 10000.0f);
        vertexRectArray[index * 12] = x;
        vertexRectArray[(index * 12) + 1] = y;
        vertexRectArray[(index * 12) + 2] = nextInt;
        vertexRectArray[(index * 12) + 3] = x;
        vertexRectArray[(index * 12) + 4] = ((HEIGHT_CHANGE_RANGE * radio) * 2.0f) + y;
        vertexRectArray[(index * 12) + 5] = nextInt;
        vertexRectArray[(index * 12) + 6] = (0.04f * widht) + x;
        vertexRectArray[(index * 12) + 7] = y;
        vertexRectArray[(index * 12) + 8] = nextInt;
        vertexRectArray[(index * 12) + 9] = (0.04f * widht) + x;
        vertexRectArray[(index * 12) + 10] = ((HEIGHT_CHANGE_RANGE * radio) * 2.0f) + y;
        vertexRectArray[(index * 12) + 11] = nextInt;
    }

    protected void RandomLineInit(int index) {
        Random random = new Random(SystemClock.currentThreadTimeMillis() * ((long) index));
        float x = (((float) (X_RANDOM_RANGE * random.nextInt(10000))) / 10000.0f) - ((float) (X_RANDOM_RANGE / 2));
        float y = ((float) (Y_RANDOM_RANGE * 4)) * ((((float) random.nextInt(10000)) / 10000.0f) - 0.5f);
        float randomZ = (((float) random.nextInt(10000)) / 10000.0f) - 0.5f;
        float z = ((float) Z_RANDOM_RANGE) * randomZ;
        vertexArray[numOfOneGroup * index] = x;
        vertexArray[(numOfOneGroup * index) + 1] = y;
        vertexArray[(numOfOneGroup * index) + 2] = z;
        vertexArray[(numOfOneGroup * index) + 3] = x;
        float radio = HEIGHT_START_OFFSET + (((float) random.nextInt(10000)) / 10000.0f);
        float[] fArr = vertexArray;
        int i = (numOfOneGroup * index) + 4;
        float f = HEIGHT_CHANGE_RANGE;
        if (radio > 1.0f) {
            radio = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        fArr[i] = (f * radio) + y;
        vertexArray[(numOfOneGroup * index) + 5] = z;
        if (isDay) {
            colorArray[index] = colorValue[random.nextInt(10000) % colorValue.length];
        } else {
            colorArray[index] = colorValueNight[random.nextInt(10000) % colorValueNight.length];
        }
        alphaArray[index] = 1.0f - Math.abs(randomZ / 0.5f);
        widthArray[index] = WIDTH_CHANGE_RANGE * (WIDTH_START_OFFSET + (((float) random.nextInt(10000)
        ) / 10000.0f));
    }

    public Rain() {
        numLines = 300;
        numRect = 5;
        numOfOneGroup = 6;
        X_RANDOM_RANGE = 20;
        Y_RANDOM_RANGE = 20;
        Y_OFFSET_LIMIT = -60;
        HEIGHT_START_OFFSET = 0.6666667f;
        WIDTH_START_OFFSET = 0.2f;
        HEIGHT_CHANGE_RANGE = 1.5f;
        WIDTH_CHANGE_RANGE = 5.0f;
        RECT_WIDTH_CHANGE_RANGE = 5.0f;
        colorValueNight = new float[][]{new float[]{0.5058824f, 0.6666667f, 0.83137256f},
                new float[]{0.6431373f, 0.50980395f, 0.0f}};
        mAlpha = 1.0f;
        isDay = false;
        vertexArray = new float[(numLines * numOfOneGroup)];
        vertexRectArray = new float[(numRect * 12)];
        colorArray = (float[][]) Array.newInstance(Float.TYPE, new int[]{numLines, 3});
        alphaArray = new float[numLines];
        widthArray = new float[numLines];
        init();
    }

    protected void init() {
        int i;
        vertexArray = new float[(numLines * numOfOneGroup)];
        vertexRectArray = new float[(numRect * 12)];
        colorArray = (float[][]) Array.newInstance(Float.TYPE, new int[]{numLines, 3});
        alphaArray = new float[numLines];
        widthArray = new float[numLines];
        for (i = 0; i < numLines; i++) {
            RandomLineInit(i);
        }
        for (i = 0; i < numRect; i++) {
            RandomRect(i);
        }
        initVertex();
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public void setDay(boolean isDay) {
        this.isDay = isDay;
    }

    protected void changePostion(float xoffset, float yoffset) {
        for (int i = 0; i < numLines; i++) {
            if (vertexArray[(numOfOneGroup * i) + 4] < ((float) Y_OFFSET_LIMIT)) {
                RandomLine(i);
            } else {
                float f;
                if (alphaArray[i] > 0.5f) {
                    f = alphaArray[i];
                } else {
                    f = 0.5f;
                }
                float alphaYoffset = (f * yoffset) * 1.1f;
                if (alphaYoffset <= yoffset) {
                    alphaYoffset = yoffset;
                }
                float[] fArr = vertexArray;
                int i2 = numOfOneGroup * i;
                fArr[i2] = fArr[i2] + xoffset;
                fArr = vertexArray;
                i2 = (numOfOneGroup * i) + 1;
                fArr[i2] = fArr[i2] + alphaYoffset;
                fArr = vertexArray;
                i2 = (numOfOneGroup * i) + 3;
                fArr[i2] = fArr[i2] + xoffset;
                fArr = vertexArray;
                i2 = (numOfOneGroup * i) + 4;
                fArr[i2] = fArr[i2] + alphaYoffset;
                if (i < numRect) {
                    if (vertexArray[(numOfOneGroup * i) + 4] < ((float) Y_OFFSET_LIMIT)) {
                        RandomRect(i);
                    }
                    alphaYoffset = (float) (((double) alphaYoffset) * 1.3d);
                    fArr = vertexRectArray;
                    i2 = i * 12;
                    fArr[i2] = fArr[i2] + xoffset;
                    fArr = vertexRectArray;
                    i2 = (i * 12) + 1;
                    fArr[i2] = fArr[i2] + alphaYoffset;
                    fArr = vertexRectArray;
                    i2 = (i * 12) + 3;
                    fArr[i2] = fArr[i2] + xoffset;
                    fArr = vertexRectArray;
                    i2 = (i * 12) + 4;
                    fArr[i2] = fArr[i2] + alphaYoffset;
                    fArr = vertexRectArray;
                    i2 = (i * 12) + 6;
                    fArr[i2] = fArr[i2] + xoffset;
                    fArr = vertexRectArray;
                    i2 = (i * 12) + 7;
                    fArr[i2] = fArr[i2] + alphaYoffset;
                    fArr = vertexRectArray;
                    i2 = (i * 12) + 9;
                    fArr[i2] = fArr[i2] + xoffset;
                    fArr = vertexRectArray;
                    i2 = (i * 12) + 10;
                    fArr[i2] = fArr[i2] + alphaYoffset;
                }
            }
        }
        initVertex();
    }

    protected void initVertex() {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertex = vbb.asFloatBuffer();
        vertex.put(vertexArray);
        vertex.position(0);
        ByteBuffer vbb2 = ByteBuffer.allocateDirect(vertexRectArray.length * 4);
        vbb2.order(ByteOrder.nativeOrder());
        vertexRect = vbb2.asFloatBuffer();
        vertexRect.put(vertexRectArray);
        vertexRect.position(0);
    }

    public void draw(GL10 gl, float xoffset, float yoffset) {
        gl.glFrontFace(2305);
        gl.glEnable(2884);
        gl.glCullFace(WeatherType.ACCU_WEATHER_MOSTLY_CLEAR);
        drawRect(gl, xoffset, yoffset);
        drawLinesRain(gl, xoffset, yoffset);
        gl.glDisable(2884);
    }

    protected void drawRect(GL10 gl, float xoffset, float yoffset) {
        gl.glEnableClientState(32884);
        changePostion(xoffset, yoffset);
        gl.glVertexPointer(RainSurfaceView.RAIN_LEVEL_DOWNPOUR, 5126, 0, vertexRect);
        for (int pos = 0; pos < numRect; pos++) {
            gl.glColor4f(colorArray[pos][0], colorArray[pos][1], colorArray[pos][2], mAlpha);
            gl.glDrawArrays(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER, pos * 4, RainSurfaceView.RAIN_LEVEL_RAINSTORM);
        }
        gl.glDisableClientState(32884);
        gl.glFinish();
    }

    protected void drawLinesRain(GL10 gl, float xoffset, float yoffset) {
        gl.glEnableClientState(32884);
        changePostion(xoffset, yoffset);
        gl.glVertexPointer(RainSurfaceView.RAIN_LEVEL_DOWNPOUR, 5126, 0, vertex);
        for (int pos = 0; pos < numLines; pos++) {
            gl.glLineWidth(widthArray[pos]);
            gl.glColor4f(colorArray[pos][0], colorArray[pos][1], colorArray[pos][2], this
                    .alphaArray[pos] * mAlpha);
            gl.glDrawArrays(1, pos * 2, RainSurfaceView.RAIN_LEVEL_SHOWER);
        }
        gl.glDisableClientState(32884);
        gl.glFinish();
    }
}
