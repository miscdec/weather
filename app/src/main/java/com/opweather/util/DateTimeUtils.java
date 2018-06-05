package com.opweather.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.DetectedActivity;
import com.opweather.R;
import com.opweather.api.helper.DateUtils;
import com.opweather.widget.openglbase.RainSurfaceView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class DateTimeUtils {
    public static final String CHINA_OFFSET = "+08:00";
    public static final long DAY = 86400000;
    public static final String DISPLAY_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final long HOUR = 3600000;
    public static final long MINUTE = 60000;
    public static final String TIME_FORMAT_HM = "HH:mm";
    public static final String WC_FORMAT = "yyyyMMddHHmm";
    public static final String WC_TIME_ZONE = "Asia/Beijing";
    private static final long WEATHER_UPDATE_DISTANCE = 2;

    public static String longTimeToHourMinute(long time, String timeZoneOffset) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_HM);
            sdf.setTimeZone(getTimeZone(timeZoneOffset));
            return sdf.format(new Date(time));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String dateToHourMinute(Date date, String timeZone) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_HM, Locale.getDefault());
            if (timeZone == null) {
                sdf.setTimeZone(TimeZone.getDefault());
            } else {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
            }
            return sdf.format(date);
        } catch (IllegalArgumentException e) {
            return null;
        }
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

    public static String longTimeToHourMinute(long time) {
        return longTimeToHourMinute(time, null);
    }

    public static String longTimeToMMdd(Context context, long time, String timeZone) {
        SimpleDateFormat sdf;
        if (isZh(context)) {
            sdf = new SimpleDateFormat("MM" + context.getString(R.string.month) + "dd" + context.getString(R.string
                    .day));
        } else {
            sdf = new SimpleDateFormat("MM/dd");
        }
        if (timeZone == null) {
            sdf.setTimeZone(TimeZone.getDefault());
        } else {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
        }
        String mmDD = sdf.format(new Date(time));
        return (isZh(context) && mmDD.startsWith("0")) ? mmDD.substring(1) : mmDD;
    }

    public static int timeToDay(Context context, long time, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
        return Integer.parseInt(sdf.format(new Date(time)));
    }

    public static String longTimeToMMddTwo(Context context, long time, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("M/dd", Locale.getDefault());
        sdf.setTimeZone(timeZone != null ? TimeZone.getTimeZone("GMT" + timeZone) : TimeZone.getDefault());
        return sdf.format(new Date(time));
    }

    public static String longTimeToRefreshTime(Context context, long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time));
    }

    public static int longTimeToHour(long time) throws NumberFormatException {
        return longTimeToHour(time, null);
    }

    public static int longTimeToHour(long time, String timeZoneOffset) throws NumberFormatException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        sdf.setTimeZone(getTimeZone(timeZoneOffset));
        return Integer.valueOf(sdf.format(new Date(time))).intValue();
    }

    public static String dateToWCFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(WC_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone(WC_TIME_ZONE));
            return sdf.format(date);
        } catch (Exception e) {
            Log.e("DateTimeUtils", "dateToWCFormat error" + e.toString());
            return null;
        }
    }

    public static long wcFormatToTimestamp(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(WC_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone(WC_TIME_ZONE));
            return sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Date wcFormatToDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(WC_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone(WC_TIME_ZONE));
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLocalDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static long stringToLong(String strTime, String format) {
        Date date = stringToDate(strTime, format);
        return date == null ? 0 : dateToLong(date);
    }

    public static long stringToLong(String strTime) {
        return stringToLong(strTime, "HH:mm:ss");
    }

    public static long stringDateToLong(String strTime) {
        Date date = stringToDate(strTime, "yyyy-MM-dd");
        return date == null ? 0 : dateToLong(date);
    }

    public static Date stringToDate(String strTime, String formatType) {
        if (TextUtils.isEmpty(formatType)) {
            return null;
        }
        try {
            return new SimpleDateFormat(formatType).parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dateToString(Date date, String formatType) {
        try {
            return new SimpleDateFormat(formatType).format(date);
        } catch (Exception e) {
            Log.e("DateTimeUtils", "dateToString error" + e.toString());
            return null;
        }
    }

    public static long[] wcSuntimeToTimestamp(Date forcastTime, String sunTime) {
        String strSunrise = sunTime.split("\\|")[0];
        String strSunset = sunTime.split("\\|")[1];
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(WC_TIME_ZONE));
        calendar.setTime(forcastTime);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, NumberUtils.parseInt(strSunrise.split(":")[0], 0));
        calendar.set(Calendar.MINUTE, NumberUtils.parseInt(strSunrise.split(":")[1], 0));
        long sunrise = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, NumberUtils.parseInt(strSunset.split(":")[0], 0));
        calendar.set(Calendar.MINUTE, NumberUtils.parseInt(strSunset.split(":")[1], 0));
        long sunset = calendar.getTimeInMillis();
        return new long[]{sunrise, sunset};
    }

    public static String getDayString(Context context, int day) {
        Resources r = context.getResources();
        switch (day) {
            case RainSurfaceView.RAIN_LEVEL_NORMAL_RAIN:
                return r.getString(R.string.days_mapping_sun);
            case RainSurfaceView.RAIN_LEVEL_SHOWER:
                return r.getString(R.string.days_mapping_mon);
            case RainSurfaceView.RAIN_LEVEL_DOWNPOUR:
                return r.getString(R.string.days_mapping_tue);
            case RainSurfaceView.RAIN_LEVEL_RAINSTORM:
                return r.getString(R.string.days_mapping_wed);
            case RainSurfaceView.RAIN_LEVEL_THUNDERSHOWER:
                return r.getString(R.string.days_mapping_thu);
            case ConnectionResult.RESOLUTION_REQUIRED:
                return r.getString(R.string.days_mapping_fri);
            case DetectedActivity.WALKING:
                return r.getString(R.string.days_mapping_sat);
            default:
                return null;
        }
    }

    private static boolean isZh(Context context) {
        String country = context.getResources().getConfiguration().locale.getCountry();
        return country.equals("CN") || country.equals("TW");
    }

    public static boolean isTimeMillisDayTime(long time) {
        return isTimeMillisDayTime(time, null);
    }

    public static boolean isTimeMillisDayTime(long time, String timeZoneOffset) {
        boolean isDay = true;
        try {
            int hourTime = longTimeToHour(time, timeZoneOffset);
            if (hourTime >= 18 || hourTime < 6) {
                isDay = false;
            }
            return isDay;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static long getDistanceDays(long start, long end) {
        return (end - start) / 86400000;
    }

    public static long getDistanceHours(long start, long end) {
        return (end - start) / 3600000;
    }

    public static int distanceOfHour(Date time) {
        return distanceOfHour(time, null);
    }

    public static int distanceOfHour(Date firstTime, Date secondTime) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(firstTime);
        Calendar calendar2 = Calendar.getInstance();
        if (secondTime != null) {
            calendar2.setTime(secondTime);
        }
        return (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) ==
                calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar
                .DAY_OF_MONTH)) ?
                calendar1.get(Calendar.HOUR_OF_DAY) - calendar2.get(Calendar.HOUR_OF_DAY) : -10001;
    }

    public static long getCurrentData() {
        return getDate(System.currentTimeMillis());
    }

    public static long getDate(long time) {
        return stringDateToLong(new SimpleDateFormat("yyyy-MM-dd").format(new Date(time)));
    }

    public static long getTimeByTimeZone() {
        return dealTimeZone();
    }

    public static long dealTimeZone() {
        try {
            Calendar cd = Calendar.getInstance();
            cd.setTimeZone(TimeZone.getDefault());
            return cd.getTimeInMillis();
        } catch (IllegalArgumentException e) {
            return System.currentTimeMillis();
        }
    }

    public static TimeZone getTimeZone(String timeZoneString) {
        if (timeZoneString == null || TextUtils.isEmpty(timeZoneString)) {
            return TimeZone.getDefault();
        }
        return (timeZoneString.contains("GMT") || timeZoneString.contains("UTC")) ? TimeZone.getTimeZone
                (timeZoneString) : TimeZone.getTimeZone("GMT" + timeZoneString);
    }

    public static boolean isNeedUpdateWeather(Context context, String cityId) {
        long currentTime = System.currentTimeMillis();
        long lastTime = SystemSetting.getRefreshTime(context, cityId);
        return !DateUtils.isSameDay(currentTime, lastTime) || getDistanceHours(lastTime, currentTime) >= 2;
    }

    public static String getTimeTitle(Date date, boolean isSuccess, Context context) {
        if (!isSuccess) {
            return context.getString(R.string.updated_fail);
        }
        long timeOffSet = System.currentTimeMillis() - date.getTime();
        if (timeOffSet <= 300000) {
            return context.getString(R.string.just_updated);
        }
        if (300000 < timeOffSet && timeOffSet < 3600000) {
            return context.getString(R.string.updated_mins_ago, new Object[]{String.valueOf(Math.round((float)
                    (timeOffSet / 60000)))});
        } else if (3600000 >= timeOffSet || timeOffSet >= 86400000) {
            return context.getString(R.string.updated_days_ago);
        } else {
            return context.getString(R.string.updated_hours_ago, new Object[]{String.valueOf(Math.round((float)
                    (timeOffSet / 3600000)))});
        }
    }

    public static long getTimeInterval() {
        return ((long) (Integer.valueOf(new SimpleDateFormat("mm", Locale.US).format(new Date())).intValue() - 15)) *
                60000;
    }

    public static boolean checkNeedRefresh(long time) {
        return time == 0 || System.currentTimeMillis() - time > 1800000;
    }

    public static long getRandomDelayMillis() {
        return (long) new Random(System.currentTimeMillis()).nextInt(120000);
    }
}
