package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.postcomment.request.CompanionPostCommentSaveRequest;
import com.trippia.travel.domain.companionpost.comment.CompanionPostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/companion-post")
@RequiredArgsConstructor
public class CompanionPostCommentApiController {

    private final CompanionPostCommentService companionPostCommentService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> saveDiaryComment(
            @CurrentUser String email,
            @PathVariable Long postId,
            @RequestBody CompanionPostCommentSaveRequest request) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 기능입니다."));
        }
        return ResponseEntity.ok(companionPostCommentService.savePostComment(email, postId, request));
    }

}
