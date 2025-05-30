package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.controller.dto.scheduleitem.requset.ScheduleItemOrderRequest;
import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.ScheduleItemType;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipant;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipantRepository;
import com.trippia.travel.domain.travel.planparticipant.PlanRole;
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

import static com.trippia.travel.controller.dto.scheduleitem.requset.ScheduleItemOrderRequest.ScheduleItemOrder;
import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.ACCEPTED;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.OWNER;
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

    @Autowired
    private PlanParticipantRepository planParticipantRepository;


    @DisplayName("스케줄 주인이 스케줄 항목을 삭제한다.")
    @Test
    void deleteScheduleItem() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user, plan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);
        // Memo 생성
        Memo memo = createMemo(savedSchedule, "content1", 1);
        Memo scheduleItem = scheduleItemRepository.save(memo);

        // when
        List<ScheduleItem> before = scheduleItemRepository.findByScheduleIdOrderBySequence(savedSchedule.getId());
        assertThat(before).hasSize(1);
        scheduleItemService.deleteScheduleItem(user.getEmail(), scheduleItem.getId());

        // then
        List<ScheduleItem> after = scheduleItemRepository.findByScheduleIdOrderBySequence(savedSchedule.getId());
        assertThat(after).hasSize(0);
    }

    @DisplayName("여행 계획 참여자가 아닌 사용자가 스케줄 항목을 삭제할 경웅 예외가 발생한다.")
    @Test
    void deleteScheduleItem_NoPermission() {
        // given
        User user1 = createUser("email1", "nickname1");
        User user2 = createUser("email2", "nickname2");

        // Plan 생성
        Plan plan = Plan.createPlan(user1.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user1, plan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);
        // Memo 생성
        Memo memo = createMemo(savedSchedule, "content1", 1);
        Memo scheduleItem = scheduleItemRepository.save(memo);

        // when
        assertThatThrownBy(() -> scheduleItemService.deleteScheduleItem(user2.getEmail(), scheduleItem.getId()))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("접근 권한이 없습니다.");
    }


    @DisplayName("스케줄에 속한 모든 스케줄항목들을 조회한다.")
    @Test
    void getScheduleItemsByScheduleId() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user, plan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // ScheduleItem 생성
        Memo memo = createMemo(savedSchedule, "hello", 1);
        SchedulePlace schedulePlace = createSchedulePlace(savedSchedule, "서울역", 2);
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

    @DisplayName("여행 참여자가 아닌 사용자가 케줄항목들을 조회하면 예외가 발생한다.")
    @Test
    void getScheduleItemsByScheduleId_NoPermission() {
        // given
        User user1 = createUser("email1", "nickname1");
        User user2 = createUser("email2", "nickname2");
        // Plan 생성
        Plan plan = Plan.createPlan(user1.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user1, plan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // ScheduleItem 생성
        Memo memo = createMemo(savedSchedule, "hello", 1);
        SchedulePlace schedulePlace = createSchedulePlace(savedSchedule, "서울역", 2);
        scheduleItemRepository.saveAll(List.of(memo, schedulePlace));

        // when & then
        assertThatThrownBy(()-> scheduleItemService.getScheduleItemsByScheduleId(user2.getEmail(), savedSchedule.getId()))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("접근 권한이 없습니다.");
    }




    @DisplayName("스케줄 항목들의 부가정보들을 업데이트한다.")
    @Test
    void updateScheduleItemMeta() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user, savedPlan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // ScheduleItem 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("hello").sequence(1).build();
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
        User user1 = createUser("email1", "nickname1");
        User user2 = createUser("email2", "nickname2");
        // Plan 생성
        Plan plan = Plan.createPlan(user1.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user1, savedPlan, OWNER);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // Memo 생성
        Memo memo = createMemo(savedSchedule, "content", 1);
        Memo scheduleItem = memoRepository.save(memo);

        // when & then
        assertThatThrownBy(() -> scheduleItemService.updateScheduleItemMeta(user2.getEmail(), scheduleItem.getId(), 100000, LocalTime.now()))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

    @DisplayName("메모를 수정한다.")
    @Test
    void updateMemo() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user, savedPlan, OWNER);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // ScheduleItem 생성
        Memo memo = Memo.builder().schedule(savedSchedule).content("hello").sequence(1).build();
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
        Plan plan = Plan.createPlan(user.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // schedulePlace 생성
        SchedulePlace schedulePlace = createSchedulePlace(savedSchedule, "서울역", 1);
        SchedulePlace scheduleItem = scheduleItemRepository.save(schedulePlace);

        // when & then
        assertThatThrownBy(() -> scheduleItemService.updateMemo(
                user.getEmail(), scheduleItem.getId(), "modify"))
                .isInstanceOf(ScheduleItemException.class);
    }

    @DisplayName("스케줄 아이템들의 순서를 재정렬한다.")
    @Test
    void reorderItems() {
        // given
        User user = createUser("email1", "nickname1");
        // Plan 생성
        Plan plan = Plan.createPlan(user.getEmail(), "plan", LocalDate.of(2099, 1, 1),
                LocalDate.of(2099, 2, 1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user, savedPlan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099, 1, 1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // item 생성
        SchedulePlace item1 = createSchedulePlace(savedSchedule, "서울역", 1);
        SchedulePlace item2 = createSchedulePlace(savedSchedule, "강남역", 2);
        Memo item3 = createMemo(savedSchedule, "content1", 3);
        Memo item4 = createMemo(savedSchedule, "content2", 4);

        scheduleItemRepository.saveAll(List.of(item1, item2, item3, item4));

        // when
        ScheduleItemOrder itemOrder1 = createItemOrder(item1, 2);
        ScheduleItemOrder itemOrder2 = createItemOrder(item2, 4);
        ScheduleItemOrder itemOrder3 = createItemOrder(item3, 1);
        ScheduleItemOrder itemOrder4 = createItemOrder(item4, 3);


        ScheduleItemOrderRequest request = ScheduleItemOrderRequest.builder()
                .scheduleId(savedSchedule.getId())
                .orders(List.of(itemOrder1, itemOrder2, itemOrder3, itemOrder4))
                .build();

        scheduleItemService.reorderItems(user.getEmail(), request);

        // then

        List<ScheduleItem> scheduleItems = scheduleItemRepository.findByScheduleId(savedSchedule.getId());
        assertThat(scheduleItems).hasSize(4)
                .extracting("id", "sequence")
                .containsExactlyInAnyOrder(
                        tuple(item3.getId(), 1),
                        tuple(item1.getId(), 2),
                        tuple(item4.getId(), 3),
                        tuple(item2.getId(), 4)
                );
    }

    private ScheduleItemOrder createItemOrder(ScheduleItem item, Integer sequence) {
        return ScheduleItemOrder.builder()
                .itemId(item.getId())
                .sequence(sequence)
                .build();
    }

    private SchedulePlace createSchedulePlace(Schedule schedule, String name, Integer sequence) {
        return SchedulePlace.builder()
                .schedule(schedule)
                .name(name)
                .sequence(sequence)
                .build();
    }

    private Memo createMemo(Schedule schedule, String content, Integer sequence) {
        return Memo.builder()
                .schedule(schedule)
                .content(content)
                .sequence(sequence)
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

    private void addPlanParticipant(User user, Plan plan, PlanRole role){
        PlanParticipant participant = PlanParticipant.builder()
                .user(user)
                .plan(plan)
                .role(role)
                .status(ACCEPTED)
                .build();
        planParticipantRepository.save(participant);
    }


}