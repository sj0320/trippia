package com.trippia.travel.controller.dto.post.response;

import com.trippia.travel.domain.companionpost.post.CompanionPost;
import com.trippia.travel.domain.companionpost.post.Gender;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class CompanionPostEditFormResponse {

    private Long id;

    private Long cityId;

    private String cityName;

    private String title;

    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Gender genderRestriction;

    private Integer recruitmentCount;

    private String thumbnailUrl;

    @Builder
    private CompanionPostEditFormResponse(Long id, Long cityId, String cityName, String title, String content, LocalDate startDate,
                                          LocalDate endDate, Gender genderRestriction, Integer recruitmentCount, String thumbnailUrl) {
        this.id = id;
        this.cityId = cityId;
        this.cityName = cityName;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genderRestriction = genderRestriction;
        this.recruitmentCount = recruitmentCount;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static CompanionPostEditFormResponse from(CompanionPost post) {
        return CompanionPostEditFormResponse.builder()
                .id(post.getId())
                .cityName(post.getCity().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .genderRestriction(post.getGenderRestriction())
                .recruitmentCount(post.getRecruitmentCount())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }
}
