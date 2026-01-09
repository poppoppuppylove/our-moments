package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.BlogPost;
import com.gravity.ourmoments.entity.Comment;
import com.gravity.ourmoments.mapper.BlogPostMapper;
import com.gravity.ourmoments.mapper.CommentMapper;
import com.gravity.ourmoments.service.CommentService;
import com.gravity.ourmoments.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Comment getCommentById(Long commentId) {
        return commentMapper.findById(commentId);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentMapper.findByPostId(postId);
    }

    @Override
    public List<Comment> getCommentsByPostIdAndPosition(Long postId, Integer position) {
        return commentMapper.findByPostIdAndPosition(postId, position);
    }

    @Override
    public Comment createComment(Comment comment) {
        commentMapper.insert(comment);

        // 发送评论通知
        BlogPost post = blogPostMapper.findById(comment.getPostId());
        if (post != null) {
            notificationService.sendCommentNotification(
                    post.getUserId(),
                    comment.getUserId(),
                    comment.getPostId(), // 传递 postId 而不是 commentId
                    comment.getCommentId(),
                    post.getTitle() != null ? post.getTitle() : "未命名文章"
            );
        }

        return comment;
    }

    @Override
    public Comment updateComment(Long commentId, Comment comment) {
        comment.setCommentId(commentId);
        commentMapper.update(comment);
        return commentMapper.findById(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentMapper.deleteById(commentId);
    }

    @Override
    public void deleteCommentsByPostId(Long postId) {
        commentMapper.deleteByPostId(postId);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentMapper.findAll();
    }
}