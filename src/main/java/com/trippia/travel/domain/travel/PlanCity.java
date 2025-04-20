package com.trippia.travel.domain.travel;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.travel.plan.Plan;
import jakarta.persistence.*;

@Entity
public class PlanCity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

}
