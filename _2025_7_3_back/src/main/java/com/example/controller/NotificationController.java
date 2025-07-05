package com.example.controller;

import com.example.entity.Notification;
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
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestParam String username) {
        System.out.println(username);
        List<Notification> notifications = notificationService.getAllNotificationsForCurrentUser(username);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification createdNotification = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification updatedNotification = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(updatedNotification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
