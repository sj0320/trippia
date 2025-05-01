package com.trippia.travel.domain.post.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom{

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.comments WHERE d.id = :diaryId")
    Optional<Diary> findWithCommentsById(@Param("diaryId") Long diaryId);

}
