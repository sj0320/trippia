package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class MemoDto {

    @Getter @Setter
    public static class MemoSaveRequest{
        private Long scheduleId;
        private String content;

        @Builder
        private MemoSaveRequest(Long scheduleId, String content) {
            this.scheduleId = scheduleId;
            this.content = content;
        }

        public Memo toEntity(){
            return Memo.builder()
                    .content(content)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

}
