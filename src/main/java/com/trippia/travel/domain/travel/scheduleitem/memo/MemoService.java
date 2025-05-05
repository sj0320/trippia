package com.trippia.travel.domain.travel.scheduleitem.memo;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.trippia.travel.controller.dto.MemoDto.MemoSaveRequest;

@RequiredArgsConstructor
@Service
public class MemoService {

    private final MemoRepository memoRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;

    public void saveMemo(String email, MemoSaveRequest request) {
        Long scheduleId = request.getScheduleId();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("해당 스케줄을 찾을 수 없습니다."));
        schedule.validateOwnerOf(email);

        Memo memo = request.toEntity();
        Memo savedMemo = memoRepository.save(memo);
        ScheduleItem scheduleItem = ScheduleItem.ofMemo(schedule, savedMemo);
        scheduleItemRepository.save(scheduleItem);
    }
}
