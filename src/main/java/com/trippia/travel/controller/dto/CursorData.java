package com.trippia.travel.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CursorData {

    private Long lastId;
    private LocalDateTime lastCreatedAt;
    private Integer lastLikeCount;
    private Integer lastViewCount;

    @Builder
    private CursorData(Long lastId, LocalDateTime lastCreatedAt, Integer lastLikeCount, Integer lastViewCount) {
        this.lastId = lastId;
        this.lastCreatedAt = lastCreatedAt;
        this.lastLikeCount = lastLikeCount;
        this.lastViewCount = lastViewCount;
    }

    public static CursorData init(){
        return CursorData.builder()
                .lastId(Long.MAX_VALUE)
                .lastCreatedAt(LocalDateTime.now().plusYears(1))
                .lastLikeCount(Integer.MAX_VALUE)
                .lastViewCount(Integer.MAX_VALUE)
                .build();
    }

    public static CursorData of(Long lastId, LocalDateTime lastCreatedAt, Integer lastLikeCount, Integer lastViewCount){
        return CursorData.builder()
                .lastId(lastId)
                .lastCreatedAt(lastCreatedAt)
                .lastLikeCount(lastLikeCount)
                .lastViewCount(lastViewCount)
                .build();
    }

}
