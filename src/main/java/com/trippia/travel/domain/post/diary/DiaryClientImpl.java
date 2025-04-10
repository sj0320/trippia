package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.post.diarytheme.DiaryThemeRepository;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.theme.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DiaryClientImpl implements DiaryClient{

    private final DiaryRepository diaryRepository;
    private final DiaryThemeRepository diaryThemeRepository;
    private final ThemeRepository themeRepository;
    private final CityRepository cityRepository;

    @Override
    public Optional<City> findCityById(Long cityId) {
        return cityRepository.findById(cityId);
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
    public void deleteDiaryById(Long id){
        diaryRepository.deleteById(id);
    }

    @Override
    public void deleteDiaryThemeByDiaryId(Long diaryId){
        diaryThemeRepository.deleteByDiaryId(diaryId);
    }

    @Override
    public List<Diary> findAllDiary() {
        return diaryRepository.findAll();
    }

    @Override
    public Optional<Diary> findDiaryById(Long id){
        return diaryRepository.findById(id);
    }

    @Override
    public List<DiaryTheme> findDiaryThemesByDiaryId(Long diaryId){
        return diaryThemeRepository.findByDiaryId(diaryId);
    }


}
