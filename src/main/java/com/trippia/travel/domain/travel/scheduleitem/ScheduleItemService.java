package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.exception.schedule.ScheduleException;
import com.trippia.travel.exception.scheduleitem.ScheduleItemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static com.trippia.travel.controller.dto.ScheduleItemDto.ScheduleItemResponse;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleItemService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final ScheduleRepository scheduleRepository;

//    @Transactional
//    public void updateExpectedCost(String email, Long scheduleItemId, Integer expectedCost) {
//        ScheduleItem scheduleItem = getScheduleItem(scheduleItemId);
//
//        Schedule schedule = scheduleItem.getSchedule();
//        schedule.validateOwnerOf(email);
//        scheduleItem.updateExpectedCost(expectedCost);
//    }

    @Transactional
    public void deleteScheduleItem(String email, Long scheduleItemId) {
        ScheduleItem scheduleItem = getScheduleItem(scheduleItemId);
        Schedule schedule = scheduleItem.getSchedule();
        schedule.validateOwnerOf(email);
        scheduleItemRepository.deleteById(scheduleItemId);
    }

    public List<ScheduleItemResponse> getScheduleItemsByScheduleId(String email, Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        schedule.validateOwnerOf(email);
        List<ScheduleItem> scheduleItems = scheduleItemRepository.findByScheduleId(scheduleId);
        return ScheduleItemConverter.toResponses(scheduleItems);
    }

    @Transactional
    public void updateScheduleItemMeta(String email, Long scheduleItemId, Integer expectedCost, LocalTime executionTime) {
        ScheduleItem scheduleItem = getScheduleItem(scheduleItemId);
        Schedule schedule = scheduleItem.getSchedule();
        schedule.validateOwnerOf(email);
        scheduleItem.updateMeta(expectedCost, executionTime);
    }

    @Transactional
    public void updateMemo(String email, Long scheduleItemId, String content) {
        ScheduleItem scheduleItem = getScheduleItem(scheduleItemId);
        if(!(scheduleItem instanceof Memo memo)){
            throw new ScheduleItemException("유효하지 않은 요청입니다");
        }
        Schedule schedule = scheduleItem.getSchedule();
        schedule.validateOwnerOf(email);
        memo.updateContent(content);
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("일정을 찾을 수 없습니다."));
    }

    private ScheduleItem getScheduleItem(Long scheduleItemId) {
        return scheduleItemRepository.findById(scheduleItemId)
                .orElseThrow(() -> new ScheduleItemException("스케줄 항목을 찾을 수 없습니다."));
    }
}
