package com.trippia.travel.domain.post.diary;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom{

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.comments WHERE d.id = :diaryId")
    Optional<Diary> findWithCommentsById(@Param("diaryId") Long diaryId);

    @Query("SELECT d FROM Diary d ORDER BY d.likeCount DESC") // 예: 조회수 기준
    List<Diary> findTopDiaries(Pageable pageable);

    Optional<Diary> findTopDiaryByCityIdOrderByLikeCountDesc(Long cityId);

    List<Diary> findAllByUserId(Long userId);
}
