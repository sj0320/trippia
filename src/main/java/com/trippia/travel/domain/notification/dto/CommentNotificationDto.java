package com.trippia.travel.domain.notification.dto;

import com.trippia.travel.domain.notification.NotificationType;
import com.trippia.travel.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentNotificationDto extends NotificationDto {

    private Long diaryId;
    private String commenterNickname;

    @Builder
    private CommentNotificationDto(User user, Long diaryId, String commenterNickname) {
        super(user, NotificationType.DIARY);
        this.diaryId = diaryId;
        this.commenterNickname = commenterNickname;
    }

    @Override
    public String getContent() {
        return commenterNickname + " 님이 댓글을 남겼습니다.";
    }

    @Override
    public String getRelatedUrl() {
        return "/diary/" + diaryId;
    }
}