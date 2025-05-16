package com.trippia.travel.controller.dto.diary.response;

import com.trippia.travel.domain.post.diary.Diary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DiaryListResponse {

    private Long id;
    private String authorNickname;
    private String authorProfile;
    private String title;
    private String thumbnail;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdAt;

    @Builder
    private DiaryListResponse(Long id, String authorNickname, String authorProfile, String title, String thumbnail,
                             int viewCount, int likeCount, LocalDateTime createdAt) {
        this.id = id;
        this.authorNickname = authorNickname;
        this.authorProfile = authorProfile;
        this.title = title;
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }

    public static List<DiaryListResponse> from(List<Diary> diaries) {
        return diaries.stream()
                .map(diary -> DiaryListResponse.builder()
                        .id(diary.getId())
                        .authorNickname(diary.getUser().getNickname())
                        .authorProfile(diary.getUser().getProfileImageUrl())
                        .title(diary.getTitle())
                        .thumbnail(diary.getThumbnail())
                        .viewCount(diary.getViewCount())
                        .likeCount(diary.getLikeCount())
                        .createdAt(diary.getCreatedAt())
                        .build())
                .toList();
    }

}
