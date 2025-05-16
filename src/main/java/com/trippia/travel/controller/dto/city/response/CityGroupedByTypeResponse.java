package com.trippia.travel.controller.dto.city.response;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CityGroupedByTypeResponse {

    private Map<String, List<CitySummary>> groupedCities = new HashMap<>();

    @Builder
    private CityGroupedByTypeResponse(Map<String, List<CitySummary>> groupedCities) {
        this.groupedCities = groupedCities;
    }
}
