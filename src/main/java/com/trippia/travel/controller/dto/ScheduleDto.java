package com.trippia.travel.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

public class ScheduleDto {

    @Data
    @AllArgsConstructor
    public static class ScheduleFormResponse {
        private Long id;
        private LocalDate date;
    }
}
