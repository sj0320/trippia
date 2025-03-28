package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.common.TripDuration;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Diary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private TripDuration tripDuration;

    private LocalDateTime createdAt;

}
