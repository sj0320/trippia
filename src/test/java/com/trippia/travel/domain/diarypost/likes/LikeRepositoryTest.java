package com.trippia.travel.domain.diarypost.likes;

import com.trippia.travel.TestConfig;
import com.trippia.travel.controller.dto.diary.request.DiarySaveRequest;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.diarypost.diary.Diary;
import com.trippia.travel.domain.diarypost.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(TestConfig.class)
@DataJpaTest
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private DiaryRepository diaryRepository;


    @DisplayName("사용자가 해당 여행일지에 좋아요를 남기지 않았다면 false를 반환한다.")
    @Test
    void existsByUserAndDiary_false() {
        // given
        City seoul = setupCountriesAndCities();
        User user = createUser();
        DiarySaveRequest request = createDiarySaveRequest(seoul.getId());
        Diary diary = request.toEntity(user, seoul, "thumbnail");
        diaryRepository.save(diary);

        // when
        boolean result = likeRepository.existsByUserAndDiary(user, diary);
        // then
        assertThat(result).isFalse();
    }

    @DisplayName("사용자가 여행일지에 좋아요를 남겼다면 true를 반환한다.")
    @Test
    void existsByUserAndDiary_true() {
        // given
        City seoul = setupCountriesAndCities();
        User user = createUser();
        DiarySaveRequest request = createDiarySaveRequest(seoul.getId());
        Diary diary = request.toEntity(user, seoul, "thumbnail");
        diaryRepository.save(diary);
        Likes like = Likes.builder()
                .diary(diary)
                .user(user)
                .build();
        likeRepository.save(like);
        // when
        boolean result = likeRepository.existsByUserAndDiary(user, diary);
        // then
        assertThat(result).isTrue();
    }

    @DisplayName("사용자가 여행일지에 누른 좋아요를 취소한다.")
    @Test
    void deleteByUserAndDiary() {
        // given
        City seoul = setupCountriesAndCities();
        User user = createUser();
        DiarySaveRequest request = createDiarySaveRequest(seoul.getId());
        Diary diary = request.toEntity(user, seoul, "thumbnail");
        diaryRepository.save(diary);
        Likes like = Likes.builder()
                .diary(diary)
                .user(user)
                .build();
        likeRepository.save(like);
        // when
        likeRepository.deleteByUserAndDiary(user,diary);
        // then
        boolean result = likeRepository.existsByUserAndDiary(user, diary);
        assertThat(result).isFalse();
    }


    private User createUser() {
        User user = User.builder()
                .email("email")
                .password("password")
                .nickname("test")
                .loginType(LoginType.LOCAL)
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    private DiarySaveRequest createDiarySaveRequest(Long cityId) {
        return DiarySaveRequest.builder()
                .cityId(cityId)
                .title("test")
                .content("test content")
                .startDate(LocalDate.of(2099, 1, 1))
                .endDate(LocalDate.of(2099, 1, 10))
                .companion("친구")
                .rating(5)
                .totalBudget(1000000)
                .themeIds(List.of(1L))
                .build();
    }

    private City setupCountriesAndCities() {
        Country korea = Country.builder().name("한국").build();
        Country japan = Country.builder().name("일본").build();
        Country china = Country.builder().name("중국").build();
        countryRepository.saveAll(List.of(korea, japan, china));

        City city1 = City.builder()
                .name("서울")
                .country(korea)
                .cityType(KOREA)
                .build();

        City city2 = City.builder()
                .name("부산")
                .country(japan)
                .cityType(KOREA)
                .build();

        City city3 = City.builder()
                .name("도쿄")
                .country(china)
                .cityType(JAPAN)
                .build();

        City city4 = City.builder()
                .name("오사카")
                .country(korea)
                .cityType(JAPAN)
                .build();

        City city5 = City.builder()
                .name("상하이")
                .country(japan)
                .cityType(CHINA)
                .build();

        City city6 = City.builder()
                .name("베이징")
                .country(china)
                .cityType(CHINA)
                .build();

        cityRepository.saveAll(List.of(city1, city2, city3, city4, city5, city6));
        return city1;
    }
}