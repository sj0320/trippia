package com.trippia.travel.controller.dto.diary.response;

import com.trippia.travel.domain.diarypost.diary.Diary;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryThumbnailResponse {

    private Long id;

    private String title;

    private String thumbnail;

    @Builder
    private DiaryThumbnailResponse(Long id, String title, String thumbnail) {
        this.id = id;
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public static DiaryThumbnailResponse from(Diary diary) {
        return mapToThumbnail(diary);
    }

    public static List<DiaryThumbnailResponse> from(List<Diary> diaries) {
        return diaries.stream()
                .map(DiaryThumbnailResponse::mapToThumbnail)
                .toList();
    }

    private static DiaryThumbnailResponse mapToThumbnail(Diary diary) {
        return DiaryThumbnailResponse.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .thumbnail(diary.getThumbnail())
                .build();
    }
}
