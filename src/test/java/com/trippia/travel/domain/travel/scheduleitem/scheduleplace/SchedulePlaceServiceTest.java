package com.trippia.travel.domain.travel.scheduleitem.scheduleplace;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.location.place.PlaceRepository;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.controller.dto.SchedulePlaceDto.PlaceSaveRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SchedulePlaceServiceTest {

    @Autowired
    private SchedulePlaceService schedulePlaceService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @Autowired
    private SchedulePlaceRepository schedulePlaceRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @DisplayName("스케줄에 방문할 장소를 저장한다.")
    @Test
    void savePlace() {
        // given
        User user = createUser("email1","nickname1");

        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099,1,1),
                LocalDate.of(2099,2,1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099,1,1));
        Schedule savedSchedule = scheduleRepository.save(schedule);


        // 스케줄에 저장할 장소 request 객체 생성
        PlaceSaveRequest request = createPlaceSaveRequest(savedSchedule.getId());
        // when
        schedulePlaceService.savePlace(user.getEmail(), request);

        // then

        // place
        List<Place> places = placeRepository.findAll();
        assertThat(places).hasSize(1);
        assertThat(places.get(0).getName()).isEqualTo(request.getName());

        // scheduleItem
        List<ScheduleItem> scheduleItems = scheduleItemRepository.findAll();
        assertThat(scheduleItems).hasSize(1);
        assertThat(scheduleItems.get(0).getSchedule()).isEqualTo(savedSchedule);
        assertThat(scheduleItems.get(0).getItemType()).isEqualTo(ScheduleItemType.PLACE);

        // schedulePlace
        List<SchedulePlace> schedulePlaces = schedulePlaceRepository.findAll();
        assertThat(schedulePlaces).hasSize(1);
        assertThat(schedulePlaces.get(0).getPlace().getId()).isEqualTo(places.get(0).getId());
    }

    @DisplayName("스케줄에 방문할 장소를 저장할때 본인의 스케줄이 아닐 경우 예외가 발생한다.")
    @Test
    void savePlaceNotOwner() {
        // given
        User user1 = createUser("email1", "nick1");
        User user2 = createUser("email2", "nick2");

        // Plan 생성
        Plan plan = Plan.createPlan(user1, "plan", LocalDate.of(2099,1,1),
                LocalDate.of(2099,2,1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099,1,1));
        Schedule savedSchedule = scheduleRepository.save(schedule);


        // 스케줄에 저장할 장소 request 객체 생성
        PlaceSaveRequest request = createPlaceSaveRequest(savedSchedule.getId());
        // when & then
        assertThatThrownBy(()-> schedulePlaceService.savePlace(user2.getEmail(), request))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("접근 권한이 없습니다.");


    }

    private PlaceSaveRequest createPlaceSaveRequest(Long scheduleId) {
        return PlaceSaveRequest.builder()
                .scheduleId(scheduleId)
                .placeId("testId")
                .name("name")
                .address("address")
                .latitude(0.0)
                .longitude(0.0)
                .category("category")
                .build();
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

}