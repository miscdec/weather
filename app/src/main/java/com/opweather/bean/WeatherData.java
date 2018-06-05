package com.opweather.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherData implements Parcelable {
    private int mCurrentRealFeelTemp;
    private int mCurrentTemp;
    private int mHighTemp;
    private int mHumidity;
    private String mLocationId;
    private int mLowTemp;
    private long mSunriseTime;
    private long mSunsetTime;
    private long mTimestamp;
    private int mWeatherDescriptionId;

    public WeatherData() {
        mCurrentRealFeelTemp = 0;
        mCurrentTemp = 0;
        mHighTemp = 0;
        mHumidity = 0;
        mLocationId = "";
        mLowTemp = 0;
        mSunriseTime = 0;
        mSunsetTime = 0;
        mTimestamp = 0;
        mWeatherDescriptionId = 0;
    }

    public int getCurrentRealFeelTemp() {
        return mCurrentRealFeelTemp;
    }

    public void setCurrentRealFeelTemp(int currentRealFeelTemp) {
        mCurrentRealFeelTemp = currentRealFeelTemp;
    }

    public int getCurrentTemp() {
        return mCurrentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        mCurrentTemp = currentTemp;
    }

    public int getHighTemp() {
        return mHighTemp;
    }

    public void setHighTemp(int highTemp) {
        mHighTemp = highTemp;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int humidity) {
        mHumidity = humidity;
    }

    public String getLocationId() {
        return mLocationId;
    }

    public void setLocationId(String locationId) {
        mLocationId = locationId;
    }

    public int getLowTemp() {
        return mLowTemp;
    }

    public void setLowTemp(int lowTemp) {
        mLowTemp = lowTemp;
    }

    public long getSunriseTime() {
        return mSunriseTime;
    }

    public void setSunriseTime(long sunriseTime) {
        mSunriseTime = sunriseTime;
    }

    public long getSunsetTime() {
        return mSunsetTime;
    }

    public void setSunsetTime(long sunsetTime) {
        mSunsetTime = sunsetTime;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public int getWeatherDescriptionId() {
        return mWeatherDescriptionId;
    }

    public void setWeatherDescriptionId(int weatherDescriptionId) {
        mWeatherDescriptionId = weatherDescriptionId;
    }

    protected WeatherData(Parcel in) {
        mCurrentRealFeelTemp = in.readInt();
        mCurrentTemp = in.readInt();
        mHighTemp = in.readInt();
        mHumidity = in.readInt();
        mLocationId = in.readString();
        mLowTemp = in.readInt();
        mSunriseTime = in.readLong();
        mSunsetTime = in.readLong();
        mTimestamp = in.readLong();
        mWeatherDescriptionId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCurrentRealFeelTemp);
        dest.writeInt(mCurrentTemp);
        dest.writeInt(mHighTemp);
        dest.writeInt(mHumidity);
        dest.writeString(mLocationId);
        dest.writeInt(mLowTemp);
        dest.writeLong(mSunriseTime);
        dest.writeLong(mSunsetTime);
        dest.writeLong(mTimestamp);
        dest.writeInt(mWeatherDescriptionId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel source) {
            WeatherData weatherDate = new WeatherData();
            weatherDate.mLocationId = source.readString();
            weatherDate.mHighTemp = source.readInt();
            weatherDate.mLowTemp = source.readInt();
            weatherDate.mCurrentTemp = source.readInt();
            weatherDate.mCurrentRealFeelTemp = source.readInt();
            weatherDate.mHumidity = source.readInt();
            weatherDate.mWeatherDescriptionId = source.readInt();
            weatherDate.mTimestamp = source.readLong();
            weatherDate.mSunriseTime = source.readLong();
            weatherDate.mSunsetTime = source.readLong();
            return weatherDate;
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };
}
