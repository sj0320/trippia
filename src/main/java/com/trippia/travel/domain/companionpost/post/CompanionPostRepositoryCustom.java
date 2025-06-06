package com.trippia.travel.domain.companionpost.post;

import com.trippia.travel.controller.dto.post.request.CursorData;
import com.trippia.travel.controller.dto.post.request.PostSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CompanionPostRepositoryCustom {
    Slice<CompanionPost> searchDiariesWithConditions(PostSearchCondition condition, CursorData cursorData, Pageable pageable);

}
