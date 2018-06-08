package com.opweather.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;

import com.opweather.R;
import com.opweather.api.WeatherException;
import com.opweather.api.nodes.Alarm;
import com.opweather.api.nodes.RootWeather;
import com.opweather.bean.CityData;
import com.opweather.provider.CitySearchProvider;
import com.opweather.provider.LocationProvider;
import com.opweather.ui.WeatherWarningActivity;
import com.opweather.util.ClockUtils;
import com.opweather.util.DateTimeUtils;
import com.opweather.util.NetUtil;
import com.opweather.util.StringUtils;
import com.opweather.util.SystemSetting;
import com.opweather.util.WeatherClientProxy;
import com.opweather.util.WeatherClientProxy.CacheMode;
import com.opweather.util.WeatherLog;
import com.opweather.widget.openglbase.RainSurfaceView;
import com.opweather.widget.widget.WidgetHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM = "net.oneplus.weather.receiver.BootReceiver" +
            ".ACTION_ALARM";
    public static final String PRIMARY_CHANNEL = "weather_default";
    private static final String TAG = "AlarmReceiver";
    private static PendingIntent mAlarmIntent = null;
    private static AlarmReceiver mReceiver = null;
    private static final long mTime = 3600000;
    private Handler mHandler;
    private NotificationManager mNotificationManager;
    private int mNotifyID;

    public AlarmReceiver() {
        mNotifyID = 10001;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action) && ACTION_ALARM.equals(action)) {
            updateWarning(context);
        }
    }

    public static synchronized AlarmReceiver getInstance() {
        AlarmReceiver alarmReceiver;
        synchronized (AlarmReceiver.class) {
            if (mReceiver == null) {
                mReceiver = new AlarmReceiver();
            }
            alarmReceiver = mReceiver;
        }
        return alarmReceiver;
    }

    public void updateWarning(Context context) {
        if (NetUtil.isNetworkAvailable(context)) {
            CityData cityData = SystemSetting.getLocationOrDefaultCity(context);
            if (cityData == null || TextUtils.isEmpty(cityData.getLocationId())) {
                WeatherLog.e("LocationId is null");
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(CitySearchProvider.GET_SEARCH_RESULT_FAIL);
                    return;
                }
                return;
            }
            if (cityData.isLocatedCity()) {
                getLocation(context, CacheMode.LOAD_NO_CACHE);
            } else {
                requestWeather(context, cityData, CacheMode.LOAD_NO_CACHE, true);
            }
            WidgetHelper.getInstance(context).updateAllWidget(true);
            setAlarmClock(context);
            return;
        }
        buildJob(context);
    }

    public void updateWarning(Context context, Handler handler) {
        mHandler = handler;
        updateWarning(context);
    }

    public void getLocation(final Context context, final CacheMode mode, final String locationId) {
        LocationProvider locationProvider = new LocationProvider(context);
        locationProvider.setOnLocationListener(new LocationProvider.OnLocationListener() {
            @Override
            public void onError(int error) {
                Log.d(TAG, "onError: 11");
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(CitySearchProvider.GET_SEARCH_RESULT_FAIL);
                }
                Log.e(TAG, "Location ERR:" + error);
                CityData cityData = SystemSetting.getLocationOrDefaultCity(context);
                if (cityData == null || TextUtils.isEmpty(cityData.getLocationId())) {
                    WeatherLog.e("LocationId is null");
                } else {
                    requestWeather(context, cityData, CacheMode.LOAD_NO_CACHE, true);
                }
            }

            @Override
            public void onLocationChanged(CityData cityData) {
                Log.d(TAG, "onLocationChanged: 11");
                if (SystemSetting.getLocationOrDefaultCity(context).isLocatedCity() || TextUtils.isEmpty
                        (SystemSetting.getLocationOrDefaultCity(context).getLocationId())) {
                    SystemSetting.setLocationOrDefaultCity(context, cityData);
                }
                if (TextUtils.isEmpty(locationId)) {
                    requestWeather(context, cityData, mode, true);
                    return;
                }
                cityData.setLocationId(locationId);
                requestWeather(context, cityData, mode, false);
            }
        });
        locationProvider.startLocation();
    }

    private void getLocation(Context context, CacheMode mode) {
        getLocation(context, mode, null);
    }

    private void requestWeather(final Context context, final CityData city, CacheMode mode, final boolean
            isCheckAlarm) {
        if (city != null && !TextUtils.isEmpty(city.getLocationId()) && !city.getLocationId().equals("0")) {
            new WeatherClientProxy(context).setCacheMode(mode).requestWeatherInfo(15, city,
                    new WeatherClientProxy.OnResponseListener() {
                @Override
                public void onCacheResponse(RootWeather rootWeather) {
                    sendWarnNotification(context, rootWeather, city, isCheckAlarm);
                    SystemSetting.notifyWeatherDataChange(context);
                }

                @Override
                public void onErrorResponse(WeatherException weatherException) {
                    Log.e(TAG, "requestWeather error");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(CitySearchProvider.GET_SEARCH_RESULT_FAIL);
                    }
                }

                @Override
                public void onNetworkResponse(RootWeather rootWeather) {
                    sendWarnNotification(context, rootWeather, city, isCheckAlarm);
                    SystemSetting.notifyWeatherDataChange(context);
                    SystemSetting.setLocale(context);
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(CitySearchProvider.GET_SEARCH_RESULT_FAIL);
                    }
                }
            });
        }
    }

    public void sendWarnNotification(Context context, RootWeather weather, CityData city, boolean
            isCheckAlarm) {
        ReflectiveOperationException e;
        Notification notification;
        if (weather != null && city != null && SystemSetting.isWeatherWarningEnabled(context)) {
            ArrayList<Alarm> alarms = getAlarmArrayList(weather.getWeatherAlarms());
            if (alarms != null && alarms.size() != 0) {
                Object[] objArr;
                String title;
                String message;
                int count = alarms.size();
                if (isCheckAlarm) {
                    if (!checkAlarmInfo(context, alarms, city)) {
                        setAlarmInfo(context, alarms.get(0), city, count);
                    } else {
                        return;
                    }
                }
                String tempTitle;
                String string;
                if (count == 1) {
                    tempTitle = (alarms.get(0)).getTypeName();
                    if (!TextUtils.isEmpty(tempTitle) && !tempTitle.equalsIgnoreCase("None") &&
                            !tempTitle
                                    .equalsIgnoreCase("null")) {
                        string = context.getString(R.string.weather_warning_title);
                        objArr = new Object[1];
                        objArr[0] = tempTitle;
                        title = String.format(string, objArr);
                        message = (alarms.get(0)).getContentText();
                    } else {
                        return;
                    }
                }
                int realCount = 0;
                StringBuffer tempMessage = new StringBuffer();
                for (int i = 0; i < count; i++) {
                    tempTitle = ((Alarm) alarms.get(i)).getTypeName();
                    if (!TextUtils.isEmpty(tempTitle) && !tempTitle.equalsIgnoreCase("None") &&
                            !tempTitle
                                    .equalsIgnoreCase("null")) {
                        if (realCount == 0) {
                            string = context.getString(R.string.weather_warning_title);
                            objArr = new Object[1];
                            objArr[0] = tempTitle;
                            tempMessage.append(String.format(string, objArr));
                        } else {
                            StringBuilder append = new StringBuilder().append("、");
                            String string2 = context.getString(R.string.weather_warning_title);
                            Object[] objArr2 = new Object[1];
                            objArr2[0] = tempTitle;
                            tempMessage.append(append.append(String.format(string2, objArr2))
                                    .toString());
                        }
                        realCount++;
                    }
                }
                if (realCount != 0) {
                    string = context.getString(R.string.weather_warning_alert);
                    objArr = new Object[1];
                    objArr[0] = String.valueOf(realCount);
                    title = String.format(string, objArr);
                    message = tempMessage.toString();
                    if (message.equalsIgnoreCase("null")) {
                        message = StringUtils.EMPTY_STRING;
                    }
                } else {
                    return;
                }
                removeWarnNotify();
                if (mNotificationManager == null) {
                    mNotificationManager = (NotificationManager) context.getSystemService(Context
                            .NOTIFICATION_SERVICE);
                }
                Builder mbuilder = new Builder(context).setContentTitle(title).setContentText
                        (message)
                        .setContentIntent(getContentIntent(context, 134217728, city, alarms))
                        .setSmallIcon(R.drawable
                                .notification_ic_warn).setAutoCancel(true).setWhen(System
                                .currentTimeMillis())
                        .setDefaults(RainSurfaceView.RAIN_LEVEL_DOWNPOUR).setPriority
                                (RainSurfaceView
                                        .RAIN_LEVEL_SHOWER).setStyle(new BigTextStyle().bigText
                                (message));
                if (VERSION.SDK_INT >= 26) {
                    try {
                        Class notificationChannelClass = Class.forName("android.app" +
                                ".NotificationChannel");
                        if (notificationChannelClass != null) {
                            Class[] clsArr = new Class[3];
                            clsArr[0] = String.class;
                            clsArr[1] = CharSequence.class;
                            clsArr[2] = Integer.TYPE;
                            Constructor channelConstructor = notificationChannelClass
                                    .getConstructor(clsArr);
                            channelConstructor.setAccessible(true);
                            Object[] objArr3 = new Object[3];
                            objArr3[0] = PRIMARY_CHANNEL;
                            objArr3[1] = context.getString(R.string.app_name);
                            objArr3[2] = Integer.valueOf(RainSurfaceView.RAIN_LEVEL_RAINSTORM);
                            Object notificationChannelField = channelConstructor.newInstance
                                    (objArr3);
                            Class[] clsArr2 = new Class[1];
                            clsArr2[0] = Boolean.TYPE;
                            Method enableLights = notificationChannelField.getClass().getMethod
                                    ("enableLights",
                                            clsArr2);
                            enableLights.setAccessible(true);
                            objArr3 = new Object[1];
                            objArr3[0] = Boolean.valueOf(true);
                            enableLights.invoke(notificationChannelField, objArr3);
                            clsArr2 = new Class[1];
                            clsArr2[0] = Boolean.TYPE;
                            Method enableVibration = notificationChannelField.getClass()
                                    .getMethod("enableVibration",
                                            clsArr2);
                            objArr3 = new Object[1];
                            objArr3[0] = Boolean.valueOf(true);
                            enableVibration.invoke(notificationChannelField, objArr3);
                            clsArr2 = new Class[1];
                            clsArr2[0] = notificationChannelClass;
                            Method createNotificationChannel = mNotificationManager.getClass
                                    ().getMethod
                                    ("createNotificationChannel", clsArr2);
                            NotificationManager notificationManager = mNotificationManager;
                            objArr = new Object[1];
                            objArr[0] = notificationChannelField;
                            createNotificationChannel.invoke(notificationManager, objArr);
                            clsArr2 = new Class[1];
                            clsArr2[0] = String.class;
                            Method setChannelId = mbuilder.getClass().getMethod("setChannelId",
                                    clsArr2);
                            objArr3 = new Object[1];
                            objArr3[0] = PRIMARY_CHANNEL;
                            setChannelId.invoke(mbuilder, objArr3);
                        }
                    } catch (ClassNotFoundException e2) {
                        e = e2;
                        e.printStackTrace();
                        notification = mbuilder.build();
                        notification.flags |= 16;
                        mNotificationManager.notify(mNotifyID, notification);
                    } catch (NoSuchMethodException e3) {
                        e = e3;
                        e.printStackTrace();
                        notification = mbuilder.build();
                        notification.flags |= 16;
                        mNotificationManager.notify(mNotifyID, notification);
                    } catch (IllegalAccessException e4) {
                        e = e4;
                        e.printStackTrace();
                        notification = mbuilder.build();
                        notification.flags |= 16;
                        mNotificationManager.notify(mNotifyID, notification);
                    } catch (InstantiationException e5) {
                        e = e5;
                        e.printStackTrace();
                        notification = mbuilder.build();
                        notification.flags |= 16;
                        mNotificationManager.notify(mNotifyID, notification);
                    } catch (InvocationTargetException e6) {
                        e = e6;
                        e.printStackTrace();
                        notification = mbuilder.build();
                        notification.flags |= 16;
                        mNotificationManager.notify(mNotifyID, notification);
                    }
                }
                notification = mbuilder.build();
                notification.flags |= 16;
                mNotificationManager.notify(mNotifyID, notification);
            }
        }
    }

    private void removeWarnNotify() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(mNotifyID);
        }
    }

    private boolean checkAlarmInfo(Context context, ArrayList<Alarm> alarms, CityData city) {
        HashMap<String, Object> alarMap = SystemSetting.getAlarmInfo(context);
        Object alarmCity = alarMap.get("WeatherAlarmCity");
        Object alarmType = alarMap.get("WeatherAlarmType");
        int alarmCount = (int) alarMap.get("WeatherAlarmCount");
        if (alarmCity == null || !city.getLocalName().equals(alarmCity.toString())) {
            return false;
        }
        if (alarmType == null || !alarms.get(0).getTypeName().equals(alarmType.toString())) {
            return false;
        }
        return alarmCount != 0 && alarmCount == alarms.size();
    }

    private void setAlarmInfo(Context context, Alarm alarm, CityData city, int count) {
        SystemSetting.setAlarmInfo(context, city.getLocalName(), alarm.getTypeName(), count);
    }

    private PendingIntent getContentIntent(Context context, int flag, CityData city,
                                           ArrayList<Alarm> alarms) {
        Intent contentIntent = new Intent();
        contentIntent.setClass(context, WeatherWarningActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        contentIntent.putParcelableArrayListExtra(WeatherWarningActivity.INTENT_PARA_WARNING,
                alarms);
        contentIntent.putExtra(WeatherWarningActivity.INTENT_PARA_CITY, city.getLocalName());
        return PendingIntent.getActivity(context, 0, contentIntent, flag);
    }

    private ArrayList<Alarm> getAlarmArrayList(List<Alarm> alarms) {
        if (alarms == null || alarms.size() == 0) {
            return null;
        }
        ArrayList<Alarm> resAlarms = new ArrayList<>();
        resAlarms.addAll(alarms);
        return resAlarms;
    }

    public static void setAlarmClock(Context context) {
        long interval = DateTimeUtils.getTimeInterval();
        long refreshTime = 3600000 + DateTimeUtils.getRandomDelayMillis();
        if (interval < 0) {
            refreshTime = (SystemClock.elapsedRealtime() - interval) + DateTimeUtils
                    .getRandomDelayMillis();
            checkAlarmIntent(context);
            ClockUtils.getInstance(context).setClock(mAlarmIntent, refreshTime);
            Log.d(TAG, "setAlarmClock , 15 minutes later , next refresh time is :" +
                    DateTimeUtils.getLocalDateTime(System.currentTimeMillis() - interval));
            return;
        }
        Log.d(TAG, "setAlarmClock , next refresh time is :" + DateTimeUtils.getLocalDateTime
                (System.currentTimeMillis() + refreshTime));
        checkAlarmIntent(context);
        ClockUtils.getInstance(context).setClock(mAlarmIntent, SystemClock.elapsedRealtime() +
                refreshTime);
    }

    public static void cancleAlarmClock(Context context) {
        checkAlarmIntent(context);
        ClockUtils.getInstance(context).cancleClock(mAlarmIntent);
    }

    private static void checkAlarmIntent(Context context) {
        if (mAlarmIntent == null) {
            Intent alarmIntent = new Intent();
            alarmIntent.setPackage(context.getPackageName());
            alarmIntent.setAction(ACTION_ALARM);
            mAlarmIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);
        }
    }

    public static void buildJob(Context context) {
        Log.d(TAG, "update weather from buildJob");
        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(Context
                .JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(context
                .getPackageName(), AlarmUpdateJob.class.getName()));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        if (mJobScheduler != null) {
            mJobScheduler.schedule(builder.build());
        }
    }
}
