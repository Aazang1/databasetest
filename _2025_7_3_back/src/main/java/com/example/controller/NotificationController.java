package com.example.controller;

import com.example.dao.NotificationRepository;
import com.example.dao.UserRepository;
import com.example.entity.Notification;
import com.example.entity.NotificationReceiver;
import com.example.entity.User;
import com.example.entity.dto.NotificationDTO;
import com.example.entity.dto.NotificationWithStatusDTO;
import com.example.service.NotificationService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationWithStatusDTO>> getAllNotifications(@RequestParam String username) {
        // 1. 获取用户的通知列表
        List<NotificationWithStatusDTO> notifications = notificationService.getAllNotificationsForCurrentUser(username);

        // 2. 提取所有发送者ID（去重）
        Set<String> senderIds = notifications.stream()
                .map(NotificationWithStatusDTO::getSenderid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 批量查询发送者信息
        Map<String, String> senderIdToNameMap = new HashMap<>();
        if (!senderIds.isEmpty()) {
            List<User> senders = userRepository.findByUsernameIn(senderIds);
            senders.forEach(user -> senderIdToNameMap.put(user.getUsername(), user.getName()));
        }

        // 4. 为每个通知设置发送者姓名
        notifications.forEach(notification -> {
            if (notification.getSenderid() != null) {
                notification.setSenderName(senderIdToNameMap.getOrDefault(notification.getSenderid(), "未知用户"));
            } else {
                notification.setSenderName("系统");
            }
        });

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


    @GetMapping("/{senderid}")
    public ResponseEntity<List<NotificationDTO>> getBySender(@PathVariable String senderid) {
        List<NotificationDTO> result = notificationService.getNotificationsWithReceivers(senderid);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/findnotification/{id}")
    public ResponseEntity<Notification> getById(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        System.out.println(id);
        return ResponseEntity.ok(notification);
    }
}
