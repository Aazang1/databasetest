package com.example.dao;

import com.example.entity.NotificationReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationReceiverRepositroy extends JpaRepository<NotificationReceiver, Long> {
    List<NotificationReceiver> findByNotificationid(Long notificationid);
}
