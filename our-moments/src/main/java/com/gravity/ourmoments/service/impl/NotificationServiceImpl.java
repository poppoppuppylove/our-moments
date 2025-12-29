package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.Friendship;
import com.gravity.ourmoments.entity.Notification;
import com.gravity.ourmoments.entity.User;
import com.gravity.ourmoments.mapper.FriendshipMapper;
import com.gravity.ourmoments.mapper.NotificationMapper;
import com.gravity.ourmoments.mapper.UserMapper;
import com.gravity.ourmoments.service.EmailService;
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
    private FriendshipMapper friendshipMapper;

    @Autowired
    private EmailService emailService;

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

        // 获取作者的所有好友（已接受状态的好友关系）
        List<Friendship> friendships = friendshipMapper.findByUserIdAndStatus(authorId, "ACCEPTED");

        for (Friendship friendship : friendships) {
            // 获取好友的ID（可能是 userId 或 friendId）
            Long friendUserId = friendship.getUserId().equals(authorId)
                ? friendship.getFriendId()
                : friendship.getUserId();

            // 发送应用内通知
            Notification notification = new Notification();
            notification.setUserId(friendUserId);
            notification.setType("NEW_POST");
            notification.setContent(authorName + " 发布了新日志 \"" + postTitle + "\"");
            notification.setRelatedId(postId);
            createNotification(notification);

            // 发送邮件通知
            User friend = userMapper.findById(friendUserId);
            if (friend != null && friend.getEmail() != null && !friend.getEmail().isEmpty()) {
                emailService.sendNewPostNotification(friend.getEmail(), authorName, postTitle, postId);
            }
        }
    }

    @Override
    public void sendMessageNotification(Long receiverId, Long senderId, Long messageId, String messageContent) {
        User sender = userMapper.findById(senderId);
        String senderName = sender != null ? sender.getNickname() : "用户";

        // 截断消息内容用于通知显示
        String shortContent = messageContent.length() > 50
            ? messageContent.substring(0, 50) + "..."
            : messageContent;

        Notification notification = new Notification();
        notification.setUserId(receiverId);
        notification.setType("MESSAGE");
        notification.setContent(senderName + " 给你发送了私信: \"" + shortContent + "\"");
        notification.setRelatedId(messageId);
        createNotification(notification);
    }
}