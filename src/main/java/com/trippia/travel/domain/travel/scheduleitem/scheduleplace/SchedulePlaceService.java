package com.trippia.travel.domain.travel.scheduleitem.scheduleplace;

import com.trippia.travel.controller.dto.scheduleplace.request.SchedulePlaceSaveRequest;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchedulePlaceService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Long savePlace(String email, SchedulePlaceSaveRequest request) {
        Schedule schedule = getSchedule(request.getScheduleId());
        schedule.validateOwnerOf(email);

        SchedulePlace schedulePlace = scheduleItemRepository.save(request.toEntity(schedule));
        return schedulePlace.getId();
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("해당 스케줄을 찾을 수 없습니다."));
    }

}
