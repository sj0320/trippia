package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.TestConfig;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@Import(TestConfig.class)
@DataJpaTest
class PlanRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @DisplayName("사용자의 다가오는 여행계획들을 조회한다.")
    @Test
    void findByUserIdAndStartDateAfter() {
        // given
        User user = createUser();

        LocalDate now = LocalDate.now();
        Plan plan1 = createPlan(user, "title1", now.plusDays(1), now.plusDays(2));
        Plan plan2 = createPlan(user, "title2", now.plusDays(2), now.plusDays(3));
        Plan plan3 = createPlan(user, "title3", now.minusDays(2), now.minusDays(1));

        // when
        List<Plan> result = planRepository.findByUserIdAndStartDateAfter(user.getId(), now);

        // then
        Assertions.assertThat(result).hasSize(2)
                .extracting("title", "startDate", "endDate")
                .containsExactlyInAnyOrder(
                        tuple(plan1.getTitle(), plan1.getStartDate(), plan1.getEndDate()),
                        tuple(plan2.getTitle(), plan2.getStartDate(), plan2.getEndDate())
                );

    }

    @DisplayName("사용자의 날짜가 지나간 여행계획들을 조회한다.")
    @Test
    void findByUserIdAndStartDateBefore() {
        User user = createUser();

        LocalDate now = LocalDate.now();
        Plan plan1 = createPlan(user, "title1", now.plusDays(1), now.plusDays(2));
        Plan plan2 = createPlan(user, "title2", now.plusDays(2), now.plusDays(3));
        Plan plan3 = createPlan(user, "title3", now.minusDays(2), now.minusDays(1));

        // when
        List<Plan> result = planRepository.findByUserIdAndStartDateBefore(user.getId(), now);
        for (Plan plan : result) {
            System.out.println(plan.getTitle());
        }

        // then
        Assertions.assertThat(result).hasSize(1)
                .extracting("title", "startDate", "endDate")
                .containsExactlyInAnyOrder(
                        tuple(plan3.getTitle(), plan3.getStartDate(), plan3.getEndDate())
                );

    }

    private Plan createPlan(User user, String title, LocalDate startDate, LocalDate endDate) {
        Plan plan = Plan.builder()
                .user(user)
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return planRepository.save(plan);
    }

    private User createUser() {
        User user = User.builder()
                .email("email")
                .password("pwd")
                .nickname("nick")
                .role(Role.ROLE_USER)
                .loginType(LoginType.LOCAL)
                .profileImageUrl("image.jpg")
                .build();
        return userRepository.save(user);
    }

}