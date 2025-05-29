package com.trippia.travel.domain.travel.planparticipant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanParticipantRepository extends JpaRepository<PlanParticipant, Long> {
    List<PlanParticipant> findByPlanId(Long planId);

    boolean existsByUserIdAndPlanId(Long userId, Long planId);
}
