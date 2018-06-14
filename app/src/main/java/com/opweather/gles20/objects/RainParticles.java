package com.opweather.gles20.objects;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.support.v4.widget.AutoScrollHelper;

import com.opweather.constants.GlobalConfig;
import com.opweather.gles20.VertexArray;
import com.opweather.gles20.programs.RainShaderProgram;
import com.opweather.gles20.util.Geometry.Vector;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.Random;

public class RainParticles {
    private static final int COLOR_COMPONENT_COUNT = 3;
    protected static final int LINE_STRIDE_DATA = 12;
    private static final int POINT_COUNT_FOR_LINE = 2;
    private static final int POINT_COUNT_FOR_RECT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    protected static final int RECT_STRIDE_DATA = 24;
    private static final int STRIDE = 24;
    protected static final int TOTAL_COMPONENT_COUNT = 6;
    public static int Z_RANDOM_RANGE;
    protected float FIRST_HEIGHT_CHANGE_RANGE;
    protected float HEIGHT_CHANGE_RANGE;
    protected float LINE_HEIGHT_START_OFFSET;
    protected float LINE_WIDTH_START_OFFSET;
    protected int MAX_RAIN_DROPS_COUNT;
    protected float RAIN_SPEED;
    protected float RECT_HEIGHT_START_OFFSET;
    protected float RECT_WIDTH_START_OFFSET;
    protected float WIDTH_CHANGE_RANGE;
    protected float X_RANDOM_RANGE;
    protected float Y_OFFSET_LIMIT;
    protected float Y_RANDOM_RANGE;
    private int color1;
    private int color2;
    private Vector directionVector;
    protected float[] lineParticles;
    protected float[] lineSpeeds;
    private VertexArray lineVertexArray;
    protected float[] lineWidths;
    Context mContext;
    protected int numLines;
    protected int numOfGroup;
    protected int numRect;
    private float rainAlpha;
    private int rainColor;
    private final Random random;
    protected float[] rectParticles;
    private VertexArray rectVertexArray;

    static {
        Z_RANDOM_RANGE = 2;
    }

    public RainParticles(Vector direction, int color1, int color2, Context mContext) {
        this.numLines = 200;
        this.numRect = 2;
        this.RAIN_SPEED = 0.06f;
        this.X_RANDOM_RANGE = 2.0f;
        this.Y_RANDOM_RANGE = 0.5f;
        this.Y_OFFSET_LIMIT = -1.5f;
        this.RECT_HEIGHT_START_OFFSET = 2.5f;
        this.RECT_WIDTH_START_OFFSET = 0.019f;
        this.LINE_HEIGHT_START_OFFSET = 0.33333334f;
        this.LINE_WIDTH_START_OFFSET = 0.2f;
        this.HEIGHT_CHANGE_RANGE = 0.1f;
        this.WIDTH_CHANGE_RANGE = 5.0f;
        this.random = new Random();
        this.MAX_RAIN_DROPS_COUNT = 500;
        this.numOfGroup = 5;
        this.rainAlpha = 1.0f;
        this.color1 = color1;
        this.color2 = color2;
        this.mContext = mContext;
        init(direction);
    }

    protected void init(Vector direction) {
        int i;
        this.directionVector = direction.normalize().scale(this.RAIN_SPEED);
        this.rectParticles = new float[(this.numRect * 24)];
        this.lineParticles = new float[(this.numLines * 12)];
        this.lineVertexArray = new VertexArray(this.lineParticles);
        this.rectVertexArray = new VertexArray(this.rectParticles);
        this.lineSpeeds = new float[this.numLines];
        this.lineWidths = new float[this.numLines];
        this.FIRST_HEIGHT_CHANGE_RANGE = 2.0f;
        for (i = 0; i < this.numLines; i++) {
            addLineParticles(i, this.FIRST_HEIGHT_CHANGE_RANGE);
        }
        for (i = 0; i < this.numRect; i++) {
            addRectParticles(i, this.FIRST_HEIGHT_CHANGE_RANGE, AutoScrollHelper.RELATIVE_UNSPECIFIED,
                    AutoScrollHelper.RELATIVE_UNSPECIFIED);
        }
        this.FIRST_HEIGHT_CHANGE_RANGE = 1.0f;
    }

    public void changeRainColor(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    public void addRectParticles(int index, float yMagnification, float xRotation, float zRotation) {
        float x = ((((float) this.random.nextInt(10000)) / 10000.0f) * this.X_RANDOM_RANGE) - (this.X_RANDOM_RANGE /
                2.0f);
        float y = (((((float) this.random.nextInt(10000)) / 10000.0f) * this.Y_RANDOM_RANGE) + 1.0f) * yMagnification;
        float z = (((float) Z_RANDOM_RANGE) * ((((float) this.random.nextInt(10000)) / 10000.0f) - 0.5f)) * 0.5f;
        this.rainColor = this.random.nextInt(POSITION_COMPONENT_COUNT) > 0 ? this.color2 : this.color1;
        float redColor = (float) (Color.red(this.rainColor) / 255);
        float greenColor = ((float) Color.green(this.rainColor)) / 255.0f;
        float blueColor = ((float) Color.blue(this.rainColor)) / 255.0f;
        float widht = this.RECT_WIDTH_START_OFFSET + (((float) this.random.nextInt(GlobalConfig
                .MESSAGE_GET_CURRENT_WEATHER_SUCC)) / 10000.0f);
        float radio = this.RECT_HEIGHT_START_OFFSET + ((((float) this.random.nextInt(10000)) / 10000.0f) * 0.5f);
        float xOffsetDegree = (float) Math.tan(((double) (45.0f - (Math.abs(zRotation) / 2.0f))) *
                0.017453292519943295d);
        float zOffsetDegree = 1.0f - xOffsetDegree;
        if ((zRotation > 0.0f && xRotation > 0.0f) || (zRotation < 0.0f && xRotation < 0.0f)) {
            xOffsetDegree = -xOffsetDegree;
        }
        int currentIndex = index * 24;
        int currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = x;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = y;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = z;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = redColor;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = greenColor;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = blueColor;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = x;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = (this.HEIGHT_CHANGE_RANGE * radio) + y;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = z;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = redColor;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = greenColor;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = blueColor;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = (widht * xOffsetDegree) + x;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = y;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = (widht * zOffsetDegree) + z;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = redColor;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = greenColor;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = blueColor;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = (widht * xOffsetDegree) + x;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = (this.HEIGHT_CHANGE_RANGE * radio) + y;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = (widht * zOffsetDegree) + z;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = redColor;
        currentIndex2 = currentIndex + 1;
        this.rectParticles[currentIndex] = greenColor;
        currentIndex = currentIndex2 + 1;
        this.rectParticles[currentIndex2] = blueColor;
    }

    protected void addLineParticles(int index, float yMagnification) {
        float x = ((((float) this.random.nextInt(10000)) / 10000.0f) * this.X_RANDOM_RANGE) - (this.X_RANDOM_RANGE /
                2.0f);
        float y = ((((((float) this.random.nextInt(10000)) / 10000.0f) * this.Y_RANDOM_RANGE) * yMagnification) +
                1.0f) * yMagnification;
        float randomZ = (((float) this.random.nextInt(10000)) / 10000.0f) - 0.5f;
        float z = ((float) Z_RANDOM_RANGE) * randomZ;
        this.rainColor = this.random.nextInt(POSITION_COMPONENT_COUNT) > 0 ? this.color2 : this.color1;
        float redColor = (float) (Color.red(this.rainColor) / 255);
        float greenColor = ((float) Color.green(this.rainColor)) / 255.0f;
        float blueColor = ((float) Color.blue(this.rainColor)) / 255.0f;
        float speed = 1.0f - Math.abs(randomZ / 0.5f);
        float[] fArr = this.lineSpeeds;
        if (speed <= 0.5f) {
            speed = 0.5f;
        }
        fArr[index] = speed;
        float radio = this.LINE_HEIGHT_START_OFFSET + (((float) this.random.nextInt(10000)) / 10000.0f);
        int currentIndex = index * 12;
        int currentIndex2 = currentIndex + 1;
        this.lineParticles[currentIndex] = x;
        currentIndex = currentIndex2 + 1;
        this.lineParticles[currentIndex2] = y;
        currentIndex2 = currentIndex + 1;
        this.lineParticles[currentIndex] = z;
        currentIndex = currentIndex2 + 1;
        this.lineParticles[currentIndex2] = redColor;
        currentIndex2 = currentIndex + 1;
        this.lineParticles[currentIndex] = greenColor;
        currentIndex = currentIndex2 + 1;
        this.lineParticles[currentIndex2] = blueColor;
        currentIndex2 = currentIndex + 1;
        this.lineParticles[currentIndex] = x;
        currentIndex = currentIndex2 + 1;
        this.lineParticles[currentIndex2] = (this.HEIGHT_CHANGE_RANGE * radio) + y;
        currentIndex2 = currentIndex + 1;
        this.lineParticles[currentIndex] = z;
        currentIndex = currentIndex2 + 1;
        this.lineParticles[currentIndex2] = redColor;
        currentIndex2 = currentIndex + 1;
        this.lineParticles[currentIndex] = greenColor;
        currentIndex = currentIndex2 + 1;
        this.lineParticles[currentIndex2] = blueColor;
        this.lineWidths[index] = this.WIDTH_CHANGE_RANGE * (this.LINE_WIDTH_START_OFFSET + (((float) this.random
                .nextInt(10000)) / 10000.0f));
    }

    private void bindData(VertexArray vertexArray, RainShaderProgram particleProgram) {
        vertexArray.setVertexAttribPointer(0, particleProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        int dataOffset = 0 + 3;
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getColorAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += 3;
    }

    public void changePosition(float currentTime, float[] modelProjectionMatrix, int rainDropsTextture, float alpha,
                               float xRotation, float zRotation, float[] modleMatrix) {
        int i;
        for (i = 0; i < this.numLines; i++) {
            if (this.lineParticles[(i * 12) + 1] < this.Y_OFFSET_LIMIT) {
                addLineParticles(i, this.FIRST_HEIGHT_CHANGE_RANGE);
            } else {
                float directionx = this.directionVector.x * this.lineSpeeds[i];
                float directiony = this.directionVector.y * this.lineSpeeds[i];
                float[] fArr = this.lineParticles;
                int i2 = i * 12;
                fArr[i2] = fArr[i2] + directionx;
                fArr = this.lineParticles;
                i2 = (i * 12) + 1;
                fArr[i2] = fArr[i2] + directiony;
                fArr = this.lineParticles;
                i2 = (i * 12) + 6;
                fArr[i2] = fArr[i2] + directionx;
                fArr = this.lineParticles;
                i2 = ((i * 12) + 6) + 1;
                fArr[i2] = fArr[i2] + directiony;
            }
        }
        for (i = 0; i < this.numRect; i++) {
            if (this.rectParticles[(i * 24) + 1] < this.Y_OFFSET_LIMIT) {
                addRectParticles(i, this.FIRST_HEIGHT_CHANGE_RANGE, xRotation, zRotation);
            } else {
                float directionx = this.directionVector.x * this.lineSpeeds[i];
                float directiony = this.directionVector.y * this.lineSpeeds[i];
                float[] fArr = this.rectParticles;
                int i2 = i * 24;
                fArr[i2] = fArr[i2] + directionx;
                fArr = this.rectParticles;
                i2 = (i * 24) + 1;
                fArr[i2] = fArr[i2] + directiony;
                fArr = this.rectParticles;
                i2 = (i * 24) + 6;
                fArr[i2] = fArr[i2] + directionx;
                fArr = this.rectParticles;
                i2 = (i * 24) + 7;
                fArr[i2] = fArr[i2] + directiony;
                fArr = this.rectParticles;
                i2 = (i * 24) + 12;
                fArr[i2] = fArr[i2] + directionx;
                fArr = this.rectParticles;
                i2 = (i * 24) + 13;
                fArr[i2] = fArr[i2] + directiony;
                fArr = this.rectParticles;
                i2 = (i * 24) + 18;
                fArr[i2] = fArr[i2] + directionx;
                fArr = this.rectParticles;
                i2 = (i * 24) + 19;
                fArr[i2] = fArr[i2] + directiony;
            }
        }
        this.lineVertexArray.updateBuffer(this.lineParticles, 0, (this.numLines * 6) * 2);
        this.rectVertexArray.updateBuffer(this.rectParticles, 0, (this.numRect * 6) * 4);
    }

    public void draw(RainShaderProgram particleProgram) {
        int i;
        bindData(this.lineVertexArray, particleProgram);
        for (i = 0; i < this.numLines; i++) {
            GLES20.glLineWidth(this.lineWidths[i]);
            GLES20.glDrawArrays(1, i * 2, POINT_COUNT_FOR_LINE);
        }
        if (this.numRect != 0) {
            bindData(this.rectVertexArray, particleProgram);
            for (i = 0; i < this.numRect; i++) {
                GLES20.glDrawArrays(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER, i * 4, POINT_COUNT_FOR_RECT);
            }
        }
    }

    public void setAlpha(float alpha) {
        this.rainAlpha = alpha;
    }

    public float getAlpha() {
        return this.rainAlpha;
    }
}
