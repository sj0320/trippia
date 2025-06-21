package com.trippia.travel.domain.notification.dto;

import com.trippia.travel.domain.notification.NotificationType;
import com.trippia.travel.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeNotificationDto extends NotificationDto {

    private Long diaryId;
    private String likerNickname;

    @Builder
    private LikeNotificationDto(User user, Long diaryId, String likerNickname) {
        super(user, NotificationType.DIARY);
        this.diaryId = diaryId;
        this.likerNickname = likerNickname;
    }

    @Override
    public String getContent() {
        return likerNickname + "님이 좋아요를 눌렀습니다.";
    }

    @Override
    public String getRelatedUrl() {
        return "/diary/" + diaryId;
    }
}