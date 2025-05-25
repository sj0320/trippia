package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.plan.request.PlanCreateRequest;
import com.trippia.travel.controller.dto.plan.response.PlanDetailsResponse;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.travel.plan.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/travel")
@Slf4j
public class TravelController {

    private final CityService cityService;
    private final PlanService planService;

    @GetMapping("/question/city")
    public String selectCityForm(Model model) {
        model.addAttribute("cities", cityService.getCitiesGroupedByType());
        return "plan/select-city";
    }

    @GetMapping("/question/date")
    public String selectDateForm(@RequestParam("cityId") List<Long> cityIds, Model model) {
        model.addAttribute("cityIds", cityIds);
        return "plan/calendar";
    }

    @PostMapping("/plan/new")
    public String createPlan(@CurrentUser String email, @ModelAttribute PlanCreateRequest request,
                             RedirectAttributes redirectAttributes) {

        Long planId = planService.createPlan(email, request);
        redirectAttributes.addAttribute("planId", planId);

        return "redirect:/travel/plan/{planId}";
    }

    @GetMapping("/plan/{planId}")
    public String planForm(@CurrentUser String email, @PathVariable Long planId, Model model){
        PlanDetailsResponse plan = planService.findPlan(email, planId);
        log.info("plan={}", plan.toString());

        model.addAttribute("plan", plan);

        return "plan/plan-form";
    }

}
