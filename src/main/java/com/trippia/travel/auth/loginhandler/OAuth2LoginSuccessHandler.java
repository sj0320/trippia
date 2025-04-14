package com.trippia.travel.auth.loginhandler;

import com.trippia.travel.auth.CustomOAuth2User;
import com.trippia.travel.domain.common.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User= (CustomOAuth2User) authentication.getPrincipal();
        log.info("role={}",oAuth2User.getRole());

        if(oAuth2User.getRole() == Role.ROLE_GUEST){
            String email = oAuth2User.getEmail();
            String socialType = oAuth2User.getOAuth2UserInfo().getProvider();
            String redirectUrl = "/users/sns-sign-up?email=" + email + "&socialType=" + socialType;

            response.sendRedirect(redirectUrl);
            return;
        }

        String redirectUriFromCookie = getCookie(request, "redirect_uri");
        if (redirectUriFromCookie != null && !redirectUriFromCookie.isBlank()) {
            clearCookie(response, "redirect_uri");
            redirectStrategy.sendRedirect(request, response, redirectUriFromCookie);
            return;
        }


        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request,response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, "/");
        }
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
