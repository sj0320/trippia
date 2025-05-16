package com.trippia.travel.controller.dto.comment.response;

import com.trippia.travel.domain.post.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponse {
    private Long id;
    private String authorNickname;
    private String authorProfile;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    private CommentResponse(Long id, String authorNickname, String authorProfile, String content, LocalDateTime createdAt) {
        this.id = id;
        this.authorNickname = authorNickname;
        this.authorProfile = authorProfile;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CommentResponse from(Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .authorNickname(comment.getUser().getNickname())
                .authorProfile(comment.getUser().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }


    public static List<CommentResponse> fromList(List<Comment> comments){
        return comments.stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .authorNickname(comment.getUser().getNickname())
                        .authorProfile(comment.getUser().getProfileImageUrl())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

}
