package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.Friendship;
import com.gravity.ourmoments.mapper.FriendshipMapper;
import com.gravity.ourmoments.service.FriendshipService;
import com.gravity.ourmoments.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Friendship getFriendshipById(Long friendshipId) {
        return friendshipMapper.findById(friendshipId);
    }

    @Override
    public List<Friendship> getFriendshipsByUserId(Long userId) {
        return friendshipMapper.findByUserId(userId);
    }

    @Override
    public List<Friendship> getFriendsByUserId(Long userId) {
        List<Friendship> friendships = friendshipMapper.findByUserIdAndStatus(userId, "ACCEPTED");
        return friendships.stream()
                .filter(f -> f.getStatus().equals("ACCEPTED"))
                .collect(Collectors.toList());
    }

    @Override
    public Friendship getFriendshipByUserAndFriend(Long userId, Long friendId) {
        return friendshipMapper.findByUserAndFriend(userId, friendId);
    }

    @Override
    public Friendship sendFriendRequest(Long userId, Long friendId) {
        // 检查是否已经存在好友关系
        Friendship existing = friendshipMapper.findByUserAndFriend(userId, friendId);
        if (existing != null) {
            return existing;
        }

        // 创建好友请求
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus("PENDING");
        friendshipMapper.insert(friendship);

        // 发送好友请求通知
        notificationService.sendFriendRequestNotification(friendId, userId, friendship.getFriendshipId());

        return friendship;
    }

    @Override
    public Friendship acceptFriendRequest(Long friendshipId, Long userId) {
        Friendship friendship = friendshipMapper.findById(friendshipId);
        if (friendship != null && friendship.getFriendId().equals(userId) && "PENDING".equals(friendship.getStatus())) {
            friendship.setStatus("ACCEPTED");
            friendshipMapper.update(friendship);

            // 发送好友接受通知
            // 这里可以添加通知逻辑，通知请求发送方请求已被接受
        }
        return friendship;
    }

    @Override
    public Friendship rejectFriendRequest(Long friendshipId, Long userId) {
        Friendship friendship = friendshipMapper.findById(friendshipId);
        if (friendship != null && friendship.getFriendId().equals(userId) && "PENDING".equals(friendship.getStatus())) {
            friendship.setStatus("REJECTED");
            friendshipMapper.update(friendship);

            // 发送好友拒绝通知
            // 这里可以添加通知逻辑，通知请求发送方请求已被拒绝
        }
        return friendship;
    }

    @Override
    public void deleteFriendship(Long userId, Long friendId) {
        friendshipMapper.deleteByUserAndFriend(userId, friendId);
    }

    @Override
    public boolean areFriends(Long userId, Long friendId) {
        Friendship friendship = friendshipMapper.findByUserAndFriend(userId, friendId);
        return friendship != null && "ACCEPTED".equals(friendship.getStatus());
    }
}