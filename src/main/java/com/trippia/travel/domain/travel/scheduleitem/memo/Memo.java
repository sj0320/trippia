package com.trippia.travel.domain.travel.scheduleitem.memo;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Memo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memo_id")
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    @Builder
    private Memo(LocalDateTime createdAt, String content) {
        this.createdAt = createdAt;
        this.content = content;
    }
}
