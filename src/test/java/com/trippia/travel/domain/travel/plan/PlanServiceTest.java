package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.controller.dto.plan.request.PlanCreateRequest;
import com.trippia.travel.controller.dto.plan.response.PlanDetailsResponse;
import com.trippia.travel.controller.dto.schedule.response.ScheduleDetailsResponse;
import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.travel.plancity.PlanCity;
import com.trippia.travel.domain.travel.plancity.PlanCityRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static com.trippia.travel.domain.common.CityType.KOREA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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

    @Autowired
    private PlanCityRepository planCityRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;


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

        List<PlanCity> planCities = planCityRepository.findAll();
        assertThat(planCities).hasSize(2);
    }

    @DisplayName("planId로 여행 계획 상세정보를 조회한다.")
    @Test
    void findPlan() {
        // given
        // User 생성
        User user = createUser("email");

        // Plan 생성
        Plan plan = Plan.createPlan(user, "title", LocalDate.now(), LocalDate.now().plusDays(1));

        // Schedule 생성
        Schedule schedule1 = new Schedule(plan, LocalDate.now());
        plan.addSchedule(schedule1);

        Schedule schedule2 = new Schedule(plan, LocalDate.now().plusDays(1));
        plan.addSchedule(schedule2);
        Plan savePlan = planRepository.save(plan);

        // ScheduleItem 생성
        Memo item1 = Memo.builder().schedule(schedule1).content("content1").sequence(1).build();
        SchedulePlace item2 = SchedulePlace.builder().schedule(schedule1).name("스타벅스 강남점").sequence(2).build();
        Memo item3 = Memo.builder().schedule(schedule2).content("content2").sequence(3).build();
        scheduleItemRepository.saveAll(List.of(item1, item2, item3));

        // when
        PlanDetailsResponse result = planService.findPlan(user.getEmail(), savePlan.getId());

        // then
        assertThat(result.getTitle()).isEqualTo(plan.getTitle());

        List<ScheduleDetailsResponse> schedules = result.getSchedules();
        assertThat(schedules).hasSize(2);
        assertThat(schedules).extracting("date")
                .containsExactlyInAnyOrder(LocalDate.now(), LocalDate.now().plusDays(1));

        // schedule1의 item 검증
        ScheduleDetailsResponse schedule1Response = schedules.stream()
                .filter(s -> s.getDate().isEqual(LocalDate.now()))
                .findFirst()
                .orElseThrow();
        List<ScheduleItemResponse> schedule1Items = schedule1Response.getScheduleItems();
        assertThat(schedule1Items).hasSize(2);
        assertThat(schedule1Items).extracting("type", "content", "name")
                .containsExactlyInAnyOrder(
                        tuple(ScheduleItemType.MEMO, "content1", null),
                        tuple(ScheduleItemType.PLACE, null, "스타벅스 강남점")
                );

        // schedule2의 item 검증
        ScheduleDetailsResponse schedule2Response = schedules.stream()
                .filter(s -> s.getDate().isEqual(LocalDate.now().plusDays(1)))
                .findFirst()
                .orElseThrow();
        List<ScheduleItemResponse> schedule2Items = schedule2Response.getScheduleItems();
        assertThat(schedule2Items).hasSize(1);
        assertThat(schedule2Items).extracting("type", "content", "name")
                .containsExactlyInAnyOrder(tuple(ScheduleItemType.MEMO, "content2", null));

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