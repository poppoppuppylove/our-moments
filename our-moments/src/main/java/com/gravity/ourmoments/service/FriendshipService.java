package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.Friendship;
import java.util.List;

public interface FriendshipService {
    Friendship getFriendshipById(Long friendshipId);
    List<Friendship> getFriendshipsByUserId(Long userId);
    List<Friendship> getFriendsByUserId(Long userId);
    Friendship getFriendshipByUserAndFriend(Long userId, Long friendId);
    Friendship sendFriendRequest(Long userId, Long friendId);
    Friendship acceptFriendRequest(Long friendshipId, Long userId);
    Friendship rejectFriendRequest(Long friendshipId, Long userId);
    void deleteFriendship(Long userId, Long friendId);
    boolean areFriends(Long userId, Long friendId);

    // Admin support
    List<Object> getAllFriendships();
    void deleteFriendship(Long friendshipId);
}