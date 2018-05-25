package com.opweather.opapi;

import android.text.TextUtils;
import android.util.Log;

import com.opweather.widget.openglbase.RainSurfaceView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static final String CHINA_OFFSET = "+08:00";
    public static final long DAY = 86400000;
    public static final String DISPLAY_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final long HOUR = 3600000;
    public static final long MINUTE = 60000;
    public static final String TIME_FORMAT_HM = "HH:mm";
    public static final String WC_FORMAT = "yyyyMMddHHmm";
    public static final String WC_TIME_ZONE = "Asia/Beijing";

    public static Date epochDateToDate(long epochDate) {
        return new Date(1000 * epochDate);
    }

    public static long dateToEpochDate(Date date) {
        try {
            return date.getTime() / 1000;
        } catch (Exception e) {
            Log.e("DateUtils", "date is error");
            return Long.MIN_VALUE;
        }
    }

    public static boolean isSameDay(Date date1, Date date2, TimeZone timeZone) {
        return isSameDay(date1.getTime(), date2.getTime(), timeZone);
    }

    public static boolean isSameDay(long ms1, long ms2, TimeZone timeZone) {
        long interval = ms1 - ms2;
        return interval < 86400000 && interval > -86400000 && toDay(ms1, timeZone) == toDay(ms2, timeZone);
    }

    private static long toDay(long millis, TimeZone timeZone) {
        return (((long) timeZone.getOffset(millis)) + millis) / 86400000;
    }

    public static int distanceOfHour(Date firstTime, Date secondTime) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(firstTime);
        Calendar calendar2 = Calendar.getInstance();
        if (secondTime != null) {
            calendar2.setTime(secondTime);
        }
        return (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) ==
                calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE)) ?
                calendar1.get(Calendar.HOUR_OF_DAY) - calendar2.get(Calendar.HOUR_OF_DAY) : -10001;
    }

    public static int distanceOfHour(Date time) {
        return distanceOfHour(time, null);
    }

    public static Date getDistanceDate(Date date, int i) {
        return getDistanceDate(date, i, TimeZone.getDefault());
    }

    public static Date getDistanceDate(Date date, int i, TimeZone timeZone) {
        Calendar c = Calendar.getInstance(timeZone);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + i);
        return c.getTime();
    }

    public static TimeZone getTimeZone(String timeZoneString) {
        return (timeZoneString == null || TextUtils.isEmpty(timeZoneString)) ? TimeZone.getDefault() : TimeZone.getTimeZone("GMT" + timeZoneString);
    }

    public static String DateTimeToHourMinute(Date date, String timeZoneOffset) {
        try {
            SimpleDateFormat sdf;
            if (TextUtils.isEmpty(timeZoneOffset)) {
                sdf = new SimpleDateFormat(TIME_FORMAT_HM, Locale.CHINA);
                sdf.setTimeZone(getTimeZone(CHINA_OFFSET));
            } else {
                sdf = new SimpleDateFormat(TIME_FORMAT_HM, Locale.CHINA);
                sdf.setTimeZone(getTimeZone(timeZoneOffset));
            }
            return sdf.format(date);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
