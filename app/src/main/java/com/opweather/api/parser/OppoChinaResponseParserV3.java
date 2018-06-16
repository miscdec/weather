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
import com.opweather.bean.HourForecastsWeather;
import com.opweather.db.CityWeatherDBHelper;
import com.opweather.ui.WeatherWarningActivity;

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
            Date date;
            int dayWeatherId;
            double maxTemperature;
            double minTemperature;
            String mobileLink;
            int nightWeatherId;
            Date sunRise;
            Date sunSet;

            DailyForecastsHolder() {
                this.date = null;
                this.dayWeatherId = Integer.MIN_VALUE;
                this.nightWeatherId = Integer.MIN_VALUE;
                this.minTemperature = Double.NaN;
                this.maxTemperature = Double.NaN;
                this.sunRise = null;
                this.sunSet = null;
                this.mobileLink = null;
            }
        }

        static class HourForecastsHolder {
            double temperature;
            Date time;
            int weatherId;

            HourForecastsHolder() {
                this.weatherId = Integer.MIN_VALUE;
                this.temperature = Double.NaN;
                this.time = null;
            }
        }

        private RootWeatherBuilder() {
            this.areaName = null;
            this.aqiValue = Integer.MIN_VALUE;
            this.ts = null;
            this.currentWeatherId = Integer.MIN_VALUE;
            this.currentTemperature = Double.NaN;
            this.currentWindDirection = null;
            this.currentWindPower = null;
            this.currentRelativeHumidity = Integer.MIN_VALUE;
            this.time = null;
            this.currentUVIndexText = null;
            this.avg_pm25 = Integer.MIN_VALUE;
            this.aqi = null;
            this.body_temp = Double.NaN;
            this.pressure = Integer.MIN_VALUE;
            this.visibility = Integer.MIN_VALUE;
            this.warnWeatherTitle = null;
            this.warnWeatherDetail = null;
            this.dailyItemList = null;
            this.hourItemList = null;
        }

        public void add(DailyForecastsHolder item) {
            if (this.dailyItemList == null) {
                this.dailyItemList = new ArrayList<>();
            }
            this.dailyItemList.add(item);
        }

        public void add(HourForecastsHolder item) {
            if (this.hourItemList == null) {
                this.hourItemList = new ArrayList<>();
            }
            this.hourItemList.add(item);
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(this.areaCode)) {
                throw new BuilderException("Valid area code empty.");
            }
            RootWeather rootWeather = new RootWeather(this.areaCode, this.areaName, OppoChinaRequest.DATA_SOURCE_NAME);
            if (this.date != null) {
                LogUtils.d(TAG, "Date: " + this.date);
            }
            OppoChinaAqiWeather aqiWeather = new OppoChinaAqiWeather(this.areaCode, this.areaName, OppoChinaRequest
                    .DATA_SOURCE_NAME, this.aqiValue, this.avg_pm25, this.aqi);
            CurrentWeather currentWeather = getCurrentWeather(this.date, this.dailyItemList);
            List<DailyForecastsWeather> dailyForecastsWeather = null;
            if (this.dailyItemList != null && this.dailyItemList.size() > 0) {
                dailyForecastsWeather = new ArrayList<>();
                for (DailyForecastsHolder holder : this.dailyItemList) {
                    if (holder.date != null) {
                        Temperature minTemperature = new Temperature(this.areaCode, this.areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.minTemperature, WeatherUtils.centigradeToFahrenheit(holder
                                .minTemperature));
                        Temperature maxTemperature = new Temperature(this.areaCode, this.areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.maxTemperature, WeatherUtils.centigradeToFahrenheit(holder
                                .maxTemperature));
                        Temperature bodyTemperature = new Temperature(this.areaCode, this.areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, this.body_temp, WeatherUtils.centigradeToFahrenheit(this.body_temp));
                        List<DailyForecastsWeather> list = dailyForecastsWeather;
                        list.add(new OppoChinaDailyForecastsWeather(this.areaCode, this.areaName, OppoChinaRequest
                                .DATA_SOURCE_NAME, holder.dayWeatherId, holder.nightWeatherId, holder.date,
                                minTemperature, maxTemperature, new Sun(this.areaCode, this.areaName,
                                OppoChinaRequest.DATA_SOURCE_NAME, holder.sunRise, holder.sunSet), bodyTemperature,
                                this.pressure, this.visibility, holder.mobileLink));
                    }
                }
            }
            List<HourForecastsWeather> hourForecastsWeather = null;
            if (this.hourItemList != null && this.hourItemList.size() > 0) {
                hourForecastsWeather = new ArrayList<>();
                for (HourForecastsHolder holder2 : this.hourItemList) {
                    if (holder2.time != null) {
                        if (holder2.weatherId != Integer.MIN_VALUE || holder2.temperature != Double.NaN) {
                            List<HourForecastsWeather> list2 = hourForecastsWeather;
                            list2.add(new OppoChinaHourForecastsWeather(this.areaCode, holder2.weatherId,
                                    holder2.time, new Temperature(this.areaCode, this.areaName, OppoChinaRequest
                                    .DATA_SOURCE_NAME, holder2.temperature, WeatherUtils.centigradeToFahrenheit
                                    (holder2.temperature))));
                        }
                    }
                }
            }
            List<Alarm> oppoAlarmList = getAlarmWeather(this.date);
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
            if (NumberUtils.isNaN(this.currentTemperature)) {
                for (DailyForecastsHolder holder : list) {
                    if (DateUtils.isSameDay(System.currentTimeMillis(), holder.date.getTime(), TimeZone.getTimeZone
                            ("GMT+08:00"))) {
                        this.currentTemperature = (holder.maxTemperature + holder.minTemperature) / 2.0d;
                        break;
                    }
                }
            }
            Date observationDate = DateUtils.parseOppoObservationDate(this.ts);
            Temperature temperature = new Temperature(this.areaCode, this.areaName, OppoChinaRequest
                    .DATA_SOURCE_NAME, this.currentTemperature, WeatherUtils.centigradeToFahrenheit(this
                    .currentTemperature));
            return new OppoChinaCurrentWeather(this.areaCode, this.areaName, OppoChinaRequest.DATA_SOURCE_NAME, this
                    .currentWeatherId, observationDate, temperature, this.currentRelativeHumidity, new OppoWind(this
                    .areaCode, this.areaName, OppoChinaRequest.DATA_SOURCE_NAME, this.currentWindDirection, this
                    .currentWindPower), Integer.MIN_VALUE, this.currentUVIndexText);
        }

        private List<Alarm> getAlarmWeather(String date) {
            if (TextUtils.isEmpty(this.warnWeatherTitle) || TextUtils.isEmpty(this.warnWeatherDetail)) {
                return null;
            }
            List<Alarm> alarmList = new ArrayList<>();
            OppoChinaAlarm alarm = OppoChinaAlarm.build(this.areaCode, this.areaName, DateUtils
                    .parseOppoCurrentWeatherDate(date), this.warnWeatherTitle, this.warnWeatherDetail);
            if (alarm == null) {
                return alarmList;
            }
            alarmList.add(alarm);
            return alarmList;
        }
    }

    public OppoChinaResponseParserV3(WeatherRequest request) {
        this.mRequest = request;
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
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        RootWeatherBuilder builder = new RootWeatherBuilder();
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.getInputStreamFromByteArray(data);
            InputStream gZIPInputStream = new GZIPInputStream(inputStream);
            try {
                String jsonData = IOUtils.byteArrayToString(IOUtils.toByteArray(gZIPInputStream), CONTENT_ENCODE);
                if (jsonData == null) {
                    throw new ParseException("Data to parse is null!");
                }
                int i;
                JSONObject jSONObject = new JSONObject(jsonData);
                builder.ts = jSONObject.getString("ts");
                String string = jSONObject.getString("ver");
                jSONObject = new JSONObject(jSONObject.getString("info"));
                builder.areaCode = jSONObject.getString("city_id");
                builder.areaName = jSONObject.getString(WeatherWarningActivity.INTENT_PARA_CITY);
                JSONObject currentObject = new JSONObject(jSONObject.getString("current"));
                builder.currentTemperature = NumberUtils.valueToDouble(currentObject.getString("temp"));
                builder.currentWeatherId = WeatherUtils.oppoChinaWeatherTextToWeatherId(currentObject.getString
                        (CityWeatherDBHelper.WeatherEntry.TABLE_NAME));
                builder.currentWindDirection = Wind.getDirectionFromOppo(currentObject.getString("wind_direction"));
                builder.currentWindPower = currentObject.getString("wind_power");
                builder.currentRelativeHumidity = NumberUtils.valueToInt(currentObject.getString(CityWeatherDBHelper
                        .WeatherEntry.COLUMN_7_HUMIDITY));
                builder.time = currentObject.getString("time");
                builder.currentUVIndexText = currentObject.getString("uv");
                builder.avg_pm25 = NumberUtils.valueToInt(currentObject.getString("avg_pm25"));
                builder.aqiValue = builder.avg_pm25;
                builder.date = currentObject.getString("pm_time");
                builder.aqi = currentObject.getString("aqi");
                builder.body_temp = NumberUtils.valueToDouble(currentObject.getString("body_temp"));
                builder.pressure = NumberUtils.valueToInt(currentObject.getString("pressure"));
                builder.visibility = NumberUtils.valueToInt(currentObject.getString("visibility"));
                jSONObject = new JSONObject(jSONObject.getString("warn"));
                builder.warnWeatherTitle = jSONObject.getString("warn_name");
                builder.warnWeatherDetail = jSONObject.getString("warn_text");
                JSONArray daysArray = jSONObject.getJSONArray("days");
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
                JSONArray hoursArray = jSONObject.getJSONArray("hours");
                int hourSize = hoursArray.length();
                for (i = 0; i < hourSize; i++) {
                    RootWeatherBuilder.HourForecastsHolder hourHolder = new RootWeatherBuilder.HourForecastsHolder();
                    JSONObject hourObject = new JSONObject(hoursArray.getString(i));
                    hourHolder.time = DateUtils.parseOppoCurrentWeatherDate(hourObject.getString("time"));
                    hourHolder.weatherId = WeatherUtils.oppoChinaWeatherTextToWeatherId(hourObject.getString
                            (CityWeatherDBHelper.WeatherEntry.TABLE_NAME));
                    hourHolder.temperature = NumberUtils.valueToDouble(hourObject.getString("temp"));
                    builder.add(hourHolder);
                }
                try {
                    gZIPInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return builder.build();
            } catch (Exception e2) {
                inputStream = gZIPInputStream;
            } catch (Throwable th3) {
                inputStream = gZIPInputStream;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                throw th3;
            }
        } catch (Exception e5) {
            LogUtils.e(TAG, "Can not parse data!", e5);
            throw new ParseException(e5.getMessage());
        }
        return builder.build();
    }
}
