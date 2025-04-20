package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.travel.plan.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.location.place.PlaceDto.PlaceRecommendResponse;
import static com.trippia.travel.domain.travel.plan.PlanDto.PlanCreateRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/travel")
@Slf4j
public class TravelController {

    private final CityService cityService;
    private final PlanService planService;

    @GetMapping("/question/city")
    public String selectCityForm(Model model){
        model.addAttribute("cities", cityService.getCitiesGroupedByType());
        return "schedule/select-city";
    }

    @GetMapping("/question/date")
    public String selectDateForm(@RequestParam("cityId") List<Long> cityIds, Model model){
        model.addAttribute("cityIds", cityIds);
        return "schedule/calendar";
    }

    @PostMapping("/plan/new")
    public String createSchedule(@CurrentUser String email,
                                 @ModelAttribute PlanCreateRequest request, Model model){

        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());

        log.info("request.startDate{}", request.getStartDate());
        log.info("request.cityId={}",request.getCityIds());

        List<LocalDate> dateList = startDate.datesUntil(endDate.plusDays(1))
                .toList();
        model.addAttribute("dateList", dateList);

        planService.createPlan(email, request);

        model.addAttribute("cityIds", request.getCityIds());

        return "schedule/schedule-form";
    }

    @GetMapping("/places/recommend")
    @ResponseBody
    public PlaceRecommendResponse getRecommendPlace(@RequestParam List<Long> cityIds){
        log.info("cityIds={}", cityIds.toString());
        return new PlaceRecommendResponse();
    }



}
