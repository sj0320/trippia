package com.trippia.travel.domain.user.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();  // 카카오의 고유 ID
    }

    @Override
    public String getEmail() {
        return ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
    }

}
