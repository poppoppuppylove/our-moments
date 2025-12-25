package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.Notification;
import com.gravity.ourmoments.entity.User;
import com.gravity.ourmoments.mapper.NotificationMapper;
import com.gravity.ourmoments.mapper.UserMapper;
import com.gravity.ourmoments.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Notification createNotification(Notification notification) {
        notification.setCreateTime(LocalDateTime.now());
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        notificationMapper.insert(notification);
        // 发布事件到 WebSocket 监听器
        eventPublisher.publishEvent(notification);
        return notification;
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        return notificationMapper.findById(notificationId);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationMapper.findByUserId(userId);
    }

    @Override
    public int getUnreadCountByUserId(Long userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public Notification markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
        return notificationMapper.findById(notificationId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteById(notificationId);
    }

    @Override
    public void sendCommentNotification(Long postAuthorId, Long commenterId, Long commentId, String postTitle) {
        // 不要给自己发通知
        if (postAuthorId.equals(commenterId)) {
            return;
        }

        User commenter = userMapper.findById(commenterId);
        String commenterName = commenter != null ? commenter.getNickname() : "用户";

        Notification notification = new Notification();
        notification.setUserId(postAuthorId);
        notification.setType("COMMENT");
        notification.setContent(commenterName + " 评论了你的文章 \"" + postTitle + "\"");
        notification.setRelatedId(commentId);
        createNotification(notification);
    }

    @Override
    public void sendFriendRequestNotification(Long recipientId, Long requesterId, Long friendshipId) {
        User requester = userMapper.findById(requesterId);
        String requesterName = requester != null ? requester.getNickname() : "用户";

        Notification notification = new Notification();
        notification.setUserId(recipientId);
        notification.setType("FRIEND_REQUEST");
        notification.setContent(requesterName + " 向你发送了好友请求");
        notification.setRelatedId(friendshipId);
        createNotification(notification);
    }

    @Override
    public void sendNewPostNotificationToFriends(Long authorId, Long postId, String postTitle) {
        User author = userMapper.findById(authorId);
        String authorName = author != null ? author.getNickname() : "用户";

        // 这里需要获取作者的所有好友，然后发送通知
        // 为简化实现，这里只创建一个示例通知
        // 实际实现中需要查询好友关系表获取好友列表

        Notification notification = new Notification();
        notification.setUserId(authorId); // 为了演示，这里先给作者自己发一个通知
        notification.setType("NEW_POST");
        notification.setContent("你发布了新文章 \"" + postTitle + "\"");
        notification.setRelatedId(postId);
        createNotification(notification);
    }
}