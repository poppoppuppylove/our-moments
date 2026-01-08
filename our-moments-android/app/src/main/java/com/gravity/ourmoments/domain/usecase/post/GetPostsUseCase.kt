package com.gravity.ourmoments.domain.usecase.post

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.Post
import com.gravity.ourmoments.domain.repository.PostRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        visibility: String? = null
    ): Result<List<Post>> {
        return postRepository.getPosts(page, size, visibility)
    }
}
