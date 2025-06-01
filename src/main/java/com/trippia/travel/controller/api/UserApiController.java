package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.user.requset.UserInfoUpdateRequest;
import com.trippia.travel.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PatchMapping("/edit")
    public ResponseEntity<Void> updateUserInfo(@CurrentUser String email,
                                               @Valid @ModelAttribute UserInfoUpdateRequest request,
                                               @RequestParam(value = "profileImage", required = false) MultipartFile profileImage){
        userService.updateUserInfo(email, request, profileImage);
        return ResponseEntity.ok().build();
    }

}
