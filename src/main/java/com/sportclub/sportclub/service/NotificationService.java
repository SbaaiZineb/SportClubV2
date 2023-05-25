package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Notification;
import com.sportclub.sportclub.entities.UserApp;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationByUser(UserApp userApp);
    void addNotification(Notification notification);
}
