package com.trippia.travel.domain.companionpost.post;

import com.trippia.travel.controller.dto.post.request.UpdatePostDto;
import com.trippia.travel.domain.companionpost.comment.CompanionPostComment;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CompanionPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private String title;

    private String content;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<CompanionPostComment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Gender genderRestriction;

    private Integer recruitmentCount;

    private int viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private CompanionPost(User user, String title, String content, String thumbnailUrl, List<CompanionPostComment> comments, City city,
                         LocalDate startDate, LocalDate endDate, Gender genderRestriction, Integer recruitmentCount,
                          int viewCount,LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.comments = comments;
        this.city = city;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genderRestriction = genderRestriction;
        this.recruitmentCount = recruitmentCount;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addViewCount() {
        this.viewCount++;
    }

    public void update(UpdatePostDto updatePostDto) {
        this.title = updatePostDto.getTitle();
        this.content = updatePostDto.getContent();
        this.startDate = updatePostDto.getStartDate();
        this.endDate = updatePostDto.getEndDate();
        this.genderRestriction = updatePostDto.getGenderRestriction();
        this.recruitmentCount = updatePostDto.getRecruitmentCount();
        this.city = updatePostDto.getCity();
        this.thumbnailUrl = updatePostDto.getThumbnailUrl();
    }

    public void addComment(CompanionPostComment comment) {
        this.comments.add(comment);
        comment.assignPost(this);
    }
}
