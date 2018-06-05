package com.opweather.bean;


import com.opweather.util.StringUtils;

public class CandidateCity {
    AdministrativeArea AdministrativeArea;
    AdministrativeArea Country;
    String Key;
    String LocalizedName;
    String Type;

    public CandidateCity() {
        this.Key = StringUtils.EMPTY_STRING;
        this.Type = StringUtils.EMPTY_STRING;
        this.LocalizedName = StringUtils.EMPTY_STRING;
        this.Country = new AdministrativeArea();
        this.AdministrativeArea = new AdministrativeArea();
    }

    public String getKey() {
        return this.Key;
    }

    public String getType() {
        return this.Type;
    }

    public String getLocalizedName() {
        return this.LocalizedName;
    }

    public AdministrativeArea getCountry() {
        return this.Country;
    }

    public AdministrativeArea getAdministrativeArea() {
        return this.AdministrativeArea;
    }
}
