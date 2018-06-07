package com.opweather.widget.shap;

import com.opweather.widget.anim.BaseAnimation;
import com.opweather.widget.anim.SnowAnimation;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Icosahedron extends SimpleShape {
    float X;
    float Z;
    private ShortBuffer indexBuffer;
    short[] indices;
    private float mRadius;
    private BaseAnimation mSnowAnimation;
    float[] vertices;

    public Icosahedron() {
        this(-0.5f, 1.0f, 0.0f);
    }

    public Icosahedron(float x, float y, float z) {
        X = 0.5257311f;
        Z = 0.8506508f;
        mRadius = 0.08f;
        indices = new short[]{(short) 0, (short) 4, (short) 1, (short) 0, (short) 9, (short) 4, (short) 9, (short) 5,
                (short) 4, (short) 4, (short) 5, (short) 8, (short) 4, (short) 8, (short) 1, (short) 8, (short) 10,
                (short) 1, (short) 8, (short) 3, (short) 10, (short) 5, (short) 3, (short) 8, (short) 5, (short) 2,
                (short) 3, (short) 2, (short) 7, (short) 3, (short) 7, (short) 10, (short) 3, (short) 7, (short) 6,
                (short) 10, (short) 7, (short) 11, (short) 6, (short) 11, (short) 0, (short) 6, (short) 0, (short) 1,
                (short) 6, (short) 6, (short) 1, (short) 10, (short) 9, (short) 0, (short) 11, (short) 9, (short) 11,
                (short) 2, (short) 9, (short) 2, (short) 5, (short) 7, (short) 2, (short) 11};
        mSnowAnimation = new SnowAnimation();
        setXYZ(x, y, z);
        X *= mRadius;
        Z *= mRadius;
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
        init(x, y, z);
    }

    public void setAnimation(BaseAnimation anim) {
        mSnowAnimation = anim;
    }

    public void draw(GL10 gl) {
        super.draw(gl);
        gl.glVertexPointer(RainSurfaceView.RAIN_LEVEL_DOWNPOUR, 5126, 0, getByteBuffer());
        gl.glDrawElements(RainSurfaceView.RAIN_LEVEL_RAINSTORM, indices.length, 5123, indexBuffer);
    }

    public void init(float x, float y, float z) {
        setXYZ(x, y, z);
        vertices = new float[]{(-X) + getX(), getY() + 0.0f, Z + getZ(), X + getX(), getY() + 0.0f, Z + getZ(), (-X)
                + getX(), getY() + 0.0f, (-Z) + getZ(), X + getX(), getY() + 0.0f, (-Z) + getZ(), getX() + 0.0f, Z +
                getY(), X + getZ(), getX() + 0.0f, Z + getY(), (-X) + getZ(), getX() + 0.0f, (-Z) + getY(), X + getZ
                (), getX() + 0.0f, (-Z) + getY(), (-X) + getZ(), Z + getX(), X + getY(), getZ() + 0.0f, (-Z) + getX()
                , X + getY(), getZ() + 0.0f, Z + getX(), (-X) + getY(), getZ() + 0.0f, (-Z) + getX(), (-X) + getY(),
                getZ() + 0.0f};
        setVertices(vertices);
    }

    public void move() {
        float[] speed = mSnowAnimation.next();
        init(getX() + speed[0], getY() + speed[1], getZ() + speed[2]);
    }
}
