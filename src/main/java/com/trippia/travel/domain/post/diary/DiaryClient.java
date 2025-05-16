package com.trippia.travel.domain.post.diary;


import com.trippia.travel.controller.dto.CursorData;
import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.theme.Theme;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface DiaryClient {

    Optional<City> findCityById(Long cityId);

    List<Theme> findThemesByIds(List<Long> themeIds);

    void saveDiary(Diary diary);
    void saveDiaryThemes(List<DiaryTheme> diaryThemes);
    void deleteDiaryById(Long id);
    void deleteDiaryThemeByDiaryId(Long id);
    Optional<Diary> findDiaryById(Long id);

    List<DiaryTheme> findDiaryThemesByDiaryId(Long diaryId);

    Slice<Diary> searchDiariesWithConditions(DiarySearchCondition condition, CursorData cursorData , Pageable pageable);

}
