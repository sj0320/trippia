package com.trippia.travel.controller;

import com.trippia.travel.controller.dto.place.response.PlaceDetailsResponse;
import com.trippia.travel.domain.location.place.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/travel/places")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{placeId}")
    public String getPlaceDetails(@PathVariable String placeId, Model model){
        PlaceDetailsResponse place = placeService.getPlaceDetails(placeId);
        model.addAttribute("place", place);
        return "place/details";
    }

}
