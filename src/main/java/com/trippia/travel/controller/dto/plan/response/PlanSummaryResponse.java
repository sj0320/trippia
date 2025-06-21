package com.trippia.travel.controller.dto.plan.response;

import com.trippia.travel.domain.travel.plan.Plan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PlanSummaryResponse {

    private Long planId;

    private String title;

    private String cityImage;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder
    private PlanSummaryResponse(Long planId, String title, String cityImage, LocalDate startDate, LocalDate endDate) {
        this.planId = planId;
        this.title = title;
        this.cityImage = cityImage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static PlanSummaryResponse from(Plan plan, String imageUrl){
        return PlanSummaryResponse.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .cityImage(imageUrl)
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .build();
    }
}
