package com.example.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class NotificationWithStatusDTO {
    private Long id;
    private String title;
    private String content;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date createdAt;
    private String senderid;
    private boolean isRead;  // 从 notification_receive 表获取
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;   // 从 notification_receive 表获取
    private Long notificationId;
    // 构造方法（用于JPQL）
    public NotificationWithStatusDTO(
            Long id, String title, String content, String type,
            Date createdAt, String senderid,
            boolean isRead, Date readTime, Long notificationId
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.senderid = senderid;
        this.isRead = isRead;
        this.readTime = readTime;
        this.notificationId = notificationId;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
// getters & setters
}
