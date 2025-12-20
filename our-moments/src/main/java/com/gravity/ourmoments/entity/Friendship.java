package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Friendship {
    private Long friendshipId;
    private Long userId;
    private Long friendId;
    private String status; // PENDING, ACCEPTED, REJECTED
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}