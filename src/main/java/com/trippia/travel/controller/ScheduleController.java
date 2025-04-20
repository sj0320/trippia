package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.location.city.CityService;
import com.trippia.travel.domain.location.country.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule")
@Slf4j
public class ScheduleController {

    private final CityService cityService;
    private final CountryRepository countryRepository;

    @GetMapping("/question/city")
    public String selectCityForm(Model model){
        model.addAttribute("cities", cityService.getCitiesGroupedByType());
        return "schedule/select-city";
    }

    @GetMapping("/question/date")
    public String selectDateForm(@RequestParam("selectedCity") String selectedCity, Model model){
        model.addAttribute("selectedCity", selectedCity);
        return "schedule/calendar";
    }

    @PostMapping("/new")
    public String createSchedule(@CurrentUser String email,
                                 @RequestParam String selectedCity,
                                 @RequestParam String startDate,
                                 @RequestParam String endDate
                                    ,Model model){
        log.info("city={}, startDate={}, endDate={}", selectedCity, startDate, endDate);
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<LocalDate> dateList = start.datesUntil(end.plusDays(1))
                .toList();

        // schedule 만드는 로직


        // schedule model에 담는 로직

        model.addAttribute("selectedCity", selectedCity);
        model.addAttribute("dateList", dateList);
        return "schedule/schedule-form";
    }

//    @GetMapping("/places/recommend")



}
