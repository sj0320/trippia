package com.trippia.travel.domain.diarypost.diary;

import com.trippia.travel.controller.dto.diary.request.CursorData;
import com.trippia.travel.controller.dto.city.response.CityCountResponse;
import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.diarypost.diarytheme.DiaryTheme;
import com.trippia.travel.domain.diarypost.diarytheme.DiaryThemeRepository;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.theme.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DiaryClientImpl implements DiaryClient {

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
    public void deleteDiaryById(Long id) {
        diaryRepository.deleteById(id);
    }

    @Override
    public void deleteDiaryThemeByDiaryId(Long diaryId) {
        diaryThemeRepository.deleteByDiaryId(diaryId);
    }

    @Override
    public Optional<Diary> findDiaryById(Long id) {
        return diaryRepository.findWithCommentsById(id);
    }

    @Override
    public List<DiaryTheme> findDiaryThemesByDiaryId(Long diaryId) {
        return diaryThemeRepository.findByDiaryId(diaryId);
    }

    @Override
    public Slice<Diary> searchDiariesWithConditions(DiarySearchCondition condition, CursorData cursorData, Pageable pageable) {
        return diaryRepository.searchDiariesWithConditions(condition, cursorData, pageable);
    }

    @Override
    public List<CityCountResponse> findTopDiaryCities(Pageable pageable) {
        return cityRepository.findTopDiaryCities(pageable);
    }

    @Override
    public List<Diary> findTopDiaries(Pageable pageable) {
        return diaryRepository.findTopDiaries(pageable);
    }

    @Override
    public Optional<Diary> findTopDiaryByCityIdOrderByLikeCountDesc(Long cityId) {
        return diaryRepository.findTopDiaryByCityIdOrderByLikeCountDesc(cityId);
    }

    @Override
    public List<Diary> findAllDiaryByUserId(Long userId) {
        return diaryRepository.findAllByUserId(userId);
    }

}
