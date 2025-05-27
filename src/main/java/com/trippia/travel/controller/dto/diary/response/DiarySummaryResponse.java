package com.trippia.travel.controller.dto.diary.response;

import com.trippia.travel.domain.post.diary.Diary;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiarySummaryResponse {

    private Long diaryId;

    private String title;

    private String thumbnailImageUrl;

    private int likeCount;

    private int viewCount;

    @Builder
    private DiarySummaryResponse(Long diaryId, String title, String thumbnailImageUrl, int viewCount, int likeCount) {
        this.diaryId = diaryId;
        this.title = title;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }

    public static DiarySummaryResponse from(Diary diary){
        return DiarySummaryResponse.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .thumbnailImageUrl(diary.getThumbnail())
                .likeCount(diary.getLikeCount())
                .viewCount(diary.getViewCount())
                .build();
    }
}
