package com.opweather.gles20.objects;

import com.opweather.gles20.util.Geometry;

import java.util.Random;

public class SandParticleShooter {
    private static final float SAND_GAP_HUGE = 0.138f;
    private static final float SAND_GAP_LARGE = 0.038f;
    private static final float SAND_GAP_MIDDLE = 0.018f;
    private static final float SAND_GAP_SMALL = 0.012f;
    private static final int SAND_SIZE_HUGE = 96;
    private static final int SAND_SIZE_LARGE = 18;
    private static final int SAND_SIZE_MIDDLE = 9;
    private static final int SAND_SIZE_SMALL = 5;
    private static final float SAND_SPEED_HUGE = 9.0f;
    private static final float SAND_SPEED_LARGE = 7.0f;
    private static final float SAND_SPEED_MIDDLE = 2.0f;
    private static final float SAND_SPEED_SMALL = 1.0f;
    private int color1;
    private int color2;
    private final Geometry.Vector directionVector;
    private final Random random;

    public SandParticleShooter(Geometry.Vector direction, int color1, int color2) {
        this.random = new Random();
        this.directionVector = direction.normalize();
        this.color1 = color1;
        this.color2 = color2;
    }

    public void changeSandColor(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    public void addParticles(SandParticleSystem particleSystem, float currentTime, int count) {
        for (int i = 0; i < count; i++) {
            int size;
            float speedAdjustment;
            float sandGap;
            Geometry.Point point = new Geometry.Point(-2.0f, (this.random.nextFloat() * 4.0f) - 2.0f, (this.random
                    .nextFloat() * 2.0f) - 1.0f);
            boolean dark = this.random.nextBoolean();
            float prob = this.random.nextFloat();
            if (prob < 0.003f) {
                size = SAND_SIZE_HUGE;
                speedAdjustment = SAND_SPEED_HUGE;
                sandGap = SAND_GAP_HUGE;
            } else if (prob < 0.083f) {
                size = SAND_SIZE_LARGE;
                speedAdjustment = SAND_SPEED_LARGE;
                sandGap = SAND_GAP_LARGE;
            } else if (prob < 0.303f) {
                size = SAND_SIZE_MIDDLE;
                speedAdjustment = SAND_SPEED_MIDDLE;
                sandGap = SAND_GAP_MIDDLE;
            } else {
                size = SAND_SIZE_SMALL;
                speedAdjustment = SAND_SPEED_SMALL;
                sandGap = SAND_GAP_SMALL;
            }
            Geometry.Vector thisDirection = this.directionVector.scale(speedAdjustment);
            for (int j = 0; j < 6; j++) {
                point = point.translate(this.directionVector.scale(sandGap));
                if (this.random.nextFloat() > 0.1f) {
                    int i2;
                    if (dark) {
                        i2 = this.color1;
                    } else {
                        i2 = this.color2;
                    }
                    particleSystem.addParticle(point, size, i2, thisDirection, currentTime);
                }
            }
        }
    }
}
