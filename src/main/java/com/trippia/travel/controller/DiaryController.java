package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.post.diary.DiaryService;
import com.trippia.travel.domain.theme.ThemeService;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.exception.file.FileException;
import com.trippia.travel.file.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final CityService cityService;
    private final ThemeService themeService;
    private final DiaryService diaryService;
    private final CountryRepository countryRepository;
    private final FileService fileService;


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
                              BindingResult bindingResult, @RequestParam("thumbnail") MultipartFile thumbnail,
                              @CurrentUser String email, Model model) {

        String thumbnailUrl = null;
        if (!thumbnail.isEmpty()) {
            thumbnailUrl = fileService.uploadFile(thumbnail).getUrl();
            model.addAttribute("thumbnailUrl", thumbnailUrl);
        }

        if (bindingResult.hasErrors()) {
            return "post/create";
        }
        try {
            Long diaryId = diaryService.saveDiary(email, request, thumbnail);
            return "redirect:/diary/" + diaryId;
        } catch (DiaryException | FileException e) {
            bindingResult.rejectValue(e.getFieldName(), e.getCode());
            return "post/create";
        }
    }

    @GetMapping("/list")
    public String getDiaryList(Model model) {
        List<DiaryListResponse> diaryList = diaryService.getDiaryList();
        model.addAttribute("diaryList", diaryList);
        return "post/list";
    }

    @GetMapping("/{id}")
    public String getDiaryDetails(@CurrentUser String email, @PathVariable Long id, Model model) {
        DiaryDetailResponse diaryDetails = diaryService.getDiaryDetail(id);
        model.addAttribute("diary", diaryDetails);
        model.addAttribute("authorEmail", email);
        return "post/details";
    }

    @DeleteMapping("/{id}")
    public String deleteDiary(@CurrentUser String email, @PathVariable Long id) {
        diaryService.deleteDiary(email, id);
        return "redirect:/diary/list";
    }

    @GetMapping("/{id}/edit")
    public String editDiaryForm(@CurrentUser String email, @PathVariable Long id, Model model) {
        EditFormResponse diary = diaryService.getEditForm(email, id);
        model.addAttribute("diary", diary);
        return "post/edit";
    }

    @PutMapping("/{id}")
    public String editDiary(@CurrentUser String email, @PathVariable Long id,
                            @ModelAttribute("diary") UpdateRequest request,
                            BindingResult bindingResult, @RequestParam("thumbnail") MultipartFile thumbnail) {
        if (bindingResult.hasErrors()) {
            return "/post/edit";
        }
        diaryService.editDiary(email, id, request, thumbnail);
        return "redirect:/diary/" + id;
    }


}
