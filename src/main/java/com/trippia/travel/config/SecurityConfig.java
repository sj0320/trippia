package com.trippia.travel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test2").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                );

        http
                .formLogin(auth -> auth
                        .loginPage("/users/login")
                        .loginProcessingUrl("/users/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll()
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다.");
                            response.sendRedirect("/users/login"); // 같은 페이지로 리다이렉트
                        })
                );

        http
                .logout(logout -> logout
                        .logoutUrl("/users/logout")  // 기본 값이 "/logout"
                        .logoutSuccessUrl("/") // 로그아웃 후 리다이렉트할 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 특정 쿠키 삭제
                );

        http
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();

    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/images/**", "/css/**", "/js/**");
    }
}
