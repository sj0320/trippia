package com.trippia.travel.domain.travel.scheduleitem.scheduleplace;

import com.trippia.travel.controller.dto.scheduleplace.request.SchedulePlaceSaveRequest;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipantRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchedulePlaceService {

    private final UserRepository userRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlanParticipantRepository planParticipantRepository;

    @Transactional
    public Long savePlace(String email, SchedulePlaceSaveRequest request) {
        Schedule schedule = getSchedule(request.getScheduleId());

        validatePermission(getUser(email), schedule);

        Integer lastSequence = scheduleItemRepository.findLastSequenceByScheduleId(schedule.getId())
                .orElse(0);
        int sequence = lastSequence + 1;

        SchedulePlace schedulePlace = scheduleItemRepository.save(request.toEntity(schedule,sequence));
        return schedulePlace.getId();
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("해당 스케줄을 찾을 수 없습니다."));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

    private void validatePermission(User user, Schedule schedule) {
        Long planId = schedule.getPlan().getId();
        boolean hasPermission = planParticipantRepository.existsByUserIdAndPlanId(user.getId(), planId);
        if (!hasPermission) {
            throw new ScheduleException("접근 권한이 없습니다.");
        }
    }

}
