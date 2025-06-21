package com.trippia.travel.controller.dto.place.response;

import com.trippia.travel.controller.dto.diary.response.DiaryListResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PlaceDetailsResponse {

    private String placeId;
    private String name;
    private String imageUrl;
    private String address;
    private double rating;
    private int reviewCount;
    private String webSite;

    private List<DiaryListResponse> relatedDiaries;

    @Builder
    private PlaceDetailsResponse(String placeId, String name, String imageUrl, String address, double rating, int reviewCount, String webSite, List<DiaryListResponse> relatedDiaries) {
        this.placeId = placeId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.webSite = webSite;
        this.relatedDiaries = relatedDiaries;
    }
}
