package com.trippia.travel.controller.dto.diary.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DiarySearchCondition {

    private String keyword;
    private String themeName;
    private String countryName;
    private String sort;

    @Builder
    private DiarySearchCondition(String keyword, String themeName, String countryName, String sort) {
        this.keyword = keyword;
        this.themeName = themeName;
        this.countryName = countryName;
        this.sort = sort;
    }
}
