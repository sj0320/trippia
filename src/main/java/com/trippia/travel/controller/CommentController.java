package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.comment.requset.CommentSaveRequest;
import com.trippia.travel.domain.post.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/diary/{diaryId}")
    public ResponseEntity<?> saveDiaryComment(
            @CurrentUser String email,
            @PathVariable Long diaryId,
            @RequestBody CommentSaveRequest request) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 기능입니다."));
        }
        return ResponseEntity.ok(commentService.saveDiaryComment(email, diaryId, request));
    }
}
