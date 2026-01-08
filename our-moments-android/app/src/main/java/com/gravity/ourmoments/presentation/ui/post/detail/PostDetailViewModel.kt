package com.gravity.ourmoments.presentation.ui.post.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.Comment
import com.gravity.ourmoments.domain.model.Post
import com.gravity.ourmoments.domain.usecase.comment.CreateCommentUseCase
import com.gravity.ourmoments.domain.usecase.comment.GetCommentsByPostIdUseCase
import com.gravity.ourmoments.domain.usecase.post.GetPostByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val getCommentsByPostIdUseCase: GetCommentsByPostIdUseCase,
    private val createCommentUseCase: CreateCommentUseCase
) : ViewModel() {

    private val postId: Long = savedStateHandle.get<String>("postId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isCommentLoading = MutableStateFlow(false)
    val isCommentLoading: StateFlow<Boolean> = _isCommentLoading.asStateFlow()

    init {
        loadPostAndComments()
    }

    private fun loadPostAndComments() {
        if (postId <= 0) {
            _uiState.value = PostDetailUiState.Error("无效的文章ID")
            return
        }

        loadPost()
        loadComments()
    }

    private fun loadPost() {
        viewModelScope.launch {
            when (val result = getPostByIdUseCase(postId)) {
                is Result.Success -> {
                    // Only update post data, keep comments if they exist
                    val currentState = _uiState.value
                    if (currentState is PostDetailUiState.Success) {
                        _uiState.value = PostDetailUiState.Success(result.data, currentState.comments)
                    } else {
                        _uiState.value = PostDetailUiState.Success(result.data, emptyList())
                    }
                }
                is Result.Error -> {
                    _uiState.value = PostDetailUiState.Error(result.message)
                }
                is Result.Loading -> {
                    _uiState.value = PostDetailUiState.Loading
                }
            }
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            when (val result = getCommentsByPostIdUseCase(postId)) {
                is Result.Success -> {
                    _comments.value = result.data
                    // Update comments in uiState if post is loaded
                    val currentState = _uiState.value
                    if (currentState is PostDetailUiState.Success) {
                        _uiState.value = PostDetailUiState.Success(currentState.post, result.data)
                    }
                }
                is Result.Error -> {
                    // Don't show error for comments, just keep empty list
                }
                is Result.Loading -> {
                    // Ignore loading state for comments
                }
            }
        }
    }

    fun refresh() {
        loadPostAndComments()
    }

    fun addComment(content: String) {
        if (content.isBlank() || postId <= 0) return

        viewModelScope.launch {
            _isCommentLoading.value = true
            when (val result = createCommentUseCase(postId, createDummyComment(content))) {
                is Result.Success -> {
                    // Refresh comments after successful creation
                    loadComments()
                }
                is Result.Error -> {
                    // Handle error - could show a toast or error message
                }
                is Result.Loading -> {
                    // Ignore loading state
                }
            }
            _isCommentLoading.value = false
        }
    }

    private fun createDummyComment(content: String): Comment {
        // This is a placeholder - in a real app, you would get the current user from repository
        return Comment(
            commentId = 0,
            postId = postId,
            content = content,
            author = com.gravity.ourmoments.domain.model.User(
                userId = 0,
                username = "当前用户"
            ),
            createTime = java.time.LocalDateTime.now().toString()
        )
    }

    fun clearError() {
        _uiState.value = PostDetailUiState.Loading
    }
}

sealed class PostDetailUiState {
    object Loading : PostDetailUiState()
    data class Success(val post: Post, val comments: List<Comment> = emptyList()) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}