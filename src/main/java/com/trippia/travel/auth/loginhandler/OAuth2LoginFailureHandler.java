package com.trippia.travel.auth.loginhandler;

import com.trippia.travel.exception.user.DifferentLoginTypeException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("OAuth2 로그인 실패!");

        if (exception instanceof OAuth2AuthenticationException authException) {
            Throwable cause = authException.getCause();
            if (cause instanceof DifferentLoginTypeException ex) {
                log.warn("기존 로그인 방식과 다름: {}", ex.getMessage());

                // 쿼리 스트링을 포함하여 리디렉트
                String redirectUrl = "/users/select-login-method?email=" + ex.getEmail() +
                        "&previousType=" + ex.getPreviousType() +
                        "&newType=" + ex.getNewType();

                response.sendRedirect(redirectUrl);
                return;
            }
        }


        // 기본 예외 처리
        response.sendRedirect("/login?error");
    }
}
