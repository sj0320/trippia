package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.exception.diary.DiaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;
import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_AFTER_END_DATE;
import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_BEFORE_TODAY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DiaryService {

    private final DiaryClient diaryClient;

    @Transactional
    public void saveDiary(String email, SaveRequest request){
        validateDate(request.getStartDate(), request.getEndDate());
        User user = diaryClient.findUserByEmail(email);
        City city = diaryClient.findCityById(request.getCityId());

        Diary diary = Diary.createDiary(request, user, city);
        diaryClient.saveDiary(diary);
        saveDiaryThemes(request.getThemeIds(), diary);
    }

    private void saveDiaryThemes(List<Long> themeIds, Diary diary) {
        List<Theme> themes = diaryClient.findThemesByIds(themeIds);
        List<DiaryTheme> diaryThemes = themes.stream()
                .map(theme -> DiaryTheme.createDiaryTheme(theme, diary))
                .toList();
        diaryClient.saveDiaryThemes(diaryThemes);
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(endDate)){
            throw new DiaryException("startDate", START_DATE_AFTER_END_DATE);
        }

        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new DiaryException("startDate", START_DATE_BEFORE_TODAY);
        }
    }
}