package com.trippia.travel.domain.diarypost.diarycomment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Long> {

    List<DiaryComment> findByDiaryId(Long diaryId);

}
