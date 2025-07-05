package com.example.entity.dto;

import java.util.List;

public class NotificationDTO {
    private String title;
    private String content;
    private String type;
    private String senderid;
    private List<String> receiverid;

    // getters and setters

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

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public List<String> getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(List<String> receiverid) {
        this.receiverid = receiverid;
    }
}
