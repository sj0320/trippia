package com.trippia.travel.event.diary.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trippia.travel.domain.diarypost.diary.cache.DiaryRankingCacheService;
import com.trippia.travel.event.diary.model.DiaryDeletedEvent;
import com.trippia.travel.event.diary.model.DiaryLikedEvent;
import com.trippia.travel.event.diary.model.DiaryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiaryEventListener {

    private final DiaryRankingCacheService cacheService;

    @EventListener
    public void handleDiaryLiked(DiaryLikedEvent event) {
        cacheService.updateRankingOnLike(event.diaryId(), event.likeCount());
    }

    @EventListener
    public void handleDiaryUpdated(DiaryUpdatedEvent event) throws JsonProcessingException {
        cacheService.updateCacheIfTopDiary(event.diaryId(), event.title(), event.thumbnail());
    }

    @EventListener
    public void handleDiaryDeleted(DiaryDeletedEvent event) {
        cacheService.removeCacheIfTopDiary(event.diaryId());
    }

}
