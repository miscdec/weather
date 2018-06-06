package com.opweather.bean;

import android.content.Context;

import com.opweather.starwar.StarWarUtils;

import java.util.Locale;

public class CommonCandidateCity {
    public static final int ACC_FOREIGN_CITY = 2;
    public static final int OP_CHINA_CITY = 1;
    private String mCityCode;
    private String mCityCountryChs;
    private String mCityCountryCht;
    private String mCityCountryEn;
    private String mCityCountryID;
    private String mCityNameChs;
    private String mCityNameCht;
    private String mCityNameEn;
    private String mCityProvinceChs;
    private String mCityProvinceCht;
    private String mCityProvinceEn;
    private int mProvider;

    public CommonCandidateCity(String mCityCode, String mCityname, String mCityProvince, String mCityCountry, String
            mCityCountryID, int provider) {
        this(mCityCode, mCityname, mCityname, mCityname, mCityProvince, mCityProvince, mCityProvince, mCityCountry,
                mCityCountry, mCityCountry, mCityCountryID, provider);
    }


    public CommonCandidateCity(String cityCode, String citynameChs, String citynameCht, String citynameEn, String
            cityProvinceChs, String cityProvinceCht, String cityProvinceEn, String cityCountryChs, String
                                       cityCountryCht, String cityCountryEn, String cityCountryID, int provider) {
        mProvider = 1;
        mCityCode = cityCode;
        mCityNameChs = citynameChs;
        mCityNameCht = citynameCht;
        mCityNameEn = citynameEn;
        mCityProvinceChs = cityProvinceChs;
        mCityProvinceCht = cityProvinceCht;
        mCityProvinceEn = cityProvinceEn;
        mCityCountryChs = cityCountryChs;
        mCityCountryCht = cityCountryCht;
        mCityCountryEn = cityCountryEn;
        mCityCountryID = cityCountryID;
        mProvider = provider;
    }

    public String getCityCode() {
        return mCityCode;
    }

    public void setCityCode(String cityCode) {
        mCityCode = cityCode;
    }

    public String getCityName(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        if ("zh" .equalsIgnoreCase(locale.getLanguage()) && "cn" .equalsIgnoreCase(locale.getCountry())) {
            return mCityNameChs;
        }
        return ("tw" .equalsIgnoreCase(locale.getCountry()) || "hk" .equalsIgnoreCase(locale.getCountry()) || "mo"
                .equalsIgnoreCase(locale.getCountry())) ? mCityNameCht : mCityNameEn;
    }

    public String getCityProvince(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        if ("zh" .equalsIgnoreCase(locale.getLanguage()) && "cn" .equalsIgnoreCase(locale.getCountry())) {
            return mCityProvinceChs;
        }
        return ("tw" .equalsIgnoreCase(locale.getCountry()) || "hk" .equalsIgnoreCase(locale.getCountry()) || "mo"
                .equalsIgnoreCase(locale.getCountry())) ? mCityProvinceCht : mCityProvinceEn;
    }

    public String getCityCountry(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        if ("zh" .equalsIgnoreCase(locale.getLanguage()) && "cn" .equalsIgnoreCase(locale.getCountry())) {
            return mCityCountryChs;
        }
        return ("tw" .equalsIgnoreCase(locale.getCountry()) || "hk" .equalsIgnoreCase(locale.getCountry()) || "mo"
                .equalsIgnoreCase(locale.getCountry())) ? mCityCountryCht : mCityCountryEn;
    }

    public String getCityCountryID() {
        return mCityCountryID;
    }

    public int getCityProvider() {
        return mProvider;
    }

    public String getCityProvinceEn() {
        return mCityProvinceEn;
    }

    public boolean equals(Object obj) {
        return (obj instanceof CommonCandidateCity) && mCityCode.equals(((CommonCandidateCity) obj).mCityCode);
    }

    public int hashCode() {
        return mCityCode.equals(StarWarUtils.STATWAR_NAME) ? -1 : Integer.valueOf(mCityCode);
    }
}
