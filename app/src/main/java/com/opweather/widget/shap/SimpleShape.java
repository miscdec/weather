package com.opweather.widget.shap;

import java.nio.FloatBuffer;

public abstract class SimpleShape extends BaseShape {
    private FloatBuffer mVerBuffer;
    private float[] mVertices;

    protected void initVertex() {
        mVerBuffer = floatToBuffer(mVertices);
    }

    public void setVertices(float[] vertices) {
        mVertices = vertices;
        initVertex();
    }

    public float[] getVertices() {
        return mVertices;
    }

    public FloatBuffer getByteBuffer() {
        return mVerBuffer;
    }
}
