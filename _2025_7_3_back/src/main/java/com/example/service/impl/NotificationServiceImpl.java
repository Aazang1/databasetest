package com.example.service.impl;

import com.example.dao.NotificationReceiverRepositroy;
import com.example.dao.NotificationRepository;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationReceiverRepositroy notificationReceiverRepositroy ;

    @Autowired
    private UserService userService;

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

        // 2. 为每个通知查询接收者
        return notifications.stream().map(n -> {
            NotificationDTO dto = new NotificationDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setContent(n.getContent());
            dto.setType(n.getType());
            dto.setSenderid(n.getSenderid());
            dto.setCreateAt(n.getCreatedAt());

            // 查询接收者列表
            List<String> receivers = notificationReceiverRepositroy.findByNotificationid(n.getId())
                    .stream()
                    .map(NotificationReceiver::getReceiverid)
                    .collect(Collectors.toList());

            dto.setReceiverid(receivers);
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

