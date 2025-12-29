package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.Notification;
import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    Notification getNotificationById(Long notificationId);
    List<Notification> getNotificationsByUserId(Long userId);
    int getUnreadCountByUserId(Long userId);
    Notification markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);

    // 业务方法
    void sendCommentNotification(Long postAuthorId, Long commenterId, Long commentId, String postTitle);
    void sendFriendRequestNotification(Long recipientId, Long requesterId, Long friendshipId);
    void sendNewPostNotificationToFriends(Long authorId, Long postId, String postTitle);
    void sendMessageNotification(Long receiverId, Long senderId, Long messageId, String messageContent);
}