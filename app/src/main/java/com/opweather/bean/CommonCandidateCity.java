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

    public CommonCandidateCity(String mCityCode, String mCitynameChs, String mCitynameCht, String mCitynameEn, String
            mCityProvinceChs, String mCityProvinceCht, String mCityProvinceEn, String mCityCountryChs, String
                                       mCityCountryCht, String mCityCountryEn, String mCityCountryID, int provider) {
        this.mProvider = 1;
        this.mCityCode = mCityCode;
        this.mCityNameChs = mCitynameChs;
        this.mCityNameCht = mCitynameCht;
        this.mCityNameEn = mCitynameEn;
        this.mCityProvinceChs = mCityProvinceChs;
        this.mCityProvinceCht = mCityProvinceCht;
        this.mCityProvinceEn = mCityProvinceEn;
        this.mCityCountryChs = mCityCountryChs;
        this.mCityCountryCht = mCityCountryCht;
        this.mCityCountryEn = mCityCountryEn;
        this.mCityCountryID = mCityCountryID;
        this.mProvider = provider;
    }

    public String getCityCode() {
        return this.mCityCode;
    }

    public void setCityCode(String cityCode) {
        this.mCityCode = cityCode;
    }

    public String getCityName(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        if ("zh" .equalsIgnoreCase(locale.getLanguage()) && "cn" .equalsIgnoreCase(locale.getCountry())) {
            return this.mCityNameChs;
        }
        return ("tw" .equalsIgnoreCase(locale.getCountry()) || "hk" .equalsIgnoreCase(locale.getCountry()) || "mo"
                .equalsIgnoreCase(locale.getCountry())) ? mCityNameCht : mCityNameEn;
    }

    public String getCityProvince(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        if ("zh" .equalsIgnoreCase(locale.getLanguage()) && "cn" .equalsIgnoreCase(locale.getCountry())) {
            return this.mCityProvinceChs;
        }
        return ("tw" .equalsIgnoreCase(locale.getCountry()) || "hk" .equalsIgnoreCase(locale.getCountry()) || "mo"
                .equalsIgnoreCase(locale.getCountry())) ? this.mCityProvinceCht : this.mCityProvinceEn;
    }

    public String getCityCountry(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        if ("zh" .equalsIgnoreCase(locale.getLanguage()) && "cn" .equalsIgnoreCase(locale.getCountry())) {
            return this.mCityCountryChs;
        }
        return ("tw" .equalsIgnoreCase(locale.getCountry()) || "hk" .equalsIgnoreCase(locale.getCountry()) || "mo"
                .equalsIgnoreCase(locale.getCountry())) ? this.mCityCountryCht : this.mCityCountryEn;
    }

    public String getCityCountryID() {
        return this.mCityCountryID;
    }

    public int getCityProvider() {
        return this.mProvider;
    }

    public String getCityProvinceEn() {
        return this.mCityProvinceEn;
    }

    public boolean equals(Object obj) {
        return (obj instanceof CommonCandidateCity) && this.mCityCode.equals(((CommonCandidateCity) obj).mCityCode);
    }

    public int hashCode() {
        return mCityCode.equals(StarWarUtils.STATWAR_NAME) ? -1 :Integer.valueOf(mCityCode);
    }
}
