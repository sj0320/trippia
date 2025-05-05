package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.location.place.Place;
import lombok.Builder;
import lombok.Data;

public class SchedulePlaceDto {

    @Data
    @Builder
    public static class PlaceSaveRequest{
        private Long scheduleId;

        private String placeId;

        private String name;

        private String address;

        private double latitude;

        private double longitude;

        private String category;

        public Place toEntity(){
            return Place.builder()
                    .googleMapId(placeId)
                    .name(name)
                    .address(address)
                    .latitude(latitude)
                    .longitude(longitude)
                    .category(category)
                    .build();
        }

    }

}
