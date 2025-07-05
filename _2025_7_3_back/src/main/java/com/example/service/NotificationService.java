package com.example.service;


import com.example.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotificationsForCurrentUser(String username);

    Notification createNotification(Notification notification);

    Notification markNotificationAsRead(Long id);

    void deleteNotification(Long id);

    long countUnreadNotificationsForCurrentUser();

    long countUnreadNotificationsForCurrentUser(String username);

}
