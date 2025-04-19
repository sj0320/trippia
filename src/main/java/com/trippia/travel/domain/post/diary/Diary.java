package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.comment.Comment;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;
import static com.trippia.travel.domain.post.diary.DiaryDto.UpdateDiaryDto;

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

    @ManyToOne
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

    @OneToMany(mappedBy = "diary", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void update(UpdateDiaryDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.thumbnail = dto.getThumbnail();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.companion = dto.getCompanion();
        this.rating = dto.getRating();
        this.totalBudget = dto.getTotalBudget();
        this.city = dto.getCity();
        this.updatedAt = LocalDateTime.now();
    }

    public int addLike(){
        return ++likeCount;
    }

    public int cancelLike(){
        return --likeCount;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
        comment.assignDiary(this);
    }


}
