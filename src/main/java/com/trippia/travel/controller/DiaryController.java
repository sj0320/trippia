package com.trippia.travel.controller;

import com.trippia.travel.domain.location.city.CityDto.CityGroupedByTypeResponse;
import com.trippia.travel.domain.location.city.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final CityService cityService;

    @GetMapping("/new")
    public String createDiaryForm(Model model){
        CityGroupedByTypeResponse cities = cityService.getCitiesGroupedByType();
        model.addAttribute("cities", cities);
        return "post/create";
    }


}
