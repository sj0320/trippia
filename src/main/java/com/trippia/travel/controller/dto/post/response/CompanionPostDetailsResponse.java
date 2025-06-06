package com.trippia.travel.controller.dto.post.response;

import com.trippia.travel.controller.dto.postcomment.response.CompanionPostCommentResponse;
import com.trippia.travel.domain.companionpost.post.CompanionPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class CompanionPostDetailsResponse {

    private Long id;
    private String authorEmail;
    private String authorNickname;
    private String authorProfile;
    private String cityName;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String genderRestriction;  // ALL, MALE, FEMALE
    private int recruitmentCount;
    private String content;
    private String thumbnailUrl;
    private String createdAt;

    private List<CompanionPostCommentResponse> comments;

    @Builder
    private CompanionPostDetailsResponse(Long id, String authorEmail, String authorNickname, String authorProfile, String cityName, String title, LocalDate startDate, LocalDate endDate,
                                         String genderRestriction, String content, int recruitmentCount,
                                         String thumbnailUrl, String createdAt, List<CompanionPostCommentResponse> comments) {
        this.id = id;
        this.authorEmail = authorEmail;
        this.authorNickname = authorNickname;
        this.authorProfile = authorProfile;
        this.cityName = cityName;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genderRestriction = genderRestriction;
        this.content = content;
        this.recruitmentCount = recruitmentCount;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
        this.comments = comments;
    }

    public static CompanionPostDetailsResponse from(CompanionPost post) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return CompanionPostDetailsResponse.builder()
                .id(post.getId())
                .authorNickname(post.getUser().getNickname())
                .authorEmail(post.getUser().getEmail())
                .authorProfile(post.getUser().getProfileImageUrl())
                .cityName(post.getCity().getName())
                .title(post.getTitle())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .genderRestriction(post.getGenderRestriction().getDisplayName())
                .content(post.getContent())
                .recruitmentCount(post.getRecruitmentCount())
                .thumbnailUrl(post.getThumbnailUrl())
                .createdAt(post.getCreatedAt().format(formatter))
                .comments(CompanionPostCommentResponse.fromList(post.getComments()))
                .build();
    }
}
