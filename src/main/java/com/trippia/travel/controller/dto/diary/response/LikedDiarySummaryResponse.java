package com.trippia.travel.controller.dto.diary.response;

import com.trippia.travel.domain.post.diary.Diary;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikedDiarySummaryResponse {

    private Long diaryId;

    private String authorNickname;

    private String title;

    private int likeCount;

    private int viewCount;

    @Builder
    private LikedDiarySummaryResponse(Long diaryId, String authorNickname, String title, int likeCount, int viewCount) {
        this.diaryId = diaryId;
        this.authorNickname = authorNickname;
        this.title = title;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
    }

    public static LikedDiarySummaryResponse from(Diary diary) {
        return LikedDiarySummaryResponse.builder()
                .diaryId(diary.getId())
                .authorNickname(diary.getUser().getNickname())
                .title(diary.getTitle())
                .likeCount(diary.getLikeCount())
                .viewCount(diary.getViewCount())
                .build();
    }
}
