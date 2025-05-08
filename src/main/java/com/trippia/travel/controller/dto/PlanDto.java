package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plancity.PlanCity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.trippia.travel.controller.dto.ScheduleDto.ScheduleDetailsResponse;

public class PlanDto {

    @Getter
    @Setter
    public static class PlanCreateRequest {
        private List<Long> cityIds;
        private String startDate;
        private String endDate;
    }

    @Getter
    public static class PlanDetailsResponse{
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

        public static PlanDetailsResponse of(Plan plan, List<PlanCity>planCities, List<ScheduleDetailsResponse> schedules){
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
}
