package com.example.dao;

import com.example.entity.Notification;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverid(String userid);



//    long countByUseridAndisread(String userid, boolean isread);
    long countByisReadAndReceiverid(boolean isread,String userid);


}
