package com.trippia.travel.domain.travel;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Memo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memo_id")
    private Long id;

    private String content;

    private LocalDateTime createdAt;

}
