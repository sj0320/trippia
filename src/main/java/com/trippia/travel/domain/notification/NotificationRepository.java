package com.trippia.travel.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Boolean existsByUserIdAndIsReadFalse(Long userId);

    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
