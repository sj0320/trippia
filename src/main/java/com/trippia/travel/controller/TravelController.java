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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.trippia.travel.domain.location.place.PlaceDto.RecommendPlaceResponse;
import static com.trippia.travel.domain.travel.plan.PlanDto.PlanCreateRequest;

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
        return "schedule/select-city";
    }

    @GetMapping("/question/date")
    public String selectDateForm(@RequestParam("cityId") List<Long> cityIds, Model model) {
        model.addAttribute("cityIds", cityIds);
        return "schedule/calendar";
    }

    @PostMapping("/plan/new")
    public String createSchedule(@CurrentUser String email,
                                 @ModelAttribute PlanCreateRequest request, Model model) {

        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());

        log.info("request.startDate{}", request.getStartDate());
        log.info("request.cityId={}", request.getCityIds());

        List<LocalDate> dateList = startDate.datesUntil(endDate.plusDays(1))
                .toList();
        model.addAttribute("dateList", dateList);

        planService.createPlan(email, request);

        model.addAttribute("cityIds", request.getCityIds());

        return "schedule/schedule-form";
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
