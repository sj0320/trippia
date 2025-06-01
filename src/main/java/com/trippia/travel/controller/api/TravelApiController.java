package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.memo.requset.MemoSaveRequest;
import com.trippia.travel.controller.dto.memo.requset.MemoUpdateRequest;
import com.trippia.travel.controller.dto.plan.request.PlanUpdateRequest;
import com.trippia.travel.controller.dto.scheduleitem.requset.ScheduleItemMetaRequest;
import com.trippia.travel.controller.dto.scheduleitem.requset.ScheduleItemOrderRequest;
import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemIdResponse;
import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import com.trippia.travel.controller.dto.scheduleplace.request.SchedulePlaceSaveRequest;
import com.trippia.travel.domain.travel.plan.PlanService;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemService;
import com.trippia.travel.domain.travel.scheduleitem.memo.MemoService;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelApiController {

    private final PlanService planService;
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
                                           @Valid @RequestBody MemoUpdateRequest request) {
        scheduleItemService.updateMemo(email, scheduleItemId, request.getContent());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schedule-item/reorder")
    public ResponseEntity<Void> reorderItems(@CurrentUser String email, @RequestBody ScheduleItemOrderRequest request) {
        scheduleItemService.reorderItems(email, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/plan/{planId}")
    public ResponseEntity<Void> deletePlan(@CurrentUser String email, @PathVariable Long planId) {
        planService.deletePlan(email, planId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/plan/{planId}")
    public ResponseEntity<Void> editPlan(@CurrentUser String email, @PathVariable Long planId,
                                         @RequestBody PlanUpdateRequest request){
        planService.updatePlan(email, planId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/plan/{planId}/invite")
    public ResponseEntity<Void> invitePlan(@CurrentUser String email,
                                           @PathVariable Long planId,
                                           @RequestParam String nickname) {
        planService.invitePlan(email, planId, nickname);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/plan/{planId}/accept")
    public ResponseEntity<Void> acceptPlanInvite(@CurrentUser String email,
                                                 @PathVariable Long planId) {

        planService.acceptInvite(email, planId);
        return ResponseEntity.ok().build();
    }

}
