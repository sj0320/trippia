package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.diarycomment.requset.DiaryCommentSaveRequest;
import com.trippia.travel.domain.diarypost.diarycomment.DiaryCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DiaryCommentApiController {

    private final DiaryCommentService diaryCommentService;

    @PostMapping("/diary/{diaryId}")
    public ResponseEntity<?> saveDiaryComment(
            @CurrentUser String email,
            @PathVariable Long diaryId,
            @RequestBody DiaryCommentSaveRequest request) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 기능입니다."));
        }
        return ResponseEntity.ok(diaryCommentService.saveDiaryComment(email, diaryId, request));
    }
}
