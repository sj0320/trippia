package com.trippia.travel.domain.travel.plancity;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.travel.plan.Plan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanCity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Builder
    private PlanCity(Plan plan, City city) {
        this.plan = plan;
        this.city = city;
    }

    public void setPlan(Plan plan){
        this.plan = plan;
    }

}
