package com.trippia.travel.domain.travel.scheduleitem.memo;

import com.trippia.travel.controller.dto.memo.requset.MemoSaveRequest;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipantRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.ACCEPTED;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemoService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final PlanParticipantRepository planParticipantRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveMemo(String email, MemoSaveRequest request) {
        Long scheduleId = request.getScheduleId();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("해당 스케줄을 찾을 수 없습니다."));

        validatePermission(getUser(email), schedule);

        Integer lastSequence = scheduleItemRepository.findLastSequenceByScheduleId(schedule.getId())
                .orElse(0);
        int sequence = lastSequence + 1;

        Memo memo = scheduleItemRepository.save(request.toEntity(schedule, sequence));
        return memo.getId();
    }

    private void validatePermission(User user, Schedule schedule) {
        Long planId = schedule.getPlan().getId();
        boolean hasPermission = planParticipantRepository.existsByUserIdAndPlanIdAndStatus(user.getId(), planId, ACCEPTED);
        if (!hasPermission) {
            throw new ScheduleException("접근 권한이 없습니다.");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }
}
