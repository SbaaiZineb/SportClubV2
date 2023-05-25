package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Notification;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.service.AdminService;
import com.sportclub.sportclub.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class NotificationRestController {
    private final NotificationService notificationService;
    private AdminService adminService;

    public NotificationRestController(NotificationService notificationService,AdminService adminService) {
        this.notificationService = notificationService;
        this.adminService=adminService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications(Authentication authentication) {
        String username = authentication.getName();
        UserApp user = adminService.loadUserByUsername(username); // Replace with your Coach retrieval logic
        List<Notification> notifications = notificationService.getNotificationByUser(user);
        return ResponseEntity.ok(notifications);
    }
}
