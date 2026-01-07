package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.data.model.Comment
import com.gravity.ourmoments.data.model.Post
import com.gravity.ourmoments.data.network.NetworkResult
import com.gravity.ourmoments.data.network.RetrofitClient
import javax.inject.Inject

class PostRepository @Inject constructor() : BaseRepository() {
    private val apiService = RetrofitClient.apiService

    suspend fun getPosts(page: Int, size: Int, visibility: String? = null): NetworkResult<List<Post>> {
        return safeApiCall {
            apiService.getPosts(page, size, visibility)
        }
    }

    suspend fun getPostById(id: Long): NetworkResult<Post> {
        return safeApiCall {
            apiService.getPostById(id)
        }
    }

    suspend fun createPost(post: Post): NetworkResult<Post> {
        return safeApiCall {
            apiService.createPost(post)
        }
    }

    suspend fun updatePost(id: Long, post: Post): NetworkResult<Post> {
        return safeApiCall {
            apiService.updatePost(id, post)
        }
    }

    suspend fun deletePost(id: Long): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deletePost(id)
        }
    }

    suspend fun getCommentsByPostId(postId: Long): NetworkResult<List<Comment>> {
        return safeApiCall {
            apiService.getCommentsByPostId(postId)
        }
    }

    suspend fun createComment(postId: Long, comment: Comment): NetworkResult<Comment> {
        return safeApiCall {
            apiService.createComment(postId, comment)
        }
    }

    suspend fun updateComment(id: Long, comment: Comment): NetworkResult<Comment> {
        return safeApiCall {
            apiService.updateComment(id, comment)
        }
    }

    suspend fun deleteComment(id: Long): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deleteComment(id)
        }
    }
}