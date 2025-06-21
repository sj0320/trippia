package com.trippia.travel.controller.dto.scheduleitem.requset;

import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ScheduleItemMetaRequest {
    @Min(value = 0, message = "예상 지출은 0원 이상이어야 합니다.")
    private Integer expectedCost;
    private LocalTime executionTime;
}
