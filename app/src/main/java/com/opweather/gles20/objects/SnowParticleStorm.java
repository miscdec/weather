package com.opweather.gles20.objects;


import com.opweather.gles20.util.Geometry;

public class SnowParticleStorm extends SnowParticleShooter {
    public SnowParticleStorm(Geometry.Vector direction, int color1, int color2) {
        super(direction, color1, color2);
        this.Y_RANDOM_RANGE = 0.5f;
        this.SIZE_OFFSET_POLYHEDRON = 0.01f;
        this.SIZE_RANGE_POLYHEDRON = 0.015f;
        this.RATE_POLYHEDRON = 0.04f;
        this.RATE_ADD_PARTICLE = 1.0f;
        this.SNOW_SPEED_MIDDLE = 2.0f;
        this.SNOW_SPEED_SMALL = 0.5f;
        this.isAlpha = false;
    }
}
