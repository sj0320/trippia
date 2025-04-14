package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.*;
import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_AFTER_END_DATE;
import static com.trippia.travel.exception.ErrorMessageSource.START_DATE_BEFORE_TODAY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DiaryService {

    private final DiaryClient diaryClient;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_DIARY_REDIS_KEY = "view:diary:%s:%s";

    @Transactional
    public Long saveDiary(String email, SaveRequest request, MultipartFile thumbnail) {
        validateDate(request.getStartDate(), request.getEndDate());
        User user = getUserByEmail(email);
        City city = getCity(request.getCityId());
        String thumbnailUrl = fileService.uploadFile(thumbnail).getUrl();

        Diary diary = Diary.createDiary(request, user, city, thumbnailUrl);

        diaryClient.saveDiary(diary);
        saveDiaryThemes(request.getThemeIds(), diary);

        return diary.getId();
    }

    @Transactional
    public void editDiary(String email, Long diaryId, UpdateRequest request, MultipartFile thumbnail) {
        validateDate(request.getStartDate(), request.getEndDate());
        Diary diary = getDiary(diaryId);
        User user = getUserByEmail(email);
        user.validateAuthorOf(diary);
        City city = getCity(request.getCityId());

        String thumbnailUrl = getUpdatedThumbnailUrl(thumbnail, diary.getThumbnail());

        UpdateDiaryDto updateDiaryDto = getUpdateDiaryDto(request, thumbnailUrl, city);
        diary.update(updateDiaryDto);

        updateDiaryThemes(diary, request.getThemeIds());
    }

    public List<DiaryListResponse> getDiaryList() {
        List<Diary> diaries = diaryClient.findAllDiary();
        return DiaryListResponse.from(diaries);
    }

    public DiaryDetailResponse getDiaryDetail(Long diaryId) {
        Diary diary = getDiary(diaryId);

        List<DiaryTheme> diaryThemes = diaryClient.findDiaryThemesByDiaryId(diaryId);
        List<Theme> themes = diaryThemes.stream()
                .map(DiaryTheme::getTheme)
                .toList();
        return DiaryDetailResponse.from(diary, themes);
    }

    public void addViewCount(Long diaryId, String ip, String userAgent) {
        String hash = DigestUtils.md5DigestAsHex((ip + userAgent).getBytes());
        String redisKey = String.format(VIEW_DIARY_REDIS_KEY, diaryId, hash);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            diaryClient.addDiaryViewCount(diaryId);
            redisTemplate.opsForValue().set(redisKey, "viewed", Duration.ofHours(24));
        }
    }

    public EditFormResponse getEditForm(String email, Long diaryId) {
        User user = getUserByEmail(email);
        Diary diary = getDiary(diaryId);
        user.validateAuthorOf(diary);
        List<DiaryTheme> diaryThemes = diaryClient.findDiaryThemesByDiaryId(diaryId);
        List<Theme> themes = diaryThemes.stream()
                .map(DiaryTheme::getTheme)
                .toList();
        return EditFormResponse.from(diary, themes);
    }

    @Transactional
    public void deleteDiary(String email, Long diaryId) {
        Diary diary = getDiary(diaryId);
        User user = getUserByEmail(email);
        user.validateAuthorOf(diary);

        diaryClient.deleteDiaryThemeByDiaryId(diaryId);
        diaryClient.deleteDiaryById(diaryId);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }


    private void saveDiaryThemes(List<Long> themeIds, Diary diary) {
        log.info("themeIds={}", themeIds);
        List<Theme> themes = diaryClient.findThemesByIds(themeIds);
        List<DiaryTheme> diaryThemes = themes.stream()
                .map(theme -> DiaryTheme.createDiaryTheme(theme, diary))
                .toList();
        diaryClient.saveDiaryThemes(diaryThemes);
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new DiaryException("startDate", START_DATE_AFTER_END_DATE);
        }

        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new DiaryException("startDate", START_DATE_BEFORE_TODAY);
        }
    }

    private void updateDiaryThemes(Diary diary, List<Long> themeIds) {
        diaryClient.deleteDiaryThemeByDiaryId(diary.getId());
        saveDiaryThemes(themeIds, diary);
    }

    private Diary getDiary(Long diaryId) {
        return diaryClient.findDiaryById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("여행일지 데이터를 찾을 수 없습니다."));
    }

    private City getCity(Long cityId) {
        return diaryClient.findCityById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("도시 정보를 찾을 수 없습니다."));
    }

    private String getUpdatedThumbnailUrl(MultipartFile newThumbnail, String existingThumbnail){
        if(newThumbnail !=null && !newThumbnail.isEmpty()){
            return fileService.uploadFile(newThumbnail).getUrl();
        }
        return existingThumbnail;
    }

    private UpdateDiaryDto getUpdateDiaryDto(UpdateRequest request, String thumbnailUrl, City city) {
        return UpdateDiaryDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnail(thumbnailUrl)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .companion(TravelCompanion.fromString(request.getCompanion()))
                .rating(request.getRating())
                .totalBudget(request.getTotalBudget())
                .city(city)
                .build();
    }
}