package com.trippia.travel.controller;

import com.trippia.travel.controller.dto.city.response.CityThumbnailResponse;
import com.trippia.travel.controller.dto.diary.response.DiaryThumbnailResponse;
import com.trippia.travel.controller.dto.post.response.CompanionPostListResponse;
import com.trippia.travel.domain.companionpost.post.CompanionPostService;
import com.trippia.travel.domain.diarypost.diary.DiaryService;
import com.trippia.travel.domain.diarypost.diary.cache.DiaryRankingCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final DiaryService diaryService;
    private final CompanionPostService companionPostService;
    private final DiaryRankingCacheService diaryRankingCacheService;

    @GetMapping
    public String home(Model model) {
//        List<DiaryThumbnailResponse> diaries = diaryService.getTopPopularDiaries(PageRequest.of(0, 10));
        List<DiaryThumbnailResponse> diaries = diaryRankingCacheService.getTopDiaries();
        model.addAttribute("diaries", diaries);

        List<CityThumbnailResponse> thumbnails = diaryService.getTopCityThumbnails(PageRequest.of(0, 10));
        model.addAttribute("thumbnails", thumbnails);

        List<CompanionPostListResponse> posts = companionPostService.searchLatestPostList(10);
        model.addAttribute("posts", posts);

        return "index";
    }
}
