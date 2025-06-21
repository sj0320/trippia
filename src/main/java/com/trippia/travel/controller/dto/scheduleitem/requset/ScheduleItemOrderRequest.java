package com.trippia.travel.controller.dto.scheduleitem.requset;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleItemOrderRequest {
    @NotNull
    private Long scheduleId;

    private List<ScheduleItemOrder> orders;

    @Builder
    private ScheduleItemOrderRequest(Long scheduleId, List<ScheduleItemOrder> orders) {
        this.scheduleId = scheduleId;
        this.orders = orders;
    }

    @Getter
    public static class ScheduleItemOrder {
        private Long itemId;
        private Integer sequence;

        @Builder
        private ScheduleItemOrder(Long itemId, Integer sequence) {
            this.itemId = itemId;
            this.sequence = sequence;
        }
    }
}
