package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO sys_message (sender_id, receiver_id, content, is_read, create_time, update_time) " +
            "VALUES (#{senderId}, #{receiverId}, #{content}, #{isRead}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    void insert(Message message);

    @Select("SELECT * FROM sys_message WHERE message_id = #{messageId}")
    Message findById(Long messageId);

    @Select("SELECT * FROM sys_message WHERE (sender_id = #{userId} AND receiver_id = #{friendId}) " +
            "OR (sender_id = #{friendId} AND receiver_id = #{userId}) ORDER BY create_time ASC")
    List<Message> findChatHistory(Long userId, Long friendId);

    @Select("SELECT * FROM sys_message WHERE receiver_id = #{userId} AND is_read = false ORDER BY create_time ASC")
    List<Message> findUnreadMessages(Long userId);

    @Update("UPDATE sys_message SET is_read = true, update_time = #{updateTime} " +
            "WHERE receiver_id = #{userId} AND sender_id = #{senderId} AND is_read = false")
    void markAsRead(Long userId, Long senderId, LocalDateTime updateTime);

    @Delete("DELETE FROM sys_message WHERE message_id = #{messageId}")
    void deleteById(Long messageId);
}