package com.opweather.api.parser;

import android.text.TextUtils;

import com.opweather.api.WeatherRequest;
import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.IOUtils;
import com.opweather.api.helper.LogUtils;
import com.opweather.api.helper.NumberUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.OppoChinaRequest;
import com.opweather.api.nodes.Alarm;
import com.opweather.api.nodes.CurrentWeather;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.HourForecastsWeather;
import com.opweather.api.nodes.OppoChinaAlarm;
import com.opweather.api.nodes.OppoChinaAqiWeather;
import com.opweather.api.nodes.OppoChinaCurrentWeather;
import com.opweather.api.nodes.OppoChinaDailyForecastsWeather;
import com.opweather.api.nodes.OppoChinaHourForecastsWeather;
import com.opweather.api.nodes.OppoWind;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.nodes.Sun;
import com.opweather.api.nodes.Temperature;
import com.opweather.api.nodes.Wind;
import com.opweather.api.nodes.Wind.Direction;
import com.opweather.db.CityWeatherDBHelper.WeatherEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

public class OppoChinaResponseParserV3 implements ResponseParser {
    private static final String CONTENT_ENCODE = "utf-8";
    private static final String TAG = "OppoChinaResponseParserV3";
    protected final WeatherRequest mRequest;

    private static class RootWeatherBuilder implements WeatherBuilder {
        private String aqi;
        private int aqiValue;
        private String areaCode;
        private String areaName;
        private int avg_pm25;
        private double body_temp;
        private int currentRelativeHumidity;
        private double currentTemperature;
        private String currentUVIndexText;
        private int currentWeatherId;
        private Direction currentWindDirection;
        private String currentWindPower;
        private List<DailyForecastsHolder> dailyItemList;
        private String date;
        private List<HourForecastsHolder> hourItemList;
        private int pressure;
        private String time;
        private String ts;
        private int visibility;
        private String warnWeatherDetail;
        private String warnWeatherTitle;

        static class DailyForecastsHolder {
            Date date = null;
            int dayWeatherId = Integer.MIN_VALUE;
            double maxTemperature = Double.NaN;
            double minTemperature = Double.NaN;
            String mobileLink = null;
            int nightWeatherId = Integer.MIN_VALUE;
            Date sunRise = null;
            Date sunSet = null;

            DailyForecastsHolder() {
            }
        }

        static class HourForecastsHolder {
            double temperature = Double.NaN;
            Date time = null;
            int weatherId = Integer.MIN_VALUE;

            HourForecastsHolder() {
            }
        }

        private RootWeatherBuilder() {
            areaName = null;
            aqiValue = Integer.MIN_VALUE;
            ts = null;
            currentWeatherId = Integer.MIN_VALUE;
            currentTemperature = Double.NaN;
            currentWindDirection = null;
            currentWindPower = null;
            currentRelativeHumidity = Integer.MIN_VALUE;
            time = null;
            currentUVIndexText = null;
            avg_pm25 = Integer.MIN_VALUE;
            aqi = null;
            body_temp = Double.NaN;
            pressure = Integer.MIN_VALUE;
            visibility = Integer.MIN_VALUE;
            warnWeatherTitle = null;
            warnWeatherDetail = null;
            dailyItemList = null;
            hourItemList = null;
        }

        public void add(DailyForecastsHolder item) {
            if (dailyItemList == null) {
                dailyItemList = new ArrayList<>();
            }
            dailyItemList.add(item);
        }

        public void add(HourForecastsHolder item) {
            if (hourItemList == null) {
                hourItemList = new ArrayList<>();
            }
            hourItemList.add(item);
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(areaCode)) {
                throw new BuilderException("Valid area code empty.");
            }
            RootWeather rootWeather = new RootWeather(areaCode, areaName, OppoChinaRequest.DATA_SOURCE_NAME);
            if (date != null) {
                LogUtils.d(OppoChinaResponseParserV3.TAG, "Date: " + date);
            }
            OppoChinaAqiWeather aqiWeather = new OppoChinaAqiWeather(areaCode, areaName, OppoChinaRequest
                    .DATA_SOURCE_NAME, aqiValue, avg_pm25, aqi);
            CurrentWeather currentWeather = getCurrentWeather(date, dailyItemList);
            List<DailyForecastsWeather> dailyForecastsWeather = null;
            if (dailyItemList != null && dailyItemList.size() > 0) {
                dailyForecastsWeather = new ArrayList<>();
                for (DailyForecastsHolder holder : dailyItemList) {
                    if (holder.date != null) {
                        Temperature minTemperature = new Temperature(areaCode, areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.minTemperature, WeatherUtils.centigradeToFahrenheit(holder
                                .minTemperature));
                        Temperature maxTemperature = new Temperature(areaCode, areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.maxTemperature, WeatherUtils.centigradeToFahrenheit(holder
                                .maxTemperature));
                        Temperature bodyTemperature = new Temperature(areaCode, areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.maxTemperature, WeatherUtils.centigradeToFahrenheit
                                (body_temp));
                        List<DailyForecastsWeather> list = dailyForecastsWeather;
                        list.add(new OppoChinaDailyForecastsWeather(areaCode, areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.dayWeatherId, holder.nightWeatherId, holder.date,
                                minTemperature, maxTemperature, new Sun(areaCode, areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.sunRise, holder.sunSet), bodyTemperature, pressure,
                                visibility, holder.mobileLink));
                    }
                }
            }
            List<HourForecastsWeather> hourForecastsWeather = null;
            if (hourItemList != null && hourItemList.size() > 0) {
                hourForecastsWeather = new ArrayList<>();
                for (HourForecastsHolder holder2 : hourItemList) {
                    if (!(holder2.time == null || (holder2.weatherId == Integer.MIN_VALUE && holder2.temperature ==
                            Double.NaN))) {
                        List<HourForecastsWeather> list2 = hourForecastsWeather;
                        list2.add(new OppoChinaHourForecastsWeather(areaCode, holder2.weatherId, holder2.time, new
                                Temperature(areaCode, areaName, OppoChinaRequest.DATA_SOURCE_NAME,
                                holder2.temperature, WeatherUtils.centigradeToFahrenheit(holder2.temperature))));
                    }
                }
            }
            List<Alarm> oppoAlarmList = getAlarmWeather(date);
            rootWeather.setCurrentWeather(currentWeather);
            rootWeather.setAqiWeather(aqiWeather);
            if (dailyForecastsWeather == null || dailyForecastsWeather.size() <= 0) {
                rootWeather.setDailyForecastsWeather(null);
            } else {
                rootWeather.setDailyForecastsWeather(dailyForecastsWeather);
            }
            if (hourForecastsWeather == null || hourForecastsWeather.size() <= 0) {
                rootWeather.setHourForecastsWeather(null);
            } else {
                rootWeather.setHourForecastsWeather(hourForecastsWeather);
            }
            if (oppoAlarmList != null) {
                rootWeather.setWeatherAlarms(oppoAlarmList);
            }
            return rootWeather;
        }

        private CurrentWeather getCurrentWeather(String date, List<DailyForecastsHolder> list) {
            if (NumberUtils.isNaN(currentTemperature)) {
                for (DailyForecastsHolder holder : list) {
                    if (DateUtils.isSameDay(System.currentTimeMillis(), holder.date.getTime(), TimeZone.getTimeZone
                            ("GMT+08:00"))) {
                        currentTemperature = (holder.maxTemperature + holder.minTemperature) / 2.0d;
                        break;
                    }
                }
            }
            Date observationDate = DateUtils.parseOppoObservationDate(ts);
            Temperature temperature = new Temperature(areaCode, areaName, OppoChinaRequest.DATA_SOURCE_NAME,
                    currentTemperature, WeatherUtils.centigradeToFahrenheit(currentTemperature));
            return new OppoChinaCurrentWeather(areaCode, areaName, OppoChinaRequest.DATA_SOURCE_NAME,
                    currentWeatherId, observationDate, temperature, currentRelativeHumidity, new OppoWind(areaCode,
                    areaName, OppoChinaRequest.DATA_SOURCE_NAME, currentWindDirection, currentWindPower), Integer
                    .MIN_VALUE, currentUVIndexText);
        }

        private List<Alarm> getAlarmWeather(String date) {
            if (TextUtils.isEmpty(warnWeatherTitle) || TextUtils.isEmpty(warnWeatherDetail)) {
                return null;
            }
            List<Alarm> alarmList = new ArrayList<>();
            OppoChinaAlarm alarm = OppoChinaAlarm.build(areaCode, areaName, DateUtils.parseOppoCurrentWeatherDate
                    (date), warnWeatherTitle, warnWeatherDetail);
            if (alarm == null) {
                return alarmList;
            }
            alarmList.add(alarm);
            return alarmList;
        }
    }

    public OppoChinaResponseParserV3(WeatherRequest request) {
        mRequest = request;
    }

    public RootWeather parseCurrent(byte[] data) throws ParseException {
        return innerCommonParse(data);
    }

    public RootWeather parseHourForecasts(byte[] data) throws ParseException {
        return innerCommonParse(data);
    }

    public RootWeather parseDailyForecasts(byte[] data) throws ParseException {
        return innerCommonParse(data);
    }

    public RootWeather parseAqi(byte[] data) throws ParseException {
        return innerCommonParse(data);
    }

    public RootWeather parseLifeIndex(byte[] data) throws ParseException {
        return null;
    }

    public RootWeather parseAlarm(byte[] data) throws ParseException {
        return null;
    }

    private RootWeather innerCommonParse(byte[] data) throws ParseException {
        Exception e;
        Throwable th;
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        RootWeatherBuilder builder = new RootWeatherBuilder();
        InputStream stream = null;
        try {
            stream = IOUtils.getInputStreamFromByteArray(data);
            InputStream gZIPInputStream = new GZIPInputStream(stream);
            try {
                String jsonData = IOUtils.byteArrayToString(IOUtils.toByteArray(gZIPInputStream), CONTENT_ENCODE);
                if (jsonData == null) {
                    throw new ParseException("Data to parse is null!");
                }
                int i;
                JSONObject dataObject = new JSONObject(jsonData);
                builder.ts = dataObject.getString("ts");
                String ver = dataObject.getString("ver");
                dataObject = new JSONObject(dataObject.getString("info"));
                builder.areaCode = dataObject.getString("city_id");
                builder.areaName = dataObject.getString("city");
                JSONObject currentObject = new JSONObject(dataObject.getString("current"));
                builder.currentTemperature = NumberUtils.valueToDouble(currentObject.getString("temp"));
                builder.currentWeatherId = WeatherUtils.oppoChinaWeatherTextToWeatherId(currentObject.getString
                        (WeatherEntry.TABLE_NAME));
                builder.currentWindDirection = Wind.getDirectionFromOppo(currentObject.getString("wind_direction"));
                builder.currentWindPower = currentObject.getString("wind_power");
                builder.currentRelativeHumidity = NumberUtils.valueToInt(currentObject.getString(WeatherEntry
                        .COLUMN_7_HUMIDITY));
                builder.time = currentObject.getString("time");
                builder.currentUVIndexText = currentObject.getString("uv");
                builder.avg_pm25 = NumberUtils.valueToInt(currentObject.getString("avg_pm25"));
                builder.aqiValue = builder.avg_pm25;
                builder.date = currentObject.getString("pm_time");
                builder.aqi = currentObject.getString("aqi");
                builder.body_temp = NumberUtils.valueToDouble(currentObject.getString("body_temp"));
                builder.pressure = NumberUtils.valueToInt(currentObject.getString("pressure"));
                builder.visibility = NumberUtils.valueToInt(currentObject.getString("visibility"));
                JSONObject warnObject = new JSONObject(dataObject.getString("warn"));
                builder.warnWeatherTitle = warnObject.getString("warn_name");
                builder.warnWeatherDetail = warnObject.getString("warn_text");
                JSONArray daysArray = dataObject.getJSONArray("days");
                int daySize = daysArray.length();
                for (i = 0; i < daySize; i++) {
                    RootWeatherBuilder.DailyForecastsHolder dayHolder = new RootWeatherBuilder.DailyForecastsHolder();
                    JSONObject dayObject = new JSONObject(daysArray.getString(i));
                    dayHolder.date = DateUtils.parseOppoforcastv3Date(dayObject.getString("date"));
                    dayHolder.dayWeatherId = WeatherUtils.oppoChinaWeatherTextToWeatherId(dayObject.getString
                            ("day_weather"));
                    dayHolder.maxTemperature = NumberUtils.valueToDouble(dayObject.getString("day_temp"));
                    dayHolder.nightWeatherId = WeatherUtils.oppoChinaWeatherTextToWeatherId(dayObject.getString
                            ("night_weather"));
                    dayHolder.minTemperature = NumberUtils.valueToDouble(dayObject.getString("night_temp"));
                    dayHolder.sunRise = DateUtils.parseOppoSunDate(dayObject.getString("sunrise"));
                    dayHolder.sunSet = DateUtils.parseOppoSunDate(dayObject.getString("sunset"));
                    dayHolder.mobileLink = StringUtils.getDailyMobileLink(builder.areaCode, i + 1);
                    builder.add(dayHolder);
                }
                JSONArray hoursArray = dataObject.getJSONArray("hours");
                int hourSize = hoursArray.length();
                for (i = 0; i < hourSize; i++) {
                    RootWeatherBuilder.HourForecastsHolder hourHolder = new RootWeatherBuilder.HourForecastsHolder();
                    JSONObject hourObject = new JSONObject(hoursArray.getString(i));
                    hourHolder.time = DateUtils.parseOppoCurrentWeatherDate(hourObject.getString("time"));
                    hourHolder.weatherId = WeatherUtils.oppoChinaWeatherTextToWeatherId(hourObject.getString
                            (WeatherEntry.TABLE_NAME));
                    hourHolder.temperature = NumberUtils.valueToDouble(hourObject.getString("temp"));
                    builder.add(hourHolder);
                }
                if (gZIPInputStream != null) {
                    try {
                        gZIPInputStream.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                return builder.build();
            } catch (Exception e3) {
                e = e3;
                stream = gZIPInputStream;
                try {
                    LogUtils.e(TAG, "Can not parse data!", e);
                    throw new ParseException(e.getMessage());
                } catch (Throwable th2) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                    }
                    throw th2;
                }
            } catch (Throwable th3) {
                stream = gZIPInputStream;
                if (stream != null) {
                    stream.close();
                }
                throw th3;
            }
        } catch (Exception e4) {
            e = e4;
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }
}
