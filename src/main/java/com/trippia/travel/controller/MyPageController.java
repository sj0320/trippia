package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.user.response.MyPageResponse;
import com.trippia.travel.domain.user.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public String myPageForm(@CurrentUser String email, Model model){
        MyPageResponse myPage = myPageService.getMyPageInfo(email);
        model.addAttribute("myPage", myPage);
        return "user/mypage";
    }
}
