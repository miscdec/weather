package com.opweather.api.parser;

import com.opweather.api.WeatherRequest;
import com.opweather.api.helper.DateUtils;
import com.opweather.api.helper.IOUtils;
import com.opweather.api.impl.OppoForeignRequest;
import com.opweather.api.nodes.CurrentWeather;
import com.opweather.api.nodes.DailyForecastsWeather;
import com.opweather.api.nodes.RootWeather;
import com.opweather.api.helper.StringUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class OppoForeignResponseParser extends OppoResponseParser {

    private static class RootWeatherBuilder implements WeatherBuilder {
        private String areaCode;
        private String areaName;
        private List<DailyForecastsHolder> itemList;

        static class DailyForecastsHolder {
            int currentUVIndex;
            String currentUVIndexText;
            int currentWeatherId;
            Date date;
            int dayWeatherId;
            double maxTemperature;
            double minTemperature;
            int nightWeatherId;

            DailyForecastsHolder() {
                this.currentWeatherId = Integer.MIN_VALUE;
                this.currentUVIndex = Integer.MIN_VALUE;
                this.currentUVIndexText = null;
                this.dayWeatherId = Integer.MIN_VALUE;
                this.nightWeatherId = Integer.MIN_VALUE;
                this.date = null;
                this.minTemperature = Double.NaN;
                this.maxTemperature = Double.NaN;
            }
        }

        private RootWeatherBuilder() {
            this.areaName = null;
            this.itemList = null;
        }

        public void add(DailyForecastsHolder item) {
            if (this.itemList == null) {
                this.itemList = new ArrayList();
            }
            this.itemList.add(item);
        }

        public RootWeather build() throws BuilderException {
            if (StringUtils.isBlank(this.areaCode)) {
                throw new BuilderException("Valid area code empty.");
            }
            RootWeather rootWeather = new RootWeather(this.areaCode, this.areaName, OppoForeignRequest.DATA_SOURCE_NAME);
            CurrentWeather currentWeather = getCurrentWeather(this.itemList);
            List<DailyForecastsWeather> dailyForecastsWeather = null;
            if (this.itemList != null && this.itemList.size() > 0) {
                dailyForecastsWeather = new ArrayList();
                for (DailyForecastsHolder holder : this.itemList) {
                    if (holder.date != null) {
                        List<DailyForecastsWeather> list = dailyForecastsWeather;
                        //list.add(new OppoForeignDailyForecastsWeather(this.areaCode, this.areaName, OppoForeignRequest.DATA_SOURCE_NAME, holder.dayWeatherId, holder.nightWeatherId, holder.date, new Temperature(this.areaCode, this.areaName, OppoForeignRequest.DATA_SOURCE_NAME, holder.minTemperature, WeatherUtils.centigradeToFahrenheit(holder.minTemperature)), new Temperature(this.areaCode, this.areaName, OppoForeignRequest.DATA_SOURCE_NAME, holder.maxTemperature, WeatherUtils.centigradeToFahrenheit(holder.maxTemperature)), null));
                    }
                }
            }
            rootWeather.setCurrentWeather(currentWeather);
            if (dailyForecastsWeather.size() > 0) {
                rootWeather.setDailyForecastsWeather(dailyForecastsWeather);
            } else {
                rootWeather.setDailyForecastsWeather(null);
            }
            return rootWeather;
        }

        private CurrentWeather getCurrentWeather(List<DailyForecastsHolder> list) {
            if (list == null || list.size() == 0) {
                return null;
            }
            for (DailyForecastsHolder holder : list) {
                if (DateUtils.isSameDay(System.currentTimeMillis(), holder.date.getTime(), TimeZone.getTimeZone("GMT+08:00"))) {
                    double ct = (holder.maxTemperature + holder.minTemperature) / 2.0d;
                    String str = this.areaCode;
                    //return new OppoForeignCurrentWeather(str, this.areaName, OppoForeignRequest.DATA_SOURCE_NAME, holder.currentWeatherId, holder.date, new Temperature(this.areaCode, this.areaName, OppoForeignRequest.DATA_SOURCE_NAME, ct, WeatherUtils.centigradeToFahrenheit(ct)), Integer.MIN_VALUE, null, holder.currentUVIndex, holder.currentUVIndexText);
                }
            }
            return null;
        }
    }

    public OppoForeignResponseParser(WeatherRequest request) {
        super(request);
    }

    public RootWeather parseCurrent(byte[] data) throws ParseException {
        if (data != null) {
            return innerCommonParse(data);
        }
        throw new ParseException("The data to parser is null!");
    }

    public RootWeather parseHourForecasts(byte[] data) throws ParseException {
        throw new ParseException("Parser class OppoForeignResponseParser do not support parser hour forecasts!");
    }

    public RootWeather parseDailyForecasts(byte[] data) throws ParseException {
        if (data != null) {
            return innerCommonParse(data);
        }
        throw new ParseException("The data to parser is null!");
    }

    public RootWeather parseAqi(byte[] data) throws ParseException {
        throw new ParseException("Parser class OppoForeignResponseParser do not support parser aqi data!");
    }

    public RootWeather parseLifeIndex(byte[] data) throws ParseException {
        throw new ParseException("Parser class OppoForeignResponseParser do not support parser life index data!");
    }

    public RootWeather parseAlarm(byte[] data) throws ParseException {
        throw new ParseException("Parser class OppoForeignResponseParser do not support parser alarm data!");
    }

    private RootWeather innerCommonParse(byte[] data) throws ParseException {
        RootWeatherBuilder builder = new RootWeatherBuilder();
        InputStream stream = null;
        try {
            stream = IOUtils.getInputStreamFromByteArray(data);
            XmlPullParser parser = getXmlPullParser(stream);
            for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                if (eventType == 2 && parser.getName().equalsIgnoreCase("WeatherForecast")) {
                    eventType = parser.next();
                    while (true) {
                        if (eventType == 3 && parser.getName().equalsIgnoreCase("WeatherForecast")) {
                            break;
                        }
                        if (eventType == 2 && parser.getName().equalsIgnoreCase("city_id")) {
                            builder.areaCode = parser.nextText();
                        } /*else if (eventType == 2 && parser.getName().equalsIgnoreCase(WeatherWarningActivity.INTENT_PARA_CITY)) {
                            builder.areaName = parser.nextText();
                        }*/ else if (eventType != 2) {
                            continue;
                        } else if (parser.getName().equalsIgnoreCase("items")) {
                            eventType = parser.next();
                            while (true) {
                                if (eventType == 3 && parser.getName().equalsIgnoreCase("items")) {
                                    break;
                                }
                                if (eventType == 2 && parser.getName().equalsIgnoreCase("item")) {
                                    eventType = parser.next();
                                    RootWeatherBuilder.DailyForecastsHolder holder = new RootWeatherBuilder.DailyForecastsHolder();
                                    while (true) {
                                        if (eventType == 3 && parser.getName().equalsIgnoreCase("item")) {
                                            break;
                                        }
                                        /*if (eventType == 2 && parser.getName().equalsIgnoreCase("date")) {
                                            holder.date = DateUtils.parseOppoforcastDate(parser.nextText());
                                        } else if (eventType == 2 && parser.getName().equalsIgnoreCase("descr")) {
                                            holder.currentWeatherId = WeatherUtils.oppoForeignWeatherTextToWeatherId(parser.nextText());
                                        } else if (eventType == 2 && parser.getName().equalsIgnoreCase("uv_index")) {
                                            holder.currentUVIndex = NumberUtils.valueToInt(parser.nextText());
                                        } else if (eventType == 2 && parser.getName().equalsIgnoreCase("uv_desc")) {
                                            holder.currentUVIndexText = parser.nextText();
                                        } else if (eventType == 2 && parser.getName().equalsIgnoreCase("descr1")) {
                                            holder.dayWeatherId = WeatherUtils.oppoForeignWeatherTextToWeatherId(parser.nextText());
                                        } else if (eventType == 2 && parser.getName().equalsIgnoreCase("temp_high")) {
                                            holder.maxTemperature = NumberUtils.valueToDouble(parser.nextText());
                                        } else if (eventType == 2 && parser.getName().equalsIgnoreCase("descr2")) {
                                            holder.nightWeatherId = WeatherUtils.oppoForeignWeatherTextToWeatherId(parser.nextText());
                                        } else if (eventType == 2 && parser.getName().equalsIgnoreCase("temp_low")) {
                                            holder.minTemperature = NumberUtils.valueToDouble(parser.nextText());
                                        }*/
                                        eventType = parser.next();
                                    }
                                    builder.add(holder);
                                }
                                eventType = parser.next();
                            }
                        }
                        eventType = parser.next();
                    }
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return builder.build();
        } catch (Exception e2) {
            throw new ParseException(e2.getMessage());
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return null;
    }
}
