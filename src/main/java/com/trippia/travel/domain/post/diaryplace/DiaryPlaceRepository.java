package com.trippia.travel.domain.post.diaryplace;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryPlaceRepository extends JpaRepository<DiaryPlace, Long> {

    List<DiaryPlace> findAllByPlace_PlaceId(String placeId);

    void deleteByDiary_Id(Long diaryId);

}
