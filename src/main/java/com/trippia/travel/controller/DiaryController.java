package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.post.diary.DiaryService;
import com.trippia.travel.domain.theme.ThemeService;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.exception.file.FileException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.DiaryListResponse;
import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final CityService cityService;
    private final ThemeService themeService;
    private final DiaryService diaryService;
    private final CountryRepository countryRepository;


    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("cities", cityService.getCitiesGroupedByType());
        model.addAttribute("themes", themeService.getAllThemes());
        model.addAttribute("countries", countryRepository.findAll());
    }

    @GetMapping("/new")
    public String createDiaryForm(Model model) {
        model.addAttribute("diary", new SaveRequest());
        return "post/create";
    }

    @PostMapping("/new")
    public String createDiary(@Valid @ModelAttribute("diary") SaveRequest request,
                              @RequestParam("thumbnail") MultipartFile thumbnail,
                              BindingResult bindingResult, @CurrentUser String email) {
        if (bindingResult.hasErrors()) {
            return "post/create";
        }
        try {
            diaryService.saveDiary(email, request, thumbnail);
        } catch (DiaryException | FileException e) {
            bindingResult.rejectValue(e.getFieldName(), e.getCode());
            return "post/create";
        }
        return "index";
    }

    @GetMapping("/list")
    public String getDiaryList(Model model) {
        List<DiaryListResponse> diaryList = diaryService.getDiaryList();
        model.addAttribute("diaryList", diaryList);
        return "post/list";
    }

}
