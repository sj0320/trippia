package com.trippia.travel.domain.post.diary;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Modifying
    @Query("UPDATE Diary d SET d.viewCount = d.viewCount + 1 WHERE d.id = :diaryId")
    void addViewCount(@Param("diaryId") Long diaryId);

}
