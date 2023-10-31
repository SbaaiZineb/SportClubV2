package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Notification;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.repository.NotificationRepo;
import com.sportclub.sportclub.service.AdminService;
import com.sportclub.sportclub.service.GymService;
import com.sportclub.sportclub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class NotificationController {
    private final NotificationService notificationService;
    private AdminService adminService;
    private AdminRepo adminRepo;
    private GymService gymService;
    @Autowired
    NotificationRepo notificationRepo;

    public NotificationController(NotificationService notificationService,AdminService adminService,AdminRepo adminRepo,GymService gymService) {
        this.notificationService = notificationService;
        this.adminService=adminService;
        this.adminRepo=adminRepo;
        this.gymService=gymService;
    }

   /* @GetMapping("/notifications")
    public List<Notification> getNotifications(Authentication authentication) {
        String username = authentication.getName();
        UserApp user = adminService.loadUserByUsername(username);
        List<Notification> notifications = notificationService.getNotificationByUser(user);
        return notifications;
    }*/
  @GetMapping("/notifications")
  public String getNotifications(Authentication authentication, Model model) {
      String username = authentication.getName();
      UserApp user = adminService.loadUserByUsername(username);
      /*List<UserApp> userAppList=new ArrayList<>();
      List<UserApp> userApps=adminRepo.findByRolesRoleNameContains("ADMIN");
      userAppList.add(user);*/

      List<Notification> notificationList=notificationRepo.findAll();
      for (Notification not:notificationList
           ) {
          for (UserApp u:not.getRecipient()
               ) {
              if (u.getEmail().equals(username)){
                  List<Notification> nots=u.getNotifications();
                  model.addAttribute("notifications",nots);
              }
          }
      }
      /*List<Notification> notifications = notificationService.getNotificationByUser(userAppList);
      model.addAttribute("notifications",notifications);*/
      System.out.println(notificationList);
      return "notifications";
  }
}
