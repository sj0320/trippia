package com.trippia.travel.domain.travel.schedule;

import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.exception.schedule.ScheduleException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private LocalDate date;

    public Schedule(Plan plan, LocalDate date) {
        this.plan = plan;
        this.date = date;
    }

    public void validateOwnerOf(String email){
        String authorEmail = plan.getUser().getEmail();
        if(!authorEmail.equals(email)){
            throw new ScheduleException("접근 권한이 없습니다.");
        }
    }


}
