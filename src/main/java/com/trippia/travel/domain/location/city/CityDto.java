package com.trippia.travel.domain.location.city;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityDto {

    @AllArgsConstructor
    @Getter @Setter
    public static class CityGroupedByTypeResponse {
        private Map<String, List<CitySummary>> groupedCities = new HashMap<>();
    }

    @AllArgsConstructor
    @Getter @Setter
    public static class CitySummary {
        private Long id;
        private String name;
    }


}
