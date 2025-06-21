package com.trippia.travel.domain.user;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.companionpost.post.CompanionPost;
import com.trippia.travel.domain.diarypost.diary.Diary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

import static com.trippia.travel.domain.user.TravelerGrade.*;

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

    @Enumerated(EnumType.STRING)
    private TravelerGrade grade = BEGINNER;

    private int exp = 0;

    private String bio;

    private LocalDateTime createdAt;

    @Builder
    private User(String email, String password, String nickname, LoginType loginType, Role role,
                 String profileImageUrl, TravelerGrade grade, int exp, String bio) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.loginType = loginType;
        this.role = (role != null) ? role : Role.ROLE_USER;
        this.profileImageUrl = profileImageUrl;
        this.grade = grade;
        this.exp = exp;
        this.bio = bio;
        this.createdAt = LocalDateTime.now();
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void completeRegistration() {
        if (this.role == Role.ROLE_GUEST) {
            this.role = Role.ROLE_USER;
        }
        this.grade = BEGINNER;
    }

    public void validateAuthorOf(Diary diary) {
        if (!this.equals(diary.getUser())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    public void validateAuthorOf(CompanionPost post) {
        if (!this.equals(post.getUser())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    public void updateProfileInfo(String nickname, String bio, String profileImageUrl) {
        this.nickname = nickname;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
