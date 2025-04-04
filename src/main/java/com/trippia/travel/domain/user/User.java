package com.trippia.travel.domain.user;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String profileImageUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public User(String email, String password, String nickname, LoginType loginType, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.loginType = loginType;
        this.role = (role != null) ? role : Role.ROLE_USER;
    }

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void completeRegistration(){
        if (this.role == Role.ROLE_GUEST) {
            this.role = Role.ROLE_USER;
        }
    }
}
