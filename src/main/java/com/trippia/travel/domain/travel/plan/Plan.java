package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.domain.travel.plancity.PlanCity;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.exception.plan.PlanException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Plan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="plan_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<PlanCity>planCities = new ArrayList<>();

    @Builder
    private Plan(User user, String title, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Plan createPlan(User user, String title, LocalDate startDate, LocalDate endDate){
        return Plan.builder()
                .user(user)
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public void addPlanCity(PlanCity planCity){
        this.planCities.add(planCity);
        planCity.setPlan(this);
    }

    public void addSchedule(Schedule schedule){
        this.schedules.add(schedule);
        schedule.setPlan(this);
    }

    public void validateOwnerOf(String email){
        String ownerEmail = this.user.getEmail();
        if(!ownerEmail.equals(email)){
            throw new PlanException("접근 권한이 없습니다.");
        }
    }

}
