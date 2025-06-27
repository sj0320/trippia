package com.trippia.travel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trippia.travel.domain.diarypost.diary.cache.DiaryRankingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final DiaryRankingCacheService diaryRankingCacheService;

    @PostMapping("/top-diary-cache/update")
    public ResponseEntity<String> updateDiaryCache() throws JsonProcessingException {
        diaryRankingCacheService.refreshTopDiariesCache();
        return ResponseEntity.ok("캐시 갱신 완료");
    }

}
