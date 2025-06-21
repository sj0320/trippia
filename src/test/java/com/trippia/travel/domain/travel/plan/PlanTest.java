package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.domain.user.User;
import com.trippia.travel.exception.plan.PlanException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class PlanTest {

    @DisplayName("Plan의 주인이 아닌 경우 예외가 발생한다.")
    @Test
    void validateOwnerOf() {
        // given
        User user = User.builder().email("email1").build();
        Plan plan = Plan.builder()
                .ownerEmail(user.getEmail())
                .title("title")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .build();
        

        // when & then
        Assertions.assertThatThrownBy(()-> plan.validateOwnerOf("notOwner"))
                .isInstanceOf(PlanException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

}