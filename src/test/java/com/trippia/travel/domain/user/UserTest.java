package com.trippia.travel.domain.user;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.post.diary.Diary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @DisplayName("유저 정보를 업데이트할 수 있다 (이메일, 닉네임, 프로필)")
    @Test
    void updateUserInfo() {
        // given
        User user = createUser("test@example.com", "test", Role.ROLE_USER);
        // when
        String newEmail = "new@example.com";
        String newNickname = "newNickname";
        String newProfile = "http://new.image.url";

        user.updateEmail(newEmail);
        user.updateNickname(newNickname);
        user.updateProfile(newProfile);

        // then
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getNickname()).isEqualTo(newNickname);
        assertThat(user.getProfileImageUrl()).isEqualTo(newProfile);
    }

    @DisplayName("사용자가 정식 유저로 전환된다.")
    @Test
    void completeRegistration_guestToUser() {
        // given
        User user = createUser("test@example.com", "test", Role.ROLE_GUEST);
        // when
        user.completeRegistration();
        // then
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @DisplayName("사용자가 여행일지 작성자가 아니라면 예외가 발생한다.")
    @Test
    void validateAuthorOf_fail() {
        // given
        User other = createUser("other@examle.com", "other", Role.ROLE_USER);
        User author = createUser("author@example.com", "author", Role.ROLE_USER);
        Diary diary = Diary.builder().user(author).build();
        // when & then
        assertThatThrownBy(() -> other.validateAuthorOf(diary))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

    @DisplayName("사용자가 여행일지 작성자라면 예외없이 통과한다.")
    @Test
    void validateAuthorOf_success() {
        // given
        User author = createUser("author@example.com", "author", Role.ROLE_USER);
        Diary diary = Diary.builder().user(author).build();
        // when & then
        assertThatCode(() -> author.validateAuthorOf(diary))
                .doesNotThrowAnyException();

    }

    private User createUser(String email, String nickname, Role role) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password("pwd")
                .loginType(LoginType.LOCAL)
                .role(role)
                .profileImageUrl("http://test.image.url")
                .build();
    }
}