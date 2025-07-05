package com.example.service;


import com.example.entity.Notification;
import com.example.entity.NotificationReceiver;
import com.example.entity.dto.NotificationDTO;
import com.example.entity.dto.NotificationWithStatusDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationWithStatusDTO> getAllNotificationsForCurrentUser(String username);

    Notification createNotification(NotificationDTO notificationDTO);

    NotificationReceiver markNotificationAsRead(Long id);

    void deleteNotification(Long id);

    long countUnreadNotificationsForCurrentUser();

    long countUnreadNotificationsForCurrentUser(String username);

}
