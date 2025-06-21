package com.trippia.travel.domain.companionpost.comment;

import com.trippia.travel.domain.companionpost.post.CompanionPost;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CompanionPostComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private CompanionPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private CompanionPostComment(Long id, User user, CompanionPost post, String content,LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.post = post;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CompanionPostComment createComment(User user, CompanionPost post, String content) {
        return CompanionPostComment.builder()
                .user(user)
                .post(post)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void assignPost(CompanionPost post){
        this.post = post;
    }
}
