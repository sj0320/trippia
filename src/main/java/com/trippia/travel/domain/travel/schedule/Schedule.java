package com.trippia.travel.domain.travel.schedule;

import com.trippia.travel.domain.travel.plan.Plan;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private int dayNumber;      // 여행 몇 일차인지

    private LocalDate date;

}
