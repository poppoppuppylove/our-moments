package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    Comment findById(Long commentId);
    List<Comment> findByPostId(Long postId);
    List<Comment> findByPostIdAndPosition(@Param("postId") Long postId, @Param("position") Integer position);
    int insert(Comment comment);
    int update(Comment comment);
    int deleteById(Long commentId);
    int deleteByPostId(Long postId);
}