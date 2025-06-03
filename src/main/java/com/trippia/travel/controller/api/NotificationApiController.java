package com.trippia.travel.controller.api;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping("/has-new")
    public ResponseEntity<Boolean> hasNewNotification(@CurrentUser String email){
        return ResponseEntity.ok(notificationService.hasNewNotification(email));
    }

}
