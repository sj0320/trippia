package com.trippia.travel.controller.api;

import com.trippia.travel.domain.location.place.PlaceService;
import com.trippia.travel.ratelimiter.RateLimiterService;
import com.trippia.travel.util.PlaceTypeMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@Slf4j
@RequiredArgsConstructor
public class PlaceApiController {

    private final PlaceService placeService;
    private final RateLimiterService rateLimiterService;

    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendPlace(@RequestParam(name = "cityIds", required = false) List<Long> cityIds,
                                                         @RequestParam(required = false) String query,
                                                         HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimiterService.resolveBucket(ip);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
        }

        if (cityIds == null || cityIds.isEmpty()) {
            log.info("글로벌 자동검색 실행");
            return ResponseEntity.ok(placeService.getAutocompletePlace(query));
        }

        if (query == null || query.isBlank()) {
            log.info("검색창에 아무런 입력 없을 경우");
            return ResponseEntity.ok(placeService.getRecommendPlacesByType(cityIds, "관광"));
        }

        String type = PlaceTypeMapper.convertToGoogleType(query);
        if (type != null) {
            log.info("검색창에 지정한 type을 그대로 입력한 경우 : 자동검색 + type에 대한 값");
            return ResponseEntity.ok(placeService.getRecommendPlacesByType(cityIds, query));
        }

        log.info("국가에 속한 지역 자동 검색");
        return ResponseEntity.ok(placeService.getAutocompletePlaces(cityIds, query));
    }

}
