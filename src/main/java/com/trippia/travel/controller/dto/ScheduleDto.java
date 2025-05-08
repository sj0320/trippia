package com.trippia.travel.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.controller.dto.ScheduleItemDto.*;

public class ScheduleDto {

    @Data
    @AllArgsConstructor
    public static class ScheduleDetailsResponse {
        private Long id;
        private LocalDate date;
        private List<ScheduleItemResponse> scheduleItems;
    }
}
