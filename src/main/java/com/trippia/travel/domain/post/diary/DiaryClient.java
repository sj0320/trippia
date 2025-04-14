package com.trippia.travel.domain.post.diary;


import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.theme.Theme;

import java.util.List;
import java.util.Optional;

public interface DiaryClient {

    Optional<City> findCityById(Long cityId);

    List<Theme> findThemesByIds(List<Long> themeIds);

    void saveDiary(Diary diary);
    void saveDiaryThemes(List<DiaryTheme> diaryThemes);
    void deleteDiaryById(Long id);
    void deleteDiaryThemeByDiaryId(Long id);
    List<Diary> findAllDiary();
    Optional<Diary> findDiaryById(Long id);

    void addDiaryViewCount(Long id);

    List<DiaryTheme> findDiaryThemesByDiaryId(Long diaryId);

}
