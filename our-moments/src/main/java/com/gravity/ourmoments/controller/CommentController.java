package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.Comment;
import com.gravity.ourmoments.security.CustomUserDetails;
import com.gravity.ourmoments.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/post/{postId}/position/{position}")
    public ResponseEntity<List<Comment>> getCommentsByPostIdAndPosition(@PathVariable Long postId, @PathVariable Integer position) {
        List<Comment> comments = commentService.getCommentsByPostIdAndPosition(postId, position);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.createComment(comment);
        return ResponseEntity.created(URI.create("/api/comments/" + savedComment.getCommentId())).body(savedComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(id, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        // Verify the user owns the comment or is admin before allowing deletion
        Long currentUserId = getCurrentUserId();
        Comment existingComment = commentService.getCommentById(id);

        if (existingComment == null) {
            return ResponseEntity.notFound().build();
        }

        if (!existingComment.getUserId().equals(currentUserId) && !isCurrentUserAdmin()) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the current user ID from the security context
     * @return User ID if authenticated, null if anonymous
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }

        return null;
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