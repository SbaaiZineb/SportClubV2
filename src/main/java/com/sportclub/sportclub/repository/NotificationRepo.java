package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Notification;
import com.sportclub.sportclub.entities.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification,Long> {
    List<Notification> findByRecipientIn(List<UserApp> user);

}
