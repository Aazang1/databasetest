package com.example.controller;

import com.example.entity.Notification;
import com.example.entity.NotificationReceiver;
import com.example.entity.dto.NotificationDTO;
import com.example.entity.dto.NotificationWithStatusDTO;
import com.example.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationWithStatusDTO>> getAllNotifications(@RequestParam String username) {
        List<NotificationWithStatusDTO> notifications = notificationService.getAllNotificationsForCurrentUser(username);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDTO notificationDTO) {
        Notification createdNotification = notificationService.createNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationReceiver> markAsRead(@PathVariable Long id) {
        NotificationReceiver updatedNotification = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(updatedNotification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
