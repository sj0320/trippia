package com.trippia.travel.domain.notification;

import com.trippia.travel.controller.dto.notification.response.NotificationResponse;
import com.trippia.travel.domain.notification.dto.NotificationDto;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public Boolean hasNewNotification(String email) {
        User user = getUser(email);
        return notificationRepository.existsByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void sendNotification(NotificationDto notificationDto){
        Notification notification = notificationDto.toEntity();
        notificationRepository.save(notification);
    }

    @Transactional
    public List<NotificationResponse> getAllNotifications(String email) {
        User user = getUser(email);
        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        notifications.forEach(Notification::markAsRead);

        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }


    private User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }
}
