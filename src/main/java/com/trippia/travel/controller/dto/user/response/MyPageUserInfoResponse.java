package com.trippia.travel.controller.dto.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageUserInfoResponse {

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    private String bio;

    private String travelerGrade;

    @Builder
    private MyPageUserInfoResponse(Long userId, String nickname, String profileImageUrl, String bio,
                                   String travelerGrade) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.travelerGrade = travelerGrade;
    }
}
