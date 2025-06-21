package com.trippia.travel.controller.api;

import com.trippia.travel.domain.user.UserService;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.trippia.travel.mail.MailDto.MailRequest;
import static com.trippia.travel.mail.MailDto.MailVerificationRequest;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class MailApiController {

    private final MailService mailService;
    private final UserService userService;

    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendEmailCode(@ModelAttribute MailRequest mailRequest) {
        userService.sendCodeToEmail(mailRequest.getEmail(), mailRequest.getPurpose());

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyEmailCode(@ModelAttribute MailVerificationRequest verificationRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            mailService.verifyEmailCode(
                    verificationRequest.getEmail(),
                    verificationRequest.getPurpose(),
                    verificationRequest.getCode()
            );
            response.put("status", "success");
        } catch (UserException e) {
            response.put("status", "fail");
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }


}
