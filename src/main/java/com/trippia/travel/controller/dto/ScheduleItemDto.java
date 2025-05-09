package com.trippia.travel.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

public class ScheduleItemDto {

    @Getter
    @AllArgsConstructor
    public static class ScheduleItemIdResponse {
        private Long scheduleItemId;
    }

    @Getter
    public static class ScheduleItemResponse {
        private Long id;
        private ScheduleItemType type;
        private String content; // memo
        private String name;    // place
        private String address;  // place
        private double latitude;    // place
        private double longitude;   // place
        private Integer expectedCost;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime executionTime;

        @Builder
        private ScheduleItemResponse(Long id, ScheduleItemType type, String content, String name,
                                     String address, double latitude, double longitude, Integer expectedCost, LocalTime executionTime) {
            this.id = id;
            this.type = type;
            this.content = content;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.expectedCost = expectedCost;
            this.executionTime = executionTime;
        }

        public static ScheduleItemResponse ofMemo(Memo memo) {
            return ScheduleItemResponse.builder()
                    .id(memo.getId())
                    .type(ScheduleItemType.MEMO)
                    .content(memo.getContent())
                    .expectedCost(memo.getExpectedCost())
                    .executionTime(memo.getExecutionTime())
                    .build();
        }

        public static ScheduleItemResponse ofPlace(SchedulePlace schedulePlace) {
            return ScheduleItemResponse.builder()
                    .id(schedulePlace.getId())
                    .type(ScheduleItemType.PLACE)
                    .name(schedulePlace.getName())
                    .address(schedulePlace.getAddress())
                    .latitude(schedulePlace.getLatitude())
                    .longitude(schedulePlace.getLongitude())
                    .expectedCost(schedulePlace.getExpectedCost())
                    .executionTime(schedulePlace.getExecutionTime())
                    .build();
        }
    }

    @Getter
    @Setter
    public static class ScheduleItemMetaRequest{
        @Min(value = 0, message = "예상 지출은 0원 이상이어야 합니다.")
        private Integer expectedCost;
        private LocalTime executionTime;
    }

//    @Getter
//    public static class ExpectedCostRequest {
//        @Min(value = 0, message = "예상 지출은 0원 이상이어야 합니다.")
//        private Integer expectedCost;
//    }
}
