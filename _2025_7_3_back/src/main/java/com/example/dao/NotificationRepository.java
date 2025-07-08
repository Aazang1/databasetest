package com.example.dao;

import com.example.entity.Notification;
import com.example.entity.User;
import com.example.entity.dto.NotificationDTO;
import com.example.entity.dto.NotificationWithStatusDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n JOIN NotificationReceiver nr ON n.id = nr.notificationid WHERE nr.receiverid = :receiverid")
    List<Notification> findByReceiverId(@Param("receiverid") String receiverid);


    @Query("SELECT NEW com.example.entity.dto.NotificationWithStatusDTO(" +
            "nr.id, n.title, n.content, n.type, n.createdAt, n.senderid, " +
            "nr.is_read, nr.read_time, n.id) " +
            "FROM NotificationReceiver nr " +
            "LEFT JOIN Notification n ON nr.notificationid = n.id " +
            "WHERE nr.receiverid = :receiverid " +
            "AND nr.notificationid IS NOT NULL " +
            "ORDER BY n.createdAt DESC")  // 添加按创建时间降序排序
    List<NotificationWithStatusDTO> findNotificationsWithStatus(
            @Param("receiverid") String receiverid
    );


    List<Notification> findBySenderidOrderByCreatedAtDesc(String senderid);



}
