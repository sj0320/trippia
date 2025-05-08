package com.trippia.travel.domain.travel.plan;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("SELECT p FROM Plan p " +
            "LEFT JOIN FETCH p.planCities " +
            "LEFT JOIN FETCH p.schedules " +
            "WHERE p.id = :planId")
    Optional<Plan> findWithPlanCitiesAndSchedulesById(Long planId);

    @EntityGraph(attributePaths = {"planCities", "schedules"})
    Optional<Plan> findById(Long planId);

}
