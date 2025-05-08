package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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

        @Builder
        private ScheduleItemResponse(Long id, ScheduleItemType type, String content, String name,
                                     String address, double latitude, double longitude, Integer expectedCost) {
            this.id = id;
            this.type = type;
            this.content = content;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.expectedCost = expectedCost;
        }

        public static ScheduleItemResponse ofMemo(Memo memo) {
            return ScheduleItemResponse.builder()
                    .id(memo.getId())
                    .type(ScheduleItemType.MEMO)
                    .content(memo.getContent())
                    .expectedCost(memo.getExpectedCost())
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
                    .build();
        }
    }

    @Getter
    public static class ExpectedCostRequest {
        private Integer expectedCost;
    }
}
