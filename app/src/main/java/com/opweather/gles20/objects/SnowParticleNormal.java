package com.opweather.gles20.objects;

import com.opweather.gles20.util.Geometry;

public class SnowParticleNormal extends SnowParticleShooter {
    public SnowParticleNormal(Geometry.Vector direction, int color1, int color2) {
        super(direction, color1, color2);
        this.Y_RANDOM_RANGE = 0.05f;
        this.SIZE_OFFSET_POLYHEDRON = 0.018f;
        this.SIZE_RANGE_POLYHEDRON = 0.005f;
        this.RATE_POLYHEDRON = 0.06f;
        this.RATE_ADD_PARTICLE = 0.4f;
        this.SNOW_SPEED_MIDDLE = 0.45f;
        this.SNOW_SPEED_SMALL = 0.15f;
        this.ALPHA_OFFSET_POINT = 0.35f;
        this.isAlpha = true;
    }
}
