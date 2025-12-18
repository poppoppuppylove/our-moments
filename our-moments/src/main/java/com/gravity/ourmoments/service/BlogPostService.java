package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.BlogPost;
import java.util.List;

public interface BlogPostService {
    BlogPost getPostById(Long postId);
    List<BlogPost> getPosts(Long userId, Long categoryId, Integer status);
    BlogPost createPost(BlogPost post);
    BlogPost updatePost(Long postId, BlogPost post);
    void deletePost(Long postId);
}
