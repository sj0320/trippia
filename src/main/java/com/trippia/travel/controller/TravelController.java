package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.location.place.PlaceService;
import com.trippia.travel.domain.travel.plan.PlanService;
import com.trippia.travel.util.PlaceTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

import static com.trippia.travel.controller.dto.PlaceDto.RecommendPlaceResponse;
import static com.trippia.travel.controller.dto.PlanDto.PlanCreateRequest;
import static com.trippia.travel.controller.dto.PlanDto.PlanDetailsResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/travel")
@Slf4j
public class TravelController {

    private final CityService cityService;
    private final PlanService planService;
    private final PlaceService placeService;

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
        model.addAttribute("plan", plan);

        return "plan/plan-form";
    }

    @GetMapping("/places/recommend")
    @ResponseBody
    public Set<RecommendPlaceResponse> getRecommendPlace(@RequestParam List<Long> cityIds,
                                                         @RequestParam(required = false) String query) {
        log.info("cityIds={}", cityIds.toString());
        if (query == null || query.isBlank()) {
            log.info("검색창에 아무런 입력 없을 경우");
            return placeService.getRecommendPlacesByType(cityIds, "관광");
        }

        String type = PlaceTypeMapper.convertToGoogleType(query);
        if(type!=null){
            log.info("검색창에 지정한 type을 그대로 입력한 경우 : 자동검색 + type에 대한 값");
            return placeService.getRecommendPlacesByType(cityIds, query);
        }

        log.info("국가에 속한 지역 자동 검색");
        return placeService.getAutocompletePlaces(cityIds, query);
    }


}
