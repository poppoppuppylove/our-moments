package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.BlogPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BlogPostMapper {
    BlogPost findById(Long postId);

    // Supports basic filtering
    List<BlogPost> findPosts(@Param("userId") Long userId,
                            @Param("categoryId") Long categoryId,
                            @Param("status") Integer status);

    // Get all posts
    List<BlogPost> findAll();

    int insert(BlogPost post);
    int update(BlogPost post);
    int deleteById(Long postId);

    // Tag relations
    int addTagToPost(@Param("postId") Long postId, @Param("tagId") Long tagId);
    int removeTagsFromPost(Long postId);

    // Draft management
    List<BlogPost> findDraftsByUserId(@Param("userId") Long userId);
    BlogPost findLatestDraftByUserId(@Param("userId") Long userId);
}
