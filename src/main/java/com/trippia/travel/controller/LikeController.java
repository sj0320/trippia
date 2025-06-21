package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.diarypost.likes.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/diary/{id}/like")
    public ResponseEntity<Map<String, Object>> likeDairy(@CurrentUser String email, @PathVariable Long id) throws Exception {
        System.out.println(email);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 기능입니다."));
        }

        int likeCount = likeService.likeDiary(email, id);
        return ResponseEntity.ok(Map.of("likeCount",likeCount));
    }

    @PostMapping("/diary/{id}/unlike")
    public ResponseEntity<Map<String, Object>> unlikeDairy(@CurrentUser String email, @PathVariable Long id){
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 기능입니다."));
        }

        int likeCount = likeService.unlikeDiary(email, id);
        return ResponseEntity.ok(Map.of("likeCount", likeCount));
    }
}
