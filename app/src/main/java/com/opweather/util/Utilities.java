package com.opweather.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.widget.TextView;

import com.opweather.ui.BaseApplication;

import java.text.DecimalFormat;

public class Utilities {
    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getApplicationContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        return (int) ((spValue * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static Drawable getAppIcon(Context context) {
        try {
            return context.getApplicationInfo().loadIcon(context.getPackageManager());
        } catch (Exception e) {
            return null;
        }
    }

    public static void measureTextLengthAndSet(TextView tv, String text, int MaxRange, int textSizeSp) {
        TextPaint paint = tv.getPaint();
        int width = (int) paint.measureText(text);
        if (MaxRange > 0 && width > MaxRange) {
            float textSize = paint.getTextSize();
            float compareSize = (float) sp2px(BaseApplication.getContext(), (float) (textSizeSp / 2));
            while (width > MaxRange && textSize > compareSize) {
                textSize -= 1.0f;
                paint.setTextSize(textSize);
                width = (int) paint.measureText(text);
            }
            tv.setTextSize(0, textSize);
        }
        tv.setText(text);
    }

    public static String formatSize(long size) {
        if (size < 1024) {
            return String.valueOf(size) + "B";
        }
        if (size < 1048576) {
            return new DecimalFormat("###0.##").format((double) (((float) size) / 1024.0f)) + "KB";
        }
        return size < 1073741824 ? new DecimalFormat("###0.##").format((double) (((float) size) / 1048576.0f)) + "MB"
                : new DecimalFormat("#######0.##").format((double) (((float) size) / 1.07374182E9f)) + "GB";
    }
}
