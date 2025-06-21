package com.trippia.travel.domain.diarypost.diary;

import com.trippia.travel.TestConfig;
import com.trippia.travel.controller.dto.diary.request.CursorData;
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
import com.trippia.travel.domain.diarypost.diarycomment.DiaryComment;
import com.trippia.travel.domain.diarypost.diarycomment.DiaryCommentRepository;
import com.trippia.travel.domain.diarypost.diaryplace.DiaryPlaceRepository;
import com.trippia.travel.domain.diarypost.diarytheme.DiaryTheme;
import com.trippia.travel.domain.diarypost.diarytheme.DiaryThemeRepository;
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
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@DataJpaTest
@Import(TestConfig.class)
class DiaryRepositoryTest {

    @Autowired
    private DiaryCommentRepository diaryCommentRepository;

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

        Diary diary = createDiary(user, seoul, "서울 여행기", "test content", 0);
        DiaryComment diaryComment = diaryCommentRepository.save(DiaryComment.createComment(user, diary, "test content"));
        diary.addComment(diaryComment);

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

        createDiary(user, seoul, "My Trip to Seoul", "wonderful trip", 0);
        createDiary(user, seoul, "Just another day", "boring content", 0);

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

        Diary diary1 = createDiary(user, seoul, "My Trip to Seoul", "wonderful trip", 0);
        Diary diary2 = createDiary(user, seoul, "Just another day", "boring content", 0);

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

        createDiary(user, tokyo, "도쿄 여행기", "sushi!", 0);
        createDiary(user, osaka, "오사카 여행기", "takoyaki!", 0);

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

    @DisplayName("요청한 size 만큼 여행일지를 좋아요를 기준으로 내림차순 조회한다.")
    @Test
    void findTopDiaries() {
        // given
        User user = createUser("email", "nick");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        City osaka = createCity("오사카", japan, JAPAN);

        Diary diary1 = createDiary(user, tokyo, "도쿄 여행기", "content1", 10);
        Diary diary2 = createDiary(user, tokyo, "도쿄 여행기2", "content2", 50);
        Diary diary3 = createDiary(user, osaka, "오사카 여행기", "content3", 30);
        Diary diary4 = createDiary(user, osaka, "오사카 여행기2", "content4", 40);

        // when
        List<Diary> topDiaries = diaryRepository.findTopDiaries(PageRequest.of(0, 3));

        // then
        assertThat(topDiaries).hasSize(3)
                .extracting("title", "content", "likeCount")
                .containsExactly(
                        tuple(diary2.getTitle(), diary2.getContent(), 50),
                        tuple(diary4.getTitle(), diary4.getContent(), 40),
                        tuple(diary3.getTitle(), diary3.getContent(), 30)
                );

    }

    @DisplayName("가장 좋아요 많은 여행일지를 도시를 기준으로 조회한다.")
    @Test
    void findTopDiaryByCityIdOrderByLikeCountDesc() {
        // given
        User user = createUser("email", "nick");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        City osaka = createCity("오사카", japan, JAPAN);

        Diary diary1 = createDiary(user, tokyo, "도쿄 여행기", "content1", 10);
        Diary diary2 = createDiary(user, tokyo, "도쿄 여행기2", "content2", 50);
        Diary diary3 = createDiary(user, tokyo, "도쿄 여행기3", "content3", 30);
        Diary diary4 = createDiary(user, osaka, "오사카 여행기2", "content4", 100);

        // when
        Diary topDiary = diaryRepository.findTopDiaryByCityIdOrderByLikeCountDesc(tokyo.getId()).orElseThrow();

        // then
        assertThat(topDiary.getCity()).isEqualTo(tokyo);
        assertThat(topDiary.getLikeCount()).isEqualTo(50);
        assertThat(topDiary.getTitle()).isEqualTo(diary2.getTitle());

    }

    @DisplayName("사용자가 작성한 여행일지들을 조회한다.")
    @Test
    void findAllByUserId() {
        // given
        User user = createUser("email", "nick");

        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);

        Diary diary1 = createDiary(user, tokyo, "도쿄 여행기", "content1", 10);
        Diary diary2 = createDiary(user, tokyo, "도쿄 여행기2", "content2", 50);
        Diary diary3 = createDiary(user, tokyo, "도쿄 여행기3", "content3", 30);

        // when
        List<Diary> diaries = diaryRepository.findAllByUserId(user.getId());

        // then
        assertThat(diaries).hasSize(3)
                .extracting("title", "content", "likeCount")
                .containsExactlyInAnyOrder(
                        tuple("도쿄 여행기", "content1", 10),
                        tuple("도쿄 여행기2", "content2", 50),
                        tuple("도쿄 여행기3", "content3", 30)
                );

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

    private Diary createDiary(User user, City city, String title, String content, int likeCount) {
        Diary diary = Diary.builder()
                .user(user)
                .city(city)
                .title(title)
                .content(content)
                .likeCount(likeCount)
                .startDate(LocalDate.of(2099, 1, 1))
                .endDate(LocalDate.of(2099, 1, 10))
                .createdAt(LocalDateTime.now())
                .build();
        return diaryRepository.save(diary);
    }

}