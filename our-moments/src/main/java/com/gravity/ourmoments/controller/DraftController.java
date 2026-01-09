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
@RequestMapping("/api/drafts")
public class DraftController {

    @Autowired
    private BlogPostService blogPostService;

    @GetMapping
    public ResponseEntity<List<BlogPost>> getDrafts() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        List<BlogPost> drafts = blogPostService.getDraftsByUserId(currentUserId);
        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/latest")
    public ResponseEntity<BlogPost> getLatestDraft() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        BlogPost draft = blogPostService.getLatestDraftByUserId(currentUserId);
        if (draft == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(draft);
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