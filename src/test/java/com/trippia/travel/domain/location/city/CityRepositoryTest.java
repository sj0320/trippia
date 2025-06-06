package com.trippia.travel.domain.location.city;

import com.trippia.travel.TestConfig;
import com.trippia.travel.controller.dto.city.response.CityCountResponse;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.diarypost.diary.Diary;
import com.trippia.travel.domain.diarypost.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @DisplayName("다이어리에서 인기 도시 Top N개를 조회한다")
    @Test
    void findTopDiaryCities() {
        // given
        // User 저장
        User user = userRepository.save(createUser());

        // Country 저장

        Country korea = createCountry("한국");

        // City 저장
        City seoul = createCity("서울", korea);
        City busan = createCity("부산", korea);
        City incheon = createCity("인천", korea);
        City yeosu = createCity("여수", korea);


        // Diary 저장
        createDiary(user,seoul);
        createDiary(user,seoul);
        createDiary(user,seoul);
        createDiary(user,seoul);

        createDiary(user,busan);

        createDiary(user,incheon);
        createDiary(user,incheon);

        createDiary(user,yeosu);
        createDiary(user,yeosu);
        createDiary(user,yeosu);

        // when
        List<CityCountResponse> topDiaryCities = cityRepository.findTopDiaryCities(PageRequest.of(0, 3));

        // then
        Assertions.assertThat(topDiaryCities).hasSize(3)
                .extracting("cityId", "count")
                .containsExactly(
                        tuple(seoul.getId(), 4L),
                        tuple(yeosu.getId(), 3L),
                        tuple(incheon.getId(), 2L)
                );
    }

    private Diary createDiary(User user,City city){
        return diaryRepository.save(Diary.builder().user(user).city(city).build());
    }

    private City createCity(String cityName, Country country) {
        return cityRepository.save(City.builder().name(cityName).country(country).build());
    }

    private Country createCountry(String name) {
        return countryRepository.save(Country.builder().name(name).build());
    }

    private User createUser() {
        return User.builder()
                .email("email")
                .password("pwd")
                .nickname("nick")
                .role(Role.ROLE_USER)
                .loginType(LoginType.LOCAL)
                .build();
    }

}