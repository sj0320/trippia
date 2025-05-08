package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.user.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.trippia.travel.controller.dto.CommentDto.CommentResponse;


public class DiaryDto {

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest {

        @NotNull(message = "다녀온 여행지를 입력해주세요")
        private Long cityId; // 여행 도시 ID

        private String cityName;

        @NotBlank(message = "제목을 입력해주세요")
        private String title; // 다이어리 제목

        @NotBlank(message = "내용을 입력해주세요")
        private String content; // 다이어리 내용

        @NotNull(message = "언제 여행을 다녀오셨나요?")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate; // 여행 시작 날짜

        @NotNull(message = "언제 여행을 다녀오셨나요?")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate; // 여행 종료 날짜

        @NotBlank(message = "누구랑 다녀오셨나요?")
        private String companion; // 여행 동반자

        @NotNull(message = "여행은 얼마나 만족하셨나요?")
        @Min(value = 1, message = "만족도는 최소 1점입니다.")
        @Max(value = 5, message = "만족도는 최대 5점입니다.")
        private Integer rating; // 여행 만족도 (1~5)

        @NotNull(message = "대략적인 경비만 입력해주세요.")
        private Integer totalBudget; // 총 여행 예산


        private List<Long> themeIds; // 선택된 Theme ID 리스트

        private String themeNames;

        public Diary toEntity(User user, City city, String thumbnailUrl) {
            return Diary.builder()
                    .user(user)
                    .city(city)
                    .title(this.title)
                    .content(this.content)
                    .thumbnail(thumbnailUrl)
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .companion(TravelCompanion.fromString(this.companion))
                    .rating(this.rating)
                    .totalBudget(this.totalBudget)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

    }

    @Getter
    @Setter
    public static class DiaryListViewModel{
        private List<DiaryListResponse> diaryList = new ArrayList<>();
        private DiarySearchCondition searchCondition;
        private boolean hasNext;
        private String sort;

        @Builder
        private DiaryListViewModel(List<DiaryListResponse> diaryList, DiarySearchCondition searchCondition, boolean hasNext, String sort) {
            this.diaryList = diaryList;
            this.searchCondition = searchCondition;
            this.hasNext = hasNext;
            this.sort = sort;
        }

        public static DiaryListViewModel createDiaryListViewModel(List<DiaryListResponse> diaryList,
                                                                  DiarySearchCondition searchCondition,
                                                                  boolean hasNext){
            return DiaryListViewModel.builder()
                    .diaryList(diaryList)
                    .searchCondition(searchCondition)
                    .hasNext(hasNext)
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class DiaryListResponse {
        private Long id;
        private String authorNickname;
        private String authorProfile;
        private String title;
        private String thumbnail;
        private int viewCount;
        private int likeCount;
        private LocalDateTime createdAt;

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

    @Getter
    @Setter
    @Builder
    public static class DiaryDetailResponse {
        private Long id;
        private String title;
        private String content;
        private String authorEmail;
        private String authorNickname;
        private String authorProfile;
        private String cityName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String companion;
        private Integer rating;
        private Integer totalBudget;
        private List<String> theme;
        private int viewCount;
        private int likeCount;
        private List<CommentResponse> comments;

        public static DiaryDetailResponse from(Diary diary, List<Theme> themes) {
            List<String> themeNames = themes.stream()
                    .map(Theme::getName)
                    .toList();

            return DiaryDetailResponse.builder()
                    .id(diary.getId())
                    .title(diary.getTitle())
                    .content(diary.getContent())
                    .authorEmail(diary.getUser().getEmail())
                    .authorNickname(diary.getUser().getNickname())
                    .authorProfile(diary.getUser().getProfileImageUrl())
                    .cityName(diary.getCity().getName())
                    .startDate(diary.getStartDate())
                    .endDate(diary.getEndDate())
                    .companion(String.valueOf(diary.getCompanion().getDescription()))
                    .rating(diary.getRating())
                    .totalBudget(diary.getTotalBudget())
                    .theme(themeNames)
                    .viewCount(diary.getViewCount())
                    .likeCount(diary.getLikeCount())
                    .comments(CommentResponse.fromList(diary.getComments()))
                    .build();
        }

    }

    @Getter @Setter
    @Builder
    public static class EditFormResponse {
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

        public static EditFormResponse from(Diary diary, List<Theme> themes) {
            List<String> themeNames = themes.stream()
                    .map(Theme::getName)
                    .toList();

            List<Long> themeIds = themes.stream()
                    .map(Theme::getId)
                    .toList(); // <-- 이거 추가!

            return EditFormResponse.builder()
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

    @Getter @Setter
    public static class UpdateRequest {
        @NotNull(message = "다녀온 여행지를 입력해주세요")
        private Long cityId; // 여행 도시 ID

        private String location;

        @NotBlank(message = "제목을 입력해주세요")
        private String title; // 다이어리 제목

        @NotBlank(message = "내용을 입력해주세요")
        private String content; // 다이어리 내용

        @NotNull(message = "언제 여행을 다녀오셨나요?")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate; // 여행 시작 날짜

        @NotNull(message = "언제 여행을 다녀오셨나요?")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
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

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UpdateDiaryDto {
        private String title;
        private String content;
        private String thumbnail;
        private LocalDate startDate;
        private LocalDate endDate;
        private TravelCompanion companion;
        private Integer rating;
        private Integer totalBudget;
        private City city;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiarySearchCondition {
        private String keyword;
        private String themeName;
        private String countryName;
        private String sort;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CursorData{
        private Long lastId;
        private LocalDateTime lastCreatedAt;
        private Integer lastLikeCount;
        private Integer lastViewCount;

        public static CursorData init(){
            return CursorData.builder()
                    .lastId(Long.MAX_VALUE)
                    .lastCreatedAt(LocalDateTime.now().plusYears(1))
                    .lastLikeCount(Integer.MAX_VALUE)
                    .lastViewCount(Integer.MAX_VALUE)
                    .build();
        }

        public static CursorData of(Long lastId, LocalDateTime lastCreatedAt, Integer lastLikeCount, Integer lastViewCount){
            return CursorData.builder()
                    .lastId(lastId)
                    .lastCreatedAt(lastCreatedAt)
                    .lastLikeCount(lastLikeCount)
                    .lastViewCount(lastViewCount)
                    .build();
        }
    }


}
