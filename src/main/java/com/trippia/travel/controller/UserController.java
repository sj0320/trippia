package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.user.requset.SocialSaveRequest;
import com.trippia.travel.controller.dto.user.requset.UpdatePasswordRequest;
import com.trippia.travel.controller.dto.user.requset.UserSaveRequest;
import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.domain.user.UserService;
import com.trippia.travel.exception.BaseException;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.mail.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.trippia.travel.exception.ErrorMessageSource.USER_NOT_FOUND_MESSAGE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
            log.info("[회원가입 시도] email={}, nickname={}", request.getEmail(), request.getNickname());
            mailService.isEmailVerified(request.getEmail(), EmailAuthPurpose.REGISTER);
            userService.saveUser(request);
            log.info("[회원가입 성공] email={}", request.getEmail());
        } catch (UserException e) {
            log.warn("[회원가입 실패] email={}, code={}, field={}", request.getEmail(), e.getCode(), e.getFieldName());
            bindingResult.rejectValue(e.getFieldName(), e.getCode());
            return "user/sign-up";
        }
        return "index";
    }

    @GetMapping("/sns-sign-up")
    public String snsSignUpForm(@RequestParam("email") String email, @RequestParam("socialType") String socialType,
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
        try{
            userService.saveSocialUser(request.getEmail(), request.getNickname());
        } catch (UserException e){
            bindingResult.rejectValue(e.getFieldName(), e.getCode());
            return "user/social-sign-up";
        }

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

    @GetMapping("/password/new")
    public String getNewPasswordForm() {
        return "user/new-password";
    }

    @GetMapping("/password/update")
    public String getPasswordUpdateForm(Model model) {
        model.addAttribute("updatePasswordRequest", new UpdatePasswordRequest());
        return "user/update-password";
    }

    @PatchMapping("/password/update")
    public String updatePassword(@CurrentUser String email,
                                 @Valid @ModelAttribute("updatePasswordRequest") UpdatePasswordRequest request,
                                 BindingResult bindingResult,
                                 Model model) {
        log.info("[비밀번호 변경 시도] email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND_MESSAGE));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("[비밀번호 변경 실패] 기존 비밀번호 불일치 email={}", email);
            bindingResult.rejectValue("currentPassword", "invalid.password");
            return "user/update-password";
        }

        if (bindingResult.hasErrors()) {
            return "user/update-password";
        }

        userService.updatePassword(email, request.getNewPassword());
        log.info("[비밀번호 변경 성공] email={}", email);

        return "redirect:/users/" + user.getId();
    }

    @DeleteMapping("/delete")
    public String deleteUser(@CurrentUser String email, HttpServletRequest request) {
        log.info("[회원 탈퇴 시도] email={}", email);
        userService.deleteUser(email);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();
        log.info("[회원 탈퇴 완료] email={}", email);

        return "redirect:/";
    }


}
