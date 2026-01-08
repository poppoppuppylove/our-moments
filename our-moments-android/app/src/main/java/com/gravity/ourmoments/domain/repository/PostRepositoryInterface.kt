package com.gravity.ourmoments.domain.repository

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.Comment
import com.gravity.ourmoments.domain.model.Post

interface PostRepositoryInterface {
    suspend fun getPosts(
        page: Int,
        size: Int,
        visibility: String? = null
    ): Result<List<Post>>

    suspend fun getPostById(id: Long): Result<Post>

    suspend fun createPost(post: Post): Result<Post>

    suspend fun updatePost(id: Long, post: Post): Result<Post>

    suspend fun deletePost(id: Long): Result<Unit>

    // Comment related methods
    suspend fun getCommentsByPostId(postId: Long): Result<List<Comment>>

    suspend fun createComment(postId: Long, comment: Comment): Result<Comment>

    suspend fun updateComment(id: Long, comment: Comment): Result<Comment>

    suspend fun deleteComment(id: Long): Result<Unit>
}
