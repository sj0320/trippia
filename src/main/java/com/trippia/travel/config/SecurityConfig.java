package com.trippia.travel.config;

import com.trippia.travel.auth.CustomOAuth2UserService;
import com.trippia.travel.auth.loginhandler.LocalAuthenticationSuccessHandler;
import com.trippia.travel.auth.loginhandler.OAuth2LoginFailureHandler;
import com.trippia.travel.auth.loginhandler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final LocalAuthenticationSuccessHandler localAuthenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/diary/new").authenticated()
                        .requestMatchers("/companion-post/new").authenticated()
                        .requestMatchers("/", "/login","/images/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers( "/diary/list","/diary/list/data","/diary/*","/api/diary/*").permitAll()
                        .requestMatchers( "/companion-post/**","/api/companion-post/*").permitAll()
                        .requestMatchers( "/users/*","/users/password/new","/api/users/check-email",
                                "/api/email/**", "/api/users/reset-password").permitAll()
                        .requestMatchers( "/travel/places/*").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/test/**")
        );


        http
                .formLogin(auth -> auth
                        .loginPage("/users/login")
                        .loginProcessingUrl("/users/login")
                        .successHandler(localAuthenticationSuccessHandler)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll()
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다.");
                            response.sendRedirect("/users/login"); // 같은 페이지로 리다이렉트
                        })
                );

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                );

        http
                .logout(logout -> logout
                        .logoutUrl("/users/logout")  // 기본 값이 "/logout"
                        .logoutSuccessUrl("/") // 로그아웃 후 리다이렉트할 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 특정 쿠키 삭제
                );

        return http.build();

    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/images/**", "/css/**", "/js/**");
    }
}
