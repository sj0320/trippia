package com.trippia.travel.controller.dto.planparticipant;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlanParticipantResponse {

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    private String role;

    @Builder
    private PlanParticipantResponse(Long userId, String nickname, String profileImageUrl, String role) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }
}
