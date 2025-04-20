package com.trippia.travel.domain.travel.plan;

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
