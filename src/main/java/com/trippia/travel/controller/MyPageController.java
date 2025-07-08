package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.user.response.MyPageResponse;
import com.trippia.travel.domain.user.MyPageService;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.trippia.travel.exception.ErrorMessageSource.USER_NOT_FOUND_MESSAGE;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class MyPageController {

    private final MyPageService myPageService;
    private final UserRepository userRepository;


    @GetMapping("/{userId}")
    public String myPageForm(@CurrentUser String email, @PathVariable Long userId, Model model) {
        log.info("[마이페이지 진입] 요청자 email={}, 조회 대상 userId={}", email, userId);

        MyPageResponse myPage = myPageService.getMyPageInfo(userId);
        boolean isOwner = false;

        if (email != null) {
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("[사용자 조회 실패] email={}", email);
                        return new UserException(USER_NOT_FOUND_MESSAGE);
                    });

            isOwner = userId.equals(currentUser.getId());
            log.info("[마이페이지 접근자 검증] 현재 사용자 ID={}, isOwner={}", currentUser.getId(), isOwner);
        } else {
            log.info("[비회원 마이페이지 접근] userId={}", userId);
        }

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("myPage", myPage);
        return "user/mypage";
    }
}
