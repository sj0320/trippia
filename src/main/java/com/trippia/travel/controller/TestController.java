package com.trippia.travel.controller;

import com.trippia.travel.domain.diarypost.diary.cache.DiaryRankingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final DiaryRankingCacheService diaryRankingCacheService;

    @GetMapping("/test/top-diary-cache/update")
    public ResponseEntity<String> updateDiaryCache() throws IOException {
        diaryRankingCacheService.refreshTopDiariesCache();
        return ResponseEntity.ok("캐시 갱신 완료");
    }

}
