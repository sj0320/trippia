package com.trippia.travel.domain.travel;

import com.trippia.travel.domain.location.place.Place;
import jakarta.persistence.*;

@Entity
public class SchedulePlace {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="schedule_item_id")
    private ScheduleItem scheduleItem;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

}
