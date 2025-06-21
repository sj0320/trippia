package com.trippia.travel.controller.dto.place.response;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class RecommendPlaceResponse {

    private String placeId;
    private String placeName;
    private String address;
    private Set<String> themes = new HashSet<>();

    @Builder
    private RecommendPlaceResponse(String placeId, String placeName, String address, Set<String> themes) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.address = address;
        this.themes = themes;
    }
}
