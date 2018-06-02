package com.opweather.api.helper;

import android.text.TextUtils;
import android.util.Log;

import com.opweather.util.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateUtils {
    private static String CHINA_ZONE = null;
    private static final String DISPLAY_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final long MILLIS_IN_DAY = 86400000;
    private static final int SECONDS_IN_DAY = 86400;
    private static final String WC_TIME_ZONE = "Asia/Beijing";
    private static final ThreadLocal<DateFormat> oppoFormat1;
    private static final ThreadLocal<DateFormat> oppoFormat2;
    private static final ThreadLocal<DateFormat> oppoFormat3;
    private static final ThreadLocal<DateFormat> oppoFormat4;
    private static final ThreadLocal<DateFormat> swaFormat1;
    private static final ThreadLocal<DateFormat> swaFormat2;
    private static final ThreadLocal<DateFormat> swaFormat3;
    private static final ThreadLocal<DateFormat> timeTominute;

    static {
        CHINA_ZONE = "GMT+08:00";
        oppoFormat1 = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                return new SimpleDateFormat("yyyyMMdd", Locale.US);
            }
        };
        oppoFormat2 = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                DateFormat result = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                result.setTimeZone(TimeZone.getTimeZone(CHINA_ZONE));
                return result;
            }
        };
        oppoFormat3 = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                DateFormat result = new SimpleDateFormat(DISPLAY_FORMAT, Locale.CHINA);
                result.setTimeZone(TimeZone.getTimeZone(CHINA_ZONE));
                return result;
            }
        };
        oppoFormat4 = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                DateFormat result = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
                result.setTimeZone(TimeZone.getTimeZone(CHINA_ZONE));
                return result;
            }
        };
        timeTominute = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                DateFormat result = new SimpleDateFormat("mm", Locale.CHINA);
                result.setTimeZone(TimeZone.getTimeZone(CHINA_ZONE));
                return result;
            }
        };
        swaFormat1 = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                DateFormat result = new SimpleDateFormat(DateTimeUtils.WC_FORMAT, Locale.CHINA);
                result.setTimeZone(TimeZone.getTimeZone(CHINA_ZONE));
                return result;
            }
        };
        swaFormat2 = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                DateFormat result = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                result.setTimeZone(TimeZone.getTimeZone(CHINA_ZONE));
                return result;
            }
        };
        swaFormat3 = new ThreadLocal<DateFormat>() {
            public DateFormat initialValue() {
                DateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                result.setTimeZone(TimeZone.getTimeZone(CHINA_ZONE));
                return result;
            }
        };
    }

    private DateUtils() {
    }

    public static String formatSwaRequestDateText(Date date) {
        return ((DateFormat) swaFormat1.get()).format(date);
    }

    public static String formatOppoRequestDateText(Date date) {
        return ((DateFormat) oppoFormat1.get()).format(date);
    }

    public static String formatOppoRequestv3DateText(Date date) {
        return ((DateFormat) oppoFormat4.get()).format(date);
    }

    public static int formatTimetoMinuteInt(Date date) {
        return Integer.parseInt(((DateFormat) timeTominute.get()).format(date));
    }

    public static Date parseOppoforcastDate(String date) {
        try {
            return ((DateFormat) oppoFormat2.get()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseOppoforcastv3Date(String date) {
        try {
            return ((DateFormat) oppoFormat1.get()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseOppoCurrentWeatherDate(String date) {
        try {
            return ((DateFormat) oppoFormat3.get()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseOppoObservationDate(String time) {
        try {
            return ((DateFormat) swaFormat1.get()).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseOppoSunDate(String time) {
        try {
            return ((DateFormat) oppoFormat3.get()).parse(((DateFormat) oppoFormat2.get()).format
                    (new Date()) + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseSwaCurrentDate(String time) {
        try {
            return ((DateFormat) swaFormat3.get()).parse(((DateFormat) swaFormat2.get()).format
                    (new Date()) + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseSwaDate(Date date, String time) {
        try {
            return ((DateFormat) swaFormat3.get()).parse(((DateFormat) swaFormat2.get()).format
                    (date) + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseSwaAlarmDate(String timeStr) {
        try {
            return ((DateFormat) swaFormat3.get()).parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseSwaAqiDate(String time) {
        try {
            return ((DateFormat) swaFormat1.get()).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public static boolean isSameDay(Date date1, Date date2) {
        return isSameDay(date1, date2, TimeZone.getDefault());
    }

    public static boolean isSameDay(long ms1, long ms2) {
        return isSameDay(ms1, ms2, TimeZone.getDefault());
    }

    public static boolean isSameDay(Date date1, Date date2, TimeZone timeZone) {
        return isSameDay(date1.getTime(), date2.getTime(), timeZone);
    }

    public static boolean isSameDay(long ms1, long ms2, TimeZone timeZone) {
        long interval = ms1 - ms2;
        return interval < 86400000 && interval > -86400000 && toDay(ms1, timeZone) == toDay(ms2,
                timeZone);
    }

    private static long toDay(long millis, TimeZone timeZone) {
        return (((long) timeZone.getOffset(millis)) + millis) / 86400000;
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

    public static int distanceOfHour(Date firstTime, Date secondTime) {
        Calendar calendar1 = Calendar.getInstance(TimeZone.getDefault());
        calendar1.setTime(firstTime);
        Calendar calendar2 = Calendar.getInstance(TimeZone.getDefault());
        if (secondTime != null) {
            calendar2.setTime(secondTime);
        }
        return (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) ==
                calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar
                .DAY_OF_MONTH)) ? calendar1.get(Calendar.HOUR_OF_DAY) - calendar2.get(Calendar.HOUR_OF_DAY) :
                Integer.MIN_VALUE;
    }

    public static String dateToWCFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(WC_TIME_ZONE));
        return sdf.format(date);
    }

    public static TimeZone getTimeZone(String timeZoneString) {
        return (timeZoneString == null || TextUtils.isEmpty(timeZoneString)) ? TimeZone
                .getDefault() : TimeZone.getTimeZone("GMT" + timeZoneString);
    }
}
