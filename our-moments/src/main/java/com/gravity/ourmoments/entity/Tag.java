package com.gravity.ourmoments.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tag {
    private Long tagId;
    private String name;
    private LocalDateTime createTime;
}
