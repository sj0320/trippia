package com.trippia.travel.auth.loginhandler;

import com.trippia.travel.auth.CustomOAuth2User;
import com.trippia.travel.domain.common.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User= (CustomOAuth2User) authentication.getPrincipal();
        log.info("role={}",oAuth2User.getRole());

        if(oAuth2User.getRole() == Role.ROLE_GUEST){
            String email = oAuth2User.getEmail();
            String redirectUrl = "/users/sns-sign-up?email=" + email;

            response.sendRedirect(redirectUrl);
            return;
        }
        response.sendRedirect("/");
    }
}
