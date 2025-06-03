package com.trippia.travel.domain.notification;

import com.trippia.travel.controller.dto.notification.response.NotificationResponse;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.trippia.travel.domain.notification.NotificationType.DIARY;
import static com.trippia.travel.domain.notification.NotificationType.PLAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("읽지 않은 새로운 알림이 있다면 true를 반환한다.")
    @Test
    void hasNewNotification() {
        // given
        User user = createUser("email");
        createNotification(user, "content", PLAN, false);

        // when
        Boolean result = notificationService.hasNewNotification(user.getEmail());

        // then
        assertThat(result).isTrue();
    }


    @DisplayName("읽지 않은 새로운 알림이 없다면 false를 반환한다.")
    @Test
    void hasNewNotification_false() {
        // given
        User user = createUser("email");
        createNotification(user, "content", PLAN, true);

        // when
        Boolean result = notificationService.hasNewNotification(user.getEmail());

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("사용자에게 발신된 모든 알림을 조회하고 읽음 처리한다.")
    @Test
    void getAllNotifications() {
        // given
        User user = createUser("email2");
        Notification n1 = createNotification(user, "content1", PLAN, false);
        Notification n2 = createNotification(user, "content2", DIARY, true);

        // when
        List<NotificationResponse> result = notificationService.getAllNotifications(user.getEmail());

        // then
        assertThat(result).hasSize(2)
                .extracting("content", "relatedUrl", "type", "isRead")
                .containsExactlyInAnyOrder(
                        tuple(n1.getContent(), n1.getRelatedUrl(), n1.getType().name(), true),
                        tuple(n2.getContent(), n2.getRelatedUrl(), n2.getType().name(), true)
                );
    }


    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("password")
                .nickname("nick_" + email)
                .loginType(LoginType.LOCAL)
                .role(Role.ROLE_USER)
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