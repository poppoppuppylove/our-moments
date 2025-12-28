package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.Message;
import com.gravity.ourmoments.entity.User;
import com.gravity.ourmoments.service.MessageService;
import com.gravity.ourmoments.service.FriendshipService;
import com.gravity.ourmoments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // 通过用户名获取用户ID
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return;
        }
        Long senderId = user.getUserId();

        // 检查是否是好友关系
        if (!friendshipService.areFriends(senderId, chatMessage.getReceiverId())) {
            // 如果不是好友，不处理消息
            return;
        }

        // 保存消息到数据库
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(chatMessage.getReceiverId());
        message.setContent(chatMessage.getContent());
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());

        messageService.sendMessage(senderId, chatMessage.getReceiverId(), chatMessage.getContent());

        // 发送消息给接收者
        messagingTemplate.convertAndSendToUser(
            chatMessage.getReceiverId().toString(),
            "/queue/messages",
            new ChatMessage(senderId, chatMessage.getReceiverId(), chatMessage.getContent())
        );
    }

    // 内部消息类用于WebSocket传输
    public static class ChatMessage {
        private Long senderId;
        private Long receiverId;
        private String content;
        private String timestamp;

        public ChatMessage() {
        }

        public ChatMessage(Long senderId, Long receiverId, String content) {
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.content = content;
            this.timestamp = LocalDateTime.now().toString();
        }

        // Getters and setters
        public Long getSenderId() {
            return senderId;
        }

        public void setSenderId(Long senderId) {
            this.senderId = senderId;
        }

        public Long getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Long receiverId) {
            this.receiverId = receiverId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}