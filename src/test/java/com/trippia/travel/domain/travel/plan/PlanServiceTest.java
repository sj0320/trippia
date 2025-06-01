package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.controller.dto.plan.request.PlanCreateRequest;
import com.trippia.travel.controller.dto.plan.request.PlanUpdateRequest;
import com.trippia.travel.controller.dto.plan.response.PlanDetailsResponse;
import com.trippia.travel.controller.dto.plan.response.PlanSummaryResponse;
import com.trippia.travel.controller.dto.planparticipant.PlanParticipantResponse;
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
import com.trippia.travel.domain.travel.planparticipant.PlanParticipant;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipantRepository;
import com.trippia.travel.domain.travel.planparticipant.PlanRole;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.plan.PlanException;
import com.trippia.travel.exception.user.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static com.trippia.travel.domain.common.CityType.KOREA;
import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.ACCEPTED;
import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.PENDING;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.OWNER;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.PARTICIPANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private PlanParticipantRepository planParticipantRepository;

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
        assertThat(plans.get(0).getOwnerEmail()).isEqualTo(email);


        List<PlanParticipant> participants = planParticipantRepository.findByPlanIdAndStatus(plans.get(0).getId(), ACCEPTED);
        assertThat(participants).hasSize(1);
        assertThat(participants.get(0).getUser()).isEqualTo(user);
        assertThat(participants.get(0).getPlan()).isEqualTo(plans.get(0));
        assertThat(participants.get(0).getRole()).isEqualTo(OWNER);

        List<PlanCity> planCities = planCityRepository.findAll();
        assertThat(planCities).hasSize(2);
    }

    @DisplayName("planId로 여행 계획 상세정보를 조회한다.")
    @Test
    void findPlan() {
        // given
        // User 생성
        User user1 = createUser("email1");
        User user2 = createUser("email2");
        User user3 = createUser("email3");

        // Plan 생성
        Plan plan = Plan.createPlan(user1.getEmail(), "title", LocalDate.now(), LocalDate.now().plusDays(1));

        // Schedule 생성
        Schedule schedule1 = new Schedule(plan, LocalDate.now());
        plan.addSchedule(schedule1);

        Schedule schedule2 = new Schedule(plan, LocalDate.now().plusDays(1));
        plan.addSchedule(schedule2);
        Plan savePlan = planRepository.save(plan);
        addPlanParticipant(user1, savePlan, OWNER);
        addPlanParticipant(user2, savePlan, PARTICIPANT);
        addPlanParticipant(user3, savePlan, PARTICIPANT);

        // ScheduleItem 생성
        Memo item1 = Memo.builder().schedule(schedule1).content("content1").sequence(1).build();
        SchedulePlace item2 = SchedulePlace.builder().schedule(schedule1).name("스타벅스 강남점").sequence(2).build();
        Memo item3 = Memo.builder().schedule(schedule2).content("content2").sequence(3).build();
        scheduleItemRepository.saveAll(List.of(item1, item2, item3));

        // when
        PlanDetailsResponse result = planService.findPlan(user1.getEmail(), savePlan.getId());

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


        // PlanParticipants 검증
        List<PlanParticipantResponse> participants = result.getParticipants();
        assertThat(participants).hasSize(3)
                .extracting("userId", "nickname", "role")
                .containsExactlyInAnyOrder(
                        tuple(user1.getId(), user1.getNickname(), OWNER.name()),
                        tuple(user2.getId(), user2.getNickname(), PARTICIPANT.name()),
                        tuple(user3.getId(), user3.getNickname(), PARTICIPANT.name())
                );
    }

    @DisplayName("여행계획 참여자가 아닌 경우 여행 계획 상세정보를 조회하면 예외가 발생한다.")
    @Test
    void findPlan_NoPermission() {
        // given
        // User 생성
        User user1 = createUser("email1");
        User user2 = createUser("email2");

        // Plan 생성
        Plan plan = Plan.createPlan(user1.getEmail(), "title", LocalDate.now(), LocalDate.now().plusDays(1));

        // Schedule 생성
        Schedule schedule1 = new Schedule(plan, LocalDate.now());
        plan.addSchedule(schedule1);

        Schedule schedule2 = new Schedule(plan, LocalDate.now().plusDays(1));
        plan.addSchedule(schedule2);
        Plan savePlan = planRepository.save(plan);
        addPlanParticipant(user1, savePlan, OWNER);

        // when & then
        assertThatThrownBy(() -> planService.findPlan(user2.getEmail(), savePlan.getId()))
                .isInstanceOf(PlanException.class)
                .hasMessage("접근 권한이 없습니다.");

    }

    @DisplayName("다가오는 여행계획 정보들을 조회한다.")
    @Test
    void getUpcomingPlanByUser() {
        // given
        User user = createUser("email");

        LocalDate now = LocalDate.now();
        Plan plan1 = createPlan(user.getEmail(), "title1", now.plusDays(1), now.plusDays(2));
        Plan plan2 = createPlan(user.getEmail(), "title2", now.plusDays(2), now.plusDays(3));
        Plan plan3 = createPlan(user.getEmail(), "title3", now.minusDays(2), now.minusDays(1));

        addPlanParticipant(user, plan1, PARTICIPANT);
        addPlanParticipant(user, plan2, PARTICIPANT);
        addPlanParticipant(user, plan3, OWNER);

        Country japan = createCountry("일본");
        Country korea = createCountry("한국");
        City tokyo = createCity("도쿄", japan, JAPAN);
        City seoul = createCity("서울", korea, KOREA);

        PlanCity planCity1 = createPlanCity(plan1, tokyo);
        PlanCity planCity2 = createPlanCity(plan2, seoul);
        PlanCity planCity3 = createPlanCity(plan3, seoul);

        plan1.addPlanCity(planCity1);
        plan2.addPlanCity(planCity2);
        plan3.addPlanCity(planCity3);

        // when
        List<PlanSummaryResponse> result = planService.getUpcomingPlanByUser(user.getEmail());

        // then
        assertThat(result).hasSize(2)
                .extracting("title", "cityImage")
                .containsExactly(
                        tuple(plan1.getTitle(), tokyo.getImageUrl()),
                        tuple(plan2.getTitle(), seoul.getImageUrl())
                );
    }


    @DisplayName("이미 지나간 여행계획 정보들을 조회한다.")
    @Test
    void getPastPlanByUser() {
        // given
        User user = createUser("email");

        LocalDate now = LocalDate.now();
        Plan plan1 = createPlan(user.getEmail(), "title1", now.plusDays(1), now.plusDays(2));
        Plan plan2 = createPlan(user.getEmail(), "title2", now.minusDays(5), now.minusDays(4));
        Plan plan3 = createPlan(user.getEmail(), "title3", now.minusDays(2), now.minusDays(1));

        addPlanParticipant(user, plan1, OWNER);
        addPlanParticipant(user, plan2, OWNER);
        addPlanParticipant(user, plan3, OWNER);

        Country japan = createCountry("일본");
        Country korea = createCountry("한국");
        City tokyo = createCity("도쿄", japan, JAPAN);
        City seoul = createCity("서울", korea, KOREA);

        PlanCity planCity1 = createPlanCity(plan1, tokyo);
        PlanCity planCity2 = createPlanCity(plan2, seoul);
        PlanCity planCity3 = createPlanCity(plan3, seoul);

        plan1.addPlanCity(planCity1);
        plan2.addPlanCity(planCity2);
        plan3.addPlanCity(planCity3);

        // when
        List<PlanSummaryResponse> result = planService.getPastPlanByUser(user.getEmail());

        // then
        assertThat(result).hasSize(2)
                .extracting("title", "cityImage")
                .containsExactly(
                        tuple(plan2.getTitle(), seoul.getImageUrl()),
                        tuple(plan3.getTitle(), seoul.getImageUrl())
                );

    }

    @DisplayName("여행 계획을 삭제한다.")
    @Test
    void deletePlan() {
        // given
        User user = createUser("email");

        LocalDate now = LocalDate.now();
        Plan plan = createPlan(user.getEmail(), "title1", now.plusDays(1), now.plusDays(2));
        addPlanParticipant(user, plan, OWNER);

        // when
        planService.deletePlan(user.getEmail(), plan.getId());

        // then
        Optional<Plan> foundPlan = planRepository.findById(plan.getId());
        assertThat(foundPlan).isEmpty();
    }

    @DisplayName("닉네임으로 사용자를 여행 계획 초대요청을 보낸다.")
    @Test
    void invitePlan() {
        // given
        User owner = createUser("email");

        LocalDate now = LocalDate.now();
        Plan plan = createPlan(owner.getEmail(), "title", now.plusDays(1), now.plusDays(2));
        addPlanParticipant(owner, plan, OWNER);

        User user = createUser("email2");

        // when
        planService.invitePlan(owner.getEmail(), plan.getId(), user.getNickname());

        // then
        List<PlanParticipant> result = planParticipantRepository.findAll();
        assertThat(result).hasSize(2)
                .extracting("user", "role", "status")
                .containsExactlyInAnyOrder(
                        tuple(owner, OWNER, ACCEPTED),
                        tuple(user, PARTICIPANT, PENDING)
                );

    }

    @DisplayName("존재하지 않는 닉네임을 초대할 경우 예외가 발생한다.")
    @Test
    void invitePlan_NotExistsUser() {
        // given
        User owner = createUser("email");

        LocalDate now = LocalDate.now();
        Plan plan = createPlan(owner.getEmail(), "title", now.plusDays(1), now.plusDays(2));
        addPlanParticipant(owner, plan, OWNER);

        // when & then
        assertThatThrownBy(() -> planService.invitePlan(owner.getEmail(), plan.getId(), "notExists"))
                .isInstanceOf(UserException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

    }

    @DisplayName("여행 계획 초대를 수락한다.")
    @Test
    void acceptInvite() {
        // given
        User owner = createUser("email");

        LocalDate now = LocalDate.now();
        Plan plan = createPlan(owner.getEmail(), "title", now.plusDays(1), now.plusDays(2));
        addPlanParticipant(owner, plan, OWNER);

        User user = createUser("email2");
        PlanParticipant participant = PlanParticipant.builder()
                .plan(plan)
                .user(user)
                .role(PARTICIPANT)
                .status(PENDING)
                .build();
        planParticipantRepository.save(participant);

        // when
        planService.acceptInvite(user.getEmail(), plan.getId());

        // then
        boolean result = planParticipantRepository.existsByUserIdAndPlanIdAndStatus(user.getId(), plan.getId(), ACCEPTED);
        assertThat(result).isTrue();
    }

    @DisplayName("초대받지 않은 사용자가 초대를 수락하면 예외가 발생한다.")
    @Test
    void acceptInvite_fail_noInvitation() {
        // given
        User owner = createUser("email1");
        Plan plan = createPlan(owner.getEmail(), "여행", LocalDate.now(), LocalDate.now().plusDays(1));
        addPlanParticipant(owner, plan, OWNER);

        User stranger = createUser("email2");

        // when & then
        assertThatThrownBy(() -> planService.acceptInvite(stranger.getEmail(), plan.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("초대 내역이 존재하지 않습니다.");
    }

    @DisplayName("이미 수락한 사용자가 초대를 다시 수락하면 예외가 발생한다.")
    @Test
    void acceptInvite_fail_alreadyAccepted() {
        // given
        User owner = createUser("email1");
        Plan plan = createPlan(owner.getEmail(), "여행", LocalDate.now(), LocalDate.now().plusDays(1));
        addPlanParticipant(owner, plan, OWNER);

        User participantUser = createUser("email2");

        PlanParticipant participant = PlanParticipant.builder()
                .plan(plan)
                .user(participantUser)
                .role(PARTICIPANT)
                .status(ACCEPTED)
                .build();

        planParticipantRepository.save(participant);

        // when & then
        assertThatThrownBy(() -> planService.acceptInvite(participantUser.getEmail(), plan.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 처리된 초대입니다.");
    }


    @DisplayName("여행계획을 수정한다.")
    @Test
    void updatePlan() {
        // given
        User owner = createUser("email1");
        Plan plan = createPlan(owner.getEmail(), "여행", LocalDate.now(), LocalDate.now().plusDays(1));
        addPlanParticipant(owner, plan, OWNER);

        LocalDate updateStartDate = LocalDate.of(2050, 1, 1);
        LocalDate updateEndDate = LocalDate.of(2050, 2, 1);

        PlanUpdateRequest request = PlanUpdateRequest.builder()
                .title("수정한 제목")
                .startDate(updateStartDate)
                .endDate(updateEndDate)
                .build();

        // when
        planService.updatePlan(owner.getEmail(), plan.getId(), request);

        // then
        Plan updatedPlan = planRepository.findById(plan.getId()).get();
        assertThat(updatedPlan.getTitle()).isEqualTo("수정한 제목");
        assertThat(updatedPlan.getStartDate()).isEqualTo(updateStartDate);
        assertThat(updatedPlan.getEndDate()).isEqualTo(updateEndDate);
    }

    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("password")
                .nickname("nick_" + email)
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
                .imageUrl(name + ".jpg")
                .build());
    }

    private Plan createPlan(String email, String title, LocalDate startDate, LocalDate endDate) {
        Plan plan = Plan.builder()
                .ownerEmail(email)
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return planRepository.save(plan);
    }

    private PlanCity createPlanCity(Plan plan, City city) {
        PlanCity planCity = PlanCity.builder()
                .plan(plan)
                .city(city)
                .build();
        return planCityRepository.save(planCity);
    }

    private void addPlanParticipant(User user, Plan plan, PlanRole role) {
        PlanParticipant participant = PlanParticipant.builder()
                .user(user)
                .plan(plan)
                .role(role)
                .status(ACCEPTED)
                .build();
        planParticipantRepository.save(participant);
    }


}