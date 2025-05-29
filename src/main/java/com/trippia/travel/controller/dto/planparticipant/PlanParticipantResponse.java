package com.trippia.travel.controller.dto.planparticipant;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanParticipantResponse {

    private Long userId;

    private String nickname;

    private String role;

    @Builder
    private PlanParticipantResponse(Long userId, String nickname, String role) {
        this.userId = userId;
        this.nickname = nickname;
        this.role = role;
    }
}
