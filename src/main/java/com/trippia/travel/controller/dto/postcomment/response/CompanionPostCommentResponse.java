package com.trippia.travel.controller.dto.postcomment.response;

import com.trippia.travel.domain.companionpost.comment.CompanionPostComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CompanionPostCommentResponse {
    private Long id;
    private String authorNickname;
    private String authorProfile;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    private CompanionPostCommentResponse(Long id, String authorNickname, String authorProfile, String content, LocalDateTime createdAt) {
        this.id = id;
        this.authorNickname = authorNickname;
        this.authorProfile = authorProfile;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CompanionPostCommentResponse from(CompanionPostComment comment) {
        return CompanionPostCommentResponse.builder()
                .id(comment.getId())
                .authorNickname(comment.getUser().getNickname())
                .authorProfile(comment.getUser().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static List<CompanionPostCommentResponse> fromList(List<CompanionPostComment> comments) {
        return comments.stream()
                .map(CompanionPostCommentResponse::from)
                .toList();
    }
}
