package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Insert("INSERT INTO sys_notification (user_id, type, content, related_id, is_read, create_time) " +
            "VALUES (#{userId}, #{type}, #{content}, #{relatedId}, #{isRead}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "notificationId")
    void insert(Notification notification);

    @Select("SELECT * FROM sys_notification WHERE notification_id = #{notificationId}")
    Notification findById(Long notificationId);

    @Select("SELECT * FROM sys_notification WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Notification> findByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM sys_notification WHERE user_id = #{userId} AND is_read = false")
    int countUnreadByUserId(Long userId);

    @Update("UPDATE sys_notification SET is_read = true WHERE notification_id = #{notificationId}")
    void markAsRead(Long notificationId);

    @Update("UPDATE sys_notification SET is_read = true WHERE user_id = #{userId}")
    void markAllAsRead(Long userId);

    @Delete("DELETE FROM sys_notification WHERE notification_id = #{notificationId}")
    void deleteById(Long notificationId);
}