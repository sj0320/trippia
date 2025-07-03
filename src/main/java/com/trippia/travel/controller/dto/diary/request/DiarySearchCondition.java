package com.trippia.travel.controller.dto.diary.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiarySearchCondition {

    private String keyword;

    private String themeName;
    private Long themeId;

    private Long countryId;
    private String countryName;

    private String cityName;
    private String sort;

    @Builder
    private DiarySearchCondition(String keyword, String themeName, Long themeId, Long countryId, String countryName, String cityName, String sort) {
        this.keyword = keyword;
        this.themeId = themeId;
        this.themeName = themeName;
        this.countryId = countryId;
        this.countryName = countryName;
        this.cityName = cityName;
        this.sort = sort;
    }
}
