package com.trippia.travel.domain.travel.scheduleitem.memo;

import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo extends ScheduleItem {

    private String content;

    private LocalDateTime createdAt;

    @Builder
    private Memo(Schedule schedule, String content, Integer sequence) {
        super(schedule,sequence);
        this.createdAt = LocalDateTime.now();
        this.content = content;
    }

    public void updateContent(String content){
        this.content = content;
    }
}
