package com.trippia.travel.controller.api;

import com.trippia.travel.controller.dto.place.response.RecommendPlaceResponse;
import com.trippia.travel.domain.location.place.PlaceService;
import com.trippia.travel.util.PlaceTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/places")
@Slf4j
@RequiredArgsConstructor
public class PlaceApiController {

    private final PlaceService placeService;

    @GetMapping("/recommend")
    public Set<RecommendPlaceResponse> getRecommendPlace(@RequestParam(required = false) List<Long> cityIds,
                                                         @RequestParam(required = false) String query) {

        if (cityIds == null || cityIds.isEmpty()) {
            log.info("글로벌 자동검색 실행");
            return placeService.getAutocompletePlace(query);
        }

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
