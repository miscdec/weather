package com.opweather.api.parser;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.IOUtils;
import com.opweather.api.helper.LogUtils;
import com.opweather.api.helper.NumberUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.SwaRequest;
import com.opweather.api.nodes.Alarm;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.LifeIndexWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.nodes.Sun;
import com.opweather.api.nodes.SwaAlarm;
import com.opweather.api.nodes.SwaAqiWeather;
import com.opweather.api.nodes.SwaCurrentWeather;
import com.opweather.api.nodes.SwaDailyForecastsWeather;
import com.opweather.api.nodes.SwaHourForecastsWeather;
import com.opweather.api.nodes.SwaLifeIndexWeather;
import com.opweather.api.nodes.SwaWind;
import com.opweather.api.nodes.Temperature;
import com.opweather.api.nodes.Wind;
import com.opweather.bean.HourForecastsWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SwaResponseParser implements ResponseParser {
    private static final String CONTENT_CHARSET = "utf-8";
    private static final String TAG = "SwaResponseParser";
    private final String mSearchKey;

    private static class CurrentWeatherBuilder implements WeatherBuilder {
        String mSearchKey;
        String relativeHumidity;
        String temperature;
        String time;
        String weather;
        String windDirection;
        String windPower;

        private CurrentWeatherBuilder() {
            mSearchKey = null;
            temperature = null;
            relativeHumidity = null;
            windPower = null;
            windDirection = null;
            weather = null;
            time = null;
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(mSearchKey)) {
                throw new BuilderException("Valid area code empty.");
            }
            int iWeatherId = WeatherUtils.swaWeatherIdToWeatherId(weather);
            Date iObservationDate = DateUtils.parseSwaCurrentDate(time);
            double iCentigradeValue = NumberUtils.valueToDouble(temperature);
            double iFahrenheitValue = WeatherUtils.centigradeToFahrenheit(iCentigradeValue);
            Temperature iTemperature = null;
            Wind swaWind = new SwaWind(mSearchKey, windDirection, windPower);
            if (!(NumberUtils.isNaN(iCentigradeValue) || NumberUtils.isNaN(iFahrenheitValue))) {
                iTemperature = new Temperature(mSearchKey, SwaRequest.DATA_SOURCE_NAME, iCentigradeValue,
                        iFahrenheitValue);
            }
            RootWeather result = new RootWeather(mSearchKey, SwaRequest.DATA_SOURCE_NAME);
            result.setCurrentWeather(new SwaCurrentWeather(mSearchKey, iWeatherId, iObservationDate, iTemperature,
                    NumberUtils.valueToInt(relativeHumidity), swaWind, Integer.MIN_VALUE, StringUtils.EMPTY_STRING));
            return result;
        }
    }

    private static class DailyForecastsWeatherBuilder implements WeatherBuilder {
        private List<ItemHolder> itemList;
        String mCityNameCn;
        String mSearchKey;

        static class ItemHolder {
            String dayTemperature;
            String dayWeather;
            String nightTemperature;
            String nightWeather;
            String sunriseAndSunset;

            ItemHolder() {
                dayWeather = null;
                nightWeather = null;
                dayTemperature = null;
                nightTemperature = null;
                sunriseAndSunset = null;
            }
        }

        private DailyForecastsWeatherBuilder() {
            mSearchKey = null;
            mCityNameCn = null;
        }

        public void add(ItemHolder item) {
            if (itemList == null) {
                itemList = new ArrayList();
            }
            itemList.add(item);
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(mSearchKey)) {
                throw new BuilderException("Valid area code empty.");
            } else if (itemList == null) {
                throw new BuilderException("Valid forecasts is empty.");
            } else {
                RootWeather rootWeather = new RootWeather(mSearchKey, mCityNameCn, SwaRequest.DATA_SOURCE_NAME);
                List<DailyForecastsWeather> list = new ArrayList();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"), Locale.CHINA);
                for (ItemHolder holder : itemList) {
                    int iDayWeatherId = WeatherUtils.swaWeatherIdToWeatherId(holder.dayWeather);
                    int iNightWeatherId = WeatherUtils.swaWeatherIdToWeatherId(holder.nightWeather);
                    Temperature iDayTemperature = null;
                    Temperature iNightTemperature = null;
                    double iDayCentigradeValue = NumberUtils.valueToDouble(holder.dayTemperature);
                    double iDayFahrenheitValue = WeatherUtils.centigradeToFahrenheit(iDayCentigradeValue);
                    double iNightCentigradeValue = NumberUtils.valueToDouble(holder.nightTemperature);
                    double iNightFahrenheitValue = WeatherUtils.centigradeToFahrenheit(iDayCentigradeValue);
                    if (!NumberUtils.isNaN(iDayCentigradeValue)) {
                        iDayTemperature = new Temperature(mSearchKey, SwaRequest.DATA_SOURCE_NAME,
                                iDayCentigradeValue, iDayFahrenheitValue);
                    }
                    if (!NumberUtils.isNaN(iNightCentigradeValue)) {
                        iNightTemperature = new Temperature(mSearchKey, SwaRequest.DATA_SOURCE_NAME,
                                iNightCentigradeValue, iNightFahrenheitValue);
                    }
                    Sun iSun = null;
                    Date rise = SwaResponseParser.getSunDate(calendar.getTime(), holder.sunriseAndSunset, true);
                    Date set = SwaResponseParser.getSunDate(calendar.getTime(), holder.sunriseAndSunset, false);
                    if (rise != null && set != null) {
                        Sun sun = new Sun(mSearchKey, SwaRequest.DATA_SOURCE_NAME, rise, set);
                    }
                    list.add(new SwaDailyForecastsWeather(mSearchKey, mCityNameCn, iDayWeatherId,
                            iNightWeatherId, calendar.getTime(), iDayTemperature, iNightTemperature, iSun));
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
                if (list.size() == 0) {
                    throw new BuilderException("Empty forecasts weather!");
                }
                rootWeather.setDailyForecastsWeather(list);
                return rootWeather;
            }
        }
    }

    private static class HourForecastsWeatherBuilder implements WeatherBuilder {
        private List<ItemHolder> itemList;
        String mSearchKey;

        static class ItemHolder {
            String forecastTime;
            String temperature;
            String weatherId;

            ItemHolder() {
                weatherId = null;
                temperature = null;
                forecastTime = null;
            }
        }

        private HourForecastsWeatherBuilder() {
            mSearchKey = null;
        }

        public void add(ItemHolder item) {
            if (itemList == null) {
                itemList = new ArrayList();
            }
            itemList.add(item);
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(mSearchKey)) {
                throw new BuilderException("Valid area code empty.");
            } else if (itemList == null) {
                throw new BuilderException("Valid forecasts is empty.");
            } else {
                RootWeather result = new RootWeather(mSearchKey, null, SwaRequest.DATA_SOURCE_NAME);
                List<HourForecastsWeather> list = new ArrayList();
                for (ItemHolder holder : itemList) {
                    HourForecastsWeather forecastsWeather = SwaHourForecastsWeather.buildFromString(mSearchKey,
                            holder.weatherId, holder.forecastTime, holder.temperature);
                    if (forecastsWeather != null) {
                        list.add(forecastsWeather);
                    }
                }
                if (list.size() == 0) {
                    throw new BuilderException("Empty forecasts weather!");
                }
                result.setHourForecastsWeather(list);
                return result;
            }
        }
    }

    private static class LifeIndexBuilder implements WeatherBuilder {
        private List<ItemHolder> itemList;
        String mSearchKey;

        static class ItemHolder {
            String indexCnAlias;
            String indexCnName;
            String indexLevel;
            String indexShortName;
            String indexText;

            ItemHolder() {
                indexShortName = null;
                indexCnName = null;
                indexCnAlias = null;
                indexLevel = null;
                indexText = null;
            }
        }

        private LifeIndexBuilder() {
            mSearchKey = null;
        }

        public void add(ItemHolder item) {
            if (itemList == null) {
                itemList = new ArrayList();
            }
            itemList.add(item);
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(mSearchKey)) {
                throw new BuilderException("Valid area code empty.");
            } else if (itemList == null) {
                throw new BuilderException("Valid life index is empty.");
            } else {
                RootWeather result = new RootWeather(mSearchKey, SwaRequest.DATA_SOURCE_NAME);
                SwaLifeIndexWeather lifeIndexWeather = new SwaLifeIndexWeather(mSearchKey);
                for (ItemHolder holder : itemList) {
                    lifeIndexWeather.add(new LifeIndexWeather.LifeIndex(holder.indexShortName, holder.indexCnName,
                            holder.indexCnAlias, holder.indexLevel, holder.indexText));
                }
                if (lifeIndexWeather.size() == 0) {
                    throw new BuilderException("Empty forecasts weather!");
                }
                result.setLifeIndexWeather(lifeIndexWeather);
                return result;
            }
        }
    }

    private static class WeatherAlarmBuilder implements WeatherBuilder {
        private List<ItemHolder> itemList;
        String mSearchKey;

        static class ItemHolder {
            String alarmAreaName;
            String contentText;
            String levelName;
            String levelNo;
            String publishTime;
            String typeName;
            String typeNo;

            ItemHolder() {
            }
        }

        private WeatherAlarmBuilder() {
            mSearchKey = null;
        }

        public void add(ItemHolder item) {
            if (itemList == null) {
                itemList = new ArrayList<>();
            }
            itemList.add(item);
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(mSearchKey)) {
                throw new BuilderException("Valid area code empty.");
            }
            RootWeather result = new RootWeather(mSearchKey, SwaRequest.DATA_SOURCE_NAME);
            List<Alarm> list = new ArrayList<>();
            if (itemList != null) {
                for (ItemHolder holder : itemList) {
                    SwaAlarm alarm = SwaAlarm.build(mSearchKey, holder.alarmAreaName, holder.typeName, holder
                            .typeNo, holder.levelName, holder.levelNo, holder.contentText, holder.publishTime);
                    if (alarm != null) {
                        list.add(alarm);
                    }
                }
            }
            result.setWeatherAlarms(list);
            return result;
        }
    }

    public SwaResponseParser(String key) {
        mSearchKey = key;
    }

    public RootWeather parseCurrent(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        CurrentWeatherBuilder builder = new CurrentWeatherBuilder();
        try {
            JSONObject current = new JSONObject(IOUtils.byteArrayToString(data, CONTENT_CHARSET)).getJSONObject("l");
            builder.mSearchKey = mSearchKey;
            builder.temperature = current.getString("l1");
            builder.relativeHumidity = current.getString("l2");
            builder.windPower = current.getString("l3");
            builder.windDirection = current.getString("l4");
            builder.weather = current.getString("l5");
            builder.time = current.getString("l7");
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    public RootWeather parseHourForecasts(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        HourForecastsWeatherBuilder builder = new HourForecastsWeatherBuilder();
        try {
            JSONObject jsObject = new JSONObject(IOUtils.byteArrayToString(data, CONTENT_CHARSET));
            builder.mSearchKey = mSearchKey;
            JSONArray jsonArray = jsObject.getJSONArray("jh");
            for (int i = 0; i < jsonArray.length(); i++) {
                HourForecastsWeatherBuilder.ItemHolder holder = new HourForecastsWeatherBuilder.ItemHolder();
                JSONObject item = jsonArray.getJSONObject(i);
                holder.weatherId = item.getString("ja");
                holder.temperature = item.getString("jb");
                holder.forecastTime = item.getString("jf");
                builder.add(holder);
            }
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    public RootWeather parseDailyForecasts(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        DailyForecastsWeatherBuilder builder = new DailyForecastsWeatherBuilder();
        try {
            JSONObject jsObject = new JSONObject(IOUtils.byteArrayToString(data, CONTENT_CHARSET));
            JSONObject city = jsObject.getJSONObject("c");
            JSONObject forecasts = jsObject.getJSONObject("f");
            builder.mSearchKey = mSearchKey;
            builder.mCityNameCn = city.getString("c3");
            JSONArray jsonArray = forecasts.getJSONArray("f1");
            for (int i = 0; i < jsonArray.length(); i++) {
                DailyForecastsWeatherBuilder.ItemHolder holder = new DailyForecastsWeatherBuilder.ItemHolder();
                JSONObject item = jsonArray.getJSONObject(i);
                holder.dayWeather = item.getString("fa");
                holder.nightWeather = item.getString("fb");
                holder.dayTemperature = item.getString("fc");
                holder.nightTemperature = item.getString("fd");
                holder.sunriseAndSunset = item.getString("fi");
                builder.add(holder);
            }
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    public RootWeather parseAqi(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        try {
            JSONObject airIndex = new JSONObject(IOUtils.byteArrayToString(data, CONTENT_CHARSET)).getJSONObject("p");
            String pm2_5 = airIndex.getString("p1");
            String aqi = airIndex.getString("p2");
            String time = airIndex.getString("p9");
            RootWeather root = new RootWeather(mSearchKey, SwaRequest.DATA_SOURCE_NAME);
            root.setAqiWeather(SwaAqiWeather.newInstance(mSearchKey, time, pm2_5, aqi));
            return root;
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    public RootWeather parseLifeIndex(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        LifeIndexBuilder builder = new LifeIndexBuilder();
        try {
            builder.mSearchKey = mSearchKey;
            JSONArray jsonArray = new JSONObject(IOUtils.byteArrayToString(data, CONTENT_CHARSET)).getJSONArray("i");
            for (int i = 0; i < jsonArray.length(); i++) {
                LifeIndexBuilder.ItemHolder holder = new LifeIndexBuilder.ItemHolder();
                JSONObject item = jsonArray.getJSONObject(i);
                holder.indexShortName = item.getString("i1");
                holder.indexCnName = item.getString("i2");
                holder.indexCnAlias = item.getString("i3");
                holder.indexLevel = item.getString("i4");
                holder.indexText = item.getString("i5");
                builder.add(holder);
            }
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    public RootWeather parseAlarm(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        WeatherAlarmBuilder builder = new WeatherAlarmBuilder();
        try {
            builder.mSearchKey = mSearchKey;
            JSONArray jsonArray = new JSONObject(IOUtils.byteArrayToString(data, CONTENT_CHARSET)).getJSONArray("w");
            for (int i = 0; i < jsonArray.length(); i++) {
                WeatherAlarmBuilder.ItemHolder holder = new WeatherAlarmBuilder.ItemHolder();
                JSONObject item = jsonArray.getJSONObject(i);
                String string = StringUtils.isBlank(item.getString("w3")) ? StringUtils.isBlank(item.getString("w2"))
                        ? null : item.getString("w2") : item.getString("w3");
                holder.alarmAreaName = string;
                if (!StringUtils.isBlank(holder.alarmAreaName)) {
                    holder.typeName = item.getString("w5");
                    holder.typeNo = item.getString("w4");
                    holder.levelName = item.getString("w7");
                    holder.levelNo = item.getString("w6");
                    holder.contentText = item.getString("w9");
                    holder.publishTime = item.getString("w8");
                    builder.add(holder);
                }
            }
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    private static Date getSunDate(Date date, String str, boolean rise) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String[] array = str.split("\\|");
        if (array.length < 2) {
            return null;
        }
        String dateStr;
        if (rise) {
            dateStr = array[0].trim();
        } else {
            dateStr = array[1].trim();
        }
        return DateUtils.parseSwaDate(date, dateStr);
    }
}
