package com.trippia.travel.controller.dto.post.response;

import com.trippia.travel.domain.companionpost.post.CompanionPost;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CompanionPostSummaryResponse {

    private Long postId;

    private String title;

    private String thumbnail;

    private int viewCount;

    private int commentCount;

    @Builder
    private CompanionPostSummaryResponse(Long postId, String title, String thumbnail, int viewCount, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }

    public static CompanionPostSummaryResponse from(CompanionPost post) {
        return CompanionPostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .thumbnail(post.getThumbnailUrl())
                .viewCount(post.getViewCount())
                .commentCount(post.getComments().size())
                .build();
    }
}
