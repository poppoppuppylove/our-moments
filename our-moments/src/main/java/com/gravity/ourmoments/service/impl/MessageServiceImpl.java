package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.Message;
import com.gravity.ourmoments.mapper.MessageMapper;
import com.gravity.ourmoments.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());

        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<Message> getChatHistory(Long userId, Long friendId) {
        return messageMapper.findChatHistory(userId, friendId);
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        return messageMapper.findUnreadMessages(userId);
    }

    @Override
    public void markAsRead(Long userId, Long senderId) {
        messageMapper.markAsRead(userId, senderId, LocalDateTime.now());
    }

    @Override
    public void deleteMessage(Long messageId) {
        messageMapper.deleteById(messageId);
    }
}