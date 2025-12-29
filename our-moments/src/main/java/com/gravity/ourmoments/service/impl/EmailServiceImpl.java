package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;

    @Override
    @Async
    public void sendEmail(String to, String subject, String content) {
        sendHtmlEmail(to, subject, content, false);
    }

    /**
     * 发送 HTML 邮件
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent, boolean isHtml) {
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
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, isHtml);
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}, error: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendNewPostNotification(String to, String authorName, String postTitle, Long postId) {
        String subject = "";
        String htmlContent = "";
        String postUrl = baseUrl + "/post/" + postId;

        // 特定用户定制内容
        if ("2827294239@qq.com".equals(to)||"lastonean@163.com".equals(to)) {
            if("2827294239@qq.com".equals(to)){
            subject = "💌 你的小猪宝宝——蕊蕊发布了新日志";
            htmlContent = buildEmailTemplate(
                "你的小猪宝宝——蕊蕊",
                "发布了新的时刻",
                postTitle,
                "她想你啦，快去看看吧！",
                postUrl,
                true
            );}else if("lastonean@163.com".equals(to)){
                subject = "💌 你的好蛋老大发布了新日志";
                htmlContent = buildEmailTemplate(
                    "全世界最好的老大",
                    "发布了新的时刻",
                    postTitle,
                    "她想你啦，快去看看吧！",
                    postUrl,
                    false
                );
                
            };
        } else {
            subject = "💌 " + authorName + " 发布了新日志";
            htmlContent = buildEmailTemplate(
                authorName,
                "发布了新的日志",
                postTitle,
                "快去看看吧！",
                postUrl,
                true
            );
        }

        sendHtmlEmail(to, subject, htmlContent, true);
    }

    /**
     * 构建美化的 HTML 邮件模板 - 米黄色淡色系风格
     */
    private String buildEmailTemplate(String authorName, String action, String postTitle,
                                       String callToAction, String postUrl, boolean isSpecial) {
        // 网站配色：米黄纸张色 + 淡紫色
        String heartEmoji = isSpecial ? "💕" : "✨";
        // 米黄色背景
        String bgColor = "#F9F5F0";
        // 卡片背景：略深的米黄
        String cardBg = "#FFFEF9";
        // 强调色：淡紫色
        String accentColor = isSpecial ? "#D291BC" : "#957DAD";
        // 按钮颜色：柔和的淡紫/粉色
        String buttonBg = isSpecial ? "#D291BC" : "#957DAD";
        // 文字颜色
        String textColor = "#4A4A4A";
        String lightTextColor = "#8C8C8C";
        // 边框/分隔线颜色
        String borderColor = "rgba(149, 125, 173, 0.25)";

        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif; background-color: %s;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                        <td align="center" style="padding: 40px 20px;">
                            <!-- 主卡片 -->
                            <table role="presentation" width="100%%" style="max-width: 480px; background-color: %s; border-radius: 16px; box-shadow: 0 4px 20px rgba(149, 125, 173, 0.12); overflow: hidden; border: 1px solid %s;">

                                <!-- 顶部装饰条 -->
                                <tr>
                                    <td style="height: 4px; background-color: %s;"></td>
                                </tr>

                                <!-- 图钉装饰 -->
                                <tr>
                                    <td align="center" style="padding-top: 24px;">
                                        <div style="width: 20px; height: 20px; background-color: %s; border-radius: 50%%; box-shadow: 0 2px 6px rgba(149, 125, 173, 0.3);"></div>
                                    </td>
                                </tr>

                                <!-- Logo 区域 -->
                                <tr>
                                    <td align="center" style="padding: 24px 40px 16px;">
                                        <h1 style="margin: 0; font-size: 14px; color: %s; font-weight: 300; letter-spacing: 3px;">
                                            %sOur Moments%s
                                        </h1>
                                        <p style="margin: 6px 0 0; font-size: 12px; color: %s; font-style: italic;">
                                            记录美好时刻
                                        </p>
                                    </td>
                                </tr>

                                <!-- 分隔线 -->
                                <tr>
                                    <td style="padding: 0 36px;">
                                        <div style="border-top: 1px dashed %s;"></div>
                                    </td>
                                </tr>

                                <!-- 主要内容 -->
                                <tr>
                                    <td style="padding: 28px 36px;">
                                        <p style="margin: 0 0 16px; font-size: 15px; color: %s; line-height: 1.7;">
                                            Hi，
                                        </p>
                                        <p style="margin: 0 0 20px; font-size: 15px; color: %s; line-height: 1.7;">
                                            <span style="color: %s; font-weight: 500;">%s</span> %s：
                                        </p>

                                        <!-- 文章标题卡片 -->
                                        <table role="presentation" width="100%%" style="background-color: #F9F5F0; border-radius: 12px; border: 1px dashed %s;">
                                            <tr>
                                                <td style="padding: 20px; text-align: center;">
                                                    <p style="margin: 0; font-size: 18px; color: %s; font-weight: 400;">
                                                        「 %s 」
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>

                                        <p style="margin: 20px 0; font-size: 14px; color: %s; text-align: center;">
                                            %s
                                        </p>

                                        <!-- 按钮 -->
                                        <table role="presentation" width="100%%" style="margin-top: 12px;">
                                            <tr>
                                                <td align="center">
                                                    <a href="%s" style="display: inline-block; padding: 12px 32px; background-color: %s; color: white; text-decoration: none; border-radius: 20px; font-size: 14px; font-weight: 400; box-shadow: 0 3px 12px rgba(149, 125, 173, 0.25);">
                                                        %s 立即查看
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- 底部分隔线 -->
                                <tr>
                                    <td style="padding: 0 36px;">
                                        <div style="border-top: 1px dashed %s;"></div>
                                    </td>
                                </tr>

                                <!-- 页脚 -->
                                <tr>
                                    <td align="center" style="padding: 20px 36px 28px;">
                                        <p style="margin: 0; font-size: 11px; color: %s;">
                                            这封邮件来自 Our Moments
                                        </p>
                                        <p style="margin: 6px 0 0; font-size: 10px; color: #bbb;">
                                            如果按钮无法点击，请复制链接到浏览器：<br>
                                            <span style="color: %s; word-break: break-all;">%s</span>
                                        </p>
                                    </td>
                                </tr>
                            </table>

                            <!-- 底部小字 -->
                            <p style="margin: 20px 0 0; font-size: 10px; color: %s;">
                                © 2025 Our Moments · Made with %s
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """,
            bgColor,         // body background
            cardBg,          // 卡片背景
            borderColor,     // 卡片边框
            accentColor,     // 顶部装饰条
            accentColor,     // 图钉颜色
            accentColor,     // 标题颜色
            heartEmoji,      // 左心形
            heartEmoji,      // 右心形
            lightTextColor,  // 副标题颜色
            borderColor,     // 分隔线
            textColor,       // Hi 文字颜色
            textColor,       // 正文颜色
            accentColor,     // 作者名颜色
            authorName,      // 作者名
            action,          // 动作描述
            borderColor,     // 标题卡片边框
            accentColor,     // 文章标题颜色
            postTitle,       // 文章标题
            lightTextColor,  // 行动号召文字颜色
            callToAction,    // 行动号召
            postUrl,         // 按钮链接
            buttonBg,        // 按钮背景
            heartEmoji,      // 按钮emoji
            borderColor,     // 底部分隔线
            lightTextColor,  // 页脚文字颜色
            accentColor,     // 链接颜色
            postUrl,         // 备用链接
            lightTextColor,  // 底部小字颜色
            heartEmoji       // 底部心形
        );
    }
}
