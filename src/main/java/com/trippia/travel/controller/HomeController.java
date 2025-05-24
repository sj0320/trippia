package com.trippia.travel.controller;

import com.trippia.travel.controller.dto.city.response.CityThumbnailResponse;
import com.trippia.travel.controller.dto.diary.response.DiaryListResponse;
import com.trippia.travel.domain.post.diary.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DiaryService diaryService;

    @GetMapping
    public String home(Model model) {
        // 인기 여행일지 Top 5
        List<DiaryListResponse> diaries = diaryService.getTopPopularDiaries(PageRequest.of(0, 5));
        model.addAttribute("diaries", diaries);

        // 가장 많이 작성된 도시의 여행일지의 썸네일들 ...
        List<CityThumbnailResponse> thumbnails = diaryService.getTopCityThumbnails(PageRequest.of(0, 5));
        model.addAttribute("thumbnails", thumbnails);
        return "index";
    }

}
