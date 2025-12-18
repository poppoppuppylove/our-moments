package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogPost {
    private Long postId;
    private Long userId;
    private Long categoryId;
    private String title;
    private String content;
    private String weather;
    private String mood;
    private String location;
    private Integer status; // 0:Draft, 1:Published
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // For joining data
    private List<BlogMedia> mediaList;
    private List<Tag> tagList;
    private Category category;
    private User author;
}
