package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.diary.request.CursorData;
import com.trippia.travel.controller.dto.diary.request.DiarySaveRequest;
import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import com.trippia.travel.controller.dto.diary.request.DiaryUpdateRequest;
import com.trippia.travel.controller.dto.diary.response.DiaryDetailResponse;
import com.trippia.travel.controller.dto.diary.response.DiaryEditFormResponse;
import com.trippia.travel.controller.dto.diary.response.DiaryListResponse;
import com.trippia.travel.controller.dto.diary.response.DiaryListViewModel;
import com.trippia.travel.domain.common.SortOption;
import com.trippia.travel.domain.diarypost.diary.DiaryService;
import com.trippia.travel.domain.diarypost.likes.LikeService;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.theme.ThemeService;
import com.trippia.travel.domain.user.UserService;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.exception.file.FileException;
import com.trippia.travel.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
@Slf4j
public class DiaryController {

    private final CityService cityService;
    private final ThemeService themeService;
    private final DiaryService diaryService;
    private final LikeService likeService;
    private final CountryRepository countryRepository;
    private final FileService fileService;
    private final UserService userService;


    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("cities", cityService.getCitiesGroupedByType());
        model.addAttribute("themes", themeService.getAllThemes());
        model.addAttribute("countries", countryRepository.findAll());
    }

    @GetMapping("/new")
    public String createDiaryForm(Model model) {
        model.addAttribute("diary", new DiarySaveRequest());
        return "diary/create";
    }

    @PostMapping("/new")
    public String saveDiary(@Valid @ModelAttribute("diary") DiarySaveRequest request,
                            BindingResult bindingResult, @RequestParam("thumbnail") MultipartFile thumbnail,
                            @CurrentUser String email, Model model) {

        String thumbnailUrl = null;
        if (!thumbnail.isEmpty()) {
            thumbnailUrl = fileService.uploadFile(thumbnail).getUrl();
            model.addAttribute("thumbnailUrl", thumbnailUrl);
        }

        if (bindingResult.hasErrors()) {
            return "diary/create";
        }
        try {
            Long diaryId = diaryService.saveDiary(email, request, thumbnail);
            return "redirect:/diary/" + diaryId;
        } catch (DiaryException | FileException e) {
            bindingResult.rejectValue(e.getFieldName(), e.getCode());
            return "diary/create";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 초기 페이지 렌더링
    @GetMapping("/list")
    public String getDiaryList(
            @ModelAttribute DiarySearchCondition searchCondition,
            @PageableDefault(size = 9) Pageable pageable,
            Model model
    ) {
        System.out.println(searchCondition.getThemeName());
        System.out.println(searchCondition.getThemeId());
        Sort sortOption = SortOption.from(searchCondition.getSort()).getSort();
        Pageable sortedpPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

        Slice<DiaryListResponse> diaryList = diaryService.searchDiaryList(searchCondition, CursorData.init(), sortedpPageable);
        DiaryListViewModel diaryListViewModel = DiaryListViewModel.createDiaryListViewModel(
                diaryList.getContent(), searchCondition, diaryList.hasNext());

        model.addAttribute("diaryListModel", diaryListViewModel);
        model.addAttribute("searchCondition", searchCondition);

        // 커서 정보 설정
        if (!diaryList.isEmpty()) {
            DiaryListResponse last = diaryList.getContent().get(diaryList.getContent().size() - 1);
            CursorData cursorData = CursorData.of(last.getId(), last.getCreatedAt(), last.getLikeCount(), last.getViewCount());
            model.addAttribute("cursorData", cursorData);
        }

        return "diary/list";
    }

    // API (무한스크롤용)
    @GetMapping("/list/data")
    @ResponseBody
    public Slice<DiaryListResponse> getDiaryListData(
            @ModelAttribute DiarySearchCondition searchCondition,
            @ModelAttribute CursorData cursorData,
            @PageableDefault(size = 9) Pageable pageable
    ) {
        System.out.println(searchCondition.getCountryName());
        System.out.println(searchCondition.getCountryId());
        Sort sortOption = SortOption.from(searchCondition.getSort()).getSort();
        Pageable sortedpPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);
        return diaryService.searchDiaryList(searchCondition, cursorData, sortedpPageable);
    }

    @GetMapping("/{id}")
    public String getDiaryDetails(@CurrentUser String email, @PathVariable Long id, Model model,
                                  HttpServletRequest request) {
        DiaryDetailResponse diaryDetails = diaryService.getDiaryDetails(id);
        diaryService.addViewCount(id, request.getRemoteAddr(), request.getHeader("User-Agent"));
        model.addAttribute("diary", diaryDetails);
        model.addAttribute("isLiked", likeService.isLikedByDiary(email, id));
        model.addAttribute("currentUserProfile", userService.getProfileImageUrl(email));
        return "diary/details";
    }

    @DeleteMapping("/{id}")
    public String deleteDiary(@CurrentUser String email, @PathVariable Long id) {
        diaryService.deleteDiary(email, id);
        return "redirect:/diary/list";
    }

    @GetMapping("/{id}/edit")
    public String editDiaryForm(@CurrentUser String email, @PathVariable Long id, Model model) {
        DiaryEditFormResponse diary = diaryService.getEditForm(email, id);
        model.addAttribute("diary", diary);
        return "diary/edit";
    }

    @PutMapping("/{id}")
    public String editDiary(@CurrentUser String email, @PathVariable Long id,
                            @Valid @ModelAttribute("diary") DiaryUpdateRequest request,
                            BindingResult bindingResult, @RequestParam("thumbnail") MultipartFile thumbnail) throws IOException {
        if (bindingResult.hasErrors()) {
            return "diary/edit";
        }
        diaryService.editDiary(email, id, request, thumbnail);
        return "redirect:/diary/" + id;
    }


}
