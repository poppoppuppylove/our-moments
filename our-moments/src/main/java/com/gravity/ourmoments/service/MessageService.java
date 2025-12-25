package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Long senderId, Long receiverId, String content);
    List<Message> getChatHistory(Long userId, Long friendId);
    List<Message> getUnreadMessages(Long userId);
    void markAsRead(Long userId, Long senderId);
    void deleteMessage(Long messageId);
}