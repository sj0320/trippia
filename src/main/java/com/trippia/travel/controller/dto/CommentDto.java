package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.post.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDto {

    @Getter @Setter
    public static class CommentSaveRequest{
        private String content;
    }

    @Getter @Setter
    @Builder
    public static class CommentResponse{
        private Long id;
        private String authorNickname;
        private String authorProfile;
        private String content;
        private LocalDateTime createdAt;

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

}
