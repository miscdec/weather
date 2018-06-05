package com.opweather.bean;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.opweather.util.StringUtils;


public class LocationData {
    AdministrativeArea AdministrativeArea;
    AdministrativeArea Country;
    String EnglishName;
    GeoPosition GeoPosition;
    boolean IsAlias;
    String Key;
    String LocalizedName;
    String PrimaryPostalCode;
    int Rank;
    AdministrativeArea Region;
    JsonArray SupplementalAdminAreas;
    TimeZone TimeZone;
    String Type;
    String Version;

    public LocationData() {
        this.Version = StringUtils.EMPTY_STRING;
        this.Key = StringUtils.EMPTY_STRING;
        this.Type = StringUtils.EMPTY_STRING;
        this.Rank = 0;
        this.LocalizedName = StringUtils.EMPTY_STRING;
        this.EnglishName = StringUtils.EMPTY_STRING;
        this.PrimaryPostalCode = StringUtils.EMPTY_STRING;
        this.Region = new AdministrativeArea();
        this.Country = new AdministrativeArea();
        this.AdministrativeArea = new AdministrativeArea();
        this.TimeZone = new TimeZone();
        this.GeoPosition = new GeoPosition();
        this.SupplementalAdminAreas = new JsonArray();
    }

    public String getVersion() {
        return this.Version;
    }

    public String getKey() {
        return this.Key;
    }

    public String getType() {
        return this.Type;
    }

    public int getRank() {
        return this.Rank;
    }

    public String getLocalizedName() {
        return this.LocalizedName;
    }

    public String getEnglishName() {
        return this.EnglishName;
    }

    public String getPrimaryPostalCode() {
        return this.PrimaryPostalCode;
    }

    public AdministrativeArea getRegion() {
        return this.Region;
    }

    public AdministrativeArea getCountry() {
        return this.Country;
    }

    public AdministrativeArea getAdministrativeArea() {
        return this.AdministrativeArea;
    }

    public TimeZone getTimeZone() {
        return this.TimeZone;
    }

    public GeoPosition getGeoPosition() {
        return this.GeoPosition;
    }

    public boolean isAlias() {
        return this.IsAlias;
    }

    public City[] getSupplementalAdminAreas() {
        return (City[]) new Gson().fromJson(this.SupplementalAdminAreas, City[].class);
    }

    public void printData() {
        Log.d("LocationData", "Location Key : " + getKey() + "\n" + "Localized Name : " + getLocalizedName() + "\n" + "English Name : " + getEnglishName());
    }
}
