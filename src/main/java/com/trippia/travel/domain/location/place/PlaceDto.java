package com.trippia.travel.domain.location.place;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

public class PlaceDto {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class RecommendPlaceResponse {
        private String placeId;
        private String placeName;
        private String address;
        private Set<String> themes = new HashSet<>();
//        private String thumbnailUrl;
    }


}
