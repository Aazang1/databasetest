package com.example.service.impl;

import com.example.dao.NotificationReceiverRepositroy;
import com.example.dao.NotificationRepository;
import com.example.dao.UserRepository;
import com.example.entity.Notification;
import com.example.entity.dto.NotificationDTO;
import com.example.entity.NotificationReceiver;
import com.example.entity.User;
import com.example.entity.dto.NotificationWithStatusDTO;
import com.example.service.NotificationService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationReceiverRepositroy notificationReceiverRepositroy ;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    ZonedDateTime beijingNow = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
    Date date = Date.from(beijingNow.toInstant());

    ZonedDateTime beijingTime = LocalDateTime.now()
            .atZone(ZoneId.of("Asia/Shanghai"));
    @Override
    public List<NotificationWithStatusDTO> getAllNotificationsForCurrentUser(String username) {
        return notificationRepository.findNotificationsWithStatus(username);
    }


    public List<NotificationDTO> getNotificationsWithReceivers(String senderid) {
        // 1. 查询发送者的所有通知
        List<Notification> notifications = notificationRepository.findBySenderidOrderByCreatedAtDesc(senderid);

        // 2. 收集所有接收者ID
        Set<String> allReceiverIds = new HashSet<>();
        notifications.forEach(n -> {
            List<NotificationReceiver> receivers = notificationReceiverRepositroy.findByNotificationid(n.getId());
            receivers.forEach(r -> allReceiverIds.add(r.getReceiverid()));
        });

        // 3. 批量查询接收者姓名
        Map<String, String> receiverIdToNameMap = new HashMap<>();
        if (!allReceiverIds.isEmpty()) {
            List<User> receivers = userRepository.findByUsernameIn(allReceiverIds);
            receivers.forEach(u -> receiverIdToNameMap.put(u.getUsername(), u.getName()));
        }

        // 4. 查询发送者姓名
        User sender = userRepository.findByUsername(senderid);
        String senderName = sender != null ? sender.getName() : "未知用户";

        // 5. 转换为DTO
        return notifications.stream().map(n -> {
            NotificationDTO dto = new NotificationDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setContent(n.getContent());
            dto.setType(n.getType());
            dto.setCreateAt(n.getCreatedAt());
            dto.setSenderid(n.getSenderid());
            dto.setSenderName(senderName);

            // 查询接收者列表
            List<String> receiverIds = notificationReceiverRepositroy.findByNotificationid(n.getId())
                    .stream()
                    .map(NotificationReceiver::getReceiverid)
                    .collect(Collectors.toList());
            dto.setReceiverid(receiverIds);

            // 设置接收者姓名
            List<String> receiverNames = receiverIds.stream()
                    .map(id -> receiverIdToNameMap.getOrDefault(id, "未知用户"))
                    .collect(Collectors.toList());
            dto.setReceiverNames(receiverNames);

            return dto;
        }).collect(Collectors.toList());
    }



    @Override
    public Notification createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setTitle(notificationDTO.getTitle());
        notification.setContent(notificationDTO.getContent());
        notification.setType(notificationDTO.getType());
        notification.setSenderid(notificationDTO.getSenderid());
        notification.setCreatedAt(date);
        notification.setSenderid(notificationDTO.getSenderid());

       Notification notification1= notificationRepository.save(notification);
        // 处理接收者
        System.out.println(notificationDTO.getReceiverid());

        for (String receiverId : notificationDTO.getReceiverid()) {
            NotificationReceiver receiver = new NotificationReceiver();
            receiver.setReceiverid(receiverId);
            receiver.setNotificationid(notification1.getId());
            receiver.setIs_read(false);
            notificationReceiverRepositroy.save(receiver);
        }

        return notification1;
    }

    @Override
    public NotificationReceiver markNotificationAsRead(Long id) {
        NotificationReceiver receiver = notificationReceiverRepositroy.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        receiver.setIs_read(true);
        receiver.setRead_time(date);


        return notificationReceiverRepositroy.save(receiver);
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
//        return notificationRepository.countByisReadAndReceiverid(false,username);
        return 0;
    }
}

