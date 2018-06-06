package com.opweather.api.parser;

import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.IOUtils;
import com.opweather.api.helper.LogUtils;
import com.opweather.api.helper.NumberUtils;
import com.opweather.api.helper.StringUtils;
import com.opweather.api.helper.WeatherUtils;
import com.opweather.api.impl.AccuRequest;
import com.opweather.api.nodes.AccuAlarm;
import com.opweather.api.nodes.AccuCurrentWeather;
import com.opweather.api.nodes.AccuDailyForecastsWeather;
import com.opweather.api.nodes.AccuWind;
import com.opweather.api.nodes.Alarm;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.nodes.Sun;
import com.opweather.api.nodes.Temperature;
import com.opweather.api.nodes.Wind;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccuResponseParser implements ResponseParser {
    private static final String CONTENT_CHARSET = "utf-8";
    private static final String TAG = "AccuResponseParser";
    private final String mSearchKey;

    private static class CurrentWeatherBuilder implements WeatherBuilder {
        double mCentigradeValue;
        Wind.Direction mDirection;
        double mFahrenheitValue;
        String mLocalTimeZone;
        String mMainMoblieLink;
        Date mObservationDate;
        int mRelativeHumidity;
        String mSearchKey;
        double mSpeed;
        int mUVIndex;
        String mUVIndexText;
        int mWeatherId;
        String mWeatherText;

        private CurrentWeatherBuilder() {
            mSearchKey = null;
            mObservationDate = null;
            mWeatherId = 0;
            mLocalTimeZone = null;
            mWeatherText = null;
            mRelativeHumidity = Integer.MIN_VALUE;
            mSpeed = Double.NaN;
            mUVIndex = Integer.MIN_VALUE;
            mUVIndexText = null;
            mCentigradeValue = Double.NaN;
            mFahrenheitValue = Double.NaN;
            mMainMoblieLink = null;
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(mSearchKey)) {
                throw new BuilderException("Valid area code empty.");
            } else if (mWeatherId == 0) {
                throw new BuilderException("Valid weather id unknown.");
            } else {
                Wind wind;
                Temperature temperature = null;
                if (!(NumberUtils.isNaN(mCentigradeValue) || NumberUtils.isNaN(mFahrenheitValue))) {
                    temperature = new Temperature(mSearchKey, AccuRequest.DATA_SOURCE_NAME, mCentigradeValue,
                            mFahrenheitValue);
                }
                if (mDirection != null) {
                    wind = new AccuWind(mSearchKey, AccuRequest.DATA_SOURCE_NAME, mDirection, mSpeed);
                } else {
                    wind = null;
                }
                RootWeather result = new RootWeather(mSearchKey, AccuRequest.DATA_SOURCE_NAME);
                result.setCurrentWeather(new AccuCurrentWeather(mSearchKey, mWeatherId, mLocalTimeZone, mWeatherText,
                        mObservationDate, temperature, mRelativeHumidity, wind, mUVIndex, mUVIndexText,
                        mMainMoblieLink));
                return result;
            }
        }
    }

    private static class DailyForecastsWeatherBuilder implements WeatherBuilder {
        private List<ItemHolder> itemList;
        String mSearchKey;
        String mlink;

        static class ItemHolder {
            int dayWeatherId;
            String dayWeatherText;
            long epochDate;
            double maxCentigradeValue;
            double maxFahrenheitValue;
            double minCentigradeValue;
            double minFahrenheitValue;
            int nightWeatherId;
            String nightWeatherText;
            long rise;
            long set;
            String url;

            ItemHolder() {
                dayWeatherId = 0;
                nightWeatherId = 0;
            }
        }

        private DailyForecastsWeatherBuilder() {
            mSearchKey = null;
            mlink = null;
        }

        public void add(ItemHolder item) {
            if (itemList == null) {
                itemList = new ArrayList();
            }
            itemList.add(item);
        }

        public void addLink(String link) {
            mlink = link;
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(mSearchKey)) {
                throw new BuilderException("Valid area code empty.");
            } else if (itemList == null) {
                throw new BuilderException("Valid forecasts is empty.");
            } else {
                RootWeather rootWeather = new RootWeather(mSearchKey, AccuRequest.DATA_SOURCE_NAME);
                List<DailyForecastsWeather> list = new ArrayList<>();
                for (ItemHolder holder : itemList) {
                    if (!NumberUtils.isNaN(holder.epochDate)) {
                        Temperature minTemperature = new Temperature(mSearchKey, AccuRequest.DATA_SOURCE_NAME, holder
                                .minCentigradeValue, holder.minFahrenheitValue);
                        Temperature maxTemperature = new Temperature(mSearchKey, AccuRequest.DATA_SOURCE_NAME, holder
                                .maxCentigradeValue, holder.maxFahrenheitValue);
                        Sun sun = null;
                        if (!(NumberUtils.isNaN(holder.rise) || NumberUtils.isNaN(holder.set))) {
                            sun = new Sun(mSearchKey, AccuRequest.DATA_SOURCE_NAME, holder.rise, holder.set);
                        }
                        List<DailyForecastsWeather> list2 = list;
                        list2.add(new AccuDailyForecastsWeather(mSearchKey, AccuRequest.DATA_SOURCE_NAME, holder
                                .dayWeatherId, holder.dayWeatherText, holder.nightWeatherId, holder.nightWeatherText,
                                DateUtils.epochDateToDate(holder.epochDate), minTemperature, maxTemperature, sun,
                                holder.url));
                    }
                }
                if (list.size() == 0) {
                    throw new BuilderException("Empty forecasts weather!");
                }
                rootWeather.setDailyForecastsWeather(list);
                rootWeather.setFutureLink(mlink);
                return rootWeather;
            }
        }
    }

    private static class HourForecastsWeatherBuilder implements WeatherBuilder {
        private List<ItemHolder> itemList;
        String mSearchKey;

        static class ItemHolder {
            ItemHolder() {
            }
        }

        private HourForecastsWeatherBuilder() {
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
            } else if (itemList != null) {
                return null;
            } else {
                throw new BuilderException("Valid forecasts is empty.");
            }
        }
    }

    private static class WeatherAlarmBuilder implements WeatherBuilder {
        private List<ItemHolder> itemList;
        String mSearchKey;

        static class ItemHolder {
            String areaName;
            String contentText;
            long endTime;
            String levelName;
            long startTime;
            String summary;

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
            RootWeather result = new RootWeather(mSearchKey, AccuRequest.DATA_SOURCE_NAME);
            List<Alarm> list = new ArrayList();
            if (itemList != null) {
                for (ItemHolder holder : itemList) {
                    AccuAlarm alarm = AccuAlarm.build(mSearchKey, holder.areaName, holder.summary, holder.levelName,
                            holder.contentText, holder.startTime, holder.endTime);
                    if (alarm != null) {
                        list.add(alarm);
                    }
                }
            }
            result.setWeatherAlarms(list);
            return result;
        }
    }

    public AccuResponseParser(String key) {
        mSearchKey = key;
    }

    public RootWeather parseCurrent(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        CurrentWeatherBuilder builder = new CurrentWeatherBuilder();
        try {
            String jsonData = IOUtils.byteArrayToString(data, CONTENT_CHARSET);
            if (jsonData == null) {
                throw new ParseException("Data to parse is null!");
            }
            JSONObject jsonObject = new JSONArray(jsonData).getJSONObject(0);
            String localObservationDateTime = jsonObject.getString("LocalObservationDateTime");
            int length = localObservationDateTime.length();
            String localTimeZone = localObservationDateTime.substring(length - 6, length);
            long epochTime = (long) jsonObject.getInt("EpochTime");
            int weatherIcon = jsonObject.getInt("WeatherIcon");
            String weatherText = jsonObject.getString("WeatherText");
            JSONObject temperature = jsonObject.getJSONObject("Temperature");
            double metricValue = temperature.getJSONObject("Metric").getDouble("Value");
            double imperialValue = temperature.getJSONObject("Imperial").getDouble("Value");
            int relativeHumidity = jsonObject.getInt("RelativeHumidity");
            JSONObject wind = jsonObject.getJSONObject("Wind");
            JSONObject windDirection = wind.getJSONObject("Direction");
            JSONObject windSpeed = wind.getJSONObject("Speed");
            String windEnglish = windDirection.getString("English");
            double windSpeedValue = windSpeed.getJSONObject("Metric").getDouble("Value");
            int uVIndex = jsonObject.getInt("UVIndex");
            String uVIndexText = jsonObject.getString("UVIndexText");
            String mobileLink = jsonObject.getString("MobileLink") + StringUtils.getPartner(jsonObject.getString
                    ("MobileLink"));
            builder.mSearchKey = mSearchKey;
            builder.mObservationDate = DateUtils.epochDateToDate(epochTime);
            builder.mWeatherId = WeatherUtils.accuWeatherIconToWeatherId(weatherIcon);
            builder.mLocalTimeZone = localTimeZone;
            builder.mWeatherText = weatherText;
            builder.mRelativeHumidity = relativeHumidity;
            builder.mDirection = Wind.getDirectionFromAccu(windEnglish);
            builder.mSpeed = windSpeedValue;
            builder.mUVIndex = uVIndex;
            builder.mUVIndexText = uVIndexText;
            builder.mCentigradeValue = metricValue;
            builder.mFahrenheitValue = imperialValue;
            builder.mMainMoblieLink = mobileLink;
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    public RootWeather parseHourForecasts(byte[] data) throws ParseException {
        throw new ParseException("Now we don't need hour forecasts data when use accu data source!");
    }

    public RootWeather parseDailyForecasts(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        DailyForecastsWeatherBuilder builder = new DailyForecastsWeatherBuilder();
        builder.mSearchKey = mSearchKey;
        try {
            String jsonData = IOUtils.byteArrayToString(data, CONTENT_CHARSET);
            if (jsonData == null) {
                throw new ParseException("Data to parse is null!");
            }
            JSONObject jSONObject = new JSONObject(jsonData);
            JSONArray dailyArray = jSONObject.getJSONArray("DailyForecasts");
            JSONObject headObject = jSONObject.getJSONObject("Headline");
            builder.addLink(headObject.getString("MobileLink") + StringUtils.getPartner(headObject.getString
                    ("MobileLink")));
            for (int i = 0; i < dailyArray.length(); i++) {
                DailyForecastsWeatherBuilder.ItemHolder holder = new DailyForecastsWeatherBuilder.ItemHolder();
                JSONObject item = dailyArray.getJSONObject(i);
                long epochDate = item.getLong("EpochDate");
                JSONObject sun = item.getJSONObject("Sun");
                String mobileLink = item.getString("MobileLink") + StringUtils.getPartner(item.getString("MobileLink"));
                long epochRise = sun.optLong("EpochRise", Long.MIN_VALUE);
                long epochSet = sun.optLong("EpochSet", Long.MIN_VALUE);
                JSONObject temperature = item.getJSONObject("Temperature");
                double minValue = temperature.getJSONObject("Minimum").getDouble("Value");
                double maxValue = temperature.getJSONObject("Maximum").getDouble("Value");
                JSONObject day = item.getJSONObject("Day");
                int dayId = day.getInt("Icon");
                String dayText = day.getString("IconPhrase");
                JSONObject night = item.getJSONObject("Night");
                int nightId = night.getInt("Icon");
                String nightText = night.getString("IconPhrase");
                holder.epochDate = epochDate;
                holder.maxCentigradeValue = maxValue;
                holder.maxFahrenheitValue = WeatherUtils.centigradeToFahrenheit(maxValue);
                holder.minCentigradeValue = minValue;
                holder.minFahrenheitValue = WeatherUtils.centigradeToFahrenheit(minValue);
                holder.rise = epochRise;
                holder.set = epochSet;
                holder.dayWeatherId = WeatherUtils.accuWeatherIconToWeatherId(dayId);
                holder.nightWeatherId = WeatherUtils.accuWeatherIconToWeatherId(nightId);
                holder.dayWeatherText = dayText;
                holder.nightWeatherText = nightText;
                holder.url = mobileLink;
                builder.add(holder);
            }
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }

    public RootWeather parseAqi(byte[] data) throws ParseException {
        throw new ParseException("Now we don't need aqi data when use accu data source!");
    }

    public RootWeather parseLifeIndex(byte[] data) throws ParseException {
        throw new ParseException("Now we don't need life index data when use accu data source!");
    }

    public RootWeather parseAlarm(byte[] data) throws ParseException {
        if (data == null) {
            throw new ParseException("The data to parser is null!");
        }
        WeatherAlarmBuilder builder = new WeatherAlarmBuilder();
        builder.mSearchKey = mSearchKey;
        try {
            String jsonData = IOUtils.byteArrayToString(data, CONTENT_CHARSET);
            if (jsonData == null) {
                throw new ParseException("Data to parse is null!");
            }
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                JSONObject description = item.getJSONObject("Description");
                JSONArray area = item.getJSONArray("Area");
                for (int j = 0; j < area.length(); j++) {
                    WeatherAlarmBuilder.ItemHolder holder = new WeatherAlarmBuilder.ItemHolder();
                    JSONObject areaItem = area.getJSONObject(j);
                    holder.summary = description.getString("Localized");
                    holder.levelName = item.getString("Level");
                    holder.areaName = areaItem.getString("Name");
                    holder.contentText = areaItem.getString("Summary");
                    holder.startTime = areaItem.getLong("EpochStartTime");
                    holder.endTime = areaItem.getLong("EpochEndTime");
                    builder.add(holder);
                }
            }
            return builder.build();
        } catch (Exception e) {
            LogUtils.e(TAG, "Can not parse data!", e);
            throw new ParseException(e.getMessage());
        }
    }
}
