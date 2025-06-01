package com.trippia.travel.controller.dto.plan.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PlanUpdateRequest {

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder
    private PlanUpdateRequest(String title, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
