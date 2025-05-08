package com.trippia.travel.domain.travel.scheduleitem.scheduleplace;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulePlace extends ScheduleItem {

    private String googleMapId;

    private String name;

    private String address;

    private double latitude;

    private double longitude;

    private String category;

    @Builder
    private SchedulePlace(Schedule schedule, String googleMapId, String name, String address, double latitude, double longitude, String category) {
        super(schedule, 0);
        this.googleMapId = googleMapId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }
}
