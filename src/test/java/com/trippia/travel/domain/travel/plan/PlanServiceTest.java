package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static com.trippia.travel.domain.common.CityType.KOREA;
import static com.trippia.travel.domain.travel.plan.PlanDto.PlanCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PlanServiceTest {

    @Autowired
    private PlanService planService;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;


    @AfterEach
    void tearDown() {
        planRepository.deleteAllInBatch();
        cityRepository.deleteAllInBatch();
        countryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("여행 일정을 생성할 수 있다.")
    @Test
    void createPlan() {
        // given
        String email = "email";
        User user = createUser(email);
        Country japan = createCountry("일본");
        Country korea = createCountry("한국");
        City tokyo = createCity("도쿄", japan, JAPAN);
        City seoul = createCity("서울", korea, KOREA);

        PlanCreateRequest planCreateRequest = new PlanCreateRequest();
        planCreateRequest.setCityIds(List.of(tokyo.getId(), seoul.getId()));
        planCreateRequest.setStartDate("2099-01-01");
        planCreateRequest.setEndDate("2099-02-01");

        // when
        planService.createPlan(email, planCreateRequest);

        // then
        List<Plan> plans = planRepository.findAll();
        assertThat(plans).hasSize(1);
        assertThat(plans.get(0).getTitle()).isEqualTo("도쿄, 서울 여행");
        assertThat(plans.get(0).getUser().getEmail()).isEqualTo(email);
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

}