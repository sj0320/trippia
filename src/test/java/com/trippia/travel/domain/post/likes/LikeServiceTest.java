package com.trippia.travel.domain.post.likes;

import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.post.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LikeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private LikeRepository likeRepository;

    @AfterEach
    void tearDown() {
        likeRepository.deleteAllInBatch();
        diaryRepository.deleteAllInBatch();
        cityRepository.deleteAllInBatch();
        countryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("여행일지에 좋아요를 남긴다.")
    @Test
    void likeDiary() {
        // given
        String email = "email";
        User user = createUser(email);
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        Diary diary = createDiary(user, tokyo, "title", "content");

        // when
        int likeCount = likeService.likeDiary(email, diary.getId());

        // then
        assertThat(likeCount).isEqualTo(1);
    }

    @DisplayName("이미 좋아요를 누른 여행일지에 다시 좋아요를 누르면 무시된다")
    @Test
    void likeDiaryIgnore() {
        // given
        String email = "email";
        User user = createUser(email);
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        Diary diary = createDiary(user, tokyo, "title", "content");

        // when: 첫 좋아요
        int firstLikeCount = likeService.likeDiary(email, diary.getId());
        // then: 좋아요 수 1 증가
        assertThat(firstLikeCount).isEqualTo(1);

        // when: 같은 사용자가 다시 좋아요
        int secondLikeCount = likeService.likeDiary(email, diary.getId());

        // then
        assertThat(secondLikeCount).isEqualTo(1);
    }

    @DisplayName("좋아요를 누른 여행일지를 취소한다.")
    @Test
    void unlikeDiary() {
        // given
        String email = "email";
        User user = createUser(email);
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        Diary diary = createDiary(user, tokyo, "title", "content");
        likeService.likeDiary(email, diary.getId());

        // when
        int likeCount = likeService.unlikeDiary(email, diary.getId());

        // then
        assertThat(likeCount).isEqualTo(0);

    }

    @DisplayName("좋아요를 누르지 않은 여행일지에 좋아요를 취소하면 무시된다.")
    @Test
    void unLikeDiaryIgnore() {
        // given
        String email = "email";
        User user = createUser(email);
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        Diary diary = createDiary(user, tokyo, "title", "content");

        // when
        int likeCount = likeService.unlikeDiary(email, diary.getId());

        // then
        assertThat(likeCount).isEqualTo(0);
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