package com.trippia.travel.domain.diarypost.diary;

import com.trippia.travel.controller.dto.diary.request.CursorData;
import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface DiaryRepositoryCustom {
    Slice<Diary> searchDiariesWithConditions(DiarySearchCondition condition, CursorData cursorData, Pageable pageable);
}
