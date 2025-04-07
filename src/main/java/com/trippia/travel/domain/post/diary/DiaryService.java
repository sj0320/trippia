package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.DiaryListResponse;
import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;
import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_AFTER_END_DATE;
import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_BEFORE_TODAY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DiaryService {

    private final DiaryClient diaryClient;
    private final FileService fileService;

    @Transactional
    public void saveDiary(String email, SaveRequest request, MultipartFile thumbnail){
        validateDate(request.getStartDate(), request.getEndDate());
        User user = diaryClient.findUserByEmail(email);
        City city = diaryClient.findCityById(request.getCityId());
        String thumbnailUrl = fileService.uploadFile(thumbnail).getUrl();

        Diary diary = Diary.createDiary(request, user, city, thumbnailUrl);

        diaryClient.saveDiary(diary);
        saveDiaryThemes(request.getThemeIds(), diary);
    }

    public List<DiaryListResponse> getDiaryList(){
        List<Diary> diaries = diaryClient.findAllDiary();
        return DiaryListResponse.from(diaries);
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