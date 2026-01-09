package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.Friendship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FriendshipMapper {
    Friendship findById(Long friendshipId);
    List<Friendship> findByUserId(Long userId);
    List<Friendship> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    Friendship findByUserAndFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
    int insert(Friendship friendship);
    int update(Friendship friendship);
    int deleteById(Long friendshipId);
    int deleteByUserAndFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // Admin support
    List<Friendship> findAll();
}