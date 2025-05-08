package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemService;
import com.trippia.travel.domain.travel.scheduleitem.memo.MemoService;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.trippia.travel.controller.dto.MemoDto.MemoSaveRequest;
import static com.trippia.travel.controller.dto.ScheduleItemDto.ExpectedCostRequest;
import static com.trippia.travel.controller.dto.ScheduleItemDto.ScheduleItemIdResponse;
import static com.trippia.travel.controller.dto.SchedulePlaceDto.SchedulePlaceSaveRequest;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelApiController {

    private final MemoService memoService;
    private final SchedulePlaceService schedulePlaceService;
    private final ScheduleItemService scheduleItemService;

    @PostMapping("/memo")
    public ResponseEntity<ScheduleItemIdResponse> saveMemo(@CurrentUser String email, @RequestBody MemoSaveRequest request){
        Long scheduleItemId = memoService.saveMemo(email, request);
        return ResponseEntity.ok(new ScheduleItemIdResponse(scheduleItemId));
    }

    @PostMapping("/place")
    public ResponseEntity<ScheduleItemIdResponse> savePlace(@CurrentUser String email, @RequestBody SchedulePlaceSaveRequest request){
        Long scheduleItemId = schedulePlaceService.savePlace(email, request);
        return ResponseEntity.ok(new ScheduleItemIdResponse(scheduleItemId));
    }

    @PatchMapping("/{scheduleItemId}/expected-cost")
    public ResponseEntity<Void> saveExpectedCost(@CurrentUser String email, @PathVariable Long scheduleItemId,
                                                 @RequestBody ExpectedCostRequest request){
        scheduleItemService.updateExpectedCost(email, scheduleItemId, request.getExpectedCost());
        return ResponseEntity.ok().build();
    }


}
