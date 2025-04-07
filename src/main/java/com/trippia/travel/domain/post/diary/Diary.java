package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    private String title;

    private String content;

    private String thumbnail;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TravelCompanion companion;

    private Integer rating;

    private Integer totalBudget;

    private int viewCount;

    private int likeCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Diary createDiary(SaveRequest request, User user, City city, String thumbnail) {
        return Diary.builder()
                .user(user)
                .city(city)
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnail(thumbnail)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .companion(TravelCompanion.fromString(request.getCompanion()))
                .rating(request.getRating())
                .totalBudget(request.getTotalBudget())
                .build();
    }

}
