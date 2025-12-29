package com.gravity.ourmoments.service;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
    void sendNewPostNotification(String to, String authorName, String postTitle, Long postId);
}
