package com.opweather.gles20.objects;

import android.graphics.Color;
import android.opengl.GLES20;

import com.opweather.gles20.VertexArray;
import com.opweather.gles20.programs.SnowShaderProgram;
import com.opweather.gles20.util.Geometry;
import com.opweather.gles20.util.Geometry.Vector;
import com.opweather.widget.openglbase.RainSurfaceView;

public class SnowParticleSystem {
    private static final int ALPHA_COMPONENT_COUNT = 1;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int POINT_FOR_POLYHEDRON = 60;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int SIZE_COMPONENT_COUNT = 1;
    private static final int STRIDE = 48;
    private static final int TOTAL_COMPONENT_COUNT = 12;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private int currentPointCount;
    private int currentPolyhedronCount;
    private final int maxPointCount;
    private final int maxPolyhedronCount;
    private int nextPoint;
    private int nextPolyhedron;
    private final float[] pointParticles;
    private final VertexArray pointVertexArray;
    private final float[] polyhedronParticles;
    private final VertexArray polyhedronVertexArray;

    public SnowParticleSystem(int maxPolyhedronCount, int maxPointCount) {
        this.polyhedronParticles = new float[((maxPolyhedronCount * 12) * 60)];
        this.pointParticles = new float[(maxPointCount * 12)];
        this.polyhedronVertexArray = new VertexArray(this.polyhedronParticles);
        this.pointVertexArray = new VertexArray(this.pointParticles);
        this.maxPolyhedronCount = maxPolyhedronCount;
        this.maxPointCount = maxPointCount;
    }

    public void addPolyhedronParticle(float[] snowPositions, int size, int color, Vector direction, float
            particleStartTime, float alpha) {
        int particleOffset = (this.nextPolyhedron * 12) * 60;
        int currentOffset = particleOffset;
        this.nextPolyhedron++;
        if (this.currentPolyhedronCount < this.maxPolyhedronCount) {
            this.currentPolyhedronCount++;
        }
        if (this.nextPolyhedron == this.maxPolyhedronCount) {
            this.nextPolyhedron = 0;
        }
        int i = currentOffset;
        for (int i2 = 0; i2 < 60; i2++) {
            currentOffset = i + 1;
            this.polyhedronParticles[i] = snowPositions[i2 * 3];
            i = currentOffset + 1;
            this.polyhedronParticles[currentOffset] = snowPositions[(i2 * 3) + 1];
            currentOffset = i + 1;
            this.polyhedronParticles[i] = snowPositions[(i2 * 3) + 2];
            i = currentOffset + 1;
            this.polyhedronParticles[currentOffset] = ((float) Color.red(color)) / 255.0f;
            currentOffset = i + 1;
            this.polyhedronParticles[i] = ((float) Color.green(color)) / 255.0f;
            i = currentOffset + 1;
            this.polyhedronParticles[currentOffset] = ((float) Color.blue(color)) / 255.0f;
            currentOffset = i + 1;
            this.polyhedronParticles[i] = direction.x;
            i = currentOffset + 1;
            this.polyhedronParticles[currentOffset] = direction.y;
            currentOffset = i + 1;
            this.polyhedronParticles[i] = direction.z;
            i = currentOffset + 1;
            this.polyhedronParticles[currentOffset] = particleStartTime;
            currentOffset = i + 1;
            this.polyhedronParticles[i] = (float) size;
            i = currentOffset + 1;
            this.polyhedronParticles[currentOffset] = alpha;
        }
        this.polyhedronVertexArray.updateBuffer(this.polyhedronParticles, particleOffset, 720);
    }

    public void addPointParticle(Geometry.Point point, int size, int color, Vector direction, float
            particleStartTime, float alpha) {
        int particleOffset = this.nextPoint * 12;
        int i = particleOffset;
        this.nextPoint++;
        if (this.currentPointCount < this.maxPointCount) {
            this.currentPointCount++;
        }
        if (this.nextPoint == this.maxPointCount) {
            this.nextPoint = 0;
        }
        int i2 = i + 1;
        this.pointParticles[i] = point.x;
        i = i2 + 1;
        this.pointParticles[i2] = point.y;
        i2 = i + 1;
        this.pointParticles[i] = point.z;
        i = i2 + 1;
        this.pointParticles[i2] = ((float) Color.red(color)) / 255.0f;
        i2 = i + 1;
        this.pointParticles[i] = ((float) Color.green(color)) / 255.0f;
        i = i2 + 1;
        this.pointParticles[i2] = ((float) Color.blue(color)) / 255.0f;
        i2 = i + 1;
        this.pointParticles[i] = direction.x;
        i = i2 + 1;
        this.pointParticles[i2] = direction.y;
        i2 = i + 1;
        this.pointParticles[i] = direction.z;
        i = i2 + 1;
        this.pointParticles[i2] = particleStartTime;
        i2 = i + 1;
        this.pointParticles[i] = (float) size;
        i = i2 + 1;
        this.pointParticles[i2] = alpha;
        this.pointVertexArray.updateBuffer(this.pointParticles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(SnowShaderProgram particleProgram, VertexArray vertexArray) {
        vertexArray.setVertexAttribPointer(0, particleProgram.getPositionAttributeLocation(), VECTOR_COMPONENT_COUNT,
                STRIDE);
        int dataOffset = 0 + 3;
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getColorAttributeLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += 3;
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getDirectionVectorAttributeLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += 3;
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getParticleStartTimeAttributeLocation(),
                SIZE_COMPONENT_COUNT, STRIDE);
        dataOffset++;
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getSizeAttributeLocation(),
                SIZE_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttribPointer(dataOffset + 1, particleProgram.getAlphaAttributeLocation(),
                SIZE_COMPONENT_COUNT, STRIDE);
    }

    public void drawPoint(SnowShaderProgram particleProgram) {
        bindData(particleProgram, this.pointVertexArray);
        GLES20.glDrawArrays(0, 0, this.currentPointCount);
    }

    public void drawPolyhedron(SnowShaderProgram particleProgram) {
        bindData(particleProgram, this.polyhedronVertexArray);
        GLES20.glDrawArrays(RainSurfaceView.RAIN_LEVEL_RAINSTORM, 0, this.currentPolyhedronCount * 60);
    }
}
