package com.trippia.travel.domain.travel.planparticipant;

import com.trippia.travel.TestConfig;
import com.trippia.travel.controller.dto.planparticipant.response.ReceivedPlanParticipantResponse;
import com.trippia.travel.controller.dto.planparticipant.response.RequestPlanParticipantResponse;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.travel.plan.PlanRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.PENDING;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.OWNER;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.PARTICIPANT;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(TestConfig.class)
@DataJpaTest
class PlanParticipantRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanParticipantRepository planParticipantRepository;

    @Autowired
    private PlanRepository planRepository;

    @DisplayName("여행 계획 초대 요청 목록을 조회한다.")
    @Test
    void findReceivedRequests() {
        // given
        User owner = createUser("ownerEmail");
        User user = createUser("userEmail");
        Plan plan = createPlan(owner.getEmail(), "title", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        addParticipant(owner, plan);
        createPlanParticipant(plan, user, PENDING);

        // when
        ReceivedPlanParticipantResponse result = planParticipantRepository.findReceivedRequests(user.getId()).get(0);

        // then
        assertThat(result.getNickname()).isEqualTo(owner.getNickname());
        assertThat(result.getProfileImageUrl()).isEqualTo(owner.getProfileImageUrl());
        assertThat(result.getPlanId()).isEqualTo(plan.getId());
    }

    @DisplayName("요청자가 보낸 초대 목록을 조회할 수 있다")
    @Test
    void findSentRequests() {
        // given
        User owner = createUser("ownerEmail"); // 로그인한 유저 (요청자)
        User participant = createUser("userEmail"); // 요청 받은 유저

        Plan plan = createPlan(owner.getEmail(), "plan title", LocalDate.now().plusDays(3), LocalDate.now().plusDays(8));
        addParticipant(owner, plan);
        createPlanParticipant(plan, participant, PENDING); // owner가 participant에게 초대 보냄

        // when
        RequestPlanParticipantResponse result = planParticipantRepository.findSentRequests(owner.getId()).get(0);

        // then
        assertThat(result.getNickname()).isEqualTo(participant.getNickname());
        assertThat(result.getProfileImageUrl()).isEqualTo(participant.getProfileImageUrl());
        assertThat(result.getPlanId()).isEqualTo(plan.getId());
        assertThat(result.getPlanTitle()).isEqualTo(plan.getTitle());
    }


    private Plan createPlan(String ownerEmail, String title, LocalDate startDate, LocalDate endDate) {
        Plan plan = Plan.builder()
                .ownerEmail(ownerEmail)
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return planRepository.save(plan);
    }

    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("pwd")
                .nickname(email + "_nick")
                .role(Role.ROLE_USER)
                .loginType(LoginType.LOCAL)
                .profileImageUrl("image.jpg")
                .build();
        return userRepository.save(user);
    }

    private PlanParticipant createPlanParticipant(Plan plan, User user, InvitationStatus status) {
        PlanParticipant planParticipant = PlanParticipant.builder()
                .plan(plan)
                .status(status)
                .user(user)
                .role(PARTICIPANT)
                .build();
        return planParticipantRepository.save(planParticipant);
    }

    private void addParticipant(User user, Plan plan) {
        PlanParticipant planParticipant = PlanParticipant.builder()
                .user(user)
                .plan(plan)
                .role(OWNER)
                .build();
        planParticipantRepository.save(planParticipant);
    }

}