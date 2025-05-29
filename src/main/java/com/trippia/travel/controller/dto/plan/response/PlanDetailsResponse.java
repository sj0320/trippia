package com.trippia.travel.controller.dto.plan.response;

import com.trippia.travel.controller.dto.planparticipant.PlanParticipantResponse;
import com.trippia.travel.controller.dto.schedule.response.ScheduleDetailsResponse;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plancity.PlanCity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class PlanDetailsResponse {

    private Long planId;
    private String title;
    private String ownerEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> cityIds = new ArrayList<>();
    private List<ScheduleDetailsResponse> schedules = new ArrayList<>();
    private List<PlanParticipantResponse> participants = new ArrayList<>();

    @Builder
    private PlanDetailsResponse(Long planId, String title, String ownerEmail,
                                LocalDate startDate, LocalDate endDate, List<Long> cityIds,
                                List<ScheduleDetailsResponse> schedules, List<PlanParticipantResponse> participants) {
        this.planId = planId;
        this.title = title;
        this.ownerEmail = ownerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cityIds = cityIds;
        this.schedules = schedules;
        this.participants = participants;
    }

    public static PlanDetailsResponse of(Plan plan, List<PlanCity> planCities,
                                         List<ScheduleDetailsResponse> schedules,
                                         List<PlanParticipantResponse> participants) {
        return PlanDetailsResponse.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .ownerEmail(plan.getOwnerEmail())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .cityIds(planCities.stream()
                        .map(planCity -> planCity.getCity().getId())
                        .toList())
                .schedules(schedules)
                .participants(participants)
                .build();
    }

}
