package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.TestConfig;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class ScheduleItemRepositoryTest {

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @DisplayName("스케줄에 속한 scheduleItem 들을 조회한다")
    @Test
    void findAllByScheduleIdIn() {
        // given
        // User 생성
        User user = createUser();

        // plan 생성
        Plan plan = Plan.createPlan(user, "title", LocalDate.now(), LocalDate.now().plusDays(1));

        // schedule 생성
        Schedule schedule = new Schedule(plan, LocalDate.now());

        // scheduleItem 생성
        Memo memo = Memo.builder()
                .schedule(schedule)
                .content("content")
                .build();
        SchedulePlace schedulePlace = SchedulePlace.builder()
                .schedule(schedule)
                .name("스타벅스 강남점")
                .build();

        scheduleItemRepository.saveAll(List.of(memo, schedulePlace));

        // plan 저장
        plan.addSchedule(schedule);
        planRepository.save(plan);

        // when
        List<ScheduleItem> scheduleItems = scheduleItemRepository.findAllByScheduleIdIn(List.of(schedule.getId()));

        // then
        assertThat(scheduleItems).hasSize(2);
        assertThat(scheduleItems).extracting("class").containsExactlyInAnyOrder(
                Memo.class,
                SchedulePlace.class
        );
    }

    private User createUser() {
        User user = User.builder()
                .email("email")
                .password("password")
                .nickname("nickname")
                .loginType(LoginType.LOCAL)
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

}