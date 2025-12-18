package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.BlogMedia;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BlogMediaMapper {
    List<BlogMedia> findByPostId(Long postId);
    int insert(BlogMedia media);
    int deleteByPostId(Long postId);
}
