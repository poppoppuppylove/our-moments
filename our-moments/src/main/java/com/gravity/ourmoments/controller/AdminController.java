package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.BlogPost;
import com.gravity.ourmoments.entity.Comment;
import com.gravity.ourmoments.entity.User;
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