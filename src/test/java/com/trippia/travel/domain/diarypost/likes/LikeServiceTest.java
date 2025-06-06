package com.trippia.travel.domain.diarypost.likes;

import com.trippia.travel.controller.dto.diary.response.LikedDiarySummaryResponse;
import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.diarypost.diary.Diary;
import com.trippia.travel.domain.diarypost.diary.DiaryRepository;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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

    @DisplayName("사용자가 좋아요한 여행일지들을 조회한다.")
    @Test
    void getLikedDiariesByUser() {
        User user = createUser("email");
        User other = createUser("other");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        Diary diary1 = createDiary(other, tokyo, "title1", "content1");
        Diary diary2 = createDiary(other, tokyo, "title2", "content2");
        Diary diary3 = createDiary(other, tokyo, "title3", "content3");

        // when
        likeService.likeDiary(user.getEmail(), diary1.getId());
        likeService.likeDiary(user.getEmail(), diary2.getId());
        List<LikedDiarySummaryResponse> result = likeService.getLikedDiariesByUser(user.getEmail());

        // then
        assertThat(result).hasSize(2)
                .extracting("diaryId", "title", "authorNickname")
                .containsExactlyInAnyOrder(
                        tuple(diary1.getId(), diary1.getTitle(), other.getNickname()),
                        tuple(diary2.getId(), diary2.getTitle(), other.getNickname())
                );

    }

    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("password")
                .nickname("nickname" + email)
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
                .thumbnail(city + ".img")
                .startDate(LocalDate.of(2099, 1, 1))
                .endDate(LocalDate.of(2099, 1, 10))
                .createdAt(LocalDateTime.now())
                .build();
        return diaryRepository.save(diary);
    }

}