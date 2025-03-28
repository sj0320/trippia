package com.trippia.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    @GetMapping("/new")
    public String createDiaryForm(){
        return "post/createForm";
    }

}
