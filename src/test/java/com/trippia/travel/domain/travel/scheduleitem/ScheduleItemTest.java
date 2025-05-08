package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
class ScheduleItemTest {

    @DisplayName("예상 지출 금액을 업데이트한다.")
    @Test
    void updateExpectedCost() {
        // given
        Schedule schedule = mock(Schedule.class);
        SchedulePlace schedulePlace = SchedulePlace.builder()
                .schedule(schedule)
                .build();

        // when
        schedulePlace.updateExpectedCost(10000);

        // then
        assertThat(schedulePlace.getExpectedCost()).isEqualTo(10000);
    }


}