package com.trippia.travel.auth;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.domain.user.dto.OAuthAttributes;
import com.trippia.travel.exception.user.DifferentLoginTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("oAuth2User={}", oAuth2User.getAttributes());


        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        LoginType loginType = getLoginType(registrationId);

        // 로그인타입으로 객체 생성
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(attributes, loginType);

        User user = getOrCreateUser(oAuthAttributes);

        return new CustomOAuth2User(oAuthAttributes.getOauth2UserInfo(), user.getRole());

    }

    private User getOrCreateUser(OAuthAttributes oAuthAttributes) {
        String email = oAuthAttributes.getOauth2UserInfo().getEmail();
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    if (!existingUser.getLoginType().equals(oAuthAttributes.getLoginType())) {
                        OAuth2Error error = new OAuth2Error("different_login_type");

                        throw new OAuth2AuthenticationException(error,
                                new DifferentLoginTypeException(existingUser.getLoginType(), oAuthAttributes.getLoginType(), email));
                    }
                    existingUser.updateEmail(email);
                    return existingUser;
                })
                .orElseGet(() -> userRepository.save(oAuthAttributes.toEntity()));
    }

    private LoginType getLoginType(String registrationId) {
        return switch (registrationId) {
            case "naver" -> LoginType.NAVER;
            case "kakao" -> LoginType.KAKAO;
            case "google" -> LoginType.GOOGLE;
            default -> LoginType.LOCAL;
        };
    }

}
