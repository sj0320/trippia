package com.trippia.travel.domain.travel.plan;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    @EntityGraph(attributePaths = {"planCities", "schedules"})
    Optional<Plan> findById(Long planId);

    List<Plan> findByUserIdAndStartDateAfter(Long userId, LocalDate date);

    List<Plan> findByUserIdAndStartDateBefore(Long userId, LocalDate now);
}
