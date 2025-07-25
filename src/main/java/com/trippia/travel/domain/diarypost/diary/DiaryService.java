package com.trippia.travel.domain.diarypost.diary;

import com.trippia.travel.controller.dto.city.response.CityThumbnailResponse;
import com.trippia.travel.controller.dto.diary.request.*;
import com.trippia.travel.controller.dto.diary.response.*;
import com.trippia.travel.controller.dto.place.response.PlaceSummaryResponse;
import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.diarypost.diaryplace.DiaryPlace;
import com.trippia.travel.domain.diarypost.diaryplace.DiaryPlaceRepository;
import com.trippia.travel.domain.diarypost.diarytheme.DiaryTheme;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.location.place.PlaceRepository;
import com.trippia.travel.domain.location.place.PlaceService;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.event.diary.model.DiaryDeletedEvent;
import com.trippia.travel.event.diary.model.DiaryUpdatedEvent;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.exception.ErrorMessageSource.DIARY_START_DATE_AFTER_END_DATE;
import static com.trippia.travel.exception.ErrorMessageSource.DIARY_START_DATE_AFTER_TODAY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DiaryService {

    private final DiaryClient diaryClient;
    private final UserRepository userRepository;
    private final DiaryPlaceRepository diaryPlaceRepository;
    private final PlaceRepository placeRepository;
    private final FileService fileService;
    private final PlaceService placeService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    private static final String VIEW_DIARY_REDIS_KEY = "view:diary:%s:%s";

    @Transactional
    public Long saveDiary(String email, DiarySaveRequest request, MultipartFile thumbnail) throws IOException {
        validateDate(request.getStartDate(), request.getEndDate());
        User user = getUser(email);
        City city = getCity(request.getCityId());
        String thumbnailUrl = fileService.uploadFile(thumbnail).getUrl();

        Diary diary = request.toEntity(user, city, thumbnailUrl);

        diaryClient.saveDiary(diary);
        saveDiaryThemes(request.getThemeIds(), diary);

        log.info("[여행일지 저장] userId={}, email={}, cityName={}, themeIds={}, budget={}",
                user.getId(), email, city.getName(), request.getThemeIds(), request.getTotalBudget());

        // 저장할 placeId가 Place 테이블에 이미 존재하면 저장하지 않음.
        List<String> placeIds = request.getPlaceIds();
        for (String placeId : placeIds) {
            PlaceSummaryResponse placeSummary = placeService.getPlaceSummary(placeId);
            Place place = placeSummary.toEntity();
            if (!placeRepository.existsById(placeId)) {
                placeRepository.save(place);
            }
            DiaryPlace diaryPlace = DiaryPlace.builder()
                    .diary(diary)
                    .place(place)
                    .build();
            diaryPlaceRepository.save(diaryPlace);
        }

        log.info("[다이어리 여행지 저장] diaryId={}, placeIds={}", diary.getId(), placeIds);

        return diary.getId();
    }

    @Transactional
    public void editDiary(String email, Long diaryId, DiaryUpdateRequest request, MultipartFile thumbnail) throws IOException {
        validateDate(request.getStartDate(), request.getEndDate());
        Diary diary = getDiary(diaryId);
        User user = getUser(email);
        user.validateAuthorOf(diary);
        City city = getCity(request.getCityId());

        String thumbnailUrl = getUpdatedThumbnailUrl(thumbnail, diary.getThumbnail());

        UpdateDiaryDto updateDiaryDto = getUpdateDiaryDto(request, thumbnailUrl, city);
        diary.update(updateDiaryDto);

        updateDiaryThemes(diary, request.getThemeIds());

        // 기존의 diary_place 삭제
        diaryPlaceRepository.deleteByDiary_Id(diaryId);

        // 저장할 placeId가 Place 테이블에 이미 존재하면 저장하지 않음.
        List<String> placeIds = request.getPlaceIds();
        for (String placeId : placeIds) {
            PlaceSummaryResponse placeSummary = placeService.getPlaceSummary(placeId);
            Place place = placeSummary.toEntity();
            if (!placeRepository.existsById(placeId)) {
                placeRepository.save(place);
            }
            DiaryPlace diaryPlace = DiaryPlace.builder()
                    .diary(diary)
                    .place(place)
                    .build();
            diaryPlaceRepository.save(diaryPlace);
        }
        eventPublisher.publishEvent(new DiaryUpdatedEvent(diaryId, diary.getTitle(), diary.getThumbnail()));
    }

    public Slice<DiaryListResponse> searchDiaryList(DiarySearchCondition condition, CursorData cursorData, Pageable pageable) {
        Slice<Diary> diaries = diaryClient.searchDiariesWithConditions(condition, cursorData, pageable);
        List<DiaryListResponse> content = DiaryListResponse.from(diaries.getContent());
        return new SliceImpl<>(content, pageable, diaries.hasNext());
    }

    public DiaryDetailResponse getDiaryDetails(Long diaryId) {
        Diary diary = getDiary(diaryId);

        List<DiaryTheme> diaryThemes = diaryClient.findDiaryThemesByDiaryId(diaryId);
        List<Theme> themes = diaryThemes.stream()
                .map(DiaryTheme::getTheme)
                .toList();
        return DiaryDetailResponse.from(diary, themes);
    }

    @Transactional
    public void addViewCount(Long diaryId, String ip, String userAgent) {
        String hash = DigestUtils.md5DigestAsHex((ip + userAgent).getBytes());
        String redisKey = String.format(VIEW_DIARY_REDIS_KEY, diaryId, hash);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            Diary diary = getDiary(diaryId);
            diary.addViewCount();
            log.info("[조회수 증가] diaryId={}, ip={}, userAgent={}", diaryId, ip, userAgent);
            redisTemplate.opsForValue().set(redisKey, "viewed", Duration.ofHours(24));
        }
    }

    public DiaryEditFormResponse getEditForm(String email, Long diaryId) {
        User user = getUser(email);
        Diary diary = getDiary(diaryId);
        user.validateAuthorOf(diary);
        List<DiaryTheme> diaryThemes = diaryClient.findDiaryThemesByDiaryId(diaryId);
        List<Theme> themes = diaryThemes.stream()
                .map(DiaryTheme::getTheme)
                .toList();
        return DiaryEditFormResponse.from(diary, themes);
    }

    @Transactional
    public void deleteDiary(String email, Long diaryId) {
        log.info("[여행일지 삭제 요청] email={}, diaryId={}", email, diaryId);
        Diary diary = getDiary(diaryId);
        User user = getUser(email);
        user.validateAuthorOf(diary);

        diaryClient.deleteDiaryThemeByDiaryId(diaryId);
        diaryClient.deleteDiaryById(diaryId);
        eventPublisher.publishEvent(new DiaryDeletedEvent(diaryId));
        log.info("[여행일지 삭제 완료] diaryId={}", diaryId);
    }

    public List<DiaryThumbnailResponse> getTopPopularDiaries(Pageable pageable) {
        List<Diary> diaries = diaryClient.findTopDiaries(pageable);
        return DiaryThumbnailResponse.from(diaries);
    }


    public List<CityThumbnailResponse> getTopCityThumbnails(Pageable pageable) {
        return diaryClient.findTopCityThumbnails(pageable);
    }


    public List<DiarySummaryResponse> getDiarySummariesByUser(String email) {
        User user = getUser(email);
        List<Diary> diaries = diaryClient.findAllDiaryByUserId(user.getId());

        return diaries.stream()
                .map(DiarySummaryResponse::from)
                .toList();
    }


    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }


    private void saveDiaryThemes(List<Long> themeIds, Diary diary) {
        List<Theme> themes = diaryClient.findThemesByIds(themeIds);
        List<DiaryTheme> diaryThemes = themes.stream()
                .map(theme -> DiaryTheme.createDiaryTheme(theme, diary))
                .toList();
        diaryClient.saveDiaryThemes(diaryThemes);
    }

    private void validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new DiaryException("startDate", DIARY_START_DATE_AFTER_END_DATE);
        }

        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new DiaryException("startDate", DIARY_START_DATE_AFTER_TODAY);
        }
    }

    private void updateDiaryThemes(Diary diary, List<Long> themeIds) {
        diaryClient.deleteDiaryThemeByDiaryId(diary.getId());
        saveDiaryThemes(themeIds, diary);
    }

    private Diary getDiary(Long diaryId) {
        return diaryClient.findDiaryById(diaryId)
                .orElseThrow(() -> new DiaryException("여행일지 데이터를 찾을 수 없습니다."));
    }

    private City getCity(Long cityId) {
        return diaryClient.findCityById(cityId)
                .orElseThrow(() -> new DiaryException("도시 정보를 찾을 수 없습니다."));
    }

    private String getUpdatedThumbnailUrl(MultipartFile newThumbnail, String existingThumbnail) {
        if (newThumbnail != null && !newThumbnail.isEmpty()) {
            return fileService.uploadFile(newThumbnail).getUrl();
        }
        return existingThumbnail;
    }

    private UpdateDiaryDto getUpdateDiaryDto(DiaryUpdateRequest request, String thumbnailUrl, City city) {
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