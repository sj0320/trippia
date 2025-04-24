package com.trippia.travel.domain.post.comment;

import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    private String content;

    private LocalDateTime createdAt;

    public static Comment createComment(User user, Diary diary, String content) {
        return Comment.builder()
                .user(user)
                .diary(diary)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void assignDiary(Diary diary) {
        if (this.diary == null || !this.diary.equals(diary)) {
            this.diary = diary;
        }
    }

}
