package com.trippia.travel.controller.dto.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageUserInfoResponse {

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    @Builder
    private MyPageUserInfoResponse(Long userId, String nickname, String profileImageUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
