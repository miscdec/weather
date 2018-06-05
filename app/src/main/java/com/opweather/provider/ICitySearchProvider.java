package com.opweather.provider;

import com.opweather.bean.CommonCandidateCity;

import java.util.ArrayList;

public interface ICitySearchProvider {
    ArrayList<CommonCandidateCity> getCandidateCityList();
}
