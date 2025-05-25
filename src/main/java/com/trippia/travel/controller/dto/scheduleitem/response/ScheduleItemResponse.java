package com.trippia.travel.controller.dto.scheduleitem.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@ToString
public class ScheduleItemResponse {
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

    private Integer sequence;

    @Builder
    private ScheduleItemResponse(Long id, ScheduleItemType type, String content, String name,
                                 String address, double latitude, double longitude, Integer expectedCost,
                                 LocalTime executionTime, Integer sequence) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expectedCost = expectedCost;
        this.executionTime = executionTime;
        this.sequence = sequence;
    }

    public static ScheduleItemResponse ofMemo(Memo memo) {
        return ScheduleItemResponse.builder()
                .id(memo.getId())
                .type(ScheduleItemType.MEMO)
                .content(memo.getContent())
                .expectedCost(memo.getExpectedCost())
                .executionTime(memo.getExecutionTime())
                .sequence(memo.getSequence())
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
                .sequence(schedulePlace.getSequence())
                .build();
    }
}
