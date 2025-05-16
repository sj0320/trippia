package com.trippia.travel.controller.dto.diary.response;

import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.theme.Theme;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
public class DiaryEditFormResponse {

    private Long diaryId;

    private Long cityId; // 여행 도시 ID

    private List<Long> themeIds;

    private String cityName;

    private String title; // 다이어리 제목

    private String content; // 다이어리 내용

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 여행 시작 날짜

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // 여행 종료 날짜

    private String companion; // 여행 동반자

    private Integer rating; // 여행 만족도 (1~5)

    private Integer totalBudget; // 총 여행 예산

    private List<String> themeNames; // 선택된 Theme ID 리스트

    private String thumbnailUrl;

    @Builder
    private DiaryEditFormResponse(Long diaryId, Long cityId, List<Long> themeIds, String cityName,
                                 String title, String content, LocalDate startDate, LocalDate endDate, String companion, Integer rating,
                                 Integer totalBudget, List<String> themeNames, String thumbnailUrl) {
        this.diaryId = diaryId;
        this.cityId = cityId;
        this.themeIds = themeIds;
        this.cityName = cityName;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.companion = companion;
        this.rating = rating;
        this.totalBudget = totalBudget;
        this.themeNames = themeNames;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static DiaryEditFormResponse from(Diary diary, List<Theme> themes) {
        List<String> themeNames = themes.stream()
                .map(Theme::getName)
                .toList();

        List<Long> themeIds = themes.stream()
                .map(Theme::getId)
                .toList(); // <-- 이거 추가!

        return DiaryEditFormResponse.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .cityName(diary.getCity().getName())
                .startDate(diary.getStartDate())
                .endDate(diary.getEndDate())
                .companion(String.valueOf(diary.getCompanion().getDescription()))
                .rating(diary.getRating())
                .totalBudget(diary.getTotalBudget())
                .themeNames(themeNames)
                .themeIds(themeIds) // <-- 이거 빠져 있었음!
                .thumbnailUrl(diary.getThumbnail())
                .build();
    }
}
