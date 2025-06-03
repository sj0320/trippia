package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.planparticipant.response.ReceivedPlanParticipantResponse;
import com.trippia.travel.controller.dto.planparticipant.response.RequestPlanParticipantResponse;
import com.trippia.travel.domain.travel.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan-participant")
@RequiredArgsConstructor
public class PlanParticipantApiController {

    private final PlanService planService;

    @GetMapping("/receive")
    public ResponseEntity<List<ReceivedPlanParticipantResponse>> getParticipantReceive(@CurrentUser String email) {
        List<ReceivedPlanParticipantResponse> response = planService.getReceivedParticipantRequests(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/request")
    public ResponseEntity<List<RequestPlanParticipantResponse>> getParticipantRequest(@CurrentUser String email) {
        List<RequestPlanParticipantResponse> response = planService.getSentParticipantRequests(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{planId}/invite")
    public ResponseEntity<Void> invitePlan(@CurrentUser String email,
                                           @PathVariable Long planId,
                                           @RequestParam String nickname) {
        planService.invitePlan(email, planId, nickname);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{planId}/accept")
    public ResponseEntity<Void> acceptPlanInvite(@CurrentUser String email,
                                                 @PathVariable Long planId) {
        planService.acceptPlanInvite(email, planId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{planId}/reject")
    public ResponseEntity<Void> rejectPlanInvite(@CurrentUser String email,
                                                 @PathVariable Long planId) {
        planService.rejectPlanInvite(email, planId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{planId}/cancel")
    public ResponseEntity<Void> cancelPlanInvite(@CurrentUser String email,
                                                 @PathVariable Long planId,
                                                 @RequestParam String nickname) {
        planService.cancelPlanInvite(email, planId, nickname);
        return ResponseEntity.ok().build();
    }


}
