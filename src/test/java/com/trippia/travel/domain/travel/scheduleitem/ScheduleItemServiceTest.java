package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.memo.MemoRepository;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import com.trippia.travel.exception.scheduleitem.ScheduleItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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


    @DisplayName("스케줄 주인이 스케줄 항목을 삭제한다.")
    @Test
    void deleteScheduleItem() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);
        // Memo 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("content").build();
        Memo scheduleItem = scheduleItemRepository.save(memo);

        // when
        List<ScheduleItem> before = scheduleItemRepository.findAllByScheduleIdIn(List.of(savedSchedule.getId()));
        assertThat(before).hasSize(1);
        scheduleItemService.deleteScheduleItem(user.getEmail(), scheduleItem.getId());

        // then
        List<ScheduleItem> after = scheduleItemRepository.findAllByScheduleIdIn(List.of(savedSchedule.getId()));
        assertThat(after).hasSize(0);
    }

    @DisplayName("스케줄에 속한 모든 스케줄항목들을 조회한다.")
    @Test
    void getScheduleItemsByScheduleId() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // ScheduleItem 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("hello").build();
        SchedulePlace schedulePlace = SchedulePlace.builder().schedule(savedSchedule).name("서울역").build();
        scheduleItemRepository.saveAll(List.of(memo, schedulePlace));

        // when
        List<ScheduleItemResponse> scheduleItems = scheduleItemService.getScheduleItemsByScheduleId(user.getEmail(), savedSchedule.getId());

        // then
        assertThat(scheduleItems).hasSize(2)
                .extracting("content", "name", "type")
                .containsExactlyInAnyOrder(
                        tuple("hello", null, ScheduleItemType.MEMO),
                        tuple(null, "서울역", ScheduleItemType.PLACE)
                );
    }

    @DisplayName("스케줄 항목들의 부가정보들을 업데이트한다.")
    @Test
    void updateScheduleItemMeta() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // ScheduleItem 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("hello").build();
        Memo savedMemo = scheduleItemRepository.save(memo);

        // when
        scheduleItemService.updateScheduleItemMeta(user.getEmail(), savedMemo.getId(), 50000, LocalTime.of(15, 0));

        // then
        assertThat(savedMemo.getExpectedCost()).isEqualTo(50000);
        assertThat(savedMemo.getExecutionTime()).isEqualTo(LocalTime.of(15, 0));
    }

    @DisplayName("스케줄 주인이 아닌 경우 스케줄 항목들을 업데이트 할 수 없다.")
    @Test
    void updateScheduleItemMetaNowOwner() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // Memo 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("content").build();
        Memo scheduleItem = memoRepository.save(memo);

        // when & then
        assertThatThrownBy(() -> scheduleItemService.updateScheduleItemMeta("email2", scheduleItem.getId(), 100000, LocalTime.now()))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

    @DisplayName("메모를 수정한다.")
    @Test
    void updateMemo() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // ScheduleItem 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("hello").build();
        Memo savedMemo = scheduleItemRepository.save(memo);

        // when
        scheduleItemService.updateMemo(user.getEmail(), savedMemo.getId(), "modify");

        // then
        assertThat(savedMemo.getContent()).isEqualTo("modify");
    }

    @DisplayName("메모가 아닌 값에 대하여 메모수정을 하면 예외가 발생한다.")
    @Test
    void updateMemoWhenNotMemo() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user, "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // schedulePlace 생성
        SchedulePlace schedulePlace = SchedulePlace.builder().schedule(savedSchedule).name("서울역").build();
        SchedulePlace scheduleItem = scheduleItemRepository.save(schedulePlace);

        // when & then
        assertThatThrownBy(() -> scheduleItemService.updateMemo(
                user.getEmail(), scheduleItem.getId(), "modify"))
                .isInstanceOf(ScheduleItemException.class);
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