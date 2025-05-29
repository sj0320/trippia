package com.trippia.travel.domain.travel.plan;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    @EntityGraph(attributePaths = {"planCities", "schedules"})
    Optional<Plan> findById(Long planId);


    @Query("select p.plan from PlanParticipant p where p.user.id = :userId and p.plan.startDate < :now")
    List<Plan> findPastPlansByUser(@Param("userId") Long userId, @Param("now") LocalDate now);

    @Query("select p.plan from PlanParticipant p where p.user.id = :userId and p.plan.startDate > :now")
    List<Plan> findUpcomingPlansByUser(@Param("userId") Long userId, @Param("now") LocalDate now);
}
