package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.Friendship;
import com.gravity.ourmoments.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Friendship>> getFriendshipsByUserId(@PathVariable Long userId) {
        List<Friendship> friendships = friendshipService.getFriendshipsByUserId(userId);
        return ResponseEntity.ok(friendships);
    }

    @PostMapping("/request")
    public ResponseEntity<Friendship> sendFriendRequest(@RequestParam Long userId, @RequestParam Long friendId) {
        Friendship friendship = friendshipService.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok(friendship);
    }

    @PutMapping("/{friendshipId}/accept")
    public ResponseEntity<Friendship> acceptFriendRequest(@PathVariable Long friendshipId, @RequestParam Long userId) {
        Friendship friendship = friendshipService.acceptFriendRequest(friendshipId, userId);
        if (friendship == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(friendship);
    }

    @PutMapping("/{friendshipId}/reject")
    public ResponseEntity<Friendship> rejectFriendRequest(@PathVariable Long friendshipId, @RequestParam Long userId) {
        Friendship friendship = friendshipService.rejectFriendRequest(friendshipId, userId);
        if (friendship == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(friendship);
    }

    @DeleteMapping("/user/{userId}/friend/{friendId}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long userId, @PathVariable Long friendId) {
        friendshipService.deleteFriendship(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFriendship(@RequestParam Long userId, @RequestParam Long friendId) {
        boolean areFriends = friendshipService.areFriends(userId, friendId);
        return ResponseEntity.ok(areFriends);
    }
}