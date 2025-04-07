package com.trippia.travel.domain.post.diarytheme;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryThemeRepository extends JpaRepository<DiaryTheme, Long> {

    List<DiaryTheme> findByDiaryId(Long diaryId);

}
