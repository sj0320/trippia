package com.trippia.travel.domain.post.diary;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class DiaryDto {

    @Getter @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest {

        @NotNull(message = "다녀온 여행지를 입력해주세요")
        private Long cityId; // 여행 도시 ID

        private String location;

        @NotBlank(message = "제목을 입력해주세요")
        private String title; // 다이어리 제목

        @NotBlank(message = "내용을 입력해주세요")
        private String content; // 다이어리 내용

        @NotNull(message = "언제 여행을 다녀오셨나요?")
        private LocalDate startDate; // 여행 시작 날짜

        @NotNull(message = "언제 여행을 다녀오셨나요?")
        private LocalDate endDate; // 여행 종료 날짜

        @NotBlank(message = "누구랑 다녀오셨나요?")
        private String companion; // 여행 동반자

        @NotNull(message = "여행은 얼마나 만족하셨나요?")
        @Min(value = 1, message = "만족도는 최소 1점입니다.")
        @Max(value = 5, message = "만족도는 최대 5점입니다.")
        private Integer rating; // 여행 만족도 (1~5)

        @NotNull(message = "대략적인 경비만 입력해주세요.")
        private Integer totalBudget; // 총 여행 예산

        @NotNull(message = "여행 유형을 선택해주세요")
        private List<Long> themeIds; // 선택된 Theme ID 리스트

    }

    @Getter @Setter
    @Builder
    public static class DiaryListResponse{
        private Long id;
        private String authorNickname;
        private String authorProfile;
        private String title;
        private String thumbnail;
        private int viewCount;
        private int likeCount;

        public static List<DiaryListResponse> from(List<Diary> diaries) {
            return diaries.stream()
                    .map(diary -> DiaryListResponse.builder()
                            .authorNickname(diary.getUser().getNickname())
                            .authorProfile(diary.getUser().getProfileImageUrl())
                            .title(diary.getTitle())
                            .thumbnail(diary.getThumbnail())
                            .viewCount(diary.getViewCount())
                            .likeCount(diary.getLikeCount())
                            .build())
                    .collect(Collectors.toList());
        }
    }


}
