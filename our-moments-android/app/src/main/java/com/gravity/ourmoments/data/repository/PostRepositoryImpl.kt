package com.gravity.ourmoments.data.repository

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.data.model.Comment as DataComment
import com.gravity.ourmoments.data.model.Post as DataPost
import com.gravity.ourmoments.data.model.User as DataUser
import com.gravity.ourmoments.data.network.NetworkResult
import com.gravity.ourmoments.data.network.RetrofitClient
import com.gravity.ourmoments.domain.model.Comment
import com.gravity.ourmoments.domain.model.Post
import com.gravity.ourmoments.domain.model.User
import com.gravity.ourmoments.domain.repository.PostRepositoryInterface
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor() : BaseRepository(), PostRepositoryInterface {
    private val apiService = RetrofitClient.apiService

    override suspend fun getPosts(page: Int, size: Int, visibility: String?): Result<List<Post>> {
        return when (val result = safeApiCall {
            apiService.getPosts(page, size, visibility)
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.map { it.toDomainModel() })
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun getPostById(id: Long): Result<Post> {
        return when (val result = safeApiCall {
            apiService.getPostById(id)
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomainModel())
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun createPost(post: Post): Result<Post> {
        return when (val result = safeApiCall {
            apiService.createPost(post.toDataModel())
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomainModel())
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun updatePost(id: Long, post: Post): Result<Post> {
        return when (val result = safeApiCall {
            apiService.updatePost(id, post.toDataModel())
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomainModel())
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun deletePost(id: Long): Result<Unit> {
        return when (val result = safeApiCall {
            apiService.deletePost(id)
        }) {
            is NetworkResult.Success -> {
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    // Comment related methods
    override suspend fun getCommentsByPostId(postId: Long): Result<List<Comment>> {
        return when (val result = safeApiCall {
            apiService.getCommentsByPostId(postId)
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.map { it.toDomainModel() })
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun createComment(postId: Long, comment: Comment): Result<Comment> {
        return when (val result = safeApiCall {
            apiService.createComment(postId, comment.toDataModel())
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomainModel())
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun updateComment(id: Long, comment: Comment): Result<Comment> {
        return when (val result = safeApiCall {
            apiService.updateComment(id, comment.toDataModel())
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomainModel())
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    override suspend fun deleteComment(id: Long): Result<Unit> {
        return when (val result = safeApiCall {
            apiService.deleteComment(id)
        }) {
            is NetworkResult.Success -> {
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                Result.error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                Result.loading()
            }
        }
    }

    private fun DataPost.toDomainModel(): Post {
        return Post(
            postId = this.postId,
            title = this.title,
            content = this.content,
            images = this.images,
            author = this.author.toDomainModel(),
            createTime = this.createTime,
            updateTime = this.updateTime,
            visibility = this.visibility
        )
    }

    private fun Post.toDataModel(): DataPost {
        return DataPost(
            postId = this.postId,
            title = this.title,
            content = this.content,
            images = this.images,
            author = this.author.toDataModel(),
            createTime = this.createTime,
            updateTime = this.updateTime,
            visibility = this.visibility
        )
    }

    private fun DataComment.toDomainModel(): Comment {
        return Comment(
            commentId = this.commentId,
            postId = this.post.postId,
            content = this.content,
            author = this.author.toDomainModel(),
            createTime = this.createTime,
            updateTime = this.updateTime
        )
    }

    private fun Comment.toDataModel(): DataComment {
        // Note: This is a simplified version. In a real implementation,
        // you might need to fetch the full Post object for the data model
        return DataComment(
            commentId = this.commentId,
            content = this.content,
            post = DataPost(
                postId = this.postId,
                title = "",
                content = "",
                images = emptyList(),
                author = DataUser(0, ""),
                createTime = "",
                visibility = "PUBLIC"
            ),
            author = this.author.toDataModel(),
            createTime = this.createTime,
            updateTime = this.updateTime
        )
    }

    private fun DataUser.toDomainModel(): User {
        return User(
            userId = this.userId,
            username = this.username,
            nickname = this.nickname,
            email = this.email,
            avatar = this.avatar,
            role = this.role,
            createTime = this.createTime
        )
    }

    private fun User.toDataModel(): DataUser {
        return DataUser(
            userId = this.userId,
            username = this.username,
            nickname = this.nickname,
            email = this.email,
            avatar = this.avatar,
            role = this.role,
            createTime = this.createTime
        )
    }
}
