package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.email.from:noreply@ourmoments.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Override
    @Async
    public void sendEmail(String to, String subject, String content) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send to: {}, subject: {}", to, subject);
            return;
        }

        if (mailSender == null) {
            log.warn("Mail sender not configured");
            return;
        }

        if (to == null || to.isEmpty()) {
            log.warn("Recipient email is empty");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}, error: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendNewPostNotification(String to, String authorName, String postTitle, Long postId) {
        String subject;
        String content;

        // 特定用户定制内容
        if ("2827294239@qq.com".equals(to)) {
            subject = "【Our Moments】你的小猪宝宝蕊蕊发布了新日志";
            content = String.format(
                "Hi,\n\n" +
                "你的小猪宝宝——蕊蕊发布了新的日志：\n\n" +
                "《%s》\n\n" +
                "她想你啦，快去看看吧！\n\n" +
                "---\n" +
                "Our Moments - 记录美好时刻",
                postTitle
            );
        } else {
            subject = "【Our Moments】" + authorName + " 发布了新日志";
            content = String.format(
                "Hi,\n\n" +
                "你的好友 %s 发布了新的日志：\n\n" +
                "《%s》\n\n" +
                "快去看看吧！\n\n" +
                "---\n" +
                "Our Moments - 记录美好时刻",
                authorName, postTitle
            );
        }

        sendEmail(to, subject, content);
    }
}
