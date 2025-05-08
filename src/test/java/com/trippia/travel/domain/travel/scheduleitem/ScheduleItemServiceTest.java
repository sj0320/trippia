package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.memo.MemoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ScheduleItemServiceTest {

    @Autowired
    private ScheduleItemService scheduleItemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @DisplayName("예상 지출 금액을 업데이트한다.")
    @Test
    void updateExpectedCost() {
        // given
        User user = createUser("email1","nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099,1,1),
                LocalDate.of(2099,2,1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099,1,1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // Memo 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("content").build();
        Memo scheduleItem = scheduleItemRepository.save(memo);
        // when
        scheduleItemService.updateExpectedCost(user.getEmail(), scheduleItem.getId(), 10000);

        // then
        assertThat(scheduleItem.getExpectedCost()).isEqualTo(10000);
    }

    @DisplayName("스케줄 주인이 아닌 사용자가 예상 지출을 업데이트 할 경우 예외가 발생한다")
    @Test
    void updateExpectedCostNotOwner() {
        // given
        User user = createUser("email1","nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099,1,1),
                LocalDate.of(2099,2,1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099,1,1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // Memo 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("content").build();
        Memo scheduleItem = memoRepository.save(memo);

        // when & then
        assertThatThrownBy(()-> scheduleItemService.updateExpectedCost("email2", scheduleItem.getId(), 100000))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("접근 권한이 없습니다.");

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