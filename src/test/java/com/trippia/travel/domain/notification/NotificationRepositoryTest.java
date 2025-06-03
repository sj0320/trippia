package com.trippia.travel.domain.notification;

import com.trippia.travel.TestConfig;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static com.trippia.travel.domain.notification.NotificationType.PLAN;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(TestConfig.class)
@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("읽지 않은 알림이 있다면 true를 반환한다.")
    @Test
    void existsByUserIdAndIsReadFalse() {
        // given
        User user = createUser();
        createNotification(user, "content", PLAN, false);

        // when
        Boolean result = notificationRepository.existsByUserIdAndIsReadFalse(user.getId());

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("읽지 않은 알림이 없다면 false를 반환한다.")
    @Test
    void existsByUserIdAndReadFalse_Is_read() {
        // given
        User user = createUser();
        createNotification(user, "content", PLAN, true);

        // when
        Boolean result = notificationRepository.existsByUserIdAndIsReadFalse(user.getId());

        // then
        assertThat(result).isFalse();
    }

    private User createUser() {
        User user = User.builder()
                .email("email")
                .password("pwd")
                .nickname("nick")
                .role(Role.ROLE_USER)
                .loginType(LoginType.LOCAL)
                .profileImageUrl("image.jpg")
                .build();
        return userRepository.save(user);
    }

    private Notification createNotification(User user, String content, NotificationType type, boolean isRead) {
        Notification notification = Notification.builder()
                .user(user)
                .content(content)
                .relatedUrl(content + "_url")
                .type(type)
                .isRead(isRead)
                .build();
        return notificationRepository.save(notification);
    }

}