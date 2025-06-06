package com.trippia.travel.controller.api;

import com.trippia.travel.controller.dto.post.request.CursorData;
import com.trippia.travel.controller.dto.post.request.PostSearchCondition;
import com.trippia.travel.controller.dto.post.response.CompanionPostListResponse;
import com.trippia.travel.domain.common.SortOption;
import com.trippia.travel.domain.companionpost.post.CompanionPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companion-post")
@Slf4j
public class CompanionPostApiController {

    private final CompanionPostService companionPostService;

    @GetMapping("/list")
    public Slice<CompanionPostListResponse> getPostList(@ModelAttribute PostSearchCondition condition,
                             @ModelAttribute CursorData cursorData,
                             @PageableDefault(size = 1) Pageable pageable){

        Sort sortOption = SortOption.from(condition.getSort()).getSort();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);
        return companionPostService.searchPostList(condition, cursorData, sortedPageable);
    }


}
