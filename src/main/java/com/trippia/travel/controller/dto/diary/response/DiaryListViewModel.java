package com.trippia.travel.controller.dto.diary.response;

import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DiaryListViewModel {

    private List<DiaryListResponse> diaryList = new ArrayList<>();
    private DiarySearchCondition searchCondition;
    private boolean hasNext;
    private String sort;

    @Builder
    private DiaryListViewModel(List<DiaryListResponse> diaryList, DiarySearchCondition searchCondition, boolean hasNext, String sort) {
        this.diaryList = diaryList;
        this.searchCondition = searchCondition;
        this.hasNext = hasNext;
        this.sort = sort;
    }

    public static DiaryListViewModel createDiaryListViewModel(List<DiaryListResponse> diaryList,
                                                              DiarySearchCondition searchCondition,
                                                              boolean hasNext) {
        return DiaryListViewModel.builder()
                .diaryList(diaryList)
                .searchCondition(searchCondition)
                .hasNext(hasNext)
                .build();
    }

}
