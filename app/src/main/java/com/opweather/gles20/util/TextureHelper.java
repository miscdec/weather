package com.opweather.gles20.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.android.volley.DefaultRetryPolicy;

public class TextureHelper {
    private static final String TAG = "TextureHelper";
    private static final int TEXTURE_MAX_ANISOTROPY_EXT = 34046;

    public enum FilterMode {
        NEAREST_NEIGHBOUR,
        BILINEAR,
        BILINEAR_WITH_MIPMAPS,
        TRILINEAR,
        ANISOTROPIC
    }

    public static int loadTexture(Context context, int resourceId) {
        int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            return 0;
        }
        Options options = new Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        GLES20.glBindTexture(3553, textureObjectIds[0]);
        GLES20.glTexParameteri(3553, 10241, 9987);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        GLES20.glGenerateMipmap(3553);
        bitmap.recycle();
        GLES20.glBindTexture(3553, 0);
        return textureObjectIds[0];
    }

    public static int loadCubeMap(Context context, int[] cubeResources) {
        int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            return 0;
        }
        Options options = new Options();
        options.inScaled = false;
        Bitmap[] cubeBitmaps = new Bitmap[6];
        for (int i = 0; i < 6; i++) {
            cubeBitmaps[i] = BitmapFactory.decodeResource(context.getResources(), cubeResources[i], options);
            if (cubeBitmaps[i] == null) {
                GLES20.glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }
        GLES20.glBindTexture(34067, textureObjectIds[0]);
        GLES20.glTexParameteri(34067, 10241, 9729);
        GLES20.glTexParameteri(34067, 10240, 9729);
        GLUtils.texImage2D(34070, 0, cubeBitmaps[0], 0);
        GLUtils.texImage2D(34069, 0, cubeBitmaps[1], 0);
        GLUtils.texImage2D(34072, 0, cubeBitmaps[2], 0);
        GLUtils.texImage2D(34071, 0, cubeBitmaps[3], 0);
        GLUtils.texImage2D(34074, 0, cubeBitmaps[4], 0);
        GLUtils.texImage2D(34073, 0, cubeBitmaps[5], 0);
        GLES20.glBindTexture(3553, 0);
        int length = cubeBitmaps.length;
        for (int i2 = 0; i2 < length; i2++) {
            cubeBitmaps[i2].recycle();
        }
        return textureObjectIds[0];
    }

    public static void adjustTextureFilters(int textureId, FilterMode filterMode, boolean supportsAnisotropicFiltering, float maxAnisotropy) {
        GLES20.glBindTexture(3553, textureId);
        if (supportsAnisotropicFiltering) {
            if (filterMode == FilterMode.ANISOTROPIC) {
                GLES20.glTexParameterf(3553, TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);
            } else {
                GLES20.glTexParameterf(3553, TEXTURE_MAX_ANISOTROPY_EXT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        }
        if (filterMode == FilterMode.NEAREST_NEIGHBOUR) {
            GLES20.glTexParameteri(3553, 10241, 9728);
            GLES20.glTexParameteri(3553, 10240, 9728);
        } else {
            GLES20.glTexParameteri(3553, 10240, 9729);
            if (filterMode == FilterMode.BILINEAR) {
                GLES20.glTexParameteri(3553, 10241, 9729);
            } else if (filterMode == FilterMode.BILINEAR_WITH_MIPMAPS) {
                GLES20.glTexParameteri(3553, 10241, 9985);
            } else {
                GLES20.glTexParameteri(3553, 10241, 9987);
            }
        }
        GLES20.glBindTexture(3553, 0);
    }
}
