package com.trippia.travel.domain.travel.plancity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanCityRepository extends JpaRepository<PlanCity, Long> {

    @EntityGraph(attributePaths = "plan")
    Optional<PlanCity> findByPlanId(Long planId);

    List<PlanCity> findAllByPlanId(Long planId);
}
