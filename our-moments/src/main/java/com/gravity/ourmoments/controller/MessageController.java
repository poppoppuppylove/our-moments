package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.Message;
import com.gravity.ourmoments.security.CustomUserDetails;
import com.gravity.ourmoments.service.MessageService;
import com.gravity.ourmoments.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private FriendshipService friendshipService;

    /**
     * 从 Authentication 中获取当前用户ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long receiverId,
            @RequestParam String content,
            Authentication authentication) {
        Long senderId = getCurrentUserId(authentication);

        // 检查是否是好友关系
        if (!friendshipService.areFriends(senderId, receiverId)) {
            return ResponseEntity.badRequest().build();
        }

        Message message = messageService.sendMessage(senderId, receiverId, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Message>> getChatHistory(
            @RequestParam Long friendId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        // 检查是否是好友关系
        if (!friendshipService.areFriends(userId, friendId)) {
            return ResponseEntity.badRequest().build();
        }

        List<Message> messages = messageService.getChatHistory(userId, friendId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<Message> messages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/read")
    public ResponseEntity<Void> markAsRead(
            @RequestParam Long senderId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        messageService.markAsRead(userId, senderId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}