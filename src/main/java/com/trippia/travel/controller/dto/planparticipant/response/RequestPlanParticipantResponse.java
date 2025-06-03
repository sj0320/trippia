package com.trippia.travel.controller.dto.planparticipant.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestPlanParticipantResponse {
    private Long planId;

    private String planTitle;

    private String nickname;

    private String profileImageUrl;


}
