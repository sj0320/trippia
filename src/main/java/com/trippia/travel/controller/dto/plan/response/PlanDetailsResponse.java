package com.trippia.travel.controller.dto.plan.response;

import com.trippia.travel.controller.dto.schedule.response.ScheduleDetailsResponse;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plancity.PlanCity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlanDetailsResponse {

    private Long planId;
    private String title;
    private List<Long> cityIds = new ArrayList<>();
    private List<ScheduleDetailsResponse> schedules = new ArrayList<>();


    @Builder
    private PlanDetailsResponse(Long planId, String title, List<Long> cityIds, List<ScheduleDetailsResponse> schedules) {
        this.planId = planId;
        this.title = title;
        this.cityIds = cityIds;
        this.schedules = schedules;
    }

    public static PlanDetailsResponse of(Plan plan, List<PlanCity> planCities, List<ScheduleDetailsResponse> schedules) {
        return PlanDetailsResponse.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .cityIds(planCities.stream()
                        .map(planCity -> planCity.getCity().getId())
                        .toList())
                .schedules(schedules)
                .build();
    }

}
