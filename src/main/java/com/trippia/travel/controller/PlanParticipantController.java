package com.trippia.travel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/plan-participant")
@RequiredArgsConstructor
public class PlanParticipantController {

    @GetMapping
    public String getPlanParticipantForm(){
        return "plan/plan-participant-list";
    }

}
