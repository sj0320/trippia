package com.trippia.travel.domain.travel.scheduleitem.memo;

import com.trippia.travel.controller.dto.memo.requset.MemoSaveRequest;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipant;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipantRepository;
import com.trippia.travel.domain.travel.planparticipant.PlanRole;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
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

import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.ACCEPTED;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemoServiceTest {

    @Autowired
    private MemoService memoService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private PlanParticipantRepository planParticipantRepository;

    @DisplayName("스케줄에 메모를 저장한다.")
    @Test
    void saveMemo() {
        // given
        User user = createUser("email1","nickname1");

        // Plan 생성
        Plan plan = Plan.createPlan(user.getEmail(), "plan", LocalDate.of(2099,1,1),
                LocalDate.of(2099,2,1));
        Plan savedPlan = planRepository.save(plan);
        addPlanParticipant(user, plan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099,1,1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        MemoSaveRequest memoSaveRequest = MemoSaveRequest.builder()
                .scheduleId(savedSchedule.getId())
                .content("content")
                .build();
        // when
        memoService.saveMemo(user.getEmail(), memoSaveRequest);
        // then
        List<Memo> memos = memoRepository.findAll();
        assertThat(memos).hasSize(1);
        assertThat(memos.get(0).getContent()).isEqualTo(memoSaveRequest.getContent());
    }


    @DisplayName("스케줄에 메모를 저장할때 권한이 없을 경우 예외가 발생한다.")
    @Test
    void saveMemoNotOwner() {
        // given
        User user1 = createUser("email1","nick1");
        User user2 = createUser("email2", "nick2");

        // Plan 생성
        Plan plan = Plan.createPlan(user1.getEmail(), "plan", LocalDate.of(2099,1,1),
                LocalDate.of(2099,2,1));
        Plan savedPlan = planRepository.save(plan);

        addPlanParticipant(user1, plan, OWNER);

        // Schedule 생성
        Schedule schedule = new Schedule(savedPlan, LocalDate.of(2099,1,1));
        Schedule savedSchedule = scheduleRepository.save(schedule);

        MemoSaveRequest memoSaveRequest = MemoSaveRequest.builder()
                .scheduleId(savedSchedule.getId())
                .content("content")
                .build();
        // when & then
        assertThatThrownBy(()-> memoService.saveMemo(user2.getEmail(), memoSaveRequest))
                .isInstanceOf(ScheduleException.class)
                .hasMessage("접근 권한이 없습니다.");
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