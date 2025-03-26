package com.trippia.travel.controller;

import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.domain.user.UserService;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.mail.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.trippia.travel.domain.user.dto.UserDto.SaveRequest;
import static com.trippia.travel.mail.MailDto.MailRequest;
import static com.trippia.travel.mail.MailDto.MailVerificationRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    @GetMapping("/users/naver-login")
    public String naverLogin() {
        return "redirect:https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=bVjOZuT6ZT3FK3cU_4i6&scope=email&state=E3C5ezwN9Y9vZKlzlYZeQqKSD3Vk7hLcpPH0TPOXmrU%3D&redirect_uri=http://localhost:8080/login/oauth2/code/naver";  // 네이버 OAuth2 인증 경로로 리디렉션
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("user", new SaveRequest());
        return "auth/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid @ModelAttribute("user") SaveRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/sign-up";
        }
        try {
            mailService.isEmailVerified(request.getEmail(), EmailAuthPurpose.REGISTER);
            userService.saveUser(request);
        } catch (UserException e) {
            bindingResult.rejectValue(e.getField(), e.getCode());
            return "auth/sign-up";
        }
        return "index";
    }

    @GetMapping("/sns-sign-up")
    public String snsSignUpForm(@RequestParam("email")String email, Model model) {
        model.addAttribute("email",email);
        return "auth/social-sign-up";
    }

    @GetMapping("/select-login-method")
    public String selectLoginMethod(@RequestParam("email") String email,
                                    @RequestParam("previousType") String previousType,
                                    @RequestParam("newType") String newType,
                                    Model model) {
        model.addAttribute("email", email);
        model.addAttribute("previousType", previousType);
        model.addAttribute("newType", newType);
        return "auth/select-login-method";
    }


    @PostMapping("/email/send-code")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendEmailCode(@ModelAttribute MailRequest mailRequest) {
        userService.sendCodeToEmail(mailRequest.getEmail(), mailRequest.getPurpose());

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }


    @PostMapping("/email/verify-code")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyEmailCode(@ModelAttribute MailVerificationRequest verificationRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            mailService.verifyEmailCode(
                    verificationRequest.getEmail(),
                    verificationRequest.getPurpose(),
                    verificationRequest.getCode()
            );
            response.put("status", "success");
        } catch (UserException e) {
            response.put("status", "fail");
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }


}
