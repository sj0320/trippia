package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.post.diarytheme.DiaryThemeRepository;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.theme.ThemeRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.file.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static com.trippia.travel.controller.dto.DiaryDto.SaveRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class DiaryServiceTest {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Mock
    private FileService fileService;

    @Autowired
    private DiaryThemeRepository diaryThemeRepository;

    @Mock
    private MultipartFile thumbnail;

    @AfterEach
    void tearDown() {
        diaryThemeRepository.deleteAllInBatch();
        diaryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        cityRepository.deleteAllInBatch();
        countryRepository.deleteAllInBatch();
        themeRepository.deleteAllInBatch();
    }

    @DisplayName("여행일지를 생성하고 저장한다.")
    @Test
    void saveDiary() {
        // given
        User user = createUser("email");
        userRepository.save(user);
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);

        // theme 저장
        Theme theme1 = Theme.builder().name("액티비티").build();
        Theme theme2 = Theme.builder().name("문화").build();
        List<Theme> themes = themeRepository.saveAll(List.of(theme1, theme2));
        List<Long> themeIds = themes.stream().map(Theme::getId).toList();

        SaveRequest request = createSaveRequest("test", "친구", tokyo.getId(), themeIds,
                LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10));
        String email = user.getEmail();

        // when
        MockMultipartFile thumbnail = getMockMultipartFile();

        diaryService.saveDiary(email, request, thumbnail);

        // then
        Diary savedDiary = diaryRepository.findAll().get(0);

        assertThat(savedDiary.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedDiary.getCompanion()).isEqualTo(TravelCompanion.FRIEND);
        assertThat(savedDiary.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(savedDiary.getEndDate()).isEqualTo(request.getEndDate());
    }

    private MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile(
                "test thumbnail",
                "thumbnail.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );
    }


    @DisplayName("여행 시작 날짜는 여행 종료 날짜보다 이후일 수 없다.")
    @Test
    void saveDiaryWhenStartDiaryOverEndDate() {
        // given
        User user = createUser("email");
        userRepository.save(user);
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        Theme theme1 = Theme.builder().name("액티비티").build();
        Theme theme2 = Theme.builder().name("문화").build();
        List<Theme> themes = themeRepository.saveAll(List.of(theme1, theme2));
        List<Long> themeIds = themes.stream().map(Theme::getId).toList();

        SaveRequest request = createSaveRequest("test", "연인", tokyo.getId(),themeIds,
                LocalDate.of(2024, 2, 10), // startDate
                LocalDate.of(2024, 2, 1)); // endDate (startDate보다 이전)

        String email = user.getEmail();
        MockMultipartFile thumbnail = getMockMultipartFile();
        // when & then
        assertThatThrownBy(() -> diaryService.saveDiary(email, request, thumbnail))
                .isInstanceOf(DiaryException.class);
    }

    @DisplayName("여행 시작 날짜가 오늘 이후라면 예외가 발생해야 한다.")
    @Test
    void saveDiaryWhenStartDateOverToday() {
        // given
        User user = createUser("email");
        userRepository.save(user);
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);

        Theme theme1 = Theme.builder().name("액티비티").build();
        Theme theme2 = Theme.builder().name("문화").build();
        List<Theme> themes = themeRepository.saveAll(List.of(theme1, theme2));
        List<Long> themeIds = themes.stream().map(Theme::getId).toList();

        LocalDate pastDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        SaveRequest request = createSaveRequest("test", "연인", tokyo.getId(),themeIds,
                pastDate,
                endDate);
        String email = user.getEmail();
        MockMultipartFile thumbnail = getMockMultipartFile();
        // when & then
        assertThatThrownBy(() -> diaryService.saveDiary(email, request, thumbnail))
                .isInstanceOf(DiaryException.class);
    }

    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("password")
                .nickname("nickname")
                .loginType(LoginType.LOCAL)
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    private Country createCountry(String name) {
        return countryRepository.save(Country.builder().name(name).build());
    }

    private City createCity(String name, Country country, CityType cityType) {
        return cityRepository.save(City.builder()
                .name(name)
                .country(country)
                .cityType(cityType)
                .build());
    }

    private SaveRequest createSaveRequest(String title, String companion, Long cityId, List<Long> themeIds,
                                          LocalDate startDate, LocalDate endDate) {
        return SaveRequest.builder()
                .title(title)
                .companion(companion)
                .content("content")
                .rating(5)
                .totalBudget(1000000)
                .cityId(cityId)
                .themeIds(themeIds)
                .startDate(startDate)
                .endDate(endDate)
                .build();


    }


}