package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long categoryId;
    private String name;
    private String iconUrl;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
