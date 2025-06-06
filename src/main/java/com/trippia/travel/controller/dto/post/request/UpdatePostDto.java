package com.trippia.travel.controller.dto.post.request;

import com.trippia.travel.domain.companionpost.post.Gender;
import com.trippia.travel.domain.location.city.City;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdatePostDto {

    private String title;

    private String content;

    private LocalDate startDate;

    private LocalDate endDate;

    private Gender genderRestriction;

    private Integer recruitmentCount;

    private City city;

    private String thumbnailUrl;

    @Builder
    private UpdatePostDto(String title, String content, Gender genderRestriction,
                         LocalDate startDate, LocalDate endDate, Integer recruitmentCount,
                         City city, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.genderRestriction = genderRestriction;
        this.endDate = endDate;
        this.startDate = startDate;
        this.recruitmentCount = recruitmentCount;
        this.city = city;
        this.thumbnailUrl = thumbnailUrl;
    }
}
