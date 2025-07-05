package com.example.service.impl;

import com.example.dao.NotificationRepository;
import com.example.entity.Notification;
import com.example.entity.User;
import com.example.service.NotificationService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    LocalDate localDate = LocalDate.now();
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    @Override
    public List<Notification> getAllNotificationsForCurrentUser(String username) {
        User user = userService.getCurrentUser(username);
        return notificationRepository.findByReceiverid(username);
    }

    @Override
    public Notification createNotification(Notification notification) {
        User user = userService.getCurrentUser(notification.getReceiverid());
        notification.setCreatedAt(date);


        return notificationRepository.save(notification);
    }

    @Override
    public Notification markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notification.setReadAt(date);

        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notificationRepository.delete(notification);
    }

    @Override
    public long countUnreadNotificationsForCurrentUser() {
        return 0;
    }


    @Override
    public long countUnreadNotificationsForCurrentUser(String username) {
        User user = userService.getCurrentUser(username);
        return notificationRepository.countByisReadAndReceiverid(false,username);
    }
}

