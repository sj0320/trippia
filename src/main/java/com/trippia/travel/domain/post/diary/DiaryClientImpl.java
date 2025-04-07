package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.post.diarytheme.DiaryThemeRepository;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.theme.ThemeRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DiaryClientImpl implements DiaryClient{

    private final DiaryRepository diaryRepository;
    private final DiaryThemeRepository diaryThemeRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;


    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @Override
    public City findCityById(Long cityId) {
        return cityRepository.findById(cityId).orElseThrow();
    }

    @Override
    public List<Theme> findThemesByIds(List<Long> themeIds) {
        return themeRepository.findAllById(themeIds);
    }

    @Override
    public void saveDiary(Diary diary) {
        diaryRepository.save(diary);
    }

    @Override
    public void saveDiaryThemes(List<DiaryTheme> diaryThemes) {
        diaryThemeRepository.saveAll(diaryThemes);
    }

    @Override
    public List<Diary> findAllDiary() {
        return diaryRepository.findAll();
    }


}
