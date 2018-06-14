package com.opweather.gles20.objects;

import android.graphics.Color;
import android.opengl.GLES20;

import com.opweather.gles20.VertexArray;
import com.opweather.gles20.programs.SandShaderProgram;
import com.opweather.gles20.util.Geometry;


public class SandParticleSystem {
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int SIZE_COMPONENT_COUNT = 1;
    private static final int STRIDE = 44;
    private static final int TOTAL_COMPONENT_COUNT = 11;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private int currentParticleCount;
    private final int maxParticleCount;
    private int nextParticle;
    private final float[] particles;
    private final VertexArray vertexArray;

    public SandParticleSystem(int maxParticleCount) {
        this.particles = new float[(maxParticleCount * 11)];
        this.vertexArray = new VertexArray(this.particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point position, int size, int color, Geometry.Vector direction, float
            particleStartTime) {
        int particleOffset = this.nextParticle * 11;
        int i = particleOffset;
        this.nextParticle++;
        if (this.currentParticleCount < this.maxParticleCount) {
            this.currentParticleCount++;
        }
        if (this.nextParticle == this.maxParticleCount) {
            this.nextParticle = 0;
        }
        int i2 = i + 1;
        this.particles[i] = position.x;
        i = i2 + 1;
        this.particles[i2] = position.y;
        i2 = i + 1;
        this.particles[i] = position.z;
        i = i2 + 1;
        this.particles[i2] = ((float) Color.red(color)) / 255.0f;
        i2 = i + 1;
        this.particles[i] = ((float) Color.green(color)) / 255.0f;
        i = i2 + 1;
        this.particles[i2] = ((float) Color.blue(color)) / 255.0f;
        i2 = i + 1;
        this.particles[i] = direction.x;
        i = i2 + 1;
        this.particles[i2] = direction.y;
        i2 = i + 1;
        this.particles[i] = direction.z;
        i = i2 + 1;
        this.particles[i2] = particleStartTime;
        i2 = i + 1;
        this.particles[i] = (float) size;
        this.vertexArray.updateBuffer(this.particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(SandShaderProgram particleProgram) {
        this.vertexArray.setVertexAttribPointer(0, particleProgram.getPositionAttributeLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        int dataOffset = 0 + 3;
        this.vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getColorAttributeLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += 3;
        this.vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getDirectionVectorAttributeLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += 3;
        this.vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getParticleStartTimeAttributeLocation(),
                SIZE_COMPONENT_COUNT, STRIDE);
        this.vertexArray.setVertexAttribPointer(dataOffset + 1, particleProgram.getSizeAttributeLocation(),
                SIZE_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(0, 0, this.currentParticleCount);
    }
}
