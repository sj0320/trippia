package com.trippia.travel.domain.notification.dto;

import com.trippia.travel.domain.notification.NotificationType;
import com.trippia.travel.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipantAcceptedNotificationDto extends NotificationDto{

    private Long planId;
    private String accepterNickname;

    @Builder
    private ParticipantAcceptedNotificationDto(User user, Long planId, String accepterNickname) {
        super(user, NotificationType.PLAN);
        this.planId = planId;
        this.accepterNickname = accepterNickname;
    }

    @Override
    public String getContent() {
        return accepterNickname + " 님이 여행에 참가했습니다!";
    }

    @Override
    public String getRelatedUrl() {
        return "/travel/plan/" + planId;
    }
}
