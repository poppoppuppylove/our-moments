package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private Integer position; // 评论在文章中的位置
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // For joining data
    private User author;
}