package com.trippia.travel.domain.post.diary;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import static com.trippia.travel.controller.dto.DiaryDto.*;

public interface DiaryRepositoryCustom {
    Slice<Diary> searchDiariesWithConditions(DiarySearchCondition condition, CursorData cursorData, Pageable pageable);
}
