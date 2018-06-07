package com.opweather.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.opweather.ui.BaseApplication;
import com.opweather.widget.WeatherCircleView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    private static final int MEMORY_SIZE_LIMIT = 20971520;
    public static final String SHARE_IMAGE_PATH = Environment.getExternalStorageDirectory() + "/OPWeather/";
    private static final long SHARE_IMAGE_SIZE_LIMIT = 4194304;
    private static final String TAG = "BitmapUtils";
    private static final int PRIORITY_HIGH_ACCURACY = 100;

    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        System.out.println("h:" + h);
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Config.RGB_565);
        scrollView.draw(new Canvas(bitmap));
        return bitmap;
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, PRIORITY_HIGH_ACCURACY, baos);
        int options = PRIORITY_HIGH_ACCURACY;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        return BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);
    }

    private static File compressBitmap(Bitmap bitmap, File file, CompressFormat format, int quality) {
        FileOutputStream fos2 = null;
        try {
            fos2 = new FileOutputStream(file);
            if (!bitmap.compress(format, quality, fos2)) {
                Log.e(TAG, "compressBitmap photo fail.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "FileNotFoundException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "compressBitmap photo fail." + e.getMessage());
        } finally {
            if (fos2 != null) {
                try {
                    fos2.flush();
                    fos2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;

    }

    public static void savePic(Bitmap b, String fname) {
        if (!TextUtils.isEmpty(fname)) {
            new File(fname).deleteOnExit();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fname);
                if (!b.compress(CompressFormat.PNG, WeatherCircleView.START_ANGEL_90, fos)) {
                    Log.e(TAG, "compressBitmap photo fail.");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void savePicByLimit(Bitmap bitmap, String fname) {
        if (!TextUtils.isEmpty(fname)) {
            File pngFile = new File(fname);
            pngFile.deleteOnExit();
            FileOutputStream fos2 = null;
            try {
                fos2 = new FileOutputStream(pngFile);
                if (!bitmap.compress(CompressFormat.PNG, PRIORITY_HIGH_ACCURACY, fos2)) {
                    Log.e(TAG, "compressBitmap photo fail.");
                }
                if (pngFile.length() > 4194304) {
                    pngFile.delete();
                    File pngFile2 = new File(fname);
                    int i = PRIORITY_HIGH_ACCURACY;
                    while (compressBitmap(bitmap, pngFile2, CompressFormat.JPEG, i).length() >= 4194304) {
                        try {
                            i -= 2;
                        } catch (Exception e2) {
                            pngFile = pngFile2;
                        }
                    }
                    pngFile = pngFile2;
                }
                MediaUtil.getInstace().scanFile(BaseApplication.getContext(), pngFile.toString(), "image/png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos2 != null) {
                    try {
                        fos2.flush();
                        fos2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String getPicFileName(String location, Context context) {
        File outfile = new File(SHARE_IMAGE_PATH);
        if (outfile.isDirectory()) {
            File[] childFile = outfile.listFiles();
            if (!(childFile == null || childFile.length == 0)) {
                for (File f : childFile) {
                    if (f.delete() && MediaUtil.getInstace().getImageContentUri(context, f) != null) {
                        context.getContentResolver().delete(MediaUtil.getInstace().getImageContentUri(context, f),
                                null, null);
                    }
                }
            }
        } else {
            try {
                outfile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SHARE_IMAGE_PATH + location.hashCode() + System.currentTimeMillis() + ".png";
    }

    public static boolean isEnoughMemoryForBitmap(View view) {
        Runtime runtime = Runtime.getRuntime();
        return ((long) (20971520 + ((view.getWidth() * view.getHeight()) * 4))) < runtime.maxMemory() - runtime
                .totalMemory();
    }

    public static int getMaxCanvasHeight() {
        return new Canvas().getMaximumBitmapHeight();
    }
}
