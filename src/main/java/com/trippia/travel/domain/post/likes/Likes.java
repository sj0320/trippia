package com.trippia.travel.domain.post.likes;

import com.trippia.travel.domain.post.diary.Diary;
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
public class Likes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="likes_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Diary diary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    @Builder
    private Likes(Diary diary, User user) {
        this.diary = diary;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
