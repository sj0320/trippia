package com.trippia.travel.domain.travel.planparticipant;

import com.trippia.travel.controller.dto.planparticipant.response.ReceivedPlanParticipantResponse;
import com.trippia.travel.controller.dto.planparticipant.response.RequestPlanParticipantResponse;

import java.util.List;

public interface PlanParticipantRepositoryCustom {

    List<ReceivedPlanParticipantResponse> findReceivedRequests(Long userId);

    List<RequestPlanParticipantResponse> findSentRequests(Long userId);
}
