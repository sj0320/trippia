package com.trippia.travel.domain.user.dto;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private final OAuth2UserInfo oauth2UserInfo;
    private final LoginType loginType;

    @Builder
    public OAuthAttributes(OAuth2UserInfo oAuth2UserInfo, LoginType loginType){
        this.oauth2UserInfo = oAuth2UserInfo;
        this.loginType = loginType;
    }

    public static OAuthAttributes of(Map<String, Object> attributes, LoginType loginType){
        if(loginType == LoginType.GOOGLE){
            return ofGoogle(attributes);
        }
        else if(loginType == LoginType.NAVER){
            return ofNaver(attributes);
        }
        else{
            return ofKakao(attributes);
        }
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .oAuth2UserInfo(new GoogleUserInfo(attributes))
                .loginType(LoginType.GOOGLE)
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .oAuth2UserInfo(new NaverUserInfo(attributes))
                .loginType(LoginType.NAVER)
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .oAuth2UserInfo(new KakaoUserInfo(attributes))
                .loginType(LoginType.KAKAO)
                .build();
    }

    public User toEntity(){
        return User.builder()
                .email(oauth2UserInfo.getEmail())
                .password(oauth2UserInfo.getProvider())
                .loginType(loginType)
                .role(Role.ROLE_GUEST)
                .build();
    }
}
