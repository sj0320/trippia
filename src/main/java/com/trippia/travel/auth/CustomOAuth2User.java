package com.trippia.travel.auth;

import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.user.dto.OAuth2UserInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserInfo oAuth2UserInfo;

    private final Role role;

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role::name);
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getEmail();
    }

    public String getEmail() {
        return oAuth2UserInfo.getEmail();
    }
}
