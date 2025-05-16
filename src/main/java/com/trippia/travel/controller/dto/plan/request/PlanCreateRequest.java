package com.trippia.travel.controller.dto.plan.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanCreateRequest {
    private List<Long> cityIds;
    private String startDate;
    private String endDate;
}
