package com.trippia.travel.controller.dto.post.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchCondition {

    private String keyword;
    private String countryName;
    private String cityName;
    private String sort;


    @Builder
    private PostSearchCondition(String keyword, String countryName, String cityName, String sort) {
        this.keyword = keyword;
        this.countryName = countryName;
        this.cityName = cityName;
        this.sort = sort;
    }
}
