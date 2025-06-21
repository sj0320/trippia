package com.trippia.travel.domain.notification.dto;

import com.trippia.travel.domain.notification.Notification;
import com.trippia.travel.domain.notification.NotificationType;
import com.trippia.travel.domain.user.User;
import lombok.Getter;

@Getter
public abstract class NotificationDto {

    protected User user;

    protected NotificationType type;

    protected NotificationDto(User user, NotificationType type) {
        this.user = user;
        this.type = type;
    }

    public abstract String getContent();

    public abstract String getRelatedUrl();

    public Notification toEntity() {
        return Notification.builder()
                .user(user)
                .content(getContent())
                .relatedUrl(getRelatedUrl())
                .type(type)
                .isRead(false)
                .build();
    }

}
