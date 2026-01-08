package com.gravity.ourmoments.domain.usecase.comment

import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.Comment
import com.gravity.ourmoments.domain.repository.PostRepositoryInterface
import javax.inject.Inject

class GetCommentsByPostIdUseCase @Inject constructor(
    private val postRepository: PostRepositoryInterface
) {
    suspend operator fun invoke(postId: Long): Result<List<Comment>> {
        return postRepository.getCommentsByPostId(postId)
    }
}