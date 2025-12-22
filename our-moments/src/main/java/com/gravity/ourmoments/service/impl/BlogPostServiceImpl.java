package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.BlogMedia;
import com.gravity.ourmoments.entity.BlogPost;
import com.gravity.ourmoments.entity.Tag;
import com.gravity.ourmoments.mapper.BlogMediaMapper;
import com.gravity.ourmoments.mapper.BlogPostMapper;
import com.gravity.ourmoments.service.BlogPostService;
import com.gravity.ourmoments.service.FriendshipService;
import com.gravity.ourmoments.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private BlogMediaMapper blogMediaMapper;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private TagService tagService;

    @Override
    public BlogPost getPostById(Long postId) {
        return blogPostMapper.findById(postId);
    }

    @Override
    public List<BlogPost> getPosts(Long userId, Long categoryId, Integer status) {
        return blogPostMapper.findPosts(userId, categoryId, status);
    }

    @Override
    @Transactional
    public BlogPost createPost(BlogPost post) {
        // 1. Insert Blog Post
        blogPostMapper.insert(post);

        // 2. Insert Media
        if (post.getMediaList() != null) {
            for (BlogMedia media : post.getMediaList()) {
                media.setPostId(post.getPostId());
                blogMediaMapper.insert(media);
            }
        }

        // 3. Insert Tags
        if (post.getTagList() != null) {
            for (Tag tag : post.getTagList()) {
                blogPostMapper.addTagToPost(post.getPostId(), tag.getTagId());
            }
        }


        return getPostById(post.getPostId());
    }

    @Override
    @Transactional
    public BlogPost updatePost(Long postId, BlogPost post) {
        post.setPostId(postId);
        blogPostMapper.update(post);

        // Handle Media Update (Delete all and re-insert for simplicity in this MVP)
        // In a real app, you might want to diff changes to avoid deleting files that haven't changed
        if (post.getMediaList() != null) {
            blogMediaMapper.deleteByPostId(postId);
            for (BlogMedia media : post.getMediaList()) {
                media.setPostId(postId);
                blogMediaMapper.insert(media);
            }
        }

        // Handle Tag Update (Delete all and re-insert)
        if (post.getTagList() != null) {
            blogPostMapper.removeTagsFromPost(postId);
            for (Tag tag : post.getTagList()) {
                blogPostMapper.addTagToPost(postId, tag.getTagId());
            }
        }

        return getPostById(postId);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        blogMediaMapper.deleteByPostId(postId);
        blogPostMapper.removeTagsFromPost(postId);
        blogPostMapper.deleteById(postId);
    }

    @Override
    public List<BlogPost> getVisiblePosts(Long currentUserId) {
        // Get all posts and filter by visibility
        List<BlogPost> allPosts = blogPostMapper.findAll();
        return filterVisiblePosts(allPosts, currentUserId);
    }

    @Override
    public List<BlogPost> getVisiblePostsByUserId(Long userId, Long currentUserId) {
        // Get posts by user and filter by visibility
        List<BlogPost> userPosts = blogPostMapper.findPosts(userId, null, null);
        return filterVisiblePosts(userPosts, currentUserId);
    }

    @Override
    public BlogPost getVisiblePostById(Long postId, Long currentUserId) {
        BlogPost post = blogPostMapper.findById(postId);
        if (post == null) {
            return null;
        }

        if (isVisibleToUser(post, currentUserId)) {
            return post;
        }

        return null;
    }

    private List<BlogPost> filterVisiblePosts(List<BlogPost> posts, Long currentUserId) {
        if (currentUserId == null) {
            // Not logged in - only show public posts
            return posts.stream()
                    .filter(post -> "PUBLIC".equals(post.getVisibility()))
                    .collect(java.util.stream.Collectors.toList());
        }

        return posts.stream()
                .filter(post -> isVisibleToUser(post, currentUserId))
                .collect(java.util.stream.Collectors.toList());
    }

    private boolean isVisibleToUser(BlogPost post, Long currentUserId) {
        // Author can always see their own posts
        if (post.getUserId().equals(currentUserId)) {
            return true;
        }

        // Public posts are visible to everyone
        if ("PUBLIC".equals(post.getVisibility())) {
            return true;
        }

        // Private posts are only visible to the author (already checked above)
        if ("PRIVATE".equals(post.getVisibility())) {
            return false;
        }

        // Friends-only posts are visible to friends
        if ("FRIENDS".equals(post.getVisibility())) {
            return friendshipService.areFriends(post.getUserId(), currentUserId);
        }

        // Default to not visible
        return false;
    }
}
