package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.schedule.Schedule;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;


@Entity
@Getter
public class ScheduleItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_item_id")
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

    @Builder
    private ScheduleItem(Schedule schedule, ScheduleItemType itemType, Memo memo, Place place) {
        this.schedule = schedule;
        this.itemType = itemType;
        this.memo = memo;
        this.place = place;
    }

    public static ScheduleItem ofMemo(Schedule schedule, Memo memo){
        return ScheduleItem.builder()
                .schedule(schedule)
                .itemType(ScheduleItemType.MEMO)
                .memo(memo)
                .build();
    }

    public static ScheduleItem ofPlace(Schedule schedule, Place place){
        return ScheduleItem.builder()
                .schedule(schedule)
                .itemType(ScheduleItemType.PLACE)
                .place(place)
                .build();
    }
}
