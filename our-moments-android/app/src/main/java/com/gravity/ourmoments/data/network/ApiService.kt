package com.gravity.ourmoments.data.network

import com.gravity.ourmoments.data.model.ApiResponse
import com.gravity.ourmoments.data.model.Comment
import com.gravity.ourmoments.data.model.LoginRequest
import com.gravity.ourmoments.data.model.LoginResponse
import com.gravity.ourmoments.data.model.Notification
import com.gravity.ourmoments.data.model.Post
import com.gravity.ourmoments.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // User
    @GET("api/users/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>

    @PUT("api/users/profile")
    suspend fun updateProfile(@Body user: User): Response<ApiResponse<User>>

    // Post
    @GET("api/posts")
    suspend fun getPosts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("visibility") visibility: String? = null
    ): Response<ApiResponse<List<Post>>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<ApiResponse<Post>>

    @POST("api/posts")
    suspend fun createPost(@Body post: Post): Response<ApiResponse<Post>>

    @PUT("api/posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Long,
        @Body post: Post
    ): Response<ApiResponse<Post>>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<ApiResponse<Unit>>

    // Comment
    @GET("api/posts/{postId}/comments")
    suspend fun getCommentsByPostId(@Path("postId") postId: Long): Response<ApiResponse<List<Comment>>>

    @POST("api/posts/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: Long,
        @Body comment: Comment
    ): Response<ApiResponse<Comment>>

    @PUT("api/comments/{id}")
    suspend fun updateComment(
        @Path("id") id: Long,
        @Body comment: Comment
    ): Response<ApiResponse<Comment>>

    @DELETE("api/comments/{id}")
    suspend fun deleteComment(@Path("id") id: Long): Response<ApiResponse<Unit>>

    // Notification
    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<List<Notification>>>

    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @PUT("api/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Response<ApiResponse<Unit>>

    @GET("api/notifications/unread-count")
    suspend fun getUnreadNotificationCount(): Response<ApiResponse<Int>>
}