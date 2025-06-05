package com.trippia.travel.domain.travel.planparticipant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanParticipantRepository extends JpaRepository<PlanParticipant, Long>, PlanParticipantRepositoryCustom {
    List<PlanParticipant> findByPlanIdAndStatus(Long planId, InvitationStatus status);

    boolean existsByUserIdAndPlanIdAndStatus(Long userId, Long planId, InvitationStatus status);

    void deleteByPlanId(Long planId);

    Optional<PlanParticipant> findByUserIdAndPlanId(Long userId, Long planId);

    void deleteByUserIdAndPlanId(Long userId, Long planId);

    List<PlanParticipant> findAllByPlanIdAndStatus(Long planId, InvitationStatus invitationStatus);
}
