package com.trippia.travel.domain.notification.dto;

import com.trippia.travel.domain.notification.NotificationType;
import com.trippia.travel.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipantInvitedNotificationDto extends NotificationDto {

    private Long planId;
    private String inviterNickname;

    @Builder
    private ParticipantInvitedNotificationDto(User user, Long planId, String inviterNickname) {
        super(user, NotificationType.PLAN);
        this.planId = planId;
        this.inviterNickname = inviterNickname;
    }

    @Override
    public String getContent() {
        return inviterNickname + " 님이 여행에 초대했습니다!";
    }

    @Override
    public String getRelatedUrl() {
        return "/plan-participant";
    }
}
