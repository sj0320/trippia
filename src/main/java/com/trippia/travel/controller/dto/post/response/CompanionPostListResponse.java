package com.trippia.travel.controller.dto.post.response;

import com.trippia.travel.domain.companionpost.post.CompanionPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CompanionPostListResponse {

    private Long id;
    private String authorNickname;
    private String authorProfile;
    private String title;
    private String thumbnail;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;


    @Builder
    private CompanionPostListResponse(Long id, String authorNickname, String title, String authorProfile,
                                     String thumbnail, int viewCount, int commentCount,LocalDateTime createdAt) {
        this.id = id;
        this.authorNickname = authorNickname;
        this.title = title;
        this.authorProfile = authorProfile;
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
    }

    public static List<CompanionPostListResponse> from(List<CompanionPost> posts) {
        return posts.stream()
                .map(post -> CompanionPostListResponse.builder()
                        .id(post.getId())
                        .authorNickname(post.getUser().getNickname())
                        .authorProfile(post.getUser().getProfileImageUrl())
                        .title(post.getTitle())
                        .thumbnail(post.getThumbnailUrl())
                        .viewCount(post.getViewCount())
                        .commentCount(post.getComments().size())
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
