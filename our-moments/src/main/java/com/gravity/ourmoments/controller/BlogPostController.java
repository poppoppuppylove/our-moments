package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.entity.BlogPost;
import com.gravity.ourmoments.service.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok(blogPostService.getPosts(userId, categoryId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getPost(@PathVariable Long id) {
        BlogPost post = blogPostService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPost post) {
        BlogPost savedPost = blogPostService.createPost(post);
        return ResponseEntity.created(URI.create("/api/posts/" + savedPost.getPostId())).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long id, @RequestBody BlogPost post) {
        BlogPost updatedPost = blogPostService.updatePost(id, post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
