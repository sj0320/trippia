package com.trippia.travel.domain.post.diary;


import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.user.User;

import java.util.List;

public interface DiaryClient {

    User findUserByEmail(String email);
    City findCityById(Long cityId);
    List<Theme> findThemesByIds(List<Long> themeIds);
    void saveDiary(Diary diary);
    void saveDiaryThemes(List<DiaryTheme> diaryThemes);
    List<Diary> findAllDiary();

}
