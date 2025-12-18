package com.gravity.ourmoments.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BlogMedia {
    private Long mediaId;
    private Long postId;
    private String mediaUrl;
    private String mediaType; // IMAGE/VIDEO
    private Integer rotation;
    private BigDecimal scale;
    private Integer positionX;
    private Integer positionY;
    private String filterStyle;
    private Integer zIndex;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
