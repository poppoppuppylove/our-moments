package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.BlogMedia;
import com.gravity.ourmoments.entity.BlogPost;
import com.gravity.ourmoments.entity.Tag;
import com.gravity.ourmoments.mapper.BlogMediaMapper;
import com.gravity.ourmoments.mapper.BlogPostMapper;
import com.gravity.ourmoments.service.BlogPostService;
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
}
