package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.TestConfig;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
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
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class ScheduleItemRepositoryTest {

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @DisplayName("스케줄에 속한 scheduleItem 들을 sequence 순서대로 조회한다")
    @Test
    void findAllByScheduleIdIn() {
        // given
        // User 생성
        User user = createUser();

        // plan 생성
        Plan plan = Plan.createPlan(user, "title", LocalDate.now(), LocalDate.now().plusDays(1));

        // schedule 생성
        Schedule schedule = new Schedule(plan, LocalDate.now());
        Schedule savedSchedule = scheduleRepository.save(schedule);
        // scheduleItem 생성
        Memo item1 = createMemo(savedSchedule, "content", 0);
        SchedulePlace item2 = createSchedulePlace(savedSchedule, "서울역", 3);
        Memo item3 = createMemo(savedSchedule, "content", 2);
        scheduleItemRepository.saveAll(List.of(item1, item2, item3));

        // plan 저장
        plan.addSchedule(savedSchedule);
        planRepository.save(plan);

        // when
        List<ScheduleItem> scheduleItems = scheduleItemRepository.findByScheduleIdOrderBySequence(savedSchedule.getId());

        // then
        assertThat(scheduleItems).hasSize(3);
        assertThat(scheduleItems)
                .extracting("id", "class")
                .containsExactly(
                        tuple(item1.getId(), Memo.class),
                        tuple(item3.getId(), Memo.class),
                        tuple(item2.getId(), SchedulePlace.class)
                );
    }


    @DisplayName("스케줄에 속한 scheduleItem 들을 sequence 순서대로 조회한다.")
    @Test
    void findAllByScheduleIdInOrderBySequence() {
        // given
        // User 생성
        User user = createUser();

        // plan 생성
        Plan plan = Plan.createPlan(user, "title", LocalDate.now(), LocalDate.now().plusDays(1));
        planRepository.save(plan);

        // schedule 생성
        Schedule schedule = new Schedule(plan, LocalDate.now());
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // scheduleItem 생성
        Memo item1 = createMemo(savedSchedule, "content", 3);
        SchedulePlace item2 = createSchedulePlace(savedSchedule, "서울역", 1);
        Memo item3 = createMemo(savedSchedule, "content", 2);
        scheduleItemRepository.saveAll(List.of(item1, item2, item3));

        // when
        List<ScheduleItem> scheduleItems = scheduleItemRepository.findAllByScheduleIdInOrderBySequence(List.of(schedule.getId()));

        // then
        assertThat(scheduleItems).hasSize(3)
                .extracting("id", "sequence")
                .containsExactly(
                        tuple(item2.getId(), 1),
                        tuple(item3.getId(), 2),
                        tuple(item1.getId(), 3)
                );

    }

    @DisplayName("스케줄의 항목중 마지막 순서를 조회한다.")
    @Test
    void findLastSequenceByScheduleId() {
        // given
        // User 생성
        User user = createUser();

        // plan 생성
        Plan plan = Plan.createPlan(user, "title", LocalDate.now(), LocalDate.now().plusDays(1));

        // schedule 생성
        Schedule schedule = new Schedule(plan, LocalDate.now());
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // scheduleItem 생성
        Memo memo1 = createMemo(savedSchedule, "content1", 3);

        Memo memo2 = createMemo(savedSchedule, "content2", 4);
        scheduleItemRepository.saveAll(List.of(memo1, memo2));
        planRepository.save(plan);

        // when
        Integer lastSequence = scheduleItemRepository.findLastSequenceByScheduleId(savedSchedule.getId())
                .orElseThrow();
        // then
        assertThat(lastSequence).isEqualTo(4);
    }

    private SchedulePlace createSchedulePlace(Schedule schedule, String name, Integer sequence) {
        return SchedulePlace.builder()
                .schedule(schedule)
                .name(name)
                .sequence(sequence)
                .build();
    }

    private Memo createMemo(Schedule savedSchedule, String content, int sequence) {
        return Memo.builder()
                .schedule(savedSchedule)
                .content(content)
                .sequence(sequence)
                .build();
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