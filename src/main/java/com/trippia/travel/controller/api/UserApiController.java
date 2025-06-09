package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.user.requset.CheckEmailRequest;
import com.trippia.travel.controller.dto.user.requset.FindPasswordRequest;
import com.trippia.travel.controller.dto.user.requset.UserInfoUpdateRequest;
import com.trippia.travel.controller.dto.user.response.CheckEmailResponse;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PatchMapping("/edit")
    public ResponseEntity<Void> updateUserInfo(@CurrentUser String email,
                                               @Valid @ModelAttribute UserInfoUpdateRequest request,
                                               @RequestParam(value = "profileImage", required = false) MultipartFile profileImage){
        userService.updateUserInfo(email, request, profileImage);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody CheckEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "EMAIL_NOT_FOUND"));
        }

        return ResponseEntity.ok(new CheckEmailResponse(user.getLoginType().name()));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody FindPasswordRequest request){
        userService.updatePassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }




}
