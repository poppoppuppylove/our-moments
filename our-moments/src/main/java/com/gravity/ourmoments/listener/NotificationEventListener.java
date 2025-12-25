package com.gravity.ourmoments.listener;

import com.gravity.ourmoments.entity.Notification;
import com.gravity.ourmoments.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 发送通知到前端的WebSocket
    public void sendNotificationToUser(Long userId, Notification notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }

    // 当有新通知时，通过WebSocket推送给用户
    @EventListener
    public void handleNotificationEvent(Notification notification) {
        // 发送WebSocket通知
        sendNotificationToUser(notification.getUserId(), notification);
    }
}