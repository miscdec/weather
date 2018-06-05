package com.opweather.bean;

import com.opweather.util.StringUtils;

public class OpCity {
    String allFirstPY;
    String allPY;
    String areaId;
    String cityCountryChs;
    String cityCountryCht;
    String cityCountryEn;
    boolean cityInChina;
    String firstPY;
    String nameChs;
    String nameCht;
    String nameEn;
    String provinceChs;
    String provinceCht;
    String provinceEn;

    public String getProvinceChs() {
        return this.provinceChs;
    }

    public String getProvinceCht() {
        return this.provinceCht;
    }

    public String getProvinceEn() {
        return this.provinceEn;
    }

    public String getNameChs() {
        return this.nameChs;
    }

    public String getNameCht() {
        return this.nameCht;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public String getAreaId() {
        return this.areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAllPY() {
        return this.allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFirstPY() {
        return this.allFirstPY;
    }

    public void setAllFirstPY(String allFirstPY) {
        this.allFirstPY = allFirstPY;
    }

    public String getFirstPY() {
        return this.firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public String getCountryNameChs() {
        return this.cityCountryChs;
    }

    public String getCountryNameCht() {
        return this.cityCountryCht;
    }

    public String getCountryNameEn() {
        return this.cityCountryEn;
    }

    public void setInChina(boolean inChina) {
        this.cityInChina = inChina;
    }

    public boolean isInChina() {
        return this.cityInChina;
    }

    public OpCity(String provinceChs, String provinceCht, String provinceEn, String nameChs, String nameCht, String
            nameEn, String areaId, String allPY, String allFirstPY, String firstPY, String countryChs, String
                          countryCht, String countryEn, boolean inChina) {
        this.provinceChs = StringUtils.EMPTY_STRING;
        this.provinceCht = StringUtils.EMPTY_STRING;
        this.provinceEn = StringUtils.EMPTY_STRING;
        this.nameChs = StringUtils.EMPTY_STRING;
        this.nameCht = StringUtils.EMPTY_STRING;
        this.nameEn = StringUtils.EMPTY_STRING;
        this.areaId = StringUtils.EMPTY_STRING;
        this.allPY = StringUtils.EMPTY_STRING;
        this.allFirstPY = StringUtils.EMPTY_STRING;
        this.firstPY = StringUtils.EMPTY_STRING;
        this.cityInChina = false;
        this.cityCountryChs = StringUtils.EMPTY_STRING;
        this.cityCountryCht = StringUtils.EMPTY_STRING;
        this.cityCountryEn = StringUtils.EMPTY_STRING;
        this.provinceChs = provinceChs;
        this.provinceCht = provinceCht;
        this.provinceEn = provinceEn;
        this.nameChs = nameChs;
        this.nameCht = nameCht;
        this.nameEn = nameEn;
        this.areaId = areaId;
        this.allPY = allPY;
        this.allFirstPY = allFirstPY;
        this.firstPY = firstPY;
        this.cityCountryChs = countryChs;
        this.cityCountryCht = countryCht;
        this.cityCountryEn = countryEn;
        this.cityInChina = inChina;
    }
}
