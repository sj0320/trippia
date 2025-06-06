package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.post.request.CompanionPostSaveRequest;
import com.trippia.travel.controller.dto.post.request.CompanionPostUpdateRequest;
import com.trippia.travel.controller.dto.post.request.PostSearchCondition;
import com.trippia.travel.controller.dto.post.response.CompanionPostDetailsResponse;
import com.trippia.travel.controller.dto.post.response.CompanionPostEditFormResponse;
import com.trippia.travel.domain.companionpost.post.CompanionPostService;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.user.UserService;
import com.trippia.travel.exception.companionpost.CompanionPostException;
import com.trippia.travel.exception.file.FileException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/companion-post")
@RequiredArgsConstructor
@Slf4j
public class CompanionPostController {

    private final CityService cityService;
    private final CountryRepository countryRepository;
    private final CompanionPostService companionPostService;
    private final UserService userService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("cities", cityService.getCitiesGroupedByType());
        model.addAttribute("countries", countryRepository.findAll());
    }


    @GetMapping("/list")
    public String getCompanionPosts(@ModelAttribute PostSearchCondition condition, Model model) {
        model.addAttribute("condition", condition);
        return "post/list";
    }

    @GetMapping("/new")
    public String createPostForm(Model model) {
        model.addAttribute("post", new CompanionPostSaveRequest());
        return "post/create";
    }

    @PostMapping("/new")
    public String savePost(@Valid @ModelAttribute("post") CompanionPostSaveRequest request,
                           BindingResult bindingResult, @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                           @CurrentUser String email, Model model) {

        if (bindingResult.hasErrors()) {
            return "post/create";
        }

        try {
            Long postId = companionPostService.savePost(email, request, thumbnail);
            return "redirect:/companion-post/" + postId;
        } catch (CompanionPostException | FileException e) {
            bindingResult.rejectValue(e.getFieldName(), e.getCode());
            return "post/create";
        }
    }

    @GetMapping("/{id}")
    public String getPostDetails(@CurrentUser String email, @PathVariable Long id, Model model,
                                 HttpServletRequest request) {
        CompanionPostDetailsResponse response = companionPostService.getPostDetails(id);
        companionPostService.addViewCount(id, request.getRemoteAddr(), request.getHeader("User-Agent"));
        model.addAttribute("post", response);
        model.addAttribute("currentUserProfile", userService.getProfileImageUrl(email));
        return "post/details";
    }

    @GetMapping("/{id}/edit")
    public String getPostEditForm(@CurrentUser String email, @PathVariable Long id, Model model) {
        CompanionPostEditFormResponse response = companionPostService.getPostEditForm(email, id);
        model.addAttribute("post", response);
        return "post/edit";
    }

    @PutMapping("/{id}/edit")
    public String editPost(@CurrentUser String email, @PathVariable Long id,
                           @Valid @ModelAttribute("post") CompanionPostUpdateRequest request,
                           BindingResult bindingResult,
                           @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) {

        if(bindingResult.hasErrors()){
            return "post/edit";
        }
        companionPostService.editPost(email, id, request, thumbnail);
        return "redirect:/companion-post/" + id;
    }

    @DeleteMapping("/{id}")
    public String deletePost(@CurrentUser String email, @PathVariable Long id){
        companionPostService.deletePost(email, id);
        return "redirect:/companion-post/list";
    }

}
