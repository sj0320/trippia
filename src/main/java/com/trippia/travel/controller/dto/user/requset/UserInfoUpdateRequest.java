package com.trippia.travel.controller.dto.user.requset;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoUpdateRequest {

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nickname;

    @Size(max = 50, message = "한줄소개는 50자 이하로 입력해주세요.")
    private String bio;

    @Builder
    private UserInfoUpdateRequest(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
    }
}
