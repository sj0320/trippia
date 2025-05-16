package com.trippia.travel.controller.dto.place.response;

import com.trippia.travel.domain.location.place.Place;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaceSummaryResponse {
    private String placeId;
    private String name;
    private String address;

    @Builder
    private PlaceSummaryResponse(String placeId, String name, String address) {
        this.placeId =placeId;
        this.name = name;
        this.address = address;
    }

    public Place toEntity(){
        return Place.builder()
                .placeId(placeId)
                .name(name)
                .address(address)
                .build();
    }

}
