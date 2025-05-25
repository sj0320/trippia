package com.trippia.travel.controller.dto.scheduleplace.request;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SchedulePlaceSaveRequest {

    private Long scheduleId;

    private String placeId;

    private String name;

    private String address;

    private double latitude;

    private double longitude;

    private String category;

    @Builder
    private SchedulePlaceSaveRequest(Long scheduleId, String placeId, String name, String address,
                                    double latitude, double longitude, String category) {
        this.scheduleId = scheduleId;
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }

    public SchedulePlace toEntity(Schedule schedule, int sequence){
        return SchedulePlace.builder()
                .schedule(schedule)
                .googleMapId(placeId)
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .category(category)
                .sequence(sequence)
                .build();
    }


}
