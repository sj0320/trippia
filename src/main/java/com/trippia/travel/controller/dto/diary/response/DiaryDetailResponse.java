package com.trippia.travel.controller.dto.diary.response;

import com.trippia.travel.controller.dto.comment.response.CommentResponse;
import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.theme.Theme;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class DiaryDetailResponse {

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

    @Builder
    private DiaryDetailResponse(Long id, String title, String content, String authorEmail,
                               String authorNickname, String authorProfile, String cityName,
                               LocalDate startDate, LocalDate endDate, String companion, Integer rating,
                               Integer totalBudget, List<String> theme, int viewCount, int likeCount, List<CommentResponse> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorEmail = authorEmail;
        this.authorNickname = authorNickname;
        this.authorProfile = authorProfile;
        this.cityName = cityName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.companion = companion;
        this.rating = rating;
        this.totalBudget = totalBudget;
        this.theme = theme;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.comments = comments;
    }

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
