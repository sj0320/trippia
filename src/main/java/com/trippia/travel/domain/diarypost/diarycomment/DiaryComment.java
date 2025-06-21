package com.trippia.travel.domain.diarypost.diarycomment;

import com.trippia.travel.domain.diarypost.diary.Diary;
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
public class DiaryComment {

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

    public static DiaryComment createComment(User user, Diary diary, String content) {
        return DiaryComment.builder()
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
