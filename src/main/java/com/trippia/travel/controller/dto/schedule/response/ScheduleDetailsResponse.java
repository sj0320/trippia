package com.trippia.travel.controller.dto.schedule.response;

import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ScheduleDetailsResponse {

    private Long id;
    private LocalDate date;
    private List<ScheduleItemResponse> scheduleItems;

    @Builder
    private ScheduleDetailsResponse(Long id, LocalDate date, List<ScheduleItemResponse> scheduleItems) {
        this.id = id;
        this.date = date;
        this.scheduleItems = scheduleItems;
    }
}
