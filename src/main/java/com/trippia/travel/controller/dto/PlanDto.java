package com.trippia.travel.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PlanDto {

    @Getter
    @Setter
    public static class PlanCreateRequest {
        private List<Long> cityIds;
        private String startDate;
        private String endDate;
    }
}
