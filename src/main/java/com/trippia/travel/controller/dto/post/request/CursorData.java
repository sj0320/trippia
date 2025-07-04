package com.trippia.travel.controller.dto.post.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CursorData {

    private Long lastId;
    private LocalDateTime lastCreatedAt;
    private Integer lastViewCount;

    @Builder
    private CursorData(Long lastId, LocalDateTime lastCreatedAt, Integer lastViewCount) {
        this.lastId = lastId;
        this.lastCreatedAt = lastCreatedAt;
        this.lastViewCount = lastViewCount;
    }
}
