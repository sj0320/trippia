package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.MemoDto;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemService;
import com.trippia.travel.domain.travel.scheduleitem.memo.MemoService;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.trippia.travel.controller.dto.MemoDto.*;
import static com.trippia.travel.controller.dto.MemoDto.MemoSaveRequest;
import static com.trippia.travel.controller.dto.ScheduleItemDto.*;
import static com.trippia.travel.controller.dto.SchedulePlaceDto.SchedulePlaceSaveRequest;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelApiController {

    private final MemoService memoService;
    private final SchedulePlaceService schedulePlaceService;
    private final ScheduleItemService scheduleItemService;

    @PostMapping("/memo")
    public ResponseEntity<ScheduleItemIdResponse> saveMemo(@CurrentUser String email, @RequestBody MemoSaveRequest request) {
        Long scheduleItemId = memoService.saveMemo(email, request);
        return ResponseEntity.ok(new ScheduleItemIdResponse(scheduleItemId));
    }

    @PostMapping("/place")
    public ResponseEntity<ScheduleItemIdResponse> savePlace(@CurrentUser String email, @RequestBody SchedulePlaceSaveRequest request) {
        Long scheduleItemId = schedulePlaceService.savePlace(email, request);
        return ResponseEntity.ok(new ScheduleItemIdResponse(scheduleItemId));
    }

    @GetMapping("/schedule-items")
    public ResponseEntity<List<ScheduleItemResponse>> getScheduleItems(@CurrentUser String email, @RequestParam Long scheduleId) {
        return ResponseEntity.ok(scheduleItemService.getScheduleItemsByScheduleId(email, scheduleId));
    }

    @DeleteMapping("/{scheduleItemId}")
    public ResponseEntity<Void> deleteScheduleItem(@CurrentUser String email, @PathVariable Long scheduleItemId) {
        scheduleItemService.deleteScheduleItem(email, scheduleItemId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/schedule-item/{scheduleItemId}/meta")
    public ResponseEntity<Void> updateScheduleItemMeta(@CurrentUser String email, @PathVariable Long scheduleItemId,
                                                       @Valid @RequestBody ScheduleItemMetaRequest request) {
        scheduleItemService.updateScheduleItemMeta(email, scheduleItemId, request.getExpectedCost(), request.getExecutionTime());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/memo/{scheduleItemId}")
    public ResponseEntity<Void> updateMemo(@CurrentUser String email, @PathVariable Long scheduleItemId,
                                           @Valid @RequestBody MemoUpdateRequest request){
        scheduleItemService.updateMemo(email, scheduleItemId, request.getContent());
        return ResponseEntity.ok().build();
    }


}
