package com.opweather.gles20;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        this.floatBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
        this.floatBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, 5126, false, stride, this.floatBuffer);
        GLES20.glEnableVertexAttribArray(attributeLocation);
        this.floatBuffer.position(0);
    }

    public void updateBuffer(float[] vertexData, int start, int count) {
        this.floatBuffer.position(start);
        this.floatBuffer.put(vertexData, start, count);
        this.floatBuffer.position(0);
    }
}
