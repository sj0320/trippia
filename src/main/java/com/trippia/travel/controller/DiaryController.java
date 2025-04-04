package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.post.diary.DiaryService;
import com.trippia.travel.domain.theme.ThemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final CityService cityService;
    private final ThemeService themeService;
    private final DiaryService diaryService;


    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("cities", cityService.getCitiesGroupedByType());
        model.addAttribute("themes", themeService.getAllThemes());
    }

    @GetMapping("/new")
    public String createDiaryForm(Model model) {
        model.addAttribute("diary", new SaveRequest());
        return "post/create";
    }

    @PostMapping("/new")
    public String createDiary(@Valid @ModelAttribute("diary") SaveRequest request,
                              BindingResult bindingResult, @CurrentUser String email) {

        diaryService.saveDiary(email, request);
        System.out.println(request.toString());
        if (bindingResult.hasErrors()) {
            return "post/create";
        }
        return "index";
    }

}
