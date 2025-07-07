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
//    List<Notification> findByReceiverid(String userid);

    @Query("SELECT n FROM Notification n JOIN NotificationReceiver nr ON n.id = nr.notificationid WHERE nr.receiverid = :receiverid")
    List<Notification> findByReceiverId(@Param("receiverid") String receiverid);


    @Query("SELECT NEW com.example.entity.dto.NotificationWithStatusDTO(" +
            "nr.id, n.title, n.content, n.type, n.createdAt, n.senderid, " + // 用nr.id替换n.id
            "nr.is_read, nr.read_time, n.id) " + // 最后补上n.id用于关联
            "FROM NotificationReceiver nr " +    // 从nr表开始查询
            "LEFT JOIN Notification n ON nr.notificationid = n.id " + // 关联通知主表
            "WHERE nr.receiverid = :receiverid " +
            "AND nr.notificationid IS NOT NULL") // 过滤无效关联
    List<NotificationWithStatusDTO> findNotificationsWithStatus(
            @Param("receiverid") String receiverid
    );


    List<Notification> findBySenderid(String senderid);


//    long countByUseridAndisread(String userid, boolean isread);
//    long countByisReadAndReceiverid(boolean isread,String userid);


}
