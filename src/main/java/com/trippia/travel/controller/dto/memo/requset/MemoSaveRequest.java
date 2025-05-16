package com.trippia.travel.controller.dto.memo.requset;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemoSaveRequest {

    private Long scheduleId;
    private String content;

    @Builder
    private MemoSaveRequest(Long scheduleId, String content) {
        this.scheduleId = scheduleId;
        this.content = content;
    }

    public Memo toEntity(Schedule schedule){
        return Memo.builder()
                .schedule(schedule)
                .content(content)
                .build();
    }

}
