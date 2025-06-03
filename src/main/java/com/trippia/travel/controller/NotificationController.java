package com.trippia.travel.controller;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.controller.dto.notification.response.NotificationResponse;
import com.trippia.travel.domain.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public String getNotifications(@CurrentUser String email, Model model) {
        List<NotificationResponse> notificationResponse = notificationService.getAllNotifications(email);
        model.addAttribute("notifications", notificationResponse);
        return "notification/list";
    }

}
