package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.BlogPost;
import com.gravity.ourmoments.entity.User;
import com.gravity.ourmoments.security.CustomUserDetails;
import com.gravity.ourmoments.service.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class BlogPostController {

    @Autowired
    private BlogPostService blogPostService;

    @GetMapping
    public ResponseEntity<List<BlogPost>> getPosts(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        Long currentUserId = getCurrentUserId();

        List<BlogPost> posts;
        if (userId != null) {
            // Get posts for a specific user with visibility filtering
            posts = blogPostService.getVisiblePostsByUserId(userId, currentUserId);
        } else {
            // Get all visible posts
            posts = blogPostService.getVisiblePosts(currentUserId);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getPost(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        BlogPost post = blogPostService.getVisiblePostById(id, currentUserId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPost post) {
        // Debug logging
        System.out.println("Received post creation request:");
        System.out.println("Title: " + post.getTitle());
        System.out.println("Content length: " + (post.getContent() != null ? post.getContent().length() : 0));
        System.out.println("Tag list size: " + (post.getTagList() != null ? post.getTagList().size() : 0));
        if (post.getTagList() != null) {
            for (int i = 0; i < post.getTagList().size(); i++) {
                System.out.println("Tag " + i + ": " + post.getTagList().get(i).getName());
            }
        }

        // Set default visibility to PUBLIC if not specified
        if (post.getVisibility() == null || post.getVisibility().isEmpty()) {
            post.setVisibility("PUBLIC");
        }

        BlogPost savedPost = blogPostService.createPost(post);
        return ResponseEntity.created(URI.create("/api/posts/" + savedPost.getPostId())).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long id, @RequestBody BlogPost post) {
        // Debug logging
        System.out.println("Received post update request for ID: " + id);
        System.out.println("Title: " + post.getTitle());
        System.out.println("Content length: " + (post.getContent() != null ? post.getContent().length() : 0));
        System.out.println("Tag list size: " + (post.getTagList() != null ? post.getTagList().size() : 0));
        if (post.getTagList() != null) {
            for (int i = 0; i < post.getTagList().size(); i++) {
                System.out.println("Tag " + i + ": " + post.getTagList().get(i).getName());
            }
        }

        // Verify the user owns the post before allowing update
        Long currentUserId = getCurrentUserId();
        BlogPost existingPost = blogPostService.getPostById(id);

        if (existingPost == null) {
            return ResponseEntity.notFound().build();
        }

        if (!existingPost.getUserId().equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        BlogPost updatedPost = blogPostService.updatePost(id, post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        // Verify the user owns the post before allowing deletion
        Long currentUserId = getCurrentUserId();
        BlogPost existingPost = blogPostService.getPostById(id);

        if (existingPost == null) {
            return ResponseEntity.notFound().build();
        }

        if (!existingPost.getUserId().equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        blogPostService.deletePost(id);
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

        // Get the user ID from the custom user details
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }

        return null;
    }
}
