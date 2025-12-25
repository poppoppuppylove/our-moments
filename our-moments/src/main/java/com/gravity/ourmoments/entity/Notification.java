package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long notificationId;
    private Long userId;
    private String type; // COMMENT, FRIEND_REQUEST, NEW_POST
    private String content;
    private Long relatedId; // 关联ID（评论ID、好友关系ID、文章ID等）
    private Boolean isRead;
    private LocalDateTime createTime;
}