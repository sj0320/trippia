package com.trippia.travel.domain.travel.scheduleitem.memo;

import com.trippia.travel.controller.dto.memo.requset.MemoSaveRequest;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;

    @Transactional
    public Long saveMemo(String email, MemoSaveRequest request) {
        Long scheduleId = request.getScheduleId();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("해당 스케줄을 찾을 수 없습니다."));
        schedule.validateOwnerOf(email);

        Memo memo = scheduleItemRepository.save(request.toEntity(schedule));
        return memo.getId();
    }
}
