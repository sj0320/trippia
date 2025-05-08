package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.exception.scheduleitem.ScheduleItemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleItemService {

    private final ScheduleItemRepository scheduleItemRepository;

    @Transactional
    public void updateExpectedCost(String email, Long scheduleItemId, Integer expectedCost) {
        ScheduleItem scheduleItem = scheduleItemRepository.findById(scheduleItemId)
                .orElseThrow(() -> new ScheduleItemException("스케줄 항목을 찾을 수 없습니다."));

        Schedule schedule = scheduleItem.getSchedule();
        schedule.validateOwnerOf(email);
        scheduleItem.updateExpectedCost(expectedCost);
    }
}
