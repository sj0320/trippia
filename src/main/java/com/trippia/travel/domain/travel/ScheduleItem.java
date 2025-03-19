package com.trippia.travel.domain.travel;

import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.location.Place;
import jakarta.persistence.*;


@Entity
public class ScheduleItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    private ScheduleItemType itemType;

    @ManyToOne
    @JoinColumn(name = "memo_id")
    private Memo memo;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

}
