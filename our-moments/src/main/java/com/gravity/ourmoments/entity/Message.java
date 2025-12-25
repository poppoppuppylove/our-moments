package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}