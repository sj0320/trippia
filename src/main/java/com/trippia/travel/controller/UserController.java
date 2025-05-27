package com.trippia.travel.controller;

import com.trippia.travel.controller.dto.user.requset.SocialSaveRequest;
import com.trippia.travel.controller.dto.user.requset.UserSaveRequest;
import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.domain.user.UserService;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.mail.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "redirect", required = false) String redirect,
                        Model model) {
        model.addAttribute("redirect", redirect);
        return "user/login";
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("user", new UserSaveRequest());
        return "user/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute("user") UserSaveRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/sign-up";
        }
        try {
            mailService.isEmailVerified(request.getEmail(), EmailAuthPurpose.REGISTER);
            userService.saveUser(request);
        } catch (UserException e) {
            bindingResult.rejectValue(e.getFieldName(), e.getCode());
            return "user/sign-up";
        }
        return "index";
    }

    @GetMapping("/sns-sign-up")
    public String snsSignUpForm(@RequestParam("email") String email, @RequestParam("socialType")String socialType,
                                Model model) {
        SocialSaveRequest request = new SocialSaveRequest();
        request.setEmail(email);
        request.setSocialType(socialType);
        model.addAttribute("user", request);
        return "user/social-sign-up";
    }

    @PostMapping("/sns-sign-up")
    public String snsSignUp(@Valid @ModelAttribute("user") SocialSaveRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/social-sign-up";
        }
        userService.saveSocialUser(request.getEmail(), request.getNickname());
        return "redirect:/oauth2/authorization/" + request.getSocialType();
    }

    @GetMapping("/select-login-method")
    public String selectLoginMethod(@RequestParam("email") String email,
                                    @RequestParam("previousType") String previousType,
                                    @RequestParam("newType") String newType,
                                    Model model) {
        model.addAttribute("email", email);
        model.addAttribute("previousType", previousType);
        model.addAttribute("newType", newType);
        return "user/select-login-method";
    }




}
