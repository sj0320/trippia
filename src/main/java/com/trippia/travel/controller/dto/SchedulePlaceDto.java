package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import lombok.Builder;
import lombok.Data;

public class SchedulePlaceDto {

    @Data
    @Builder
    public static class SchedulePlaceSaveRequest {
        private Long scheduleId;

        private String placeId;

        private String name;

        private String address;

        private double latitude;

        private double longitude;

        private String category;

        public SchedulePlace toEntity(Schedule schedule){
            return SchedulePlace.builder()
                    .schedule(schedule)
                    .googleMapId(placeId)
                    .name(name)
                    .address(address)
                    .latitude(latitude)
                    .longitude(longitude)
                    .category(category)
                    .build();
        }

    }

}
