package com.trippia.travel.controller.dto.diarycomment.response;

import com.trippia.travel.domain.diarypost.diarycomment.DiaryComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DiaryCommentResponse {
    private Long id;
    private String authorNickname;
    private String authorProfile;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    private DiaryCommentResponse(Long id, String authorNickname, String authorProfile, String content, LocalDateTime createdAt) {
        this.id = id;
        this.authorNickname = authorNickname;
        this.authorProfile = authorProfile;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static DiaryCommentResponse from(DiaryComment diaryComment){
        return DiaryCommentResponse.builder()
                .id(diaryComment.getId())
                .authorNickname(diaryComment.getUser().getNickname())
                .authorProfile(diaryComment.getUser().getProfileImageUrl())
                .content(diaryComment.getContent())
                .createdAt(diaryComment.getCreatedAt())
                .build();
    }


    public static List<DiaryCommentResponse> fromList(List<DiaryComment> diaryComments){
        return diaryComments.stream()
                .map(comment -> DiaryCommentResponse.builder()
                        .id(comment.getId())
                        .authorNickname(comment.getUser().getNickname())
                        .authorProfile(comment.getUser().getProfileImageUrl())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

}
