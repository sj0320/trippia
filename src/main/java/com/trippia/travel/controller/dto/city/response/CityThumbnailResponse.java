package com.trippia.travel.controller.dto.city.response;

import lombok.Getter;

@Getter
public class CityThumbnailResponse {

    private String cityName;

    private String imageUrl;

    public CityThumbnailResponse(String cityName, String imageUrl) {
        this.cityName = cityName;
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl(){
        return "/diary/list?sort=latest&cityName=" + cityName;
    }
}
