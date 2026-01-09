package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.BlogPost;
import com.gravity.ourmoments.entity.Comment;
import com.gravity.ourmoments.entity.User;
import com.gravity.ourmoments.entity.Friendship;
import com.gravity.ourmoments.security.CustomUserDetails;
import com.gravity.ourmoments.service.BlogPostService;
import com.gravity.ourmoments.service.CommentService;
import com.gravity.ourmoments.service.FriendshipService;
import com.gravity.ourmoments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FriendshipService friendshipService;

    // User management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        // Set default password if not provided
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }

        User createdUser = userService.register(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        User updatedUser = userService.updateUser(userId, user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestBody User user) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        User updatedUser = userService.updateUserRole(userId, user.getRole());
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<User> resetUserPassword(@PathVariable Long userId) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        User updatedUser = userService.resetPassword(userId, "123456");
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        // Delete all user data
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Post management
    @GetMapping("/posts")
    public ResponseEntity<List<BlogPost>> getAllPosts() {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        List<BlogPost> posts = blogPostService.getPosts(null, null, null);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long postId, @RequestBody BlogPost post) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        BlogPost updatedPost = blogPostService.updatePost(postId, post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        blogPostService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // Comment management
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getAllComments() {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody Comment comment) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        Comment updatedComment = commentService.updateComment(commentId, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // Friendship management
    @GetMapping("/friendships")
    public ResponseEntity<List<Object>> getAllFriendships() {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        List<Object> friendships = friendshipService.getAllFriendships();
        return ResponseEntity.ok(friendships);
    }

    @PostMapping("/friendships")
    public ResponseEntity<Object> createFriendship(@RequestBody Map<String, Object> payload) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long friendId = Long.valueOf(payload.get("friendId").toString());
            String status = (String) payload.getOrDefault("status", "PENDING");

            // Create friendship object
            Friendship friendship = new Friendship();
            friendship.setUserId(userId);
            friendship.setFriendId(friendId);
            friendship.setStatus(status);

            // Use friendship service to create the friendship
            Friendship createdFriendship = friendshipService.createFriendship(friendship);
            return ResponseEntity.ok(createdFriendship);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/friendships/{friendshipId}/status")
    public ResponseEntity<Friendship> updateFriendshipStatus(@PathVariable Long friendshipId, @RequestBody Map<String, String> payload) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        String status = payload.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Friendship updatedFriendship = friendshipService.updateFriendshipStatus(friendshipId, status);
        if (updatedFriendship == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedFriendship);
    }

    @DeleteMapping("/friendships/{friendshipId}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Long friendshipId) {
        if (!isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build();
        }

        friendshipService.deleteFriendship(friendshipId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if the current user is an admin
     * @return true if admin, false otherwise
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).isAdmin();
        }

        return false;
    }
}