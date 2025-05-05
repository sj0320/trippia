package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.travel.scheduleitem.memo.MemoService;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.trippia.travel.controller.dto.MemoDto.MemoSaveRequest;
import static com.trippia.travel.controller.dto.SchedulePlaceDto.PlaceSaveRequest;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelApiController {

    private final MemoService memoService;
    private final SchedulePlaceService schedulePlaceService;

    @PostMapping("/memo")
    public ResponseEntity<Void> saveMemo(@CurrentUser String email, @RequestBody MemoSaveRequest request){
        memoService.saveMemo(email, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/place")
    public ResponseEntity<Void> savePlace(@CurrentUser String email, @RequestBody PlaceSaveRequest request){
        schedulePlaceService.savePlace(email, request);
        return ResponseEntity.ok().build();
    }
}
