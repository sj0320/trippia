package com.trippia.travel.domain.post.diary;

import com.trippia.travel.controller.dto.city.response.CityThumbnailResponse;
import com.trippia.travel.controller.dto.diary.request.DiarySaveRequest;
import com.trippia.travel.controller.dto.diary.response.DiaryListResponse;
import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.location.place.PlaceRepository;
import com.trippia.travel.domain.post.diaryplace.DiaryPlace;
import com.trippia.travel.domain.post.diaryplace.DiaryPlaceRepository;
import com.trippia.travel.domain.post.diarytheme.DiaryThemeRepository;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.theme.ThemeRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.file.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.*;
import static com.trippia.travel.domain.common.CityType.JAPAN;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private DiaryPlaceRepository diaryPlaceRepository;

    @Mock
    private FileService fileService;

    @Autowired
    private DiaryThemeRepository diaryThemeRepository;

    @Mock
    private MultipartFile thumbnail;

    @DisplayName("여행일지를 생성하고 저장한다.")
    @Test
    void saveDiary() throws IOException {
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

        String placeId1 = "ChIJlaUrm3mZfDURv0xPXhWjoM4";
        String placeId2 = "ChIJ7xbMEwCZfDUR_HdWf-TNSDU";
        List<String> placeIds = List.of(placeId1, placeId2);

        DiarySaveRequest request = createSaveRequest("test", "친구", tokyo.getId(), themeIds,
                LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10)
                , placeIds);
        String email = user.getEmail();
        MockMultipartFile thumbnail = getMockMultipartFile();

        // when
        diaryService.saveDiary(email, request, thumbnail);

        // then
        Diary savedDiary = diaryRepository.findAll().get(0);

        assertThat(savedDiary.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedDiary.getCompanion()).isEqualTo(TravelCompanion.FRIEND);
        assertThat(savedDiary.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(savedDiary.getEndDate()).isEqualTo(request.getEndDate());

        List<Place> savedPlaces = placeRepository.findAll();
        assertThat(savedPlaces).hasSize(2);

        List<DiaryPlace> savedDiaryPlaces = diaryPlaceRepository.findAll();
        assertThat(savedDiaryPlaces).hasSize(2);
    }

    @DisplayName("이미 저장된 장소는 Place 테이블에 저장하지 않고 DiaryPlace 테이블에만 저장한다.")
    @Test
    void saveDiary_shouldNotDuplicateExistingPlace() throws IOException {
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

        String placeId1 = "ChIJlaUrm3mZfDURv0xPXhWjoM4";
        String placeId2 = "ChIJ7xbMEwCZfDUR_HdWf-TNSDU";
        String placeId3 = "ChIJNYUq8XxfezUR9ZNIMTkhBBc";


        List<String> firstPlaceIds = List.of(placeId1, placeId2);
        List<String> secondPlaceIds = List.of(placeId1, placeId2, placeId3);

        DiarySaveRequest firstRequest = createSaveRequest("test", "친구", tokyo.getId(), themeIds,
                LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10)
                , firstPlaceIds);
        DiarySaveRequest secondRequest = createSaveRequest("test", "친구", tokyo.getId(), themeIds,
                LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10)
                , secondPlaceIds);
        String email = user.getEmail();
        MockMultipartFile thumbnail = getMockMultipartFile();

        // when
        diaryService.saveDiary(email, firstRequest, thumbnail);
        diaryService.saveDiary(email, secondRequest, thumbnail);

        // then
        List<Place> savedPlaces = placeRepository.findAll();
        assertThat(savedPlaces).hasSize(3);

        List<DiaryPlace> savedDiaryPlaces = diaryPlaceRepository.findAll();
        assertThat(savedDiaryPlaces).hasSize(5);

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

        DiarySaveRequest request = createSaveRequest("test", "연인", tokyo.getId(), themeIds,
                LocalDate.of(2024, 2, 10), // startDate
                LocalDate.of(2024, 2, 1), List.of()); // endDate (startDate보다 이전)

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
        DiarySaveRequest request = createSaveRequest("test", "연인", tokyo.getId(), themeIds,
                pastDate, endDate, List.of());
        String email = user.getEmail();
        MockMultipartFile thumbnail = getMockMultipartFile();
        // when & then
        assertThatThrownBy(() -> diaryService.saveDiary(email, request, thumbnail))
                .isInstanceOf(DiaryException.class);
    }

    @DisplayName("좋아요순으로 여행일지들을 원하는 개수만큼 조회한다.")
    @Test
    void getTopPopularDiaries() {
        // given
        User user = createUser("email");
        userRepository.save(user);
        Country korea = createCountry("한국");
        City seoul = createCity("서울", korea, KOREA);

        Diary diary1 = createDiary(user, seoul, "title1", 50);
        Diary diary2 = createDiary(user, seoul, "title1", 10);
        Diary diary3 = createDiary(user, seoul, "title1", 20);
        Diary diary4 = createDiary(user, seoul, "title1", 60);
        // when
        List<DiaryListResponse> diaries = diaryService.getTopPopularDiaries(PageRequest.of(0, 3));

        // then
        assertThat(diaries).hasSize(3)
                .extracting("id", "title", "likeCount")
                .containsExactly(
                        tuple(diary4.getId(), diary4.getTitle(), 60),
                        tuple(diary1.getId(), diary1.getTitle(), 50),
                        tuple(diary3.getId(), diary3.getTitle(), 20)
                );
    }

    @DisplayName("여행일지에서 가장 많이 사용된 도시들 중에서, 각 도시들의 썸네일을 가장 많이 좋아요를 받은 여행일지의 썸네일로 가져온다.")
    @Test
    void getTopCityThumbnails() {
        // given
        User user = createUser("email");
        userRepository.save(user);
        Country korea = createCountry("한국");
        Country japan = createCountry("일본");
        Country china = createCountry("중국");
        Country america = createCountry("미국");

        City seoul = createCity("서울", korea, KOREA);
        City busan = createCity("부산", korea, KOREA);
        City incheon = createCity("인천", korea, KOREA);
        City osaka = createCity("오사카", japan, JAPAN);
        City beijing = createCity("베이징", china, CHINA);
        City newYork = createCity("뉴욕", america, NORTH_AMERICA);

        // korea
        Diary kDiary1 = createDiary(user, seoul, "title1", 50);
        Diary kDiary2 = createDiary(user, seoul, "title2", 51);
        Diary kDiary3 = createDiary(user, seoul, "title3", 50);
        Diary kDiary4 = createDiary(user, incheon, "title4", 50);

        // japan
        Diary jDiary1 = createDiary(user, osaka, "title5", 0);
        Diary jDiary2 = createDiary(user, osaka, "title6", 0);
        Diary jDiary3 = createDiary(user, osaka, "title7", 1);
        Diary jDiary4 = createDiary(user, osaka, "title8", 0);

        // china
        Diary cDiary1 = createDiary(user, beijing, "title9", 1);
        Diary cDiary2 = createDiary(user, beijing, "title10", 2);

        // america
        Diary aDiary1 = createDiary(user, newYork, "title11", 100);


        // when
        List<CityThumbnailResponse> result = diaryService.getTopCityThumbnails(PageRequest.of(0, 3));

        // then
        assertThat(result).hasSize(3)
                .extracting("cityName", "imageUrl")
                .containsExactly(
                        tuple("오사카", jDiary3.getThumbnail()),
                        tuple("서울", kDiary2.getThumbnail()),
                        tuple("베이징", cDiary2.getThumbnail())
                );
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

    private Diary createDiary(User user, City city, String title, int likeCount) {
        Diary diary = Diary.builder()
                .user(user)
                .city(city)
                .title(title)
                .content("content")
                .likeCount(likeCount)
                .thumbnail(title + ".jpg")
                .startDate(LocalDate.of(2099, 1, 1))
                .endDate(LocalDate.of(2099, 1, 10))
                .createdAt(LocalDateTime.now())
                .build();
        return diaryRepository.save(diary);
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

    private DiarySaveRequest createSaveRequest(String title, String companion, Long cityId, List<Long> themeIds,
                                               LocalDate startDate, LocalDate endDate, List<String> placeIds) {
        return DiarySaveRequest.builder()
                .title(title)
                .companion(companion)
                .content("content")
                .rating(5)
                .totalBudget(1000000)
                .cityId(cityId)
                .themeIds(themeIds)
                .startDate(startDate)
                .endDate(endDate)
                .placeIds(placeIds)
                .build();


    }


}