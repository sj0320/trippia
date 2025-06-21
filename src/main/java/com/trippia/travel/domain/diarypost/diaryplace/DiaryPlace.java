package com.trippia.travel.domain.diarypost.diaryplace;

import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.diarypost.diary.Diary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DiaryPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diary_place_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Builder
    private DiaryPlace(Place place, Diary diary) {
        this.place = place;
        this.diary = diary;
    }
}
