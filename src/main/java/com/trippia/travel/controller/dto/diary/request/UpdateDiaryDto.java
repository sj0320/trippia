package com.trippia.travel.controller.dto.diary.request;

import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateDiaryDto {

    private String title;
    private String content;
    private String thumbnail;
    private LocalDate startDate;
    private LocalDate endDate;
    private TravelCompanion companion;
    private Integer rating;
    private Integer totalBudget;
    private City city;

    @Builder
    private UpdateDiaryDto(String title, String content, String thumbnail, LocalDate startDate, LocalDate endDate,
                          TravelCompanion companion, Integer rating, Integer totalBudget, City city) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.companion = companion;
        this.rating = rating;
        this.totalBudget = totalBudget;
        this.city = city;
    }
}
