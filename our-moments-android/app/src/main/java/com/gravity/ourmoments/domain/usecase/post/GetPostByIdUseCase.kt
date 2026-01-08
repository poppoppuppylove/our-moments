package com.gravity.ourmoments.domain.usecase.post

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.Post
import com.gravity.ourmoments.domain.repository.PostRepositoryInterface
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val postRepository: PostRepositoryInterface
) {
    suspend operator fun invoke(id: Long): Result<Post> {
        return postRepository.getPostById(id)
    }
}