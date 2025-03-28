package com.trippia.travel.domain.post;

import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memo_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Diary diary;

    private String content;

    private LocalDateTime createdAt;

}
