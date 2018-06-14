package com.opweather.gles20.objects;

import com.opweather.gles20.util.Geometry;

public class SnowParticleFlurry extends SnowParticleShooter {
    public SnowParticleFlurry(Geometry.Vector direction, int color1, int color2) {
        super(direction, color1, color2);
        this.Y_RANDOM_RANGE = 0.05f;
        this.SIZE_OFFSET_POLYHEDRON = 0.01f;
        this.SIZE_RANGE_POLYHEDRON = 0.005f;
        this.RATE_POLYHEDRON = 0.03f;
        this.RATE_ADD_PARTICLE = 0.25f;
        this.SNOW_SPEED_MIDDLE = 0.3f;
        this.SNOW_SPEED_SMALL = 0.15f;
        this.ALPHA_OFFSET_POINT = 0.5f;
        this.isAlpha = true;
    }
}
