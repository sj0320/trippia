package com.trippia.travel.domain.post.diary;

import com.trippia.travel.TestConfig;
import com.trippia.travel.controller.dto.CursorData;
import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.SortOption;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.location.place.PlaceRepository;
import com.trippia.travel.domain.post.comment.Comment;
import com.trippia.travel.domain.post.comment.CommentRepository;
import com.trippia.travel.domain.post.diaryplace.DiaryPlaceRepository;
import com.trippia.travel.domain.post.diarytheme.DiaryTheme;
import com.trippia.travel.domain.post.diarytheme.DiaryThemeRepository;
import com.trippia.travel.domain.theme.Theme;
import com.trippia.travel.domain.theme.ThemeRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static com.trippia.travel.domain.common.CityType.KOREA;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(TestConfig.class)
class DiaryRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private DiaryThemeRepository diaryThemeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private DiaryPlaceRepository diaryPlaceRepository;


    @DisplayName("여행일지를 조회할때 댓글도 함께 조회한다.")
    @Test
    void findWithCommentsById() {
        // given
        User user = createUser("email1", "nick1");
        City seoul = createCity("서울", createCountry("한국"), KOREA);

        Diary diary = createDiary(user, seoul, "서울 여행기", "test content");
        Comment comment = commentRepository.save(Comment.createComment(user, diary, "test content"));
        diary.addComment(comment);

        // when
        Diary foundDiary = diaryRepository.findWithCommentsById(diary.getId()).orElseThrow();

        // then
        assertThat(foundDiary.getComments()).hasSize(1)
                .extracting("content")
                .containsExactly("test content");
    }

    @DisplayName("키워드로 여행일지를 검색한다.")
    @Test
    void searchDiariesWithKeyword() {
        // given
        User user = createUser("email1", "nick1");
        Country korea = createCountry("한국");
        City seoul = createCity("서울", korea, KOREA);

        createDiary(user, seoul, "My Trip to Seoul", "wonderful trip");
        createDiary(user, seoul, "Just another day", "boring content");

        DiarySearchCondition condition = DiarySearchCondition.builder()
                .keyword("trip")
                .themeName(null)
                .countryName(null)
                .sort("latest")
                .build();

        Sort sort = SortOption.from(condition.getSort()).getSort();
        Pageable pageable = PageRequest.of(0, 3, sort);

        // when
        Slice<Diary> result = diaryRepository.searchDiariesWithConditions(condition, CursorData.init(), pageable);
        List<Diary> content = result.getContent();
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Trip");
    }

    @DisplayName("여행테마로 여행일지를 검색한다.")
    @Test
    void searchDiariesWithKTheme() {
        // given
        User user = createUser("email1", "nick1");
        Country korea = createCountry("한국");
        City seoul = createCity("서울", korea, KOREA);

        Diary diary1 = createDiary(user, seoul, "My Trip to Seoul", "wonderful trip");
        Diary diary2 = createDiary(user, seoul, "Just another day", "boring content");

        DiarySearchCondition condition = DiarySearchCondition.builder()
                .keyword(null)
                .themeName("맛집 탐방")
                .countryName(null)
                .sort("latest")
                .build();

        Sort sort = SortOption.from(condition.getSort()).getSort();
        Pageable pageable = PageRequest.of(0, 3, sort);

        // theme 저장
        Theme theme1 = Theme.builder().name("액티비티").build();
        Theme theme2 = Theme.builder().name("문화").build();
        Theme theme3 = Theme.builder().name("맛집 탐방").build();
        themeRepository.saveAll(List.of(theme1, theme2, theme3));

        // diaryTheme에 저장
        diaryThemeRepository.save(DiaryTheme.createDiaryTheme(theme1, diary1));
        diaryThemeRepository.save(DiaryTheme.createDiaryTheme(theme2, diary2));
        diaryThemeRepository.save(DiaryTheme.createDiaryTheme(theme3, diary2));

        // when
        Slice<Diary> result = diaryRepository.searchDiariesWithConditions(condition, CursorData.init(), pageable);

        // then

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Just another day");
    }

    @DisplayName("국가이름으로 여행일지를 검색한다.")
    @Test
    void searchDiariesWithCountry() {
        // given
        User user = createUser("email2", "nick2");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        City osaka = createCity("오사카", japan, JAPAN);

        createDiary(user, tokyo, "도쿄 여행기", "sushi!");
        createDiary(user, osaka, "오사카 여행기", "takoyaki!");

        DiarySearchCondition condition = DiarySearchCondition.builder()
                .countryName("일본")
                .themeName(null)
                .keyword(null)
                .sort("latest")
                .build();

        Sort sort = SortOption.from(condition.getSort()).getSort();
        Pageable pageable = PageRequest.of(0, 3, sort);
        // when
        Slice<Diary> result = diaryRepository.searchDiariesWithConditions(condition, CursorData.init(), pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .extracting(Diary::getCity)
                .extracting(City::getCountry)
                .extracting(Country::getName)
                .containsOnly("일본");
    }

    private User createUser(String email, String nickname) {
        User user = User.builder()
                .email(email)
                .password("password")
                .nickname(nickname)
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

    private Diary createDiary(User user, City city, String title, String content) {
        Diary diary = Diary.builder()
                .user(user)
                .city(city)
                .title(title)
                .content(content)
                .startDate(LocalDate.of(2099, 1, 1))
                .endDate(LocalDate.of(2099, 1, 10))
                .createdAt(LocalDateTime.now())
                .build();
        return diaryRepository.save(diary);
    }

}