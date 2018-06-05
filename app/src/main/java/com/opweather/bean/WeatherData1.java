package com.opweather.bean;

import java.util.List;

public class WeatherData1 {

    /**
     * ts : 201805312106
     * ver : 2.0
     * info : {"city_id":"101281701","city":"中山","current":{"temp":"31","weather":"多云","wind_direction":"东南风","wind_power":"7级","humidity":"69","time":"21:06","uv":"最弱","avg_pm25":"45","pm_time":"2018-05-31 20:00:00","aqi":"优","body_temp":"34","pressure":"1007","visibility":"16100"},"days":[{"date":"20180531","day_weather":"多云","day_temp":"36","night_weather":"多云","night_temp":"27","sunrise":"05:42:00","sunset":"19:06:00"},{"date":"20180601","day_weather":"阵雨","day_temp":"35","night_weather":"阵雨","night_temp":"26","sunrise":"05:42:00","sunset":"19:07:00"},{"date":"20180602","day_weather":"阵雨","day_temp":"33","night_weather":"多云","night_temp":"26","sunrise":"05:42:00","sunset":"19:07:00"},{"date":"20180603","day_weather":"多云","day_temp":"33","night_weather":"多云","night_temp":"26","sunrise":"05:42:00","sunset":"19:08:00"},{"date":"20180604","day_weather":"阵雨","day_temp":"32","night_weather":"中雨","night_temp":"25","sunrise":"05:41:00","sunset":"19:08:00"},{"date":"20180605","day_weather":"中雨","day_temp":"31","night_weather":"中雨","night_temp":"24","sunrise":"05:41:00","sunset":"19:09:00"}],"hours":[{"time":"2018-05-31 21:00:00","weather":"多云","temp":"31","pcpratio":""},{"time":"2018-05-31 22:00:00","weather":"多云","temp":"31","pcpratio":"2"},{"time":"2018-05-31 23:00:00","weather":"晴","temp":"30","pcpratio":"2"},{"time":"2018-06-01 00:00:00","weather":"晴","temp":"29","pcpratio":"2"},{"time":"2018-06-01 01:00:00","weather":"晴","temp":"29","pcpratio":"3"},{"time":"2018-06-01 02:00:00","weather":"晴","temp":"29","pcpratio":"3"},{"time":"2018-06-01 03:00:00","weather":"晴","temp":"28","pcpratio":"3"},{"time":"2018-06-01 04:00:00","weather":"晴","temp":"28","pcpratio":"3"},{"time":"2018-06-01 05:00:00","weather":"晴","temp":"27","pcpratio":"3"},{"time":"2018-06-01 06:00:00","weather":"晴","temp":"27","pcpratio":"3"},{"time":"2018-06-01 07:00:00","weather":"晴","temp":"28","pcpratio":"5"},{"time":"2018-06-01 08:00:00","weather":"晴","temp":"30","pcpratio":"5"},{"time":"2018-06-01 09:00:00","weather":"多云","temp":"31","pcpratio":"6"},{"time":"2018-06-01 10:00:00","weather":"晴","temp":"32","pcpratio":"7"},{"time":"2018-06-01 11:00:00","weather":"多云","temp":"33","pcpratio":"7"},{"time":"2018-06-01 12:00:00","weather":"多云","temp":"34","pcpratio":"11"},{"time":"2018-06-01 13:00:00","weather":"多云","temp":"33","pcpratio":"19"},{"time":"2018-06-01 14:00:00","weather":"多云","temp":"35","pcpratio":"19"},{"time":"2018-06-01 15:00:00","weather":"多云","temp":"34","pcpratio":"24"},{"time":"2018-06-01 16:00:00","weather":"多云","temp":"33","pcpratio":"49"},{"time":"2018-06-01 17:00:00","weather":"阵雨","temp":"32","pcpratio":"58"},{"time":"2018-06-01 18:00:00","weather":"阵雨","temp":"31","pcpratio":"58"},{"time":"2018-06-01 19:00:00","weather":"阵雨","temp":"28","pcpratio":"56"},{"time":"2018-06-01 20:00:00","weather":"多云","temp":"28","pcpratio":"49"}],"warn":{"warn_name":"","warn_text":""}}
     */

    private String ts;
    private String ver;
    private InfoBean info;

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * city_id : 101281701
         * city : 中山
         * current : {"temp":"31","weather":"多云","wind_direction":"东南风","wind_power":"7级","humidity":"69","time":"21:06","uv":"最弱","avg_pm25":"45","pm_time":"2018-05-31 20:00:00","aqi":"优","body_temp":"34","pressure":"1007","visibility":"16100"}
         * days : [{"date":"20180531","day_weather":"多云","day_temp":"36","night_weather":"多云","night_temp":"27","sunrise":"05:42:00","sunset":"19:06:00"},{"date":"20180601","day_weather":"阵雨","day_temp":"35","night_weather":"阵雨","night_temp":"26","sunrise":"05:42:00","sunset":"19:07:00"},{"date":"20180602","day_weather":"阵雨","day_temp":"33","night_weather":"多云","night_temp":"26","sunrise":"05:42:00","sunset":"19:07:00"},{"date":"20180603","day_weather":"多云","day_temp":"33","night_weather":"多云","night_temp":"26","sunrise":"05:42:00","sunset":"19:08:00"},{"date":"20180604","day_weather":"阵雨","day_temp":"32","night_weather":"中雨","night_temp":"25","sunrise":"05:41:00","sunset":"19:08:00"},{"date":"20180605","day_weather":"中雨","day_temp":"31","night_weather":"中雨","night_temp":"24","sunrise":"05:41:00","sunset":"19:09:00"}]
         * hours : [{"time":"2018-05-31 21:00:00","weather":"多云","temp":"31","pcpratio":""},{"time":"2018-05-31 22:00:00","weather":"多云","temp":"31","pcpratio":"2"},{"time":"2018-05-31 23:00:00","weather":"晴","temp":"30","pcpratio":"2"},{"time":"2018-06-01 00:00:00","weather":"晴","temp":"29","pcpratio":"2"},{"time":"2018-06-01 01:00:00","weather":"晴","temp":"29","pcpratio":"3"},{"time":"2018-06-01 02:00:00","weather":"晴","temp":"29","pcpratio":"3"},{"time":"2018-06-01 03:00:00","weather":"晴","temp":"28","pcpratio":"3"},{"time":"2018-06-01 04:00:00","weather":"晴","temp":"28","pcpratio":"3"},{"time":"2018-06-01 05:00:00","weather":"晴","temp":"27","pcpratio":"3"},{"time":"2018-06-01 06:00:00","weather":"晴","temp":"27","pcpratio":"3"},{"time":"2018-06-01 07:00:00","weather":"晴","temp":"28","pcpratio":"5"},{"time":"2018-06-01 08:00:00","weather":"晴","temp":"30","pcpratio":"5"},{"time":"2018-06-01 09:00:00","weather":"多云","temp":"31","pcpratio":"6"},{"time":"2018-06-01 10:00:00","weather":"晴","temp":"32","pcpratio":"7"},{"time":"2018-06-01 11:00:00","weather":"多云","temp":"33","pcpratio":"7"},{"time":"2018-06-01 12:00:00","weather":"多云","temp":"34","pcpratio":"11"},{"time":"2018-06-01 13:00:00","weather":"多云","temp":"33","pcpratio":"19"},{"time":"2018-06-01 14:00:00","weather":"多云","temp":"35","pcpratio":"19"},{"time":"2018-06-01 15:00:00","weather":"多云","temp":"34","pcpratio":"24"},{"time":"2018-06-01 16:00:00","weather":"多云","temp":"33","pcpratio":"49"},{"time":"2018-06-01 17:00:00","weather":"阵雨","temp":"32","pcpratio":"58"},{"time":"2018-06-01 18:00:00","weather":"阵雨","temp":"31","pcpratio":"58"},{"time":"2018-06-01 19:00:00","weather":"阵雨","temp":"28","pcpratio":"56"},{"time":"2018-06-01 20:00:00","weather":"多云","temp":"28","pcpratio":"49"}]
         * warn : {"warn_name":"","warn_text":""}
         */

        private String city_id;
        private String city;
        private CurrentBean current;
        private WarnBean warn;
        private List<DaysBean> days;
        private List<HoursBean> hours;

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public CurrentBean getCurrent() {
            return current;
        }

        public void setCurrent(CurrentBean current) {
            this.current = current;
        }

        public WarnBean getWarn() {
            return warn;
        }

        public void setWarn(WarnBean warn) {
            this.warn = warn;
        }

        public List<DaysBean> getDays() {
            return days;
        }

        public void setDays(List<DaysBean> days) {
            this.days = days;
        }

        public List<HoursBean> getHours() {
            return hours;
        }

        public void setHours(List<HoursBean> hours) {
            this.hours = hours;
        }

        public static class CurrentBean {
            /**
             * temp : 31
             * weather : 多云
             * wind_direction : 东南风
             * wind_power : 7级
             * humidity : 69
             * time : 21:06
             * uv : 最弱
             * avg_pm25 : 45
             * pm_time : 2018-05-31 20:00:00
             * aqi : 优
             * body_temp : 34
             * pressure : 1007
             * visibility : 16100
             */

            private String temp;
            private String weather;
            private String wind_direction;
            private String wind_power;
            private String humidity;
            private String time;
            private String uv;
            private String avg_pm25;
            private String pm_time;
            private String aqi;
            private String body_temp;
            private String pressure;
            private String visibility;

            public String getTemp() {
                return temp;
            }

            public void setTemp(String temp) {
                this.temp = temp;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getWind_direction() {
                return wind_direction;
            }

            public void setWind_direction(String wind_direction) {
                this.wind_direction = wind_direction;
            }

            public String getWind_power() {
                return wind_power;
            }

            public void setWind_power(String wind_power) {
                this.wind_power = wind_power;
            }

            public String getHumidity() {
                return humidity;
            }

            public void setHumidity(String humidity) {
                this.humidity = humidity;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUv() {
                return uv;
            }

            public void setUv(String uv) {
                this.uv = uv;
            }

            public String getAvg_pm25() {
                return avg_pm25;
            }

            public void setAvg_pm25(String avg_pm25) {
                this.avg_pm25 = avg_pm25;
            }

            public String getPm_time() {
                return pm_time;
            }

            public void setPm_time(String pm_time) {
                this.pm_time = pm_time;
            }

            public String getAqi() {
                return aqi;
            }

            public void setAqi(String aqi) {
                this.aqi = aqi;
            }

            public String getBody_temp() {
                return body_temp;
            }

            public void setBody_temp(String body_temp) {
                this.body_temp = body_temp;
            }

            public String getPressure() {
                return pressure;
            }

            public void setPressure(String pressure) {
                this.pressure = pressure;
            }

            public String getVisibility() {
                return visibility;
            }

            public void setVisibility(String visibility) {
                this.visibility = visibility;
            }
        }

        public static class WarnBean {
            /**
             * warn_name :
             * warn_text :
             */

            private String warn_name;
            private String warn_text;

            public String getWarn_name() {
                return warn_name;
            }

            public void setWarn_name(String warn_name) {
                this.warn_name = warn_name;
            }

            public String getWarn_text() {
                return warn_text;
            }

            public void setWarn_text(String warn_text) {
                this.warn_text = warn_text;
            }
        }

        public static class DaysBean {
            /**
             * date : 20180531
             * day_weather : 多云
             * day_temp : 36
             * night_weather : 多云
             * night_temp : 27
             * sunrise : 05:42:00
             * sunset : 19:06:00
             */

            private String date;
            private String day_weather;
            private String day_temp;
            private String night_weather;
            private String night_temp;
            private String sunrise;
            private String sunset;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getDay_weather() {
                return day_weather;
            }

            public void setDay_weather(String day_weather) {
                this.day_weather = day_weather;
            }

            public String getDay_temp() {
                return day_temp;
            }

            public void setDay_temp(String day_temp) {
                this.day_temp = day_temp;
            }

            public String getNight_weather() {
                return night_weather;
            }

            public void setNight_weather(String night_weather) {
                this.night_weather = night_weather;
            }

            public String getNight_temp() {
                return night_temp;
            }

            public void setNight_temp(String night_temp) {
                this.night_temp = night_temp;
            }

            public String getSunrise() {
                return sunrise;
            }

            public void setSunrise(String sunrise) {
                this.sunrise = sunrise;
            }

            public String getSunset() {
                return sunset;
            }

            public void setSunset(String sunset) {
                this.sunset = sunset;
            }
        }

        public static class HoursBean {
            /**
             * time : 2018-05-31 21:00:00
             * weather : 多云
             * temp : 31
             * pcpratio :
             */

            private String time;
            private String weather;
            private String temp;
            private String pcpratio;

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getWeather() {
                return weather;
            }

            public void setWeather(String weather) {
                this.weather = weather;
            }

            public String getTemp() {
                return temp;
            }

            public void setTemp(String temp) {
                this.temp = temp;
            }

            public String getPcpratio() {
                return pcpratio;
            }

            public void setPcpratio(String pcpratio) {
                this.pcpratio = pcpratio;
            }
        }
    }
}
