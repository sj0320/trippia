package com.trippia.travel.domain.location.place;

import lombok.Getter;

public class PlaceDto {

    @Getter
    public static class PlaceRecommendResponse {
        private Long id;
        private String placeName;
        private String category;
    }


}
