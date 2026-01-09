package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.Comment;
import java.util.List;

public interface CommentService {
    Comment getCommentById(Long commentId);
    List<Comment> getCommentsByPostId(Long postId);
    List<Comment> getCommentsByPostIdAndPosition(Long postId, Integer position);
    Comment createComment(Comment comment);
    Comment updateComment(Long commentId, Comment comment);
    void deleteComment(Long commentId);
    void deleteCommentsByPostId(Long postId);

    // Admin support
    List<Comment> getAllComments();
}