package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Notification;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.repository.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImp implements NotificationService{
    private  NotificationRepo notificationRepository;

    public NotificationServiceImp(NotificationRepo notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    @Override
    public List<Notification> getNotificationByUser(List<UserApp> userApp) {
        return notificationRepository.findByRecipientIn(userApp);
    }

    @Override
    public void addNotification(Notification notification) {
        notificationRepository.save(notification);
    }
}
