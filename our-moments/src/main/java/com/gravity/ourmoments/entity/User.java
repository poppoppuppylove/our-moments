package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String bio;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
