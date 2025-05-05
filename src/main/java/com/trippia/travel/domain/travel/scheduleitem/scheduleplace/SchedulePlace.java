package com.trippia.travel.domain.travel.scheduleitem.scheduleplace;

import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class SchedulePlace {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="schedule_item_id")
    private ScheduleItem scheduleItem;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Builder
    private SchedulePlace(ScheduleItem scheduleItem, Place place) {
        this.scheduleItem = scheduleItem;
        this.place = place;
    }
}
