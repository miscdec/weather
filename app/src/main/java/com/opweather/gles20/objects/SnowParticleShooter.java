package com.opweather.gles20.objects;

import android.opengl.Matrix;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.opweather.R;
import com.opweather.constants.GlobalConfig;
import com.opweather.gles20.util.Geometry;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SnowParticleShooter {
    private static final float ANGLE_VARIANCE_MAX = 45.0f;
    public static int Z_RANDOM_RANGE;
    protected float ALPHA_OFFSET_POINT;
    protected float RATE_ADD_PARTICLE;
    protected float RATE_OTHER_COLOR;
    protected float RATE_POLYHEDRON;
    protected float RATE_SPEED_MIDDLE;
    protected int SIZE_OFFSET_POINT;
    protected float SIZE_OFFSET_POLYHEDRON;
    protected int SIZE_RANGE_POINT;
    protected float SIZE_RANGE_POLYHEDRON;
    protected float SNOW_SPEED_MIDDLE;
    protected float SNOW_SPEED_SMALL;
    protected float SPEED_CHANGE_RANGE;
    protected float X_RANDOM_RANGE;
    protected float Y_RANDOM_RANGE;
    private float a_alpha;
    private int color;
    private int color1;
    private int color2;
    private float[] directionVector;
    protected boolean isAlpha;
    private final Random random;
    private float[] resultVector;
    private float[] rotationMatrix;
    private int size;
    private float[] snowPositions;
    private float speedAdjustment;
    private float x;
    private float y;
    private float z;

    static {
        Z_RANDOM_RANGE = 2;
    }

    public SnowParticleShooter(Geometry.Vector direction, int color1, int color2) {
        this.X_RANDOM_RANGE = 2.0f;
        this.Y_RANDOM_RANGE = 0.5f;
        this.SIZE_OFFSET_POINT = 5;
        this.SIZE_RANGE_POINT = 20;
        this.SIZE_OFFSET_POLYHEDRON = 0.018f;
        this.SIZE_RANGE_POLYHEDRON = 0.005f;
        this.RATE_POLYHEDRON = 0.08f;
        this.RATE_ADD_PARTICLE = 0.5f;
        this.SNOW_SPEED_MIDDLE = 0.4f;
        this.SNOW_SPEED_SMALL = 0.1f;
        this.SPEED_CHANGE_RANGE = 0.2f;
        this.RATE_SPEED_MIDDLE = 0.2f;
        this.ALPHA_OFFSET_POINT = 0.5f;
        this.isAlpha = true;
        this.RATE_OTHER_COLOR = 0.2f;
        this.size = 4;
        this.random = new Random();
        this.rotationMatrix = new float[16];
        this.directionVector = new float[4];
        this.resultVector = new float[4];
        this.color1 = color1;
        this.color2 = color2;
        Geometry.Vector localVector = direction.normalize();
        this.directionVector[0] = localVector.x;
        this.directionVector[1] = localVector.y;
        this.directionVector[2] = localVector.z;
    }

    public void changeSnowColor(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    public void addParticles(SnowParticleSystem particleSystem, float currentTime, int count) {
        int i = 0;
        while (i < count) {
            float nextFloat = this.random.nextFloat();
            nextFloat = this.random.nextFloat();
            if (nextFloat <= this.RATE_ADD_PARTICLE) {
                int i2;
                this.x = ((((float) this.random.nextInt(10000)) / 10000.0f) * this.X_RANDOM_RANGE) - (this
                        .X_RANDOM_RANGE / 2.0f);
                this.y = ((((float) this.random.nextInt(10000)) / 10000.0f) * this.Y_RANDOM_RANGE) + 1.0f;
                this.z = ((float) Z_RANDOM_RANGE) * ((((float) this.random.nextInt(10000)) / 10000.0f) - 0.5f);
                this.speedAdjustment = Math.abs(this.z) < this.RATE_SPEED_MIDDLE ? this.SNOW_SPEED_MIDDLE : this
                        .SNOW_SPEED_SMALL;
                if (this.random.nextFloat() > this.RATE_OTHER_COLOR) {
                    i2 = this.color2;
                } else {
                    i2 = this.color1;
                }
                this.color = i2;
                this.a_alpha = this.ALPHA_OFFSET_POINT - (Math.abs(this.z) * this.ALPHA_OFFSET_POINT);
                Matrix.setRotateEulerM(this.rotationMatrix, 0, (this.random.nextFloat() - 0.5f) * 45.0f, (this.random
                        .nextFloat() - 0.5f) * 45.0f, (this.random.nextFloat() - 0.5f) * 45.0f);
                Matrix.multiplyMV(this.resultVector, 0, this.rotationMatrix, 0, this.directionVector, 0);
                if (nextFloat > this.RATE_POLYHEDRON) {
                    this.size = (int) (((((float) this.random.nextInt(10000)) / 10000.0f) * ((float) this
                            .SIZE_RANGE_POINT)) + ((float) this.SIZE_OFFSET_POINT));
                    if (!this.isAlpha) {
                        float f;
                        this.a_alpha = (0.7f * ((float) this.size)) / ((float) this.SIZE_RANGE_POINT);
                        if (((float) this.size) < 15.0f) {
                            f = this.a_alpha / 1.5f;
                        } else {
                            f = this.a_alpha;
                        }
                        this.a_alpha = f;
                        this.speedAdjustment = ((this.speedAdjustment * ((float) this.size)) / ((float) this
                                .SIZE_RANGE_POINT)) + (((float) (this.random.nextInt(10000) / 10000)) * this
                                .SPEED_CHANGE_RANGE);
                    }
                    particleSystem.addPointParticle(new Geometry.Point(this.x, this.y, this.z), this.size, this
                            .color, new Geometry.Vector(this.resultVector[0] * this.speedAdjustment, this
                            .resultVector[1] * this.speedAdjustment, this.resultVector[2] * this.speedAdjustment),
                            currentTime, this.a_alpha);
                } else {
                    addSnowPosition();
                    this.a_alpha = Math.abs(this.z) < 0.2f ? 0.9f - Math.abs(this.z) : this.a_alpha;
                    if (!this.isAlpha) {
                        this.a_alpha = 1.0f;
                        this.color = this.color2;
                    }
                    particleSystem.addPolyhedronParticle(this.snowPositions, this.size, this.color, new Geometry
                            .Vector(this.resultVector[0] * this.speedAdjustment, this.resultVector[1] * this
                            .speedAdjustment, this.resultVector[2] * this.speedAdjustment), currentTime, this.a_alpha);
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void addSnowPosition() {
        this.z *= 0.8f;
        float aHalf = ((((float) this.random.nextInt(10000)) / 10000.0f) * this.SIZE_RANGE_POLYHEDRON) + this
                .SIZE_OFFSET_POLYHEDRON;
        float bHalf = aHalf * 0.618034f;
        ArrayList<Float> alVertix20 = new ArrayList();
        ArrayList<Integer> alFaceIndex20 = new ArrayList();
        initAlVertix20(alVertix20, aHalf, bHalf);
        initAlFaceIndex20(alFaceIndex20);
        this.snowPositions = cullVertex(alVertix20, alFaceIndex20);
    }

    private void initAlVertix20(ArrayList<Float> alVertix20, float aHalf, float bHalf) {
        alVertix20.add(Float.valueOf(this.x));
        alVertix20.add(Float.valueOf(this.y + aHalf));
        alVertix20.add(Float.valueOf(this.z - bHalf));
        alVertix20.add(Float.valueOf(this.x));
        alVertix20.add(Float.valueOf(this.y + aHalf));
        alVertix20.add(Float.valueOf(this.z + bHalf));
        alVertix20.add(Float.valueOf(this.x + aHalf));
        alVertix20.add(Float.valueOf(this.y + bHalf));
        alVertix20.add(Float.valueOf(this.z));
        alVertix20.add(Float.valueOf(this.x + bHalf));
        alVertix20.add(Float.valueOf(this.y));
        alVertix20.add(Float.valueOf(this.z - aHalf));
        alVertix20.add(Float.valueOf(this.x - bHalf));
        alVertix20.add(Float.valueOf(this.y));
        alVertix20.add(Float.valueOf(this.z - aHalf));
        alVertix20.add(Float.valueOf(this.x - aHalf));
        alVertix20.add(Float.valueOf(this.y + bHalf));
        alVertix20.add(Float.valueOf(this.z));
        alVertix20.add(Float.valueOf(this.x - bHalf));
        alVertix20.add(Float.valueOf(this.y));
        alVertix20.add(Float.valueOf(this.z + aHalf));
        alVertix20.add(Float.valueOf(this.x + bHalf));
        alVertix20.add(Float.valueOf(this.y));
        alVertix20.add(Float.valueOf(this.z + aHalf));
        alVertix20.add(Float.valueOf(this.x + aHalf));
        alVertix20.add(Float.valueOf(this.y - bHalf));
        alVertix20.add(Float.valueOf(this.z));
        alVertix20.add(Float.valueOf(this.x));
        alVertix20.add(Float.valueOf(this.y - aHalf));
        alVertix20.add(Float.valueOf(this.z - bHalf));
        alVertix20.add(Float.valueOf(this.x - aHalf));
        alVertix20.add(Float.valueOf(this.y - bHalf));
        alVertix20.add(Float.valueOf(this.z));
        alVertix20.add(Float.valueOf(this.x));
        alVertix20.add(Float.valueOf(this.y - aHalf));
        alVertix20.add(Float.valueOf(this.z + bHalf));
    }

    private void initAlFaceIndex20(ArrayList<Integer> alFaceIndex20) {
        alFaceIndex20.add(Integer.valueOf(0));
        alFaceIndex20.add(Integer.valueOf(1));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_SHOWER));
        alFaceIndex20.add(Integer.valueOf(0));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_SHOWER));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
        alFaceIndex20.add(Integer.valueOf(0));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
        alFaceIndex20.add(Integer.valueOf(0));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER));
        alFaceIndex20.add(Integer.valueOf(0));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER));
        alFaceIndex20.add(Integer.valueOf(1));
        alFaceIndex20.add(Integer.valueOf(1));
        alFaceIndex20.add(Integer.valueOf(6));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetLeft));
        alFaceIndex20.add(Integer.valueOf(1));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetLeft));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_SHOWER));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_SHOWER));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetLeft));
        alFaceIndex20.add(Integer.valueOf(ItemTouchHelper.RIGHT));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_SHOWER));
        alFaceIndex20.add(Integer.valueOf(ItemTouchHelper.RIGHT));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
        alFaceIndex20.add(Integer.valueOf(ItemTouchHelper.RIGHT));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetStart));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_DOWNPOUR));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetStart));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetStart));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_SUCC));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_RAINSTORM));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_SUCC));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_SUCC));
        alFaceIndex20.add(Integer.valueOf(6));
        alFaceIndex20.add(Integer.valueOf(RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER));
        alFaceIndex20.add(Integer.valueOf(6));
        alFaceIndex20.add(Integer.valueOf(1));
        alFaceIndex20.add(Integer.valueOf(6));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_FAIL));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetLeft));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetLeft));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_FAIL));
        alFaceIndex20.add(Integer.valueOf(ItemTouchHelper.RIGHT));
        alFaceIndex20.add(Integer.valueOf(ItemTouchHelper.RIGHT));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_FAIL));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetStart));
        alFaceIndex20.add(Integer.valueOf(R.styleable.Toolbar_contentInsetStart));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_FAIL));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_SUCC));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_SUCC));
        alFaceIndex20.add(Integer.valueOf(GlobalConfig.MESSAGE_GET_CURRENT_WEATHER_FAIL));
        alFaceIndex20.add(Integer.valueOf(6));
    }

    public static float[] cullVertex(ArrayList<Float> alv, ArrayList<Integer> alFaceIndex) {
        float[] vertices = new float[(alFaceIndex.size() * 3)];
        int i = 0;
        Iterator it = alFaceIndex.iterator();
        while (it.hasNext()) {
            int i2 = ((Integer) it.next()).intValue();
            int i3 = i + 1;
            vertices[i] = ((Float) alv.get(i2 * 3)).floatValue();
            i = i3 + 1;
            vertices[i3] = ((Float) alv.get((i2 * 3) + 1)).floatValue();
            i3 = i + 1;
            vertices[i] = ((Float) alv.get((i2 * 3) + 2)).floatValue();
            i = i3;
        }
        return vertices;
    }
}
