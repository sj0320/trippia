package com.trippia.travel.controller.dto.notification.response;

import com.trippia.travel.domain.notification.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private Long id;
    private String content;
    private String relatedUrl;
    private String type;
    private boolean isRead;
    private String timeAgo;

    @Builder
    private NotificationResponse(Long id, String content, String relatedUrl, String type, boolean isRead, String timeAgo) {
        this.id = id;
        this.content = content;
        this.relatedUrl = relatedUrl;
        this.type = type;
        this.isRead = isRead;
        this.timeAgo = timeAgo;
    }

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .relatedUrl(notification.getRelatedUrl())
                .type(notification.getType().name())
                .isRead(notification.isRead())
                .timeAgo(calculateTimeAgo(notification.getCreatedAt()))
                .build();
    }

    private static String calculateTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + "초 전";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분 전";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "시간 전";
        } else if (seconds < 2592000) {
            return (seconds / 86400) + "일 전";
        } else if (seconds < 31536000) {
            return (seconds / 2592000) + "달 전";
        } else {
            return (seconds / 31536000) + "년 전";
        }
    }
}
