package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.domain.travel.schedule.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ScheduleItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(columnDefinition = "int default 0", nullable = false)
    private Integer expectedCost;

    public ScheduleItem(Schedule schedule, Integer expectedCost) {
        this.schedule = schedule;
        this.expectedCost = expectedCost;
    }

    public void updateExpectedCost(Integer expectedCost) {
        this.expectedCost = expectedCost;
    }
}
